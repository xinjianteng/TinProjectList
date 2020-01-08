package com.tin.projectlist.app.library.reader.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

import com.core.log.L;
import com.geeboo.R;

public class VideoViewListener implements OnClickListener {

	final String TAG = "VideoViewListener";
	VideoView mVideoView = null;
	SeekBar mSeekBar = null;
	private View mParent = null;
	// 设置时间显示
	private TextView mTvSumTime, mTvCurrentTime;
	View mRdoGoBack, mRdoGoForward, mFullScreen;
	boolean isFull = false;

	private Rect mRect = null;
	CheckBox mCkbStartOrPause = null;
	Context mContext = null;
	View mVideoControler = null;
	// 播放路径
	private Uri mUri;

	// 网络视频加载中
	private View mTvLoading;
	// 缓冲进度条
	private ProgressBar mProgressBar;

	@SuppressLint("NewApi")
	public VideoViewListener(View parent, View view, Uri uri, Rect rect) {
		mContext = view.getContext();
		mParent = parent;
		mRect = rect;
		mUri = uri;
		mCkbStartOrPause = (CheckBox) view
				.findViewById(R.id.ckb_start_or_pause);
		mVideoControler = view.findViewById(R.id.ll_video_menu_container);

		mCkbStartOrPause
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {

						if (isChecked) {
							mVideoView.start();
							refreshProgress();
						} else {
							mVideoView.pause();
						}

					}
				});
		mRdoGoBack = view.findViewById(R.id.rdo_go_back);
		mRdoGoBack.setOnClickListener(this);
		mRdoGoForward = view.findViewById(R.id.rdo_go_forward);
		mRdoGoForward.setOnClickListener(this);
		mFullScreen = view.findViewById(R.id.rdo_fullscreen);
		mFullScreen.setOnClickListener(this);

		mVideoView = (VideoView) view.findViewById(R.id.vv);
		// jiazai
		mTvLoading = view.findViewById(R.id.tv_loading);
		// 缓冲进度
		mProgressBar = (ProgressBar) view.findViewById(R.id.pb_buffer);
		mProgressBar.setProgress(0);
		if (!mUri.getScheme().startsWith("http")) {// 网络请求
			mProgressBar.setProgress(100);
		}
		// 时间显示
		mTvSumTime = (TextView) view.findViewById(R.id.tv_sum_time);
		mTvCurrentTime = (TextView) view.findViewById(R.id.tv_current_time);
		mVideoView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mVideoControler.getVisibility() == View.GONE) {
					mVideoControler.startAnimation(AnimationUtils
							.loadAnimation(mContext, android.R.anim.fade_in));
					mVideoControler.setVisibility(View.VISIBLE);
					new Handler().postDelayed(fadeInGoneRunnable, 3000);
				}
				return false;
			}
		});
		mVideoView.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// 播放完成时间设为满个
				mTvCurrentTime.setText(""
						+ getMyTime((mVideoView.getDuration()) / 1000));
				isNoPlaying = true;
				mSeekBar.setProgress(100);
				mCkbStartOrPause.setChecked(false);

			}
		});
		mVideoView.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				isNoPlaying = true;
				return false;
			}
		});
		// 测试videoview下载缓冲监听
