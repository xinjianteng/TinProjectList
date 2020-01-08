package com.tin.projectlist.app.library.reader.view;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


import com.tin.projectlist.app.library.reader.R;
import com.tin.projectlist.app.library.reader.parser.common.util.IFunction;
import com.tin.projectlist.app.library.reader.pdf.PdfActivity;

import java.io.File;
import java.util.List;

/**
 * 阅读底部菜单
 *
 * @author Shikh
 * @time 16-8-31 下午5:06
 */

public class ReadBottomMenu implements OnClickListener, AnimationListener, OnSeekBarChangeListener, IFunction<Integer> {

    private BaseMenuActivity mActivity;

    private Animation mOutAnim;
    private Animation mInAnim;
    private Animation mRightOutAnim;
    private Animation mRightInAnim;
    public boolean mIsDismess = true;
    private boolean mIsLock = false; // 锁定（避免快速点击）

    private int mStepSize = 5; // 进度条改变步长
    // 当前操作
    private OPREATION mCurrenOpre = OPREATION.GOTO;

    enum OPREATION {// 操作类型
        // 页面跳转,亮度调节,字体设置,设置
        GOTO, LIGHT, FONT, SET
    }

    private String[] fontDownLoadPath = new String[]{"download/yahei.ttf", "download/songti.ttf", "download/youyuan.ttf"};
    private String[] fontDownLoadName = new String[]{"yahei.ttf", "songti.ttf", "youyuan.ttf"};
    private String[] fontName = new String[]{"微软雅黑", "宋体", "幼圆", "默认"};

    public ReadBottomMenu(BaseMenuActivity activity) {
        this.mActivity = activity;
        initMenu();
    }

    /**
     * 初始化菜单控件
     */
    private void initMenu() {
        if (mActivity instanceof PdfActivity) {
            initPdfMenuView();
        } else {
            initMainView();
        }
        mRightOutAnim = AnimationUtils.loadAnimation(mActivity, R.anim.read_bottom_menu_from_right_out);
        mOutAnim = AnimationUtils.loadAnimation(mActivity, R.anim.read_bottom_menu_out);
        mOutAnim.setAnimationListener(this);
        // 消失
        mInAnim = AnimationUtils.loadAnimation(mActivity, R.anim.read_bottom_menu_in);
        mRightInAnim = AnimationUtils.loadAnimation(mActivity, R.anim.read_bottom_menu_from_right_in);
    }


    public View mMainView; // 底部菜单view对象
    private RadioButton mLandspace;
    public SeekBar mPDFSeekBar;// 进度栏

