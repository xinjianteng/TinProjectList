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
import com.geeboo.read.model.book.Annotations;
import com.geeboo.read.view.action.AnnotationOptype;
import com.geeboo.utils.UIUtil;

public class AnnotationActivity extends Activity implements OnClickListener {

    EditText etAnnotationText = null;

    private int mId = -1;
    private String mText = "";

    public static void actionView(Activity context, int id, String text) {
        Intent intent = new Intent(context, AnnotationActivity.class);
        intent.putExtra("ID", id);
        intent.putExtra("TEXT", text);
        context.startActivityForResult(intent, 3);
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setContentView(R.layout.annotation_edit_activity);
        etAnnotationText = (EditText) this.findViewById(R.id.ed_annotation_text);

        findViewById(R.id.btn_save_annotation).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);

        if (getIntent().getExtras() != null) {
            mId = getIntent().getIntExtra("ID", -1);
            mText = getIntent().getStringExtra("TEXT");
            etAnnotationText.setText(mText);

            String titleText = getIntent().getExtras().getString("titleText");
            if (null != titleText) {
                ((TextView) this.findViewById(R.id.txt_title)).setText(titleText);
            }
        }

        /*
         * new Handler().postDelayed(new Runnable() {
         * @Override public void run() { etAnnotationText.requestFocus();
         * InputMethodManager input = (InputMethodManager)
         * getSystemService(Context.INPUT_METHOD_SERVICE);
         * input.showSoftInput(etAnnotationText, 0); } }, 300);
         */

        // input.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save_annotation) {
            if (mId == -1) {
                GBApplication.Instance().runAction(ActionCode.SELECTION_NOTE_ANNOTATION, AnnotationOptype.INSERT,
                        Annotations.ANNOTATION, etAnnotationText.getText().toString(),0);
                this.finish();
            } else if (!mText.equals(etAnnotationText.getText().toString())) {
                GBApplication.Instance().runAction(ActionCode.SELECTION_NOTE_ANNOTATION, AnnotationOptype.UPDATE, mId,
                        etAnnotationText.getText().toString(),0);
                this.finish();

            } else {
                UIUtil.showMessageText(this, GBResource.resource("readerPage").getResource("nodeAnnotationPlease").getValue());
            }
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
