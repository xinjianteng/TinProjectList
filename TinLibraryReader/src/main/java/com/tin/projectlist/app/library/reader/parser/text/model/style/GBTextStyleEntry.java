package com.core.text.model.style;

import com.core.common.util.NumUtil;
import com.core.object.GBBoolean3;
import com.core.text.model.GBTextMetrics;

/**
 *
 * 描述： 文本样式条目 创建者： 燕冠楠<br>
 * 创建日期：2013-3-26<br>
 */
public abstract class GBTextStyleEntry {

    /**
     *
     * 功能描述：加载数据 将char[]形式描述的styleEntry信息 实例styleEntry 创建者： yangn<br>
     * 创建日期：2013-6-7<br>
     *
     * @param
     */
    public abstract void loadData(char[] data, int offset, int len);

    public abstract char[] toChars();

    protected char[] getRealData(char[] block, int realLen) {
        if (block.length == realLen) {
            return block;
        }
        char[] realData = new char[realLen];
        System.arraycopy(block, 0, realData, 0, realLen);
        block = null;
        return realData;
    }

    protected int setVal(char[] block, int offset, Length length) {
        final int size = NumUtil.parseInt(length.Size);
        block[offset++] = (char) size;
        block[offset++] = (char) (size >> 16);
        block[offset++] = (char) length.Unit;
        return offset;
    }

    /**
     * 内容显示特征
     */
    public interface Feature {
        int LENGTH_LEFT_INDENT = 0;
        int LENGTH_RIGHT_INDENT = 1;
        int LENGTH_FIRST_LINE_INDENT_DELTA = 2;
        int LENGTH_SPACE_BEFORE = 3;
        int LENGTH_SPACE_AFTER = 4;
        int LENGTH_FONT_SIZE = 5;
        int NUMBER_OF_LENGTHS = 6;
        int ALIGNMENT_TYPE = NUMBER_OF_LENGTHS;
        int FONT_FAMILY = NUMBER_OF_LENGTHS + 1;
        int FONT_STYLE_MODIFIER = NUMBER_OF_LENGTHS + 2;
        /** 字体颜色 */
        int FONT_COLOR = NUMBER_OF_LENGTHS + 3;
        int FONT_SIZE = NUMBER_OF_LENGTHS + 4;
        int LINE_HEIGHT = NUMBER_OF_LENGTHS + 5;
        int FONT_WEIGHT = NUMBER_OF_LENGTHS + 6;
    }

    /**
     *
     * 描述： 字体样式 创建者： 燕冠楠<br>
     * 创建日期：2013-3-26<br>
     */
    public interface FontModifier {
        byte FONT_MODIFIER_BOLD = 1 << 0;
        byte FONT_MODIFIER_ITALIC = 1 << 1;
        byte FONT_MODIFIER_UNDERLINED = 1 << 2;
        byte FONT_MODIFIER_STRIKEDTHROUGH = 1 << 3;
        byte FONT_MODIFIER_SMALLCAPS = 1 << 4;
        byte FONT_MODIFIER_INHERIT = 1 << 5;
        byte FONT_MODIFIER_SMALLER = 1 << 6;
        byte FONT_MODIFIER_LARGER = (byte) (1 << 7);
    }

    /**
     *
     * 描述： 尺寸单位 创建者： 燕冠楠<br>
     * 创建日期：2013-3-26<br>
     */
    public interface SizeUnit {
        byte PIXEL = 0; // px 像素
        byte POINT = 1; // pt = px *3/4
        byte EM_100 = 2; // em 百分比 1em = 100% , 如果字体大小为 10px ，1.2em = 12px;
        byte EX_100 = 3; // ex 字体x-height 1ex 相当与字体尺寸的一半
        byte PERCENT = 4; // %
        // font size val
        byte XX_SMALL = 5;
        byte X_SMALL = 6;
        byte SMALL = 7;
        byte MEDIUM = 8;
        byte LARGE = 9;
        byte X_LARGE = 10;
        byte XX_LARGE = 11;
        byte LARGER = 12;
        byte SMALLER = 13;
        // 自定义单位，只依据默认12换算
        byte GBPIXEL = 14;
    }

    public static Length parseLength(String str) {
        final String val = str.trim();
        byte unit = 0;
        if (val.endsWith("em")) {
            unit = SizeUnit.EM_100;
        } else if (val.endsWith("ex")) {
            unit = SizeUnit.EX_100;
        } else if (val.endsWith("%")) {
            unit = SizeUnit.PERCENT;
        } else if (val.endsWith("gbpx")) {
            unit = SizeUnit.GBPIXEL;
        }else if (val.endsWith("px")) {
            unit = SizeUnit.PIXEL;
        }
        float size = NumUtil.findNum(str);
        return new GBTextStyleEntry.Length(size, unit);
        /*
         * else if(str.endsWith("")){ }
         */
    }

    /**
     *
     * 描述：长度实体 创建者： 燕冠楠<br>
     * 创建日期：2013-3-26<br>
     */
    public static class Length {
        public final float Size; // int size转换为float modify by yangn
        public final byte Unit;

        public Length(float size, byte unit) {
            Size = size;
            Unit = unit;
        }

    }

    private short myFeatureMask;

    private Length[] myLengths = new Length[Feature.NUMBER_OF_LENGTHS];
    private byte myAlignmentType;
    private String myFontFamily;
    private byte mySupportedFontModifiers;
    private byte myFontModifiers;

