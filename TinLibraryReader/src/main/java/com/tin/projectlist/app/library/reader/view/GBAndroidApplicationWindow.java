package com.tin.projectlist.app.library.reader.view;

import android.app.Activity;
import android.os.Handler;

import com.core.domain.GBApplication;
import com.core.domain.GBApplicationWindow;
import com.core.view.GBViewInter;
import com.geeboo.utils.UIUtil;

/**
 * 类名： GBAndroidApplicationWindow.java<br>
 * 描述： 阅读器窗体管理类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBAndroidApplicationWindow extends GBApplicationWindow {
    // private final HashMap<MenuItem, String> myMenuItemMap = new
    // HashMap<MenuItem, String>();
    //
    // private final MenuItem.OnMenuItemClickListener myMenuListener = new
    // MenuItem.OnMenuItemClickListener() {
    // public boolean onMenuItemClick(MenuItem item) {
    // getApplication().runAction(myMenuItemMap.get(item));
    // return true;
    // }
    // };

    public GBAndroidApplicationWindow(GBApplication application) {
        super(application);
    }

    // public Menu addSubMenu(Menu menu, String id) {
    // return
    // menu.addSubMenu(GBResource.resource("menu").getResource(id).getValue());
    // }
    //
    // public void addMenuItem(Menu menu, String actionId, Integer iconId,
    // String name) {
    // if (name == null) {
    // name = GBResource.resource("menu").getResource(actionId).getValue();
    // }
    // final MenuItem menuItem = menu.add(name);
    // if (iconId != null) {
    // menuItem.setIcon(iconId);
    // }
    // menuItem.setOnMenuItemClickListener(myMenuListener);
    // myMenuItemMap.put(menuItem, actionId);
    // }

    @Override
    public void refresh() {
        // for (Map.Entry<MenuItem, String> entry : myMenuItemMap.entrySet()) {
        // final String actionId = entry.getValue();
        // final GBApplication application = getApplication();
        // final MenuItem menuItem = entry.getKey();
        // menuItem.setVisible(application.isActionVisible(actionId) &&
        // application.isActionEnabled(actionId));
        // switch (application.isActionChecked(actionId)) {
        // case B3_TRUE :
        // menuItem.setCheckable(true);
        // menuItem.setChecked(true);
        // break;
        // case B3_FALSE :
        // menuItem.setCheckable(true);
        // menuItem.setChecked(false);
        // break;
        // case B3_UNDEFINED :
        // menuItem.setCheckable(false);
        // break;
        // }
        // }
    }

    @Override
    public void runWithMessage(String key, Runnable action, Runnable postAction) {
        final Activity activity = ((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity();
        if (activity != null) {
            UIUtil.runWithMessage(activity, key, action, postAction, false);
        } else {
            action.run();
        }
    }

    @Override
    protected void processException(Exception exception) {
        exception.printStackTrace();
        // 阅读异常处理
        // 参见org.geometerplus.android.fbreader.error.BookReadingErrorActivity

        final Activity activity = ((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity();
        UIUtil.showMessageTextOnUIThread(activity, exception.getMessage());
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                activity.finish();
            }
        }, 100);
        // Toast.makeText(activity,exception.getMessage(),
        // Toast.LENGTH_LONG).show();
        // final Intent intent = new Intent("android.fbreader.action.ERROR", new
        // Uri.Builder().scheme(
        // exception.getClass().getSimpleName()).build());
        // intent.putExtra(ErrorKeys.MESSAGE, exception.getMessage());
        // final StringWriter stackTrace = new StringWriter();
        // exception.printStackTrace(new PrintWriter(stackTrace));
        // intent.putExtra(ErrorKeys.STACKTRACE, stackTrace.toString());
        // /*
        // * if (exception instanceof BookReadingException) { final GBFile file
        // =
        // * ((BookReadingException)exception).File; if (file != null) {
        // * intent.putExtra("file", file.getPath()); } }
        // */
        // try {
        // activity.startActivity(intent);
        // } catch (ActivityNotFoundException e) {
        // // ignore
        // e.printStackTrace();
        // }
    }

    @Override
    public void setTitle(final String title) {
        final Activity activity = ((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    activity.setTitle(title);
                }
            });
        }
    }

    public void close() {
        ((GBAndroidLibrary) GBAndroidLibrary.Instance()).finish();
    }

    private int myBatteryLevel;
    protected int getBatteryLevel() {
        return myBatteryLevel;
    }
    public void setBatteryLevel(int percent) {
        myBatteryLevel = percent;
    }

    public interface ErrorKeys {
        static final String STACKTRACE = "gbreader.stacktrace";
        static final String MESSAGE = "gbreader.message";
        static final String FILE_PATH = "gbreader.filePath";
    }

    @Override
    protected GBViewInter getViewImp() {
        return ((GBAndroidLibrary) GBAndroidLibrary.Instance()).getWidget();
    }
}
