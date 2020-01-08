package com.tin.projectlist.app.library.reader.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.core.common.util.IFunction;
import com.core.text.widget.GBTextFixedPosition;
import com.geeboo.R;
import com.geeboo.read.controller.GeeBookMgr;
import com.geeboo.read.exception.TipException;
import com.geeboo.read.model.book.Bookmark;
import com.geeboo.read.view.widget.RadioImageView;
import com.geeboo.utils.EventName;
import com.geeboo.utils.GeeBookLoader;
import com.geeboo.utils.UIUtil;
import com.iflytek.speech.SpeechUtility;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * 类名： HeadMenu.java<br>
 * 描述： 阅读头部菜单控件<br>
 * 创建者： jack<br>
 * 创建日期：2012-11-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class ReadHeadMenu implements OnClickListener, AnimationListener {
    private BaseMenuActivity mActivity;
    public View mMainView;

    private Animation mOutAnim;
    private Animation mInAnim;
    private boolean mIsDismess = true;
    private boolean mIsLock = false;

    private LinearLayout mBack;
    private RadioImageView rb_bookmarky;
    private RadioImageView rbNotify;
    private RadioImageView rbRead;
    private RadioImageView rbShare;
    private RadioImageView rbFriend;
    private LinearLayout layoutContent;

    private RadioImageView rbPlay;
    private SeekBar sbSpeed;
    private ImageView ivReduce, ivAdd;

    private View readView, shareView;
    private FrameLayout rl;

    public ReadHeadMenu(BaseMenuActivity activity) {
        this.mActivity = activity;
        initMenu();
    }

    /**
     * 功能描述：初始化菜单控件<br>
     * 创建者： jack<br>
     * 创建日期：2012-11-22<br>
     *
     * @param
     */
    private void initMenu() {

        initMainView();

        mOutAnim = AnimationUtils.loadAnimation(mActivity,
                R.anim.read_top_menu_out);
        mOutAnim.setAnimationListener(this);
        mInAnim = AnimationUtils.loadAnimation(mActivity,
                R.anim.read_top_menu_in);
    }

    /**
     * 功能描述： 初始化头部菜单<br>
     * 创建者： jack<br>
     * 创建日期：2012-11-22<br>
     *
     * @param
     */
    private void initMainView() {
        mMainView = mActivity.getLayoutInflater().inflate(R.layout.read_head_menu, null);

        mBack = (LinearLayout) mMainView.findViewById(R.id.ll_back);
        mBack.setOnClickListener(this);
        rb_bookmarky = (RadioImageView)mMainView.findViewById(R.id.rb_bookmarky);
        rb_bookmarky.setOnClickListener(this);
        rbNotify = (RadioImageView) mMainView.findViewById(R.id.rb_notify);
        rbNotify.setOnClickListener(this);
        rbRead = (RadioImageView) mMainView.findViewById(R.id.rb_read);
        rbRead.setOnClickListener(this);
        rbShare = (RadioImageView) mMainView.findViewById(R.id.rb_share);
        rbShare.setOnClickListener(this);
        rbFriend = (RadioImageView) mMainView.findViewById(R.id.rb_friend);
        rbFriend.setOnClickListener(this);
        layoutContent = (LinearLayout) mMainView.findViewById(R.id.ll_reader_head_body);
        mMainView.setVisibility(View.GONE);

        
    }

    /**
     * 功能描述： 呼出菜单<br>
     * 创建者： jack<br>
     * 创建日期：2012-11-22<br>
     *
     * @param
     */
    public void showOrDismess() {
        cleanView();
        rbShare.setChecked(false);
        if (mIsLock) {
            return;
        }
        if (mMainView.getVisibility() == View.VISIBLE) {
            mIsDismess = true;
            mMainView.startAnimation(mOutAnim);
        } else {
            mIsDismess = false;
            mMainView.setVisibility(View.VISIBLE);
            mMainView.startAnimation(mInAnim);

            final List list = mActivity.mApplication.BookTextView.getBookPageMarkList();
            if (list != null && !list.isEmpty()) {
                rb_bookmarky.setImageResource(R.drawable.bookmark_black);
            } else {
                rb_bookmarky.setImageResource(R.drawable.bookmark_gray);
            }
        }
    }

    @Override
    public void onClick(View v) {
        mActivity.closeView(0);
        if (v.getId() == R.id.ll_back) {
            // ReaderApplication.Instance().closeWindow();
            mActivity.onBackClose();
        } else if (v.getId() == R.id.rb_bookmarky) {
            MobclickAgent.onEvent(mActivity, EventName.READER_ADDMARK);
            if (mActivity instanceof ReaderActivity) {
                final List<GBTextFixedPosition> list = mActivity.mApplication.BookTextView
                        .getBookPageMarkList();
                if (list == null || list.isEmpty()) {
                    ((ReaderActivity) mActivity).addPageBookmark();
                    rb_bookmarky.setImageResource(R.drawable.bookmark_black);
                } else {
                    for (GBTextFixedPosition gp : list) {
                        if (GeeBookLoader.getBookMgr() == null) {
                            mActivity.mApplication.Collection
                                    .deleteBookmark(((Bookmark) gp));
                        } else {
                            try {
                                GeeBookLoader.getBookMgr().deleteBookmark(
                                        ((Bookmark) gp));
                            } catch (TipException e) {
                                e.toast(mActivity);
                            }
                        }

                    }
                    rb_bookmarky.setImageResource(R.drawable.bookmark_gray);
                }
            }
        } else if (v.getId() == R.id.rb_notify) {
            mActivity.showDisMenu();
            GeeBookLoader.getBookMgr().addNotify();
        } else if (v.getId() == R.id.rb_read) {
            repleReadView();
        } else if (v.getId() == R.id.rb_share) {
            GeeBookMgr geebooMgr = GeeBookLoader.getBookMgr();
            int b = geebooMgr.getHoldStatus();
            if (b==1||b==2||b==3|b==4){
            }else {
                Toast.makeText(mActivity,"本书还未同步到云端，无法进行此操作", Toast.LENGTH_SHORT).show();
                return;
            }
            repleShareView();
//            doshare();
        } else if (v.getId() == R.id.rb_friend) {
            GeeBookMgr geebooMgr = GeeBookLoader.getBookMgr();
            int b = geebooMgr.getHoldStatus();
            if (b==1||b==2||b==3|b==4){
            }else {
                Toast.makeText(mActivity,"本书还未同步到云端，无法进行此操作", Toast.LENGTH_SHORT).show();
                return;
            }
            cleanView();
            doInvite();
        } else if (v.getId() == R.id.rb_play) {
            doSpeech();
        }


    }

    private void cleanView() {
//        layoutContent.setVisibility(View.GONE);
        layoutContent.removeAllViews();
        rbShare.setChecked(false);
        rbRead.setChecked(false);
    }

    /**
     * 头部分享
     *
     * @author Shikh
     * @time 16-8-31 下午4:27
     */
    private void repleShareView() {
        if (rbRead.isChecked())
            rbRead.setChecked(false);
        if (shareView == null) {
            shareView = mActivity.getLayoutInflater().inflate(R.layout.layout_share_view, null);
        }
        layoutContent.removeAllViews();
        rbShare.toggle();
        if (rbShare.isChecked()) {
            layoutContent.addView(shareView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutContent.setVisibility(rbShare.isChecked() ? View.VISIBLE : View.GONE);
        } else {
            layoutContent.setVisibility(rbShare.isChecked() ? View.VISIBLE : View.GONE);
        }
        shareView.findViewById(R.id.share_book_group).setOnClickListener(new mclick());
        shareView.findViewById(R.id.share_friend).setOnClickListener(new mclick());
        shareView.findViewById(R.id.share_friend_zone).setOnClickListener(new mclick());
        shareView.findViewById(R.id.share_qq_friend).setOnClickListener(new mclick());
        shareView.findViewById(R.id.share_wb).setOnClickListener(new mclick());
        shareView.findViewById(R.id.share_wx).setOnClickListener(new mclick());
    }

    public void closeOtherView(){
        layoutContent.removeAllViews();
        rbShare.setChecked(false);
        rbRead.setChecked(false);
    }

    /**
     * 朗读页 已取消
     *
     * @author Shikh
     * @time 16-8-31 下午4:27
     */

    private void repleReadView() {
        if (rbShare.isChecked())
            rbShare.setChecked(false);
        if (readView == null) {
            readView = mActivity.getLayoutInflater().inflate(R.layout.layout_read_aloud, null);
            rbPlay = (RadioImageView) readView.findViewById(R.id.rb_play);
            rbPlay.setOnClickListener(this);
            ivReduce = (ImageView) readView.findViewById(R.id.iv_reduce);
            ivReduce.setOnClickListener(this);
            ivAdd = (ImageView) readView.findViewById(R.id.iv_add);
            ivAdd.setOnClickListener(this);
            sbSpeed = (SeekBar) readView.findViewById(R.id.sb_speed);
        }
        layoutContent.removeAllViews();
        rbRead.toggle();
        if (rbRead.isChecked()) {
            layoutContent.addView(readView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutContent.setVisibility(rbRead.isChecked() ? View.VISIBLE : View.GONE);
        } else {
            layoutContent.setVisibility(rbRead.isChecked() ? View.VISIBLE : View.GONE);
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

    /*
     * 检查是否存在语音引擎
     */
    private boolean isCheck = false;

    private boolean checkSpeechEngine() {
        // 没有可用的引擎
        if (SpeechUtility.getUtility(mActivity).queryAvailableEngines() == null
                || SpeechUtility.getUtility(mActivity).queryAvailableEngines().length <= 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
            dialog.setMessage(R.string.voice_tip);
            dialog.setNegativeButton(R.string.cancel, null);
            dialog.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface,
                                            int i) {
                            if (GeeBookLoader.getBookMgr() != null) {
                                GeeBookLoader.getBookMgr().downloadFile(GeeBookLoader.getBookMgr().getVoiceEngineUri(), "speechVoice.apk", true, "正在下载语音插件",
                                        new IFunction<Integer>() {
                                            @Override
                                            public void callback(Integer nowProgress) {
                                                progress = nowProgress;
                                                voiceProgressHander.sendEmptyMessage(0);
                                            }
                                        });
                            }
//
                        }
                    });
            dialog.show();
            return false;
        }
        // 设置你申请的应用appid
        SpeechUtility.getUtility(mActivity).setAppid("5229943a");
        isCheck = true;
        return true;
    }

    private int progress;
    Handler voiceProgressHander = new Handler() {
        public void handleMessage(android.os.Message mesg) {
            if (progress == 0) {//开始
            } else if (progress > 0 && progress < 100) {//下载中
            } else if (progress == 100) {//下载完成
                GeeBookLoader.getBookMgr().installVoiceEngine();
            } else {
                UIUtil.showMessageText(mActivity, "下载语音引擎失败！");
            }
        }
    };

    private void doSpeech() {
        MobclickAgent.onEvent(mActivity, EventName.READER_SPEECH);
        if (!isCheck) {
            if (!checkSpeechEngine())
                return;
        }
        if (mActivity.mIsSpeech) {
            // stop speech
//			mSpeech.setBackgroundResource(R.drawable.reader_voice);
            mActivity.stopSpeech();
        } else {
            // start speech
//			mSpeech.setBackgroundResource(R.drawable.reader_voice_selected);
//            mActivity.showDisMenu();
            mActivity.startSpeech();
        }
    }

    private void doshare() {
        mActivity.showDisMenu();
        if (GeeBookLoader.getBookMgr() != null) {
            GeeBookLoader.getBookMgr().showShareBook();
        }

    }

    private void doCommend() {
        mActivity.showDisMenu();
        if (GeeBookLoader.getBookMgr() != null) {
            GeeBookLoader.getBookMgr().showCommendBook();
        }

    }

    private void doInvite() {
        mActivity.showDisMenu();
        if (GeeBookLoader.getBookMgr() != null) {
            GeeBookLoader.getBookMgr().showInviteBook();
        }

    }


    private class mclick implements OnClickListener {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.share_book_group) {
                if (GeeBookLoader.getBookMgr() != null) {
                    GeeBookLoader.getBookMgr().shareToBookgroup(mActivity,"",ReaderActivity.getView(),0);
                }
            }
            if (i == R.id.share_friend) {//书友
                if (GeeBookLoader.getBookMgr() != null) {
                    GeeBookLoader.getBookMgr().openActShareGoodFriendActivity(mActivity);
                }
            }
            if (i == R.id.share_friend_zone) {
                if (GeeBookLoader.getBookMgr() != null) {
                    GeeBookLoader.getBookMgr().sharetoWXfriendGroup(mActivity);
                }
            }
            if (i == R.id.share_qq_friend) {
                if (GeeBookLoader.getBookMgr() != null) {
                    GeeBookLoader.getBookMgr().sharetoQQ(mActivity);
                }
            }
            if (i == R.id.share_wb) {
                if (GeeBookLoader.getBookMgr() != null) {
                    GeeBookLoader.getBookMgr().sharetoWB(mActivity);
                }
            }
            if (i == R.id.share_wx) {
                if (GeeBookLoader.getBookMgr() != null) {
                    GeeBookLoader.getBookMgr().sharetoWX(mActivity);
                }
            }
        }
    }
}
