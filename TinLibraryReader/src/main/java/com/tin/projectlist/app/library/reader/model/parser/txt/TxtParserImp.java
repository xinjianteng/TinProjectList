package com.tin.projectlist.app.library.reader.model.parser.txt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 类名： TxtParserImp.java<br>
 * 描述： <br>
 * 创建者： jack<br>
 * 创建日期：2013-9-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class TxtParserImp extends TxtParser {

    // protected MappedByteBuffer mByteBuffer;
    protected FileChannel mChannel;

    public TxtParserImp(File file) throws FileNotFoundException, IOException {
        super();
        mFile = file;
        mFileLen = file.length();
        mChannel = new FileInputStream(file).getChannel();
        // mByteBuffer = new RandomAccessFile(mFile,
        // "r").getChannel().map(FileChannel.MapMode.READ_ONLY, 0, mFileLen);

    };

    @Override
    protected void readHead(ByteBuffer head) throws IOException {
        mChannel.read(head);
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
