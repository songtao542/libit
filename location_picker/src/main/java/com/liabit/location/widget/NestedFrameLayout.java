package com.liabit.location.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


public class NestedFrameLayout extends FrameLayout {

    @SuppressWarnings("unused")
    private static final String TAG = "NestedParentLayout";

    private int mTopViewHeight;
    private View mNestedView;
    private View mTopView;
    private boolean mTopScaleable = false;
    private OnTopVisibleHeightChangeListener mTopHeightChangeListener;

    private int mDirection = 0;

    private int mMinHeight = 0;

    private Scroller mScroller;

    public NestedFrameLayout(Context context) {
        super(context);
        init(context);
    }

    public NestedFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NestedFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("unused")
    public NestedFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
    }

    public void setMinHeight(int minHeight) {
        this.mMinHeight = minHeight;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        //Log.d(TAG, "onStartNestedScroll target=" + target);
        if (mNestedView != null && mNestedView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) mNestedView;
            //内容太少，填不满NestedView
            if (viewGroup.getChildCount() > 0) {
                View lastChild = viewGroup.getChildAt(viewGroup.getChildCount() - 1);
                int[] l1 = new int[2];
                //获取最后一个子View在屏幕的位置
                lastChild.getLocationOnScreen(l1);
                int[] l2 = new int[2];
                //获取ViewGroup在屏幕的位置
                getLocationOnScreen(l2);
                //当未滑动之前，最后一个子View的底部坐标 比 ViewGroup 的底部坐标还要小，则不进行拦截
                if (getScrollY() == 0 && (l1[1] + lastChild.getHeight()) < (l2[1] + getHeight())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        //Log.d(TAG, "onNestedScrollAccepted");
    }

    @Override
    public void onStopNestedScroll(View target) {
        if (mScroller.isFinished() && getScrollY() > 0 && getScrollY() < mTopViewHeight - mMinHeight) {
            // mDirection>0 向下
            mScroller.forceFinished(true);
            int scrollY = getScrollY();
            if (mDirection > 0) {
                mScroller.startScroll(0, scrollY, 0, -scrollY);
            } else {
                mScroller.startScroll(0, scrollY, 0, mTopViewHeight - mMinHeight - scrollY);
            }
            invalidate();
            mDirection = 0;
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        //Log.d(TAG, "onNestedScroll");
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        //Log.d(TAG, "onNestedPreScroll dy-->" + dy);
        //往上滑动时 dy>0
        if (mDirection != 0) {
            mDirection = dy;
        }
        boolean hiddenTop = dy > 0 && getScrollY() < mTopViewHeight - mMinHeight;
        boolean showTop = dy < 0 && getScrollY() > 0 && !target.canScrollVertically(-1);

        int maxCanConsumed = mTopViewHeight - mMinHeight - getScrollY();
        int consumedY = Math.min(dy, maxCanConsumed);
        if (hiddenTop || showTop) {
            scrollBy(0, consumedY);
            mTopView.scrollBy(0, -consumedY / 2);
            consumed[1] = consumedY;
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        //往上滑动时，velocityY>0
        if (!consumed) {
            if (mScroller.isFinished()) {
                int scrollY = getScrollY();
                mScroller.forceFinished(true);
                if (velocityY > 0) {
                    mScroller.startScroll(0, scrollY, 0, mTopViewHeight - mMinHeight - scrollY);
                } else {
                    mScroller.startScroll(0, scrollY, 0, -scrollY);
                }
                invalidate();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        //不做拦截 可以传递给子View
        if (getScrollY() != mTopViewHeight - mMinHeight) {
            if (mScroller.isFinished()) {
                int scrollY = getScrollY();
                mScroller.forceFinished(true);
                if (velocityY > 0) {
                    mScroller.startScroll(0, scrollY, 0, mTopViewHeight - mMinHeight - scrollY);
                } else {
                    mScroller.startScroll(0, scrollY, 0, -scrollY);
                }
                invalidate();
            }
            return true;
        }
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        //Log.d(TAG, "getNestedScrollAxes");
        return 0;
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        final int action = ev.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN: {
//                if (!mScroller.isFinished()) {
//                    mScroller.forceFinished(true);
//                }
//            }
//        }
//        return super.onInterceptTouchEvent(ev);
//    }

    @Override
    public void computeScroll() {
        if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
            int y = mScroller.getCurrY();
            scrollTo(0, y);
            mTopView.scrollTo(0, -y / 2);
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() >= 2) {
            mTopView = getChildAt(0);
            mNestedView = getFirstNestedScrollableView();
            if (mNestedView != null) {
                View topView = getChildAt(0);
                topView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                mTopViewHeight = topView.getMeasuredHeight();
                ViewGroup.LayoutParams params = mNestedView.getLayoutParams();
                params.height = getMeasuredHeight() - mMinHeight;
            }
        }
    }

    private View getFirstNestedScrollableView() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view.isNestedScrollingEnabled()) {
                return view;
            }
        }
        return null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getChildCount() >= 1) {
            mTopView = getChildAt(0);
            mTopViewHeight = mTopView.getMeasuredHeight();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > mTopViewHeight - mMinHeight) {
            y = mTopViewHeight - mMinHeight;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
            if (mTopScaleable && mTopView != null) {
                float scale = 1 - y * 1f / mTopViewHeight;
                mTopView.setPivotY(mTopViewHeight);
                mTopView.setScaleX(scale);
                mTopView.setScaleY(scale);
                mTopView.setAlpha(scale);
            }
            if (mTopHeightChangeListener != null) {
                mTopHeightChangeListener.onTopVisibleHeightChange(mTopViewHeight - mMinHeight, y);
            }
        }
    }

    /**
     * @param scaleable is scale able
     */
    @SuppressWarnings("unused")
    public void setTopScaleable(boolean scaleable) {
        mTopScaleable = scaleable;
    }

    /**
     * @param listener listen
     */
    @SuppressWarnings("unused")
    public void setOnTopVisibleHeightChangeListener(OnTopVisibleHeightChangeListener listener) {
        mTopHeightChangeListener = listener;
    }

    public interface OnTopVisibleHeightChangeListener {
        void onTopVisibleHeightChange(int totalHeight, int visibleHeight);
    }

}
