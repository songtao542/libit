package com.liabit.swipeback;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

@SuppressWarnings("unused")
public class SwipeBackActivity extends AppCompatActivity {

    private SwipeBackHelper swipeBackHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        swipeBackHelper = new SwipeBackHelper(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        swipeBackHelper.setContentView(layoutResID);
    }

    /**
     * 滑动退出
     *
     * @param enableSwipe
     */
    public void enableSwipeBack(boolean enableSwipe) {
        swipeBackHelper.enableSwipeBack(enableSwipe);
    }

    public void setTrackingEdge(boolean trackingEdge) {
        swipeBackHelper.setTrackingEdge(trackingEdge);
    }

    public void setTrackingDirection(int direction) {
        swipeBackHelper.setTrackingDirection(direction);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return swipeBackHelper.getSwipeBackLayout();
    }

    public void setOnSwipeBackListener(SwipeBackLayout.OnSwipeBackListener listener) {
        swipeBackHelper.setOnSwipeBackListener(listener);
    }


}
