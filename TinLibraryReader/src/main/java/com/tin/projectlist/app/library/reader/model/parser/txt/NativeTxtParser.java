package com.tin.projectlist.app.library.reader.model.parser.txt;

import java.util.List;

public class NativeTxtParser {

    static{
        System.loadLibrary("NativeTxtParser");
    }

    public native int init(String filePath);
    public native byte[] readParagrah(int position);
    public native List<String> getChapters();
    public native int[] getChapIndex(String filePath);
}
