package com.tin.projectlist.app.library.reader.parser.view;

import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.file.image.GBImageData;
import com.tin.projectlist.app.library.reader.parser.object.GBColor;
import com.tin.projectlist.app.library.reader.parser.object.GBSize;

/**
 * 类名： GBPaint.java#ZLPaintContext<br>
 * 描述： 页面绘制抽象类<br>
 * 创建者： jack<br>
 * 创建日期：2013-3-28<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBPaint {

    protected GBPaint() {
    }

    /**
     * clear 功能描述： 绘制壁纸<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param wallPage 壁纸文件
     * @param mode     绘制壁纸模式
     */
    public abstract void drawWallPage(GBFile wallPage, PageEnum.PageBgMode mode);

    /**
     * clear 功能描述： 绘制单色背景<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param color 颜色
     */
    public abstract void drawSingleColor(GBColor color);

    /**
     * getBackgroundColor 功能描述： 获取当前使用的背景色<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     */
    public abstract GBColor getBgColor();

    public abstract int getWidth();

    public abstract int getHeight();

    private boolean mIsRestFont = true; // 是否设置字体
    private String mFontName = ""; // 字体名称
    private float mFontSize; // 字体大小

    public float getmFontSize() {
        return mFontSize;
    }

    public void setmFontSize(float mFontSize) {
        if (mFontSize == 0)
            return;
        this.mFontSize = mFontSize;
        setFontImp(mFontName, mFontSize, mIsBold, mIsItalic, mIsUnderline, myFontIsStrikedThrough);
    }

    private boolean mIsBold; // 是否粗体
    private boolean mIsItalic; // 是否斜体
    private boolean mIsUnderline; // 是否有下划线
    private boolean myFontIsStrikedThrough;

    /**
     * setFont 功能描述： 设置绘制字体<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param
     */
    // int size转换为float modify by yangn
    public final void setFont(String fontName, float size, boolean bold, boolean italic, boolean underline,
                              boolean strikeThrough) {
        if ((fontName != null) && !mFontName.equals(fontName)) {
            mFontName = fontName;
            mIsRestFont = true;
        }
        if (mFontSize != size) {
            mFontSize = size;
            mIsRestFont = true;
        }
        if (mIsBold != bold) {
            mIsBold = bold;
            mIsRestFont = true;
        }
        if (mIsItalic != italic) {
            mIsItalic = italic;
            mIsRestFont = true;
        }
        if (mIsUnderline != underline) {
            mIsUnderline = underline;
            mIsRestFont = true;
        }
        if (myFontIsStrikedThrough != strikeThrough) {
            myFontIsStrikedThrough = strikeThrough;
            mIsRestFont = true;
        }
        if (mIsRestFont) {
            mIsRestFont = false;
            setFontImp(mFontName, size, bold, italic, underline, strikeThrough);
            mSpaceWidth = -1;
            mStringHeight = -1;
            mDescent = -1;
        }
    }

    /**
     * setFontInternal 功能描述： 改变画笔的抽象方法<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param
     */
    protected abstract void setFontImp(String fontName, float size, boolean bold, boolean italic, boolean underline,
                                       boolean strikeThrough);

    public abstract void setTextColor(GBColor color);

    public abstract void setLineColor(GBColor color);

    public abstract void setLineWidth(int width);

    /**
     * setFillColor 功能描述： 设置填充色<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param
     */
    public final void setFillColor(GBColor color) {
        setFillColorImp(color, 0xFF);
    }

    /**
     * setFillColor 功能描述： 设置填充色 <br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param color 颜色
     * @param alpha 透明度
     */
    public abstract void setFillColorImp(GBColor color, int alpha);

    /**
     * getStringWidth 功能描述： 获取字符串所占宽度<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param
     */
    public final int getStrWidth(String string) {
        return getCharArrWidth(string.toCharArray(), 0, string.length());
    }

    /**
     * getStringWidth<br>
     * 功能描述： 获取字符数组所占宽度<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param charArr 字符数据
     * @param offset  计算开始位置
     * @param length  要计算的长度
     */
    public abstract int getCharArrWidth(char[] charArr, int offset, int length);

    // 单个空格宽度
    private int mSpaceWidth = -1;

    /**
     * getSpaceWidth<br>
     * 功能描述： 获取单个空格宽度<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param
     */
    public final int getSpaceWidth() {
        int spaceWidth = mSpaceWidth;
        if (spaceWidth == -1) {
            spaceWidth = getSpaceWidthImp();
            mSpaceWidth = spaceWidth;
        }
        return spaceWidth;
    }

    /**
     * getSpaceWidthInternal <br>
     * 功能描述： 获取当前单个空格宽度<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @return
     */
    protected abstract int getSpaceWidthImp();

    // 当前字体高度
    private int mStringHeight = -1;

    public final int getStringHeight() {
        int stringHeight = mStringHeight;
        if (stringHeight == -1) {
            stringHeight = getStringHeightImp();
            mStringHeight = stringHeight;
        }
        return stringHeight;
    }

    /**
     * getStringHeightInternal 功能描述： 获取当前字符串高度<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @return
     */
    abstract protected int getStringHeightImp();

    // 获取下边距
    private int mDescent = -1;

    public final int getDescent() {
        int descent = mDescent;
        if (descent == -1) {
            descent = getDescentImp();
            mDescent = descent;
        }
        return descent;
    }

    protected abstract int getDescentImp();

    /**
     * drawString 功能描述： 绘制一串字符串<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param x      开始绘制的x坐标
     * @param y      开始绘制的y坐标
     * @param string 要绘制的字符串
     */
    public final void drawString(int x, int y, String string) {
        drawString(x, y, string.toCharArray(), 0, string.length());
    }

    /**
     * drawString 功能描述： 绘制字符串<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param x
     * @param y
     * @param string 要绘制的字符串数组
     * @param offset 要绘制字符数组的开始位置
     * @param length 要绘制的长度
     */
    public abstract void drawString(int x, int y, char[] string, int offset, int length);

    /**
     * imageSize 功能描述：获取图片尺寸<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param image   图片对象
     * @param maxSize 最大尺寸
     * @param fitType 缩放模式
     * @return
     */
    public abstract GBSize getImgSize(GBImageData image, GBSize maxSize, PageEnum.ImgFitType fitType);

    public abstract GBSize getNoteSize(GBSize maxSize);

    public abstract GBSize getVideoSize(GBSize maxSize);

    public abstract GBSize getAudioSize(GBSize maxSize);

    public abstract GBSize getAnimBgSize(GBSize maxSize);

    /**
     * 功能描述： 绘制图片<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param x       开始绘制x坐标
     * @param y       开始绘制y坐标
     * @param image   图片对象
     * @param maxSize 最大尺寸
     * @param fitType 绘制模式
     */
    public abstract void drawImage(int x, int y, GBImageData image, GBSize maxSize, PageEnum.ImgFitType fitType);

    public abstract GBColor getAverageColor(GBImageData image, GBSize maxSize, PageEnum.ImgFitType fitType);

    /**
     * 功能描述：绘制一根线 <br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param x0 起始点x
     * @param y0 起始点y
     * @param x1 结束点x
     * @param y1 结束点y
     */
    public abstract void drawLine(int x0, int y0, int x1, int y1);

    /**
     * 功能描述： 填充一个矩形<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param x0 起始点x
     * @param y0 起始点y
     * @param x1 对角点x
     * @param y1 对角点y
     */
    public abstract void fillRectangle(int x0, int y0, int x1, int y1);

    /**
     * 功能描述： 绘制一多边形<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param xs x坐标数组
     * @param ys y坐标数组
     */
    public abstract void drawPolygonalLine(int[] xs, int ys[]);

    /**
     * 功能描述： 填充一个多边形<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param xs x坐标数组
     * @param ys y坐标数组
     */
    public abstract void fillPolygon(int[] xs, int[] ys);

    /**
     * 功能描述： 绘制边线<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param xs x坐标数组
     * @param ys y坐标数组
     */
    public abstract void drawOutline(int[] xs, int ys[]);

    /**
     * 功能描述： 绘制圆角矩形<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-30<br>
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param rx     x轴方向半径
     * @param ry     y轴方向半径
     */
    public abstract void drawRoundRect(float left, float top, float right, float bottom, int rx, int ry);

    /**
     * 功能描述： 绘制实点<br>
     * 创建者： jack<br>
     * 创建日期：2013-6-9<br>
     *
     * @param cx     绘制点x
     * @param cy     绘制点y
     * @param radius 点的半径
     */
    public abstract void drawCircle(float cx, float cy, float radius);
}
