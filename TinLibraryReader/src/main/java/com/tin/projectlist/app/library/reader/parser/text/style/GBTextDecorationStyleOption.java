package com.tin.projectlist.app.library.reader.parser.text.style;

import com.tin.projectlist.app.library.reader.parser.object.GBBoolean3;
import com.tin.projectlist.app.library.reader.parser.option.GBBoolean3Option;
import com.tin.projectlist.app.library.reader.parser.option.GBIntegerOption;
import com.tin.projectlist.app.library.reader.parser.option.GBIntegerRangeOption;
import com.tin.projectlist.app.library.reader.parser.option.GBStringOption;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextHyperlink;

/**
 * 类名： GBTextDecorationStyleOption#ZLTextStyleDecoration<br>
 * 描述： 文字装饰配置封装定义<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextDecorationStyleOption {
    static final String STYLE = "Style";

    public final GBStringOption FontFamilyOption;
    public final GBIntegerRangeOption FontSizeDeltaOption;
    public final GBBoolean3Option BoldOption;
    public final GBBoolean3Option ItalicOption;
    public final GBBoolean3Option UnderlineOption;
    public final GBBoolean3Option StrikeThroughOption;
    public final GBIntegerOption VerticalShiftOption;
    public final GBBoolean3Option AllowHyphenationsOption;

    private final String myName;
    /**
     * @param name 样式名称
     * @param fontFamily 字体名
     * @param fontSizeDelta 默认字体大小
     * @param bold 是否粗体
     * @param italic 是否斜体
     * @param underline 是否有下划线
     * @param strikeThrough 是否有删除线
     * @param verticalShift
     * @param allowHyphenations
     */
    public GBTextDecorationStyleOption(String name, String fontFamily, int fontSizeDelta, GBBoolean3 bold,
                                       GBBoolean3 italic, GBBoolean3 underline, GBBoolean3 strikeThrough, int verticalShift,
                                       GBBoolean3 allowHyphenations) {
        myName = name;
        FontFamilyOption = new GBStringOption(STYLE, name + ":fontFamily", fontFamily);
        FontSizeDeltaOption = new GBIntegerRangeOption(STYLE, name + ":fontSize", -16, 16, fontSizeDelta);
        BoldOption = new GBBoolean3Option(STYLE, name + ":bold", bold);
        ItalicOption = new GBBoolean3Option(STYLE, name + ":italic", italic);
        UnderlineOption = new GBBoolean3Option(STYLE, name + ":underline", underline);
        StrikeThroughOption = new GBBoolean3Option(STYLE, name + ":strikeThrough", strikeThrough);
        VerticalShiftOption = new GBIntegerOption(STYLE, name + ":vShift", verticalShift);
        AllowHyphenationsOption = new GBBoolean3Option(STYLE, name + ":allowHyphenations", allowHyphenations);
    }

    public GBTextStyle createDecoratedStyle(GBTextStyle base) {
        return createDecoratedStyle(base, null);
    }
    /**
     * 功能描述： 根据当前配置创建一个样式实体<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-22<br>
     *
     * @param base 默认的基本样式
     * @param hyperlink 文本链接对象
     * @return
     */
    public GBTextStyle createDecoratedStyle(GBTextStyle base, GBTextHyperlink hyperlink) {
        return new GBTextPartiallyDecoratedStyle(base, this, hyperlink);
    }

    public String getName() {
        return myName;
    }
}
