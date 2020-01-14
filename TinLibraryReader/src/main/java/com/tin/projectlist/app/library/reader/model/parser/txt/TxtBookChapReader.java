package com.tin.projectlist.app.library.reader.model.parser.txt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.SparseArray;

import com.tin.projectlist.app.library.reader.model.bookmodel.BookModel;
import com.tin.projectlist.app.library.reader.model.bookmodel.BookReader;
import com.tin.projectlist.app.library.reader.model.bookmodel.BookReadingException;
import com.tin.projectlist.app.library.reader.model.parser.oeb.OEBBookReader;
import com.tin.projectlist.app.library.reader.model.parser.xhtml.XHTMLReader;
import com.tin.projectlist.app.library.reader.parser.common.util.LicenseMgr;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextModel;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextFixedPosition;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextPosition;

//import com.core.common.util.IFunction;
//import com.core.common.util.LicenseMgr;
//import com.core.file.GBFile;
//import com.core.log.L;
//import com.core.text.model.GBTextModel;
//import com.core.text.model.GBTextWritableModel;
//import com.core.text.widget.GBTextFixedPosition;
//import com.core.text.widget.GBTextPosition;
//import com.geeboo.read.model.bookmodel.BookModel;
//import com.geeboo.read.model.bookmodel.BookReader;
//import com.geeboo.read.model.bookmodel.BookReadingException;
//import com.geeboo.read.model.bookmodel.GBTextKind;
//import com.geeboo.read.model.bookmodel.TOCTree;
//import com.geeboo.read.model.parser.oeb.OEBBookReader;
//import com.geeboo.read.model.parser.txt.GetChap.OnAnalyzeLisener;
//import com.geeboo.read.model.parser.xhtml.XHTMLReader;

