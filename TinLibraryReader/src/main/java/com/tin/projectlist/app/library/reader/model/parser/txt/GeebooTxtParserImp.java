package com.tin.projectlist.app.library.reader.model.parser.txt;

import com.core.common.util.LicenseMgr;
import com.geeboo.book.GBBook;
import com.geeboo.book.GBBookChannel;
import com.geeboo.book.GBBookFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 类名： TxtParserImp.java<br>
 * 描述： <br>
 * 创建者： jack<br>
 * 创建日期：2013-9-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GeebooTxtParserImp extends TxtParser {

    // protected GBBookByteBuffer mByteBuffer;
    protected GBBookChannel mChannel;

    public GeebooTxtParserImp(File file) {
        super();
        mFile = file;
        GBBook book = GBBookFactory.openGBBook(LicenseMgr.getGeebooSecretKey(), file.getAbsolutePath());
        mChannel = book.getFileChannel(book.read());
        try {
            mFileLen = mChannel.size();
        } catch (IOException e) {
            e.printStackTrace();
            mFileLen = -1;
        }
        // mByteBuffer = book.getByteBuffer(book.read());
        // mFileLen = mByteBuffer.length();
    };
    /**
     * 获取图书目录缓存
     */
    protected void getCache() {

    }

    /**
     * 判断缓存是否存在
     *
     * @return
     */
    protected boolean isCacheExists() {
        return true;
    }

    @Override
    protected void readHead(ByteBuffer headData) throws IOException {
        mChannel.read(headData);
    }

    @Override
    protected int getByteBuffer(long index, ByteBuffer bb) throws IOException {
        bb.rewind();
        mChannel.position(index);
        mChannel.read(bb);
        bb.flip();
        bb.clear();
        if (mFileLen - index > bb.capacity()) {
            return bb.capacity();
        } else {
            int len = (int) (mFileLen - index);
            return len;
        }
    }

    @Override
    public void close() {
        if (mChannel != null) {
            try {
                mChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
