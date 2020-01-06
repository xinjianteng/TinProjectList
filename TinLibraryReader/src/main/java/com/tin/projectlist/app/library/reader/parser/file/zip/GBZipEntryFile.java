package com.tin.projectlist.app.library.reader.parser.file.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.core.zip.LocalFileHeader;
import com.core.zip.ZipFile;
import com.geeboo.Geeboo;
import com.geeboo.book.GBBook;
import com.geeboo.book.GBBookFactory;
import com.tin.projectlist.app.library.base.utils.LogUtils;
import com.tin.projectlist.app.library.reader.parser.common.util.LicenseMgr;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.file.zip.GBArchiveEntryFile;

/**
 * 类名： GBZipEntryFile.java#ZLZipEntryFile<br>
 * 描述： zip类型压缩文件对象封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-10<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBZipEntryFile extends GBArchiveEntryFile {
    /**
     * 功能描述： 获取压缩文件中所有文件的列表<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-10<br>
     *
     * @param archive
     * @return
     */
    public static List<GBFile> archiveEntries(GBFile archive) {
        try {
            final ZipFile zf = GBZipEntryFile.getZipFile(archive);
            final Collection<LocalFileHeader> headers = zf.headers();
            if (!headers.isEmpty()) {
                ArrayList<GBFile> entries = new ArrayList<GBFile>(headers.size());
                for (LocalFileHeader h : headers) {
                    entries.add(new GBZipEntryFile(archive, h.FileName));
                }
                return entries;
            }
        } catch (IOException e) {
        }
        return Collections.emptyList();
    }

    /**
     * 压缩文件对象缓存集合
     */
    private static HashMap<GBFile, ZipFile> ourZipFileMap = new HashMap<GBFile, ZipFile>();

    /**
     * 功能描述： 根据file对象获取读取到内存中zip压缩文件对象<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-10<br>
     *
     * @param file 指定的zip文件对像
     * @return
     * @throws IOException
     */
    private static ZipFile getZipFile(final GBFile file) throws IOException {
        synchronized (ourZipFileMap) {
            ZipFile zf = file.isCached() ? ourZipFileMap.get(file) : null;
            if (zf == null) {
                zf = new ZipFile(new ZipFile.InputStreamHolder() {
                    public InputStream getInputStream() throws IOException {
                        return file.getInputStream();
                    }
                });
                if (file.isCached()) {
                    ourZipFileMap.put(file, zf);
                }
            }
            return zf;
        }
    }

    public static void removeFromCache(GBFile file) {
        ourZipFileMap.remove(file);
    }

    public GBZipEntryFile(GBFile parent, String name) {
        super(parent, name);

    }

    static HashMap<String, GBBook> mGBBookMapCache = new HashMap<String, GBBook>();

    /**
     *
     * 功能描述： 获取gbook管理对象<br>
     * 创建者： yangn<br>
     * 创建日期：2014-4-22<br>
     *
     * @param
     */
    GBBook getGBBook() {
        GBBook geebook = mGBBookMapCache.get(myParent.getPath());
        if (null == geebook) {
            geebook = GBBookFactory.openGBBook(LicenseMgr.getGeebooSecretKey(), myParent.getPath());
            mGBBookMapCache.put(myParent.getPath(), geebook);
        }

        return geebook;
    }

    /**
     *
     * 功能描述：关闭geebook管理对象并移除缓存引用 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-4-22<br>
     *
     * @param
     */
    public static void removeGeebookCache(GBFile file) {
        if(file == null) return;
        LogUtils.e("ReaderActivity", "removeGeebookCache close path=" + file.getPath());
        GBBook geebook = mGBBookMapCache.get(file.getPath());
        if (null != geebook) {
            geebook.close();
            geebook = null;
            mGBBookMapCache.remove(file.getPath());
        }
    }

    @Override
    public boolean exists() {
        try {
            return myParent.exists() && getZipFile(myParent).entryExists(myName);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long size() {
        try {
            return getZipFile(myParent).getEntrySize(myName);
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {

        // L.e("GBZipEntryFile",
        // "LicenseMgr.isFreeLicense()==" + LicenseMgr.isFreeLicense());

        if (LicenseMgr.isFreeLicense()) {// 原来解压方式
            ZipFile zipFile = getZipFile(myParent);
            if (null == zipFile) {

                throw new IOException("已加密epub、txt文件使用非解密方式打开异常");

            }
            return zipFile.getInputStream(myName);

        } else {
            try {

                GBBook book = getGBBook();
                // L.e("GBZipEntryFile", "file myname:" + myName + ":" + book);
                if (null == book) {
                    throw new IOException(
                            "非加密epub、txt文件使用解密方式打开异常 GBBookFactory.openGBBook return null from libGeeboo.so");
                }
                InputStream in = book.read(myName);

                return in;
            } catch (Exception exception) {
                throw new IOException(exception.getMessage());
            }

            // geeboo 解密解压方式

        }

    }

}
