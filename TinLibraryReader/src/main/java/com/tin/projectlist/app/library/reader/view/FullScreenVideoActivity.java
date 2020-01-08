package com.tin.projectlist.app.library.reader.view;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

import com.core.platform.GBLibrary;
import com.geeboo.R;

public class FullScreenVideoActivity extends Activity implements
        OnClickListener {
	private TextView mTvTime, mTvCurrnetTime;// 视频总时间
	private VideoView mVideoView = null;
	SeekBar mSeekBar = null;
	private View mParent = null;
	// 加载中
	private View mTvLoading;
	View mRdoGoBack, mRdoGoForward, mFullScreen;
	CheckBox mCkbStartOrPause = null;// 暂停&播放
	// 播放路径
	private Uri uri;
	// 播放菜单试图
	View mVideoControler = null;
	private boolean isHttp = false;
	Runnable fadeInGoneRunnable = new Runnable() {

		@Override
		public void run() {
			mVideoControler.startAnimation(AnimationUtils.loadAnimation(
					FullScreenVideoActivity.this, android.R.anim.fade_out));
			mVideoControler.setVisibility(View.GONE);

		}
	};
	//
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (!isNoPlaying && null != mSeekBar && null != mVideoView) {
				refreshProgress();
			}

		};
	};

	// 每搁一秒刷新进度,handle
	Runnable playRunnable = new Runnable() {

		@Override
		public void run() {
			for (;;) {// 每个三秒刷新进度
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mHandler.obtainMessage().sendToTarget();
				if (isNoPlaying) {
					break;
				}
			}

		}
	};
	// 每搁一秒刷新进度,handle
	Runnable loadingRunnable = new Runnable() {

		@Override
		public void run() {
			for (;;) {// 每个三秒刷新进度
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mHandlerLoading.obtainMessage().sendToTarget();
				if (isFinishPlay) {
					break;
				}
			}

		}
	};
	Handler mHandlerLoading = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			if (mCkbStartOrPause.isChecked()) {
				// 当前处于播放,checkbox选中
				// mTvLoading.setVisibility(View.VISIBLE);

				if (mVideoView.getBufferPercentage() <= (mVideoView
						.getCurrentPosition() * 100 / mVideoView.getDuration() + 3)) {

					mTvLoading.setVisibility(View.VISIBLE);

				} else {
					mTvLoading.setVisibility(View.GONE);

				}
				// Log.i("after",
				// "buffer  +"
				// + mVideoView.getBufferPercentage()
				// + "  current++"
				// + (mVideoView.getCurrentPosition() * 100 / (mVideoView
				// .getDuration())));
			}
			// }

		}
	};
	volatile boolean isNoPlaying = true;// 线程共享参数
	private int myFullScreenFlag;// 控制全屏显示
	// 播放暂停按钮是否播放
	// private boolean isVideoPlaying = false;
	// 上一级是否暂停
	private boolean isPause = false;
	// 是否i结束放映
	volatile boolean isFinishPlay = false;
	// 缓存进度条
	private ProgressBar mProgressbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_popup_layout_activity);
		initView();
		uri = (Uri) getIntent().getExtras().get("uri");
		Log.i("after", "dedao字符串" + uri.getPath() + " host" + uri.getHost()
				+ " aut" + uri.getAuthority() + "sm" + uri.getScheme());
		mProgressbar.setProgress(0);
		if (uri.getScheme().startsWith("http")) {
			isHttp = true;

		} else {
			isHttp = false;
			mProgressbar.setProgress(100);
		}
		int lastProgress = getIntent().getExtras().getInt("progress");
		isPause = getIntent().getExtras().getBoolean("ispause");
		mVideoView.setVideoURI(uri);
		final GBAndroidLibrary gbLibrary = (GBAndroidLibrary) GBLibrary
				.Instance();
		myFullScreenFlag = gbLibrary.ShowStatusBarOption.getValue() ? 0
				: WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				myFullScreenFlag);
		// chenckbox控制
		mCkbStartOrPause
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {// 根据checkbox开始播放shipin

						if (isChecked) {
							mVideoView.start();
							refreshProgress();

						} else {
							if (mVideoView.isPlaying()) {
								mVideoView.pause();
							}

						}

					}
				});
		// 触摸跳出控制菜单
		mVideoView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mVideoControler.getVisibility() == View.GONE) {
					mVideoControler.startAnimation(AnimationUtils
							.loadAnimation(FullScreenVideoActivity.this,
									android.R.anim.fade_in));
					mVideoControler.setVisibility(View.VISIBLE);
					new Handler().postDelayed(fadeInGoneRunnable, 3000);
				}
				return false;
			}
		});
		// 视频播放完成
		mVideoView.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				isNoPlaying = true;
				isFinishPlay = true;
				mSeekBar.setProgress(0);
				mCkbStartOrPause.setChecked(false);
				mTvCurrnetTime.setText(""
						+ getMyTime((mVideoView.getDuration()) / 1000));
				// setButtonState(true);
				// mVideoView.pause();
			}
		});
		// 视频播放错误
		mVideoView.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				isNoPlaying = true;
				isFinishPlay = true;
				return false;
			}
		});
		mSeekBar.setOnSeekBarChangeListener(listener);
		// new Thread(loadingRunnable).start();// 首次进去就启动
		mVideoView.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				isNoPlaying = false;
				isFinishPlay = false;
				new Thread(playRunnable).start();
				mTvLoading.setVisibility(View.GONE);

				//
				if (!isPause) {
					// setButtonState(false);
					mCkbStartOrPause.setChecked(true);
				}

				// 设置时间格式
				int sumTime = mVideoView.getDuration() / 1000;

				mTvTime.setText("/" + getMyTime(sumTime));

			}
		});

		mVideoView.requestFocus();
		mTvLoading.setVisibility(View.VISIBLE);
		gotoProgress(lastProgress);// 直接接上面的刻度进行播放
		refreshProgress();
		if (isPause) {
			// 需要暂停

			mVideoView.pause();
			mCkbStartOrPause.setChecked(false);
		}

	}

	/**
	 *
	 * 功能描述： 设置时间格式<br>
	 * 创建者： 张永宏<br>
	 * 创建日期：2015-4-13<br>
	 *
	 * @param
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// remove
	}

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

	/**
	 *
	 * 功能描述： 实例化控件<br>
	 * 创建者： 张永宏<br>
	 * 创建日期：2015-4-7<br>
	 *
	 * @param
	 */
	public void initView() {
		mVideoView = (VideoView) findViewById(R.id.vv);
		mSeekBar = (SeekBar) findViewById(R.id.skb_video_ctr);
		// 控制选择控件
		mRdoGoBack = findViewById(R.id.rdo_go_back);
		mRdoGoBack.setOnClickListener(this);
		mRdoGoForward = findViewById(R.id.rdo_go_forward);
		mRdoGoForward.setOnClickListener(this);
		mFullScreen = findViewById(R.id.rdo_fullscreen);
		mFullScreen.setOnClickListener(this);
		mCkbStartOrPause = (CheckBox) findViewById(R.id.ckb_start_or_pause);
		mCkbStartOrPause.setOnClickListener(this);
		mVideoControler = findViewById(R.id.ll_video_menu_container);// 菜单
		// 时间\
		mTvTime = (TextView) findViewById(R.id.tv_sum_time);
		mTvCurrnetTime = (TextView) findViewById(R.id.tv_current_time);
		// jiazaizhong
		mTvLoading = findViewById(R.id.tv_loading);
		// 进度条
		mProgressbar = (ProgressBar) findViewById(R.id.pb_buffer);
	}

	// 进度条变化监听
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
				// if (mVideoView.getBufferPercentage() <= (mVideoView
				// .getCurrentPosition() * 100 / mVideoView.getDuration() + 3))
				// {
				//
				// mTvLoading.setVisibility(View.VISIBLE);
				//
				// } else {
				// mTvLoading.setVisibility(View.GONE);
				//
				// }
			}

		}
	};

	// 设置video进度
	void gotoProgress(int progress) {
		mVideoView.seekTo(progress);

		if (!mVideoView.isPlaying()) {
			mVideoView.start();
			mCkbStartOrPause.setChecked(true);
			// setButtonState(false);
		}

	}

	// 刷新进度
	void refreshProgress() {
		mSeekBar.setProgress((int) ((float) mVideoView.getCurrentPosition()
				/ mVideoView.getDuration() * 100));
		// 同时刷新时间
		// 设置时间格式
		int videoCurrentShTime = mVideoView.getCurrentPosition() / 1000;

		mTvCurrnetTime.setText(getMyTime(videoCurrentShTime));
		if (isHttp) {
			mProgressbar.setProgress(mVideoView.getBufferPercentage());
			Log.i("after", "PERCENTEtage" + mVideoView.getBufferPercentage());
		}

	}

	// 暂停

	public void onPauseVideo() {

		isNoPlaying = true;
		isFinishPlay = false;
		if (mVideoView.isPlaying()) {
			mCkbStartOrPause.setChecked(false);
			mVideoView.suspend();
			mVideoView.stopPlayback();
			mVideoView = null;

		}

	};

	// ji需播
	public void goOn() {
		if (null != mVideoView) {
			mVideoView.resume();
			mCkbStartOrPause.setChecked(true);
			// setButtonState(false);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		refreshProgress();
		if (v.getId() == R.id.rdo_go_forward) {// 快进
			if (mVideoView.canSeekForward()) {
				gotoProgress(mVideoView.getCurrentPosition() + 5000);
			}
			// if (mVideoView.getBufferPercentage() <= (mVideoView
			// .getCurrentPosition() * 100 / mVideoView.getDuration() + 3)) {
			//
			// mTvLoading.setVisibility(View.VISIBLE);
			//
			// } else {
			// mTvLoading.setVisibility(View.GONE);
			//
			// }
		} else if (v.getId() == R.id.rdo_go_back) {// 后退
			if (mVideoView.canSeekBackward()) {
				gotoProgress(mVideoView.getCurrentPosition() - 5000);
			}

		} else if (v.getId() == R.id.rdo_fullscreen) {// tuichu 全屏
			// 传递当前进度,然后finish
			// 回调接口

			Intent intent = new Intent(FullScreenVideoActivity.this,
					ReaderActivity.class);
			if (null != mVideoView) {
				mVideoView.pause();
			}
			intent.putExtra("progress", mVideoView.getCurrentPosition());
			intent.putExtra("ispause", !mCkbStartOrPause.isChecked());
			setResult(3, intent);
			mVideoView = null;
			finish();

		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (null != mVideoView) {
			mVideoView.pause();
		}
		Intent intent = new Intent(FullScreenVideoActivity.this,
				ReaderActivity.class);
		intent.putExtra("progress", mVideoView.getCurrentPosition());
		intent.putExtra("ispause", !mCkbStartOrPause.isChecked());
		setResult(3, intent);

		mVideoView = null;
		finish();

	}

	// 按下返回键

}
