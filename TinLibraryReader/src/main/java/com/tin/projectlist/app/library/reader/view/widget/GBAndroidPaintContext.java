package com.tin.projectlist.app.library.reader.view.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import com.core.file.GBFile;
import com.core.file.image.GBImageData;
import com.core.object.GBColor;
import com.core.object.GBSize;
import com.core.option.GBBooleanOption;
import com.core.view.GBPaint;
import com.core.view.PageEnum;
import com.core.view.PageEnum.ImgFitType;
import com.geeboo.read.view.GBAndroidLibrary;
import com.geeboo.read.view.img.GBAndroidImageData;

/**
 * 类名： ZLAndroidPaintContext.java<br>
 * 描述： 页面绘制对象封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBAndroidPaintContext extends GBPaint {

    public static GBBooleanOption AntiAliasOption = new GBBooleanOption("Fonts", "AntiAlias", true);
    public static GBBooleanOption DeviceKerningOption = new GBBooleanOption("Fonts", "DeviceKerning", false);
    public static GBBooleanOption DitheringOption = new GBBooleanOption("Fonts", "Dithering", false);
    public static GBBooleanOption SubpixelOption = new GBBooleanOption("Fonts", "Subpixel", false);

    private final Canvas myCanvas;
    private final Paint myTextPaint = new Paint();
    private final Paint myLinePaint = new Paint();
    private final Paint myFillPaint = new Paint();
    private final Paint myOutlinePaint = new Paint();

    private final int myWidth;
    private final int myHeight;
    private final int myScrollbarWidth;

    private GBColor myBackgroundColor = new GBColor(0, 0, 0);

    GBAndroidPaintContext(Canvas canvas, int width, int height, int scrollbarWidth) {
        myCanvas = canvas;
        myWidth = width - scrollbarWidth;
        myHeight = height;
        myScrollbarWidth = scrollbarWidth;

        myTextPaint.setLinearText(false);
        myTextPaint.setAntiAlias(AntiAliasOption.getValue());
        if (DeviceKerningOption.getValue()) {
            myTextPaint.setFlags(myTextPaint.getFlags() | Paint.DEV_KERN_TEXT_FLAG);
        } else {
            myTextPaint.setFlags(myTextPaint.getFlags() & ~Paint.DEV_KERN_TEXT_FLAG);
        }
        myTextPaint.setDither(DitheringOption.getValue());
        myTextPaint.setSubpixelText(SubpixelOption.getValue());

        myLinePaint.setStyle(Paint.Style.STROKE);

        myOutlinePaint.setColor(Color.rgb(255, 127, 0));
        myOutlinePaint.setAntiAlias(true);
        myOutlinePaint.setDither(true);
        myOutlinePaint.setStrokeWidth(4);
        myOutlinePaint.setStyle(Paint.Style.STROKE);
        myOutlinePaint.setPathEffect(new CornerPathEffect(5));
        myOutlinePaint.setMaskFilter(new EmbossMaskFilter(new float[]{1, 1, 1}, .4f, 6f, 3.5f));
    }

    private static GBFile ourWallpaperFile;
    private static Bitmap ourWallpaper;
    @Override
    public void drawWallPage(GBFile wallpaperFile, PageEnum.PageBgMode mode) {
        if (!wallpaperFile.equals(ourWallpaperFile)) {
            ourWallpaperFile = wallpaperFile;
            ourWallpaper = null;
            try {
                final Bitmap fileBitmap = BitmapFactory.decodeStream(wallpaperFile.getInputStream());
                switch (mode) {
                    case STRETCH : {
                        final int w = fileBitmap.getWidth();
                        final int h = fileBitmap.getHeight();
                        final Bitmap wallpaper = Bitmap.createBitmap(myWidth, myHeight, fileBitmap.getConfig());
                        final Canvas wallpaperCanvas = new Canvas(wallpaper);
                        final Paint wallpaperPaint = new Paint();
                        wallpaperCanvas.drawBitmap(fileBitmap, null, new Rect(0, 0, myWidth, myHeight), wallpaperPaint);
                        // Matrix m = new Matrix();
                        // wallpaperCanvas.drawBitmap(fileBitmap, m,
                        // wallpaperPaint);
                        // m.preScale(-1, 1);
                        // m.postTranslate(2 * w, 0);
                        // wallpaperCanvas.drawBitmap(fileBitmap, m,
                        // wallpaperPaint);
                        // m.preScale(1, -1);
                        // m.postTranslate(0, 2 * h);
                        // wallpaperCanvas.drawBitmap(fileBitmap, m,
                        // wallpaperPaint);
                        // m.preScale(-1, 1);
                        // m.postTranslate(-2 * w, 0);
                        // wallpaperCanvas.drawBitmap(fileBitmap, m,
                        // wallpaperPaint);
                        ourWallpaper = wallpaper;
                        break;
                    }
                    case TILE :
                        ourWallpaper = fileBitmap;
                        break;
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (ourWallpaper != null) {
            myBackgroundColor = GBAndroidColorUtil.getAverageColor(ourWallpaper);
            final int w = ourWallpaper.getWidth();
            final int h = ourWallpaper.getHeight();
            for (int cw = 0, iw = 1; cw < myWidth; cw += w, ++iw) {
                for (int ch = 0, ih = 1; ch < myHeight; ch += h, ++ih) {
                    myCanvas.drawBitmap(ourWallpaper, cw, ch, myFillPaint);
                }
            }
        } else {
            drawSingleColor(new GBColor(128, 128, 128));
        }
    }

    @Override
    public void drawSingleColor(GBColor color) {
        myBackgroundColor = color;
        myFillPaint.setColor(GBAndroidColorUtil.rgb(color));
        myCanvas.drawRect(0, 0, myWidth + myScrollbarWidth, myHeight, myFillPaint);
    }

    @Override
    public GBColor getBgColor() {
        return myBackgroundColor;
    }

    public void fillPolygon(int[] xs, int ys[]) {
        final Path path = new Path();
        final int last = xs.length - 1;
        path.moveTo(xs[last], ys[last]);
        for (int i = 0; i <= last; ++i) {
            path.lineTo(xs[i], ys[i]);
        }
        myCanvas.drawPath(path, myFillPaint);
    }

    public void drawPolygonalLine(int[] xs, int ys[]) {
        final Path path = new Path();
        final int last = xs.length - 1;
        path.moveTo(xs[last], ys[last]);
        for (int i = 0; i <= last; ++i) {
            path.lineTo(xs[i], ys[i]);
        }
        myCanvas.drawPath(path, myLinePaint);
    }

    public void drawOutline(int[] xs, int ys[]) {
        final int last = xs.length - 1;
        int xStart = (xs[0] + xs[last]) / 2;
        int yStart = (ys[0] + ys[last]) / 2;
        int xEnd = xStart;
        int yEnd = yStart;
        if (xs[0] != xs[last]) {
            if (xs[0] > xs[last]) {
                xStart -= 5;
                xEnd += 5;
            } else {
                xStart += 5;
                xEnd -= 5;
            }
        } else {
            if (ys[0] > ys[last]) {
                yStart -= 5;
                yEnd += 5;
            } else {
                yStart += 5;
                yEnd -= 5;
            }
        }

        final Path path = new Path();
        path.moveTo(xStart, yStart);
        for (int i = 0; i <= last; ++i) {
            path.lineTo(xs[i], ys[i]);
        }
        path.lineTo(xEnd, yEnd);
        myCanvas.drawPath(path, myOutlinePaint);
    }
    // int size转换为float modify by yangn
    @Override
    protected void setFontImp(String family, float size, boolean bold, boolean italic, boolean underline,
                              boolean strikeThrought) {
        myTextPaint.setTypeface(AndroidFontUtil.typeface(family, bold, italic));
        myTextPaint.setTextSize(size);
        myTextPaint.setUnderlineText(underline);
        myTextPaint.setStrikeThruText(strikeThrought);
    }

    @Override
    public void setTextColor(GBColor color) {
        myTextPaint.setColor(GBAndroidColorUtil.rgb(color));
    }

    @Override
    public void setLineColor(GBColor color) {
        myLinePaint.setColor(GBAndroidColorUtil.rgb(color));
    }
    @Override
    public void setLineWidth(int width) {
        myLinePaint.setStrokeWidth(width);
    }

    @Override
    public void setFillColorImp(GBColor color, int alpha) {
        myFillPaint.setColor(GBAndroidColorUtil.rgba(color, alpha));
    }

    public int getWidth() {
        return myWidth;
    }
    public int getHeight() {
        return myHeight;
    }

    @Override
    public int getCharArrWidth(char[] string, int offset, int length) {
        boolean containsSoftHyphen = false;
        for (int i = offset; i < offset + length; ++i) {
            if (string[i] == (char) 0xAD) {
                containsSoftHyphen = true;
                break;
            }
        }
        if (!containsSoftHyphen) {
            return (int) (myTextPaint.measureText(new String(string, offset, length)) + 0.5f);
        } else {
            final char[] corrected = new char[length];
            int len = 0;
            for (int o = offset; o < offset + length; ++o) {
                final char chr = string[o];
                if (chr != (char) 0xAD) {
                    corrected[len++] = chr;
                }
            }
            return (int) (myTextPaint.measureText(corrected, 0, len) + 0.5f);
        }
    }
    @Override
    protected int getSpaceWidthImp() {
        return (int) (myTextPaint.measureText(" ", 0, 1) + 0.5f);
    }
    @Override
    protected int getStringHeightImp() {
        return (int) (myTextPaint.getTextSize() + 0.5f);
    }
    @Override
    protected int getDescentImp() {
        return (int) (myTextPaint.descent() + 0.5f);
    }
    @Override
    public void drawString(int x, int y, char[] string, int offset, int length) {
        boolean containsSoftHyphen = false;
        for (int i = offset; i < offset + length; ++i) {
            if (string[i] == (char) 0xAD) {
                containsSoftHyphen = true;
                break;
            }
        }
        if (!containsSoftHyphen) {
            myCanvas.drawText(string, offset, length, x, y, myTextPaint);
        } else {
            final char[] corrected = new char[length];
            int len = 0;
            for (int o = offset; o < offset + length; ++o) {
                final char chr = string[o];
                if (chr != (char) 0xAD) {
                    corrected[len++] = chr;
                }
            }
            myCanvas.drawText(corrected, 0, len, x, y, myTextPaint);
        }
    }

    @Override
    public GBSize getImgSize(GBImageData imageData, GBSize maxSize, ImgFitType scaling) {
        final Bitmap bitmap = ((GBAndroidImageData) imageData).getBitmap(maxSize, scaling);
        return (bitmap != null && !bitmap.isRecycled()) ? new GBSize(bitmap.getWidth(), bitmap.getHeight()) : null;
    }
    @Override
    public GBSize getNoteSize(GBSize maxSize) {
        final Bitmap bitmap = BitmapFactory.decodeResource(((GBAndroidLibrary) GBAndroidLibrary.Instance())
                .getActivity().getResources(), com.geeboo.R.drawable.notes);
        return bitmap != null ? new GBSize(Math.min(bitmap.getWidth(), maxSize.mWidth), Math.min(maxSize.mHeight,
                bitmap.getHeight())) : null;
    }
    @Override
    public GBSize getAudioSize(GBSize maxSize) {
        final Bitmap bitmap = BitmapFactory.decodeResource(((GBAndroidLibrary) GBAndroidLibrary.Instance())
                .getActivity().getResources(), com.geeboo.R.drawable.audio_bg);
        return bitmap != null ? new GBSize(Math.min(bitmap.getWidth(), maxSize.mWidth), Math.min(maxSize.mHeight,
                bitmap.getHeight())) : null;
    }
    @Override
    public GBSize getVideoSize(GBSize maxSize) {
        final Bitmap bitmap = BitmapFactory.decodeResource(((GBAndroidLibrary) GBAndroidLibrary.Instance())
                .getActivity().getResources(), com.geeboo.R.drawable.video_bg);
        return bitmap != null ? new GBSize(Math.min(bitmap.getWidth(), maxSize.mWidth), Math.min(maxSize.mHeight,
                bitmap.getHeight())) : null;
    }
    @Override
    public GBSize getAnimBgSize(GBSize maxSize) {
        final Bitmap bitmap = BitmapFactory.decodeResource(((GBAndroidLibrary) GBAndroidLibrary.Instance())
                .getActivity().getResources(), com.geeboo.R.drawable.anim_bg);
        return bitmap != null ? new GBSize(Math.min(bitmap.getWidth(), maxSize.mWidth), Math.min(maxSize.mHeight,
                bitmap.getHeight())) : null;
    }

    @Override
    public void drawImage(int x, int y, GBImageData imageData, GBSize maxSize, ImgFitType scaling) {
        final Bitmap bitmap = ((GBAndroidImageData) imageData).getBitmap(maxSize, scaling);
        if (bitmap != null && !bitmap.isRecycled()) {
            myCanvas.drawBitmap(bitmap, x, y - bitmap.getHeight(), myFillPaint);
        }
    }
    // add by jack 绘制图片方法
    public void drawImage(int x, int y, Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            myCanvas.drawBitmap(bitmap, x, y, myFillPaint);
            // myCanvas.drawRoundRect(new R, rx, ry, paint);
        }
    }
    public void drawImage(int startX, int startY, int endX, int endY, Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            myCanvas.drawBitmap(bitmap, null, new RectF(startX, startY, endX, endY), myFillPaint);
        }
    }
    public GBColor getAverageColor(GBImageData image, GBSize maxSize, ImgFitType scaling) {
        return GBAndroidColorUtil.getAverageColor(((GBAndroidImageData) image).getBitmap(maxSize, scaling));
    }

    @Override
    public void drawLine(int x0, int y0, int x1, int y1) {
        final Canvas canvas = myCanvas;
        final Paint paint = myLinePaint;
        paint.setAntiAlias(false);
        canvas.drawLine(x0, y0, x1, y1, paint);
        canvas.drawPoint(x0, y0, paint);
        canvas.drawPoint(x1, y1, paint);
        paint.setAntiAlias(true);
    }

    @Override
    public void fillRectangle(int x0, int y0, int x1, int y1) {
        if (x1 < x0) {
            int swap = x1;
            x1 = x0;
            x0 = swap;
        }
        if (y1 < y0) {
            int swap = y1;
            y1 = y0;
            y0 = swap;
        }
        myCanvas.drawRect(x0, y0, x1 + 1, y1 + 1, myFillPaint);
    }

    @Override
    public void drawRoundRect(float left, float top, float right, float bottom, int rx, int ry) {
        myCanvas.drawRoundRect(new RectF(left, top, right, bottom), rx, ry, myFillPaint);

    }

    @Override
    public void drawCircle(float cx, float cy, float radius) {
        myCanvas.drawCircle(cx, cy, radius, myFillPaint);
    }

}
