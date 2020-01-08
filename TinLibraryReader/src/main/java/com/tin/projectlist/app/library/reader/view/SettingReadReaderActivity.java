package com.tin.projectlist.app.library.reader.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.core.common.util.IFunction;
import com.core.domain.GBApplication;
import com.core.log.L;
import com.core.text.style.GBTextStyleCollection;
import com.geeboo.R;
import com.geeboo.read.controller.ReaderApplication;
import com.geeboo.utils.EventName;
import com.geeboo.utils.GeeBookLoader;
import com.geeboo.utils.UIUtil;
import com.geeboo.utils.switchbtn.SwitchButton;
import com.iflytek.speech.SpeechUtility;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

/**
 * 类名： SettingReadReaderActivity.java<br>
 * 描述： 阅读设置功能<br>
 * 创建者： jack<br>
 * 创建日期：2013-8-19<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class SettingReadReaderActivity extends BaseReaderActivity implements OnClickListener, OnCheckedChangeListener {

    final String TAG = "SettingReadReaderActivity";
    private View mBack; // 返回按钮
    private SwitchButton sb_page_up_down;//是否启用上下翻页
    private SwitchButton show_percent;//是否启用
    private SwitchButton show_pagenum;//是否启用
    private SwitchButton show_battery;//是否启用
    private SwitchButton show_time;//是否启用
    private SwitchButton show_title;//是否启用
    private RadioButton mOneMin,mThreeMin,mFiveMin,mTenMin,mKeep;// 屏幕保护设置
    private ProgressBar borrow_btn1;//PDF阅读设置按钮
    private TextView tv_borrow_title1;//PDF阅读设置按钮文字
    private ProgressBar borrow_btn2;//语音朗读按钮
    private TextView tv_borrow_title2;//语音朗读按钮文字
    private int progress;
//    private View rl_font_set;//字体设置
//    private SeekBar mLinePadding; // 行间距
//    private SwitchButton mCurl; // 翻页动画开关
//    private SwitchButton mDoublePage; // 双翻页开关
    // 主题切换
//    private RadioButton mSheepSkin,mDay,mFlaxBrown,mSimpleBrown,mWash;//羊皮,白天,亚麻棕,简棕色,水墨色

    private static boolean downVoiceEngine;
    // 语音设置
//    private SeekBar mSpeech; // 语速
//    private SeekBar mPitch; // 音高
//    private SwitchButton mSensorPage;// 感应翻页
//    private SeekBar mSensitivity;// 翻页感应灵敏度

    // 翻页动画切换
//    private RadioButton rdoMoveAnim,rdoSlideAnim,rdoRealAnim,rgFlipAnimSwitch;
//    private RadioGroup rgSeitchTheme;
//    private RadioGroup rgFlipPaperSwitch;
//    private RadioButton rdoDoubleFlipOn, rdoDoubleFlipOff;
    private View ivSendIdea = null; // 意见反馈

//    private View ivOne, ivTwo, ivThree , ivFour;
//    EditText etSpaceExtra = null;

    public static void actionView(Activity context, boolean mDownVoiceEngine) {
        downVoiceEngine = mDownVoiceEngine;
        Intent intent = new Intent(context, SettingReadReaderActivity.class);
        context.startActivityForResult(intent, 1);

    }

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setContentView(R.layout.read_setting_activity);

        mBack = findViewById(R.id.iv_back);
        mBack.setOnClickListener(this);

        L.e(TAG, "cre  " + GBTextStyleCollection.Instance().getBaseStyle().LineSpaceOption.getValue());
        ReaderApplication application = (ReaderApplication) GBApplication.Instance();
        //启用上下翻页
        sb_page_up_down = (SwitchButton) this.findViewById(R.id.sb_page_up_down);
        if(application.isEnableVolumeScrollPage==1){
            sb_page_up_down.setChecked(true);
        } else {
            sb_page_up_down.setChecked(false);
        }
//        if (ScrollingPreferences.Instance().HorizontalOption.getValue()) {
//
//        } else {
//
//        }
        sb_page_up_down.setOnCheckedChangeListener(this);
        show_percent = (SwitchButton) this.findViewById(R.id.show_percent);
        if(application.isShowPercent==1){
            show_percent.setChecked(true);
        } else {
            show_percent.setChecked(false);
        }
        show_percent.setOnCheckedChangeListener(this);
        show_pagenum = (SwitchButton) this.findViewById(R.id.show_pagenum);
        if(application.isShowPageNum==1){
            show_pagenum.setChecked(true);
        } else {
            show_pagenum.setChecked(false);
        }
        show_pagenum.setOnCheckedChangeListener(this);
        show_battery = (SwitchButton) this.findViewById(R.id.show_battery);
        if(application.isShowElec==1){
            show_battery.setChecked(true);
        } else {
            show_battery.setChecked(false);
        }
        show_battery.setOnCheckedChangeListener(this);
        show_time = (SwitchButton) this.findViewById(R.id.show_time);
        if(application.isShowTime==1){
            show_time.setChecked(true);
        } else {
            show_time.setChecked(false);
        }
        show_time.setOnCheckedChangeListener(this);
        show_title = (SwitchButton) this.findViewById(R.id.show_title);
        if(application.isShowTitle==1){
            show_title.setChecked(true);
        } else {
            show_title.setChecked(false);
        }
        show_title.setOnCheckedChangeListener(this);

        // 屏保设置application.getColorProfile()
        mOneMin = (RadioButton) findViewById(R.id.rb_1_minute);
        mThreeMin = (RadioButton) findViewById(R.id.rb_3_minute);
        mFiveMin = (RadioButton) findViewById(R.id.rb_5_minute);
        mTenMin = (RadioButton) findViewById(R.id.rb_10_minute);
        mKeep = (RadioButton) findViewById(R.id.rb_0_minute);
        setLightChecked();
        mOneMin.setOnClickListener(mLightSleepListener);
        mThreeMin.setOnClickListener(mLightSleepListener);
        mFiveMin.setOnClickListener(mLightSleepListener);
        mTenMin.setOnClickListener(mLightSleepListener);
        mKeep.setOnClickListener(mLightSleepListener);

        //pdf阅读设置
        borrow_btn1 = (ProgressBar) findViewById(R.id.borrow_btn1);
        tv_borrow_title1 = (TextView) findViewById(R.id.tv_borrow_title1);

        //语音朗读
        borrow_btn2 = (ProgressBar) findViewById(R.id.borrow_btn2);
        tv_borrow_title2 = (TextView) findViewById(R.id.tv_borrow_title2);
        // 没有可用的引擎

        if(downVoiceEngine){//下载引擎
            if(GeeBookLoader.getBookMgr() != null){
                GeeBookLoader.getBookMgr().downloadFile(GeeBookLoader.getBookMgr().getVoiceEngineUri(),"speechVoice.apk",true,"正在下载语音插件", new IFunction<Integer>() {
                    @Override
                    public void callback(Integer nowProgress) {
                        progress = nowProgress;
                        voiceProgressHander.sendEmptyMessage(0);
                    }
                });
            }
        }

//        etSpaceExtra = (EditText) this.findViewById(R.id.et_line_setting);
//        rdoVerticalFlipPaperOff = (RadioButton) this.findViewById(R.id.rdo_vertical_flip_paper_off);

//        rgFlipPaperSwitch = (RadioGroup) this.findViewById(R.id.rg_vertical_flip_paper_switch);

//        rdoMoveAnim = (RadioButton) this.findViewById(R.id.rdo_move_anim);
//        rdoSlideAnim = (RadioButton) this.findViewById(R.id.rdo_slide_anim);
//        rdoRealAnim = (RadioButton) this.findViewById(R.id.rdo_real_anim);

//        rgFlipAnimSwitch = (RadioGroup) this.findViewById(R.id.rg_flip_anim_switch);
//        rgFlipAnimSwitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.rdo_move_anim) {
//                    ScrollingPreferences.Instance().AnimationOption.setValue(Anim.FLIP);
//                    findViewById(R.id.rl_vertical_anim).setVisibility(View.VISIBLE);
//                    findViewById(R.id.rl_duble_flip_layout).setVisibility(View.GONE);
//                    rdoMoveAnim.setTextColor(getResources().getColor(R.color.white));
//                    rdoRealAnim.setTextColor(getResources().getColor(R.color.black));
//                    rdoSlideAnim.setTextColor(getResources().getColor(R.color.black));
//                    rdoDoubleFlipOff.setChecked(true);
//                } else if (checkedId == R.id.rdo_slide_anim) {
//                    ScrollingPreferences.Instance().AnimationOption.setValue(Anim.FLIP_FRAME);
//                    findViewById(R.id.rl_vertical_anim).setVisibility(View.VISIBLE);
//                    findViewById(R.id.rl_duble_flip_layout).setVisibility(View.GONE);
//                    rdoMoveAnim.setTextColor(getResources().getColor(R.color.black));
//                    rdoSlideAnim.setTextColor(getResources().getColor(R.color.white));
//                    rdoRealAnim.setTextColor(getResources().getColor(R.color.black));
//                    rdoDoubleFlipOff.setChecked(true);
//                } else if (checkedId == R.id.rdo_real_anim) {
//                    ScrollingPreferences.Instance().AnimationOption.setValue(Anim.CURL);
//                    findViewById(R.id.rl_vertical_anim).setVisibility(View.GONE);
//                    findViewById(R.id.rl_duble_flip_layout).setVisibility(View.VISIBLE);
//                    rdoMoveAnim.setTextColor(getResources().getColor(R.color.black));
//                    rdoSlideAnim.setTextColor(getResources().getColor(R.color.black));
//                    rdoRealAnim.setTextColor(getResources().getColor(R.color.white));
//                }
//            }
//        });

//        rdoDoubleFlipOn = (RadioButton) this.findViewById(R.id.rdo_double_paper_on);
//        rdoDoubleFlipOff = (RadioButton) this.findViewById(R.id.rdo_doub_paper_off);

//        RadioGroup.OnCheckedChangeListener onCheckedChange = new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                MobclickAgent.onEvent(SettingReadReaderActivity.this, EventName.READER_SETTING_ANIMATIONCURL);
//
//            }
//        };
//        rgFlipPaperSwitch.setOnCheckedChangeListener(onCheckedChange);

//        if (ScrollingPreferences.Instance().DoublePageOption.getValue()) {
//            rdoDoubleFlipOn.setChecked(true);
//            rdoDoubleFlipOff.setChecked(false);
//        } else {
//            rdoDoubleFlipOn.setChecked(false);
//            rdoDoubleFlipOff.setChecked(true);
//        }
//        Anim currentAnim = ScrollingPreferences.Instance().AnimationOption.getValue();
//        if (currentAnim == PageEnum.Anim.CURL) {
//            rdoRealAnim.setChecked(true);
//        } else if (currentAnim == PageEnum.Anim.FLIP) {
//            rdoMoveAnim.setChecked(true);
//        } else if (currentAnim == PageEnum.Anim.FLIP_FRAME) {
//            rdoSlideAnim.setChecked(true);
//        }

//        rgSeitchTheme = (RadioGroup) this.findViewById(R.id.rg_switch_theme);
        initView();

    };

    @Override
    protected void onResume() {
        super.onResume();
        File pdfSoFile = new File(this.getDir("lib", Context.MODE_PRIVATE), "libmupdf.so");
        if (!pdfSoFile.exists()) {
            borrow_btn1.setOnClickListener(this);
            tv_borrow_title1.setText("下载");
        } else {
            tv_borrow_title1.setText("已下载");
        }
        if (SpeechUtility.getUtility(this).queryAvailableEngines() == null
                || SpeechUtility.getUtility(this).queryAvailableEngines().length <= 0) {
            borrow_btn2.setOnClickListener(this);
            tv_borrow_title2.setText("下载");
        } else {
            tv_borrow_title2.setText("已安装");
        }
    }

//    void setVisibility(View view) {
//        if (ivOne.equals(view)) {
//            ivOne.setVisibility(View.INVISIBLE);
//            ivTwo.setVisibility(View.VISIBLE);
//            ivThree.setVisibility(View.VISIBLE);
//        } else if (ivTwo.equals(view)) {
//            ivTwo.setVisibility(View.INVISIBLE);
//            ivOne.setVisibility(View.VISIBLE);
//            ivThree.setVisibility(View.VISIBLE);
//        } else if (ivThree.equals(view)) {
//            ivThree.setVisibility(View.INVISIBLE);
//            ivOne.setVisibility(View.VISIBLE);
//            ivTwo.setVisibility(View.VISIBLE);
//        } else if (ivFour.equals(view)) {
//            ivThree.setVisibility(View.VISIBLE);
//            ivOne.setVisibility(View.VISIBLE);
//            ivTwo.setVisibility(View.VISIBLE);
//        }
//    }

//    void setLineSpaceProgress(SeekBar seekBar) {
//        if (seekBar.getProgress() < 32) {
//            setVisibility(ivOne);
//            if (seekBar.getProgress() != 15) {
//                seekBar.setProgress(15);
//
//            }
//
//        } else if (seekBar.getProgress() < 67) {
//            setVisibility(ivTwo);
//            if (seekBar.getProgress() != 50)
//                seekBar.setProgress(50);
//        } else if (seekBar.getProgress() < 92) {
//            setVisibility(ivThree);
//            if (seekBar.getProgress() != 92)
//                seekBar.setProgress(85);
//        } else {
//            setVisibility(ivFour);
//            if (seekBar.getProgress() != 100) {
//                seekBar.setProgress(100);
//            }
//        }
//    }

    private void initView() {
//        ivOne = this.findViewById(R.id.iv_one);
//        ivTwo = this.findViewById(R.id.iv_two);
//        ivThree = this.findViewById(R.id.iv_three);
//        ivFour = this.findViewById(R.id.iv_four);
//        mLinePadding = (SeekBar) findViewById(R.id.sb_linepadding);
//        mLinePadding.setProgress((GBTextStyleCollection.Instance().getBaseStyle().LineSpaceOption.getValue() - 10) * 10);
//        setLineSpaceProgress(mLinePadding);
//        L.e(TAG,"mLinePadding.getProgress()=" + mLinePadding.getProgress() + "  =="+ ((float) mLinePadding.getProgress() / 100));
//        etSpaceExtra.setLineSpacing(etSpaceExtra.getTextSize(), (float) mLinePadding.getProgress() / 100);

//        mLinePadding.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                setLineSpaceProgress(seekBar);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                MobclickAgent.onEvent(SettingReadReaderActivity.this, EventName.READER_SETTING_LINEPADDINGSET);
//                GBTextStyleCollection.Instance().getBaseStyle().LineSpaceOption.setValue(seekBar.getProgress() / 10 + 10);
//                etSpaceExtra.setLineSpacing(etSpaceExtra.getTextSize(), (float) progress / 100);
//            }
//        });

//        mCurl = (SwitchButton) findViewById(R.id.sbtn_anim_switch);
//        mCurl.setChecked(ScrollingPreferences.Instance().AnimationOption.getValue() == PageEnum.Anim.CURL);
//        mCurl.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isChecked && mDoublePage.isChecked()) {
//                    mDoublePage.setChecked(false);
//                    ScrollingPreferences.Instance().DoublePageOption.setValue(false);
//                    GBLibrary.Instance().DoublePageOption.setValue(false);
//                }
//            }
//        });

//        RadioGroup rdDoublePage = (RadioGroup) findViewById(R.id.rg_doublepage_switch);
//        rdDoublePage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                boolean isChecked = checkedId == R.id.rdo_double_paper_on;
//
////                final boolean flag = rdoVerticalFlipPaperOn.isChecked();
//
//                if (isChecked) {
//
//                    rdoDoubleFlipOn.setTextColor(getResources().getColor(R.color.white));
//                    rdoDoubleFlipOff.setTextColor(getResources().getColor(R.color.black));
//                } else {
//                    rdoDoubleFlipOn.setTextColor(getResources().getColor(R.color.black));
//                    rdoDoubleFlipOff.setTextColor(getResources().getColor(R.color.white));
//
//                }
//
//                ScrollingPreferences.Instance().DoublePageOption.setValue(isChecked);
//                GBLibrary.Instance().DoublePageOption.setValue(isChecked);
//
//            }
//        });
//        mDoublePage = (SwitchButton) findViewById(R.id.sbtn_doublepage_switch);
//        mDoublePage.setChecked(ScrollingPreferences.Instance().DoublePageOption.getValue());
//        mDoublePage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                MobclickAgent.onEvent(SettingReadReaderActivity.this, EventName.READER_SETTING_UDCURL);
//                final boolean flag = mCurl.isChecked();
//                if (!flag && isChecked)
//                    mCurl.setChecked(true);
//                ScrollingPreferences.Instance().DoublePageOption.setValue(isChecked);
//                GBLibrary.Instance().DoublePageOption.setValue(isChecked);
//            }
//        });

//        OnCheckedChangeListener onCheckedChange = new OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    buttonView.setTextColor(getResources().getColor(R.color.white));
//                } else {
//                    buttonView.setTextColor(getResources().getColor(R.color.hui));
//                }
//
//            }
//        };

//        mSheepSkin = (RadioButton) findViewById(R.id.b_sheep_skin);
//        mSheepSkin.setOnCheckedChangeListener(onCheckedChange);
//        mDay = (RadioButton) findViewById(R.id.b_day);
//        mDay.setOnCheckedChangeListener(onCheckedChange);
//        mFlaxBrown = (RadioButton) findViewById(R.id.b_flax_brown);
//        mFlaxBrown.setOnCheckedChangeListener(onCheckedChange);
//        mSimpleBrown = (RadioButton) findViewById(R.id.b_simple_brown);
//        mSimpleBrown.setOnCheckedChangeListener(onCheckedChange);
//        mWash = (RadioButton) findViewById(R.id.b_wash);
//        mWash.setOnCheckedChangeListener(onCheckedChange);
//        mSheepSkin.setOnClickListener(this);
//        mDay.setOnClickListener(this);
//        mFlaxBrown.setOnClickListener(this);
//        mSimpleBrown.setOnClickListener(this);
//        mWash.setOnClickListener(this);
//        final ReaderApplication application = (ReaderApplication) GBApplication.Instance();
//        if (application.getColorProfileName().equals(ColorProfile.DAY)) {
//            switch (application.mDayModel) {
//                case Day :
//                    mDay.setChecked(true);
//                    break;
//                case FlaxBrown :
//                    mFlaxBrown.setChecked(true);
//                    break;
//                case SleepKin :
//                    mSheepSkin.setChecked(true);
//                    break;
//                case SimpleBrown :
//                    mSimpleBrown.setChecked(true);
//                    break;
//                case Wash :
//                    mWash.setChecked(true);
//                    break;
//            }
//        }

//        mLinePadding.setEnabled(!mIsPdf);
//        mCurl.setEnabled(!mIsPdf);
//        mSheepSkin.setEnabled(!mIsPdf);
//        mDay.setEnabled(!mIsPdf);
//        mFlaxBrown.setEnabled(!mIsPdf);
//        mSimpleBrown.setEnabled(!mIsPdf);
//        mWash.setEnabled(!mIsPdf);

        // 语音设置
//        mSpeech = (SeekBar) findViewById(R.id.sb_speed);
        // TODO 设置当前设置值

//        mSpeech.setProgress(application.Speed.getValue());
//        mSpeech.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                MobclickAgent.onEvent(SettingReadReaderActivity.this, EventName.READER_SETTING_READSPEED);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                application.Speed.setValue(progress);
//            }
//        });
//        mPitch = (SeekBar) findViewById(R.id.sb_pitch);
        // TODO 设置当前设置值
//        mPitch.setProgress(application.Ptich.getValue());
//        mPitch.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                MobclickAgent.onEvent(SettingReadReaderActivity.this, EventName.READER_SETTING_READVOICE);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                application.Ptich.setValue(progress);
//            }
//        });

//        mSensorPage = (SwitchButton) findViewById(R.id.sbtn_sensorpage_switch);
//        mSensorPage.setChecked(ScrollingPreferences.Instance().SensorPageOption.getValue());
//        mSensorPage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                ScrollingPreferences.Instance().SensorPageOption.setValue(isChecked);
//            }
//        });

//        mSensitivity = (SeekBar) findViewById(R.id.sb_sensitivity);
//        mSensitivity.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                // TODO Auto-generated method stub
//
//            }
//        });

        ivSendIdea = this.findViewById(R.id.iv_send_idea);
        ivSendIdea.setOnClickListener(this);

//        ((TextView) mCurrentFontName).setText(GBTextStyleCollection.Instance().getBaseStyle().FontFamilyOption
//                .getValue());

    }

    @Override
    public void onClick(View v) {
        if (v == mBack) {
            this.onBackPressed();
            return;
        }

//        final ReaderApplication application = (ReaderApplication) GBApplication.Instance();
//        if (v == mSheepSkin) {
//            application.setColorProfileName(ColorProfile.DAY, DayModel.SleepKin);
//        } else if (v == mDay) {
//            application.setColorProfileName(ColorProfile.DAY, DayModel.Day);
//        } else if (v == mFlaxBrown) {
//            application.setColorProfileName(ColorProfile.DAY, DayModel.FlaxBrown);
//        } else if (v == mSimpleBrown) {
//            application.setColorProfileName(ColorProfile.DAY, DayModel.SimpleBrown);
//        } else if (v == mWash) {
//            application.setColorProfileName(ColorProfile.DAY, DayModel.Wash);
        if (v.getId() == R.id.iv_send_idea) {
            Intent sendIdeaIntent = new Intent(this, SendIdeaReaderActivity.class);
            startActivity(sendIdeaIntent);
            this.finish();
//        } else if (v.getId() == R.id.rl_font_set) {
//            MobclickAgent.onEvent(this, EventName.READER_SETTING_FONT);
//            Intent settingFontIntent = new Intent(this, TypefaceReaderActivity.class);
//            startActivity(settingFontIntent);
        } else if(v.getId() == R.id.borrow_btn1){//PDF阅读设置
            GeeBookLoader.getBookMgr().downloadFile(GeeBookLoader.getBookMgr().getPdfSoUri(),"libmupdf.so",true,"正在下载PDF插件", new IFunction<Integer>() {
                @Override
                public void callback(Integer nowProgress) {
                    progress = nowProgress;
                    pdfProgressHander.sendEmptyMessage(0);
                }
            });
        } else if(v.getId() == R.id.borrow_btn2){//语音朗读
            GeeBookLoader.getBookMgr().downloadFile(GeeBookLoader.getBookMgr().getVoiceEngineUri(),"speechVoice.apk",true,"正在下载语音插件", new IFunction<Integer>() {
                @Override
                public void callback(Integer nowProgress) {
                    progress = nowProgress;
                    voiceProgressHander.sendEmptyMessage(0);
                }
            });
        }
        // TODO
        MobclickAgent.onEvent(this, EventName.READER_SETTING_SKINBG);
    }

    Handler pdfProgressHander = new Handler() {
        public void handleMessage(android.os.Message mesg) {
            if(progress == 0){//开始
                borrow_btn1.setClickable(false);
                borrow_btn1.setProgress(0);
                tv_borrow_title1.setText("开始下载");
            }else if(progress > 0 && progress < 100){//下载中
                borrow_btn1.setProgress(progress);
                tv_borrow_title1.setText("下载中...");
            }else if(progress == 100){//下载完成
                borrow_btn1.setProgress(0);
                GeeBookLoader.getBookMgr().installPdfSo();
                tv_borrow_title1.setText("已下载");
            } else {
                borrow_btn1.setClickable(true);
                borrow_btn1.setOnClickListener(SettingReadReaderActivity.this);
                borrow_btn1.setProgress(0);
                tv_borrow_title1.setText("下载");
                UIUtil.showMessageText(SettingReadReaderActivity.this, "下载pdf插件失败！");
            }
        }
    };

    Handler voiceProgressHander = new Handler() {
        public void handleMessage(android.os.Message mesg) {
            if(progress == 0){//开始
                borrow_btn2.setClickable(false);
                borrow_btn2.setProgress(0);
                tv_borrow_title2.setText("开始下载");
            }else if(progress > 0 && progress < 100){//下载中
                borrow_btn2.setProgress(progress);
                tv_borrow_title2.setText("下载中...");
            }else if(progress == 100){//下载完成
                borrow_btn2.setProgress(0);
                GeeBookLoader.getBookMgr().installVoiceEngine();
                tv_borrow_title2.setText("等待安装");
            } else {
                borrow_btn2.setClickable(true);
                borrow_btn2.setOnClickListener(SettingReadReaderActivity.this);
                borrow_btn2.setProgress(0);
                tv_borrow_title2.setText("下载");
                UIUtil.showMessageText(SettingReadReaderActivity.this, "下载语音引擎失败！");
            }
        }
    };

    //=================屏幕保护监听设置=========================
    private RadioButton mCurrentChecked;
    private OnClickListener mLightSleepListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v != mCurrentChecked) {
                mCurrentChecked.setChecked(false);
                mCurrentChecked.setTextColor(getResources().getColor(R.color.hui));
                mCurrentChecked = ((RadioButton) v);
                mCurrentChecked.setChecked(true);
                mCurrentChecked.setTextColor(getResources().getColor(R.color.white));
                final ReaderApplication application = (ReaderApplication) GBApplication.Instance();
                application.SleepTimeOption.setValue(getCheckLight());
            }
        }
    };

    /**
     * 功能描述： 获取睡眠选择模式
     */
    private int getCheckLight() {
        if (mOneMin.isChecked())
            return 1;
        if (mThreeMin.isChecked())
            return 3;
        if (mFiveMin.isChecked())
            return 5;
        if (mTenMin.isChecked())
            return 10;
        return 0;
    }

    /**
     * 功能描述：设置屏保默认设置
     */
    private void setLightChecked() {
        MobclickAgent.onEvent(this, EventName.READER_SETTING_SLEEPTIME);
        final ReaderApplication application = (ReaderApplication) GBApplication.Instance();
        switch (application.SleepTimeOption.getValue()) {
            case 1 :
                mOneMin.setChecked(true);
                mCurrentChecked = mOneMin;
                break;
            case 3 :
                mThreeMin.setChecked(true);
                mCurrentChecked = mThreeMin;
                break;
            case 5 :
                mFiveMin.setChecked(true);
                mCurrentChecked = mFiveMin;
                break;
            case 10 :
                mTenMin.setChecked(true);
                mCurrentChecked = mTenMin;
                break;
            default :
                mKeep.setChecked(true);
                mCurrentChecked = mKeep;
                break;
        }
        mCurrentChecked.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ReaderApplication application = (ReaderApplication) GBApplication.Instance();
        if(buttonView.getId() == R.id.sb_page_up_down){//启用上下翻页
            if(buttonView.isChecked()){
                application.isEnableVolumeScrollPage = 1;
//				ScrollingPreferences.Instance().HorizontalOption.setValue(false);
            }else{
                application.isEnableVolumeScrollPage = 0;
//            	ScrollingPreferences.Instance().HorizontalOption.setValue(true);
            }
        } else if(buttonView.getId() == R.id.show_percent){
            if(buttonView.isChecked()){
                application.isShowPercent = 1;
            } else {
                application.isShowPercent = 0;
            }
        } else if(buttonView.getId() == R.id.show_pagenum){
            if(buttonView.isChecked()){
                application.isShowPageNum = 1;
            } else {
                application.isShowPageNum = 0;
            }
        } else if(buttonView.getId() == R.id.show_battery){
            if(buttonView.isChecked()){
                application.isShowElec = 1;
            } else {
                application.isShowElec = 0;
            }
        } else if(buttonView.getId() == R.id.show_time){
            if(buttonView.isChecked()){
                application.isShowTime = 1;
            } else {
                application.isShowTime = 0;
            }
        } else if(buttonView.getId() == R.id.show_title){
            if(buttonView.isChecked()){
                application.isShowTitle = 1;
            } else {
                application.isShowTitle = 0;
            }
        }
    }
}
