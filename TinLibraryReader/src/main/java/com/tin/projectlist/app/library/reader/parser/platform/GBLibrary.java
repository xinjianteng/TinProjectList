package com.core.platform;

import com.core.file.GBResourceFile;
import com.core.option.GBBooleanOption;
import com.core.option.GBStringOption;

import java.util.List;

public abstract class GBLibrary {
    public static GBLibrary Instance() {
        return ourImplementation;
    }

    private static GBLibrary ourImplementation;

    public static final String SCREEN_ORIENTATION_SYSTEM = "system";
    public static final String SCREEN_ORIENTATION_SENSOR = "sensor";
    public static final String SCREEN_ORIENTATION_PORTRAIT = "portrait";
    public static final String SCREEN_ORIENTATION_LANDSCAPE = "landscape";
    public static final String SCREEN_ORIENTATION_REVERSE_PORTRAIT = "reversePortrait";
    public static final String SCREEN_ORIENTATION_REVERSE_LANDSCAPE = "reverseLandscape";

    public final GBStringOption OrientationOption = new GBStringOption("LookNFeel", "Orientation", "system");
    // 是否使用样式
    public final GBBooleanOption UseCssOption = new GBBooleanOption("Options", "usecssOption", true);
    // 是否双页模式
    public final GBBooleanOption DoublePageOption = new GBBooleanOption("Options", "doublePage", false);

    protected GBLibrary() {
        ourImplementation = this;
    }

    abstract public GBResourceFile createResourceFile(String path);
    abstract public GBResourceFile createResourceFile(GBResourceFile parent, String name);

    abstract public String getVersionName();
    abstract public String getFullVersionName();
    abstract public String getCurrentTimeString();
    abstract public void setScreenBrightness(int percent);
    abstract public int getScreenBrightness();
    abstract public int getDisplayDPI();
    abstract public int getPixelWidth();
    abstract public int getPixelHeight();
    abstract public List<String> defaultLanguageCodes();

    abstract public boolean supportsAllOrientations();
    public String[] allOrientations() {
        return supportsAllOrientations() ? new String[]{SCREEN_ORIENTATION_SYSTEM, SCREEN_ORIENTATION_SENSOR,
                SCREEN_ORIENTATION_PORTRAIT, SCREEN_ORIENTATION_LANDSCAPE, SCREEN_ORIENTATION_REVERSE_PORTRAIT,
                SCREEN_ORIENTATION_REVERSE_LANDSCAPE} : new String[]{SCREEN_ORIENTATION_SYSTEM,
                SCREEN_ORIENTATION_SENSOR, SCREEN_ORIENTATION_PORTRAIT, SCREEN_ORIENTATION_LANDSCAPE};
    }
    // 是否支持gpu （add by jack）
    abstract public boolean isGPU();
    abstract public String getPhoneSN();
}
