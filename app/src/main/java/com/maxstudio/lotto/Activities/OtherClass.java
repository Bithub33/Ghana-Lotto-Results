package com.maxstudio.lotto.Activities;

import android.app.Activity;

public class OtherClass {
    private Activity mActivity;

    public OtherClass(Activity activity) {
        mActivity = activity;
    }

    public void closeActivity() {
        if (mActivity != null) {
            mActivity.finish();
        }
    }
}

