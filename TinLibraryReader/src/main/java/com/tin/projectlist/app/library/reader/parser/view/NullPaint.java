package com.tin.projectlist.app.library.reader.parser.view;

import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.file.image.GBImageData;
import com.tin.projectlist.app.library.reader.parser.object.GBColor;
import com.tin.projectlist.app.library.reader.parser.object.GBSize;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum.ImgFitType;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum.PageBgMode;
/**
 * 类名： NullPaint.java<br>
 * 描述： 绘制空实现<br>
 * 创建者： jack<br>
 * 创建日期：2013-3-28<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class NullPaint extends GBPaint {

    @Override
    public void drawWallPage(GBFile wallPage, PageBgMode mode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawSingleColor(GBColor color) {
        // TODO Auto-generated method stub

    }

    @Override
    public GBColor getBgColor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getWidth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getHeight() {
        // TODO Auto-generated method stub
        return 0;
    }
    // int size转换为float modify by yangn
    @Override
    protected void setFontImp(String fontName, float size, boolean bold, boolean italic, boolean underline,
                              boolean strikeThrough) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTextColor(GBColor color) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLineColor(GBColor color) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLineWidth(int width) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFillColorImp(GBColor color, int alpha) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getCharArrWidth(char[] charArr, int offset, int length) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected int getSpaceWidthImp() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected int getStringHeightImp() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected int getDescentImp() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void drawString(int x, int y, char[] string, int offset, int length) {
        // TODO Auto-generated method stub

    }

    @Override
    public GBSize getImgSize(GBImageData image, GBSize maxSize, ImgFitType fitType) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public GBSize getNoteSize(GBSize maxSize) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public GBSize getAudioSize(GBSize maxSize) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public GBSize getVideoSize(GBSize maxSize) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public GBSize getAnimBgSize(GBSize maxSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void drawImage(int x, int y, GBImageData image, GBSize maxSize, ImgFitType fitType) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawLine(int x0, int y0, int x1, int y1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fillRectangle(int x0, int y0, int x1, int y1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawPolygonalLine(int[] xs, int[] ys) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fillPolygon(int[] xs, int[] ys) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawOutline(int[] xs, int[] ys) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawRoundRect(float left, float top, float right, float bottom, int rx, int ry) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawCircle(float cx, float cy, float radius) {
        // TODO Auto-generated method stub

    }

    @Override
    public GBColor getAverageColor(GBImageData image, GBSize maxSize, ImgFitType fitType) {
        // TODO Auto-generated method stub
        return null;
    }

}
