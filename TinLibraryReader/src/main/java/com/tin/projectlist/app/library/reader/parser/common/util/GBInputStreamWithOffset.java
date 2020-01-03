package com.tin.projectlist.app.library.reader.parser.common.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * 带下标的流包装类
 *
 * 把下标记录下来，可以知道当前正在输入流的第几个字节位置
 *
 * @author fuchen
 * @date 2013-4-10
 */
public class GBInputStreamWithOffset extends InputStream {
    private final InputStream myDecoratedStream;
    private int myOffset = 0;

    public GBInputStreamWithOffset(InputStream stream) {
        myDecoratedStream = stream;
    }

    @Override
    public int available() throws IOException {
        return myDecoratedStream.available();
    }

    @Override
    public long skip(long n) throws IOException {
        long shift = myDecoratedStream.skip(n);
        if (shift > 0) {
            myOffset += (int)shift;
        }
        while ((shift < n) && (read() != -1)) {
            ++shift;
        }
        return shift;
    }

    @Override
    public int read() throws IOException {
        int result = myDecoratedStream.read();
        if (result != -1) {
            ++myOffset;
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        myOffset = 0;
        myDecoratedStream.close();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        final int shift = myDecoratedStream.read(b, off, len);
        if (shift > 0) {
            myOffset += shift;
        }
        return shift;
    }

    @Override
    public void reset() throws IOException {
        myOffset = 0;
        myDecoratedStream.reset();
    }

    /**
     * 获取下标
     * @return
     * @author fuchen
     * @date 2013-4-10
     */
    public int offset() {
        return myOffset;
    }
}
