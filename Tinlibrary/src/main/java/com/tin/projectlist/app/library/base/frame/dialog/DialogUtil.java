package com.tin.projectlist.app.library.base.frame.dialog;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public class DialogUtil {

    public static boolean showDialogFragment(DialogFragment dialogFragment, FragmentManager fragmentManager, String tag) {
        if (dialogFragment == null || fragmentManager == null) {
            return false;
        }
        if (fragmentManager.isDestroyed()) {
            return false;
        } else if (fragmentManager.isStateSaved()) {
            fragmentManager.beginTransaction().add(dialogFragment, tag).commitAllowingStateLoss();
        } else {
            dialogFragment.show(fragmentManager, tag);
        }
        return true;
    }


    public static void dismissDialogFragment(DialogFragment dialogFragment, FragmentManager fragmentManager) {
        if (dialogFragment == null || fragmentManager == null || fragmentManager.isDestroyed()) {
            return;
        }
        if (fragmentManager.isStateSaved()) {
            dialogFragment.dismissAllowingStateLoss();
        } else {
            dialogFragment.dismiss();
        }
    }

}
