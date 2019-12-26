package com.tin.projectlist.app.model.oldBook.oldBook.constant;

import com.tin.projectlist.app.library.base.utils.FileUtils;
import com.tin.projectlist.app.model.oldBook.common.MyApplication;

public class PathConstant {

    public static String PATH_DATA = FileUtils.createRootPath(MyApplication.getContext());
    public static String PATH_EPUB = PATH_DATA + "/epub";
    public static final String SUFFIX_TXT = ".txt";
    public static final String SUFFIX_PDF = ".pdf";
    public static final String SUFFIX_EPUB = ".epub";
    public static final String SUFFIX_ZIP = ".zip";
    public static final String SUFFIX_CHM = ".chm";


}