//		mVideoView.setOnInfoListener(new OnInfoListener() {
//
//			@Override
//			public boolean onInfo(MediaPlayer mp, int what, int extra) {
//				// TODO Auto-generated method stub
//
//				return false;
//			}
//		});

		mSeekBar = (SeekBar) view.findViewById(R.id.skb_video_ctr);
		mSeekBar.setOnSeekBarChangeListener(listener);
		// mVideoView.setVideoPath(path);
		mVideoView.setVideoURI(uri);

		mVideoView.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				isNoPlaying = false;
				new Thread(playRunnable).start();
				if (lastVideoPause) {

				} else {
					mCkbStartOrPause.setChecked(true);
				}
				// L.i("after", "onpreared");
				// 设置时间格式
				int sumTime = mVideoView.getDuration() / 1000;

				mTvSumTime.setText("/" + getMyTime(sumTime));

				mTvLoading.setVisibility(View.GONE);
			}
		});
		mVideoView.start();
		mVideoView.requestFocus();
		// vv.set
	}

	Runnable fadeInGoneRunnable = new Runnable() {

		@Override
		public void run() {
			mVideoControler.startAnimation(AnimationUtils.loadAnimation(
					mContext, android.R.anim.fade_out));
			mVideoControler.setVisibility(View.GONE);

		}
	};

	/**
	 * 功能描述： 暂停<br>
	 * 创建者： jack<br>
	 * 创建日期：2015-3-26<br>
	 *
	 * @param
	 */
	public void onPause() {

		isNoPlaying = true;
		if (mVideoView.isPlaying()) {
			// mCkbStartOrPause.setChecked(false);
			mVideoView.suspend();
			mVideoView.stopPlayback();
			mVideoView = null;

		}

	};

	/**
	 * 功能描述： 继续播放<br>
	 * 创建者： jack<br>
	 * 创建日期：2015-3-26<br>
	 *
	 * @param
	 */
	public void goOn() {
		if (null != mVideoView) {
			// L.i("after", "goon");
			mVideoView.resume();
			mCkbStartOrPause.setChecked(true);
		}
	}

	OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
			if (fromUser) {
				int newProgress = mVideoView.getDuration() * progress / 100;
				gotoProgress(newProgress);
			}

		}
	};

	void gotoProgress(int progress) {
		mVideoView.seekTo(progress);
		// s mVideoView.setPressed(true)
		// L.e("videoacti", "mVideoView.isShown()" + mVideoView.isPlaying());
		if (!mVideoView.isPlaying()) {
			mVideoView.start();
			mCkbStartOrPause.setChecked(true);
		}
		// mVideoView.refreshDrawableState();
		// mVideoView.buildLayer();

	}

	@Override
	public void onClick(View v) {
		refreshProgress();
		if (v.getId() == R.id.rdo_go_forward) {
			if (mVideoView.canSeekForward()) {
				gotoProgress(mVideoView.getCurrentPosition() + 5000);
			}

		} else if (v.getId() == R.id.rdo_go_back) {
			if (mVideoView.canSeekBackward()) {
				gotoProgress(mVideoView.getCurrentPosition() - 5000);
			}

		} else if (v.getId() == R.id.rdo_fullscreen) {
			// if (!isFull) {// 设置RelativeLayout的全屏模式
			// GBAndroidLibrary gbl = ((GBAndroidLibrary) GBAndroidLibrary
			// .Instance());
			// gbl.getActivity().setRequestedOrientation(
			// ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			//
			// RelativeLayout.LayoutParams layoutParams = new
			// RelativeLayout.LayoutParams(
			// gbl.getPixelWidth(), gbl.getPixelHeight());
			// layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			// layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			// layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			// layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			// mVideoView.setLayoutParams(layoutParams);

			// isFull = true;// 改变全屏/窗口的标记
			// 跳到另外一个视频显示界面

			// refreshProgress();
			// mVideoView.pause();
			Intent intent = new Intent(mContext, FullScreenVideoActivity.class);
			intent.putExtra("uri", mUri);
			// L.i("after", "地址++++++" + mUri);
			// 需要传递进度
			// if (mVideoView.canSeekForward()) {
			if (null != mVideoView) {
				mVideoView.pause();
			}
			intent.putExtra("progress", mVideoView.getCurrentPosition());// int类型
			// 是否暂停中
			intent.putExtra("ispause", !mCkbStartOrPause.isChecked());// true暂停.false,执行
			// mVideoView.stopPlayback();

			// mContext.startActivity(intent);
			((ReaderActivity) mContext).startActivityForResult(intent, 3);

			// } else {// 设置RelativeLayout的窗口模式
			// GBAndroidLibrary gbl = ((GBAndroidLibrary) GBAndroidLibrary
			// .Instance());
			// gbl.getActivity().setRequestedOrientation(
			// ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			// RelativeLayout.LayoutParams layoutParams = new
			// RelativeLayout.LayoutParams(
			// mRect.right - mRect.left - 2, mRect.bottom - mRect.top
			// - 2);
			// layoutParams.setMargins(mRect.left - 1, mRect.top - 1, 0, 0);
			// mVideoView.setLayoutParams(layoutParams);
			// isFull = false;// 改变全屏/窗口的标记
			// }
		}

	}

	void refreshProgress() {
		mSeekBar.setProgress((int) ((float) mVideoView.getCurrentPosition()
				/ mVideoView.getDuration() * 100));
		int videoCurrentShTime = mVideoView.getCurrentPosition() / 1000;
		mTvCurrentTime.setText(getMyTime(videoCurrentShTime));
		if (mUri.getScheme().startsWith("http")) {
			mProgressBar.setProgress(mVideoView.getBufferPercentage());
			// Log.i("after", "PERCENTEtage" +
			// mVideoView.getBufferPercentage());
		}
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (!isNoPlaying && null != mSeekBar && null != mVideoView) {
				refreshProgress();
			}

		};
	};

	volatile boolean isNoPlaying = true;

	Runnable playRunnable = new Runnable() {

		@Override
		public void run() {
			for (;;) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mHandler.obtainMessage().sendToTarget();
				if (isNoPlaying) {
					L.e(TAG, "play runnable exit");
					break;
				}
			}

		}
	};
	private boolean lastVideoPause = false;

	public void playFromProgress(int progress, boolean isPause) {
		gotoProgress(progress);

		if (isPause) {
			lastVideoPause = true;
			mVideoView.pause();// 是否需要暂停
			mCkbStartOrPause.setChecked(false);
		} else {
			lastVideoPause = false;
		}

		refreshProgress();
	}

	/**
	 *
	 * 功能描述： 设置视频播放时间,格式<br>
	 * 创建者： 张永宏<br>
	 * 创建日期：2015-4-14<br>
	 *
	 * @param
	 */
	public String getMyTime(int sumTime) {
		String strTime = "";
		// 设置时间格式

		int h = sumTime / 3600;
		int m = sumTime % 3600 / 60;
		int s = sumTime % 3600 % 60;
		if (h > 0) {
			strTime = strTime + h + ":";
			if (m < 10) {
				strTime = strTime + "0" + m + ":";
				if (s < 10) {
					strTime = strTime + "0" + s;
				} else {
					strTime = strTime + s;
				}
			} else {
				strTime = strTime + m + ":";
				if (s < 10) {
					strTime = strTime + "0" + s;
				} else {
					strTime = strTime + s;
				}
			}
		} else {
			// strTime = strTime + h + ":";
			if (m < 10) {
				strTime = strTime + "0" + m + ":";
				if (s < 10) {
					strTime = strTime + "0" + s;
				} else {
					strTime = strTime + s;
				}
			} else {
				strTime = strTime + m + ":";
				if (s < 10) {
					strTime = strTime + "0" + s;
				} else {
					strTime = strTime + s;
				}
			}
		}
		return strTime;
	}
}
