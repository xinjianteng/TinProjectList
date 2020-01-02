package com.core.text.model.style;

import com.core.common.util.NumUtil;
import com.core.text.model.GBTextAlignmentType;

/**
 *
 * 类名：文本对齐方式 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-6-7<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextWordStyleEntry extends GBTextStyleEntry {

    interface Feature {
        byte TEXT_ALIGN = 1;
        byte TEXT_INDENT = 2;
    }

    private byte textAlign = 0;
    private Length textIndent = null;// text-indent




    public Length getTextIndent() {
        return textIndent;
    }

    public void setTextIndent(Length textIndent) {
        this.textIndent = textIndent;
    }

    public void setTextIndent(String textIndentStr) {
        if (null == textIndentStr || "".equals(textIndentStr)) {
            return;
        }
        this.textIndent = super.parseLength(textIndentStr);
    }

    public byte getTextAlign() {
        return textAlign;
    }

    public void setTextAlign(byte textAlign) {
        this.textAlign = textAlign;
    }

    public void setTextAlign(String textAlign) {
        if (null == textAlign || "".equals(textAlign)) {
            return;
        }
        switch (textAlign.charAt(0)) {
            case 'l' :
            case 'L' :
                this.textAlign = GBTextAlignmentType.ALIGN_LEFT;
                break;
            case 'r' :
            case 'R' :
                this.textAlign = GBTextAlignmentType.ALIGN_RIGHT;
                break;
            case 'c' :
            case 'C' :
                this.textAlign = GBTextAlignmentType.ALIGN_CENTER;
                break;
            case 'j' :
            case 'J' :
                this.textAlign = GBTextAlignmentType.ALIGN_JUSTIFY;
                break;
        }
    }

    @Override
    public void loadData(char[] data, int offset, int len) {
        int length = offset + len;
        int ret = 0;
        byte unit = 0;
        while (offset < length) {
            switch (data[offset++]) {
                case Feature.TEXT_ALIGN :
                    this.textAlign = (byte) data[offset++];
                    break;
                case Feature.TEXT_INDENT :
                    ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                    unit = (byte) data[offset++];
                    this.textIndent = new Length(NumUtil.parseFloat(ret), unit);
                    break;

            }
        }

    }

    @Override
    public char[] toChars() {
        char[] block = new char[6];
        int offset = 0, realLen = 0;
        if (0 != textAlign) {
            realLen += 2;
            block[offset++] = Feature.TEXT_ALIGN;
            block[offset++] = (char) this.textAlign;
        }

        if (null != this.textIndent) {
            realLen += 4;
            block[offset++] = Feature.TEXT_INDENT;
            offset = setVal(block, offset, this.textIndent);
        }



        return getRealData(block, realLen);
    }

}
