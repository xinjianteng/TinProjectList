package com.tin.projectlist.app.library.reader.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.core.common.GBResource;
import com.core.domain.GBApplication;
import com.geeboo.R;
import com.geeboo.read.controller.ActionCode;
import com.geeboo.utils.UIUtil;

public class ShareTextReaderActivity extends BaseReaderActivity implements OnClickListener {

	EditText etShareText = null;

	private String mText = "";
	private int mKind = -1;

	public static void actionView(Activity context, final int kind, String text) {
		Intent intent = new Intent(context, ShareTextReaderActivity.class);
		intent.putExtra("KIND", kind);
		intent.putExtra("TEXT", text);

		// intent.putExtra("TITLE", title);
		context.startActivity(intent);
		// context.startActivityForResult(intent, 3);
		context.overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		this.setContentView(R.layout.share_text_activity);
		etShareText = (EditText) this.findViewById(R.id.ed_share_text);

		findViewById(R.id.btn_send_text).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);

		if (getIntent().getExtras() != null) {
			final int kind = getIntent().getIntExtra("KIND", -1);
			mKind = kind;
			mText = getIntent().getStringExtra("TEXT");
			// etShareText.setText("#阅光宝盒#\n");

			etShareText.append(mText);
			// etShareText.append("选自《");
			// etShareText.append("");
			// etShareText.append("");
			// L.e("shareTextActivity", "get kind" + kind);
			if (kind == R.id.ll_share_sina_weibo) {
				((TextView) this.findViewById(R.id.txt_title))
						.setText("分享到新浪微博");
			} else if (kind == R.id.ll_share_renren) {
				((TextView) this.findViewById(R.id.txt_title))
						.setText("分享到人人网");
			} else if (kind == R.id.ll_share_qq_space) {
				((TextView) this.findViewById(R.id.txt_title))
						.setText("分享到QQ空间");
			} else if (kind == R.id.ll_share_tencent_weibo) {
				((TextView) this.findViewById(R.id.txt_title))
						.setText("分享到腾讯微博");

			}

		}

		/*
		 * new Handler().postDelayed(new Runnable() {
		 *
		 * @Override public void run() { etShareText.requestFocus();
		 * InputMethodManager input = (InputMethodManager)
		 * getSystemService(Context.INPUT_METHOD_SERVICE);
		 * input.showSoftInput(etShareText, 0);
		 *
		 * } }, 300);
		 */

		// input.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

		super.onCreate(savedInstanceState);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_send_text) {
			if (mKind == R.id.ll_share_sina_weibo) {
				GBApplication.Instance().runAction(ActionCode.SHARE_SINA_WEIBO,
						etShareText.getText().toString());
				this.finish();
			} else if (mKind == R.id.ll_share_renren) {
//				GBApplication.Instance().runAction(ActionCode.SHARE_RENN,
//						etShareText.getText().toString());
				this.finish();
			} else if (mKind == R.id.ll_share_qq_space) {

//				final Bundle params = new Bundle();
//				params.putString(Tencent.SHARE_TO_QQ_TITLE, etShareText
//						.getText().toString());
//				params.putString(Tencent.SHARE_TO_QQ_SUMMARY, "from geeboo");
//				params.putString(Tencent.SHARE_TO_QQ_TARGET_URL,
//						"http://www.geeboo.cn");
//
//				ArrayList<String> imageUrls = new ArrayList<String>();
//				params.putStringArrayList(Tencent.SHARE_TO_QQ_IMAGE_URL,
//						imageUrls);
//
//				Tencent tencent = Tencent.createInstance("222222", this);
//				doShareToQzone(params, tencent);
				this.finish();
			} else if (mKind == R.id.ll_share_tencent_weibo) {
//				GBApplication.Instance().runAction(
//						ActionCode.SHARE_TENCENT_WEIBO,
//						etShareText.getText().toString());

//				String expireStr = Util.getSharePersistent(this, "EXPIRES_IN");

//				if (null == expireStr || "".equals(expireStr)) {
//					// ((Button) this.findViewById(R.id.btn_send_text))
//					// .setText("授权");
//
//				} else {
				this.finish();
//				}

			} else {
				UIUtil.showMessageText(this, GBResource.resource("readerPage").getResource("writeNote").getValue());
			}
		} else if (v.getId() == R.id.btn_cancel) {

			this.finish();
		}

	}

	/**
	 * 用异步方式启动QQ空间分享
	 *
	 */
//	private void doShareToQzone(final Bundle params, final Tencent tencent) {
//		final Activity activity = ShareTextReaderActivity.this;
//		new Thread() {
//			public void run() {
//				if(null==tencent){
//					return;
//				}
//				tencent.shareToQzone(activity, params, new IUiListener() {
//
//					@Override
//					public void onCancel() {
//
//						UIUtil.showMessageText(activity, "onCancel");
//					}
//
//					@Override
//					public void onComplete(JSONObject response) {
//						// TODO Auto-generated method stub
//						UIUtil.showMessageText(activity, "onComplete: "
//								+ response.toString());
//					}
//
//					@Override
//					public void onError(UiError e) {
//						// TODO Auto-generated method stub
//						UIUtil.showMessageText(activity, "onComplete: "
//								+ e.errorMessage);
//					}
//
//				});
//			};
//		}.start();
//
//	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	@Override
	public void finish() {
		InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		input.hideSoftInputFromWindow(etShareText.getWindowToken(), 0);
		super.finish();
	}
}
