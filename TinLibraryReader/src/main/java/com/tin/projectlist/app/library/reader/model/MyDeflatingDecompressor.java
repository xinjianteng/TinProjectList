package com.tin.projectlist.app.library.reader.model;


import com.tin.projectlist.app.library.reader.parser.zip.DeflatingDecompressorInterface;

public class MyDeflatingDecompressor implements DeflatingDecompressorInterface {

    static {
        System.loadLibrary("Ziplib");
    }

    @Override
    public int startInflating() {
        // TODO Auto-generated method stub
        return startInflatingInternal();
    }

    @Override
    public void endInflating(int inflatorId) {
        // TODO Auto-generated method stub
        endInflatingInternal(inflatorId);
    }

    @Override
    public long inflate(int inflatorId, byte[] in, int inOffset, int inLength, byte[] out) {
        // TODO Auto-generated method stub
        return inflateInternal(inflatorId, in, inOffset, inLength, out);
    }

    private native int startInflatingInternal();
    private native void endInflatingInternal(int inflatorId);
    private native long inflateInternal(int inflatorId, byte[] in, int inOffset, int inLength, byte[] out);
}
