package com.tin.projectlist.app.library.reader.view;

import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetFileDescriptor;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;

import com.core.file.GBFile;
import com.core.file.GBResourceFile;
import com.core.option.GBBooleanOption;
import com.core.option.GBIntegerRangeOption;
import com.geeboo.read.view.widget.GBAndroidWidget;
import com.tin.projectlist.app.library.reader.parser.platform.GBLibrary;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

/**
 * 类名： GBAndroidLibrary.java<br>
 * 描述： <br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBAndroidLibrary extends GBLibrary {
    // 底部状态栏状态
    public final GBBooleanOption ShowStatusBarOption = new GBBooleanOption("LookNFeel", "ShowStatusBar",
            hasNoHardwareMenuButton());
    public final GBIntegerRangeOption BatteryLevelToTurnScreenOffOption = new GBIntegerRangeOption("LookNFeel",
            "BatteryLevelToTurnScreenOff", 0, 100, 50);
    public final GBBooleanOption DontTurnScreenOffDuringChargingOption = new GBBooleanOption("LookNFeel",
            "DontTurnScreenOffDuringCharging", true);
    public final GBIntegerRangeOption ScreenBrightnessLevelOption = new GBIntegerRangeOption("LookNFeel",
            "ScreenBrightnessLevel", 0, 100, 0);
    public final GBBooleanOption DisableButtonLightsOption = new GBBooleanOption("LookNFeel", "DisableButtonLights",
            !hasButtonLightsBug());
    /**
     * 功能描述：判断屏幕是否存在底部按钮<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-28<br>
     *
     * @return
     */
    private boolean hasNoHardwareMenuButton() {
        return
                // Eken M001
                (Build.DISPLAY != null && Build.DISPLAY.contains("simenxie")) ||
                        // PanDigital
                        "PD_Novel".equals(Build.MODEL);
    }

    private Boolean myIsKindleFire = null;
    public boolean isKindleFire() {
        if (myIsKindleFire == null) {
            final String KINDLE_MODEL_REGEXP = ".*kindle(\\s+)fire.*";
            myIsKindleFire = Build.MODEL != null && Build.MODEL.toLowerCase().matches(KINDLE_MODEL_REGEXP);
        }
        return myIsKindleFire;
    }

    public boolean hasButtonLightsBug() {
        return "GT-S5830".equals(Build.MODEL);
    }

    private BaseMenuActivity myActivity;
    private final Application myApplication;

    public GBAndroidLibrary(Application application) {
        myApplication = application;
        // add by jack 获取gpu状态
        initGPUState();
    }

    public void setActivity(BaseMenuActivity activity) {
        myActivity = activity;
    }

    public void finish() {
        if (myActivity != null && !myActivity.isFinishing()) {
            myActivity.finish();
        }
    }

    public BaseMenuActivity getActivity() {
        return myActivity;
    }
    /*
     * 获取当前阅读控件
     */
    public GBAndroidWidget getWidget() {
        if (myActivity instanceof ReaderActivity)
            return ((ReaderActivity) myActivity).getmWidget();
        else
            return null;
    }

    @Override
    public GBResourceFile createResourceFile(String path) {
        return new AndroidAssetsFile(path);
    }

    @Override
    public GBResourceFile createResourceFile(GBResourceFile parent, String name) {
        return new AndroidAssetsFile((AndroidAssetsFile) parent, name);
    }

    @Override
    public String getVersionName() {
        try {
            final PackageInfo info = myApplication.getPackageManager()
                    .getPackageInfo(myApplication.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String getFullVersionName() {
        try {
            final PackageInfo info = myApplication.getPackageManager()
                    .getPackageInfo(myApplication.getPackageName(), 0);
            return info.versionName + " (" + info.versionCode + ")";
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String getCurrentTimeString() {
        return DateFormat.getTimeFormat(myApplication.getApplicationContext()).format(new Date());
    }

    @Override
    public void setScreenBrightness(int percent) {
        if (myActivity != null) {
            myActivity.setScreenBrightness(percent);
        }
    }

    @Override
    public int getScreenBrightness() {
        return (myActivity != null) ? myActivity.getScreenBrightness() : 0;
    }

    private DisplayMetrics myMetrics;

    @Override
    public int getDisplayDPI() {
        if (myMetrics == null) {
            if (myActivity == null) {
                return 0;
            }
            myMetrics = new DisplayMetrics();
            myActivity.getWindowManager().getDefaultDisplay().getMetrics(myMetrics);
        }
        return (int) (160 * myMetrics.density);
    }

    @Override
    public int getPixelWidth() {
        if (myMetrics == null) {
            if (myActivity == null) {
                return 0;
            }
            myMetrics = new DisplayMetrics();
            myActivity.getWindowManager().getDefaultDisplay().getMetrics(myMetrics);
        }
        return myMetrics.widthPixels;
    }

    @Override
    public int getPixelHeight() {
        if (myMetrics == null) {
            if (myActivity == null) {
                return 0;
            }
            myMetrics = new DisplayMetrics();
            myActivity.getWindowManager().getDefaultDisplay().getMetrics(myMetrics);
        }
        return myMetrics.heightPixels;
    }

    @Override
    public List<String> defaultLanguageCodes() {
        final TreeSet<String> set = new TreeSet<String>();
        set.add(Locale.getDefault().getLanguage());
        final TelephonyManager manager = (TelephonyManager) myApplication.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            final String country0 = manager.getSimCountryIso().toLowerCase();
            final String country1 = manager.getNetworkCountryIso().toLowerCase();
            for (Locale locale : Locale.getAvailableLocales()) {
                final String country = locale.getCountry().toLowerCase();
                if (country != null && country.length() > 0 && (country.equals(country0) || country.equals(country1))) {
                    set.add(locale.getLanguage());
                }
            }
            if ("ru".equals(country0) || "ru".equals(country1)) {
                set.add("ru");
            } else if ("by".equals(country0) || "by".equals(country1)) {
                set.add("ru");
            } else if ("ua".equals(country0) || "ua".equals(country1)) {
                set.add("ru");
            }
        }
        set.add("multi");
        return new ArrayList<String>(set);
    }

    @Override
    public boolean supportsAllOrientations() {
        try {
            return ActivityInfo.class.getField("SCREEN_ORIENTATION_REVERSE_PORTRAIT") != null;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private final class AndroidAssetsFile extends GBResourceFile {
        private final AndroidAssetsFile myParent;

        AndroidAssetsFile(AndroidAssetsFile parent, String name) {
            super(parent.getPath().length() == 0 ? name : parent.getPath() + '/' + name);
            myParent = parent;
        }

        AndroidAssetsFile(String path) {
            super(path);
            if (path.length() == 0) {
                myParent = null;
            } else {
                final int index = path.lastIndexOf('/');
                myParent = new AndroidAssetsFile(index >= 0 ? path.substring(0, path.lastIndexOf('/')) : "");
            }
        }

        @Override
        protected List<GBFile> directoryEntries() {
            try {
                String[] names = myApplication.getAssets().list(getPath());
                if (names != null && names.length != 0) {
                    ArrayList<GBFile> files = new ArrayList<GBFile>(names.length);
                    for (String n : names) {
                        files.add(new AndroidAssetsFile(this, n));
                    }
                    return files;
                }
            } catch (IOException e) {
            }
            return Collections.emptyList();
        }

        @Override
        public boolean isDirectory() {
            try {
                InputStream stream = myApplication.getAssets().open(getPath());
                if (stream == null) {
                    return true;
                }
                stream.close();
                return false;
            } catch (IOException e) {
                return true;
            }
        }

        @Override
        public boolean exists() {
            try {
                InputStream stream = myApplication.getAssets().open(getPath());
                if (stream != null) {
                    stream.close();
                    // file exists
                    return true;
                }
            } catch (IOException e) {
            }
            try {
                String[] names = myApplication.getAssets().list(getPath());
                if (names != null && names.length != 0) {
                    // directory exists
                    return true;
                }
            } catch (IOException e) {
            }
            return false;
        }

        private long mySize = -1;
        @Override
        public long size() {
            if (mySize == -1) {
                mySize = sizeInternal();
            }
            return mySize;
        }

        private long sizeInternal() {
            try {
                AssetFileDescriptor descriptor = myApplication.getAssets().openFd(getPath());
                // for some files (archives, crt) descriptor cannot be opened
                if (descriptor == null) {
                    return sizeSlow();
                }
                long length = descriptor.getLength();
                descriptor.close();
                return length;
            } catch (IOException e) {
                return sizeSlow();
            }
        }

        private long sizeSlow() {
            try {
                final InputStream stream = getInputStream();
                if (stream == null) {
                    return 0;
                }
                long size = 0;
                final long step = 1024 * 1024;
                while (true) {
                    // TODO: does skip work as expected for these files?
                    long offset = stream.skip(step);
                    size += offset;
                    if (offset < step) {
                        break;
                    }
                }
                return size;
            } catch (IOException e) {
                return 0;
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return myApplication.getAssets().open(getPath());
        }

        @Override
        public GBFile getParent() {
            return myParent;
        }
    }

    // 是否支持gpu （add by jack）
    @Override
    public boolean isGPU() {
        return mIsGPU;
    }
    // 获取gpu状态
    public boolean mIsGPU;
    private void initGPUState() {
        mIsGPU = false;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method m = c.getMethod("get", new Class[]{String.class, String.class});
            Object[] object =new String[]{"persist.sys.ui.hw", "false"};
            mIsGPU = Boolean.parseBoolean(m.invoke(c, object).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String mPhoneSN;
    /**
     * 获取PhoneSN add by jack
     *
     * @param context 当前上下文
     * @return PhoneSN
     */
    @Override
    public String getPhoneSN() {
        if (mPhoneSN != null && !"".equals(mPhoneSN))
            return mPhoneSN;

        TelephonyManager manager = (TelephonyManager) myActivity.getSystemService(Context.TELEPHONY_SERVICE);
        String PHONE_ID = manager.getDeviceId();// 手机序列号
        String PHONE_SYSTEM = Settings.System.getString(myActivity.getApplicationContext().getContentResolver(),
                Settings.System.ANDROID_ID);// 系统序列号
        mPhoneSN = PHONE_ID + PHONE_SYSTEM;// 设备唯一码
        return mPhoneSN;
    }

    /*
     * public void putInt(String key,int val){ } public int getInt(String key){
     * }
     */
}
