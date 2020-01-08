package com.tin.projectlist.app.library.reader.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.geeboo.R;

/**
 * 加载网页
 * @author yangn
 *
 */
public class WebReaderActivity extends BaseReaderActivity {

	public static void actionView(Context context, String url) {

		Intent intent = new Intent(context, WebReaderActivity.class);
		intent.putExtra("url", url);
		context.startActivity(intent);

	}

	WebView mWvContainer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		this.setContentView(R.layout.web_activity);
		mWvContainer = (WebView) this.findViewById(R.id.wv_);

		if (getIntent().getExtras() == null) {
			return;
		}

		String url = getIntent().getExtras().getString("url");
		mWvContainer.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				setProgress(newProgress * 100);
				super.onProgressChanged(view, newProgress);
			}
		});

		mWvContainer.loadUrl(url);

		mWvContainer.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mWvContainer.loadUrl(url);
				// return super.shouldOverrideUrlLoading(view, url);
				return true;
			}
		});


		super.onCreate(savedInstanceState);
	}
}
