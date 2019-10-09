package com.tin.library.widget;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;

import com.tin.library.R;

import java.util.HashMap;

public class DialogUtil {

    /***
     * 普通确认对话框
     * @param fragmentManager
     * @param title
     * @param tag
     * @param btnClickListener
     */

    public static CommonDialogFragment showConfirmDialog(FragmentManager fragmentManager, String title, String tag, CommonDialogFragment.OnBtnClickListener btnClickListener) {
        return showConfirmDialog(fragmentManager, title, null, null, tag, btnClickListener);
    }


    public static CommonDialogFragment showConfirmDialog(FragmentManager fragmentManager, String title, String leftButtonText, String rightButtonText, String tag, CommonDialogFragment.OnBtnClickListener btnClickListener) {
        CommonDialogFragment commonDialogFragment = new CommonDialogFragment();
        commonDialogFragment.setLayoutRes(R.layout.fragment_simple_confirm);
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("listener", (View.OnClickListener) view -> {
            dismissDialogFragment(commonDialogFragment, fragmentManager);
            int i = view.getId();
            if (i == R.id.tv_left) {
                if (btnClickListener != null) {
                    btnClickListener.onConfirm();
                }

            } else if (i == R.id.tv_right) {
                if (btnClickListener != null) {
                    btnClickListener.onCancle();
                }
            }
        });
        variables.put("title", title);
        if (!TextUtils.isEmpty(leftButtonText)) {
            variables.put("leftButtonText", leftButtonText);
        }
        if (!TextUtils.isEmpty(rightButtonText)) {
            variables.put("rightButtonText", rightButtonText);
        }
        commonDialogFragment.setVariables(variables);
        showDialogFragment(commonDialogFragment, fragmentManager, tag);
        return commonDialogFragment;
    }


    /***
     * 普通确认对话框 带title
     * @param fragmentManager
     * @param title
     * @param content
     * @param tag
     * @param btnClickListener
     */
    public static CommonDialogFragment showConfirmDialogWithTitle(FragmentManager fragmentManager, String title, String content, String tag, CommonDialogFragment.OnBtnClickListener btnClickListener) {
        return showConfirmDialogWithTitle(fragmentManager, title, content, null, null, tag, btnClickListener);
    }

    /***
     *
     * @param fragmentManager
     * @param title
     * @param content
     * @param leftButtonText
     * @param rightButtonText
     * @param tag
     * @param btnClickListener
     */
    public static CommonDialogFragment showConfirmDialogWithTitle(FragmentManager fragmentManager, String title, String content, String leftButtonText, String rightButtonText, String tag, CommonDialogFragment.OnBtnClickListener btnClickListener) {
        CommonDialogFragment commonDialogFragment = new CommonDialogFragment();
        commonDialogFragment.setLayoutRes(R.layout.fragment_simple_confirm_with_title);
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("listener", (View.OnClickListener) view -> {
            dismissDialogFragment(commonDialogFragment, fragmentManager);
            int i = view.getId();
            if (i == R.id.tv_left) {
                if (btnClickListener != null) {
                    btnClickListener.onConfirm();
                }

            } else if (i == R.id.tv_right) {
                if (btnClickListener != null) {
                    btnClickListener.onCancle();
                }

            }
        });
        variables.put("title", title);
        variables.put("content", content);
        if (!TextUtils.isEmpty(leftButtonText)) {
            variables.put("leftButtonText", leftButtonText);
        }
        if (!TextUtils.isEmpty(rightButtonText)) {
            variables.put("rightButtonText", rightButtonText);
        }
        commonDialogFragment.setVariables(variables);
        showDialogFragment(commonDialogFragment, fragmentManager, tag);
        return commonDialogFragment;
    }


    /***
     * 普通编辑对话框
     * @param fragmentManager
     * @param title
     * @param hint
     * @param tag
     * @param btnClickListener
     */

