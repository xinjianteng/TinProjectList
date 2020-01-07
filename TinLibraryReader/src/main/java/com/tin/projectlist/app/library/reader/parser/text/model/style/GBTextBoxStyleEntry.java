package com.tin.projectlist.app.library.reader.parser.text.model.style;

import com.tin.projectlist.app.library.reader.parser.common.util.NumUtil;
import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextStyleEntry;

/**
 *
 * 类名： .java<br>
 * 描述：外边距 内边距 创建者： yangn<br>
 * 创建日期：2013-5-28<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextBoxStyleEntry extends GBTextStyleEntry {

    interface Feature {
        byte MARGIN = 1;
        byte MARGIN_TOP = 2;
        byte MARGIN_RIGHT = 3;
        byte MARGIN_BOTTOM = 4;
        byte MARGIN_LEFT = 5;
        byte PADDING = 6;
        byte PADDING_TOP = 7;
        byte PADDING_RIGHT = 8;
        byte PADDING_BOTTOM = 9;
        byte PADDING_LEFT = 10;
    }

    /*
     * private int getVal(char[] data, int offset){ final int ret=(int)
     * data[offset++] + (((int) data[offset++]) << 16); float size =
     * NumUtil.parseFloat(ret); byte unit = (byte) data[offset++];
     * this.setMarginRight(new Length(size, unit)); return offset; }
     */

    @Override
    public void loadData(char[] data, int offset, int len) {
        int length = offset + len;

        float size = 0;
        byte unit = 0;

        while (offset < length) {
            switch (data[offset++]) {

                case Feature.MARGIN :
                    final int marginLen = (int) data[offset++];
                    byte marginUnit = 0;
                    Length temp = null;
                    float marginSizeOne = 0;
                    float marginSizeTwo = 0;
                    float marginSizeThree = 0;
                    int ret = 0;

                    switch (marginLen) {// 长度包含一个单位
                        case 2 :// 1
                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            final float marginSize = NumUtil.parseFloat(ret);
                            marginUnit = (byte) data[offset++];
                            temp = new Length(marginSize, marginUnit);
                            this.setMarginTop(temp);
                            this.setMarginRight(temp);
                            this.setMarginBottom(temp);
                            this.setMarginLeft(temp);
                            break;
                        case 3 :// 2
                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            marginSizeOne = NumUtil.parseFloat(ret);

                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            marginSizeTwo = NumUtil.parseFloat(ret);

                            marginUnit = (byte) data[offset++];

                            temp = new Length(marginSizeOne, marginUnit);
                            this.setMarginTop(temp);
                            this.setMarginBottom(temp);

                            temp = new Length(marginSizeTwo, marginUnit);
                            this.setMarginLeft(temp);
                            this.setMarginRight(temp);
                            break;
                        case 4 :// 3
                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            marginSizeOne = NumUtil.parseFloat(ret);

                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            marginSizeTwo = NumUtil.parseFloat(ret);

                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            marginSizeThree = NumUtil.parseFloat(ret);

                            marginUnit = (byte) data[offset++];

                            temp = new Length(marginSizeOne, marginUnit);
                            this.setMarginTop(temp);

                            temp = new Length(marginSizeTwo, marginUnit);
                            this.setMarginLeft(temp);
                            this.setMarginRight(temp);

                            temp = new Length(marginSizeThree, marginUnit);
                            this.setMarginBottom(temp);

                            break;
                        case 5 :// 4
                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            marginSizeOne = NumUtil.parseFloat(ret);

                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            marginSizeTwo = NumUtil.parseFloat(ret);

                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            marginSizeThree = NumUtil.parseFloat(ret);

                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            float marginSizeFour = NumUtil.parseFloat(ret);

                            temp = new Length(marginSizeOne, marginUnit);
                            this.setMarginTop(temp);

                            temp = new Length(marginSizeTwo, marginUnit);
                            this.setMarginRight(temp);

                            temp = new Length(marginSizeThree, marginUnit);
                            this.setMarginBottom(temp);

                            temp = new Length(marginSizeFour, marginUnit);
                            this.setMarginLeft(temp);
                            break;
                    }
                    setMargin();
                    break;
                case Feature.MARGIN_TOP :
                    // L.e("BoxEntry", "offset="+offset+"  len=="+length);
                    ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                    size = NumUtil.parseFloat(ret);
                    unit = (byte) data[offset++];
                    this.setMarginTop(new Length(size, unit));
                    break;
                case Feature.MARGIN_RIGHT :
                    ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                    size = NumUtil.parseFloat(ret);
                    unit = (byte) data[offset++];
                    this.setMarginRight(new Length(size, unit));
                    break;
                case Feature.MARGIN_BOTTOM :
                    ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                    size = NumUtil.parseFloat(ret);
                    unit = (byte) data[offset++];
                    this.setMarginBottom(new Length(size, unit));
                    break;
                case Feature.MARGIN_LEFT :
                    ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                    size = NumUtil.parseFloat(ret);
                    unit = (byte) data[offset++];
                    this.setMarginLeft(new Length(size, unit));
                    break;
                case Feature.PADDING :
                    final int paddingLen = (int) data[offset++];
                    byte paddingUnit = 0;
                    Length paddingVal = null;
                    float paddingSizeOne = 0;
                    float paddingSizeTwo = 0;
                    float paddingSizeThree = 0;

                    switch (paddingLen) {// 长度包含一个单位
                        case 2 :// 1
                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            final float paddingSize = NumUtil.parseFloat(ret);

                            paddingUnit = (byte) data[offset++];
                            paddingVal = new Length(paddingSize, paddingUnit);
                            this.setPaddingTop(paddingVal);
                            this.setPaddingRight(paddingVal);
                            this.setPaddingBottom(paddingVal);
                            this.setPaddingLeft(paddingVal);
                            break;
                        case 3 :// 2
                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            paddingSizeOne = NumUtil.parseFloat(ret);

                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            paddingSizeTwo = NumUtil.parseFloat(ret);

                            paddingUnit = (byte) data[offset++];

                            paddingVal = new Length(paddingSizeOne, paddingUnit);
                            this.setPaddingTop(paddingVal);
                            this.setPaddingBottom(paddingVal);

                            paddingVal = new Length(paddingSizeTwo, paddingUnit);
                            this.setPaddingLeft(paddingVal);
                            this.setPaddingRight(paddingVal);
                            break;
                        case 4 :// 3
                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            paddingSizeOne = NumUtil.parseFloat(ret);

                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            paddingSizeTwo = NumUtil.parseFloat(ret);

                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            paddingSizeThree = NumUtil.parseFloat(ret);

                            paddingUnit = (byte) data[offset++];

                            paddingVal = new Length(paddingSizeOne, paddingUnit);
                            this.setPaddingTop(paddingVal);

                            paddingVal = new Length(paddingSizeTwo, paddingUnit);
                            this.setPaddingLeft(paddingVal);
                            this.setPaddingRight(paddingVal);

                            paddingVal = new Length(paddingSizeThree, paddingUnit);
                            this.setPaddingBottom(paddingVal);

                            break;
                        case 5 :// 4
                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            paddingSizeOne = NumUtil.parseFloat(ret);

                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            paddingSizeTwo = NumUtil.parseFloat(ret);

                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            paddingSizeThree = NumUtil.parseFloat(ret);

                            ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                            float paddingSizeFour = NumUtil.parseFloat(ret);

                            marginUnit = (byte) data[offset++];

                            temp = new Length(paddingSizeOne, marginUnit);
                            this.setPaddingTop(temp);

                            temp = new Length(paddingSizeTwo, marginUnit);
                            this.setPaddingRight(temp);

                            temp = new Length(paddingSizeThree, marginUnit);
                            this.setPaddingBottom(temp);

                            temp = new Length(paddingSizeFour, marginUnit);
                            this.setPaddingLeft(temp);
                            break;
                    }
                    setPadding();
                    break;
                case Feature.PADDING_TOP :
                    ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                    size = NumUtil.parseFloat(ret);
                    unit = (byte) data[offset++];
                    this.setPaddingTop(new Length(size, unit));
                    break;
                case Feature.PADDING_RIGHT :
                    ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                    size = NumUtil.parseFloat(ret);
                    unit = (byte) data[offset++];
                    this.setPaddingRight(new Length(size, unit));
                    break;
                case Feature.PADDING_BOTTOM :
                    ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                    size = NumUtil.parseFloat(ret);
                    unit = (byte) data[offset++];
                    this.setPaddingBottom(new Length(size, unit));
                    break;
                case Feature.PADDING_LEFT :
                    ret = (int) data[offset++] + (((int) data[offset++]) << 16);
                    size = NumUtil.parseFloat(ret);
                    unit = (byte) data[offset++];
                    this.setPaddingLeft(new Length(size, unit));
                    break;
            }
        }

    }

    @Override
    public char[] toChars() {

        char[] block = new char[32];
        int offset = 0, realLen = 0;
        /*
         * if (null != margin && margin.length > 0) { realLen += 2;
         * block[offset++] = Feature.MARGIN; block[offset++] = (char)
         * (margin.length*2 + 1);// 每个长度使用两个char存储 // 加单位 realLen +=
         * (margin.length * 2 + 1); for (int i = 0; i < margin.length; i++) {
         * final int size = NumUtil.parseInt(margin[i].Size); block[offset++] =
         * (char) size; block[offset++] = (char) (size >> 16); } block[offset++]
         * = (char) margin[0].Unit; } else {
         */

        if (null != marginTop) {
            realLen += 4;
            block[offset++] = Feature.MARGIN_TOP;
            offset = setVal(block, offset, marginTop);
        }
        if (null != marginRight) {
            realLen += 4;
            block[offset++] = Feature.MARGIN_RIGHT;
            offset = setVal(block, offset, marginRight);
        }
        if (null != marginBottom) {
            realLen += 4;
            block[offset++] = Feature.MARGIN_BOTTOM;
            offset = setVal(block, offset, marginBottom);
        }
        if (null != marginLeft) {
            realLen += 4;
            block[offset++] = Feature.MARGIN_LEFT;
            offset = setVal(block, offset, marginLeft);
        }

        // }

        /*
         * if (null != padding && padding.length > 0) { realLen += 2; realLen +=
         * (padding.length * 2 + 1); block[offset++] = Feature.PADDING;
         * block[offset++] = (char) (padding.length*2 + 1); for (int i = 0; i <
         * padding.length; i++) { final int size =
         * NumUtil.parseInt(padding[i].Size); block[offset++] = (char) size;
         * block[offset++] = (char) (size >> 16); } block[offset++] = (char)
         * padding[0].Unit; } else {
         */
        if (null != paddingTop) {
            realLen += 4;
            block[offset++] = Feature.PADDING_TOP;
            offset = setVal(block, offset, paddingTop);
        }
        if (null != paddingRight) {
            realLen += 4;
            block[offset++] = Feature.PADDING_RIGHT;
            offset = setVal(block, offset, paddingRight);
        }
        if (null != paddingBottom) {
            realLen += 4;
            block[offset++] = Feature.PADDING_BOTTOM;
            offset = setVal(block, offset, paddingBottom);
        }
        if (null != paddingLeft) {
            realLen += 4;
            block[offset++] = Feature.PADDING_LEFT;
            offset = setVal(block, offset, paddingLeft);
        }
        // }

        return getRealData(block, realLen);
    }

    // 检索或设置对象四边的外补丁。默认值为 0 0。 如果提供全部四个参数值，将按上－右－下－左的顺序作用于四边。
    // 如果只提供一个，将用于全部的四边。如果提供两个，第一个用于上－下，第二个用于左－右。如果提供三个，第一
    // 个用于上，第二个用于左－右，第三个用于下
    private Length[] margin;

    private Length marginTop;
    private Length marginRight;
    private Length marginBottom;
    private Length marginLeft;

    // 检索或设置对象四边的外补丁。默认值为 0 0。 如果提供全部四个参数值，将按上－右－下－左的顺序作用于四边。
    // 如果只提供一个，将用于全部的四边。如果提供两个，第一个用于上－下，第二个用于左－右。如果提供三个，第一
    // 个用于上，第二个用于左－右，第三个用于下
    private Length[] padding;
    private Length paddingTop;
    private Length paddingRight;
    private Length paddingBottom;
    private Length paddingLeft;

    private void resetMargin() {
        marginTop = null;
        marginRight = null;
        marginBottom = null;
        marginLeft = null;
    }

    private void resetPadding() {
        paddingTop = null;
        paddingRight = null;
        paddingBottom = null;
        paddingLeft = null;
    }

    public Length[] getMargin() {
        return margin;
    }

    public void setMargin(Length[] margin) {
        this.margin = margin;
    }

    public void setMarginStr(String strings) {
        if (strings.contains(" ")) {
            String str[] = strings.split(" ");
            if (str != null) {
                setMargin(str);

            }
        } else {
            setMargin(strings);
        }
    }

    public void setMargin(String[] strings) {

        switch (strings.length) {
            case 1 :
                setMargin(strings[0]);
                break;
            case 2 :
                this.marginTop = super.parseLength(strings[0]);
                this.marginBottom = super.parseLength(strings[0]);

                this.marginRight = super.parseLength(strings[1]);
                this.marginLeft = super.parseLength(strings[1]);
                break;
            case 3 :
                this.marginTop = super.parseLength(strings[0]);

                this.marginRight = super.parseLength(strings[1]);
                this.marginLeft = super.parseLength(strings[1]);

                this.marginBottom = super.parseLength(strings[2]);
                break;
            case 4 :
                this.marginTop = super.parseLength(strings[0]);
                this.marginRight = super.parseLength(strings[1]);
                this.marginBottom = super.parseLength(strings[2]);
                this.marginLeft = super.parseLength(strings[3]);
                break;
        }

        setMargin();

    }

    public void setMargin(String margin) {
        this.marginTop = super.parseLength(margin);
        this.marginRight = super.parseLength(margin);
        this.marginBottom = super.parseLength(margin);
        this.marginLeft = super.parseLength(margin);
    }

    protected void setMargin() {
        margin = new Length[4];
        margin[0] = marginTop;
        margin[1] = marginRight;
        margin[2] = marginBottom;
        margin[3] = marginLeft;
    }

    public Length getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(Length marginTop) {
        this.marginTop = marginTop;
    }

    public void setMarginTop(String marginTopStr) {
        if (null == marginTopStr || "".equals(marginTopStr.trim())) {
            return;
        }
        this.marginTop = super.parseLength(marginTopStr);
    }

    public Length getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(Length marginRight) {
        this.marginRight = marginRight;
    }

    public void setMarginRight(String marginRightStr) {
        if (null == marginRightStr || "".equals(marginRightStr)) {
            return;
        }
        this.marginRight = super.parseLength(marginRightStr);
    }

    public Length getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(Length marginBottom) {
        this.marginBottom = marginBottom;
    }

    public void setMarginBottom(String marginBottomStr) {
        if (null == marginBottomStr || "".equals(marginBottomStr)) {
            return;
        }
        this.marginBottom = super.parseLength(marginBottomStr);
    }

    public Length getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(Length marginLeft) {
        this.marginLeft = marginLeft;
    }

    public void setMarginLeft(String marginLeftStr) {
        if (null == marginLeftStr || "".equals(marginLeftStr)) {
            return;
        }
        this.marginLeft = super.parseLength(marginLeftStr);
    }

    public Length[] getPadding() {
        return padding;
    }

    public void setPadding(Length[] padding) {
        this.padding = padding;
    }

    public void setPaddingStr(String strings) {
        if (strings.contains(" ")) {
            String str[] = strings.split(" ");
            if (str != null) {
                switch (str.length) {
                    case 1 :
                        setPadding(str[0]);
                        break;
                    case 2 :
                        setPadding(str[0], str[1]);
                        break;
                    case 3 :
                        setPadding(str[0], str[1], str[2]);
                        break;
                    case 4 :
                        setPadding(str[0], str[1], str[2], str[3]);
                        break;
                }

            }
        } else {
            setPadding(strings);
        }
    }
    public void setPadding(String... strings) {

        switch (strings.length) {
            case 1 :
                this.paddingTop = super.parseLength(strings[0]);
                this.paddingRight = super.parseLength(strings[0]);
                this.paddingBottom = super.parseLength(strings[0]);
                this.paddingLeft = super.parseLength(strings[0]);
                break;
            case 2 :
                this.paddingTop = super.parseLength(strings[0]);
                this.paddingBottom = super.parseLength(strings[0]);

                this.paddingRight = super.parseLength(strings[1]);
                this.paddingLeft = super.parseLength(strings[1]);

                break;
            case 3 :
                this.paddingTop = super.parseLength(strings[0]);

                this.paddingRight = super.parseLength(strings[1]);
                this.paddingLeft = super.parseLength(strings[1]);

                this.paddingBottom = super.parseLength(strings[2]);

                break;
            case 4 :
                this.paddingTop = super.parseLength(strings[0]);
                this.paddingRight = super.parseLength(strings[1]);
                this.paddingBottom = super.parseLength(strings[2]);
                this.paddingLeft = super.parseLength(strings[3]);
                break;
        }

        setPadding();
    }

    private void setPadding() {
        padding = new Length[4];
        padding[0] = paddingTop;
        padding[1] = paddingRight;
        padding[2] = paddingBottom;
        padding[3] = paddingLeft;
    }

    public Length getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(Length paddingTop) {
        this.paddingTop = paddingTop;
    }

    public void setPaddingTop(String paddingTopStr) {
        if (null == paddingTopStr || "".equals(paddingTopStr)) {
            return;
        }
        this.paddingTop = super.parseLength(paddingTopStr);
    }

    public Length getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(Length paddingRight) {
        this.paddingRight = paddingRight;
    }

    public void setPaddingRight(String paddingRightStr) {
        if (null == paddingRightStr || "".equals(paddingRightStr)) {
            return;
        }
        this.paddingRight = super.parseLength(paddingRightStr);
    }

    public Length getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(Length paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void setPaddingBottom(String paddingBottomStr) {
        if (null == paddingBottomStr || "".equals(paddingBottomStr)) {
            return;
        }
        this.paddingBottom = super.parseLength(paddingBottomStr);;
    }

    public Length getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(String paddingLeftStr) {
        if (null == paddingLeftStr || "".equals(paddingLeftStr)) {
            return;
        }
        this.paddingLeft = super.parseLength(paddingLeftStr);
    }

    public void setPaddingLeft(Length paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

}
