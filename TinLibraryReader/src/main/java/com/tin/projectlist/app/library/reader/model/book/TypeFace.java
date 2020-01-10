package com.tin.projectlist.app.library.reader.model.book;

public class TypeFace {

    public String fontName;
    public String downloadUrl;
    //    public final int PacketSize;
    public boolean isExist;
    public TypeFace(String fontName, String downloadUrl, boolean isExist) {
        this.fontName = fontName;
        this.downloadUrl = downloadUrl;
        this.isExist = isExist;
    }
//    public TypeFace(String name,String url) {
//        Name = name;
//        URL = url;
//        PacketSize = 0;
//    }
}
