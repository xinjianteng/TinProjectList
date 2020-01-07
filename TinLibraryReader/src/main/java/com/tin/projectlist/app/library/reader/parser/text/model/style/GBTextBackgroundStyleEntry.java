package com.tin.projectlist.app.library.reader.parser.text.model.style;

import com.tin.projectlist.app.library.base.utils.LogUtils;
import com.tin.projectlist.app.library.reader.parser.common.util.NumUtil;

/**
 *
 * 描述：背景样式 创建者： 燕冠楠<br>
 * 创建日期：2013-3-26<br>
 */
public final class GBTextBackgroundStyleEntry extends GBTextStyleEntry {

    final String TAG = "GBTextBackgroundStyleEntry";

    public GBTextBackgroundStyleEntry() {
    }

    private int backgroundColor = -1;
    private String backgroundImg=null;

    interface  Feature{
        byte BACKGROUND_COLOR=1;
        byte BACKGROUND_IMG=2;
    }

    public String getBackgroundImg() {
        return backgroundImg;
    }

    public void setBackgroundImg(String backgroundImg) {
        this.backgroundImg = backgroundImg;
    }


    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public final void setBackgroundColor(String backgroundColorStr) {
        if (null == backgroundColorStr || "".equals(backgroundColorStr)) {
            return;
        }
        try {
            this.backgroundColor = NumUtil.parseColor(backgroundColorStr);
        } catch (NumberFormatException ex) {
            LogUtils.e(TAG, "NumberFormatException  from GBTextBackgroundStyleEntry ");
        }
    }

    @Override
    public void loadData(char[] data, int offset, int len) {
        int length = offset + len;
        while (offset < length) {
            switch (data[offset++]) {
                case Feature.BACKGROUND_COLOR :
                    final int backgroundColor = (int) data[offset++] + (((int) data[offset++]) << 16);
                    setBackgroundColor(backgroundColor);
                    break;
                case Feature.BACKGROUND_IMG:
                    int imgLen=data[offset++];// 一个字符存储长度
                    this.backgroundImg=new String(data, offset,imgLen);
                    offset+=imgLen;
                    break;
            }
        }

    }

    @Override
    public char[] toChars() {
        int imgLen=null==backgroundImg||"".equals(backgroundImg)?0:backgroundImg.length();
        char[] block = new char[3+imgLen+2];//2: img mark  and img len
        int offset = 0, realLen = 0;
        if (0 != backgroundColor) {
            realLen += 3;
            block[offset++] = Feature.BACKGROUND_COLOR;
            block[offset++] = (char) backgroundColor;
            block[offset++] = (char) (backgroundColor >> 16);
        }

        if(null!= backgroundImg){
            realLen+=2;
            block[offset++]=Feature.BACKGROUND_IMG;
            block[offset++]=(char)imgLen;
            char[] backImg=backgroundImg.toCharArray();
            System.arraycopy(backImg, 0, block, offset, backImg.length);
            realLen+=imgLen;
            offset+=backImg.length;
        }

        return getRealData(block, realLen);
    }
}
