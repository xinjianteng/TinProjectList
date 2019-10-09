package com.tin.library.widget;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;

public class CommonDialogFragment<D extends ViewDataBinding> extends DialogFragment {

    private static final String TAG = CommonDialogFragment.class.getSimpleName();


    private @LayoutRes
    int layoutRes = BaseActivity.NO_ID;
    private int gravity = Gravity.CENTER;
    private Integer width;
    private Integer height;
    private HashMap<String, Object> variables;
    private OnViewCreateListener onViewCreateListener;
    private OnViewDismissListener onViewDismissListener;

    public static abstract class OnBtnClickListener {
        public void onConfirm() {
        }

        public void onCancle() {
        }

        public void onConfirm(String input) {
        }
    }

    public interface OnViewCreateListener {
        void onCreateView(View view);


    }

    public interface OnViewDismissListener {
        void onViewDismiss();
    }


    private D viewDataBinding;
    private Exception exception;

    public CommonDialogFragment() {
        exception = new Exception();
    }

    public CommonDialogFragment setLayoutRes(int layoutRes) {
        this.layoutRes = layoutRes;
        return this;
    }

    public CommonDialogFragment setWidth(int width) {
        this.width = width;
        return this;
    }

    public CommonDialogFragment setHeight(int height) {
        this.height = height;
        return this;
    }

    public CommonDialogFragment setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public void setOnViewCreateListener(OnViewCreateListener onViewCreateListener) {
        this.onViewCreateListener = onViewCreateListener;
    }

    public void setOnViewDismissListener(OnViewDismissListener onViewDismissListener) {
        this.onViewDismissListener = onViewDismissListener;
    }


    public void setVariables(HashMap<String, Object> variables) {
        this.variables = variables;
    }

    public D getViewDataBinding() {
        return viewDataBinding;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (layoutRes == BaseActivity.NO_ID) {
            BugglyMgr.postException(new Exception("activity " + getActivity() + ", tag " + getTag(), exception));
            return null;
        }
        if (viewDataBinding == null) {
            viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), layoutRes, null, false);
        }
        viewDataBinding.setVariable(BR.variables, variables);
        View root = viewDataBinding.getRoot();
        return root;
    }


    @Override
    public void onDestroyView() {
        if (viewDataBinding != null) {
            View root = viewDataBinding.getRoot();
            ((ViewGroup) root.getParent()).removeView(root);
        }
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0.3f;
        params.gravity = gravity;
        if (width != null) {
            params.width = width;
        } else {
            params.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.8);
        }
        if (height != null) {
            params.height = height;
        }
        window.setAttributes(params); //设置背景透明
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    @Override
    public void onResume() {
        super.onResume();
        if (onViewCreateListener != null) {
            onViewCreateListener.onCreateView(getView());
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onViewDismissListener != null) {
            onViewDismissListener.onViewDismiss();
        }
    }


}