    public static void showEditDialog(FragmentManager fragmentManager, String title, String hint, String tag, CommonDialogFragment.OnBtnClickListener btnClickListener) {
        showEditDialog(fragmentManager, title, hint, null, tag, btnClickListener);
    }

    /***
     *  普通编辑对话框
     * @param fragmentManager
     * @param title
     * @param hint
     * @param leftButtonText
     * @param tag
     * @param btnClickListener
     */
    public static void showEditDialog(FragmentManager fragmentManager, String title, String hint, String leftButtonText, String tag, CommonDialogFragment.OnBtnClickListener btnClickListener) {
        CommonDialogFragment<FragmentSimpleInputBinding> commonDialogFragment = new CommonDialogFragment();
        commonDialogFragment.setLayoutRes(R.layout.fragment_simple_input);
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("listener", (View.OnClickListener) view -> {
            dismissDialogFragment(commonDialogFragment, fragmentManager);
            int i = view.getId();
            if (i == R.id.tv_left) {
                String input = commonDialogFragment.getViewDataBinding().editText.getText().toString();
                if (btnClickListener != null) {
                    btnClickListener.onConfirm(input);
                }

            } else if (i == R.id.tv_right) {
                if (btnClickListener != null) {
                    btnClickListener.onCancle();
                }
            }
        });
        variables.put("title", title);
        variables.put("hint", hint);
        if (!TextUtils.isEmpty(leftButtonText)) {
            variables.put("leftButtonText", leftButtonText);
        }
        commonDialogFragment.setVariables(variables);
        showDialogFragment(commonDialogFragment, fragmentManager, tag);
    }


    /***
     * 显示loadingDialog
     * @param msg
     */
    public static CommonDialogFragment showLoadingDialog(FragmentManager fragmentManager, String msg, String tag) {
        CommonDialogFragment<FragmentLoadingBinding> commonDialogFragment = new CommonDialogFragment();
        commonDialogFragment.setLayoutRes(R.layout.fragment_loading);
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("msg", msg);
        commonDialogFragment.setVariables(variables);
        commonDialogFragment.setCancelable(false);
        showDialogFragment(commonDialogFragment, fragmentManager, tag);
        return commonDialogFragment;
    }


    /***
     * 显示普通自定义界面dialog
     * @param fragmentManager
     * @param layoutRes
     * @param tag
     * @return
     */
    public static CommonDialogFragment showCommonDialog(FragmentManager fragmentManager, int layoutRes, String tag) {
        return showCommonDialog(fragmentManager, layoutRes, tag, new HashMap<>());
    }

    /***
     * 显示普通自定义界面dialog
     * @param fragmentManager
     * @param layoutRes
     * @param tag
     * @param variables
     * @return
     */
    public static CommonDialogFragment showCommonDialog(FragmentManager fragmentManager, int layoutRes, String tag, HashMap<String, Object> variables) {
        CommonDialogFragment commonDialogFragment = new CommonDialogFragment();
        commonDialogFragment.setLayoutRes(layoutRes);
        commonDialogFragment.setVariables(variables);
        showDialogFragment(commonDialogFragment, fragmentManager, tag);
        return commonDialogFragment;
    }

    public static boolean showDialogFragment(DialogFragment dialogFragment, FragmentManager fragmentManager, String tag) {
        if (dialogFragment == null || fragmentManager == null) {
            return false;
        }
        if (fragmentManager.isStateSaved()) {
            fragmentManager.beginTransaction().add(dialogFragment, tag).commitAllowingStateLoss();
        } else if (!fragmentManager.isDestroyed()) {
            dialogFragment.show(fragmentManager, tag);
        } else {
            return false;
        }
        return true;
    }

    public static void dismissDialogFragment(DialogFragment dialogFragment,  FragmentManager fragmentManager) {
        if (dialogFragment == null || fragmentManager == null) {
            return;
        }
        if (fragmentManager.isStateSaved()) {
            dialogFragment.dismissAllowingStateLoss();
        } else if (!fragmentManager.isDestroyed()) {
            dialogFragment.dismiss();
        }
    }
}
