package com.tin.projectlist.app.library.reader.model;


import com.tin.projectlist.app.library.reader.parser.text.linbreak.LineBreakerInterface;

public class MyLineBreaker implements LineBreakerInterface {
    static {
        System.loadLibrary("MergeLine");
        initInternal();
    }

    private static native void initInternal();
    private static native void setLineBreaksForCharArrayInternal(char[] data, int offset, int length, String lang, byte[] breaks);
    private static native void setLineBreaksForStringInternal(String data, String lang, byte[] breaks);

    @Override
    public void init() {
        // TODO Auto-generated method stub
        initInternal();
    }

    @Override
    public void setLineBreaksForCharArray(char[] data, int offset, int length, String lang, byte[] breaks) {
        // TODO Auto-generated method stub
        setLineBreaksForCharArrayInternal(data, offset, length, lang, breaks);
    }

    @Override
    public void setLineBreaksForString(String data, String lang, byte[] breaks) {
        // TODO Auto-generated method stub
        setLineBreaksForStringInternal(data, lang, breaks);
    }

}
