package com.tin.projectlist.app.library.reader.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.core.text.widget.GBTextFixedPosition;
import com.geeboo.R;
import com.geeboo.read.exception.TipException;
import com.geeboo.read.model.book.Bookmark;
import com.geeboo.read.view.pdf.PdfActivity;
import com.geeboo.utils.EventName;
import com.geeboo.utils.FileUtils;
import com.geeboo.utils.GeeBookLoader;
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
public class HeadMenu implements OnClickListener, AnimationListener, OnTouchListener {
	private BaseMenuActivity mActivity;
	public View mMainView;

	private Animation mOutAnim;
	private Animation mInAnim;
	private boolean mIsDismess = true;
	private boolean mIsLock = false;

	private View mBack;
	private View mMore;
	private View mBookmark;
	private ImageView mVBookmark;
	private ImageView iv_new_tip;
	private View mNote;
//	private ImageButton mSpeech;

	public HeadMenu(BaseMenuActivity activity) {
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
		mMainView = mActivity.getLayoutInflater().inflate(R.layout.read_head, null);

		mBack = mMainView.findViewById(R.id.ib_reader_back);
		mBack.setOnClickListener(this);
		mMore = mMainView.findViewById(R.id.ib_reader_more);
		mMore.setOnClickListener(this);
		iv_new_tip = (ImageView) mMainView.findViewById(R.id.iv_new_tip);
		mVBookmark = (ImageView)mMainView.findViewById(R.id.iv_bookmark);
		mBookmark = mMainView.findViewById(R.id.ib_reader_bookmark);
		mBookmark.setOnClickListener(this);
		mNote = mMainView.findViewById(R.id.ib_reader_note);
		mNote.setOnClickListener(this);
//		mSpeech = (ImageButton) mMainView.findViewById(R.id.ib_reader_speech);
//		mSpeech.setOnClickListener(this);
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
		if (mIsLock)
			return;
		if (mActivity.mApplication.isReadPdf) {
//			mSpeech.setVisibility(View.GONE);
			mBookmark.setVisibility(View.GONE);
		} else {
			if (FileUtils.isSpeech()) {
//				mSpeech.setVisibility(View.VISIBLE);
			}
		}

		if (mMainView.getVisibility() == View.VISIBLE) {
			mIsDismess = true;
			mMainView.startAnimation(mOutAnim);
		} else {
			mIsDismess = false;
			mMainView.setVisibility(View.VISIBLE);
			mMainView.startAnimation(mInAnim);
			final List list = mActivity.mApplication.BookTextView
					.getBookPageMarkList();
			if (list != null && !list.isEmpty()){
				mVBookmark.setImageResource(R.drawable.reader_bookmark_selected);
			} else {
				mVBookmark.setImageResource(R.drawable.reader_bookmark);
			}
			boolean isInviteNew = false;
			if(GeeBookLoader.getBookMgr() != null){
				isInviteNew= GeeBookLoader.getBookMgr().getIsNewByBusiCode(101);
			}
			if(isInviteNew){
				iv_new_tip.setVisibility(View.VISIBLE);
			} else {
				iv_new_tip.setVisibility(View.GONE);
			}
//			mSpeech.setBackgroundResource(mActivity.mIsSpeech ? R.drawable.reader_voice_selected: R.drawable.reader_voice);
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.ib_reader_back) {
			// ReaderApplication.Instance().closeWindow();
			mActivity.onBackClose();
		} else if (v.getId() == R.id.ib_reader_more) {
			MobclickAgent.onEvent(mActivity, EventName.READER_MORE);
			initPopupWindow();
		} else if (v.getId() == R.id.ib_reader_bookmark) {
			MobclickAgent.onEvent(mActivity, EventName.READER_ADDMARK);
			if (mActivity instanceof ReaderActivity) {
				final List<GBTextFixedPosition> list = mActivity.mApplication.BookTextView
						.getBookPageMarkList();
				if (list == null || list.isEmpty()) {
					((ReaderActivity) mActivity).addPageBookmark();
					mVBookmark.setImageResource(R.drawable.reader_bookmark_selected);
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
					mVBookmark.setImageResource(R.drawable.reader_bookmark);
				}
			}
		} else if (v.getId() == R.id.rl_reader_speech) {

		} else if (v.getId() == R.id.ib_reader_note) {
			MobclickAgent.onEvent(mActivity, EventName.READER_ADDNOTE);
			if (GeeBookLoader.getBookMgr() != null) {
				GeeBookLoader.getBookMgr().startReadNoteView(mActivity.mApplication.chapterName);
			} else {

				// UIUtil.showMessageText(mActivity, "跳往读书笔记页面");
			}
		}

	}

