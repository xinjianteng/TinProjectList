package com.tin.projectlist.app.library.reader.parser.file.image;

/**
 * 类名： GBImageManager.java#ZLImageManager<br>
 * 描述： 图片管理类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-11<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBImageManager {
    private static GBImageManager ourInstance;

    //单一模式
    public static GBImageManager Instance() {
        return ourInstance;
    }

    protected GBImageManager() {
        ourInstance = this;
    }

    /**
     * 功能描述： 获取图片数据<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-11<br>
     *
     * @param image 图片对象
     * @return
     */
    public abstract GBImageData getImageData(GBImage image);

    protected abstract void startImageLoading(GBLoadableImage image, Runnable postLoadingRunnable);

}
