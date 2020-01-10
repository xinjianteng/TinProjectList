package com.tin.projectlist.app.library.reader.controller;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.tin.projectlist.app.library.reader.parser.domain.GBApplication;
import com.tin.projectlist.app.library.reader.parser.domain.GBKeyBindings;
import com.tin.projectlist.app.library.reader.parser.object.GBColor;
import com.tin.projectlist.app.library.reader.parser.option.GBBooleanOption;
import com.tin.projectlist.app.library.reader.parser.option.GBColorOption;
import com.tin.projectlist.app.library.reader.parser.option.GBEnumOption;
import com.tin.projectlist.app.library.reader.parser.option.GBFloatOption;
import com.tin.projectlist.app.library.reader.parser.option.GBIntegerOption;
import com.tin.projectlist.app.library.reader.parser.option.GBIntegerRangeOption;
import com.tin.projectlist.app.library.reader.parser.option.GBStringOption;
import com.tin.projectlist.app.library.reader.parser.platform.GBLibrary;
import com.tin.projectlist.app.library.reader.parser.text.linbreak.LineBreaker;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextPosition;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum;
import com.tin.projectlist.app.library.reader.parser.zip.DeflatingDecompressor;
import com.tin.projectlist.app.library.reader.view.ReadView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类名： ReaderApplication.java<br>
 * 描述： 阅读器上下文对象<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-24<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class ReaderApplication extends GBApplication {
    final String TAG = "ReaderApplication";

    public final GBBooleanOption AllowScreenBrightnessAdjustmentOption = new GBBooleanOption("LookNFeel",
            "AllowScreenBrightnessAdjustment", true);
    public final GBStringOption TextSearchPatternOption = new GBStringOption("TextSearch", "Pattern", "");

    public final GBBooleanOption UseSeparateBindingsOption = new GBBooleanOption("KeysOptions", "UseSeparateBindings",
            false);
    // 是否支持双击
    public final GBBooleanOption EnableDoubleTapOption = new GBBooleanOption("Options", "EnableDoubleTap", true);
    public final GBBooleanOption NavigateAllWordsOption = new GBBooleanOption("Options", "NavigateAllWords", false);

    /*
     * 文本点击状态
     */
    public static enum WordTappingAction {
        doNothing, selectSingleWord, startSelecting, openDictionary
    }

    public final GBEnumOption<WordTappingAction> WordTappingActionOption = new GBEnumOption<WordTappingAction>(
            "Options", "WordTappingAction", WordTappingAction.startSelecting);
    // 图片背景色配置
    public final GBColorOption ImageViewBackgroundOption = new GBColorOption("Colors", "ImageViewBackground",
            new GBColor(127, 127, 127));
    public final GBEnumOption<ReadView.ImageFitting> FitImagesToScreenOption = new GBEnumOption<ReadView.ImageFitting>(
            "Options", "FitImagesToScreen", ReadView.ImageFitting.covers);

    public static enum ImageTappingAction {
        doNothing, selectImage, openImageView
    }

    public final GBEnumOption<ImageTappingAction> ImageTappingActionOption = new GBEnumOption<ImageTappingAction>(
            "Options", "ImageTappingAction", ImageTappingAction.openImageView);

    /*
     * 阅读版面的边距设置
     */
    public GBIntegerRangeOption LeftMarginOption;
    public GBIntegerRangeOption RightMarginOption;
    public GBIntegerRangeOption TopMarginOption;
    public GBIntegerRangeOption BottomMarginOption;

    public void initMargin() {
        final int dpi = GBLibrary.Instance().getDisplayDPI();
        final int x = GBLibrary.Instance().getPixelWidth();
        final int y = GBLibrary.Instance().getPixelHeight();
        final int horMargin = Math.min(dpi / 5, Math.min(x, y) / 30);
        LeftMarginOption = new GBIntegerRangeOption("Options", "LeftMargin", 0, 100, horMargin);
        RightMarginOption = new GBIntegerRangeOption("Options", "RightMargin", 0, 100, horMargin);
        TopMarginOption = new GBIntegerRangeOption("Options", "TopMargin", 0, 100, 30);
        BottomMarginOption = new GBIntegerRangeOption("Options", "BottomMargin", 0, 100, 30);
    }

    // fbreader 的页面底部配置信息
    // public final GBIntegerRangeOption ScrollbarTypeOption =
    // new GBIntegerRangeOption("Options", "ScrollbarType", 0, 3,
    // ReadView.SCROLLBAR_SHOW_AS_FOOTER);
    // public final GBIntegerRangeOption FooterHeightOption =
    // new GBIntegerRangeOption("Options", "FooterHeight", 8, 20, 9);
    // public final GBBooleanOption FooterShowTOCMarksOption =
    // new GBBooleanOption("Options", "FooterShowTOCMarks", true);
    // public final GBBooleanOption FooterShowClockOption =
    // new GBBooleanOption("Options", "ShowClockInFooter", true);
    // public final GBBooleanOption FooterShowBatteryOption =
    // new GBBooleanOption("Options", "ShowBatteryInFooter", true);
    // public final GBBooleanOption FooterShowProgressOption =
    // new GBBooleanOption("Options", "ShowProgressInFooter", true);
    // public final GBStringOption FooterFontOption =
    // new GBStringOption("Options", "FooterFont", "Droid Sans");
    // 阅读模式配置（夜间白天）
    public final GBStringOption ColorProfileOption = new GBStringOption("Options", "ColorProfile", ColorProfile.NIGHT);
    public ColorProfile.DayModel mDayModel;
    public int isEnableVolumeScrollPage = 1;// 是否启动音量键翻页
    public int isShowTitle = 1;// 是否显示标题
    public String chapterName = "";//章节名称
    public int isShowPercent = 1;// 是否显示百分比
    public int isShowPageNum = 1;// 是否显示页码
    public int isShowElec = 1;// 是否显示电量
    public int isShowTime = 1;// 是否显示时间
    public boolean isFirstRead = true;//是否首次阅读

    // 亮度设置配置add by jack
    public final GBFloatOption LightOption = new GBFloatOption("Options", "LightOption", 0.5f);
    // 屏保时间设置
    public final GBIntegerOption SleepTimeOption = new GBIntegerOption("Options", "SleepTime", 0);
    // 语音设置
    public final GBIntegerRangeOption Speed = new GBIntegerRangeOption("Options", "speed", 0, 100, 50);
    public final GBIntegerRangeOption Ptich = new GBIntegerRangeOption("Options", "pitch", 0, 100, 50);
    // public final GBBooleanOption ShowLibraryInCancelMenuOption = new
    // GBBooleanOption("CancelMenu", "library", true);
    // public final GBBooleanOption ShowNetworkLibraryInCancelMenuOption = new
    // GBBooleanOption("CancelMenu",
    // "networkLibrary", true);
    // public final GBBooleanOption ShowPreviousBookInCancelMenuOption = new
    // GBBooleanOption("CancelMenu", "previousBook",
    // false);
    // public final GBBooleanOption ShowPositionsInCancelMenuOption = new
    // GBBooleanOption("CancelMenu", "positions", true);

    private final GBKeyBindings myBindings = new GBKeyBindings("Keys");

    public final ReadView BookTextView;
    // public final ReadView FootnoteView;

    public volatile BookModel Model;

    private GBTextPosition myJumpEndPosition;
    private Date myJumpTimeStamp;
    // 阅读业务处理类
    public final IBookCollection Collection;

    /**
     * 构造方法 初始化支持的业务处理
     *
     * @param collection
     */
    public ReaderApplication(IBookCollection collection) {
        super();
        // 设置分词和编码器
        LineBreaker.setmBreakerInterface(new MyLineBreaker());
        DeflatingDecompressor.setmInterface(new MyDeflatingDecompressor());
        //

        Collection = collection;

        addAction(ActionCode.INCREASE_FONT, new ChangeFontSizeAction(this, +2));
        addAction(ActionCode.DECREASE_FONT, new ChangeFontSizeAction(this, -2));

        addAction(ActionCode.FIND_NEXT, new FindNextAction(this));
        addAction(ActionCode.FIND_PREVIOUS, new FindPreviousAction(this));
        addAction(ActionCode.CLEAR_FIND_RESULTS, new ClearFindResultsAction(this));

        addAction(ActionCode.SELECTION_CLEAR, new SelectionClearAction(this));

        addAction(ActionCode.TURN_PAGE_FORWARD, new TurnPageAction(this, true));
        addAction(ActionCode.TURN_PAGE_BACK, new TurnPageAction(this, false));

        addAction(ActionCode.MOVE_CURSOR_UP, new MoveCursorAction(this, PageEnum.DirectType.UP));
        addAction(ActionCode.MOVE_CURSOR_DOWN, new MoveCursorAction(this, PageEnum.DirectType.DOWN));
        addAction(ActionCode.MOVE_CURSOR_LEFT, new MoveCursorAction(this, PageEnum.DirectType.RTOL));
        addAction(ActionCode.MOVE_CURSOR_RIGHT, new MoveCursorAction(this, PageEnum.DirectType.LTOR));

        addAction(ActionCode.VOLUME_KEY_SCROLL_FORWARD, new VolumeKeyTurnPageAction(this, true));
        addAction(ActionCode.VOLUME_KEY_SCROLL_BACK, new VolumeKeyTurnPageAction(this, false));

        addAction(ActionCode.SWITCH_TO_DAY_PROFILE, new SwitchProfileAction(this, ColorProfile.DAY));
        addAction(ActionCode.SWITCH_TO_NIGHT_PROFILE, new SwitchProfileAction(this, ColorProfile.NIGHT));

        addAction(ActionCode.EXIT, new ExitReadAction(this));
        // 页面跳转
        addAction(ActionCode.GOTO_PAGE, new GOTOAction(this));

        BookTextView = new ReadView(this);
        // FootnoteView = new ReadView(this);

        setView(BookTextView);
    }

    private static boolean isOpening = false; // 是否正在打开图书
    private static Runnable mAferRun = null; // 打开之后的动作
    private Exception mOpenException; // 打开图书异常

    /*
     * 是否正在打开图书
     */
    public static boolean isOpening() {
        return isOpening;
    }

    /**
     * 功能描述： 设置打开之后的业务处理<br>
     * 创建者： jack<br>
     * 创建日期：2014-6-26<br>
     *
     * @param
     */
    public static void setmAferRun(Runnable mAferRun) {
        ReaderApplication.mAferRun = mAferRun;
    }

    public void openBook(final Activity context, final View mLoadingView, final LinearLayout ll_loading1, final Book book, final Bookmark bookmark, final Runnable postAction) {
        isOpening = true;
        mOpenException = null;
        if (book != null || Model == null) {
            //加载特效
            mLoadingView.setVisibility(View.VISIBLE);
            ll_loading1.setVisibility(View.VISIBLE);
            mLoadingView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            new Thread(){
                @Override
                public void run() {
                    long t1 = System.currentTimeMillis();
                    openBookInternal(book, bookmark, false);
                    L.e("time", (System.currentTimeMillis() - t1) / 1000 + "-----------ms");
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //解除特效
                            mLoadingView.setVisibility(View.GONE);
                            ll_loading1.setVisibility(View.GONE);
                            if (mOpenException != null) {
                                processException(mOpenException);
                                // 清除图书缓存
                                delLinkCache(book);
                            } else {
                                isOpening = false;
                                if (mAferRun != null) {
                                    mAferRun.run();
                                }
                                postAction.run();
                            }
                        }
                    });
                }
            }.start();
