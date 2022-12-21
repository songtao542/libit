package com.liabit.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.SeekBar;

@SuppressLint("AppCompatCustomView")
public class UiSeekBar extends SeekBar {
    private float mTouchDownX;
    private float mTouchDownY;
    private float mTouchDownProgress;
    private boolean mIsDragging;
    private int mScaledTouchSlop;

    private boolean mFromUser = false;
    private boolean mMirrorForRtl = false;

    private boolean mVertical = false;

    public UiSeekBar(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public UiSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public UiSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public UiSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            mMirrorForRtl = getBoolean(context, attrs, android.R.attr.mirrorForRtl);
            mVertical = getInt(context, attrs, android.R.attr.orientation, 0) == 1;
        }

        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser || mFromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        });
    }

    private boolean getBoolean(Context context, AttributeSet attrs, int attr) {
        int[] attrsArray = new int[]{attr};
        TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
        if (array == null) {
            return false;
        }
        boolean result = array.getBoolean(0, false);
        array.recycle();
        return result;
    }

    private int getInt(Context context, AttributeSet attrs, int attr, int defValue) {
        int[] attrsArray = new int[]{attr};
        TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
        if (array == null) {
            return defValue;
        }
        int result = array.getInt(0, defValue);
        array.recycle();
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //if (getThumb() != null) {
                //    final int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
                //    float mTouchThumbOffset = (getProgress() - getMin()) / (float) (getMax()
                //            - getMin()) - (event.getX() - getPaddingLeft()) / availableWidth;
                //    if (Math.abs(mTouchThumbOffset * availableWidth) > getThumbOffset()) {
                //        mTouchThumbOffset = 0;
                //    }
                //}
                mTouchDownX = event.getX();
                mTouchDownY = event.getY();
                mTouchDownProgress = getProgress();
                //if (isInScrollingContainer()) {
                //    mTouchDownX = event.getX();
                //} else {
                startDrag(event);
                //}
                break;

            case MotionEvent.ACTION_MOVE:
                if (mIsDragging) {
                    trackTouchEvent(event);
                } else {
                    final float x = event.getX();
                    if (Math.abs(x - mTouchDownX) > mScaledTouchSlop) {
                        startDrag(event);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mIsDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    setPressed(false);
                } else {
                    // Touch up when we never crossed the touch slop threshold should
                    // be interpreted as a tap-seek to that location.
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                }
                // ProgressBar doesn't know to repaint the thumb drawable
                // in its inactive state when the touch stops (because the
                // value has not apparently changed)
                invalidate();
                break;

            case MotionEvent.ACTION_CANCEL:
                if (mIsDragging) {
                    onStopTrackingTouch();
                    setPressed(false);
                }
                invalidate(); // see above explanation
                break;
        }
        return true;
    }

    /**
     * This is called when the user has started touching this widget.
     */
    private void onStartTrackingTouch() {
        mIsDragging = true;
    }

    /**
     * This is called when the user either releases their touch or the touch is
     * canceled.
     */
    private void onStopTrackingTouch() {
        mIsDragging = false;
    }

    private void startDrag(MotionEvent event) {
        setPressed(true);

        Drawable thumb = getThumb();
        if (thumb != null) {
            // This may be within the padding region.
            invalidate(thumb.getBounds());
        }

        onStartTrackingTouch();
        //trackTouchEvent(event);
        attemptClaimDrag();
    }

    /**
     * Tries to claim the user's drag motion, and requests disallowing any
     * ancestors from stealing events in the drag.
     */
    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private void trackTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        final int width = getWidth();
        final int height = getHeight();
        final int availableSize = mVertical ? (height - getPaddingTop() - getPaddingBottom()) : (width - getPaddingLeft() - getPaddingRight());
        float progress = mTouchDownProgress;
        float moved;
        if (mVertical) {
            moved = mTouchDownY - y;
        } else {
            if (isLayoutRtl() && mMirrorForRtl) {
                moved = mTouchDownX - x;
            } else {
                moved = x - mTouchDownX;
            }
        }
        final int range = getMax() - getMin();
        progress = progress + range * (moved / (float) availableSize);
        if (progress < getMin()) {
            progress = getMin();
        }
        if (progress > getMax()) {
            progress = getMax();
        }

        setHotspot(x, y);
        mFromUser = true;
        setProgress(Math.round(progress));
        mFromUser = false;
    }

    private void setHotspot(float x, float y) {
        final Drawable bg = getBackground();
        if (bg != null) {
            bg.setHotspot(x, y);
        }
    }

    public boolean isInScrollingContainer() {
        ViewParent p = getParent();
        while (p instanceof ViewGroup) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }

    public boolean isLayoutRtl() {
        return (getLayoutDirection() == LAYOUT_DIRECTION_RTL);
    }

    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }

}
