package com.tin.projectlist.app.library.reader.view.widget;

import android.graphics.Bitmap;

import com.tin.projectlist.app.library.reader.parser.view.PageEnum;

/**
 * 类名： BitmapManager.java<br>
 * 描述： 图片管理类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class BitmapManager {
    // 缓存图片数量
    private final int SIZE = 2;
    // 图片缓存数组
    private final Bitmap[][] myBitmaps = new Bitmap[SIZE][SIZE];
    // private final Bitmap[] mLoadingBitmap = new Bitmap[SIZE];
    // 对应图片的位置
    private final PageEnum.PageIndex[] myIndexes = new PageEnum.PageIndex[SIZE];

    private int myWidth = 0;
    private int myHeight = 0;

    public final GBAndroidWidget myWidget;
    private boolean mIsDoubleModel;

    /**
     * 构造方法
     *
     * @param widget 绘制控件
     * @param isDouble 是否双页模式
     */
    BitmapManager(GBAndroidWidget widget, boolean isDouble) {
        myWidget = widget;
        mIsDoubleModel = isDouble;
    }


    /**
     * 功能描述： 设置图片宽高<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-26<br>
     *
     * @param w
     * @param h
     */
    void setSize(int w, int h, boolean isDouble) {
        // 如果正在打开图书时不让切换屏幕
        if (myWidth > 0 && ReaderApplication.isOpening()) {
            ReaderApplication.setmAferRun(new Runnable() {
                @Override
                public void run() {
                    myWidget.clearPageCache();
                    myWidget.repaintOnThread(true);
                }
            });
            return;
        }

        mIsDoubleModel = isDouble;
        if (myWidth != w || myHeight != h) {
            myWidth = w;
            myHeight = h;
            finalize();
            myWidget.clearPageCache();
        }
    }

    public void finalize() {
        for (int i = 0; i < SIZE; ++i) {
            if (myBitmaps[i][0] != null) {
                myBitmaps[i][0].recycle();
                myBitmaps[i][0] = null;
            }
            if (myBitmaps[i][1] != null) {
                myBitmaps[i][1].recycle();
                myBitmaps[i][1] = null;
            }
            myIndexes[i] = null;
        }
        System.gc();
    };



    /**
     * 功能描述：获取指定的图片<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-26<br>
     *
     * @param index
     * @return
     */
    Bitmap[] getBitmap(PageEnum.PageIndex index) {
        for (int i = 0; i < SIZE; ++i) {
            if (index == myIndexes[i]) {
                return myBitmaps[i];
            }
        }
        final int iIndex = getInternalIndex(index);
        myIndexes[iIndex] = index;
        if (myBitmaps[iIndex][0] == null && myWidth > 0 && myHeight > 0l) {
            try {
                myBitmaps[iIndex][0] = Bitmap.createBitmap(myWidth, myHeight, Bitmap.Config.RGB_565);
                if (mIsDoubleModel)
                    myBitmaps[iIndex][1] = Bitmap.createBitmap(myWidth, myHeight, Bitmap.Config.RGB_565);
            } catch (OutOfMemoryError e) {
                System.gc();
                System.gc();
                myBitmaps[iIndex][0] = Bitmap.createBitmap(myWidth, myHeight, Bitmap.Config.RGB_565);
                if (mIsDoubleModel)
                    myBitmaps[iIndex][1] = Bitmap.createBitmap(myWidth, myHeight, Bitmap.Config.RGB_565);
            }
        }
        myWidget.drawOnBitmap(myBitmaps[iIndex], index);
        return myBitmaps[iIndex];
    }

    private int getInternalIndex(PageEnum.PageIndex index) {
        for (int i = 0; i < SIZE; ++i) {
            if (myIndexes[i] == null) {
                return i;
            }
        }
        for (int i = 0; i < SIZE; ++i) {
            if (myIndexes[i] != PageEnum.PageIndex.CURRENT) {
                return i;
            }
        }
        throw new RuntimeException("That's impossible");
    }

    void reset() {
        for (int i = 0; i < SIZE; ++i) {
            myIndexes[i] = null;
        }
    }

    void shift(boolean forward) {
        for (int i = 0; i < SIZE; ++i) {
            if (myIndexes[i] == null) {
                continue;
            }
            myIndexes[i] = forward ? myIndexes[i].getPrevious() : myIndexes[i].getNext();
        }
    }

}
