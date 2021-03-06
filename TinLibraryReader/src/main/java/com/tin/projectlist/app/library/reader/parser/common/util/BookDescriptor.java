package com.tin.projectlist.app.library.reader.parser.common.util;


import com.tin.projectlist.app.library.reader.parser.common.CopyVersionInfo;

import java.io.Serializable;

/**
 * 描述：图书描述类，阅读器根据图书描述信息加载图书  版权页<br>
 * 创建者： T_xin<br>
 * 创建日期：2020-01-02<br>
 */
public final class BookDescriptor implements Serializable {

    /**
     * 类名： .java<br>
     * 描述：标识文件后缀 <br>
     * 创建者： yangn<br>
     * 创建日期：2014-1-13<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public static interface Suffix {
        byte TXT = 0;
        byte EPUB = 1;
        byte PDF = 2;
    }

    private static final long serialVersionUID = 89757L;

    /**
     * 图书文件路径
     */
    public final String FilePath;
    /**
     * 图书名称
     */
    public final String Title;

    /**
     * 阅读范围
     */
    public final int ReadRange;

    /**
     * 文件后缀标识
     */
    public byte RealSuffer;

    /**
     * @param filePath  图书文件路径
     * @param title     阅读器界面图书显示标题
     * @param readRange 阅读范围 数值图书百分比
     */
    public BookDescriptor(String filePath, String title, int readRange) {
        FilePath = filePath;
        Title = title;
        ReadRange = readRange;
    }


    // 添加版权支持
    private CopyVersionInfo mCopyVersionInfo = null;

    public CopyVersionInfo getCopyVersionInfo() {
        return mCopyVersionInfo;
    }

    public void setCopyVersionInfo(CopyVersionInfo mCopyVersionInfo) {
        this.mCopyVersionInfo = mCopyVersionInfo;
    }

}