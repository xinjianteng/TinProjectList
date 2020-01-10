package com.tin.projectlist.app.library.reader.model.parser.oeb;
import android.graphics.Bitmap;

import com.core.common.CopyVersionInfo;
import com.core.common.XMLNamespaces;
import com.core.common.util.IFunction;
import com.core.common.util.MiscUtil;
import com.core.file.GBFile;
import com.core.file.image.GBFileImage;
import com.core.file.image.GBImageData;
import com.core.file.image.GBImageManager;
import com.core.log.L;
import com.core.object.GBSize;
import com.core.text.model.GBTextModel;
import com.core.text.model.GBTextWritableModel;
import com.core.text.model.impl.GBTextWritablePlainModel;
import com.core.text.widget.GBTextFixedPosition;
import com.core.text.widget.GBTextPosition;
import com.core.view.PageEnum.ImgFitType;
import com.core.xml.GBStringMap;
import com.core.xml.GBXMLReaderAdapter;
import com.geeboo.read.model.bookmodel.BookModel;
import com.geeboo.read.model.bookmodel.BookReader;
import com.geeboo.read.model.bookmodel.BookReadingException;
import com.geeboo.read.model.bookmodel.GBTextKind;
import com.geeboo.read.model.bookmodel.TagSpoor;
import com.geeboo.read.model.parser.IBookChpReader;
import com.geeboo.read.model.parser.css.XHTMLStyleReader;
import com.geeboo.read.model.parser.xhtml.XHTMLReader;
import com.geeboo.read.view.img.GBAndroidImageData;
import com.geeboo.utils.FileUtils;
import com.geeboo.utils.GeeBookLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * epub图书解析器
 */
public class OEBBookReader extends GBXMLReaderAdapter implements XMLNamespaces, IBookChpReader {
    final String TAG = "OEBBookReader";
    /**
     * 标识页码计算完毕
     */
    public static final int CALL_BOOK_OVER = -1, NULL = -1, INITCHAPTER = -2, INITPAGES = -3;
    private static final char[] Dots = new char[]{'.', '.', '.'};

    protected BookReader myModelReader;
    private final HashMap<String, String> myIdToHref = new HashMap<String, String>();
    protected final ArrayList<String> myHtmlFileNames = new ArrayList<String>();
    private final ArrayList<Reference> myTourTOC = new ArrayList<Reference>();
    private final ArrayList<Reference> myGuideTOC = new ArrayList<Reference>();

    private String myOPFSchemePrefix;
    private String myFilePrefix;
    private String myNCXTOCFileName;
    private String myCoverFileName;

    public OEBBookReader(BookModel model) {
        if (null == model) {
            return;
        }
        myModelReader = new BookReader(model);
        model.setLabelResolver(new BookModel.LabelResolver() {
            public List<String> getCandidates(String id) {
                final int index = id.indexOf("#");
                return index > 0 ? Collections.<String> singletonList(id.substring(0, index)) : Collections
                        .<String> emptyList();
            }
        });
    }

    // epub章节文件对应顺序号《文件名：线性阅读顺序》
    private HashMap<String, String> myFileNumbers = new HashMap<String, String>();
    private HashMap<String, Integer> myTOCLabels = new HashMap<String, Integer>();
    protected volatile int[] realPositionMapping = new int[]{-1, -1, -1};// 已加载章节映射

    /**
     * 判断是否已保存缓存文件
     *
     * @return
     */
    protected boolean isCacheExists(boolean isTemp) {
        return myModelReader.Model.BookTextModel.isCacheExists(false);
    }
    // 当前正在页码分析的章节
    protected volatile int currentExecCacheChpFileIndex = NULL;
    // 当前正在解析的章节
    protected volatile int execingChpFileIndex = NULL;

    protected volatile boolean isStopRead = false;

    /**
     * 停止阅读
     *
     * @param stopRead
     */
    public void stopRead(boolean stopRead) {
        isStopRead = stopRead;
    }