    private void initPdfMenuView() {
        mMainView = mActivity.getLayoutInflater().inflate(R.layout.read_pdf_bottom, null);
        mLandspace = (RadioButton) mMainView.findViewById(R.id.rb_reader_landspace);
        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLandspace.setText("横屏");
        }
        mLandspace.setOnClickListener(this);
        mPDFSeekBar = (SeekBar) mMainView.findViewById(R.id.sb_goto_bar);
        // mPages = (TextView) mMainView.findViewById(R.id.tv_read_pages);
        mPDFSeekBar.setOnSeekBarChangeListener(this);
        mMainView.setVisibility(View.GONE);
    }

    // ==============普通菜单初始化区==================
    protected LinearLayout mChangeBody; // 可切换的布局
    private RadioImageView mCatalog; // 目录
    public RadioImageView mFontSet; // 字体设置
    public RadioImageView mNight;//夜间模式
    public RadioImageView mLight; // 亮度
    private ImageView mBottom_write_notes;//写笔记

    //初始化底部菜单
    private void initMainView() {
        mMainView = mActivity.getLayoutInflater().inflate(R.layout.read_bottom_menu, null);
        mChangeBody = (LinearLayout) mMainView.findViewById(R.id.ll_reader_bottom_body);
        mCatalog = (RadioImageView) mMainView.findViewById(R.id.rb_reader_catalog);//目录
        mCatalog.setOnClickListener(this);
        mFontSet = (RadioImageView) mMainView.findViewById(R.id.rb_reader_font);//字体
        mFontSet.setOnClickListener(this);
        mNight = (RadioImageView) mMainView.findViewById(R.id.rb_reader_night);//章节
        mNight.setOnClickListener(this);
        mLight = (RadioImageView) mMainView.findViewById(R.id.rb_reader_light);//亮度
        mLight.setOnClickListener(this);
        mBottom_write_notes = (ImageView) mMainView.findViewById(R.id.bottom_write_notes);
        mBottom_write_notes.setOnClickListener(this);
        mMainView.setVisibility(View.GONE);
        initmNightView();
        initmLightView();
        initmFontSetView();
    }

    /**
     * 初始化章节设置
     */
    public SeekBar mNightSeekBar;// 进度栏
    private View type;
    public TextView tv_read_pages;
    private LinearLayout ll_perious;
    private LinearLayout ll_next;

    private void initmNightView() {
        type = mActivity.getLayoutInflater().inflate(R.layout.type, null);
        mNightSeekBar = (SeekBar) type.findViewById(R.id.type_sb_goto_bar);
        mNightSeekBar.setOnSeekBarChangeListener(this);
        tv_read_pages = (TextView) type.findViewById(R.id.tv_read_pages);
        ll_perious = (LinearLayout) type.findViewById(R.id.ll_perious);
        ll_next = (LinearLayout) type.findViewById(R.id.ll_next);
        ll_perious.setOnClickListener(this);
        ll_next.setOnClickListener(this);
    }

    /**
     * 初始化亮度设置
     */
    public SeekBar mLightSeekBar;// 进度栏
    private View light;

    private void initmLightView() {
        light = mActivity.getLayoutInflater().inflate(R.layout.light, null);
        mLightSeekBar = (SeekBar) light.findViewById(R.id.light_sb_goto_bar);
        mLightSeekBar.setOnSeekBarChangeListener(this);
    }

    /**
     * 初始化字体设置
     */
    private View font;
    private ImageView read_bg_01, read_bg_02, read_bg_03, read_bg_04;//背景
    private LinearLayout ll_iv_01, ll_iv_02, ll_iv_03, ll_iv_04;//背景
    private ImageView iv_lts_01, iv_lts_02, iv_lts_03, iv_lts_04;//对齐
    private LinearLayout ll_lts_01, ll_lts_02, ll_lts_03, ll_lts_04;//对齐
    private ImageView iv_font_01, iv_font_02, iv_font_03, iv_font_04;//字号
    private LinearLayout ll_font_01, ll_font_02, ll_font_03, ll_font_04;//字号
    private LinearLayout ll_font_more;//选择字体的布局
    private LinearLayout ll_font_more_btn;//选择字体的选择器
    private RelativeLayout font_default, font_microsoft, font_ltalics, font_youyuan;
    private RelativeLayout rel_download_02, rel_download_03, rel_download_04;//下载字体布局
    private TextView tv_download_02, tv_download_03, tv_download_04;//下载字体的按钮
    private TextView btn_download_02, btn_download_03, btn_download_04;//下载字体的按钮
    private ProgressBar progrwwssbar_download_02, progrwwssbar_download_03, progrwwssbar_download_04;//下载字体的按钮
    public String fontUrl;
    private GBStringOption mFontOption;
    public String currentFont;
    private TextView isfont_tv;
    private RelativeLayout rl_font_more;

    private void initmFontSetView() {

        font = mActivity.getLayoutInflater().inflate(R.layout.bar, null);

        //换背景
        read_bg_01 = (ImageView) font.findViewById(R.id.read_bg_01);
        read_bg_02 = (ImageView) font.findViewById(R.id.read_bg_02);
        read_bg_03 = (ImageView) font.findViewById(R.id.read_bg_03);
        read_bg_04 = (ImageView) font.findViewById(R.id.read_bg_04);
        ll_iv_01 = (LinearLayout) font.findViewById(R.id.ll_iv_01);
        ll_iv_02 = (LinearLayout) font.findViewById(R.id.ll_iv_02);
        ll_iv_03 = (LinearLayout) font.findViewById(R.id.ll_iv_03);
        ll_iv_04 = (LinearLayout) font.findViewById(R.id.ll_iv_04);
        ll_iv_01.setOnClickListener(mSetButtonListener);
        ll_iv_02.setOnClickListener(mSetButtonListener);
        ll_iv_03.setOnClickListener(mSetButtonListener);
        ll_iv_04.setOnClickListener(mSetButtonListener);
        //对其方式
        iv_lts_01 = (ImageView) font.findViewById(R.id.iv_lts_01);
        iv_lts_02 = (ImageView) font.findViewById(R.id.iv_lts_02);
        iv_lts_03 = (ImageView) font.findViewById(R.id.iv_lts_03);
        iv_lts_04 = (ImageView) font.findViewById(R.id.iv_lts_04);
        ll_lts_01 = (LinearLayout) font.findViewById(R.id.ll_lts_01);
        ll_lts_02 = (LinearLayout) font.findViewById(R.id.ll_lts_02);
        ll_lts_03 = (LinearLayout) font.findViewById(R.id.ll_lts_03);
        ll_lts_04 = (LinearLayout) font.findViewById(R.id.ll_lts_04);
        ll_lts_01.setOnClickListener(mSetButtonListener);
        ll_lts_02.setOnClickListener(mSetButtonListener);
        ll_lts_03.setOnClickListener(mSetButtonListener);
        ll_lts_04.setOnClickListener(mSetButtonListener);
        //字号
        iv_font_01 = (ImageView) font.findViewById(R.id.iv_font_01);
        iv_font_02 = (ImageView) font.findViewById(R.id.iv_font_02);
        iv_font_03 = (ImageView) font.findViewById(R.id.iv_font_03);
        iv_font_04 = (ImageView) font.findViewById(R.id.iv_font_04);
        ll_font_01 = (LinearLayout) font.findViewById(R.id.ll_font_01);
        ll_font_02 = (LinearLayout) font.findViewById(R.id.ll_font_02);
        ll_font_03 = (LinearLayout) font.findViewById(R.id.ll_font_03);
        ll_font_04 = (LinearLayout) font.findViewById(R.id.ll_font_04);

        ll_font_01.setOnClickListener(mSetButtonListener);
        ll_font_02.setOnClickListener(mSetButtonListener);
        ll_font_03.setOnClickListener(mSetButtonListener);
        ll_font_04.setOnClickListener(mSetButtonListener);

        ll_font_more_btn = (LinearLayout) font.findViewById(R.id.ll_font_more_btn);
        ll_font_more_btn.setOnClickListener(mSetButtonListener);
        rl_font_more = (RelativeLayout) font.findViewById(R.id.rl_font_more);
        rl_font_more.setOnClickListener(mSetButtonListener);

        isfont_tv = (TextView) font.findViewById(R.id.isfont_tv);

        //字体选择布局 mFontButtonListener
        ll_font_more = (LinearLayout) font.findViewById(R.id.ll_font_more);
        ll_font_more.setVisibility(View.GONE);
        font_default = (RelativeLayout) ll_font_more.findViewById(R.id.font_default);
        font_microsoft = (RelativeLayout) ll_font_more.findViewById(R.id.font_microsoft);
        font_ltalics = (RelativeLayout) ll_font_more.findViewById(R.id.font_ltalics);
        font_youyuan = (RelativeLayout) ll_font_more.findViewById(R.id.font_youyuan);
        rel_download_02 = (RelativeLayout) ll_font_more.findViewById(R.id.rel_download_02);
        rel_download_03 = (RelativeLayout) ll_font_more.findViewById(R.id.rel_download_03);
        rel_download_04 = (RelativeLayout) ll_font_more.findViewById(R.id.rel_download_04);
        btn_download_02 = (TextView) ll_font_more.findViewById(R.id.btn_download_02);
        btn_download_03 = (TextView) ll_font_more.findViewById(R.id.btn_download_03);
        btn_download_04 = (TextView) ll_font_more.findViewById(R.id.btn_download_04);
        tv_download_02 = (TextView) ll_font_more.findViewById(R.id.tv_download_02);
        tv_download_03 = (TextView) ll_font_more.findViewById(R.id.tv_download_03);
        tv_download_04 = (TextView) ll_font_more.findViewById(R.id.tv_download_04);
        progrwwssbar_download_02 = (ProgressBar) ll_font_more.findViewById(R.id.progrwwssbar_download_02);
        progrwwssbar_download_03 = (ProgressBar) ll_font_more.findViewById(R.id.progrwwssbar_download_03);
        progrwwssbar_download_04 = (ProgressBar) ll_font_more.findViewById(R.id.progrwwssbar_download_04);


    }
    public void closeOtherView() {
        mChangeBody.removeAllViews();
        mNight.setChecked(false);
        mFontSet.setChecked(false);
        mLight.setChecked(false);
    }
    // ============初始化epub/txt跳转框===============
    private void initGoToBar(int p) {
        mMainView.startAnimation(mOutAnim);
        mChangeBody.removeAllViews();
        switch (p) {
            case 1:
                mNight.setChecked(false);
                mFontSet.setChecked(false);
                mLight.setChecked(false);
                break;
            case 2:
                if (mNight.isChecked()) {
                    mNight.setChecked(false);
                } else {
                    mNightSeekBar.setProgress((int) (mActivity.getReadPro() * 100));
                    mMainView.startAnimation(mInAnim);
                    mChangeBody.addView(type);
                    mNight.setChecked(true);
                }
                mFontSet.setChecked(false);
                mLight.setChecked(false);
                break;
            case 3:
                if (mLight.isChecked()) {
                    mLight.setChecked(false);
                } else {
                    int pro = GBLibrary.Instance().getScreenBrightness();
                    mLightSeekBar.setProgress(pro);
                    mMainView.startAnimation(mInAnim);
                    mChangeBody.addView(light);
                    mLight.setChecked(true);
                }
                mFontSet.setChecked(false);
                mNight.setChecked(false);
                break;
            case 4:
                mFontOption = GBTextStyleCollection.Instance().getBaseStyle().FontFamilyOption;
                currentFont = mFontOption.getValue();
                fontUrl = null;
                String name = isFont();
                isfont_tv.setText(name);
                ll_font_more.setVisibility(View.GONE);
                if (mFontSet.isChecked()) {
                    mFontSet.setChecked(false);
                } else {
                    ReaderApplication application = (ReaderApplication) GBApplication.Instance();
                    if (application.getColorProfileName().equals(ColorProfile.DAY)) {
                        switch (application.mDayModel) {
                            case Day:
                                setReadBackgroudUnChecked(2);
                                break;
                            case FlaxBrown:
                                setReadBackgroudUnChecked(3);
                                break;
                            case SleepKin:
                                setReadBackgroudUnChecked(1);
                                break;
                            case SimpleBrown:
                                setReadBackgroudUnChecked(4);
                                break;
                        }
                    }
                    int va = application.RightMarginOption.getValue();
                    switch (va) {
                        case 19:
                            setReadLtsUnChecked(4);
                            break;
                        case 20:
                            setReadLtsUnChecked(3);
                            break;
                        case 21:
                            setReadLtsUnChecked(2);
                            break;
                        case 30:
                            setReadLtsUnChecked(1);
                            break;
                    }
                    GBIntegerRangeOption option = GBTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
                    int min = option.MinValue;
                    int max = option.MaxValue;
                    if (option.getValue() == min) {
                        setReadFontUnChecked(1);
                    } else if (option.getValue() == min + ((max - min) / 3)) {
                        setReadFontUnChecked(2);
                    } else if (option.getValue() == max - ((max - min) / 3)) {
                        setReadFontUnChecked(3);
                    } else if (option.getValue() == max) {
                        setReadFontUnChecked(4);
                    }
                    mMainView.startAnimation(mInAnim);
                    mChangeBody.addView(font);
                    mFontSet.setChecked(true);
                }
                mNight.setChecked(false);
                mLight.setChecked(false);
                break;
        }
    }



    /**
     * 功能描述： 呼出菜单<br>
     * 创建者： jack<br>
     * 创建日期：2012-11-22<br>
     *
     * @param
     */
    public void showOrDismess() {
        if (mIsLock)
            return;
        if (mActivity.mApplication.isReadPdf) {// PDF阅读模式
            if (mMainView.getVisibility() == View.VISIBLE) {
                mIsDismess = true;// 隐藏菜单
                mMainView.startAnimation(mOutAnim);
            } else {
                mIsDismess = false;// 显示菜单
                int ori = mActivity.getWindowManager().getDefaultDisplay().getOrientation();
                mLandspace.setText(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE == ori ? "横屏" : "竖屏");
                mPDFSeekBar.setProgress((int) (mActivity.getReadPro() * 100));
                // mPages.setText("(" + mActivity.getProPage() + ")");
                // mPages.setVisibility(View.VISIBLE);
                mCurrenOpre = OPREATION.GOTO;
                mMainView.setVisibility(View.VISIBLE);
                mMainView.startAnimation(mInAnim);
            }
        } else {
            ll_font_more.setVisibility(View.GONE);
            if (mMainView.getVisibility() == View.VISIBLE) {
                mIsDismess = true;// 隐藏菜单
                initGoToBar(1);
                mMainView.setVisibility(View.GONE);
                mMainView.startAnimation(mOutAnim);
            } else {
                mIsDismess = false;// 显示菜单
                mMainView.setVisibility(View.VISIBLE);
                mMainView.startAnimation(mInAnim);
            }

        }
    }


    private void resetReaderConfig() {
        mActivity.mApplication.clearTextCaches();
        mActivity.mWidget.reset();
        mActivity.mWidget.repaint();
    }

    @Override
    public void onClick(View v) {
        mActivity.closeView(1);
        if (mActivity.mApplication.isReadPdf) {
            if (v.getId() == R.id.rb_reader_landspace) {
                MobclickAgent.onEvent(mActivity, EventName.READER_PDF_SECREENSETING);
                RadioButton rdoBtn = (RadioButton) v;
                Cookie cookie = new Cookie(mActivity, Cookie.APP_CFG);
                if (rdoBtn.getText().toString().trim().equals("横屏")) {
                    if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        rdoBtn.setText("竖屏");
                        cookie.putVal(PdfActivity.SCREEN_ORIENT, true);
                    }
                } else {
                    if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        rdoBtn.setText("横屏");
                        cookie.putVal(PdfActivity.SCREEN_ORIENT, false);
                    }
                }
            }
            return;
        }
        if (v.getId() == R.id.rb_reader_catalog) {// 目录
            initGoToBar(1);
            MobclickAgent.onEvent(mActivity, EventName.READER_SHOWCATALOG);
            mActivity.showDisMenu();
            if (mActivity.mApplication.isReadPdf) {
                if (mActivity.isHaveCatalog()) {
                    CatalogAndMarkActivity.actionView(mActivity, mActivity.menuRoot, (byte) 1);
                } else {
                    UIUtil.showMessageText(mActivity, GBResource.resource("readerPage").getResource("noBookCatalog")
                            .getValue());
                }
            } else {
                final ReaderApplication fbreader = (ReaderApplication) GBApplication.Instance();
                if (fbreader.Model.TOCTree.getSize() > 1) {
                    CatalogAndMarkActivity.actionView(mActivity, fbreader.Model.TOCTree, (byte) 2);
                } else {
                    UIUtil.showMessageText(mActivity, isPageing ? "正在提取目录" : GBResource.resource("readerPage")
                            .getResource("noBookCatalog").getValue());
                }
            }
        } else if (v.getId() == R.id.rb_reader_font) {// 字体
            MobclickAgent.onEvent(mActivity, EventName.READER_FONTSETTING);
            initGoToBar(4);
            mCurrenOpre = OPREATION.FONT;
        } else if (v.getId() == R.id.rb_reader_night) {// 夜间模式
            MobclickAgent.onEvent(mActivity, EventName.READER_READMODEL);
            initGoToBar(2);
            mCurrenOpre = OPREATION.SET;
        } else if (v.getId() == R.id.rb_reader_light) {// 亮度
            MobclickAgent.onEvent(mActivity, EventName.READER_SECREENLIGHT);
            initGoToBar(3);
            mCurrenOpre = OPREATION.LIGHT;
        } else if (v.getId() == R.id.bottom_write_notes) {//写笔记
            GeeBookMgr geebooMgr = GeeBookLoader.getBookMgr();
            int b = geebooMgr.getHoldStatus();
            if (b == 1 || b == 2 || b == 3 | b == 4) {
            } else {
                Toast.makeText(mActivity, "本书还未同步到云端，无法进行此操作", Toast.LENGTH_SHORT).show();
                return;
            }
            int type = 1;
            mActivity.mApplication.runAction(ActionCode.SELECTION_NOTE_ANNOTATION, 0, type);
        } else if (v.getId() == R.id.ll_next) {
            if (!mActivity.mApplication.BookTextView.nextChapter())
                UIUtil.showMessageText(mActivity,
                        GBResource.resource("readerPage").getResource("isLastChapter").getValue());
            mActivity.getmWidget().postInvalidate();
            GBApplication.Instance().runAction(ActionCode.RESET_PAGEINFO);
        } else if (v.getId() == R.id.ll_perious) {
            if (!mActivity.mApplication.BookTextView.preChapter()) {
                UIUtil.showMessageText(mActivity,
                        GBResource.resource("readerPage").getResource("isFristChapter").getValue());
            }
            mActivity.getmWidget().postInvalidate();
            GBApplication.Instance().runAction(ActionCode.RESET_PAGEINFO);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mIsLock = true;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mIsLock = false;
        if (mIsDismess)
            mMainView.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (mCurrenOpre) {
            case GOTO:
                int total = mActivity.mApplication.getCurrentView().getTotalPage();
                // mPages.setText((progress == 0 ? 1 : total * progress / 100) +
                // "/" + total);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 滑块托动处理
        int i = seekBar.getId();
        if (i == R.id.type_sb_goto_bar) {
            if (mActivity.mApplication.getCurrentView().isLoading()) {
                UIUtil.showMessageText(mActivity, GBResource.resource("readerPage").getResource("loadingPlease")
                        .getValue());
            } else {
                MobclickAgent.onEvent(mActivity, EventName.READER_GOTO);
                ReaderApplication.Instance().runAction(ActionCode.GOTO_PAGE, mNightSeekBar.getProgress());
            }
        } else if (i == R.id.light_sb_goto_bar) {
            ReaderApplication.Instance().runAction(ActionCode.SCREEN_LIGHT, mLightSeekBar.getProgress());
        } else if (i == R.id.sb_goto_bar) {
            if (mActivity.mApplication.getCurrentView().isLoading()) {
                UIUtil.showMessageText(mActivity, GBResource.resource("readerPage").getResource("loadingPlease")
                        .getValue());
            } else {
                MobclickAgent.onEvent(mActivity, EventName.READER_GOTO);
                ReaderApplication.Instance().runAction(ActionCode.GOTO_PAGE, mPDFSeekBar.getProgress());
            }
        }
    }


    boolean isPageing = false;


    View.OnClickListener mSetButtonListener = new OnClickListener() {//底部字体的监听

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ll_iv_01) {
                setReadBackgroudUnChecked(1);
                ((ReaderApplication) GBApplication.Instance()).setColorProfileName(ColorProfile.DAY, DayModel.SleepKin);
                resetReaderConfig();
            } else if (v.getId() == R.id.ll_iv_02) {
                setReadBackgroudUnChecked(2);
                ((ReaderApplication) GBApplication.Instance()).setColorProfileName(ColorProfile.DAY, DayModel.Day);
                resetReaderConfig();
            } else if (v.getId() == R.id.ll_iv_03) {
                setReadBackgroudUnChecked(3);
                ((ReaderApplication) GBApplication.Instance()).setColorProfileName(ColorProfile.DAY, DayModel.FlaxBrown);
                resetReaderConfig();
            } else if (v.getId() == R.id.ll_iv_04) {
                setReadBackgroudUnChecked(4);
                ((ReaderApplication) GBApplication.Instance()).setColorProfileName(ColorProfile.DAY, DayModel.SimpleBrown);
                resetReaderConfig();
            } else if (v.getId() == R.id.ll_lts_01) {// 常规
                GBTextStyleCollection.Instance().getBaseStyle().LeftIndentOption.setValue(0);// 段落缩进
                GBTextStyleCollection.Instance().getBaseStyle().LineSpaceOption.setValue(10);
                GBTextStyleCollection.Instance().getBaseStyle().ParaSpaceOption.setValue(0);
                ((ReaderApplication) GBApplication.Instance()).TopMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).BottomMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).LeftMarginOption.setValue(30);
                ((ReaderApplication) GBApplication.Instance()).RightMarginOption.setValue(30);
                setReadLtsUnChecked(1);
                resetReaderConfig();
            } else if (v.getId() == R.id.ll_lts_02) {// 常规、缩进
                GBTextStyleCollection.Instance().getBaseStyle().LeftIndentOption.setValue(25);// 段落缩进
                GBTextStyleCollection.Instance().getBaseStyle().LineSpaceOption.setValue(10);
                GBTextStyleCollection.Instance().getBaseStyle().ParaSpaceOption.setValue(0);
                ((ReaderApplication) GBApplication.Instance()).TopMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).BottomMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).LeftMarginOption.setValue(21);
                ((ReaderApplication) GBApplication.Instance()).RightMarginOption.setValue(21);
                setReadLtsUnChecked(2);
                resetReaderConfig();
            } else if (v.getId() == R.id.ll_lts_03) {// 段间距、缩进
                GBTextStyleCollection.Instance().getBaseStyle().LeftIndentOption.setValue(25);// 段落缩进
                GBTextStyleCollection.Instance().getBaseStyle().LineSpaceOption.setValue(10);
                GBTextStyleCollection.Instance().getBaseStyle().ParaSpaceOption.setValue(15);
                ((ReaderApplication) GBApplication.Instance()).TopMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).BottomMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).LeftMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).RightMarginOption.setValue(20);
                setReadLtsUnChecked(3);
                resetReaderConfig();
            } else if (v.getId() == R.id.ll_lts_04) {// 行间距、缩进
                GBTextStyleCollection.Instance().getBaseStyle().LeftIndentOption.setValue(25);// 段落缩进
                GBTextStyleCollection.Instance().getBaseStyle().LineSpaceOption.setValue(20);
                GBTextStyleCollection.Instance().getBaseStyle().ParaSpaceOption.setValue(0);
                ((ReaderApplication) GBApplication.Instance()).TopMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).BottomMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).LeftMarginOption.setValue(19);
                ((ReaderApplication) GBApplication.Instance()).RightMarginOption.setValue(19);
                setReadLtsUnChecked(4);
                resetReaderConfig();
            } else if (v.getId() == R.id.ll_font_01) {
                GBIntegerRangeOption option = GBTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
                ReaderApplication.Instance().runAction(ActionCode.INCREASE_FONT, option.MinValue);
                setReadFontUnChecked(1);
            } else if (v.getId() == R.id.ll_font_02) {
                GBIntegerRangeOption option = GBTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
                int min = option.MinValue;
                int max = option.MaxValue;
                int i = min + ((max - min) / 3);
                ReaderApplication.Instance().runAction(ActionCode.INCREASE_FONT, i);
                setReadFontUnChecked(2);
            } else if (v.getId() == R.id.ll_font_03) {
                GBIntegerRangeOption option = GBTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
                int min = option.MinValue;
                int max = option.MaxValue;
                int i = max - ((max - min) / 3);
                ReaderApplication.Instance().runAction(ActionCode.INCREASE_FONT, i);
                setReadFontUnChecked(3);
            } else if (v.getId() == R.id.ll_font_04) {
                GBIntegerRangeOption option = GBTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
                ReaderApplication.Instance().runAction(ActionCode.INCREASE_FONT, option.MaxValue);
                setReadFontUnChecked(4);
            } else if (v.getId() == R.id.ll_font_more_btn || v.getId() == R.id.rl_font_more) {

                localFamiliesList = AndroidFontUtil.getLocalFamiliesList();
                btn_download_02.setText("点击下载");
                btn_download_03.setText("点击下载");
                btn_download_04.setText("点击下载");
                btn_download_02.setTextColor(mActivity.getResources().getColor(R.color.font_right_01));
                tv_download_02.setTextColor(mActivity.getResources().getColor(R.color.font_left_01));
                btn_download_03.setTextColor(mActivity.getResources().getColor(R.color.font_right_01));
                tv_download_03.setTextColor(mActivity.getResources().getColor(R.color.font_left_01));
                btn_download_04.setTextColor(mActivity.getResources().getColor(R.color.font_right_01));
                tv_download_04.setTextColor(mActivity.getResources().getColor(R.color.font_left_01));
                font_default.setOnClickListener(mFontButtonListener);//默认
                font_microsoft.setOnClickListener(null);
                font_ltalics.setOnClickListener(null);
                font_youyuan.setOnClickListener(null);
                rel_download_02.setOnClickListener(mFontButtonListener);
                rel_download_03.setOnClickListener(mFontButtonListener);
                rel_download_04.setOnClickListener(mFontButtonListener);
                if (localFamiliesList.size() > 1) {
                    for (int i = 0; i < localFamiliesList.size(); i++) {
                        for (int j = 0; j < fontDownLoadName.length; j++) {
                            if (fontDownLoadName[j].contains(localFamiliesList.get(i).fontName)) {
                                if (j == 0) {
                                    btn_download_02.setText("已下载");
                                    btn_download_02.setTextColor(mActivity.getResources().getColor(R.color.font_right_02));
                                    tv_download_02.setTextColor(mActivity.getResources().getColor(R.color.font_left_02));
                                    font_microsoft.setOnClickListener(mFontButtonListener);
                                    rel_download_02.setOnClickListener(null);
                                } else if (j == 1) {
                                    btn_download_03.setText("已下载");
                                    btn_download_03.setTextColor(mActivity.getResources().getColor(R.color.font_right_02));
                                    tv_download_03.setTextColor(mActivity.getResources().getColor(R.color.font_left_02));
                                    font_ltalics.setOnClickListener(mFontButtonListener);
                                    rel_download_03.setOnClickListener(null);
                                } else if (j == 2) {
                                    btn_download_04.setText("已下载");
                                    btn_download_04.setTextColor(mActivity.getResources().getColor(R.color.font_right_02));
                                    tv_download_04.setTextColor(mActivity.getResources().getColor(R.color.font_left_02));
                                    font_youyuan.setOnClickListener(mFontButtonListener);
                                    rel_download_04.setOnClickListener(null);
                                }
                            }
                        }
                    }
                }
                ll_font_more.startAnimation(mRightInAnim);
                ll_font_more.setVisibility(View.VISIBLE);
            }
        }
    };
    private List<TypeFace> localFamiliesList;
    View.OnClickListener mFontButtonListener = new OnClickListener() {//字体选择的监听
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.font_default) {
                setFont("", fontName[3]);
            } else if (v.getId() == R.id.font_microsoft) {
                setFont(fontDownLoadName[0], fontName[0]);
            } else if (v.getId() == R.id.font_ltalics) {
                setFont(fontDownLoadName[1], fontName[1]);
            } else if (v.getId() == R.id.font_youyuan) {
                setFont(fontDownLoadName[2], fontName[2]);
            } else if (v.getId() == R.id.rel_download_02) {
                if (isLoading != 0) {
                    Toast.makeText(mActivity, "正在下载字体", Toast.LENGTH_SHORT).show();
                    return;
                }
                isLoading = 2;
                final File itemFontFile = new File(FileUtils.getFONT() + File.separator + fontDownLoadName[0]);
                GeeBookLoader.getBookMgr().downloadFontFile(fontDownLoadPath[0], itemFontFile.getAbsolutePath(),
                        false, "正在下载" + "字体", new IFunction<Integer>() {
                            @Override
                            public void callback(Integer nowProgress) {
                                Message message = new Message();
                                message.what = nowProgress;
                                progressHander.sendMessage(message);
                            }
                        });
            } else if (v.getId() == R.id.rel_download_03) {
                if (isLoading != 0) {
                    Toast.makeText(mActivity, "正在下载字体", Toast.LENGTH_SHORT).show();
                    return;
                }
                isLoading = 3;
                final File itemFontFile = new File(FileUtils.FONT + File.separator + fontDownLoadName[1]);
                GeeBookLoader.getBookMgr().downloadFontFile(fontDownLoadPath[1], itemFontFile.getAbsolutePath(),
                        false, "正在下载" + "字体", new IFunction<Integer>() {
                            @Override
                            public void callback(Integer nowProgress) {
                                Message message = new Message();
                                message.what = nowProgress;
                                progressHander.sendMessage(message);
                            }
                        });
            } else if (v.getId() == R.id.rel_download_04) {
                if (isLoading != 0) {
                    Toast.makeText(mActivity, "正在下载字体", Toast.LENGTH_SHORT).show();
                    return;
                }
                isLoading = 4;
                final File itemFontFile = new File(FileUtils.FONT + File.separator + fontDownLoadName[2]);
                GeeBookLoader.getBookMgr().downloadFontFile(fontDownLoadPath[2], itemFontFile.getAbsolutePath(),
                        false, "正在下载" + "字体", new IFunction<Integer>() {
                            @Override
                            public void callback(Integer nowProgress) {
                                Message message = new Message();
                                message.what = nowProgress;
                                progressHander.sendMessage(message);
                            }
                        });
            }
        }
    };
    // 阅读器底部菜单进度条
    Handler mProgressHander = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == OEBBookReader.CALL_BOOK_OVER) {
                mActivity.resetPages();
            } else if (msg.what < OEBBookReader.CALL_BOOK_OVER) {
                // mPages.setText(isPageing ? "正在分页..." : "正在提取目录...");
            } else {
//                mReading.setProgress(msg.what);
            }
        }
    };
    private int isLoading;

    @Override
    public void callback(final Integer result) {
        if (result < OEBBookReader.CALL_BOOK_OVER) {
            isPageing = result == OEBBookReader.INITCHAPTER;
        }
        mProgressHander.sendEmptyMessage(result);
    }

    Handler progressHander = new Handler() {
        public void handleMessage(android.os.Message mesg) {
            if (isLoading == 2) {
                if (mesg.what == 0) {//开始
                    btn_download_02.setText("开始下载");
                    progrwwssbar_download_02.setProgress(0);
                    progrwwssbar_download_02.setVisibility(View.VISIBLE);
                } else if (mesg.what > 0 && mesg.what < 100) {//下载中
                    btn_download_02.setText("正在下载");
                    progrwwssbar_download_02.setProgress(mesg.what);
                } else if (mesg.what == 100) {//下载完成
                    isLoading = 0;
                    btn_download_02.setText("已下载");
                    btn_download_02.setTextColor(mActivity.getResources().getColor(R.color.font_right_02));
                    tv_download_02.setTextColor(mActivity.getResources().getColor(R.color.font_left_02));
                    rel_download_02.setOnClickListener(null);
                    font_microsoft.setOnClickListener(mFontButtonListener);
                    progrwwssbar_download_02.setVisibility(View.GONE);
                    AndroidFontUtil.clearFontCache();
                } else if (mesg.what < 0) {//失败
                    btn_download_02.setText("点击下载");
                    isLoading = 0;
                    progrwwssbar_download_02.setVisibility(View.GONE);
                    Toast.makeText(mActivity, "下载失败", Toast.LENGTH_SHORT).show();
                }
            } else if (isLoading == 3) {
                if (mesg.what == 0) {//开始
                    btn_download_03.setText("开始下载");
                    progrwwssbar_download_03.setProgress(0);
                    progrwwssbar_download_03.setVisibility(View.VISIBLE);
                } else if (mesg.what > 0 && mesg.what < 100) {//下载中
                    btn_download_03.setText("正在下载");
                    progrwwssbar_download_03.setProgress(mesg.what);
                } else if (mesg.what == 100) {//下载完成
                    isLoading = 0;
                    btn_download_03.setText("已下载");
                    btn_download_03.setTextColor(mActivity.getResources().getColor(R.color.font_right_02));
                    tv_download_03.setTextColor(mActivity.getResources().getColor(R.color.font_left_02));
                    rel_download_03.setOnClickListener(null);
                    font_ltalics.setOnClickListener(mFontButtonListener);
                    progrwwssbar_download_03.setVisibility(View.GONE);
                    AndroidFontUtil.clearFontCache();
                } else if (mesg.what < 0) {//失败
                    btn_download_03.setText("点击下载");
                    isLoading = 0;
                    progrwwssbar_download_03.setVisibility(View.GONE);
                    Toast.makeText(mActivity, "下载失败", Toast.LENGTH_SHORT).show();
                }
            } else if (isLoading == 4) {
                if (mesg.what == 0) {//开始
                    btn_download_04.setText("开始下载");
                    progrwwssbar_download_04.setProgress(0);
                    progrwwssbar_download_04.setVisibility(View.VISIBLE);
                } else if (mesg.what > 0 && mesg.what < 100) {//下载中
                    btn_download_04.setText("正在下载");
                    progrwwssbar_download_04.setProgress(mesg.what);
                } else if (mesg.what == 100) {//下载完成
                    isLoading = 0;
                    btn_download_04.setText("已下载");
                    btn_download_04.setTextColor(mActivity.getResources().getColor(R.color.font_right_02));
                    tv_download_04.setTextColor(mActivity.getResources().getColor(R.color.font_left_02));
                    rel_download_04.setOnClickListener(null);
                    font_youyuan.setOnClickListener(mFontButtonListener);
                    progrwwssbar_download_04.setVisibility(View.GONE);
                    AndroidFontUtil.clearFontCache();
                } else if (mesg.what < 0) {//失败
                    btn_download_04.setText("点击下载");
                    isLoading = 0;
                    progrwwssbar_download_04.setVisibility(View.GONE);
                    Toast.makeText(mActivity, "下载失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    /**
     * 改变背景选择的背景
     *
     * @param i
     */
    private void setReadBackgroudUnChecked(int i) {
        read_bg_01.setImageResource(R.drawable.bg_white_noselected);
        read_bg_02.setImageResource(R.drawable.bg_pink_noselected);
        read_bg_03.setImageResource(R.drawable.bg_green_noselected);
        read_bg_04.setImageResource(R.drawable.bg_gray_noselected);
        switch (i) {
            case 1:
                read_bg_01.setImageResource(R.drawable.bg_white_noselected_b);
                break;
            case 2:
                read_bg_02.setImageResource(R.drawable.bg_pink_noselected_b);
                break;
            case 3:
                read_bg_03.setImageResource(R.drawable.bg_green_noselected_b);
                break;
            case 4:
                read_bg_04.setImageResource(R.drawable.bg_gray_noselected_b);
                break;
        }
    }

    /**
     * 改变对其方式的背景
     *
     * @param i
     */
    private void setReadLtsUnChecked(int i) {
        iv_lts_01.setImageResource(R.drawable.both_side_align_noselected);
        iv_lts_02.setImageResource(R.drawable.left_side_align_noselected);
        iv_lts_03.setImageResource(R.drawable.middle_align_noselected);
        iv_lts_04.setImageResource(R.drawable.right_side_align_noselected);
        switch (i) {
            case 1:
                iv_lts_01.setImageResource(R.drawable.both_side_align_noselected_b);
                break;
            case 2:
                iv_lts_02.setImageResource(R.drawable.left_side_align_noselected_b);
                break;
            case 3:
                iv_lts_03.setImageResource(R.drawable.middle_align_noselected_b);
                break;
            case 4:
                iv_lts_04.setImageResource(R.drawable.right_side_align_noselected_b);
                break;
        }
    }

    /**
     * 改变字体选择的背景
     *
     * @param i
     */
    private void setReadFontUnChecked(int i) {
        iv_font_01.setImageResource(R.drawable.font_size_1_noselected);
        iv_font_02.setImageResource(R.drawable.font_size_2_noselected);
        iv_font_03.setImageResource(R.drawable.font_size_3_noselected);
        iv_font_04.setImageResource(R.drawable.font_size_4_noselected);
        switch (i) {
            case 1:
                iv_font_01.setImageResource(R.drawable.font_size_1_noselected_b);
                break;
            case 2:
                iv_font_02.setImageResource(R.drawable.font_size_2_noselected_b);
                break;
            case 3:
                iv_font_03.setImageResource(R.drawable.font_size_3_noselected_b);
                break;
            case 4:
                iv_font_04.setImageResource(R.drawable.font_size_4_noselected_b);
                break;
        }
    }

    /**
     * 设置字体
     *
     * @param fontFileName
     */
    public void setFont(final String fontFileName, String name) {
        String realFontName = null;
        if ("".equals(fontFileName)) {//默认字体
            realFontName = "serif";
        } else {
            File fontFile = new File(GBPaths.getFontsPathOption().getValue(), fontFileName);
            if (!fontFile.exists()) {
                UIUtil.showMessageText(mActivity, "字体文件不存在");
                return;
            }
            realFontName = AndroidFontUtil.getRealFontName(fontFile);
//            realFontName = fontDownLoadRealName;
        }
        if (mFontOption.getValue().equals(realFontName)) {
            UIUtil.showMessageText(mActivity, "已经是该字体了");
            return;
        }
        //设置字体
        mFontOption.setValue(realFontName);
        currentFont = mFontOption.getValue();
        resetReaderConfig();
        ll_font_more.startAnimation(mRightOutAnim);
        ll_font_more.setVisibility(View.GONE);
        isfont_tv.setText(name);
    }

    /**
     * 判断是什么字体
     *
     * @return
     */
    private String[] realNames = new String[]{"Microsoft YaHei", "SimSun-ExtB", "YouYuan"};

    private String isFont() {
        if (mFontOption.getValue().equals(realNames[0])) {
            return fontName[0];
        } else if (mFontOption.getValue().equals(realNames[1])) {
            return fontName[1];
        } else if (mFontOption.getValue().equals(realNames[2])) {
            return fontName[2];
        } else {
            return fontName[3];
        }
    }
}