//            runWithMessage(GBResource.resource("readerPage").getResource("loadTip").getValue(), new Runnable() {
//                public void run() {
//                    long t1 = System.currentTimeMillis();
//                    openBookInternal(book, bookmark, false);
//                    L.e("time", (System.currentTimeMillis() - t1) / 1000 + "-----------ms");
//                }
//            }, new Runnable() {
//
//                @Override
//                public void run() {
//                    if (mOpenException != null) {
//                        processException(mOpenException);
//                        // 清除图书缓存
//                        delLinkCache(book);
//                    } else {
//                        isOpening = false;
//                        if (mAferRun != null) {
//                            mAferRun.run();
//                        }
//                        postAction.run();
//                    }
//                }
//            });
        }
    }

    /**
     * 清除阅读相关缓存
     *
     * @param book
     * @return
     */
    public boolean delLinkCache(Book book) {
        if (book == null)
            return false;
        File file = new File(GBPaths.cacheDirectory());
        File[] fileList = file.listFiles();
        if (null != fileList && fileList.length > 0) {
            for (File item : fileList) {
                if (item.isDirectory()) {
                    continue;
                }
                int lastPoint = item.getName().lastIndexOf(".") - 1;
                if (lastPoint < 0) {
                    continue;
                }
                // 判断后缀相同并且名字最后一个字符为数字说明是子项缓存 和删除分页缓存
                if (item.getName().endsWith("cache") && item.getName().contains(book.getId() + "")) {
                    if (!item.delete())
                        item.deleteOnExit();
                }
            }
        }
        return true;
    }

    @Override
    public void openBookByChapFileIndex(int chapterFileIndex, IFunction callback) {
        try {
            GBTextPosition bookChpPosition = new GBTextFixedPosition(chapterFileIndex, 0, 0, 0);

            Model.Book.getPlugin().readModel(bookChpPosition, callback);
        } catch (BookReadingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean closeWindow() {
        try {
            if (Model != null) {
                Model.Book.getPlugin().stopReadMode();
                // Model.getTextModel().finalize();
            }
        } catch (BookReadingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i = 0; i < 10; i++) {
            System.gc();
        }
        return super.closeWindow();
    }

    /**
     * 获取图书名称
     *
     * @return
     */
    public String getBookTitle() {
        return null == mBookTile ? "" : mBookTile;
    }
    String mBookTile = "";

    public void setBookTitle(String title) {
        mBookTile = title;
    }

    @Override
    public boolean isLoadBookChp(int chpFileIndex) {
        try {
            return Model.Book.getPlugin().isLoadChp(chpFileIndex);
        } catch (BookReadingException e) {
            return false;
        }
    }

    /*
     * 重新加载图书
     */
    public void reloadBook() {
        if (Model != null && Model.Book != null) {
            runWithMessage("loadingBook", new Runnable() {
                public void run() {
                    openBookInternal(Model.Book, null, true);
                }
            }, null);
        }
    }

    private ColorProfile myColorProfile;

    /*
     * 获取不同阅读模式下的颜色配置
     */
    public ColorProfile getColorProfile() {
        if (myColorProfile == null) {
            myColorProfile = ColorProfile.get(getColorProfileName());
        }
        return myColorProfile;
    }

    public String getColorProfileName() {
        return ColorProfileOption.getValue();
    }

    /**
     * 功能描述： 设置当前阅读模式（白天/黑夜）<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-26<br>
     *
     * @param name 白天/夜间
     * @param model 白天主题
     */
    public void setColorProfileName(String name, DayModel model) {
        ColorProfileOption.setValue(name);
        mDayModel = model;
        myColorProfile = null;
        if (name.equals(ColorProfile.DAY))
            getColorProfile().setDayModel(model);
    }

    public GBKeyBindings keyBindings() {
        return myBindings;
    }

    public ReadView getTextView() {
        return (ReadView) getCurrentView();
    }

    /**
     * 功能描述：内部链接跳转<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-2<br>
     *
     * @param id
     */
    public void tryOpenFootnote(String id) {
        if (Model != null) {
            myJumpEndPosition = null;
            myJumpTimeStamp = null;
            BookModel.Label label = Model.getLabel(id);
            if (label != null) {
                if (label.ModelId == null) {
                    if (getTextView() == BookTextView) {
                        addInvisibleBookmark();
                        myJumpEndPosition = new GBTextFixedPosition(label.ChpFileIndex, label.ParagraphIndex, 0, 0);
                        myJumpTimeStamp = new Date();
                    }
                    BookTextView.gotoPosition(label.ChpFileIndex, label.ParagraphIndex, 0, 0);
                    setView(BookTextView);
                } else {
                    // FootnoteView.setModel(Model.getFootnoteModel(label.ModelId));
                    // setView(FootnoteView);
                    // FootnoteView.gotoPosition(label.ParagraphIndex, 0, 0);
                }
                getViewImp().repaint();
            }
        }
    }

    /**
     * 功能描述： 清除文字缓存<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-26<br>
     */
    public void clearTextCaches() {
        BookTextView.clearCaches();
        // FootnoteView.clearCaches();
    }

    synchronized void openBookInternal(Book book, Bookmark bookmark, boolean force) {
        if (book == null) {
            // /*
            // * book = Collection.getRecentBook(0); if (book == null ||
            // * !book.File.exists()) { book =
            // * Collection.getBookByFile(BookUtil.getHelpFile()); } if (book ==
            // * null) { return; }
            // */
            mOpenException = new TipException("图书对象为空！");
            return;
        }

        if (!force && Model != null && book.equals(Model.Book)) {
            if (bookmark != null) {
                gotoBookmark(bookmark);
            }
            try {
                Model.Book.getPlugin().startBuildCache();
            } catch (BookReadingException e) {
                mOpenException = e;
            }

            return;
        }

        onViewChanged();

        // storePosition();
        BookTextView.setModel(null);
        // FootnoteView.setModel(null);
        clearTextCaches();
        // 切断强引用
        Model = null;

        System.gc();
        try {
            final GBTextPosition lastPosition;
            if (null == GeeBookLoader.getBookMgr()) {
                lastPosition = Collection.getStoredPosition(book.getId());
            } else {
                lastPosition = GeeBookLoader.getBookMgr().getLastReadPostion();
            }

            Model = BookModel.createModel(book, lastPosition);
            Collection.saveBook(book, false);
            GBTextHyphenator.Instance().load(book.getLanguage());
            BookTextView.setModel(Model.getTextModel());
            if (lastPosition != null)
                loadAnnotation(lastPosition.getChpFileIndex(), false);

            L.e(TAG, "in open book internal" + lastPosition);

            BookTextView.gotoPosition(lastPosition);
            if (bookmark == null) {
                setView(BookTextView);
            } else {
                gotoBookmark(bookmark);
            }
            Collection.addBookToRecentList(book);
            final StringBuilder title = new StringBuilder(book.getTitle());
            if (!book.authors().isEmpty()) {
                boolean first = true;
                for (Author a : book.authors()) {
                    title.append(first ? " (" : ", ");
                    title.append(a.DisplayName);
                    first = false;
                }
                title.append(")");
            }
            setTitle(title.toString());
        } catch (BookReadingException e) {
            // processException(e);
            mOpenException = e;
            return;
        } catch (TipException e) {
            mOpenException = e;
            // processException(e);
            return;
        }

        getViewImp().reset();
        getViewImp().repaint();
    }

    public boolean jumpBack() {
        try {
            if (getTextView() != BookTextView) {
                showBookTextView();
                return true;
            }

            if (myJumpEndPosition == null || myJumpTimeStamp == null) {
                return false;
            }
            // more than 2 minutes ago
            if (myJumpTimeStamp.getTime() + 2 * 60 * 1000 < new Date().getTime()) {
                return false;
            }
            if (!myJumpEndPosition.equals(BookTextView.getStartCursor())) {
                return false;
            }

            final List<Bookmark> bookmarks = Collection.invisibleBookmarks(Model.Book);
            if (bookmarks.isEmpty()) {
                return false;
            }
            final Bookmark b = bookmarks.get(0);
            Collection.deleteBookmark(b);
            gotoBookmark(b);
            return true;
        } finally {
            myJumpEndPosition = null;
            myJumpTimeStamp = null;
        }
    }

    private void gotoBookmark(Bookmark bookmark) {
        final String modelId = bookmark.ModelId;
        if (modelId == null) {
            addInvisibleBookmark();
            BookTextView.gotoPosition(bookmark);
            setView(BookTextView);
        }
        // else {
        // FootnoteView.setModel(Model.getFootnoteModel(modelId));
        // FootnoteView.gotoPosition(bookmark);
        // setView(FootnoteView);
        // }
        getViewImp().repaint();
    }

    /**
     * 功能描述： 显示阅读页<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-26<br>
     */
    public void showBookTextView() {
        setView(BookTextView);
    }

    public void onWindowClosing() {
        // storePosition();
    }

    public void storePosition(boolean closeBook) {
        L.i(">>>>>>>>>保存阅读进度>>>>>>>>>>>");
        if (Model != null && Model.Book != null && BookTextView != null) {
            // Model.
            if (null == GeeBookLoader.getBookMgr()) {
                Collection.storePosition(Model.Book.getId(), BookTextView.getStartCursorFromUser());
            } else {
                try {
                    GeeBookLoader.getBookMgr().storedPosition(BookTextView.getStartCursorFromUser(),
                            BookTextView.getReadProgressPercent(), closeBook);
                    // L.e(TAG,
                    // "store position="+BookTextView.getStartCursorFromUser());
                } catch (TipException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    // static enum CancelActionType {
    // library, networkLibrary, previousBook, returnTo, close
    // }
    //
    // public static class CancelActionDescription {
    // final CancelActionType Type;
    // public final String Title;
    // public final String Summary;
    //
    // CancelActionDescription(CancelActionType type, String summary) {
    // final GBResource resource = GBResource.resource("cancelMenu");
    // Type = type;
    // Title = resource.getResource(type.toString()).getValue();
    // Summary = summary;
    // }
    // }
    //
    // private static class BookmarkDescription extends CancelActionDescription
    // {
    // final Bookmark Bookmark;
    //
    // BookmarkDescription(Bookmark b) {
    // super(CancelActionType.returnTo, b.getText());
    // Bookmark = b;
    // }
    // }

    // private final ArrayList<CancelActionDescription> myCancelActionsList =
    // new ArrayList<CancelActionDescription>();
    // /*
    // * fbreader返回按钮业务
    // */
    // public List<CancelActionDescription> getCancelActionsList() {
    // myCancelActionsList.clear();
    // if (ShowLibraryInCancelMenuOption.getValue()) {
    // myCancelActionsList.add(new
    // CancelActionDescription(CancelActionType.library, null));
    // }
    // if (ShowNetworkLibraryInCancelMenuOption.getValue()) {
    // myCancelActionsList.add(new
    // CancelActionDescription(CancelActionType.networkLibrary, null));
    // }
    // if (ShowPreviousBookInCancelMenuOption.getValue()) {
    // final Book previousBook = Collection.getRecentBook(1);
    // if (previousBook != null) {
    // myCancelActionsList.add(new
    // CancelActionDescription(CancelActionType.previousBook, previousBook
    // .getTitle()));
    // }
    // }
    // if (ShowPositionsInCancelMenuOption.getValue()) {
    // if (Model != null && Model.Book != null) {
    // for (Bookmark bookmark : Collection.invisibleBookmarks(Model.Book)) {
    // myCancelActionsList.add(new BookmarkDescription(bookmark));
    // }
    // }
    // }
    // myCancelActionsList.add(new
    // CancelActionDescription(CancelActionType.close, null));
    // return myCancelActionsList;
    // }

    // public void runCancelAction(int index) {
    // if (index < 0 || index >= myCancelActionsList.size()) {
    // return;
    // }
    //
    // final CancelActionDescription description =
    // myCancelActionsList.get(index);
    // switch (description.Type) {
    // case library:
    // runAction(ActionCode.SHOW_LIBRARY);
    // break;
    // case networkLibrary:
    // runAction(ActionCode.SHOW_NETWORK_LIBRARY);
    // break;
    // case previousBook:
    // openBook(Collection.getRecentBook(1), null, null);
    // break;
    // case returnTo:
    // {
    // final Bookmark b = ((BookmarkDescription)description).Bookmark;
    // Collection.deleteBookmark(b);
    // gotoBookmark(b);
    // break;
    // }
    // case close:
    // closeWindow();
    // break;
    // }
    // }

    private synchronized void updateInvisibleBookmarksList(Bookmark b) {
        if (Model != null && Model.Book != null && b != null) {
            for (Bookmark bm : Collection.invisibleBookmarks(Model.Book)) {
                if (b.equals(bm)) {
                    Collection.deleteBookmark(bm);
                }
            }
            Collection.saveBookmark(b);
            final List<Bookmark> bookmarks = Collection.invisibleBookmarks(Model.Book);
            for (int i = 3; i < bookmarks.size(); ++i) {
                Collection.deleteBookmark(bookmarks.get(i));
            }
        }
    }

    public void addInvisibleBookmark(GBTextWordCursor cursor) {
        if (cursor != null && Model != null && Model.Book != null && getTextView() == BookTextView) {
            MiscUtil.convertInternalPositionToChpInWordNum(Model.getTextModel(), cursor);
            updateInvisibleBookmarksList(new Bookmark(Model.Book, getTextView().getModel().getId(), cursor, 6, false));
        }
    }

    public void addInvisibleBookmark() {
        if (Model.Book != null && getTextView() == BookTextView) {
            updateInvisibleBookmarksList(createBookmark(6, false));
        }
    }

    public Bookmark createBookmark(int maxLength, boolean visible) {
        final ReadView view = getTextView();
        final GBTextWordCursor cursor = view.getStartCursor();

        if (cursor.isNull()) {
            return null;
        }

        return new Bookmark(Model.Book, view.getModel().getId(), cursor, maxLength, visible);
    }

    /**
     * 功能描述： 获取当前阅读章节的目录树节点<br>
     * 创建者： jack<br>
     * 创建日期：2013-10-14<br>
     *
     * @return
     */
    public TOCTree getCurrentTOCElement() {
        return getTOCElementByPage(BookTextView.myCurrentPage);
        // final GBTextWordCursor cursor = BookTextView.getStartCursor();
        // if (Model == null || cursor == null) {
        // return null;
        // }
        // int index = BookTextView.myCurrentPage.getCurrentChpIndex();//
        // cursor.getChpFileIndex();
        // boolean isSlipByChapter = BookTextView.isSlipByChapter();
        // // 获取当前选中目录
        // TOCTree treeToSelect = null;
        // for (TOCTree tree : Model.TOCTree) {
        // final TOCTree.Reference reference = tree.getReference();
        // if (reference == null) {
        // continue;
        // }
        // if (isSlipByChapter) {
        // // epub目录判断
        // if (reference.ChpFileIndex > index) {
        // break;
        // }
        // treeToSelect = tree;
        // } else {
        // // txt目录判断
        // if (reference.ChpFileIndex > index) {
        // break;
        // } else if (reference.ChpFileIndex == index &&
        // cursor.getParagraphIndex() < reference.ParagraphIndex) {
        // break;
        // }
        // treeToSelect = tree;
        // }
        // }
        // return treeToSelect;
    }
    /**
     * 功能描述： 根据页面信息获取章节的目录树节点<br>
     * 创建者： jack<br>
     * 创建日期：2013-10-14<br>
     *
     * @return
     */
    public TOCTree getTOCElementByPage(final GBTextPage page) {
        final GBTextWordCursor cursor = page.StartCursor;
        if (Model == null || cursor == null) {
            return null;
        }
        int index = page.getCurrentChpIndex();// cursor.getChpFileIndex();
        boolean isSlipByChapter = BookTextView.isSlipByChapter();
        // 获取当前选中目录
        TOCTree treeToSelect = null;
        for (TOCTree tree : Model.TOCTree) {
            final TOCTree.Reference reference = tree.getReference();
            if (reference == null) {
                continue;
            }
            if (isSlipByChapter) {
                // epub目录判断
                if (reference.ChpFileIndex > index) {
                    break;
                }
                treeToSelect = tree;
            } else {
                // txt目录判断
                if (reference.ChpFileIndex > index) {
                    break;
                } else if (reference.ChpFileIndex == index && cursor.getParagraphIndex() < reference.ParagraphIndex) {
                    break;
                }
                treeToSelect = tree;
            }
        }
        return treeToSelect;
    }

    /**
     * 功能描述： 获取txt上一个/下一个目录<br>
     * 创建者： jack<br>
     * 创建日期：2015-1-7<br>
     *
     * @param isNext 是否下一个章节
     */
    public TOCTree getTxtTocTree(boolean isNext) {
        final GBTextWordCursor cursor = BookTextView.getStartCursor();
        if (Model == null || cursor == null) {
            return null;
        }
        int index = cursor.getChpFileIndex();
        // 获取当前选中目录
        TOCTree preTocTree = null;
        TOCTree treeToSelect = null;
        for (TOCTree tree : Model.TOCTree) {
            final TOCTree.Reference reference = tree.getReference();
            if (reference == null) {
                continue;
            }
            if (isNext) {
                preTocTree = treeToSelect;
                treeToSelect = tree;
                // txt目录判断
                if (reference.ChpFileIndex > index) {
                    break;
                } else if (reference.ChpFileIndex == index && cursor.getParagraphIndex() < reference.ParagraphIndex) {
                    break;
                }
            } else {
                // txt目录判断
                if (reference.ChpFileIndex > index) {
                    break;
                } else if (reference.ChpFileIndex == index && cursor.getParagraphIndex() < reference.ParagraphIndex) {
                    break;
                }
                preTocTree = treeToSelect;
                treeToSelect = tree;
            }

        }
        return isNext ? treeToSelect : preTocTree;
    }
    /**
     * 功能描述：获取章节信息<br>
     * 创建者： jack<br>
     * 创建日期：2013-10-14<br>
     *
     * @return
     */
    public String getChapterInfo() {
        final GBTextWordCursor cursor = BookTextView.getStartCursor();
        if (Model == null || cursor == null) {
            return "";
        }
        return (cursor.getChpFileIndex() + 1) + "/" + Model.getTextModel().getChapterSize();
    }

    @Override
    public String getCardDirectory() {
        return FileUtils.ROOT_PATH + File.separator;
    }

    //
    public boolean isReadPdf = false;
    private PdfReaderView mPdfReaderView;

    public PdfReaderView getmPdfReaderView() {
        return mPdfReaderView;
    }

    public void setmPdfReaderView(PdfReaderView mPdfReaderView) {
        this.mPdfReaderView = mPdfReaderView;
    }

    @Override
    public List<GBTextFixedPosition> getPageBookmark(GBTextWordCursor start, GBTextWordCursor end) {
        if (Model == null || Model.Book == null)
            return null;
        List<GBTextFixedPosition> list = new ArrayList<GBTextFixedPosition>();
        final List<Bookmark> bookmarks;

        if (GeeBookLoader.getBookMgr() == null) {
            bookmarks = Collection.visibleBookmarks(Model.Book);
        } else {
            try {
                bookmarks = GeeBookLoader.getBookMgr().visibleBookmarks();
            } catch (TipException e) {
                e.toast(((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity());
                // to do uiutil
                return null;
            }
        }

        if (null == bookmarks) {
            return null;
        }
        for (Bookmark bm : bookmarks) {
            if (bm.ChpFileIndex == start.getChpFileIndex()) {
                if (start.getParagraphIndex() < bm.ParagraphIndex && bm.ParagraphIndex < end.getParagraphIndex())
                    list.add(bm);
                else if (start.getParagraphIndex() == bm.ParagraphIndex && bm.ElementIndex >= start.getElementIndex())
                    list.add(bm);
                else if (end.getParagraphIndex() == bm.ParagraphIndex && bm.ElementIndex <= end.getElementIndex())
                    list.add(bm);
                else if (start.getParagraphIndex() == bm.ParagraphIndex && bm.ParagraphIndex == end.getElementIndex()
                        && bm.ElementIndex > start.getElementIndex() && bm.ElementIndex < end.getElementIndex())
                    list.add(bm);
            }
        }
        return list;
    }

    /**
     * 功能描述：加载高亮，笔记等信息<br>
     * 创建者： jack<br>
     * 创建日期：2013-8-15<br>
     */
    private int mCurrentChpIndex = -1;

    public void setmCurrentChpIndex(int mCurrentChpIndex) {
        this.mCurrentChpIndex = mCurrentChpIndex;
    }

    public void loadAnnotation(int chpIndex, boolean isRepaint) {
        if (Model == null || Model.Book == null || chpIndex == mCurrentChpIndex || BookTextView == null) {
            return;
        }
        mCurrentChpIndex = chpIndex;
        List<GBTextHighlighting> list1 = new ArrayList<GBTextHighlighting>();
        List<GBTextAnnotation> list2 = new ArrayList<GBTextAnnotation>();
        List<Annotations> mAnnotationList = null;
        if (GeeBookLoader.getBookMgr() == null) {
            mAnnotationList = Collection.loadAnotations((int) Model.Book.getId());
        } else {

            try {
                mAnnotationList = GeeBookLoader.getBookMgr().loadAnotations(chpIndex,Model.Book.getId());
            } catch (TipException e) {
                // todo uiutil
                e.toast(((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity());

            }
        }

        if (null == mAnnotationList) {
            return;
        }
        for (Annotations ann : mAnnotationList) {
            if (ann.annotationType == null)
                continue;

            if (ann.annotationType.equals(Annotations.HIGHLIGHT)) {
                // GBTextHighlighting gbh = ann.getHighlighting();
                // if(gbh.getStartPosition().getElementIndex() == -1 ||
                // gbh.getStartPosition().getCharIndex() == -1){
                // MiscUtil.convertChpInWordNumToInternalPosition(get,
                // position);
                // }
                list1.add(ann.getHighlighting());
            } else if (ann.annotationType.equals(Annotations.ANNOTATION) || ann.annotationType.equals(Annotations.NOTE)) {
                list2.add(ann.getAnnotation());
            }
        }
        BookTextView.setupHighligth(list1);
        BookTextView.setupAnnotation(list2);
        if (isRepaint) {
            getViewImp().reset();
            getViewImp().repaint();
        }
    }

    @Override
    public void getReadProgress(IFunction<Integer> progressChangeHandler) {
        try {
            Model.Book.getPlugin().getReadProgress(progressChangeHandler);
        } catch (BookReadingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    /**
     * 功能描述：设置音量键是否控制翻页 <br>
     * 创建者： jack<br>
     * 创建日期：2015-1-22<br>
     *
     * @param
     */
    public void setVolumePageEnable(boolean isEnable) {
        if (isEnable && !isActionEnabled(myBindings.getBinding(KeyEvent.KEYCODE_VOLUME_UP, false))) {
            addAction(ActionCode.VOLUME_KEY_SCROLL_FORWARD, new VolumeKeyTurnPageAction(this, true));
            addAction(ActionCode.VOLUME_KEY_SCROLL_BACK, new VolumeKeyTurnPageAction(this, false));
        } else if (!isEnable && hasActionForKey(KeyEvent.KEYCODE_VOLUME_UP, false)) {
            removeAction(ActionCode.VOLUME_KEY_SCROLL_FORWARD);
            removeAction(ActionCode.VOLUME_KEY_SCROLL_BACK);
        }
    }
}
