package com.tin.projectlist.app.library.reader.model.parser.txt;

import com.cliff.CharSpot;
import com.cliff.UnKnowCharsetException;
import com.geeboo.read.model.parser.txt.GetChap.OnAnalyzeLisener;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 类名： TxtParser.java<br>
 * 描述： 文本解析器接口<br>
 * 创建者： jack<br>
 * 创建日期：2013-9-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class TxtParser {

    public static int IOFAIL = 0; // 读写失败
    public static int EMPTYFILE = 1; // 空白文件
    public static int FILENOFOUND = 2; // 文件未找到
    public static int SUCCESS = 3; // 初始化成功
    public static int UNKOWNERROR = 4; // 未知错误

    protected File mFile;
    protected long mFileLen;

    protected String mEncode = "GBK"; // 解析文件的编码格式

    private int MaxLen = 2097125 * 4; // 1024*1024*2 2MB;
    // private List[] mPie;
    // private int mPieNum;

    /**
     * 读取文本头
     *
     * @param headData 都读取到的文本头信息传递给 headData
     */
    protected abstract void readHead(ByteBuffer headData) throws IOException;
    /**
     * 获取index字节
     *
     * @return
     */
    // protected abstract byte get(int index);
    private int cacheLen = 1024;
    private ByteBuffer mBytes = ByteBuffer.allocate(cacheLen);
    protected abstract int getByteBuffer(long index, ByteBuffer bb) throws IOException;
    // protected List<String> mChapName = new ArrayList<String>();

    // protected String REGX =
    // "^[\\u7b2c][\\u4e00-\\u9fa50-9]{1,10}[\\u7ae0\\u56de\\u7bc7\\u96c6\\u56de][\\S\\s]{1,40}$";

    /**
     * 功能描述： 初始化<br>
     * 创建者： jack<br>
     * 创建日期：2013-9-22<br>
     *
     * @return 初始化状态
     */
    public int init() {
        int flag = UNKOWNERROR;
        if (mFile.isFile() && mFile.exists()) {
            if (mFileLen > 0) {
                try {
                    // mEncode = new CharSpot().detectEncoding(file);

                    // MappedByteBuffer byteBuffer = new RandomAccessFile(mFile,
                    // "r").getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
                    // mFileLen);
                    // 获取编码

                    // byteBuffer.get(h);
                    int len = 200;
                    if (mFileLen < 200)
                        len = (int) mFileLen;
                    // byte[] h = new byte[len];
                    ByteBuffer bb = ByteBuffer.allocate(len);
                    readHead(bb);

                    mEncode = new CharSpot().detectEncoding(bb.array());
                    flag = SUCCESS;
                } catch (IOException ex) {
                    flag = IOFAIL;
                } catch (UnKnowCharsetException e) {
                    e.printStackTrace();
                    flag = SUCCESS;
                }
            } else
                flag = EMPTYFILE;
        } else
            flag = FILENOFOUND;
        return flag;
    }

    /**
     * 功能描述： 初始化<br>
     * 创建者： jack<br>
     * 创建日期：2013-9-22<br>
     *
     * @param filePath 要解析的文件路径
     * @return 初始化状态
     */

    public int init(String filePath) {
        mFile = new File(filePath);
        return init();
    }
    /**
     * 功能描述： 关闭通道<br>
     * 创建者： jack<br>
     * 创建日期：2014-9-12<br>
     *
     * @param
     */
    public abstract void close();

    /**
     * 功能描述：智能断章<br>
     * 创建者： jack<br>
     * 创建日期：2013-9-22<br>
     *
     * @return
     */
    public void getChapters(OnAnalyzeLisener lisener) throws IOException {
        GetChap gc = new GetChap(this, 0, (int) mFileLen, 0, lisener);
        // if (mFileLen < MaxLen)
        gc.run();
        // else
        // gc.start();
    }
    // public List<String> getChapters() {
    //
    // ArrayList<String> chapNameList = new ArrayList<String>();
    // mChapters.clear();
    //
    // // 平均分配线程
    // int TNum = (int) mFileLen / MaxLen + 1;
    // mPieNum = TNum;
    // mPie = new ArrayList[TNum];
    // for (int i = 0; i < TNum; i++) {
    // int start = i * MaxLen;
    // int end = i == TNum - 1 ? (int) mFileLen : MaxLen * (i + 1);
    // GetChap gc = new GetChap(this, start, end, i, this);
    // gc.start();
    // }
    //
    // // 两个线程
    // // mPie = new ArrayList[2];
    // // mPieNum = 2;
    // // int tem = (int) mFileLen / 2;
    // // GetChap gc1 = new GetChap(this, 0, tem, 0, this);
    // // gc1.start();
    // // GetChap gc2 = new GetChap(this, tem, (int) mFileLen, 1, this);
    // // gc2.start();
    //
    // while (mPieNum != 0) {
    // try {
    // Thread.currentThread().sleep(100);
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // for (List list : mPie) {
    // chapNameList.addAll(list);
    // }
    // if (chapNameList.size() > 0) {
    // mChapters.put(chapNameList.get(0), 0);
    // } else {
    // chapNameList.add("首页");
    // mChapters.put("首页", 0);
    // }
    // return chapNameList;
    // }

    public boolean checkChapPara(byte[] para) {
        boolean flag = false;
        if (para.length < 3 || para.length > 80)
            return flag;
        if (mEncode.equals("GBK") || mEncode.equals("GB2312") || mEncode.equals("ASCII")) {
            flag = checkAscii(para);
        } else if (mEncode.equals("UTF-8")) {
            flag = checkUtf8(para);
        } else if (mEncode.equals("Unicode") || mEncode.equals("UTF-16LE") || mEncode.equals("UTF-16")
                || mEncode.equals("UTF-32")) {
            checkUnicode16le(para);
        } else if (mEncode.equals("UTF-16BE")) {
            checkUnicode16be(para);
        }
        return flag;
    }
    // utf8编码目录检查
    private boolean checkUtf8(byte[] para) {
        int index = 0;
        for (int i = 0; i < para.length; i++) {
            if (para[i] == 9) { // Tab
                continue;
            } else if (para[i] == 32) { // 空格
                continue;
            }
            index = i;
            break;
        }
        if (para.length - index >= 10 && para[index] == -25 && para[index + 1] == -84 && para[index + 2] == -84)
            return true;
        return false;
    }
    // unicode little 编码检查
    private boolean checkUnicode16le(byte[] para) {
        int index = 0;
        for (int i = 0; i < para.length; i++) {
            if (para[i] == 9 && i < para.length - 1 && para[i + 1] == 0) { // tab
                i++;
                continue;
            } else if (para[i] == 32 && i < para.length - 1 && para[i + 1] == 0) { // 空格
                i++;
                continue;
            }
            index = i;
            break;
        }
        if (para.length - index >= 7 && para[index] == 44 && para[index + 1] == 123)
            return true;
        return false;
    }
    // unicode big检查
    private boolean checkUnicode16be(byte[] para) {
        int index = 0;
        for (int i = 0; i < para.length; i++) {
            if (para[i] == 0 && i < para.length - 1 && para[i + 1] == 9) { // tab
                i++;
                continue;
            } else if (para[i] == 0 && i < para.length - 1 && para[i + 1] == 32) { // 空格
                i++;
                continue;
            }
            index = i;
            break;
        }
        if (para.length - index >= 7 && para[index] == 123 && para[index + 1] == 44)
            return true;
        return false;
    }
    // GB2312 GBK ASCII 编码段落检查
    private boolean checkAscii(byte[] para) {
        int index = 0;
        for (int i = 0; i < para.length; i++) {
            if (para[i] == -95) { // Tab
                continue;
            } else if (para[i] == 32) { // 半角空格
                continue;
            }
            // else if (para[i] == 0xa1 && i < para.length - 1 && para[i + 1] ==
            // 0xa1) { // 全角空格
            // i++;
            // continue;
            // }
            index = i;
            break;
        }
        if (para.length - index >= 8 && para[index] == -75 && para[index + 1] == -38)
            return true;
        return false;
    }

    /**
     * 功能描述： 从指定的字节下标读取一个段落<br>
     * 创建者： jack<br>
     * 创建日期：2013-9-22<br>
     *
     * @param position 开始位置
     * @return
     */
    public byte[] readParagrah(long position) throws IOException {
        long nStart = position;
        long i = nStart;
        byte b0, b1;
        // 根据编码格式判断换行
        if (mEncode.equals("UNICODE") || mEncode.equals("UTF-16LE")) {
            loop : while (i < mFileLen - 1) {
                int len = getByteBuffer(i, mBytes);
                for (int j = 0; j < len; j++) {
                    b0 = mBytes.get(j++);// get(i++);
                    b1 = mBytes.get(j);// get(i++);
                    if (b0 == 0x0a && b1 == 0x00) {
                        i += j + 1;
                        break loop;
                    }
                }
                i += len;
            }
        } else if (mEncode.equals("UTF-16") || mEncode.equals("UTF-16BE")) {
            // while (i < mFileLen - 1) {
            // b0 = get(i++);
            // b1 = get(i++);
            // if (b0 == 0x00 && b1 == 0x0a) {
            // break;
            // }
            // }
            loop : while (i < mFileLen - 1) {
                int len = getByteBuffer(i, mBytes);
                for (int j = 0; j < len; j++) {
                    b0 = mBytes.get(j++);// get(i++);
                    b1 = mBytes.get(j);// get(i++);
                    if (b0 == 0x00 && b1 == 0x0a) {
                        i += j + 1;
                        break loop;
                    }
                }
                i += len;
            }
        } else {
            // while (i < mFileLen) {
            // b0 = get(i++);
            // if (b0 == 0x0a) {
            // break;
            // }
            // }
            loop : while (i < mFileLen - 1) {
                int len = getByteBuffer(i, mBytes);
                for (int j = 0; j < len; j++) {
                    b0 = mBytes.get(j);// get(i++);
                    if (b0 == 0x0a) {
                        i += j + 1;
                        break loop;
                    }
                }
                i += len;
            }
        }
        // int nParaSize = i - nStart;
        // byte[] buf = new byte[nParaSize];
        // for (i = 0; i < nParaSize; i++) {
        // buf[i] = get(position + i);
        // }
        ByteBuffer buffer = ByteBuffer.allocate((int) (i - nStart));
        getByteBuffer(nStart, buffer);
        return buffer.array();
    }

}