    /**
     * 是否已经加载chpFileIndex章
     *
     * @param chpFileIndex
     * @return已加载返回true 未加载或正在加载返回false;
     */
    public boolean isLoadChp(int chpFileIndex) {
        synchronized (this) {
            int chapFileIndexRealPosition = chpFileIndex % GBTextModel.cacheChapNumber;
            boolean flag = execingChpFileIndex != chpFileIndex
                    && realPositionMapping[chapFileIndexRealPosition] == chpFileIndex;
//            L.e("OEBReader","is load chap:"+chpFileIndex+":"+flag);
            return flag;
        }
    }
    // 图书解析进度监听
    protected IFunction<Integer> mProgressHander = null;
    public void setmProgressHander(IFunction<Integer> mProgressHander) {
        this.mProgressHander = mProgressHander;
    }

    private Map<Integer, IFunction<Integer>> mFunctions = new HashMap<Integer, IFunction<Integer>>();
    /**
     * 读取图书指定章节
     */
    public void readBookByChpFileIndex(int chpFileIndex, IFunction<Integer> function) {
        // 判断如果章节索引越界直接返回
        if (getChapterCount() <= chpFileIndex || chpFileIndex < 0) {
            if (null != function) {
                function.callback(NULL);
            }
            return;
        }
        isStopRead = false;

        // 判断章节是否已加载
        if (isLoad(chpFileIndex)) {
            // case已加载成功
            if (null != function) {
                function.callback(chpFileIndex);
            }
            return;
        }
        if (isChpLoading(chpFileIndex)) {
            if (function != null) {
                synchronized (mFunctions) {
                    mFunctions.put(chpFileIndex, function);
                }
            }
            return;
        } else {
            synchronized (mFunctions) {
                mFunctions.put(chpFileIndex, function);
            }
            new ReadChapThread(chpFileIndex).start();
        }

    }
    // 章节读取线程
    private class ReadChapThread extends Thread {
        int chapIndex;
        public ReadChapThread(int chpIndex) {
            this.chapIndex = chpIndex;
        }
        @Override
        public void run() {

            super.run();
            if (isStopRead) {
                return;
            }

            boolean isReadOk = false;
            try {
                // 判断是否章节累计
                if (myModelReader.Model.BookTextModel.isLoadBookOver()) {
                    ((GBTextWritableModel) myModelReader.Model.BookTextModel).settingSumSize(true);
                } else {
                    ((GBTextWritableModel) myModelReader.Model.BookTextModel).settingSumSize(false);
                }

                readChpFile(chapIndex, getChpaterPath(chapIndex));
                L.e(TAG, "chp file index:" + chapIndex + "  read over");

                myModelReader.Model.BookTextModel.buildLastDataCache(chapIndex);
                isReadOk = true;
            } catch (BookReadingException e) {
                e.printStackTrace();
            }

            synchronized (mFunctions) {
                if (!isStopRead) {
                    IFunction<Integer> funtion;
                    funtion = mFunctions.remove(chapIndex);
                    if (funtion != null) {
                        funtion.callback(isReadOk ? chapIndex : NULL);
                    }
                }
            }
        }
    }

    private float readChpCount = 0;// = GBTextModel.cacheChapNumber;

    /**
     * 开始生成缓存
     */
    public void startBuildCache() {
        myModelReader.Model.BookTextModel.initCacheDir();
        isStopRead = false;
        isCacheExists = isCacheExists(false);
        new Thread(getPageNumRunnable).start();

    }

    // 标记后台生成缓存线程是否正在执行
    protected volatile boolean isNoRun = true;
    // 是否第一次加载 标记是否存在缓存
    protected boolean isCacheExists = false;

    // 版权信息
    private CopyVersionInfo mCopyVersionInfo;
    public final static String COPYRIGHT = "Geeboo_CopyRight";

    public void readBook(GBFile file, GBTextPosition lastPosition, CopyVersionInfo copyVersionInfo)
            throws BookReadingException {
        this.mCopyVersionInfo = copyVersionInfo;
        myModelReader.Model.BookTextModel.setIsHaveCopyRight(true);
        readBook(file, lastPosition);
    }

    // 支持版权信息页面

