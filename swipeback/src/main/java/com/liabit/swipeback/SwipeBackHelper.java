package com.liabit.swipeback;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;

public class SwipeBackHelper {

    private final SwipeBackLayout swipeBackLayout;
    private final Activity activity;

    public SwipeBackHelper(Activity activity) {
        this.activity = activity;
        swipeBackLayout = new SwipeBackLayout(activity);
    }

    public void setContentView(int layoutResID) {
        activity.setContentView(swipeBackLayout);
        View view = LayoutInflater.from(activity).inflate(layoutResID, null);
        swipeBackLayout.addView(view);
    }

    public void setContentView(View view) {
        activity.setContentView(swipeBackLayout);
        swipeBackLayout.addView(view);
    }

    public void setSwipeBackFactor(@FloatRange(from = 0.0f, to = 1.0f) float swipeBackFactor) {
        swipeBackLayout.setSwipeBackFactor(swipeBackFactor);
    }

    public void setMaskAlpha(@IntRange(from = 0, to = 255) int maskAlpha) {
        swipeBackLayout.setMaskAlpha(maskAlpha);
    }

    public void enableSwipeBack(boolean enableSwipe) {
        swipeBackLayout.enableSwipeBack(enableSwipe);
    }

    /**
     * @param trackingEdge
     */
    public void setTrackingEdge(boolean trackingEdge) {
        swipeBackLayout.setSwipeFromEdge(trackingEdge);
    }

    /**
     * {@link SwipeBackLayout#FROM_LEFT}
     * {@link SwipeBackLayout#FROM_TOP}
     * {@link SwipeBackLayout#FROM_RIGHT}
     * {@link SwipeBackLayout#FROM_BOTTOM}
     *
     * @param direction
     */
    public void setTrackingDirection(int direction) {
        swipeBackLayout.setDirectionMode(direction);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return swipeBackLayout;
    }

    public void setOnSwipeBackListener(SwipeBackLayout.OnSwipeBackListener listener) {
        swipeBackLayout.setOnSwipeBackListener(listener);
    }

}
