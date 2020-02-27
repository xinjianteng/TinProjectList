package com.tin.projectlist.app.library.base.frame.permission;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cliff.libs.base.BR;
import com.cliff.libs.base.R;
import com.cliff.libs.base.mvp.IBasePresenter;
import com.cliff.libs.base.view.BaseDialogFragment;

public class PermissionDialogFragment extends BaseDialogFragment {

    private static final String KEY_MESSAGE = "message";
    private static final String KEY_CONFIRM = "confirm";

    public static PermissionDialogFragment getInstance(String message, String confirm) {
        PermissionDialogFragment fragment = new PermissionDialogFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        arguments.putString(KEY_MESSAGE, message);
        arguments.putString(KEY_CONFIRM, confirm);
        return fragment;
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewDataBinding inflate = DataBindingUtil.inflate(inflater, R.layout.fragment_permission, null, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            inflate.setVariable(BR.message, arguments.getString(KEY_MESSAGE));
            inflate.setVariable(BR.confirm, arguments.getString(KEY_CONFIRM, getResources().getString(R.string.str_confirm)));
        }
        inflate.setVariable(BR.onConfirmListener, (View.OnClickListener) v -> {
            dismissDialogFragment(PermissionDialogFragment.this);
            if (onClickListener != null) {
                onClickListener.onConfirmClick();
            }
        });

        inflate.setVariable(BR.onCancelListener, (View.OnClickListener) v -> {
            dismissDialogFragment(PermissionDialogFragment.this);
            if (onClickListener != null) {
                onClickListener.onCancelClick();
            }
        });
        return inflate.getRoot();
    }

    @Override
    public <T extends IBasePresenter> T bindPresenter() {
        return null;
    }

    public interface OnClickListener {
        void onConfirmClick();

        void onCancelClick();
    }
}