	private PopupWindow optionMenu;
	private View menuView;//菜单视图

	private void initPopupWindow() {
		if(menuView==null){
			menuView = LayoutInflater.from(mActivity).inflate(R.layout.reader_more_menu, null);
			optionMenu = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			optionMenu.setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(R.color
					.top_menu_normal)));
			optionMenu.setFocusable(true);
			optionMenu.setOutsideTouchable(true);
			View rl_reader_speech = menuView.findViewById(R.id.rl_reader_speech);
			if (mActivity instanceof PdfActivity) {
				rl_reader_speech.setVisibility(View.GONE);
			} else {
				rl_reader_speech.setVisibility(View.VISIBLE);
				rl_reader_speech.setOnTouchListener(this);
			}
			View rl_reader_commend = menuView.findViewById(R.id.rl_reader_commend);
			rl_reader_commend.setOnTouchListener(this);
			View rl_reader_share = menuView.findViewById(R.id.rl_reader_share);
			rl_reader_share.setOnTouchListener(this);
//			View rl_reader_report = menuView.findViewById(R.id.rl_reader_report);
//			rl_reader_report.setOnTouchListener(this);
			View rl_reader_invite = menuView.findViewById(R.id.rl_reader_invite);
			rl_reader_invite.setOnTouchListener(this);
		}
		boolean isInviteNew =GeeBookLoader.getBookMgr() != null? GeeBookLoader.getBookMgr()
				.getIsNewByBusiCode(101):false;
		if(isInviteNew){
			menuView.findViewById(R.id.iv_new_tip).setVisibility(View.VISIBLE);
		} else {
			menuView.findViewById(R.id.iv_new_tip).setVisibility(View.GONE);
		}
		optionMenu.showAsDropDown(mMore, 0, 0);
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
							// String assetsApk = "speech/SpeechService.apk";
							// if (!ApkInstaller.installFromAssets(mActivity,
							// assetsApk)) {
							// Toast.makeText(mActivity,
							// GBResource.resource("readerPage").getResource("install_fail").getValue(),
							// Toast.LENGTH_SHORT).show();
							// } else {
							// // isCheck = true;
							// mActivity.finish();
							// }
							SettingReadReaderActivity.actionView(mActivity,true);
//							if (GeeBookLoader.getBookMgr() != null) {
//								ApkInstaller.openDownloadWeb(mActivity,
//										GeeBookLoader.getBookMgr()
//												.getVoiceEngineUri());
//							} else {
//								UIUtil.showMessageText(mActivity,
//										GBResource.resource("readerPage")
//												.getResource("install_fail")
//												.getValue());
//							}
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				v.setBackgroundColor(mActivity.getResources().getColor(R.color.top_menu_focused));
				break;
			case MotionEvent.ACTION_UP :
				v.setBackgroundColor(mActivity.getResources().getColor(R.color.c110));
				if (optionMenu.isShowing()) {
					optionMenu.dismiss();
				}
				if(v.getId()==R.id.rl_reader_speech){
					doSpeech();
				} else if (v.getId() == R.id.rl_reader_commend) {
					doCommend();
				} else if(v.getId() == R.id.rl_reader_share){
					doshare();
				} else if(v.getId() == R.id.rl_reader_invite){
					doInvite();
				}
				break;
		}
		if (v instanceof ViewGroup)
			return true;
		return false;
	}

	private void doSpeech(){
		MobclickAgent.onEvent(mActivity, EventName.READER_SPEECH);
		mActivity.showDisMenu();
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
			mActivity.startSpeech();
		}
	}

	private void doshare(){
		mActivity.showDisMenu();
		if(GeeBookLoader.getBookMgr() != null){
			GeeBookLoader.getBookMgr().showShareBook();
		}

	}

	private void doCommend(){
		mActivity.showDisMenu();
		if(GeeBookLoader.getBookMgr() != null){
			GeeBookLoader.getBookMgr().showCommendBook();
		}

	}

//	private void doReport(){
//		mActivity.showDisMenu();
//		GeeBookLoader.getBookMgr().showReportBook();
//	}

	private void doInvite(){
		mActivity.showDisMenu();
		if(GeeBookLoader.getBookMgr() != null){
			GeeBookLoader.getBookMgr().showInviteBook();
		}

	}
}