    public void readBook(GBFile file, GBTextPosition lastPosition) throws BookReadingException {
        stopRead(true);
        myFilePrefix = MiscUtil.htmlDirectoryPrefix(file);

        myIdToHref.clear();
        myHtmlFileNames.clear();
        myNCXTOCFileName = null;
        myTourTOC.clear();
        myGuideTOC.clear();
        myState = READ_NONE;
        myTOCLabels.clear();
        isStopRead = false;
        XHTMLStyleReader.clearStyleExternal();
        TagSpoor.STACK.clear();
        try {
            read(file);
        } catch (IOException e) {
            throw new BookReadingException(e, file);
        }

        if (0 == myHtmlFileNames.size()) {
            L.i(TAG, "图书章节数量为0异常，强制中断,请检查图书文件格式是否正确");
            // if (GeeBookLoader.getOnExceptionListener() != null) {
            // GeeBookLoader.getOnExceptionListener().onCatchException(new
            // Exception("请检查图书文件格式是否正确"));
            // }
            throw new BookReadingException("opfFileNotFound", file);
        }

        // 添加版权支持
        if (mCopyVersionInfo != null) {
            // 在目录后添加版权页
            myHtmlFileNames.add(1, COPYRIGHT);
        }
        // 版权支持结束

        myModelReader.setMainTextModel();
        myModelReader.pushKind(GBTextKind.REGULAR);
        int chpFileSize = GBTextModel.cacheChapNumber;

        // 生成目录
        final XHTMLReader reader = new XHTMLReader(myModelReader, myFileNumbers, myFilePrefix);
        readContent(reader, myHtmlFileNames.size());

        // 判断是否第一次打开图书
        isCacheExists = isCacheExists(false);
        // 初始化章节加载状态
        // mChpReadStates = new byte[chpFileSize];

        if (myHtmlFileNames.size() > chpFileSize) {
            myModelReader.Model.BookTextModel.extendTextSizes(myHtmlFileNames.size(), isCacheExists);

        } else {
            myModelReader.Model.BookTextModel.setChapterSize(myHtmlFileNames.size());
        }

        myModelReader.Model.getInternalHyperLinks().resetChpSize(myHtmlFileNames.size());
        // 获取上一次阅读位置
        int chpFileIndex = 0;
        if (null != lastPosition) {
            chpFileIndex = lastPosition.getChpFileIndex();

            if (chpFileIndex < 0 || chpFileIndex >= myHtmlFileNames.size()) {
                chpFileIndex = 0;
                lastPosition = new GBTextFixedPosition(0, 0, 0, 0);
            }
        }

        if (chpFileIndex != 0) {
            --chpFileIndex;
        }

        // 设置章节索引映射
        myModelReader.Model.BookTextModel.getChpFileNumMapping().clear();

        int realSize = myHtmlFileNames.size();
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
            myModelReader.Model.BookTextModel.clearCache(i, true);

            ((GBTextWritableModel) myModelReader.Model.BookTextModel).settingSumSize(false);

            readChpFile(i, myHtmlFileNames.get(i));
        }

        myModelReader.Model.BookTextModel.buildLastDataCache(size - 1);

        generateTOC();

