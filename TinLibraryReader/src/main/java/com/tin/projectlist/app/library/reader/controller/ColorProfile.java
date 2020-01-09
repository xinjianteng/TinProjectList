package com.tin.projectlist.app.library.reader.controller;


import com.tin.projectlist.app.library.reader.parser.object.GBColor;
import com.tin.projectlist.app.library.reader.parser.option.GBBooleanOption;
import com.tin.projectlist.app.library.reader.parser.option.GBColorOption;
import com.tin.projectlist.app.library.reader.parser.option.GBIntegerOption;
import com.tin.projectlist.app.library.reader.parser.option.GBStringOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 类名： ColorProfile.java<br>
 * 描述： 白天和夜间模式下的相关配置信息<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class ColorProfile {
    public static final String DAY = "defaultLight"; // 白天模式
    public static final String NIGHT = "defaultDark"; // 夜间模式

    public enum DayModel {
        Day, // 白天
        SleepKin, // 羊皮纸
        FlaxBrown, // 亚麻棕
        SimpleBrown, // 简棕色
        Wash;// 水墨

        public static DayModel getByString(String str) {
            if (str.equals(Day.toString())) {
                return Day;
            } else if (str.equals(SleepKin.toString())) {
                return SleepKin;
            } else if (str.equals(FlaxBrown.toString())) {
                return FlaxBrown;
            } else if (str.equals(SimpleBrown.toString())) {
                return SimpleBrown;
            } else if (str.equals(Wash.toString())) {
                return Wash;
            }
            return null;
        }
    }

    private static final ArrayList<String> ourNames = new ArrayList<String>();
    private static final HashMap<String, ColorProfile> ourProfiles = new HashMap<String, ColorProfile>();

    public static List<String> names() {
        if (ourNames.isEmpty()) {
            final int size = new GBIntegerOption("Colors", "NumberOfSchemes", 0).getValue();
            if (size == 0) {
                ourNames.add(DAY);
                ourNames.add(NIGHT);
            } else
                for (int i = 0; i < size; ++i) {
                    ourNames.add(new GBStringOption("Colors", "Scheme" + i, "").getValue());
                }
        }
        return Collections.unmodifiableList(ourNames);
    }

    public static ColorProfile get(String name) {
        ColorProfile profile = ourProfiles.get(name);
        if (profile == null) {
            profile = new ColorProfile(name);
            ourProfiles.put(name, profile);
        }
        return profile;
    }

    public final GBStringOption WallpaperOption; // 壁纸配置
    public final GBBooleanOption WallpageModelOption; // 壁纸是否平铺渲染
    public final GBColorOption BackgroundOption; // 无壁纸背景色配置
    public final GBColorOption SelectionBackgroundOption; // 选中背景色配置
    public final GBColorOption SelectionForegroundOption; // 选中的前景色配置
    public final GBColorOption HighlightingOption; // 高亮颜色配置
    public final GBColorOption RegularTextOption; // 字体颜色
    public final GBColorOption HyperlinkTextOption; // 未点击链接文本颜色
    public final GBColorOption VisitedHyperlinkTextOption; // 已点击链接文本颜色
    public final GBColorOption PageTiltleTextOption; // 页面标题颜色
    public final GBColorOption ReadTextColorOption; // 语音阅读背景颜色
    public final GBColorOption FooterFillOption;

    private ColorProfile(String name, ColorProfile base) {
        this(name);
        BackgroundOption.setValue(base.BackgroundOption.getValue());
        SelectionBackgroundOption.setValue(base.SelectionBackgroundOption.getValue());
        SelectionForegroundOption.setValue(base.SelectionForegroundOption.getValue());
        HighlightingOption.setValue(base.HighlightingOption.getValue());
        RegularTextOption.setValue(base.RegularTextOption.getValue());
        HyperlinkTextOption.setValue(base.HyperlinkTextOption.getValue());
        VisitedHyperlinkTextOption.setValue(base.VisitedHyperlinkTextOption.getValue());
        FooterFillOption.setValue(base.FooterFillOption.getValue());
    }

    private static GBColorOption createOption(String profileName, String optionName, int r, int g, int b) {
        return new GBColorOption("Colors", profileName + ':' + optionName, new GBColor(r, g, b));
    }

    private ColorProfile(String name) {
        if (NIGHT.equals(name)) {
            WallpaperOption = new GBStringOption("Colors", name + ":Wallpaper", "");
            WallpageModelOption = new GBBooleanOption("Colors", name + ":WallPageModel", false);
            BackgroundOption = createOption(name, "Background", 34, 34, 34);
            SelectionBackgroundOption = createOption(name, "SelectionBackground", 82, 131, 194);
            SelectionForegroundOption = createOption(name, "SelectionForeground", 255, 255, 220);
            HighlightingOption = createOption(name, "Highlighting", 96, 96, 128);
            HyperlinkTextOption = createOption(name, "Hyperlink", 60, 142, 224);
            VisitedHyperlinkTextOption = createOption(name, "VisitedHyperlink", 200, 139, 255);
            PageTiltleTextOption = createOption(name, "Text", 57, 57, 57);
            ReadTextColorOption = createOption(name, "Text", 96, 96, 128);
            RegularTextOption = createOption(name, "Text", 86, 86, 86);// 194,
            // 38, 41
            FooterFillOption = createOption(name, "FooterFillOption", 85, 85, 85);
        } else {
            WallpaperOption = new GBStringOption("Colors", name + ":Wallpaper", "wallpapers/day.png");
            WallpageModelOption = new GBBooleanOption("Colors", name + ":WallPageModel", false);
            BackgroundOption = createOption(name, "Background", 0, 0, 0);
            SelectionBackgroundOption = createOption(name, "SelectionBackground", 102, 204, 255);
            SelectionForegroundOption = createOption(name, "SelectionForeground", 255, 255, 220);
            HighlightingOption = createOption(name, "Highlighting", 44, 199, 130);
            RegularTextOption = createOption(name, "Text", 33, 33, 33);
            HyperlinkTextOption = createOption(name, "Hyperlink", 60, 139, 255);
            VisitedHyperlinkTextOption = createOption(name, "VisitedHyperlink", 200, 139, 255);
            PageTiltleTextOption = createOption(name, "Text", 33, 33, 33);
            ReadTextColorOption = createOption(name, "Text", 250, 232, 146);
            FooterFillOption = createOption(name, "FooterFillOption", 170, 170, 170);
        }
    }

    public void setDayModel(DayModel model) {
        switch (model) {
            case SleepKin :
                WallpaperOption.setValue("");
                BackgroundOption.setValue(new GBColor(251, 251, 251));
                PageTiltleTextOption.setValue(new GBColor(120, 111, 93));
                RegularTextOption.setValue(new GBColor(65, 52, 61));
                break;
            case Day :
                WallpaperOption.setValue("");
                BackgroundOption.setValue(new GBColor(241, 234, 213));
                PageTiltleTextOption.setValue(new GBColor(127, 127, 127));
                RegularTextOption.setValue(new GBColor(47, 47, 47));
                break;
            case FlaxBrown :
                WallpaperOption.setValue("");
                BackgroundOption.setValue(new GBColor(191, 235, 196));
                PageTiltleTextOption.setValue(new GBColor(119, 133, 118));
                RegularTextOption.setValue(new GBColor(39, 53, 38));
                break;
            case SimpleBrown :
                WallpaperOption.setValue("");
                BackgroundOption.setValue(new GBColor(5, 28, 44));
                PageTiltleTextOption.setValue(new GBColor(65, 86, 103));
                RegularTextOption.setValue(new GBColor(95, 113, 125));
                break;
        }
    }
}
