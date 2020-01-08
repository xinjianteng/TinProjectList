package com.tin.projectlist.app.library.reader.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;

/**
 * Created by Shikh
 * on 16-8-30.
 */
public class RadioImageView extends android.widget.ImageView implements Checkable {
    int[] CHECKED_STATE_SET = { android.R.attr.state_checked };
    private boolean mChecked;
    public RadioImageView(Context context) {
        super(context);
    }

    public RadioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
//        return super.onCreateDrawableState(extraSpace);
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
}
