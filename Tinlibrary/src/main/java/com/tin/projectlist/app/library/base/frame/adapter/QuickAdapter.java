package com.tin.projectlist.app.library.base.frame.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tin.projectlist.app.library.base.BR;
import com.tin.projectlist.app.library.base.R;

import java.util.List;


public class QuickAdapter<T> extends BaseQuickAdapter<T, QuickAdapter.BindingViewHolder> {

    public QuickAdapter(int layoutResId) {
        super(layoutResId);
    }

    public QuickAdapter(int layoutResId, List<T> datas) {
        super(layoutResId, datas);
    }


    @Override
    protected void convert(@NonNull BindingViewHolder helper, T item) {
        helper.getBinding().setVariable(BR.entity, item);
        helper.getBinding().setVariable(BR.listener, getOnItemClickListener());
        helper.getBinding().setVariable(BR.position, helper.getLayoutPosition() - getHeaderLayoutCount());
        helper.getBinding().setVariable(BR.adapter, this);
    }


    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        ViewDataBinding binding = DataBindingUtil.inflate(mLayoutInflater, layoutResId, parent, false);
        if (binding == null) {
            return super.getItemView(layoutResId, parent);
        }
        View view = binding.getRoot();
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding);
        return view;
    }

    public static class BindingViewHolder extends BaseViewHolder {

        public BindingViewHolder(View view) {
            super(view);
        }

        public ViewDataBinding getBinding() {
            return (ViewDataBinding) getConvertView().getTag(R.id.BaseQuickAdapter_databinding_support);
        }
    }
}
