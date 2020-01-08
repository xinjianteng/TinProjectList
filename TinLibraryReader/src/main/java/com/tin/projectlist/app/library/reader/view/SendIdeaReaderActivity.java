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
import android.widget.Toast;

import com.core.common.GBResource;
import com.core.common.util.Cookie;
import com.geeboo.R;
import com.geeboo.utils.UIUtil;
import com.geeboo.utils.mail.SimpleMailSender;

public class SendIdeaReaderActivity extends BaseReaderActivity implements OnClickListener {

	EditText etAnnotationText = null;

	private int mId = -1;
	private String mText = "";

	Cookie cookie = null;

	public static void actionView(Activity context, int id, String text) {
		Intent intent = new Intent(context, SendIdeaReaderActivity.class);
		intent.putExtra("ID", id);
		intent.putExtra("TEXT", text);
		context.startActivityForResult(intent, 3);
		context.overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		this.setContentView(R.layout.send_idea_activity);
		etAnnotationText = (EditText) this
				.findViewById(R.id.ed_annotation_text);

		findViewById(R.id.btn_save_annotation).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);

		cookie = new Cookie(this, Cookie.APP_CFG);
		String previousIdea = cookie.getVal("previousIdea", null);
		if (null != previousIdea) {
			etAnnotationText.setText(previousIdea);
		}

		super.onCreate(savedInstanceState);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_save_annotation) {

			if ("".equals(etAnnotationText.getText().toString())) {
				Toast.makeText(this, GBResource.resource("readerPage").getResource("write_info").getValue(), Toast.LENGTH_SHORT).show();
				return;
			}

			UIUtil.dialogTask(this, "正在发送,请稍候...",
					new UIUtil.ICallback<Boolean>() {

						@Override
						public Boolean execTask() {
							int failCount = 0;
							String[] mailIn = new String[] { "hsj@geeboo.cn",
									"ygn@geeboo.cn" };

							SimpleMailSender sms = new SimpleMailSender();

							for (int i = 0; i < mailIn.length; i++) {

								if (!sms.sendMailSpeedy(mailIn[i],
										"来自ReaderTest.apk的反馈", etAnnotationText
												.getText().toString())) {
									++failCount;
									break;
								}
							}

							return failCount == 0;
						}

						@Override
						public void handUI(Boolean result) {
							if (result) {
								cookie.putVal("previousIdea", null);
								Toast.makeText(SendIdeaReaderActivity.this, GBResource.resource("readerPage").getResource("send_success").getValue(), Toast.LENGTH_SHORT)
										.show();
								finish();
							} else {
								cookie.putVal("previousIdea", etAnnotationText
										.getText().toString());
								Toast.makeText(SendIdeaReaderActivity.this,
										GBResource.resource("readerPage").getResource("send_fail_again").getValue(), Toast.LENGTH_SHORT).show();
							}

						}
					});
		} else if (v.getId() == R.id.btn_cancel) {
			this.finish();
		}

	}

	@Override
	public void finish() {
		InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		input.hideSoftInputFromWindow(etAnnotationText.getWindowToken(), 0);
		super.finish();
	}

}
