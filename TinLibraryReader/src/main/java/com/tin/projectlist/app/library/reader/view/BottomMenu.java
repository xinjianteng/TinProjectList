package com.tin.projectlist.app.library.reader.view;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.core.common.GBResource;
import com.core.common.util.Cookie;
import com.core.common.util.IFunction;
import com.core.domain.GBApplication;
import com.core.option.GBIntegerRangeOption;
import com.core.platform.GBLibrary;
import com.core.text.style.GBTextStyleCollection;
import com.core.view.PageEnum;
import com.core.view.PageEnum.Anim;
import com.geeboo.R;
import com.geeboo.read.controller.ActionCode;
import com.geeboo.read.controller.ColorProfile;
import com.geeboo.read.controller.ColorProfile.DayModel;
import com.geeboo.read.controller.ReaderApplication;
import com.geeboo.read.controller.ScrollingPreferences;
import com.geeboo.read.model.parser.oeb.OEBBookReader;
import com.geeboo.read.view.catalog.CatalogAndMarkActivity;
import com.geeboo.read.view.pdf.PdfActivity;
import com.geeboo.utils.EventName;
import com.geeboo.utils.UIUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 类名： BottomMenu.java<br>
 * 描述： 阅读底部菜单<br>
 * 创建者： jack<br>
 * 创建日期：2012-11-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class BottomMenu implements OnClickListener, AnimationListener, OnSeekBarChangeListener, IFunction<Integer> {

    private BaseMenuActivity mActivity;

    private Animation mOutAnim;
    private Animation mInAnim;
    public boolean mIsDismess = true;
    private boolean mIsLock = false; // 锁定（避免快速点击）

    private int mStepSize = 5; // 进度条改变步长
    // 当前操作
    private OPREATION mCurrenOpre = OPREATION.GOTO;

    enum OPREATION {// 操作类型
        // 页面跳转,亮度调节,字体设置,设置
        GOTO, LIGHT, FONT, SET
    }

    public BottomMenu(BaseMenuActivity activity) {
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
        mOutAnim = AnimationUtils.loadAnimation(mActivity, R.anim.read_bottom_menu_out);
        mOutAnim.setAnimationListener(this);
        // 消失
        mInAnim = AnimationUtils.loadAnimation(mActivity, R.anim.read_bottom_menu_in);
    }

    public SeekBar mSeekBar;// 进度栏
    public View mMainView; // 底部菜单view对象
    // public TextView mPages;

    // ==============PDF菜单初始化区==================
    private RadioButton mLandspace;
    private void initPdfMenuView() {
        mMainView = mActivity.getLayoutInflater().inflate(R.layout.read_pdf_bottom, null);
        mLandspace = (RadioButton) mMainView.findViewById(R.id.rb_reader_landspace);
        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLandspace.setText("横屏");
        }
        mLandspace.setOnClickListener(this);
        mSeekBar = (SeekBar) mMainView.findViewById(R.id.sb_goto_bar);
        // mPages = (TextView) mMainView.findViewById(R.id.tv_read_pages);
        mSeekBar.setOnSeekBarChangeListener(this);
        mMainView.setVisibility(View.GONE);
    }

    // ==============普通菜单初始化区==================
    protected LinearLayout mChangeBody; // 可切换的布局
    private RadioButton mCatalog; // 目录
    public RadioButton mFontSet; // 字体设置
    public RadioButton mNight;//夜间模式
    public RadioButton mLight; // 亮度
    private RadioButton mMore; // 更多设置
    private void initMainView() {
        mMainView = mActivity.getLayoutInflater().inflate(R.layout.read_bottom, null);
        mChangeBody = (LinearLayout) mMainView.findViewById(R.id.ll_reader_bottom_body);
        mCatalog = (RadioButton) mMainView.findViewById(R.id.rb_reader_catalog);
        mCatalog.setOnClickListener(this);
        mFontSet = (RadioButton) mMainView.findViewById(R.id.rb_reader_font);
        mFontSet.setOnClickListener(this);
        mNight = (RadioButton) mMainView.findViewById(R.id.rb_reader_night);
        mNight.setOnClickListener(this);
        mLight = (RadioButton) mMainView.findViewById(R.id.rb_reader_light);
        mLight.setOnClickListener(this);
        mMore = (RadioButton) mMainView.findViewById(R.id.rb_reader_more);
        mMore.setOnClickListener(this);
        initGoToBar();
        mMainView.setVisibility(View.GONE);
    }

    // ============初始化epub/txt跳转框===============
    private View mReadGoToBar;// 菜单调节框
    private View ll_font_set, ll_normal_set;
    private View ll_perious, ll_next;// 上一页下一页布局
    private TextView tv_perious, tv_next;// 上一页下一页名称
    private ImageView iv_perious, iv_next;// 上一页下一页图片
    private ImageButton ib_perious, ib_next;// 上一页下一页布局
    public ProgressBar mReading;// 进度栏
    private View ll_button;// 按钮布局区
    private ImageView iv_button;// 子页面的按住按钮
    private View ll_change_font;//更换字体
    private void initGoToBar() {
        mReadGoToBar = mActivity.getLayoutInflater().inflate(R.layout.read_goto_bar, null);
        ll_font_set = mReadGoToBar.findViewById(R.id.ll_font_set);
        ib_perious = (ImageButton) mReadGoToBar.findViewById(R.id.ib_perious);
        ib_perious.setOnClickListener(mAddCutListener);
        ib_next = (ImageButton) mReadGoToBar.findViewById(R.id.ib_next);
        ib_next.setOnClickListener(mAddCutListener);
        ll_normal_set = mReadGoToBar.findViewById(R.id.ll_normal_set);
        ll_perious = mReadGoToBar.findViewById(R.id.ll_perious);
        ll_perious.setOnClickListener(mAddCutListener);
        ll_next = mReadGoToBar.findViewById(R.id.ll_next);
        ll_next.setOnClickListener(mAddCutListener);
        tv_perious = (TextView) mReadGoToBar.findViewById(R.id.tv_perious);
        tv_next = (TextView) mReadGoToBar.findViewById(R.id.tv_next);
        iv_perious = (ImageView) mReadGoToBar.findViewById(R.id.iv_perious);
        iv_next = (ImageView) mReadGoToBar.findViewById(R.id.iv_next);
        mSeekBar = (SeekBar) mReadGoToBar.findViewById(R.id.sb_goto_bar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mReading = (ProgressBar) mReadGoToBar.findViewById(R.id.pb_reading);
        // mPages = (TextView) mReadGoToBar.findViewById(R.id.tv_read_pages);
        ll_button = mReadGoToBar.findViewById(R.id.ll_button);
        ll_button.setOnClickListener(mAddCutListener);
        iv_button = (ImageView) mReadGoToBar.findViewById(R.id.iv_button);
        ll_change_font = mReadGoToBar.findViewById(R.id.ll_change_font);
        ll_change_font.setOnClickListener(mAddCutListener);
        mChangeBody.removeAllViews();
        mChangeBody.addView(mReadGoToBar, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    }

    // ============初始化设置跳转框===============
    private View rl_set_sheep, rl_set_day, rl_set_brown, rl_set_simple, rl_set_water;
    private ImageView iv_set_sheep, iv_set_day, iv_set_brown, iv_set_simple, iv_set_water;
    private View rl_set_center, rl_set_right, rl_set_top, rl_set_eve, rl_set_diy;
    private TextView tv_change_no, tv_change_line, tv_change_flow, tv_change_true, tv_change_updown;
    private TextView tv_more_set;// 更多设置
    private void initSettingBar() {
        mReadGoToBar = mActivity.getLayoutInflater().inflate(R.layout.read_setting_bar, null);
        rl_set_sheep = mReadGoToBar.findViewById(R.id.rl_set_sheep);
        rl_set_sheep.setOnClickListener(mSetButtonListener);
        iv_set_sheep = (ImageView) mReadGoToBar.findViewById(R.id.iv_set_sheep);
        rl_set_day = mReadGoToBar.findViewById(R.id.rl_set_day);
        rl_set_day.setOnClickListener(mSetButtonListener);
        iv_set_day = (ImageView) mReadGoToBar.findViewById(R.id.iv_set_day);
        rl_set_brown = mReadGoToBar.findViewById(R.id.rl_set_brown);
        rl_set_brown.setOnClickListener(mSetButtonListener);
        iv_set_brown = (ImageView) mReadGoToBar.findViewById(R.id.iv_set_brown);
        rl_set_simple = mReadGoToBar.findViewById(R.id.rl_set_simple);
        rl_set_simple.setOnClickListener(mSetButtonListener);
        iv_set_simple = (ImageView) mReadGoToBar.findViewById(R.id.iv_set_simple);
        rl_set_water = mReadGoToBar.findViewById(R.id.rl_set_water);
        rl_set_water.setOnClickListener(mSetButtonListener);
        iv_set_water = (ImageView) mReadGoToBar.findViewById(R.id.iv_set_water);
        rl_set_center = mReadGoToBar.findViewById(R.id.rl_set_center);
        rl_set_center.setOnClickListener(mSetButtonListener);
        rl_set_right = mReadGoToBar.findViewById(R.id.rl_set_right);
        rl_set_right.setOnClickListener(mSetButtonListener);
        rl_set_top = mReadGoToBar.findViewById(R.id.rl_set_top);
        rl_set_top.setOnClickListener(mSetButtonListener);
        rl_set_eve = mReadGoToBar.findViewById(R.id.rl_set_eve);
        rl_set_eve.setOnClickListener(mSetButtonListener);
        rl_set_diy = mReadGoToBar.findViewById(R.id.rl_set_diy);
        rl_set_diy.setOnClickListener(mSetButtonListener);
        tv_change_no = (TextView) mReadGoToBar.findViewById(R.id.tv_change_no);
        tv_change_no.setOnClickListener(mSetButtonListener);
        tv_change_line = (TextView) mReadGoToBar.findViewById(R.id.tv_change_line);
        tv_change_line.setOnClickListener(mSetButtonListener);
        tv_change_flow = (TextView) mReadGoToBar.findViewById(R.id.tv_change_flow);
        tv_change_flow.setOnClickListener(mSetButtonListener);
        tv_change_true = (TextView) mReadGoToBar.findViewById(R.id.tv_change_true);
        tv_change_true.setOnClickListener(mSetButtonListener);
        tv_change_updown = (TextView) mReadGoToBar.findViewById(R.id.tv_change_updown);
        tv_change_updown.setOnClickListener(mSetButtonListener);
        tv_more_set = (TextView) mReadGoToBar.findViewById(R.id.tv_more_set);
        tv_more_set.setOnClickListener(mSetButtonListener);
        mChangeBody.removeAllViews();
        mChangeBody.addView(mReadGoToBar, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public SeekBar sb_line_progress;// 进度栏
    public SeekBar sb_paragh_progress;// 进度栏
    public SeekBar sb_up_progress;// 进度栏
    public SeekBar sb_left_progress;// 进度栏
    private void initSettingSpace() {
        mReadGoToBar = mActivity.getLayoutInflater().inflate(R.layout.read_setting_style, null);
        sb_line_progress = (SeekBar) mReadGoToBar.findViewById(R.id.sb_line_progress);
        sb_line_progress.setOnSeekBarChangeListener(this);
        sb_paragh_progress = (SeekBar) mReadGoToBar.findViewById(R.id.sb_paragh_progress);
        sb_paragh_progress.setOnSeekBarChangeListener(this);
        sb_up_progress = (SeekBar) mReadGoToBar.findViewById(R.id.sb_up_progress);
        sb_up_progress.setOnSeekBarChangeListener(this);
        sb_left_progress = (SeekBar) mReadGoToBar.findViewById(R.id.sb_left_progress);
        sb_left_progress.setOnSeekBarChangeListener(this);
        mChangeBody.removeAllViews();
        mChangeBody.addView(mReadGoToBar, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
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
                mSeekBar.setProgress((int) (mActivity.getReadPro() * 100));
                // mPages.setText("(" + mActivity.getProPage() + ")");
                // mPages.setVisibility(View.VISIBLE);
                mCurrenOpre = OPREATION.GOTO;
                mMainView.setVisibility(View.VISIBLE);
                mMainView.startAnimation(mInAnim);
            }
        } else {
            if (mCurrenOpre == OPREATION.SET) {// 如果是设置
                initGoToBar();
            }
            if (!mActivity.mApplication.Model.getTextModel().isLoadBookOver()) {
                mReading.setVisibility(View.VISIBLE);
                mSeekBar.setVisibility(View.GONE);
                // mPages.setText(isPageing ? "正在分页..." : "正在提取目录...");
            } else {
                mSeekBar.setProgress((int) (mActivity.getReadPro() * 100));
                mSeekBar.setVisibility(View.VISIBLE);
                mReading.setVisibility(View.GONE);
                // mPages.setText("(" + mActivity.getProPage() + ")");
            }
            if (mMainView.getVisibility() == View.VISIBLE) {
                mIsDismess = true;// 隐藏菜单
                mMainView.startAnimation(mOutAnim);
            } else {
                mIsDismess = false;// 显示菜单
                // mChangeBody.removeAllViews();
                // 设置进度跳转样式
                if (mActivity.mWidget == null) {
                    tv_perious.setVisibility(View.GONE);
                    tv_next.setVisibility(View.GONE);
                } else {
                    iv_perious.setBackgroundResource(R.drawable.reader_sort_pre);
                    iv_next.setBackgroundResource(R.drawable.reader_sort_next);
                    // if (mActivity.mBookDescriptor.RealSuffer ==
                    // BookDescriptor.Suffix.EPUB) {
                    tv_perious.setText("上一章");
                    tv_next.setText("下一章");
                    // } else {
                    // tv_perious.setText("上一节");
                    // tv_next.setText("下一节");
                    // }
                    tv_perious.setVisibility(View.VISIBLE);
                    tv_next.setVisibility(View.VISIBLE);
                }
                ll_button.setVisibility(View.GONE);
                ll_change_font.setVisibility(View.GONE);
                ll_font_set.setVisibility(View.GONE);
                ll_normal_set.setVisibility(View.VISIBLE);
                // mPages.setVisibility(View.VISIBLE);
                mCurrenOpre = OPREATION.GOTO;
                setMenuUnChecked();
                mMainView.setVisibility(View.VISIBLE);
                mMainView.startAnimation(mInAnim);
            }

        }
    }

    /**
     * 设置菜单未选中
     */
    private void setMenuUnChecked() {
        // 判断是否是夜间模式
        if (mActivity.mApplication.getColorProfileName().equals(ColorProfile.NIGHT)) {
            mNight.setChecked(true);
        } else {
            mNight.setChecked(false);
        }
        mCatalog.setChecked(false);
        mFontSet.setChecked(false);
        mLight.setChecked(false);
        mMore.setChecked(false);
    }

    private void setVideoUnChecked() {
        tv_change_no.setBackgroundColor(mActivity.getResources().getColor(R.color.c70));
        tv_change_no.setTextColor(mActivity.getResources().getColor(R.color.white));
        tv_change_line.setBackgroundColor(mActivity.getResources().getColor(R.color.c70));
        tv_change_line.setTextColor(mActivity.getResources().getColor(R.color.white));
        tv_change_flow.setBackgroundColor(mActivity.getResources().getColor(R.color.c70));
        tv_change_flow.setTextColor(mActivity.getResources().getColor(R.color.white));
        tv_change_true.setBackgroundColor(mActivity.getResources().getColor(R.color.c70));
        tv_change_true.setTextColor(mActivity.getResources().getColor(R.color.white));
        tv_change_updown.setBackgroundColor(mActivity.getResources().getColor(R.color.c70));
        tv_change_updown.setTextColor(mActivity.getResources().getColor(R.color.white));
    }

    private void setReadBackgroudUnChecked() {
        iv_set_sheep.setVisibility(View.INVISIBLE);
        iv_set_day.setVisibility(View.INVISIBLE);
        iv_set_brown.setVisibility(View.INVISIBLE);
        iv_set_simple.setVisibility(View.INVISIBLE);
        iv_set_water.setVisibility(View.INVISIBLE);
    }

    private void resetReaderConfig() {
        mActivity.mApplication.clearTextCaches();
        mActivity.mWidget.reset();
        mActivity.mWidget.repaint();
    }

    @Override
    public void onClick(View v) {
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
        if (mCurrenOpre != OPREATION.SET) {// 如果不是设置
            mSeekBar.setVisibility(View.VISIBLE);
            mReading.setVisibility(View.GONE);
        }
        if (v.getId() != R.id.rb_reader_night) {// 夜间模式
            setMenuUnChecked();
        }
        if (v.getId() == R.id.rb_reader_catalog) {// 目录
            MobclickAgent.onEvent(mActivity, EventName.READER_SHOWCATALOG);
            mCatalog.setChecked(true);
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
            mFontSet.setChecked(true);
            initGoToBar();
            ll_font_set.setVisibility(View.VISIBLE);
            ll_normal_set.setVisibility(View.GONE);
            ll_change_font.setVisibility(View.VISIBLE);
//            tv_perious.setVisibility(View.GONE);
//            tv_next.setVisibility(View.GONE);
            // iv_button.setImageResource(R.drawable.reader_font_mode);
//            ll_button.setVisibility(View.GONE);
//            iv_perious.setBackgroundResource(R.drawable.reader_font_down);
//            iv_next.setBackgroundResource(R.drawable.reader_font_up);
//            final GBIntegerRangeOption option = GBTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
//            int pro = option.getValue() * 100 / (option.MaxValue - option.MinValue);
//            mSeekBar.setProgress(pro);
            mCurrenOpre = OPREATION.FONT;
            // mPages.setVisibility(View.GONE);
            mReadGoToBar.startAnimation(mInAnim);
        } else if (v.getId() == R.id.rb_reader_night) {// 夜间模式
            MobclickAgent.onEvent(mActivity, EventName.READER_READMODEL);
            if (mActivity.mApplication.getColorProfileName().equals(ColorProfile.NIGHT)) {
                mNight.setChecked(false);
            } else {
                mNight.setChecked(true);
            }
            mActivity.mApplication.setColorProfileName(
                    mActivity.mApplication.getColorProfileName().equals(ColorProfile.NIGHT)
                            ? ColorProfile.DAY
                            : ColorProfile.NIGHT, mActivity.mApplication.mDayModel);
            resetReaderConfig();
        } else if (v.getId() == R.id.rb_reader_light) {// 亮度
            MobclickAgent.onEvent(mActivity, EventName.READER_SECREENLIGHT);
            mLight.setChecked(true);
            initGoToBar();
            ll_font_set.setVisibility(View.GONE);
            ll_change_font.setVisibility(View.GONE);
            ll_normal_set.setVisibility(View.VISIBLE);
            tv_perious.setVisibility(View.GONE);
            tv_next.setVisibility(View.GONE);
//            ll_button.setVisibility(View.VISIBLE);
            iv_perious.setBackgroundResource(R.drawable.reader_light_down);
            iv_next.setBackgroundResource(R.drawable.reader_light_up);
            // 判断是否是夜间模式
//            if (mActivity.mApplication.getColorProfileName().equals(ColorProfile.NIGHT)) {
//                iv_button.setImageResource(R.drawable.reader_night_mode_selected);
//            } else {
//                iv_button.setImageResource(R.drawable.reader_night_mode);
//            }
//            final ReaderApplication application = (ReaderApplication) GBApplication.Instance();
            int pro = GBLibrary.Instance().getScreenBrightness();
            mSeekBar.setProgress(pro);
            mCurrenOpre = OPREATION.LIGHT;
            // mPages.setVisibility(View.GONE);
            mReadGoToBar.startAnimation(mInAnim);
        } else if (v.getId() == R.id.rb_reader_more) {// 设置
            MobclickAgent.onEvent(mActivity, EventName.READER_READSETTING);
            mMore.setChecked(true);
            initSettingBar();
            // 背景类型
            setReadBackgroudUnChecked();
            ReaderApplication application = (ReaderApplication) GBApplication.Instance();
            if (application.getColorProfileName().equals(ColorProfile.DAY)) {
                switch (application.mDayModel) {
                    case Day :
                        iv_set_day.setVisibility(View.VISIBLE);
                        break;
                    case FlaxBrown :
                        iv_set_brown.setVisibility(View.VISIBLE);
                        break;
                    case SleepKin :
                        iv_set_sheep.setVisibility(View.VISIBLE);
                        break;
                    case SimpleBrown :
                        iv_set_simple.setVisibility(View.VISIBLE);
                        break;
                    case Wash :
                        iv_set_water.setVisibility(View.VISIBLE);
                        break;
                }
            }

            // 动画方式
            setVideoUnChecked();
            Anim currentAnim = ScrollingPreferences.Instance().AnimationOption.getValue();
            boolean horScroll = ScrollingPreferences.Instance().HorizontalOption.getValue();
            if(!horScroll){//上下滑动
                tv_change_updown.setBackgroundColor(Color.parseColor("#636060"));
                tv_change_updown.setTextColor(Color.parseColor("#3b3939"));
            } else if (currentAnim == PageEnum.Anim.CURL) {
                tv_change_true.setBackgroundColor(Color.parseColor("#636060"));
                tv_change_true.setTextColor(Color.parseColor("#3b3939"));
            } else if (currentAnim == PageEnum.Anim.FLIP) {
                tv_change_line.setBackgroundColor(Color.parseColor("#636060"));
                tv_change_line.setTextColor(Color.parseColor("#3b3939"));
            } else if (currentAnim == PageEnum.Anim.FLIP_FRAME) {
                tv_change_flow.setBackgroundColor(Color.parseColor("#636060"));
                tv_change_flow.setTextColor(Color.parseColor("#3b3939"));
            } else {
                tv_change_no.setBackgroundColor(Color.parseColor("#636060"));
                tv_change_no.setTextColor(Color.parseColor("#3b3939"));
            }
            mCurrenOpre = OPREATION.SET;
            mReadGoToBar.startAnimation(mInAnim);
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
            case GOTO :
                int total = mActivity.mApplication.getCurrentView().getTotalPage();
                // mPages.setText((progress == 0 ? 1 : total * progress / 100) +
                // "/" + total);
                break;
            // case LIGHT :
            //
            // break;
            // case FONT :
            // if (mActivity instanceof PdfActivity)
            // UIUtil.showMessageText(mActivity, "pdf图书暂时不支持字体缩放");
            // else {
            // final GBIntegerRangeOption option =
            // GBTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
            // mToast.setText("字号:" + (option.MaxValue - option.MinValue) * progress
            // / 100);
            // mToast.show();
            // }
            // break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 滑块托动处理
        switch (mCurrenOpre) {
            case GOTO :
                if (mActivity.mApplication.getCurrentView().isLoading()) {
                    UIUtil.showMessageText(mActivity, GBResource.resource("readerPage").getResource("loadingPlease")
                            .getValue());
                } else {
                    MobclickAgent.onEvent(mActivity, EventName.READER_GOTO);
                    ReaderApplication.Instance().runAction(ActionCode.GOTO_PAGE, mSeekBar.getProgress());
                }
                break;
            case LIGHT :
                ReaderApplication.Instance().runAction(ActionCode.SCREEN_LIGHT, mSeekBar.getProgress());
                break;
//            case FONT :
//                if (mActivity instanceof PdfActivity)
//                    UIUtil.showMessageText(mActivity, GBResource.resource("readerPage").getResource("pdfNoSupportFont")
//                            .getValue());
//                else
//                    ReaderApplication.Instance().runAction(ActionCode.INCREASE_FONT, mSeekBar.getProgress());
//                break;
            case SET :
                int progress = seekBar.getProgress();
                if (seekBar.getId() == R.id.sb_line_progress) {
                    GBTextStyleCollection.Instance().getBaseStyle().LineSpaceOption.setValue((int) (progress
                            * (10 * 1.0 / 100) + 10));
                } else if (seekBar.getId() == R.id.sb_paragh_progress) {
                    GBTextStyleCollection.Instance().getBaseStyle().ParaSpaceOption
                            .setValue((int) (progress * (15 * 1.0 / 100)));
                } else if (seekBar.getId() == R.id.sb_up_progress) {
                    ((ReaderApplication) GBApplication.Instance()).TopMarginOption.setValue((int) (progress
                            * (80 * 1.0 / 100) + 20));
                    ((ReaderApplication) GBApplication.Instance()).BottomMarginOption.setValue((int) (progress
                            * (80 * 1.0 / 100) + 20));
                } else if (seekBar.getId() == R.id.sb_left_progress) {
                    ((ReaderApplication) GBApplication.Instance()).LeftMarginOption.setValue(progress);
                    ((ReaderApplication) GBApplication.Instance()).RightMarginOption.setValue(progress);
                }
                resetReaderConfig();
                break;
        }
    }

    /**
     * 调解框按钮点击事件处理
     */
    View.OnClickListener mAddCutListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (mCurrenOpre) {
                case GOTO :
                    if (mActivity.mWidget != null) {
                        if (mActivity.mApplication.getCurrentView().isLoading()) {
                            UIUtil.showMessageText(mActivity,
                                    GBResource.resource("readerPage").getResource("loadingPlease").getValue());
                        } else {
                            if (v.getId() == R.id.ll_next) {
                                if (!mActivity.mApplication.BookTextView.nextChapter())
                                    UIUtil.showMessageText(mActivity,
                                            GBResource.resource("readerPage").getResource("isLastChapter").getValue());
                            } else if (v.getId() == R.id.ll_perious) {
                                if (!mActivity.mApplication.BookTextView.preChapter()) {
                                    UIUtil.showMessageText(mActivity,
                                            GBResource.resource("readerPage").getResource("isFristChapter").getValue());
                                }
                            }
                            mActivity.getmWidget().postInvalidate();
                            GBApplication.Instance().runAction(ActionCode.RESET_PAGEINFO);
                        }
                    } else {
                        // 跳转
                        if (v.getId() == R.id.ll_next) {
                            // MobclickAgent.onEvent(mActivity,EventName.READER_NEXTCHAPTER);
                            mSeekBar.setProgress(mSeekBar.getProgress() + mStepSize);
                        } else if (v.getId() == R.id.ll_perious) {
                            // MobclickAgent.onEvent(mActivity,EventName.READER_PRIOUSCHAPTER);
                            mSeekBar.setProgress(mSeekBar.getProgress() - mStepSize);
                        }
                        ReaderApplication.Instance().runAction(ActionCode.GOTO_PAGE, mSeekBar.getProgress());
                    }
                    break;
                case LIGHT :
//                    if (v.getId() == R.id.ll_button) {// 夜间模式
//                        MobclickAgent.onEvent(mActivity, EventName.READER_READMODEL);
//                        if (mActivity.mApplication.getColorProfileName().equals(ColorProfile.NIGHT)) {
//                            iv_button.setImageResource(R.drawable.reader_night_mode);
//                        } else {
//                            iv_button.setImageResource(R.drawable.reader_night_mode_selected);
//                        }
//                        mActivity.mApplication.setColorProfileName(
//                                mActivity.mApplication.getColorProfileName().equals(ColorProfile.NIGHT)
//                                        ? ColorProfile.DAY
//                                        : ColorProfile.NIGHT, mActivity.mApplication.mDayModel);
//                        resetReaderConfig();
//                        mActivity.showDisMenu();
//                    } else {
                    // 亮度调节
                    if (v.getId() == R.id.ll_next) {
                        mSeekBar.setProgress(mSeekBar.getProgress() + mStepSize);
                    } else if (v.getId() == R.id.ll_perious) {
                        mSeekBar.setProgress(mSeekBar.getProgress() - mStepSize);
                    }
                    ReaderApplication.Instance().runAction(ActionCode.SCREEN_LIGHT, mSeekBar.getProgress());
//                    }
                    break;
                case FONT :
                    if (mActivity instanceof PdfActivity)
                        UIUtil.showMessageText(mActivity, "pdf图书暂时不支持字体缩放");
                    else {
                        // 字体设置
                        if (v.getId() == R.id.ll_next) {
                            ReaderApplication.Instance().runAction(ActionCode.INCREASE_FONT);
                        } else if (v.getId() == R.id.ll_perious) {
                            ReaderApplication.Instance().runAction(ActionCode.DECREASE_FONT);
                        } else if(v.getId() == R.id.ll_change_font){
                            MobclickAgent.onEvent(mActivity, EventName.READER_SETTING_FONT);
                            mActivity.showDisMenu();
                            TypefaceReaderActivity.actionView(mActivity);
                        } else if(v.getId() == R.id.ib_perious){
                            if (mActivity instanceof PdfActivity) {
                                UIUtil.showMessageText(mActivity, GBResource.resource("readerPage").getResource("pdfNoSupportFont")
                                        .getValue());
                            } else {
                                GBIntegerRangeOption option = GBTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
                                if(option.getValue() <= option.MinValue){
                                    UIUtil.showMessageText(mActivity,"已经很小了,再小就看不清了.");
                                    return;
                                }
                                ReaderApplication.Instance().runAction(ActionCode.INCREASE_FONT, option.getValue()-1);
//                            	ReaderApplication.Instance().runAction(ActionCode.INCREASE_FONT, mSeekBar.getProgress());
                            }

                            break;
                        } else if(v.getId() == R.id.ib_next){
                            if (mActivity instanceof PdfActivity){
                                UIUtil.showMessageText(mActivity, GBResource.resource("readerPage").getResource("pdfNoSupportFont")
                                        .getValue());
                            } else {
                                GBIntegerRangeOption option = GBTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
                                if(option.getValue() >= option.MaxValue){
                                    UIUtil.showMessageText(mActivity,"已经很大了,再大就看得眼花了.");
                                    return;
                                }
                                ReaderApplication.Instance().runAction(ActionCode.INCREASE_FONT, option.getValue()+1);
//                        		ReaderApplication.Instance().runAction(ActionCode.INCREASE_FONT, mSeekBar.getProgress());
                            }
                            break;
                        }
                        final GBIntegerRangeOption option = GBTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
                        int pro = option.getValue() * 100 / (option.MaxValue - option.MinValue);
                        mSeekBar.setProgress(pro);
                    }
                    break;
            }
        }
    };

    View.OnClickListener mSetButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // 跳转
            if (v.getId() == R.id.tv_more_set) {// 更多设置
                mActivity.showDisMenu();
                SettingReadReaderActivity.actionView(mActivity,false);
//                SettingReadReaderActivity.actionView(mActivity, mActivity.mApplication.isReadPdf);
            } else if (v.getId() == R.id.tv_change_no) {// 滑动：无
                setVideoUnChecked();
                ScrollingPreferences.Instance().HorizontalOption.setValue(true);
                ScrollingPreferences.Instance().AnimationOption.setValue(Anim.NONE);
                tv_change_no.setBackgroundColor(Color.parseColor("#636060"));
                tv_change_no.setTextColor(Color.parseColor("#3b3939"));
            } else if (v.getId() == R.id.tv_change_line) {// 滑动：平移
                setVideoUnChecked();
                ScrollingPreferences.Instance().HorizontalOption.setValue(true);
                ScrollingPreferences.Instance().AnimationOption.setValue(Anim.FLIP);
                tv_change_line.setBackgroundColor(Color.parseColor("#636060"));
                tv_change_line.setTextColor(Color.parseColor("#3b3939"));
            } else if (v.getId() == R.id.tv_change_flow) {// 滑动：滑动
                setVideoUnChecked();
                ScrollingPreferences.Instance().HorizontalOption.setValue(true);
                ScrollingPreferences.Instance().AnimationOption.setValue(Anim.FLIP_FRAME);
                tv_change_flow.setBackgroundColor(Color.parseColor("#636060"));
                tv_change_flow.setTextColor(Color.parseColor("#3b3939"));
            } else if (v.getId() == R.id.tv_change_true) {// 滑动：仿真
                setVideoUnChecked();
                ScrollingPreferences.Instance().HorizontalOption.setValue(true);
                ScrollingPreferences.Instance().AnimationOption.setValue(Anim.CURL);
                tv_change_true.setBackgroundColor(Color.parseColor("#636060"));
                tv_change_true.setTextColor(Color.parseColor("#3b3939"));
            } else if(v.getId() == R.id.tv_change_updown){// 滑动: 上下
                setVideoUnChecked();
                ScrollingPreferences.Instance().HorizontalOption.setValue(false);
                ScrollingPreferences.Instance().AnimationOption.setValue(Anim.FLIP);
                tv_change_updown.setBackgroundColor(Color.parseColor("#636060"));
                tv_change_updown.setTextColor(Color.parseColor("#3b3939"));
            } else if (v.getId() == R.id.rl_set_sheep) {
                setReadBackgroudUnChecked();
                ((ReaderApplication) GBApplication.Instance()).setColorProfileName(ColorProfile.DAY, DayModel.SleepKin);
                resetReaderConfig();
                iv_set_sheep.setVisibility(View.VISIBLE);
            } else if (v.getId() == R.id.rl_set_day) {
                setReadBackgroudUnChecked();
                ((ReaderApplication) GBApplication.Instance()).setColorProfileName(ColorProfile.DAY, DayModel.Day);
                resetReaderConfig();
                iv_set_day.setVisibility(View.VISIBLE);
            } else if (v.getId() == R.id.rl_set_brown) {
                setReadBackgroudUnChecked();
                ((ReaderApplication) GBApplication.Instance())
                        .setColorProfileName(ColorProfile.DAY, DayModel.FlaxBrown);
                resetReaderConfig();
                iv_set_brown.setVisibility(View.VISIBLE);
            } else if (v.getId() == R.id.rl_set_simple) {
                setReadBackgroudUnChecked();
                ((ReaderApplication) GBApplication.Instance()).setColorProfileName(ColorProfile.DAY,
                        DayModel.SimpleBrown);
                resetReaderConfig();
                iv_set_simple.setVisibility(View.VISIBLE);
            } else if (v.getId() == R.id.rl_set_water) {
                setReadBackgroudUnChecked();
                ((ReaderApplication) GBApplication.Instance()).setColorProfileName(ColorProfile.DAY, DayModel.Wash);
                resetReaderConfig();
                iv_set_water.setVisibility(View.VISIBLE);
            } else if (v.getId() == R.id.rl_set_center) {// 常规
                GBTextStyleCollection.Instance().getBaseStyle().LeftIndentOption.setValue(0);// 段落缩进
                GBTextStyleCollection.Instance().getBaseStyle().LineSpaceOption.setValue(10);
                GBTextStyleCollection.Instance().getBaseStyle().ParaSpaceOption.setValue(0);
                ((ReaderApplication) GBApplication.Instance()).TopMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).BottomMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).LeftMarginOption.setValue(10);
                ((ReaderApplication) GBApplication.Instance()).RightMarginOption.setValue(10);
                resetReaderConfig();
            } else if (v.getId() == R.id.rl_set_right) {// 常规、缩进
                GBTextStyleCollection.Instance().getBaseStyle().LeftIndentOption.setValue(25);// 段落缩进
                GBTextStyleCollection.Instance().getBaseStyle().LineSpaceOption.setValue(10);
                GBTextStyleCollection.Instance().getBaseStyle().ParaSpaceOption.setValue(0);
                ((ReaderApplication) GBApplication.Instance()).TopMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).BottomMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).LeftMarginOption.setValue(10);
                ((ReaderApplication) GBApplication.Instance()).RightMarginOption.setValue(10);
                resetReaderConfig();
            } else if (v.getId() == R.id.rl_set_top) {// 段间距、缩进
                GBTextStyleCollection.Instance().getBaseStyle().LeftIndentOption.setValue(25);// 段落缩进
                GBTextStyleCollection.Instance().getBaseStyle().LineSpaceOption.setValue(10);
                GBTextStyleCollection.Instance().getBaseStyle().ParaSpaceOption.setValue(15);
                ((ReaderApplication) GBApplication.Instance()).TopMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).BottomMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).LeftMarginOption.setValue(10);
                ((ReaderApplication) GBApplication.Instance()).RightMarginOption.setValue(10);
                resetReaderConfig();
            } else if (v.getId() == R.id.rl_set_eve) {// 行间距、缩进
                GBTextStyleCollection.Instance().getBaseStyle().LeftIndentOption.setValue(25);// 段落缩进
                GBTextStyleCollection.Instance().getBaseStyle().LineSpaceOption.setValue(20);
                GBTextStyleCollection.Instance().getBaseStyle().ParaSpaceOption.setValue(0);
                ((ReaderApplication) GBApplication.Instance()).TopMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).BottomMarginOption.setValue(20);
                ((ReaderApplication) GBApplication.Instance()).LeftMarginOption.setValue(10);
                ((ReaderApplication) GBApplication.Instance()).RightMarginOption.setValue(10);
                resetReaderConfig();
            } else if (v.getId() == R.id.rl_set_diy) {
                initSettingSpace();
                ReaderApplication application = (ReaderApplication) GBApplication.Instance();
                int leftPro = application.LeftMarginOption.getValue();// 左右间距
                int upPro = application.TopMarginOption.getValue();// 上下间距
                int linePro = (int) ((GBTextStyleCollection.Instance().getBaseStyle().LineSpaceOption.getValue() - 10) * 10);// 行间距
                int paraghPro = (int) (GBTextStyleCollection.Instance().getBaseStyle().ParaSpaceOption.getValue() * (100 * 1.0 / 15));// 段间距
                sb_line_progress.setProgress(linePro);
                sb_paragh_progress.setProgress(paraghPro);
                sb_up_progress.setProgress((int) ((upPro - 20) * (100 * 1.0 / 80)));
                sb_left_progress.setProgress(leftPro);
                resetReaderConfig();
                // GBTextStyleCollection.Instance().getBaseStyle().LeftIndentOption.getValue();//段落缩进
            }
        }
    };

    // 阅读器底部菜单进度条
    Handler mProgressHander = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == OEBBookReader.CALL_BOOK_OVER) {
                mSeekBar.setVisibility(View.VISIBLE);
                mReading.setVisibility(View.GONE);
                mActivity.resetPages();
            } else if (msg.what < OEBBookReader.CALL_BOOK_OVER) {
                // mPages.setText(isPageing ? "正在分页..." : "正在提取目录...");
            } else {
                mReading.setProgress(msg.what);
            }
        }
    };

    boolean isPageing = false;

    @Override
    public void callback(final Integer result) {
        if (result < OEBBookReader.CALL_BOOK_OVER) {
            isPageing = result == OEBBookReader.INITCHAPTER;
        }
        mProgressHander.sendEmptyMessage(result);
    }

}
