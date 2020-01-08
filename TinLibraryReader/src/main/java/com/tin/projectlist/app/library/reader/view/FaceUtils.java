package com.tin.projectlist.app.library.reader.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.geeboo.R;
import com.geeboo.read.view.poppanel.GridViewFaceAdapter;

/**
 * 类名： FaceUtils
 * 描述：
 * 创建者: lhz
 * 创建时间: 16-8-15.
 * 版本：
 * 修改者：
 * 修改日期：
 */
public class FaceUtils {
    private Context context;
    private GridView gridView;
    private EditText editText;
    private GridViewFaceAdapter mGVFaceAdapter;
    // 聊天表情支持
    private InputMethodManager imm;
    // 是否显示表情
//    public boolean isShowFace = false;
    private ImageView ivFace;


    public FaceUtils(Context context, GridView gridView, EditText editText, InputMethodManager imm, ImageView ivFace) {
        this.context = context;
        this.gridView = gridView;
        this.editText = editText;
        this.imm = imm;
        this.ivFace = ivFace;
    }

    public void init() {
        mGVFaceAdapter = new GridViewFaceAdapter(context);
        gridView.setAdapter(mGVFaceAdapter);
        gridView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // 插入的表情
                        SpannableString ss = new SpannableString(view.getTag()
                                .toString());
                        Drawable d = context.getResources().getDrawable(
                                (int) mGVFaceAdapter.getItemId(position));
                        float ff = context.getResources().getDisplayMetrics().density;
                        d.setBounds(0, 0, (int) (24 * ff), (int) (24 * ff));// 设置表情图片的显示大小
                        ImageSpan span = new ImageSpan(d,
                                ImageSpan.ALIGN_BOTTOM);
                        ss.setSpan(span, 0, view.getTag().toString().length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        // 在光标所在处插入表情
                        editText.getText().insert(editText.getSelectionStart(),ss);
                    }
                });
    }

    /**
     * 功能描述： 软键盘和表情切换<br>
     * 创建者： lihaozhou<br>
     * 创建日期：2014-6-18<br>
     *
     * @param
     */
    public void showOrHideIMM() {
        if (ivFace.getTag() == null) {
            // 隐藏软键盘
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            // 显示表情
            showFace();
        } else {
            // 显示软键盘
            imm.showSoftInput(editText, 0);
            // 隐藏表情
            hideFace();
        }
    }
    public void closeSoftInputFromWindow() {
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        if (ivFace.getTag() != null) {
            // 显示表情
            hideFace();
        }
    }
    public void showSoftInputFromWindow() {
        // 显示软键盘
        imm.showSoftInput(editText, 0);
        if (ivFace.getTag() != null) {
            // 显示表情
            hideFace();
        }
    }
    public void closeOrHideIMMShowSoftInput() {
        // 显示软键盘
        imm.showSoftInput(editText, 0);
        // 隐藏表情
        hideFace();
    }

    private void showFace() {
        ivFace.setImageResource(R.drawable.icon_keyboard);
        ivFace.setTag(1);
        gridView.setVisibility(View.VISIBLE);
    }

    private void hideFace() {
        ivFace.setImageResource(R.drawable.face);
        ivFace.setTag(null);
        gridView.setVisibility(View.GONE);
    }
}
