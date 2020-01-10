package com.tin.projectlist.app.library.reader.controller;


import com.tin.projectlist.app.library.reader.parser.option.GBBooleanOption;
import com.tin.projectlist.app.library.reader.parser.option.GBEnumOption;
import com.tin.projectlist.app.library.reader.parser.option.GBIntegerRangeOption;
import com.tin.projectlist.app.library.reader.parser.option.GBStringOption;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum;

/**
 * 类名： ScrollingPreferences.java<br>
 * 描述： 翻页效果配置类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-24<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class ScrollingPreferences {
    private static ScrollingPreferences ourInstance;

    public static ScrollingPreferences Instance() {
        return (ourInstance != null) ? ourInstance : new ScrollingPreferences();
    }

    public static enum FingerScrolling {
        byTap, byFlick, byTapAndFlick
    }
    public final GBEnumOption<FingerScrolling> FingerScrollingOption = new GBEnumOption<FingerScrolling>("Scrolling",
            "Finger", FingerScrolling.byTapAndFlick);

    public final GBEnumOption<PageEnum.Anim> AnimationOption = new GBEnumOption<PageEnum.Anim>("Scrolling",
            "ANIMATION_CASE", PageEnum.Anim.FLIP);
    // 当前是否设置了双翻页
    public final GBBooleanOption DoublePageOption = new GBBooleanOption("Scrolling", "DoublePage", false);

    public final GBIntegerRangeOption AnimationSpeedOption = new GBIntegerRangeOption("Scrolling", "AnimationSpeed", 1,
            10, 5);
    // 是否水平翻页
    public final GBBooleanOption HorizontalOption = new GBBooleanOption("Scrolling", "Horizontal", true);
    // 响应热区配置
    public final GBStringOption TapZoneMapOption = new GBStringOption("Scrolling", "TapZoneMap", "default");
    // 是否感应翻页
    public final GBBooleanOption SensorPageOption = new GBBooleanOption("Scrolling", "SensorPageCurl", false);

    private ScrollingPreferences() {
        ourInstance = this;
    }
}