    /**
     * 功能描述： 是否支持该特征<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-22<br>
     *
     * @param mask
     * @param featureId 显示特征id
     * @return
     */
    public static boolean isFeatureSupported(short mask, int featureId) {
        return (mask & (1 << featureId)) != 0;
    }

    protected GBTextStyleEntry() {
    }

    public final boolean isFeatureSupported(int featureId) {
        return isFeatureSupported(myFeatureMask, featureId);
    }

    public final void setLength(int featureId, float size, byte unit) {
        myFeatureMask |= 1 << featureId;
        myLengths[featureId] = new Length(size, unit);
    }

    private int fullSize(GBTextMetrics metrics, int featureId) {
        switch (featureId) {
            default :
            case Feature.LENGTH_LEFT_INDENT :
            case Feature.LENGTH_RIGHT_INDENT :
            case Feature.LENGTH_FIRST_LINE_INDENT_DELTA :
                return metrics.FullWidth;
            case Feature.LENGTH_SPACE_BEFORE :
            case Feature.LENGTH_SPACE_AFTER :
                return metrics.FullHeight;
            case Feature.LENGTH_FONT_SIZE :
                return metrics.FontSize;
        }
    }

    // int size转换为float modify by yangn
    public final float getLength(int featureId, GBTextMetrics metrics) {
        switch (myLengths[featureId].Unit) {
            default :
            case SizeUnit.PIXEL :
                return myLengths[featureId].Size * metrics.FontSize / metrics.DefaultFontSize;
            // we understand "point" as "1/2 point"
            case SizeUnit.POINT :
                return myLengths[featureId].Size * metrics.DPI * metrics.FontSize / 72 / metrics.DefaultFontSize / 2;
            case SizeUnit.EM_100 :
                return (myLengths[featureId].Size * metrics.FontSize + 50) / 100;
            case SizeUnit.EX_100 :
                return (myLengths[featureId].Size * metrics.FontXHeight + 50) / 100;
            case SizeUnit.PERCENT :
                return (myLengths[featureId].Size * fullSize(metrics, featureId) + 50) / 100;
        }
    }

    /**
     * 功能描述： 单位尺寸转换<br>
     * 创建者： jack<br>
     * 创建日期：2013-6-8<br>
     *
     * @param length 要转换的信息
     * @param metrics 参照值
     * @param perType 0 参照字体，1参照屏幕宽，2参照屏幕高
     * @return
     */
    public final float getLength(Length length, GBTextMetrics metrics, int perType) {
        if (length == null)
            return 0;
        switch (length.Unit) {
            default :
            case SizeUnit.PIXEL :
                return length.Size * ((float) metrics.FontSize / 12);
            // we understand "point" as "1/2 point"
            case SizeUnit.POINT :
                return length.Size * ((float) metrics.FontSize / 16) * 4 / 3;
            case SizeUnit.EM_100 :
                return length.Size * metrics.FontSize;
            case SizeUnit.EX_100 :
                return length.Size * metrics.FontSize / 2;
            case SizeUnit.PERCENT :
                float reSize = 0;
                if (perType == 1)
                    reSize = metrics.FullWidth * length.Size / 100;
                else if (perType == 2)
                    reSize = metrics.FullHeight * length.Size / 100;
                else
                    reSize = metrics.FontSize * length.Size / 100;
                return reSize;
            case SizeUnit.XX_SMALL :
                return metrics.FontSize * 0.4f;
            case SizeUnit.X_SMALL :
                return metrics.FontSize * 0.6f;
            case SizeUnit.SMALLER :
            case SizeUnit.SMALL :
                return metrics.FontSize * 0.8f;
            case SizeUnit.MEDIUM :
                return metrics.FontSize;
            case SizeUnit.LARGE :
            case SizeUnit.LARGER :
                return metrics.FontSize * 1.2f;
            case SizeUnit.X_LARGE :
                return metrics.FontSize * 1.6f;
            case SizeUnit.XX_LARGE :
                return metrics.FontSize * 1.8f;
            case SizeUnit.GBPIXEL :
                return length.Size*metrics.DPI/160;
        }
    }
    @Deprecated
    public final void setAlignmentType(byte alignmentType) {
        myFeatureMask |= 1 << Feature.ALIGNMENT_TYPE;
        myAlignmentType = alignmentType;
    }

    @Deprecated
    public final byte getAlignmentType() {
        return myAlignmentType;
    }

    @Deprecated
    public final void setFontFamily(String fontFamily) {
        myFeatureMask |= 1 << Feature.FONT_FAMILY;
        myFontFamily = fontFamily;
    }

    @Deprecated
    public final String getFontFamily() {
        return myFontFamily;
    }

    @Deprecated
    public final void setFontModifiers(byte supported, byte values) {
        myFeatureMask |= 1 << Feature.FONT_STYLE_MODIFIER;
        mySupportedFontModifiers = supported;
        myFontModifiers = values;
    }

    @Deprecated
    final void setFontModifier(byte modifier, boolean on) {
        myFeatureMask |= 1 << Feature.FONT_STYLE_MODIFIER;
        mySupportedFontModifiers |= modifier;
        if (on) {
            myFontModifiers |= modifier;
        } else {
            myFontModifiers &= ~modifier;
        }
    }

    @Deprecated
    public final GBBoolean3 getFontModifier(byte modifier) {
        if ((mySupportedFontModifiers & modifier) == 0) {
            return GBBoolean3.B3_UNDEFINED;
        }
        return (myFontModifiers & modifier) == 0 ? GBBoolean3.B3_FALSE : GBBoolean3.B3_TRUE;
    }
}