        new Thread(getPageNumRunnable).start();

    }

    /**
     * 章节是否加载中
     *
     * @param chpFileIndex
     * @return
     */
    public boolean isChpLoading(int chpFileIndex) {
        return execingChpFileIndex == chpFileIndex;
    }

    /**
     * 章节是否加载完毕
     *
     * @param chpFileIndex
     * @return
     */
    public boolean isLoad(int chpFileIndex) {
        return isLoadChp(chpFileIndex);
    }

    /**
     * 读取目录信息
     */
    private void readContent(XHTMLReader reader, int chpFileSize) {
        // L.e(TAG, "--->read content" + chpFileSize);
        for (int chpFileIndex = 0; chpFileIndex < chpFileSize; chpFileIndex++) {
            final GBFile xhtmlFile = GBFile.createFileByPath(myFilePrefix + myHtmlFileNames.get(chpFileIndex));
            final String referenceName = reader.getFileAlias(MiscUtil.archiveEntryName(xhtmlFile.getPath()));
            myTOCLabels.put(referenceName, chpFileIndex);
        }

    }

    /**
     * 功能描述： 读取章节文件<br>
     * 创建者： jack<br>
     * 创建日期：2014-11-19<br>
     *
     * @param chpFileIndex 章节索引
     * @param name 章节文件名称
     */
    protected boolean readChpFile(int chpFileIndex, String name) throws BookReadingException {
        L.e("@@@", "read chp file:" + chpFileIndex + "--" + name);
        synchronized (this) {
            execingChpFileIndex = chpFileIndex;
            myModelReader.setMainTextModel();

            int chpFileIndexRealPosition = chpFileIndex % GBTextModel.cacheChapNumber;
            realPositionMapping[chpFileIndexRealPosition] = chpFileIndex;
            myModelReader.Model.BookTextModel.getChpFileNumMapping().put(chpFileIndex, chpFileIndexRealPosition);

            myModelReader.myCurrentTextModel.setWrithChpFiliNum(chpFileIndex);
            // ++readChpCount;

            myModelReader.Model.BookTextModel.clearCache(chpFileIndex, true);
            // ((GBTextWritableModel)
            // myModelReader.Model.BookTextModel).setWrithChpFiliNum(chpFileIndex);

            final XHTMLReader reader = new XHTMLReader(myModelReader, myFileNumbers, myFilePrefix);
            if (name.equals(COPYRIGHT)) {
                // 解析版权模板
                GBFile copyRight = GBFile.createFileByPath("resources/copyright.html");
                try {
                    reader.setCopyVersionInfo(mCopyVersionInfo);
                    reader.readFile(copyRight, "#");
                } catch (IOException e) {
                    throw new BookReadingException(e, copyRight);
                }
            } else {
                final GBFile xhtmlFile = GBFile.createFileByPath(myFilePrefix + name);
                if (xhtmlFile == null || !xhtmlFile.exists()) {
                    myModelReader.Model.BookTextModel.nextChp();
                    return false;
                }

                reader.setmIsCoverFile(xhtmlFile.getPath().equals(myCoverFileName));

                final String referenceName = reader.getFileAlias(MiscUtil.archiveEntryName(xhtmlFile.getPath()));

                myModelReader.addHyperlinkLabel(referenceName);
                myTOCLabels.put(referenceName, chpFileIndex);
                try {
                    reader.readFile(xhtmlFile, referenceName + '#');
                } catch (IOException e) {
                    throw new BookReadingException(e, xhtmlFile);
                }
            }
            myModelReader.insertEndOfSectionParagraph();
            myModelReader.Model.BookTextModel.nextChp();

            // mChpReadStates[chpFileIndex % GBTextModel.cacheChapNumber] =
            // READ;
            execingChpFileIndex = -1;
            return true;

        }
    }

    /*************** epub/txt 差异化方法 ******************/
    // 获取章节解析器
    protected XHTMLReader getReader() {
        return new XHTMLReader(myModelReader, myFileNumbers, myFilePrefix);
    }

    protected int getChapterCount() {
        return myHtmlFileNames.size();
    }

    protected String getChpaterPath(int chapterIndex) {
        return myHtmlFileNames.get(chapterIndex);
    }

    // 初始化章节
    protected void initChapter() throws IOException {

    }

    /*************** epub/txt 差异化方法 ******************/

    /**
     * 功能描述： 页码扫描<br>
     * 创建者： jack<br>
     * 创建日期：2014-9-16<br>
     *
     * @param
     */
    private void scanPageNum(XHTMLReader reader, GBTextWritablePlainModel writeMode, GBTextWritablePlainModel textMode,
                             int chpPointer, int useIndex, int chpSize) throws Exception {

        writeMode.settingSumSize(false);
        // ++readChpCount;
        writeMode.resetParagraphsNumber(useIndex);

        // 检查正在打开章节与目前要生成缓存章节相同 则等待

        if (isLoad(chpPointer) || isChpLoading(chpPointer)) {
            return;
        }

        currentExecCacheChpFileIndex = chpPointer;
        simpleReadChpFile(reader, chpPointer, getChpaterPath(chpPointer), useIndex);// myHtmlFileNames.get(chpPointer),
        // useIndex);
        textMode.setParagraphsNumber(chpPointer, writeMode.getParagraphsNumberByChpFileIndex(useIndex));

        textMode.setTextSize(chpPointer, writeMode.getTextSizesByChpFileIndex(useIndex),
                writeMode.getParagraphsNumberByChpFileIndex(useIndex));

        currentExecCacheChpFileIndex = NULL;

        // 更新分页进度
        if (null != mProgressHander) {
            mProgressHander.callback((int) ((float) (readChpCount) / chpSize * 100));
        }

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.gc();
    }

    /**
     * txt/epub （txt提取目录）页码扫描线程 TODO 待优化缓存机制
     */
    protected Runnable getPageNumRunnable = new Runnable() {

        @Override
        public void run() {
            // 初始化目录信息
            try {
                initChapter();
            } catch (IOException e1) {
                e1.printStackTrace();
                return;
            }

            if (isCacheExists) {
                // 加载缓存章节信息
                boolean flag = myModelReader.Model.BookTextModel.getLinkCache(false);
                if (!flag) {
                    try {
                        synchronized (OEBBookReader.this) {
                            myModelReader.Model.BookTextModel.loadBookOver();
                            notifyBookOver();
                        }
                        if (null != mProgressHander) {
                            mProgressHander.callback(CALL_BOOK_OVER);
                        }
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 获取缓存信息有误
                myModelReader.Model.getTextModel().delLinkCache();
                isCacheExists = false;

            }

            // 设置状态为分页
            if (mProgressHander != null)
                mProgressHander.callback(INITPAGES);

            isNoRun = false;

            // Cookie cookie = new Cookie(((GBAndroidLibrary)
            // GBAndroidLibrary.Instance()).getActivity(), Cookie.APP_CFG);

            final int chpSize = getChapterCount(); // myHtmlFileNames.size();
            GBTextWritablePlainModel writeMode = (GBTextWritablePlainModel) myModelReader.Model.GenerPageNumModel;
            GBTextWritablePlainModel textMode = (GBTextWritablePlainModel) myModelReader.Model.BookTextModel;

            final XHTMLReader reader = getReader();
            int useIndex = 0;
            // int chpPointer = cookie.getInt(READ_LAST_CHP_INDEX +
            // myModelReader.Model.Book.getId());
            // readChpCount = cookie.getFloat(READ_CHP_COUNT +
            // myModelReader.Model.Book.getId());
            int chpPointer = 0;
            // 设置章节映射关系
            for (int i = chpPointer; i < chpSize; i++) {
                writeMode.getChpFileNumMapping().put(i, useIndex);
            }

            for (; chpPointer < chpSize; chpPointer++) {
                readChpCount++;
                // 判断是否已经是加载章节
                final int[] cacheKeys = textMode.getChpFileNumMapping().getKeys();
                boolean isCache = false;
                for (int i = 0; i < GBTextModel.cacheChapNumber; i++) {
                    if (chpPointer == cacheKeys[i]) {
                        isCache = true;
                        break;
                    }
                }
                if (isCache)
                    continue;

                if (isStopRead) {

                    // cookie.putVal(READ_LAST_CHP_INDEX +
                    // myModelReader.Model.Book.getId(), --chpPointer);
                    // cookie.putVal(READ_CHP_COUNT +
                    // myModelReader.Model.Book.getId(), --readChpCount);
                    // myModelReader.Model.BookTextModel.buildLinkCache(true);
                    isNoRun = true;
                    readChpCount = 0;
                    return;
                }

                try {
                    scanPageNum(reader, writeMode, textMode, chpPointer, useIndex, chpSize);

                    Thread.sleep(10);
                } catch (Exception e) {

                    // cookie.putVal(READ_LAST_CHP_INDEX +
                    // myModelReader.Model.Book.getId(), chpPointer);
                    // cookie.putVal(READ_CHP_COUNT +
                    // myModelReader.Model.Book.getId(), readChpCount);
                    // myModelReader.Model.BookTextModel.buildLinkCache(true);
                    isNoRun = true;
                    return;
                }

            }

            synchronized (OEBBookReader.this) {
                textMode.loadBookOver();
                notifyBookOver();
            }
            if (null != mProgressHander) {
                mProgressHander.callback(CALL_BOOK_OVER);
            }

            textMode.buildLinkCache(false);

        }
    };

    /**
     * 功能描述： 页码扫描完毕，保存扫描信息<br>
     * 创建者： jack<br>
     * 创建日期：2014-9-22<br>
     *
     * @param
     */
    private void notifyBookOver() {
        if (myModelReader.Model.BookTextModel.isLoadBookOver()) {
            // myModelReader.setMainTextModel();
            int chapFileNumber = myModelReader.Model.BookTextModel.getChapterSize();
            if (null != GeeBookLoader.getBookMgr() && chapFileNumber > 0) {
                long totalCharNum = myModelReader.Model.BookTextModel.getTextLength(chapFileNumber - 1,
                        myModelReader.Model.BookTextModel.getParagraphsNumber(chapFileNumber - 1));
                GeeBookLoader.getBookMgr().notifyLocalOverCountClipBoardTextLen(totalCharNum);

                L.e(TAG, "totalCharNum" + totalCharNum);
            }
        }
    }

    /**
     * 功能描述： 页码扫描章节分析<br>
     * 创建者： jack<br>
     * 创建日期：2014-9-22<br>
     *
     * @param reader xhtml解析器
     * @param chpFileIndex 章节索引
     * @param name 章节文件名称
     * @param useIndex 缓存索引
     */
    protected boolean simpleReadChpFile(XHTMLReader reader, int chpFileIndex, String name, int useIndex)
            throws BookReadingException {
        synchronized (this) {

            myModelReader.settingGenerPageMode();
            myModelReader.myCurrentTextModel.setWrithChpFiliNum(useIndex);
            if (name.equals(COPYRIGHT)) {
                // 解析版权模板
                GBFile copyRight = GBFile.createFileByPath("resources/copyright.html");
                try {
                    reader.readFile(copyRight, "#");
                } catch (IOException e) {
                    throw new BookReadingException(e, copyRight);
                }
            } else {
                final GBFile xhtmlFile = GBFile.createFileByPath(myFilePrefix + name);

                if (xhtmlFile == null || !xhtmlFile.exists()) {
                    return false;
                }

                final String referenceName = reader.getFileAlias(MiscUtil.archiveEntryName(xhtmlFile.getPath()));

                try {
                    reader.readFile(xhtmlFile, referenceName + '#');
                } catch (IOException e) {
                    throw new BookReadingException(e, xhtmlFile);
                }
            }
            myModelReader.insertEndOfSectionParagraph();
            return true;

        }
    }

    private BookModel.Label getTOCLabel(String id) {
        final int index = id.indexOf('#');
        final String path = (index >= 0) ? id.substring(0, index) : id;
        final String num = myFileNumbers.get(path);

        if (num == null) {
            return null;
        }

        BookModel.Label label = new BookModel.Label(null, Integer.parseInt(num), 0);

        return label;

        /*
         * if (index == -1) { // modify by yangn final Integer chp =
         * myTOCLabels.get(num); if (chp == null) { return null; } return new
         * BookModel.Label(null, chp, 0); } return
         * myModelReader.Model.getLabel(num + id.substring(index));
         */

    }

    private boolean readNCX() throws BookReadingException {
        if (myNCXTOCFileName == null) {
            return false;
        }

        final GBFile ncxFile = GBFile.createFileByPath(myFilePrefix + myNCXTOCFileName);
        if (ncxFile == null || !ncxFile.exists()) {
            return false;
        }

        final NCXReader ncxReader = new NCXReader(myModelReader);
        ncxReader.readFile(ncxFile);
        final Map<Integer, NCXReader.NavPoint> navigationMap = ncxReader.navigationMap();
        if (navigationMap.isEmpty()) {
            return false;
        }

        int level = 0;

        for (NCXReader.NavPoint point : navigationMap.values()) {
            final BookModel.Label label = getTOCLabel(point.ContentHRef);
            /*
             * if(null==label){ final int index =
             * point.ContentHRef.indexOf('#'); final String path = (index >= 0)
             * ? point.ContentHRef.substring(0, index) : point.ContentHRef;
             * final String num = myFileNumbers.get(path);
             * System.out.println("pp"); }
             */
            int index = (label != null) ? label.ChpFileIndex : NULL;
            while (level > point.Level) {
                myModelReader.endContentsParagraph();
                --level;
            }
            while (++level <= point.Level) {
                myModelReader.beginContentsParagraph(-2);
                myModelReader.addContentsData(Dots);
            }
            myModelReader.beginContentsParagraph(index);
            myModelReader.addContentsData(point.Text.toCharArray());
        }
        while (level > 0) {
            myModelReader.endContentsParagraph();
            --level;
        }

        return true;
    }

    /**
     * 功能描述： 矫正章节目录索引<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-17<br>
     *
     * @throws BookReadingException
     */
    private void generateTOC() throws BookReadingException {
        if (readNCX()) {
            return;
        }

        for (Reference ref : myTourTOC.isEmpty() ? myGuideTOC : myTourTOC) {

            final BookModel.Label label = getTOCLabel(ref.HRef);
            if (label != null) {
                final int chpFileIndex = label.ChpFileIndex;// .ParagraphIndex;
                // modify by yangn
                if (chpFileIndex != NULL) {
                    myModelReader.beginContentsParagraph(chpFileIndex);
                    myModelReader.addContentsData(ref.Title.toCharArray());
                    myModelReader.endContentsParagraph();
                }
            }
        }
    }

    private static final String MANIFEST = "manifest";
    private static final String SPINE = "spine";
    private static final String GUIDE = "guide";
    private static final String TOUR = "tour";
    private static final String SITE = "site";
    private static final String REFERENCE = "reference";
    private static final String ITEMREF = "itemref";
    private static final String ITEM = "item";

    private static final String COVER = "cover";
    private static final String COVER_IMAGE = "other.ms-coverimage-standard";

    private static final int READ_NONE = 0;
    private static final int READ_MANIFEST = 1;
    private static final int READ_SPINE = 2;
    private static final int READ_GUIDE = 3;
    private static final int READ_TOUR = 4;

    private int myState;

    private static final String META = "meta";
    private static final String BOOK_NAME_DISPLAY_MODE = "bookName";
    private static final String CHP_NAME_DISPLAY_MODE = "chapterName";

    @Override
    public boolean startElementHandler(String tag, GBStringMap xmlattributes) {
        tag = tag.toLowerCase();
        if (myOPFSchemePrefix != null && tag.startsWith(myOPFSchemePrefix)) {
            tag = tag.substring(myOPFSchemePrefix.length());
        }
        tag = tag.intern();
        if (MANIFEST == tag) {
            myState = READ_MANIFEST;
        } else if (SPINE == tag) {
            myNCXTOCFileName = myIdToHref.get(xmlattributes.getValue("toc"));
            myState = READ_SPINE;
        } else if (GUIDE == tag) {
            myState = READ_GUIDE;
        } else if (TOUR == tag) {
            myState = READ_TOUR;
        } else if (myState == READ_MANIFEST && ITEM == tag) {
            final String id = xmlattributes.getValue("id");
            String href = xmlattributes.getValue("href");
            if ((id != null) && (href != null)) {
                href = MiscUtil.decodeHtmlReference(href);
                myIdToHref.put(id, href);
            }
        } else if (myState == READ_SPINE && ITEMREF == tag) {
            final String id = xmlattributes.getValue("idref");
            if (id != null) {
                final String fileName = myIdToHref.get(id);
                if (fileName != null) {
                    myHtmlFileNames.add(fileName);
                }
            }
        } else if (myState == READ_GUIDE && REFERENCE == tag) {
            final String type = xmlattributes.getValue("type");
            final String title = xmlattributes.getValue("title");
            String href = xmlattributes.getValue("href");
            if (href != null) {
                href = MiscUtil.decodeHtmlReference(href);
                if (title != null) {
                    myGuideTOC.add(new Reference(title, href));
                }

                if (COVER.equals(type)) {
                    final GBFile imageFile = GBFile.createFileByPath(myFilePrefix + href);
                    myCoverFileName = imageFile.getPath();

                    final GBFileImage image = XHTMLImageFinder.getCoverImage(imageFile);

                    if (image != null) {
                        GBImageData imageData = GBImageManager.Instance().getImageData(image);
                        final Bitmap bitmap = ((GBAndroidImageData) imageData).getBitmap(new GBSize(118, 250),
                                ImgFitType.AUTO_FIT);
                        try {
                            FileUtils.saveBitmap(bitmap,
                                    FileUtils.THUMB + File.separator + myModelReader.Model.Book.getId() + ".jpg");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        myCoverFileName = null;
                    }
                } else if (COVER_IMAGE.equals(type)) {
                    // final GBFile imageFile =
                    // GBFile.createFileByPath(myFilePrefix + href);
                    // myCoverFileName = imageFile.getPath();
                    // final String imageName = imageFile.getFullName();
                    // myModelReader.setMainTextModel();
                    // myModelReader.addImageReference(imageName, (short) 0,
                    // true);
                    // myModelReader.addImage(imageName, new
                    // GBFileImage(MimeType.IMAGE_AUTO, imageFile));
                    // myModelReader.insertEndOfSectionParagraph();
                }

            }
        } else if (myState == READ_TOUR && SITE == tag) {
            final String title = xmlattributes.getValue("title");
            String href = xmlattributes.getValue("href");
            if ((title != null) && (href != null)) {
                href = MiscUtil.decodeHtmlReference(href);
                myTourTOC.add(new Reference(title, href));
            }
        } else if (META.equals(tag)) {
            String val = xmlattributes.getValue("name");

            if (null == val || "".equals(val)) {
                return false;
            }

            if (val.equals(BOOK_NAME_DISPLAY_MODE)) {
                val = xmlattributes.getValue("content");
                if (null != val && !"".equals(val)) {
                    try {
                        myModelReader.Model.BookTextModel.setBookNameDisplayMode(Byte.parseByte(val));
                    } catch (NumberFormatException e) {
                    }

                }
            } else if (val.equals(CHP_NAME_DISPLAY_MODE)) {
                val = xmlattributes.getValue("content");
                if (null != val && !"".equals(val)) {
                    try {
                        myModelReader.Model.BookTextModel.setChpNameDisplayMode(Byte.parseByte(val));
                    } catch (NumberFormatException e) {
                    }

                }
            }

        }
        return false;
    }

    @Override
    public boolean endElementHandler(String tag) {
        tag = tag.toLowerCase();
        if (myOPFSchemePrefix != null && tag.startsWith(myOPFSchemePrefix)) {
            tag = tag.substring(myOPFSchemePrefix.length());
        }
        tag = tag.intern();
        if (MANIFEST == tag || SPINE == tag || GUIDE == tag || TOUR == tag) {
            myState = READ_NONE;
        }
        return false;
    }

    @Override
    public boolean processNamespaces() {
        return true;
    }

    @Override
    public void namespaceMapChangedHandler(Map<String, String> namespaceMap) {
        myOPFSchemePrefix = null;
        for (Map.Entry<String, String> entry : namespaceMap.entrySet()) {
            if (OpenPackagingFormat.equals(entry.getValue())) {
                myOPFSchemePrefix = entry.getKey() + ":";
                break;
            }
        }
    }

    @Override
    public boolean dontCacheAttributeValues() {
        return true;
    }


    public class Reference
    {
        public final String Title;
        public final String HRef;

        public Reference(String title, String href)
        {
            Title = title;
            HRef = href;
        }
    }

}
