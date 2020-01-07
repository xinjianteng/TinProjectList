package com.tin.projectlist.app.library.reader.parser.text.model.style;

import com.tin.projectlist.app.library.reader.parser.common.util.ArrayUtils;
import com.tin.projectlist.app.library.reader.parser.common.util.NumUtil;

/**
 *
 * 类名： .java<br>
 * 描述：边框样式 创建者： yangn<br>
 * 创建日期：2013-6-8<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextBorderStyleEntry extends GBTextStyleEntry {

    interface Feature {
        byte BORDER = 1;
        byte BORDER_BOTTOM = 2;
        byte BORDER_BOTTOM_COLOR = 3;
        byte BORDER_BOTTOM_STYLE = 4;
        byte BORDER_BOTTOM_WIDTH = 5;
        byte BORDER_COLOR = 6;
        byte BORDER_LEFT = 7;
        byte BORDER_LEFT_COLOR = 8;
        byte BORDER_LEFT_STYLE = 9;
        byte BORDER_LEFT_WIDTH = 10;
        byte BORDER_RIGHT = 11;
        byte BORDER_RIGHT_COLOR = 12;
        byte BORDER_RIGHT_STYLE = 13;
        byte BORDER_RIGHT_WIDTH = 14;
        byte BORDER_STYLE = 15;
        byte BORDER_TOP = 16;
        byte BORDER_TOP_COLOR = 17;
        byte BORDER_TOP_STYLE = 18;
        byte BORDER_TOP_WIDTH = 19;
        byte BORDER_WIDTH = 20;
    }

    /**
     *
     * 类名： .java<br>
     * 描述： border 样式值<br>
     * 创建者： yangn<br>
     * 创建日期：2013-11-28<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public interface FeatureModifier {
        byte NONE = 0;// none; :　 默认值。无边框。不受任何指定的 border-width 值影响
        byte HIDDEN = 1;// hidden :　 隐藏边框。IE不支持
        byte DOTTED = 2;// dotted :　 在MAC平台上IE4+与WINDOWS和UNIX平台上IE5.5+为点线。否则为实线
        byte DASHED = 3;// dashed :　 在MAC平台上IE4+与WINDOWS和UNIX平台上IE5.5+为虚线。否则为实线
        byte SOLID = 4;// solid :　 实线边框
        byte DOUBLE = 5;// double :　 双线边框。两条单线与其间隔的和等于指定的 border-width 值
        byte GROOVE = 6;// groove :　 根据 border-color 的值画3D凹槽
        byte RIDGE = 7;// ridge :　 根据 border-color 的值画3D凸槽
        byte INSET = 8;// inset :　 根据 border-color 的值画3D凹边
        byte OUTSET = 9;// outset :　 根据 border-color 的值画3D凸边
    }

    byte getStyle(String styleStr) {
        if (null == styleStr || "".equals(styleStr.trim())) {
            return 0;
        }
        String val = styleStr.trim();
        if (val.equalsIgnoreCase("none")) {
            return FeatureModifier.NONE;
        } else if (val.equalsIgnoreCase("hidden")) {
            return FeatureModifier.HIDDEN;
        } else if (val.equalsIgnoreCase("dotted")) {
            return FeatureModifier.DOTTED;
        } else if (val.equalsIgnoreCase("dashed")) {
            return FeatureModifier.DASHED;
        } else if (val.equalsIgnoreCase("solid")) {
            return FeatureModifier.SOLID;
        } else if (val.equalsIgnoreCase("double")) {
            return FeatureModifier.DOUBLE;
        } else if (val.equalsIgnoreCase("groove")) {
            return FeatureModifier.GROOVE;
        } else if (val.equalsIgnoreCase("ridge")) {
            return FeatureModifier.RIDGE;
        } else if (val.equalsIgnoreCase("inset")) {
            return FeatureModifier.INSET;
        } else if (val.equalsIgnoreCase("outset")) {
            return FeatureModifier.OUTSET;
        } else {
            return 0;
        }

    }

    // border-width || border-style || border-color

    private int borderBottomColor = -1;
    private byte borderBottomStyle;
    private Length borderBottomWidth;

    private int borderLeftColor = -1;
    private byte borderLeftStyle;
    private Length borderLeftWidth;

    private int borderRightColor = -1;
    private byte borderRightStyle;
    private Length borderRightWidth;

    private int borderTopColor = -1;
    private byte borderTopStyle;
    private Length borderTopWidth;

    /*
     * @Override public String toString() { StringBuilder sb = new
     * StringBuilder(); sb.append("bottom-color=" + this.borderBottomColor);
     * sb.append("bottom-style=" + this.borderBottomStyle); String ss =
     * this.borderBottomWidth == null ? " is null" : borderBottomWidth.Size +
     * ""; sb.append("bottom-width" + ss); sb.append("right-color=" +
     * this.borderRightColor); sb.append("right-style=" +
     * this.borderRightStyle); ss = this.borderRightWidth == null ? " is null" :
     * this.borderRightWidth.Size + ""; sb.append("right-width" + ss);
     * sb.append("left-color=" + this.borderLeftColor); sb.append("left-style="
     * + this.borderLeftStyle); ss = this.borderLeftWidth == null ? " is null" :
     * this.borderLeftWidth.Size + ""; sb.append("left-width" + ss);
     * sb.append("top-color=" + this.borderTopColor); sb.append("top-style=" +
     * this.borderTopStyle); ss = this.borderTopWidth == null ? " is null" :
     * this.borderTopWidth.Size + ""; sb.append("top-width" + ss); return
     * sb.toString(); }
     */

    @Override
    public char[] toChars() {
        char[] block = new char[36];
        int offset = 0, realLen = 0;
        // bottom

        if (0 != borderBottomColor) {
            realLen += 3;
            block[offset++] = Feature.BORDER_BOTTOM_COLOR;
            block[offset++] = (char) this.borderBottomColor;
            block[offset++] = (char) (this.borderBottomColor >> 16);
        }

        if (null != borderBottomWidth) {
            realLen += 4;
            block[offset++] = Feature.BORDER_BOTTOM_WIDTH;
            offset = setVal(block, offset, this.borderBottomWidth);
        }

        if (0 != borderBottomStyle) {
            realLen += 2;
            block[offset++] = Feature.BORDER_BOTTOM_STYLE;
            block[offset++] = (char) this.borderBottomStyle;
        }
        // left

        if (0 != borderLeftColor) {
            realLen += 3;
            block[offset++] = Feature.BORDER_LEFT_COLOR;
            block[offset++] = (char) this.borderLeftColor;
            block[offset++] = (char) (this.borderLeftColor >> 16);
        }

        if (null != borderLeftWidth) {
            realLen += 4;
            block[offset++] = Feature.BORDER_LEFT_WIDTH;
            offset = setVal(block, offset, this.borderLeftWidth);
        }

        if (0 != borderLeftStyle) {
            realLen += 2;
            block[offset++] = Feature.BORDER_LEFT_STYLE;
            block[offset++] = (char) this.borderLeftStyle;
        }
        // right

        if (0 != borderRightColor) {
            realLen += 3;
            block[offset++] = Feature.BORDER_RIGHT_COLOR;
            block[offset++] = (char) this.borderRightColor;
            block[offset++] = (char) (this.borderRightColor >> 16);
        }

        if (null != borderRightWidth) {
            realLen += 4;
            block[offset++] = Feature.BORDER_RIGHT_WIDTH;
            offset = setVal(block, offset, this.borderRightWidth);
        }

        if (0 != borderRightStyle) {
            realLen += 2;
            block[offset++] = Feature.BORDER_RIGHT_STYLE;
            block[offset++] = (char) this.borderRightStyle;
        }

        // top
        if (0 != borderTopColor) {
            realLen += 3;
            block[offset++] = Feature.BORDER_TOP_COLOR;
            block[offset++] = (char) this.borderTopColor;
            block[offset++] = (char) (this.borderTopColor >> 16);
        }

        if (null != borderTopWidth) {
            realLen += 4;
            block[offset++] = Feature.BORDER_TOP_WIDTH;
            offset = setVal(block, offset, this.borderTopWidth);
        }

        if (0 != borderTopStyle) {
            realLen += 2;
            block[offset++] = Feature.BORDER_TOP_STYLE;
            block[offset++] = (char) this.borderTopStyle;
        }

        // if(null!=){}

        return getRealData(block, realLen);
    }

    public Length getBorderTopWidth() {
        return borderTopWidth;
    }

    public void setBorderTopWidth(Length borderTopWidth) {
        this.borderTopWidth = borderTopWidth;
    }

    public void setBorderTopWidth(String borderTopWidth) {
        this.borderTopWidth = super.parseLength(borderTopWidth);
    }

    @Override
    public void loadData(char[] data, int offset, int len) {
        int length = offset + len;
        int ret = 0;
        byte unit = 0;
        while (offset < length) {
            switch (data[offset++]) {
            /*
             * case Feature.BORDER: break;
             */
                case Feature.BORDER_BOTTOM :
                    break;
                case Feature.BORDER_BOTTOM_COLOR :
                    this.borderBottomColor = (int) data[offset++] + (((int) data[offset++]) << 16);
                    break;
                case Feature.BORDER_BOTTOM_STYLE :
                    this.borderBottomStyle = (byte) data[offset++];
                    break;
                case Feature.BORDER_BOTTOM_WIDTH :
                    ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                    unit = (byte) data[offset++];
                    this.setBorderBottomWidth(new Length(NumUtil.parseFloat(ret), unit));
                    break;
                case Feature.BORDER_COLOR :

                    break;
                case Feature.BORDER_LEFT :
                    break;
                case Feature.BORDER_LEFT_COLOR :
                    this.borderLeftColor = (int) data[offset++] + (((int) data[offset++]) << 16);
                    break;
                case Feature.BORDER_LEFT_STYLE :
                    this.borderLeftStyle = (byte) data[offset++];
                    break;
                case Feature.BORDER_LEFT_WIDTH :
                    ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                    unit = (byte) data[offset++];
                    this.setBorderLeftWidth(new Length(NumUtil.parseFloat(ret), unit));
                    break;
                case Feature.BORDER_RIGHT :
                    break;
                case Feature.BORDER_RIGHT_COLOR :
                    this.borderRightColor = (int) data[offset++] + (((int) data[offset++]) << 16);
                    break;
                case Feature.BORDER_RIGHT_STYLE :
                    this.borderRightStyle = (byte) data[offset++];
                    break;
                case Feature.BORDER_RIGHT_WIDTH :
                    ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                    unit = (byte) data[offset++];
                    this.setBorderRightWidth(new Length(NumUtil.parseFloat(ret), unit));
                    break;
                case Feature.BORDER_STYLE :
                    break;
                case Feature.BORDER_TOP :
                    break;
                case Feature.BORDER_TOP_COLOR :
                    this.borderTopColor = (int) data[offset++] + (((int) data[offset++]) << 16);
                    break;
                case Feature.BORDER_TOP_STYLE :
                    this.borderTopStyle = (byte) data[offset++];
                    break;
                case Feature.BORDER_TOP_WIDTH :
                    ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                    unit = (byte) data[offset++];
                    this.setBorderTopWidth(new Length(NumUtil.parseFloat(ret), unit));
                    break;
                case Feature.BORDER_WIDTH :
                    break;
            }
        }
    }

    public void setBorderWidth(Length borderWidth) {
        this.borderTopWidth = borderWidth;
        this.borderRightWidth = borderWidth;
        this.borderBottomWidth = borderWidth;
        this.borderLeftWidth = borderWidth;
    }

    public void setBorderWidthStr(String borderWidthStr) {
        this.borderTopWidth = super.parseLength(borderWidthStr);
        this.borderRightWidth = super.parseLength(borderWidthStr);
        this.borderBottomWidth = super.parseLength(borderWidthStr);
        this.borderLeftWidth = super.parseLength(borderWidthStr);
    }

    public void setBorderWidth(String borderWidthStr) {
        if (null == borderWidthStr || "".equals(borderWidthStr)) {
            return;
        }

        String[] borderWidthArrStr = ArrayUtils.splitDeleteSpace(borderWidthStr);
        if (borderWidthArrStr != null) {
            this.setBorderWidth(borderWidthArrStr);
        }

    }

    public void setBorderWidth(String[] borderWidthArr) {
        switch (borderWidthArr.length) {
            case 1 :
                this.setBorderWidthStr(borderWidthArr[0]);
                break;
            case 2 :
                this.borderTopWidth = GBTextStyleEntry.parseLength(borderWidthArr[0]);
                this.borderBottomWidth = GBTextStyleEntry.parseLength(borderWidthArr[0]);

                this.borderRightWidth = GBTextStyleEntry.parseLength(borderWidthArr[1]);
                this.borderLeftWidth = GBTextStyleEntry.parseLength(borderWidthArr[1]);
                break;
            case 3 :
                this.borderTopWidth = GBTextStyleEntry.parseLength(borderWidthArr[0]);

                this.borderRightWidth = GBTextStyleEntry.parseLength(borderWidthArr[1]);
                this.borderLeftWidth = GBTextStyleEntry.parseLength(borderWidthArr[1]);

                this.borderBottomWidth = GBTextStyleEntry.parseLength(borderWidthArr[2]);
                break;
            case 4 :
                this.borderTopWidth = GBTextStyleEntry.parseLength(borderWidthArr[0]);
                this.borderRightWidth = GBTextStyleEntry.parseLength(borderWidthArr[1]);
                this.borderBottomWidth = GBTextStyleEntry.parseLength(borderWidthArr[2]);
                this.borderLeftWidth = GBTextStyleEntry.parseLength(borderWidthArr[3]);
                break;
        }
    }

    public Length getBorderBottomWidth() {
        return borderBottomWidth;
    }

    public void setBorderBottomWidth(Length borderBottomWidth) {
        this.borderBottomWidth = borderBottomWidth;
    }

    public void setBorderBottomWidth(String borderBottomWidth) {
        this.borderBottomWidth = super.parseLength(borderBottomWidth);
    }

    public Length getBorderLeftWidth() {
        return borderLeftWidth;
    }

    public void setBorderLeftWidth(Length borderLeftWidth) {
        this.borderLeftWidth = borderLeftWidth;
    }

    public void setBorderLeftWidth(String borderLeftWidth) {
        this.borderLeftWidth = super.parseLength(borderLeftWidth);
    }

    public Length getBorderRightWidth() {
        return borderRightWidth;
    }

    public void setBorderRightWidth(Length borderRightWidth) {
        this.borderRightWidth = borderRightWidth;
    }

    public void setBorderRightWidth(String borderRightWidth) {
        this.borderRightWidth = super.parseLength(borderRightWidth);
    }

    private Length htmBorder;

    public void setHtmBorder(String val) {
        this.htmBorder = super.parseLength(val);
        this.setBorder(val);
    }

    /**
     *
     * 功能描述： border : border-width border-style border-color 创建者： yangn<br>
     * 创建日期：2013-6-6<br>
     *
     * @param
     */
    public void setBorder(String[] border) {
        if (null == border || "".equals(border)) {
            return;
        }

        if (border.length == 3) {
            this.setBorderWidth(border[0]);
            this.setBorderStyle(border[1]);
            this.setBorderColor(border[2]);
        } else if (border.length == 2) {
            this.setBorderWidth(border[0]);
            this.setBorderStyle(border[1]);
            // this.setBorderColor(border[2]);
        } else if (border.length == 1) {
            this.setBorderWidth(border[0]);
            this.setBorderStyle("solid");
        }

    }

    public void setBorder(String border) {
        if (null == border || "".equals(border)) {
            return;
        }
        String[] borderArr = ArrayUtils.splitDeleteSpace(border);
        if (borderArr.length == 3 || borderArr.length == 2 || borderArr.length == 1) {
            this.setBorder(borderArr);
        } else {
            return;
        }
    }

    public void setBorderColor(String borderColorStr) {
        if (null == borderColorStr || "".equals(borderColorStr)) {
            return;
        }

        if (borderColorStr.trim().contains(" ")) {
            String[] borderColorArrStr = ArrayUtils.splitDeleteSpace(borderColorStr);

            int[] borderColorArr = new int[borderColorArrStr.length];
            for (int i = 0; i < borderColorArr.length; i++) {
                try {
                    borderColorArr[i] = NumUtil.parseColor(borderColorArrStr[i].trim());
                } catch (NumberFormatException ex) {
                    continue;
                }
            }
            setBorderColor(borderColorArr);

        } else {
            this.setBorderColorStr(borderColorStr.trim());
        }
    }

    public void setBorderColorStr(String borderColorStr) {
        try {
            int color = NumUtil.parseColor(borderColorStr);
            this.setBorderColor(color);
        } catch (NumberFormatException ex) {

        }
    }

    public void setBorderBottom(String borderVal) {
        if (null == borderVal || "".equals(borderVal)) {
            return;
        }

        String borderValArr[] = ArrayUtils.splitDeleteSpace(borderVal);
        if (borderValArr == null || borderValArr.length != 3) {
            return;
        }
        this.setBorderBottomWidth(borderValArr[0]);
        this.setBorderBottomStyle(borderValArr[1]);
        this.setBorderBottomColor(borderValArr[2]);
    }

    public void setBorderColor(int borderColor) {
        this.borderTopColor = borderColor;
        this.borderRightColor = borderColor;
        this.borderBottomColor = borderColor;
        this.borderLeftColor = borderColor;
    }

    public void setBorderColor(int[] borderColor) {
        switch (borderColor.length) {
            case 1 :
                this.setBorderColor(borderColor[0]);
                break;
            case 2 :
                this.borderBottomColor = borderColor[0];
                this.borderTopColor = borderColor[0];

                this.borderRightColor = borderColor[1];
                this.borderLeftColor = borderColor[1];

                break;
            case 3 :
                this.borderTopColor = borderColor[0];

                this.borderRightColor = borderColor[1];
                this.borderLeftColor = borderColor[1];

                this.borderBottomColor = borderColor[2];
                break;
            case 4 :
                this.borderTopColor = borderColor[0];
                this.borderRightColor = borderColor[1];
                this.borderBottomColor = borderColor[2];
                this.borderLeftColor = borderColor[3];
                break;
        }

    }

    public void setBorderStyleStr(String borderStyleStr) {
        byte borderStyle = getStyle(borderStyleStr);
        this.borderTopStyle = borderStyle;
        this.borderRightStyle = borderStyle;
        this.borderBottomStyle = borderStyle;
        this.borderLeftStyle = borderStyle;
    }

    public void setBorderStyle(String styleStr) {
        if (null == styleStr || "".equals(styleStr)) {
            return;
        }

        String[] styleArrStr = ArrayUtils.splitDeleteSpace(styleStr);
        if (styleArrStr == null || styleArrStr.length == 1) {
            this.setBorderStyleStr(styleStr);
        } else {
            switch (styleArrStr.length) {
                case 1 :
                    this.setBorderStyleStr(styleArrStr[0]);
                    break;
                case 2 :
                    this.setBorderTopStyle(styleArrStr[0]);
                    this.setBorderBottomStyle(styleArrStr[0]);

                    this.setBorderRightStyle(styleArrStr[1]);
                    this.setBorderLeftStyle(styleArrStr[1]);
                    break;
                case 3 :
                    this.setBorderTopStyle(styleArrStr[0]);

                    this.setBorderRightStyle(styleArrStr[1]);
                    this.setBorderLeftStyle(styleArrStr[1]);

                    this.setBorderBottomStyle(styleArrStr[2]);
                    break;
                case 4 :
                    this.setBorderTopStyle(styleArrStr[0]);
                    this.setBorderRightStyle(styleArrStr[1]);
                    this.setBorderBottomStyle(styleArrStr[2]);
                    this.setBorderLeftStyle(styleArrStr[3]);
                    break;
            }
        }

    }

    public int getBorderBottomColor() {
        return borderBottomColor;
    }

    public void setBorderBottomColor(int borderBottomColor) {
        this.borderBottomColor = borderBottomColor;
    }

    public void setBorderBottomColor(String borderBottomColorStr) {
        try {
            this.borderBottomColor = NumUtil.parseColor(borderBottomColorStr.trim());
        } catch (NumberFormatException ex) {

        }
    }

    public byte getBorderBottomStyle() {
        return borderBottomStyle;
    }

    public void setBorderBottomStyle(byte borderBottomStyle) {
        this.borderBottomStyle = borderBottomStyle;
    }

    public void setBorderBottomStyle(String borderBottomStyle) {

        this.borderBottomStyle = getStyle(borderBottomStyle);

    }

    public void setBorderLeft(String borderVal) {
        if (null == borderVal || "".equals(borderVal)) {
            return;
        }

        String borderValArr[] = ArrayUtils.splitDeleteSpace(borderVal);
        if (borderValArr == null || borderValArr.length != 3) {
            return;
        }
        this.setBorderLeftWidth(borderValArr[0]);
        this.setBorderLeftStyle(borderValArr[1]);
        this.setBorderLeftColor(borderValArr[2]);
    }

    public int getBorderLeftColor() {
        return borderLeftColor;
    }

    public void setBorderLeftColor(int borderLeftColor) {
        this.borderLeftColor = borderLeftColor;
    }

    public void setBorderLeftColor(String borderLeftColor) {
        try {
            this.borderLeftColor = NumUtil.parseColor(borderLeftColor.trim());
        } catch (NumberFormatException ex) {

        }
    }

    public byte getBorderLeftStyle() {
        return borderLeftStyle;
    }

    public void setBorderLeftStyle(byte borderLeftStyle) {
        this.borderLeftStyle = borderLeftStyle;
    }

    public void setBorderLeftStyle(String borderLeftStyle) {

        this.borderLeftStyle = getStyle(borderLeftStyle);

    }

    public void setBorderRight(String borderVal) {
        if (null == borderVal || "".equals(borderVal)) {
            return;
        }

        String borderValArr[] = ArrayUtils.splitDeleteSpace(borderVal);
        if (borderValArr == null || borderValArr.length != 3) {
            return;
        }
        this.setBorderRightWidth(borderValArr[0]);
        this.setBorderRightStyle(borderValArr[1]);
        this.setBorderRightColor(borderValArr[2]);
    }

    public int getBorderRightColor() {
        return borderRightColor;
    }

    public void setBorderRightColor(int borderRightColor) {
        this.borderRightColor = borderRightColor;
    }

    public void setBorderRightColor(String borderRightColor) {
        try {
            this.borderRightColor = NumUtil.parseColor(borderRightColor.trim());
        } catch (NumberFormatException ex) {

        }
    }

    public byte getBorderRightStyle() {
        return borderRightStyle;
    }

    public void setBorderRightStyle(byte borderRightStyle) {
        this.borderRightStyle = borderRightStyle;
    }

    public void setBorderRightStyle(String borderRightStyle) {

        this.borderRightStyle = getStyle(borderRightStyle);

    }

    public void setBorderTop(String borderTop) {
        if (null == borderTop || "".equals(borderTop)) {
            return;
        }

        String borderTopVal[] = ArrayUtils.splitDeleteSpace(borderTop);
        if (borderTopVal == null || borderTopVal.length != 3) {
            return;
        }
        this.setBorderTopWidth(borderTopVal[0]);
        this.setBorderTopStyle(borderTopVal[1]);
        this.setBorderTopColor(borderTopVal[2]);
    }

    public int getBorderTopColor() {
        return borderTopColor;
    }

    public void setBorderTopColor(int borderTopColor) {
        this.borderTopColor = borderTopColor;
    }

    public void setBorderTopColor(String borderTopColor) {
        try {
            this.borderTopColor = NumUtil.parseColor(borderTopColor.trim());
        } catch (NumberFormatException ex) {

        }
    }

    public byte getBorderTopStyle() {
        return borderTopStyle;
    }

    public void setBorderTopStyle(byte borderTopStyle) {
        this.borderTopStyle = borderTopStyle;
    }

    public void setBorderTopStyle(String borderTopStyle) {

        this.borderTopStyle = getStyle(borderTopStyle);

    }

}