/**
 * 类名： TxtBookChapReader.java<br>
 * 描述： txt图书智能断章解析<br>
 * 创建者： jack<br>
 * 创建日期：2013-5-6<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class TxtBookChapReader extends OEBBookReader implements GetChap.OnAnalyzeLisener {
    final String TAG = "TxtBookChapReader";

    private TxtParser mParser;
    private GBFile mFile;
    private int mPartLen = 1024 * 20;// 20kb;
    protected SparseArray<Long> mParts; // 加载片段
    protected Map<String, String> mChapters = new HashMap<String, String>(); // 断章信息

    public TxtBookChapReader(BookModel model) {
        super(null);
        myModelReader = new BookReader(model);
    }

    int txtParsetFactory(File file) throws FileNotFoundException, IOException {
        if (LicenseMgr.isFreeLicense()/* MiscUtil.isTxtMadeInGeeboo(file) */) {
            mParser = new TxtParserImp(file);
        } else {
            mParser = new GeebooTxtParserImp(file);
        }
        return mParser.init(file.getPath());
    }

    /**
     * 功能描述：解析文件<br>
     * 创建者： jack<br>
     * 创建日期：2013-9-24<br>
     *
     * @param file 要解析的文件
     * @param lastPosition 最后阅读位置
     * @throws BookReadingException
     */
    @Override
    public void readBook(GBFile file, GBTextPosition lastPosition) throws BookReadingException {
        mFile = file;
        try {
            myModelReader.setMainTextModel();
            // 创建txt解析对象
            int result = txtParsetFactory(new File(file.getPath()));
            if (result != TxtParser.SUCCESS) {
                throw new BookReadingException("errorReadingFile", file);
            }
            // 初始化片段信息
            initParts();
            isCacheExists = isCacheExists(false);// && !isAnalyzeChaptering;
            int chpFileSize = GBTextModel.cacheChapNumber;
//            mChpReadStates = new byte[chpFileSize];

            if (mParts.size() > chpFileSize) {
                myModelReader.Model.BookTextModel.extendTextSizes(mParts.size(), isCacheExists);
                myModelReader.Model.getInternalHyperLinks().resetChpSize(mParts.size());
            } else {
                myModelReader.Model.BookTextModel.setChapterSize(mParts.size());
                myModelReader.Model.getInternalHyperLinks().resetChpSize(mParts.size());
            }
            // 获取打开位置信息
            int chpFileIndex = 0;
            if (null != lastPosition) {
                chpFileIndex = lastPosition.getChpFileIndex();
                if (chpFileIndex < 0 || chpFileIndex >= mParts.size()) {
                    chpFileIndex = 0;
                    lastPosition = new GBTextFixedPosition(0, 0, 0, 0);
                }
            }
            // 预加载上一章，索引偏移
            if (chpFileIndex != 0) {
                --chpFileIndex;
            }

            // 设置章节索引映射
            myModelReader.Model.BookTextModel.getChpFileNumMapping().clear();

            int realSize = mParts.size();
            int useSize = GBTextModel.cacheChapNumber;
            if (realSize < useSize) {
                useSize = realSize;
            }

            int offset = chpFileIndex + useSize - realSize;
            if (offset > 0) {
                chpFileIndex -= offset;
            }

            int size = chpFileIndex + useSize;
            for (int i = chpFileIndex; i < size; i++) {

                if (isStopRead) {
                    return;
                }

//                int chpFileIndexRealPosition = prepareLoadChp(i);

                // 清除上次解析缓存
                // if (myModelReader.Model.BookTextModel.isCacheItemExists(i)) {
                myModelReader.Model.BookTextModel.clearCache(i, true);
                // }

                // if (isCacheExists) {
                // realPositionMapping[chapFileIndexRealPosition] =
                // (short)

                ((GBTextWritableModel) myModelReader.Model.BookTextModel).settingSumSize(false);
                // }

                /*
                 * else { // 若第一次加载chapFileIndexRealPosition是章节对应真实的值 需计算下标
                 * realPositionMapping[chapFileIndexRealPosition % 3] = (short)
                 * i; }
                 */

                readChpFile(i, null);
//                L.e(TAG, "chp file index" + i + " mapping" + chpFileIndexRealPosition + " read over");
            }
            myModelReader.Model.BookTextModel.buildLastDataCache(size - 1);

            // if (isCacheExists) {
            // myModelReader.Model.BookTextModel.loadBookOver();
            // if (null != mProgressHander) {
            // mProgressHander.callback(100);
            // }
            // initChapter();
            // return;
            // } else {
            // 临时缓存不存在情况
            // if (!myModelReader.Model.BookTextModel.isCacheExists(true)) {
            // resetProgressInfo();
            // }
            // 分析页码
            new Thread(getPageNumRunnable).start();
            // }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BookReadingException("errorReadingFile", file);
        }

    }

    @Override
    protected boolean isCacheExists(boolean isTemp) {
        Object obj = myModelReader.Model.getTextModel().getCacheItem("myHtmlFileNames_new");
        Object chaps = myModelReader.Model.getTextModel().getCacheItem("chapters_new");
        return obj != null && chaps != null && super.isCacheExists(isTemp);
    }

    @Override
    protected boolean simpleReadChpFile(XHTMLReader reader, int chpFileIndex, String name, int useIndex)
            throws BookReadingException {
        synchronized (this) {

            myModelReader.settingGenerPageMode();
            myModelReader.myCurrentTextModel.setWrithChpFiliNum(useIndex);
            long start = mParts.get(chpFileIndex);// Long.parseLong(mChapters.get(myHtmlFileNames.get(chpFileIndex))
            // + "");
            long end = chpFileIndex + 1 >= mParts.size() ? mParser.mFileLen - 1 : mParts.get(chpFileIndex + 1);
            try {
                readChapter(start, end);
            } catch (IOException e) {
                throw new BookReadingException(e, mFile);
            }
            myModelReader.insertEndOfSectionParagraph();
            return true;

        }
    }

    @Override
    protected boolean readChpFile(int chpFileIndex, String name) throws BookReadingException {
        synchronized (this) {

            execingChpFileIndex = chpFileIndex;
            myModelReader.setMainTextModel();

            int chpFileIndexRealPosition = chpFileIndex % GBTextModel.cacheChapNumber;
            realPositionMapping[chpFileIndexRealPosition] = chpFileIndex;
            myModelReader.Model.BookTextModel.getChpFileNumMapping().put(chpFileIndex, chpFileIndexRealPosition);


            myModelReader.myCurrentTextModel.setWrithChpFiliNum(chpFileIndex);

            myModelReader.Model.BookTextModel.clearCache(chpFileIndex, true);

            long start = mParts.get(chpFileIndex);// Long.parseLong(mChapters.get(myHtmlFileNames.get(chpFileIndex))
            // + "");
            long end = chpFileIndex + 1 >= mParts.size() ? mParser.mFileLen - 1 : mParts.get(chpFileIndex + 1);
            try {
                readChapter(start, end);
            } catch (IOException e) {
                throw new BookReadingException(e, mFile);
            }
            myModelReader.insertEndOfSectionParagraph();
            // L.e(TAG, "read txt file " + chpFileIndex + " over:" +
            // myModelReader.Model.BookTextModel.getParagraphsNumber());
            myModelReader.Model.BookTextModel.nextChp();

            execingChpFileIndex = -1;
//            mChpReadStates[chpFileIndex % GBTextModel.cacheChapNumber] = READ;
            return true;

        }
    }

    /*
     * txt初始化片段
     */
    private void initParts() {
        // Object obj =
        // myModelReader.Model.getTextModel().getCacheItem("mParts");
        // if (obj == null) {
        mParts = new SparseArray<Long>();
        // 初始化分解片段
        long len = 0l;
        int i = 0;
        while (i < mParser.mFileLen) {
            mParts.put(i, len);
            i++;
            // 获取下一个片段节点
            if (mParser.mFileLen - len < mPartLen)
                break;
            len += mPartLen;
            try {
                len += mParser.readParagrah(len).length;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        // myModelReader.Model.getTextModel().addCacheItem("mParts", mParts);
        // } else {
        // mParts = (SparseArray<Long>) obj;
        // }
    }

    /**
     * 功能描述：智能断章<br>
     * 创建者： jack<br>
     * 创建日期：2013-9-23<br>
     *
     * @throws IOException
     */
    protected void initChapter() throws IOException {
        if (!myHtmlFileNames.isEmpty()) {
            myHtmlFileNames.clear();
        }

        long time = System.currentTimeMillis();
        Object obj = myModelReader.Model.getTextModel().getCacheItem("myHtmlFileNames_new");
        Object chaps = myModelReader.Model.getTextModel().getCacheItem("chapters_new");
        if (obj == null || chaps == null) {
            TxtParser parser = null;
            if (LicenseMgr.isFreeLicense()/* MiscUtil.isTxtMadeInGeeboo(file) */) {
                parser = new TxtParserImp(new File(mFile.getPath()));
            } else {
                parser = new GeebooTxtParserImp(new File(mFile.getPath()));
            }
            parser.init(mFile.getPath());
            parser.getChapters(this);
        } else {
            myHtmlFileNames.addAll((ArrayList<String>) obj);
            mChapters.putAll((Map<String, String>) chaps);
            // 装载目录
            try {
                TOCTree curentTree = myModelReader.Model.TOCTree;
                for (int i = 0; i < myHtmlFileNames.size(); i++) {
                    TOCTree temp = new TOCTree(curentTree);
                    temp.setText(myHtmlFileNames.get(i));
                    String[] pi = mChapters.get(myHtmlFileNames.get(i)).split(":");
                    temp.setReference(myModelReader.Model.BookTextModel, Integer.valueOf(pi[0]), Integer.valueOf(pi[1]));
                }
            } catch (Exception ex) {

            }
        }

        L.e(TAG, "time :" + (System.currentTimeMillis() - time));

    }

    @Override
    protected XHTMLReader getReader() {
        return null;
    }

    @Override
    protected int getChapterCount() {
        return mParts.size();
    }

    @Override
    protected String getChpaterPath(int chapterIndex) {
        return "";
    }

    /**
     * 功能描述：读取指定章节 <br>
     * 创建者： jack<br>
     * 创建日期：2013-9-24<br>
     *
     * @param from 章节开始位置（字节位置）
     * @param end 章节结束位置
     * @throws IOException
     */
    void readChapter(long from, long end) throws IOException {
        if (from >= end)
            return;
        long i = from;
        while (i < end) {
            byte[] bt = mParser.readParagrah(i);
            try {
                String str = new String(bt, mParser.mEncode);
                addData(str);
                i += bt.length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 功能描述： 添加一个段落到缓存<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-7<br>
     *
     * @param sb
     */
    private void addData(String para) {
        boolean isCatalog = GetChap.mTcrem.isBodyCatalogElement(para);
        myModelReader.beginParagraph();
        myModelReader.addControl(isCatalog ? GBTextKind.H5 : GBTextKind.CODE, true);
        myModelReader.addData(para.toCharArray());
        myModelReader.addControl(isCatalog ? GBTextKind.H5 : GBTextKind.CODE, false);
        myModelReader.endParagraph();
        if (isCatalog) {
            myModelReader.pushKind(GBTextKind.HR);
            myModelReader.beginParagraph();
            myModelReader.endParagraph();
            myModelReader.popKind();
        }
    }
    @Override
    public void readBookByChpFileIndex(int chpFileIndex, IFunction<Integer> function) {
        super.readBookByChpFileIndex(chpFileIndex, function);
    }

    // 是否通知过正在分析页码
    private boolean isnotifyAnalyze = false;
    private int mChapterParaCount = 0; // 章节段落计数器
    private int mChapterNum = 0; // 章节计数器

    @Override
    public void onStart() {
        isnotifyAnalyze = false;
    }

    @Override
    public void onPragrahProgress(long readLen, int paragrahLen) {
        mChapterParaCount++;
        if (mParts.get(mChapterNum + 1) != null && readLen == mParts.get(mChapterNum + 1)) {
            mChapterNum++;
            mChapterParaCount = 0;
        }

    }

    @Override
    public void onAnalyzeProgress(int progress, String chapterName, long index) {
        myHtmlFileNames.add(chapterName);
        L.i(chapterName + ":" + index);
        mChapters.put(chapterName, mChapterNum + ":" + mChapterParaCount);

        if (mProgressHander != null) {
            if (!isnotifyAnalyze) {
                mProgressHander.callback(INITCHAPTER);
                isnotifyAnalyze = true;
            }
            mProgressHander.callback(progress);
        }
    }

    @Override
    public void onFinish() {
        myModelReader.Model.getTextModel().addCacheItem("myHtmlFileNames_new", myHtmlFileNames);
        myModelReader.Model.getTextModel().addCacheItem("chapters_new", mChapters);

        // 装载目录
        try {
            TOCTree curentTree = myModelReader.Model.TOCTree;
            for (int i = 0; i < myHtmlFileNames.size(); i++) {
                TOCTree temp = new TOCTree(curentTree);
                temp.setText(myHtmlFileNames.get(i));
                String[] pi = mChapters.get(myHtmlFileNames.get(i)).split(":");
                temp.setReference(myModelReader.Model.BookTextModel, Integer.valueOf(pi[0]), Integer.valueOf(pi[1]));
            }
        } catch (Exception ex) {

        }
        // updateChapterAndExtendCache();
        L.e(TAG, "on analyze chapter finish" + myHtmlFileNames.size());
    }

    @Override
    public void onError() {
        onFinish();
    }

}
