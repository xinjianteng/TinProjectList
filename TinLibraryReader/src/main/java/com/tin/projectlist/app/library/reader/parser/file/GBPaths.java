package com.tin.projectlist.app.library.reader.parser.file;

import com.core.domain.GBApplication;
import com.core.option.GBStringOption;
/**
 * 类名： GBPaths.java#Paths<br>
 * 描述： 获取资源路径工具类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-3<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBPaths {
    /**
     * 功能描述： 获取磁盘存储位置<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-3<br>
     *
     * @return
     */
    protected static String cardDirectory() {
        return GBApplication.Instance().getCardDirectory() + "";
    }
    /**
     * BooksDirectoryOption
     * 功能描述： 获取图书路径信息<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-3<br>
     * @return
     */
    public static GBStringOption getBookPathOption() {
        return new GBStringOption("Files", "BookPath", cardDirectory() + "/book");
    }
    /**
     * FontsDirectoryOption
     * 功能描述： 获取字体存放路径<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-3<br>
     * @return
     */
    public static GBStringOption getFontsPathOption() {
        return new GBStringOption("Files", "FontPath", cardDirectory() + "/.fonts");
    }
    /**
     * WallpapersDirectoryOption
     * 功能描述： 获取壁纸存放路径<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-3<br>
     * @return
     */
    public static GBStringOption getWallPagerPathOption() {
        return new GBStringOption("Files", "WallpaperPath", cardDirectory() + "/wallpager");
    }
    /**
     * 功能描述： 获取缓存文件路径<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-3<br>
     * @return
     */
    public static GBStringOption getCachePathOption(){
        return new GBStringOption("Files", "CachePath", cardDirectory() + "/.cache");
    }
    /**
     * mainBookDirectory
     * 功能描述： <br>
     * 创建者： jack<br>
     * 创建日期：2013-4-3<br>
     * @return
     */
    public static String getBookPath() {
        return getBookPathOption().getValue();
    }
    /**
     * cacheDirectory
     * 功能描述： 获取缓存路径<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-3<br>
     * @return
     */
    public static String cacheDirectory() {
        return getCachePathOption().getValue();
    }

//    public static String networkCacheDirectory() {
//        return cacheDirectory() + "/cache";
//    }

    public static String systemShareDirectory() {
        return "/system/usr/share/GBReader";
    }
}
