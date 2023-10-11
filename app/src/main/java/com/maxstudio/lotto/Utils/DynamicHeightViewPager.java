package com.maxstudio.lotto.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class DynamicHeightViewPager extends ViewPager {
    //private int maxHeight = 0;
    private  boolean isSwipeEnabled;

    public DynamicHeightViewPager(@NonNull Context context) {
        super(context);

        isSwipeEnabled = true;
    }

    public DynamicHeightViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        isSwipeEnabled = true;
    }

    public void updateHeight()
    {
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int maxHeight = 0;

        for (int i = 0; i < getChildCount(); i++)
        {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int fragmentHeight = child.getMeasuredHeight();
            if (fragmentHeight > maxHeight)
            {
                maxHeight = fragmentHeight;
            }
        }

        if (maxHeight != 0)
        {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isSwipeEnabled && super.onTouchEvent(ev);
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        isSwipeEnabled = swipeEnabled;
    }
}
