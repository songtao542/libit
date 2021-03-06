package com.liabit.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * Created by Carbs.Wang.
 * email : yeah0126@yeah.net
 * github : https://github.com/Carbs0126/NumberPickerView
 */
@SuppressWarnings({"unused", "FieldCanBeLocal", "SuspiciousNameCombination"})
public class NumberPickerView extends View {
    // default text color of not selected item
    private static final int DEFAULT_TEXT_COLOR_NORMAL = 0xffcecece;
    private static final int DEFAULT_TEXT_COLOR_SELECTED = 0xff007cee;  //selected item

    // default text size of normal item
    private static final int DEFAULT_TEXT_SIZE_NORMAL_SP = 17;
    private static final int DEFAULT_TEXT_SIZE_SELECTED_SP = 22;

    // default text size of hint text, the middle item's right text
    private static final int DEFAULT_TEXT_SIZE_HINT_SP = DEFAULT_TEXT_SIZE_NORMAL_SP;

    // distance between selected text and hint text
    private static final int DEFAULT_MARGIN_START_OF_HINT_DP = 8;

    // distance between hint text and right of this view, used in wrap_content mode
    private static final int DEFAULT_MARGIN_END_OF_HINT_DP = 8;

    // default divider's color
    private static final int DEFAULT_DIVIDER_COLOR = 0xffe5e5e5;

    // default divider's height
    private static final int DEFAULT_DIVIDER_HEIGHT = 2;

    // default divider's margin to the left & right of this view
    private static final int DEFAULT_DIVIDER_MARGIN_HORIZONTAL = 0;

    // default shown items' count, now we display 3 items, the 2nd one is selected
    private static final int DEFAULT_SHOWN_COUNT = 3;

    // default items' horizontal padding, left padding and right padding are both 5dp,
    // only used in wrap_content mode
    private static final int DEFAULT_ITEM_PADDING_DP_H = 5;

    // default items' vertical padding, top padding and bottom padding are both 2dp,
    // only used in wrap_content mode
    private static final int DEFAULT_ITEM_PADDING_DP_V = 2;

    // message's what argument to refresh current state, used by mHandler
    private static final int HANDLER_WHAT_REFRESH = 1;

    // message's what argument to respond value changed event, used by mHandler
    private static final int HANDLER_WHAT_LISTENER_VALUE_CHANGED = 2;

    // message's what argument to request layout, used by mHandlerInMainThread
    private static final int HANDLER_WHAT_REQUEST_LAYOUT = 3;

    // interval time to scroll the distance of one item's height
    private static final int HANDLER_INTERVAL_REFRESH = 32;//millisecond

    // in millisecond unit, default duration of scrolling an item' distance
    private static final int DEFAULT_INTERVAL_REVISE_DURATION = 300;

    // max and min durations when scrolling from one value to another
    private static final int DEFAULT_MIN_SCROLL_BY_INDEX_DURATION = DEFAULT_INTERVAL_REVISE_DURATION;
    private static final int DEFAULT_MAX_SCROLL_BY_INDEX_DURATION = DEFAULT_INTERVAL_REVISE_DURATION * 2;

    private static final String TEXT_ELLIPSIZE_START = "start";
    private static final String TEXT_ELLIPSIZE_MIDDLE = "middle";
    private static final String TEXT_ELLIPSIZE_END = "end";

    private static final boolean DEFAULT_SHOW_DIVIDER = true;
    private static final boolean DEFAULT_WRAP_SELECTOR_WHEEL = true;
    private static final boolean DEFAULT_CURRENT_ITEM_INDEX_EFFECT = false;
    private static final boolean DEFAULT_RESPOND_CHANGE_ON_DETACH = false;
    private static final boolean DEFAULT_RESPOND_CHANGE_IN_MAIN_THREAD = true;

    private int mTextColorNormal = DEFAULT_TEXT_COLOR_NORMAL;
    private int mTextColorSelected = DEFAULT_TEXT_COLOR_SELECTED;
    private int mTextColorHint = DEFAULT_TEXT_COLOR_SELECTED;
    private int mTextSizeNormal = 0;
    private int mTextSizeSelected = 0;
    private int mTextSizeHint = 0;
    private int mWidthOfHintText = 0;
    private int mWidthOfAlterHint = 0;
    private int mMarginStartOfHint = 0;
    private int mMarginEndOfHint = 0;
    private int mItemPaddingVertical = 0;
    private int mItemPaddingHorizontal = 0;
    private int mDividerColor = DEFAULT_DIVIDER_COLOR;
    private int mDividerHeight = DEFAULT_DIVIDER_HEIGHT;
    private int mDividerMarginL = DEFAULT_DIVIDER_MARGIN_HORIZONTAL;
    private int mDividerMarginR = DEFAULT_DIVIDER_MARGIN_HORIZONTAL;
    private int mShownCount = DEFAULT_SHOWN_COUNT;
    private int mDividerIndex0 = 0;
    private int mDividerIndex1 = 0;
    private int mMinShowIndex = -1;
    private int mMaxShowIndex = -1;
    //compat for android.widget.NumberPicker
    private int mMinValue = 0;
    //compat for android.widget.NumberPicker
    private int mMaxValue = 0;
    private int mMaxWidthOfDisplayedValues = 0;
    private int mMaxHeightOfDisplayedValues = 0;
    private int mNormalHeightOfDisplayedValues = 0;
    private int mMaxWidthOfAlterArrayWithMeasureHint = 0;
    private int mMaxWidthOfAlterArrayWithoutMeasureHint = 0;
    private int mPrevPickedIndex = 0;
    private int mMiniVelocityFling = 150;
    private int mScaledTouchSlop = 8;
    private String mHintText;
    private String mTextEllipsize;
    private String mEmptyItemHint;
    private String mAlterHint;
    //friction used by scroller when fling
    private float mFriction = 1f;
    private float mTextSizeNormalCenterYOffset = 0f;
    private float mTextSizeSelectedCenterYOffset = 0f;
    private float mTextSizeHintCenterYOffset = 0f;
    //true to show the two dividers
    private boolean mShowDivider = DEFAULT_SHOW_DIVIDER;
    //true to wrap the displayed values
    private boolean mWrapSelectorWheel = DEFAULT_WRAP_SELECTOR_WHEEL;
    //true to set to the current position, false set position to 0
    private boolean mCurrentItemIndexEffect = DEFAULT_CURRENT_ITEM_INDEX_EFFECT;
    //true if NumberPickerView has initialized
    private boolean mHasInit = false;
    // if displayed values' number is less than show count, then this value will be false.
    private boolean mWrapSelectorWheelCheck = true;
    // if you want you set to linear mode from wrap mode when scrolling, then this value will be true.
    private boolean mPendingWrapToLinear = false;

    // if this view is used in same dialog or PopupWindow more than once, and there are several
    // NumberPickerViews linked, such as Gregorian Calendar with MonthPicker and DayPicker linked,
    // set mRespondChangeWhenDetach true to respond onValueChanged callbacks if this view is scrolling
    // when detach from window, but this solution is unlovely and may cause NullPointerException
    // (even i haven't found this NullPointerException),
    // so I highly recommend that every time setting up a reusable dialog with a NumberPickerView in it,
    // please initialize NumberPickerView's data, and in this way, you can set mRespondChangeWhenDetach false.
    private boolean mRespondChangeOnDetach = DEFAULT_RESPOND_CHANGE_ON_DETACH;

    // this is to set which thread to respond onChange... listeners including
    // OnValueChangeListener, OnValueChangeListenerRelativeToRaw and OnScrollListener when view is
    // scrolling or starts to scroll or stops scrolling.
    private boolean mRespondChangeInMainThread = DEFAULT_RESPOND_CHANGE_IN_MAIN_THREAD;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private final Paint mPaintDivider = new Paint();
    private final TextPaint mTextPaint = new TextPaint();
    private final Paint mPaintHint = new Paint();

    private List<? extends CharSequence> mDisplayedValues;
    private List<? extends CharSequence> mAlterTextArrayWithMeasureHint;
    private List<? extends CharSequence> mAlterTextArrayWithoutMeasureHint;

    private boolean mAutoTextSize = false;
    // 记录 mDisplayedValues 中文字的字体大小
    private final ArrayMap<CharSequence, Integer> mDisplayedValueTextSizeMap = new ArrayMap<>();

    private HandlerThread mHandlerThread;
    private Handler mHandlerInNewThread;
    private Handler mHandlerInMainThread;

    private final Map<String, Integer> mTextWidthCache = new ConcurrentHashMap<>();

    // compatible for NumberPicker, value always offset by MinValue
    public interface OnValueChangeListener {
        void onValueChange(NumberPickerView picker, int oldVal, int newVal);
    }

    public interface OnValueChangeListenerRelativeToRaw {
        void onValueChangeRelativeToRaw(NumberPickerView picker, int oldPickedIndex, int newPickedIndex,
                                        List<? extends CharSequence> displayedValues);
    }

    public interface OnValueChangeListenerInScrolling {
        void onValueChangeInScrolling(NumberPickerView picker, int oldVal, int newVal);
    }

    // compatible for NumberPicker
    public interface OnScrollListener {
        int SCROLL_STATE_IDLE = 0;
        int SCROLL_STATE_TOUCH_SCROLL = 1;
        int SCROLL_STATE_FLING = 2;

        void onScrollStateChange(NumberPickerView view, int scrollState);
    }

    private OnValueChangeListenerRelativeToRaw mOnValueChangeListenerRaw;
    private OnValueChangeListener mOnValueChangeListener; //compatible for NumberPicker
    private OnScrollListener mOnScrollListener;//compatible for NumberPicker
    private OnValueChangeListenerInScrolling mOnValueChangeListenerInScrolling;//response onValueChanged in scrolling

    private AutoTextSizeHelper mAutoTextSizeHelper;

    // The current scroll state of the NumberPickerView.
    private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;

    public NumberPickerView(Context context) {
        this(context, null);
    }

    public NumberPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs, defStyleAttr, R.style.NumberPickerViewDefaultStyle);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NumberPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void initAttr(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerView, defStyleAttr, defStyleRes);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.NumberPickerView_showCount) {
                mShownCount = a.getInt(attr, DEFAULT_SHOWN_COUNT);
            } else if (attr == R.styleable.NumberPickerView_dividerColor) {
                mDividerColor = a.getColor(attr, DEFAULT_DIVIDER_COLOR);
            } else if (attr == R.styleable.NumberPickerView_dividerHeight) {
                mDividerHeight = a.getDimensionPixelSize(attr, DEFAULT_DIVIDER_HEIGHT);
            } else if (attr == R.styleable.NumberPickerView_dividerDrawable) {
                mDividerDrawable = a.getResourceId(attr, 0);
            } else if (attr == R.styleable.NumberPickerView_dividerMarginLeft) {
                mDividerMarginL = a.getDimensionPixelSize(attr, DEFAULT_DIVIDER_MARGIN_HORIZONTAL);
            } else if (attr == R.styleable.NumberPickerView_dividerMarginRight) {
                mDividerMarginR = a.getDimensionPixelSize(attr, DEFAULT_DIVIDER_MARGIN_HORIZONTAL);
            } else if (attr == R.styleable.NumberPickerView_textArray) {
                mDisplayedValues = convertCharSequenceArrayToList(a.getTextArray(attr));
            } else if (attr == R.styleable.NumberPickerView_textColorNormal) {
                mTextColorNormal = a.getColor(attr, DEFAULT_TEXT_COLOR_NORMAL);
            } else if (attr == R.styleable.NumberPickerView_textColorSelected) {
                mTextColorSelected = a.getColor(attr, DEFAULT_TEXT_COLOR_SELECTED);
            } else if (attr == R.styleable.NumberPickerView_textColorHint) {
                mTextColorHint = a.getColor(attr, DEFAULT_TEXT_COLOR_SELECTED);
            } else if (attr == R.styleable.NumberPickerView_textSizeNormal) {
                mTextSizeNormal = a.getDimensionPixelSize(attr, sp2px(DEFAULT_TEXT_SIZE_NORMAL_SP));
            } else if (attr == R.styleable.NumberPickerView_textSizeSelected) {
                mTextSizeSelected = a.getDimensionPixelSize(attr, sp2px(DEFAULT_TEXT_SIZE_SELECTED_SP));
            } else if (attr == R.styleable.NumberPickerView_textSizeHint) {
                mTextSizeHint = a.getDimensionPixelSize(attr, sp2px(DEFAULT_TEXT_SIZE_HINT_SP));
            } else if (attr == R.styleable.NumberPickerView_minValue) {
                mMinShowIndex = a.getInteger(attr, 0);
            } else if (attr == R.styleable.NumberPickerView_maxValue) {
                mMaxShowIndex = a.getInteger(attr, 0);
            } else if (attr == R.styleable.NumberPickerView_wrapSelectorWheel) {
                mWrapSelectorWheel = a.getBoolean(attr, DEFAULT_WRAP_SELECTOR_WHEEL);
            } else if (attr == R.styleable.NumberPickerView_showItemDivider) {
                mShowDivider = a.getBoolean(attr, DEFAULT_SHOW_DIVIDER);
            } else if (attr == R.styleable.NumberPickerView_hintText) {
                mHintText = a.getString(attr);
            } else if (attr == R.styleable.NumberPickerView_alternativeHint) {
                mAlterHint = a.getString(attr);
            } else if (attr == R.styleable.NumberPickerView_emptyItemHint) {
                mEmptyItemHint = a.getString(attr);
            } else if (attr == R.styleable.NumberPickerView_marginStartOfHint) {
                mMarginStartOfHint = a.getDimensionPixelSize(attr, dp2px(DEFAULT_MARGIN_START_OF_HINT_DP));
            } else if (attr == R.styleable.NumberPickerView_marginEndOfHint) {
                mMarginEndOfHint = a.getDimensionPixelSize(attr, dp2px(DEFAULT_MARGIN_END_OF_HINT_DP));
            } else if (attr == R.styleable.NumberPickerView_itemPaddingVertical) {
                mItemPaddingVertical = a.getDimensionPixelSize(attr, dp2px(DEFAULT_ITEM_PADDING_DP_V));
            } else if (attr == R.styleable.NumberPickerView_itemPaddingHorizontal) {
                mItemPaddingHorizontal = a.getDimensionPixelSize(attr, dp2px(DEFAULT_ITEM_PADDING_DP_H));
            } else if (attr == R.styleable.NumberPickerView_alternativeTextArrayWithMeasureHint) {
                mAlterTextArrayWithMeasureHint = convertCharSequenceArrayToList(a.getTextArray(attr));
            } else if (attr == R.styleable.NumberPickerView_alternativeTextArrayWithoutMeasureHint) {
                mAlterTextArrayWithoutMeasureHint = convertCharSequenceArrayToList(a.getTextArray(attr));
            } else if (attr == R.styleable.NumberPickerView_respondChangeOnDetached) {
                mRespondChangeOnDetach = a.getBoolean(attr, DEFAULT_RESPOND_CHANGE_ON_DETACH);
            } else if (attr == R.styleable.NumberPickerView_respondChangeInMainThread) {
                mRespondChangeInMainThread = a.getBoolean(attr, DEFAULT_RESPOND_CHANGE_IN_MAIN_THREAD);
            } else if (attr == R.styleable.NumberPickerView_textEllipsize) {
                mTextEllipsize = a.getString(attr);
            } else if (attr == R.styleable.NumberPickerView_autoTextSize) {
                mAutoTextSize = a.getBoolean(attr, false);
            }
        }
        a.recycle();
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
        mAutoTextSizeHelper = new AutoTextSizeHelper();
        mMiniVelocityFling = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        if (mTextSizeNormal == 0) {
            mTextSizeNormal = sp2px(DEFAULT_TEXT_SIZE_NORMAL_SP);
        }
        if (mTextSizeSelected == 0) {
            mTextSizeSelected = sp2px(DEFAULT_TEXT_SIZE_SELECTED_SP);
        }
        if (mTextSizeHint == 0) {
            mTextSizeHint = sp2px(DEFAULT_TEXT_SIZE_HINT_SP);
        }
        if (mMarginStartOfHint == 0) {
            mMarginStartOfHint = dp2px(DEFAULT_MARGIN_START_OF_HINT_DP);
        }
        if (mMarginEndOfHint == 0) {
            mMarginEndOfHint = dp2px(DEFAULT_MARGIN_END_OF_HINT_DP);
        }
        mPaintDivider.setColor(mDividerColor);
        mPaintDivider.setAntiAlias(true);
        mPaintDivider.setStyle(Paint.Style.STROKE);
        mPaintDivider.setStrokeWidth(mDividerHeight);

        mTextPaint.setColor(mTextColorNormal);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Align.CENTER);

        mPaintHint.setColor(mTextColorHint);
        mPaintHint.setAntiAlias(true);
        mPaintHint.setTextAlign(Align.CENTER);
        mPaintHint.setTextSize(mTextSizeHint);

        if (mShownCount % 2 == 0) {
            mShownCount++;
        }
        if (mMinShowIndex == -1 || mMaxShowIndex == -1) {
            updateValueForInit();
        }
        initHandler(context);
    }

    private void initHandler(Context context) {
        mHandlerThread = new HandlerThread("HandlerThread-For-Refreshing");
        mHandlerThread.start();

        mHandlerInNewThread = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case HANDLER_WHAT_REFRESH:
                        if (!mScroller.isFinished()) {
                            if (mScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                                onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                            }
                            mHandlerInNewThread.sendMessageDelayed(getMsg(HANDLER_WHAT_REFRESH, 0, 0, msg.obj), HANDLER_INTERVAL_REFRESH);
                        } else {
                            int duration = 0;
                            int willPickIndex;
                            //if scroller finished(not scrolling), then adjust the position
                            if (mCurrDrawFirstItemY != 0) {//need to adjust
                                if (mScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                                    onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                                }
                                if (mCurrDrawFirstItemY < (-mItemHeight / 2)) {
                                    //adjust to scroll upward
                                    duration = (int) ((float) DEFAULT_INTERVAL_REVISE_DURATION * (mItemHeight + mCurrDrawFirstItemY) / mItemHeight);
                                    mScroller.startScroll(0, mCurrDrawGlobalY, 0, mItemHeight + mCurrDrawFirstItemY, duration * 3);
                                    willPickIndex = getWillPickIndexByGlobalY(mCurrDrawGlobalY + mItemHeight + mCurrDrawFirstItemY);
                                } else {
                                    //adjust to scroll downward
                                    duration = (int) ((float) DEFAULT_INTERVAL_REVISE_DURATION * (-mCurrDrawFirstItemY) / mItemHeight);
                                    mScroller.startScroll(0, mCurrDrawGlobalY, 0, mCurrDrawFirstItemY, duration * 3);
                                    willPickIndex = getWillPickIndexByGlobalY(mCurrDrawGlobalY + mCurrDrawFirstItemY);
                                }
                                postInvalidate();
                            } else {
                                onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
                                //get the index which will be selected
                                willPickIndex = getWillPickIndexByGlobalY(mCurrDrawGlobalY);
                            }
                            Message changeMsg = getMsg(HANDLER_WHAT_LISTENER_VALUE_CHANGED, mPrevPickedIndex, willPickIndex, msg.obj);
                            if (mRespondChangeInMainThread) {
                                mHandlerInMainThread.sendMessageDelayed(changeMsg, duration * 2);
                            } else {
                                mHandlerInNewThread.sendMessageDelayed(changeMsg, duration * 2);
                            }
                        }
                        break;
                    case HANDLER_WHAT_LISTENER_VALUE_CHANGED:
                        respondPickedValueChanged(msg.arg1, msg.arg2, msg.obj);
                        break;
                }
            }
        };
        mHandlerInMainThread = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HANDLER_WHAT_REQUEST_LAYOUT:
                        requestLayout();
                        break;
                    case HANDLER_WHAT_LISTENER_VALUE_CHANGED:
                        respondPickedValueChanged(msg.arg1, msg.arg2, msg.obj);
                        break;
                }
            }
        };
    }

    private int mInScrollingPickedOldValue;
    private int mInScrollingPickedNewValue;

    private void respondPickedValueChangedInScrolling(int oldVal, int newVal) {
        mOnValueChangeListenerInScrolling.onValueChangeInScrolling(this, oldVal, newVal);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updateMaxWHOfDisplayedValues(false);
        setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
    }

    @SuppressWarnings({"SingleStatementInBlock", "UnclearExpression"})
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mItemHeight = mViewHeight / mShownCount;
        mViewCenterX = ((float) (mViewWidth + getPaddingLeft() - getPaddingRight())) / 2;
        int defaultValue = 0;
        if (getOneRecycleSize() > 1) {
            if (mHasInit) {
                defaultValue = getValue() - mMinValue;
            } else if (mCurrentItemIndexEffect) {
                defaultValue = mCurrDrawFirstItemIndex + (mShownCount - 1) / 2;
            }
        }
        correctPositionByDefaultValue(defaultValue, mWrapSelectorWheel && mWrapSelectorWheelCheck);
        updateFontAttr();
        updateNotWrapYLimit();
        updateDividerAttr();
        mHasInit = true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mHandlerThread == null || !mHandlerThread.isAlive()) {
            initHandler(getContext());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandlerThread.quit();
        //These codes are for dialog or PopupWindow which will be used for more than once.
        //Not an elegant solution, if you have any good idea, please let me know, thank you.
        if (mItemHeight == 0) {
            return;
        }
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            mCurrDrawGlobalY = mScroller.getCurrY();
            calculateFirstItemParameterByGlobalY();
            if (mCurrDrawFirstItemY != 0) {
                if (mCurrDrawFirstItemY < (-mItemHeight / 2)) {
                    mCurrDrawGlobalY = mCurrDrawGlobalY + mItemHeight + mCurrDrawFirstItemY;
                } else {
                    mCurrDrawGlobalY = mCurrDrawGlobalY + mCurrDrawFirstItemY;
                }
                calculateFirstItemParameterByGlobalY();
            }
            onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
        }
        // see the comments on mRespondChangeOnDetach, if mRespondChangeOnDetach is false,
        // please initialize NumberPickerView's data every time setting up NumberPickerView,
        int currPickedIndex = getWillPickIndexByGlobalY(mCurrDrawGlobalY);
        if (currPickedIndex != mPrevPickedIndex && mRespondChangeOnDetach) {
            try {
                if (mOnValueChangeListener != null) {
                    mOnValueChangeListener.onValueChange(NumberPickerView.this, mPrevPickedIndex + mMinValue, currPickedIndex + mMinValue);
                }
                if (mOnValueChangeListenerRaw != null) {
                    mOnValueChangeListenerRaw.onValueChangeRelativeToRaw(NumberPickerView.this, mPrevPickedIndex, currPickedIndex, mDisplayedValues);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mPrevPickedIndex = currPickedIndex;
    }

    public int getOneRecycleSize() {
        return mMaxShowIndex - mMinShowIndex + 1;
    }

    public int getRawContentSize() {
        if (mDisplayedValues != null) {
            return mDisplayedValues.size();
        }
        return 0;
    }

    public void setDisplayedValuesAndPickedIndex(List<? extends CharSequence> newDisplayedValues, int pickedIndex, boolean needRefresh) {
        stopScrolling();
        if (newDisplayedValues == null) {
            throw new IllegalArgumentException("newDisplayedValues should not be null.");
        }
        if (pickedIndex < 0) {
            throw new IllegalArgumentException("pickedIndex should not be negative, now pickedIndex is " + pickedIndex);
        }
        updateContent(newDisplayedValues);
        updateMaxWHOfDisplayedValues(true);
        updateNotWrapYLimit();
        updateValue();
        mPrevPickedIndex = pickedIndex + mMinShowIndex;
        correctPositionByDefaultValue(pickedIndex, mWrapSelectorWheel && mWrapSelectorWheelCheck);
        if (needRefresh) {
            mHandlerInNewThread.sendMessageDelayed(getMsg(HANDLER_WHAT_REFRESH), 0);
            postInvalidate();
        }
    }

    public void setDisplayedValues(List<? extends CharSequence> newDisplayedValues, boolean needRefresh) {
        setDisplayedValuesAndPickedIndex(newDisplayedValues, 0, needRefresh);
    }

    public void setDisplayedValues(List<CharSequence> newDisplayedValues) {
        stopRefreshing();
        stopScrolling();
        if (newDisplayedValues == null) {
            throw new IllegalArgumentException("newDisplayedValues should not be null.");
        }

        if (mMaxValue - mMinValue + 1 > newDisplayedValues.size()) {
            throw new IllegalArgumentException("mMaxValue - mMinValue + 1 should not be greater than mDisplayedValues.length, now "
                    + "((mMaxValue - mMinValue + 1) is " + (mMaxValue - mMinValue + 1)
                    + " newDisplayedValues.length is " + newDisplayedValues.size()
                    + ", you need to set MaxValue and MinValue before setDisplayedValues(String[])");
        }
        updateContent(newDisplayedValues);
        updateMaxWHOfDisplayedValues(true);
        mPrevPickedIndex = mMinShowIndex;
        correctPositionByDefaultValue(0, mWrapSelectorWheel && mWrapSelectorWheelCheck);
        postInvalidate();
        mHandlerInMainThread.sendEmptyMessage(HANDLER_WHAT_REQUEST_LAYOUT);
    }

    /**
     * Gets the values to be displayed instead of string values.
     *
     * @return The displayed values.
     */
    public List<? extends CharSequence> getDisplayedValues() {
        return mDisplayedValues;
    }

    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        if (mWrapSelectorWheel != wrapSelectorWheel) {
            if (!wrapSelectorWheel) {
                if (mScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    internalSetWrapToLinear();
                } else {
                    mPendingWrapToLinear = true;
                }
            } else {
                mWrapSelectorWheel = true;
                updateWrapStateByContent();
                postInvalidate();
            }
        }
    }

    /**
     * get the "fromValue" by using getValue(), if your picker's minValue is not 0,
     * make sure you can get the accurate value by getValue(), or you can use
     * smoothScrollToValue(int fromValue, int toValue, boolean needRespond)
     *
     * @param toValue the value you want picker to scroll to
     */
    public void smoothScrollToValue(int toValue) {
        smoothScrollToValue(getValue(), toValue, true);
    }

    /**
     * get the "fromValue" by using getValue(), if your picker's minValue is not 0,
     * make sure you can get the accurate value by getValue(), or you can use
     * smoothScrollToValue(int fromValue, int toValue, boolean needRespond)
     *
     * @param toValue     the value you want picker to scroll to
     * @param needRespond set if you want picker to respond onValueChange listener
     */
    public void smoothScrollToValue(int toValue, boolean needRespond) {
        smoothScrollToValue(getValue(), toValue, needRespond);
    }

    public void smoothScrollToValue(int fromValue, int toValue) {
        smoothScrollToValue(fromValue, toValue, true);
    }

    /**
     * @param fromValue   need to set the fromValue, can be greater than mMaxValue or less than mMinValue
     * @param toValue     the value you want picker to scroll to
     * @param needRespond need Respond to the ValueChange callback When Scrolling, default is false
     */
    public void smoothScrollToValue(int fromValue, int toValue, boolean needRespond) {
        int deltaIndex;
        fromValue = refineValueByLimit(fromValue, mMinValue, mMaxValue, mWrapSelectorWheel && mWrapSelectorWheelCheck);
        toValue = refineValueByLimit(toValue, mMinValue, mMaxValue, mWrapSelectorWheel && mWrapSelectorWheelCheck);
        if (mWrapSelectorWheel && mWrapSelectorWheelCheck) {
            deltaIndex = toValue - fromValue;
            int halfOneRecycleSize = getOneRecycleSize() / 2;
            if (deltaIndex < -halfOneRecycleSize || halfOneRecycleSize < deltaIndex) {
                deltaIndex = deltaIndex > 0 ? deltaIndex - getOneRecycleSize() : deltaIndex + getOneRecycleSize();
            }
        } else {
            deltaIndex = toValue - fromValue;
        }
        setValue(fromValue);
        if (fromValue == toValue) {
            return;
        }
        scrollByIndexSmoothly(deltaIndex, needRespond);
    }

    /**
     * simplify the "setDisplayedValue() + setMinValue() + setMaxValue()" process,
     * default minValue is 0, and make sure you do NOT change the minValue.
     *
     * @param display new values to be displayed
     */
    public void refreshByNewDisplayedValues(List<CharSequence> display) {
        int minValue = getMinValue();

        int oldMaxValue = getMaxValue();
        int oldSpan = oldMaxValue - minValue + 1;

        int newMaxValue = display.size() - 1;
        int newSpan = newMaxValue - minValue + 1;

        if (newSpan > oldSpan) {
            setDisplayedValues(display);
            setMaxValue(newMaxValue);
        } else {
            setMaxValue(newMaxValue);
            setDisplayedValues(display);
        }
    }

    /**
     * used by handlers to respond onchange callbacks
     *
     * @param oldVal        prevPicked value
     * @param newVal        currPicked value
     * @param respondChange if want to respond onchange callbacks
     */
    @SuppressWarnings("ConditionCoveredByFurtherCondition")
    private void respondPickedValueChanged(int oldVal, int newVal, Object respondChange) {
        onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
        if (oldVal != newVal) {
            if (respondChange == null || !(respondChange instanceof Boolean) || (Boolean) respondChange) {
                if (mOnValueChangeListener != null) {
                    mOnValueChangeListener.onValueChange(NumberPickerView.this, oldVal + mMinValue, newVal + mMinValue);
                }
                if (mOnValueChangeListenerRaw != null) {
                    mOnValueChangeListenerRaw.onValueChangeRelativeToRaw(NumberPickerView.this, oldVal, newVal, mDisplayedValues);
                }
            }
        }
        mPrevPickedIndex = newVal;
        if (mPendingWrapToLinear) {
            mPendingWrapToLinear = false;
            internalSetWrapToLinear();
        }
    }

    private void scrollByIndexSmoothly(int deltaIndex) {
        scrollByIndexSmoothly(deltaIndex, true);
    }

    /**
     * @param deltaIndex  the delta index it will scroll by
     * @param needRespond need Respond to the ValueChange callback When Scrolling, default is false
     */
    private void scrollByIndexSmoothly(int deltaIndex, boolean needRespond) {
        if (!(mWrapSelectorWheel && mWrapSelectorWheelCheck)) {
            int willPickRawIndex = getPickedIndexRelativeToRaw();
            if (willPickRawIndex + deltaIndex > mMaxShowIndex) {
                deltaIndex = mMaxShowIndex - willPickRawIndex;
            } else if (willPickRawIndex + deltaIndex < mMinShowIndex) {
                deltaIndex = mMinShowIndex - willPickRawIndex;
            }
        }
        int duration;
        int dy;
        if (mCurrDrawFirstItemY < (-mItemHeight / 2)) {
            //scroll upwards for a distance of less than mItemHeight
            dy = mItemHeight + mCurrDrawFirstItemY;
            duration = (int) ((float) DEFAULT_INTERVAL_REVISE_DURATION * (mItemHeight + mCurrDrawFirstItemY) / mItemHeight);
            if (deltaIndex < 0) {
                duration = -duration - deltaIndex * DEFAULT_INTERVAL_REVISE_DURATION;
            } else {
                duration = duration + deltaIndex * DEFAULT_INTERVAL_REVISE_DURATION;
            }
        } else {
            //scroll downwards for a distance of less than mItemHeight
            dy = mCurrDrawFirstItemY;
            duration = (int) ((float) DEFAULT_INTERVAL_REVISE_DURATION * (-mCurrDrawFirstItemY) / mItemHeight);
            if (deltaIndex < 0) {
                duration = duration - deltaIndex * DEFAULT_INTERVAL_REVISE_DURATION;
            } else {
                duration = duration + deltaIndex * DEFAULT_INTERVAL_REVISE_DURATION;
            }
        }
        dy = dy + deltaIndex * mItemHeight;
        if (duration < DEFAULT_MIN_SCROLL_BY_INDEX_DURATION) {
            duration = DEFAULT_MIN_SCROLL_BY_INDEX_DURATION;
        }
        if (duration > DEFAULT_MAX_SCROLL_BY_INDEX_DURATION) {
            duration = DEFAULT_MAX_SCROLL_BY_INDEX_DURATION;
        }
        mScroller.startScroll(0, mCurrDrawGlobalY, 0, dy, duration);
        if (needRespond) {
            mHandlerInNewThread.sendMessageDelayed(getMsg(HANDLER_WHAT_REFRESH), duration / 4);
        } else {
            mHandlerInNewThread.sendMessageDelayed(getMsg(HANDLER_WHAT_REFRESH, 0, 0, false), duration / 4);
        }
        postInvalidate();
    }

    public int getMinValue() {
        return mMinValue;
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMinValue(int minValue) {
        mMinValue = minValue;
        mMinShowIndex = 0;
        updateNotWrapYLimit();
    }

    //compatible for android.widget.NumberPicker
    public void setMaxValue(int maxValue) {
        if (mDisplayedValues == null) {
            throw new NullPointerException("mDisplayedValues should not be null");
        }
        if (maxValue - mMinValue + 1 > mDisplayedValues.size()) {
            throw new IllegalArgumentException("(maxValue - mMinValue + 1) should not be greater than mDisplayedValues.length now " +
                    " (maxValue - mMinValue + 1) is " + (maxValue - mMinValue + 1) + " and mDisplayedValues.length is " + mDisplayedValues.size());
        }
        mMaxValue = maxValue;
        mMaxShowIndex = mMaxValue - mMinValue + mMinShowIndex;
        setMinAndMaxShowIndex(mMinShowIndex, mMaxShowIndex);
        updateNotWrapYLimit();
    }

    //compatible for android.widget.NumberPicker
    public void setValue(int value) {
        if (value < mMinValue) {
            value = mMinValue;
            //throw new IllegalArgumentException("should not set a value less than mMinValue, value is " + value);
        }
        if (value > mMaxValue) {
            value = mMaxValue;
            //throw new IllegalArgumentException("should not set a value greater than mMaxValue, value is " + value +"   mMaxValue="+mMaxValue);
        }
        if (value - mMinValue == mPrevPickedIndex) {
            return;
        }
        setPickedIndexRelativeToRaw(value - mMinValue);
    }

    //compatible for android.widget.NumberPicker
    public int getValue() {
        return getPickedIndexRelativeToRaw() + mMinValue;
    }

    public CharSequence getDisplayValue() {
        return mDisplayedValues.get(getValue() - mMinValue);
    }

    public int getIndex() {
        return getValue() - mMinValue;
    }

    public boolean getWrapSelectorWheel() {
        return mWrapSelectorWheel;
    }

    public boolean getWrapSelectorWheelAbsolutely() {
        return mWrapSelectorWheel && mWrapSelectorWheelCheck;
    }

    public void setHintText(String hintText) {
        if (isStringEqual(mHintText, hintText)) {
            return;
        }
        mHintText = hintText;
        mTextSizeHintCenterYOffset = getTextCenterYOffset(mPaintHint.getFontMetrics());
        mWidthOfHintText = getTextWidth(mHintText, mPaintHint);
        mHandlerInMainThread.sendEmptyMessage(HANDLER_WHAT_REQUEST_LAYOUT);
    }

    public void setPickedIndexRelativeToMin(int pickedIndexToMin) {
        if (0 <= pickedIndexToMin && pickedIndexToMin < getOneRecycleSize()) {
            mPrevPickedIndex = pickedIndexToMin + mMinShowIndex;
            correctPositionByDefaultValue(pickedIndexToMin, mWrapSelectorWheel && mWrapSelectorWheelCheck);
            postInvalidate();
        }
    }

    public void setNormalTextColor(int normalTextColor) {
        if (mTextColorNormal == normalTextColor) {
            return;
        }
        mTextColorNormal = normalTextColor;
        postInvalidate();
    }

    public void setSelectedTextColor(int selectedTextColor) {
        if (mTextColorSelected == selectedTextColor) {
            return;
        }
        mTextColorSelected = selectedTextColor;
        postInvalidate();
    }

    public void setHintTextColor(int hintTextColor) {
        if (mTextColorHint == hintTextColor) {
            return;
        }
        mTextColorHint = hintTextColor;
        mPaintHint.setColor(mTextColorHint);
        postInvalidate();
    }

    public void setDividerColor(int dividerColor) {
        if (mDividerColor == dividerColor) {
            return;
        }
        mDividerColor = dividerColor;
        mPaintDivider.setColor(mDividerColor);
        postInvalidate();
    }

    public void setDividerDrawable(int resId) {
        mDividerDrawable = resId;
        postInvalidate();
    }

    public void setPickedIndexRelativeToRaw(int pickedIndexToRaw) {
        if (mMinShowIndex > -1) {
            if (mMinShowIndex <= pickedIndexToRaw && pickedIndexToRaw <= mMaxShowIndex) {
                mPrevPickedIndex = pickedIndexToRaw;
                correctPositionByDefaultValue(pickedIndexToRaw - mMinShowIndex, mWrapSelectorWheel && mWrapSelectorWheelCheck);
                postInvalidate();
            }
        }
    }

    public int getPickedIndexRelativeToRaw() {
        int willPickIndex;
        if (mCurrDrawFirstItemY != 0) {
            if (mCurrDrawFirstItemY < (-mItemHeight / 2)) {
                willPickIndex = getWillPickIndexByGlobalY(mCurrDrawGlobalY + mItemHeight + mCurrDrawFirstItemY);
            } else {
                willPickIndex = getWillPickIndexByGlobalY(mCurrDrawGlobalY + mCurrDrawFirstItemY);
            }
        } else {
            willPickIndex = getWillPickIndexByGlobalY(mCurrDrawGlobalY);
        }
        return willPickIndex;
    }

    public void setMinAndMaxShowIndex(int minShowIndex, int maxShowIndex) {
        setMinAndMaxShowIndex(minShowIndex, maxShowIndex, true);
    }

    @SuppressWarnings("ConstantConditions")
    public void setMinAndMaxShowIndex(int minShowIndex, int maxShowIndex, boolean needRefresh) {
        if (minShowIndex > maxShowIndex) {
            throw new IllegalArgumentException("minShowIndex should be less than maxShowIndex, minShowIndex is "
                    + minShowIndex + ", maxShowIndex is " + maxShowIndex + ".");
        }
        if (mDisplayedValues == null) {
            throw new IllegalArgumentException("mDisplayedValues should not be null, you need to set mDisplayedValues first.");
        } else {
            if (minShowIndex < 0) {
                throw new IllegalArgumentException("minShowIndex should not be less than 0, now minShowIndex is " + minShowIndex);
            } else if (minShowIndex > mDisplayedValues.size() - 1) {
                throw new IllegalArgumentException("minShowIndex should not be greater than (mDisplayedValues.length - 1), now " +
                        "(mDisplayedValues.length - 1) is " + (mDisplayedValues.size() - 1) + " minShowIndex is " + minShowIndex);
            }

            if (maxShowIndex < 0) {
                throw new IllegalArgumentException("maxShowIndex should not be less than 0, now maxShowIndex is " + maxShowIndex);
            } else if (maxShowIndex > mDisplayedValues.size() - 1) {
                throw new IllegalArgumentException("maxShowIndex should not be greater than (mDisplayedValues.length - 1), now " +
                        "(mDisplayedValues.length - 1) is " + (mDisplayedValues.size() - 1) + " maxShowIndex is " + maxShowIndex);
            }
        }
        mMinShowIndex = minShowIndex;
        mMaxShowIndex = maxShowIndex;
        if (needRefresh) {
            mPrevPickedIndex = mMinShowIndex;
            correctPositionByDefaultValue(0, mWrapSelectorWheel && mWrapSelectorWheelCheck);
            postInvalidate();
        }
    }

    /**
     * set the friction of scroller, it will effect the scroller's acceleration when fling
     *
     * @param friction default is ViewConfiguration.get(mContext).getScrollFriction()
     *                 if setFriction(2 * ViewConfiguration.get(mContext).getScrollFriction()),
     *                 the friction will be twice as much as before
     */
    public void setFriction(float friction) {
        if (friction <= 0) {
            throw new IllegalArgumentException("you should set a a positive float friction, now friction is " + friction);
        }
        mFriction = ViewConfiguration.getScrollFriction() / friction;
    }

    //compatible for NumberPicker
    private void onScrollStateChange(int scrollState) {
        if (mScrollState == scrollState) {
            return;
        }
        mScrollState = scrollState;
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChange(this, scrollState);
        }
    }

    //compatible for NumberPicker
    public void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    //compatible for NumberPicker
    public void setOnValueChangedListener(OnValueChangeListener listener) {
        mOnValueChangeListener = listener;
    }

    public void setOnValueChangedListenerRelativeToRaw(OnValueChangeListenerRelativeToRaw listener) {
        mOnValueChangeListenerRaw = listener;
    }

    public void setOnValueChangeListenerInScrolling(OnValueChangeListenerInScrolling listener) {
        mOnValueChangeListenerInScrolling = listener;
    }

    public void setContentTextTypeface(Typeface typeface) {
        mTextPaint.setTypeface(typeface);
    }

    public void setHintTextTypeface(Typeface typeface) {
        mPaintHint.setTypeface(typeface);
    }

    //return index relative to mDisplayedValues from 0.
    private int getWillPickIndexByGlobalY(int globalY) {
        if (mItemHeight == 0) {
            return mPrevPickedIndex;
        }
        int willPickIndex = globalY / mItemHeight + mShownCount / 2;
        int index = getIndexByRawIndex(willPickIndex, getOneRecycleSize(), mWrapSelectorWheel && mWrapSelectorWheelCheck);
        if (0 <= index && index < getOneRecycleSize()) {
            return index + mMinShowIndex;
        } else {
            throw new IllegalArgumentException("getWillPickIndexByGlobalY illegal index : " + index
                    + " getOneRecycleSize() : " + getOneRecycleSize() + " mWrapSelectorWheel : " + mWrapSelectorWheel);
        }
    }

    private int getIndexByRawIndex(int index, int size, boolean wrap) {
        if (size <= 0) {
            return 0;
        }
        if (wrap) {
            index = index % size;
            if (index < 0) {
                index = index + size;
            }
        }
        return index;
    }

    private void internalSetWrapToLinear() {
        int rawIndex = getPickedIndexRelativeToRaw();
        correctPositionByDefaultValue(rawIndex - mMinShowIndex, false);
        mWrapSelectorWheel = false;
        postInvalidate();
    }

    private void updateDividerAttr() {
        mDividerIndex0 = mShownCount / 2;
        mDividerIndex1 = mDividerIndex0 + 1;
        dividerY0 = mDividerIndex0 * 1f * mViewHeight / mShownCount;
        dividerY1 = mDividerIndex1 * 1f * mViewHeight / mShownCount;
        /*if (mDividerMarginL < 0) {
            mDividerMarginL = 0;
        }
        if (mDividerMarginR < 0) {
            mDividerMarginR = 0;
        }*/

        if (mDividerMarginL + mDividerMarginR == 0) {
            return;
        }
        if (getPaddingLeft() + mDividerMarginL >= mViewWidth - getPaddingRight() - mDividerMarginR) {
            int surplusMargin = getPaddingLeft() + mDividerMarginL + getPaddingRight() + mDividerMarginR - mViewWidth;
            mDividerMarginL = (int) (mDividerMarginL - (float) surplusMargin * mDividerMarginL / (mDividerMarginL + mDividerMarginR));
            mDividerMarginR = (int) (mDividerMarginR - (float) surplusMargin * mDividerMarginR / (mDividerMarginL + mDividerMarginR));
        }
    }

    private int mNotWrapLimitYTop;
    private int mNotWrapLimitYBottom;

    private void updateFontAttr() {
        if (mTextSizeNormal > mItemHeight) {
            mTextSizeNormal = mItemHeight;
        }
        if (mTextSizeSelected > mItemHeight) {
            mTextSizeSelected = mItemHeight;
        }

        mPaintHint.setTextSize(mTextSizeHint);
        mTextSizeHintCenterYOffset = getTextCenterYOffset(mPaintHint.getFontMetrics());
        mWidthOfHintText = getTextWidth(mHintText, mPaintHint);

        mTextPaint.setTextSize(mTextSizeSelected);
        mTextSizeSelectedCenterYOffset = getTextCenterYOffset(mTextPaint.getFontMetrics());
        mTextPaint.setTextSize(mTextSizeNormal);
        mTextSizeNormalCenterYOffset = getTextCenterYOffset(mTextPaint.getFontMetrics());
    }

    private void updateNotWrapYLimit() {
        mNotWrapLimitYTop = 0;
        mNotWrapLimitYBottom = -mShownCount * mItemHeight;
        if (mDisplayedValues != null) {
            mNotWrapLimitYTop = (getOneRecycleSize() - mShownCount / 2 - 1) * mItemHeight;
            mNotWrapLimitYBottom = -(mShownCount / 2) * mItemHeight;
        }
    }

    private float downYGlobal = 0;
    private float downY = 0;
    private float currY = 0;

    private int limitY(int currDrawGlobalYPreferred) {
        if (mWrapSelectorWheel && mWrapSelectorWheelCheck) {
            return currDrawGlobalYPreferred;
        }
        if (currDrawGlobalYPreferred < mNotWrapLimitYBottom) {
            currDrawGlobalYPreferred = mNotWrapLimitYBottom;
        } else if (currDrawGlobalYPreferred > mNotWrapLimitYTop) {
            currDrawGlobalYPreferred = mNotWrapLimitYTop;
        }
        return currDrawGlobalYPreferred;
    }

    private boolean mFlagMayPress = false;

    @SuppressWarnings("StatementWithEmptyBody")
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        if (mItemHeight == 0) {
            return true;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        currY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFlagMayPress = true;
                mHandlerInNewThread.removeMessages(HANDLER_WHAT_REFRESH);
                stopScrolling();
                downY = currY;
                downYGlobal = mCurrDrawGlobalY;
                onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float spanY = downY - currY;
                if (mFlagMayPress && (-mScaledTouchSlop < spanY && spanY < mScaledTouchSlop)) {
                } else {
                    mFlagMayPress = false;
                    mCurrDrawGlobalY = limitY((int) (downYGlobal + spanY));
                    calculateFirstItemParameterByGlobalY();
                    invalidate();
                }
                onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                break;
            case MotionEvent.ACTION_UP:
                if (mFlagMayPress) {
                    click(event);
                } else {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000);
                    int velocityY = (int) (velocityTracker.getYVelocity() * mFriction);
                    if (Math.abs(velocityY) > mMiniVelocityFling) {
                        mScroller.fling(0, mCurrDrawGlobalY, 0, -velocityY,
                                Integer.MIN_VALUE, Integer.MAX_VALUE, limitY(Integer.MIN_VALUE), limitY(Integer.MAX_VALUE));
                        invalidate();
                        onScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
                    }
                    mHandlerInNewThread.sendMessageDelayed(getMsg(HANDLER_WHAT_REFRESH), 0);
                    releaseVelocityTracker();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                downYGlobal = mCurrDrawGlobalY;
                stopScrolling();
                mHandlerInNewThread.sendMessageDelayed(getMsg(HANDLER_WHAT_REFRESH), 0);
                break;
        }
        return true;
    }

    private void click(MotionEvent event) {
        float y = event.getY();
        for (int i = 0; i < mShownCount; i++) {
            if (mItemHeight * i <= y && y < mItemHeight * (i + 1)) {
                clickItem(i);
                break;
            }
        }
    }

    private void clickItem(int showCountIndex) {
        if (0 <= showCountIndex && showCountIndex < mShownCount) {
            //clicked the showCountIndex of the view
            scrollByIndexSmoothly(showCountIndex - mShownCount / 2);
        }
    }

    private float getTextCenterYOffset(Paint.FontMetrics fontMetrics) {
        if (fontMetrics == null) {
            return 0;
        }
        return Math.abs(fontMetrics.top + fontMetrics.bottom) / 2;
    }

    private int mViewWidth;
    private int mViewHeight;
    private int mItemHeight;
    private float dividerY0;
    private float dividerY1;
    private float mViewCenterX;

    //defaultPickedIndex relative to the shown part
    private void correctPositionByDefaultValue(int defaultPickedIndex, boolean wrap) {
        mCurrDrawFirstItemIndex = defaultPickedIndex - (mShownCount - 1) / 2;
        mCurrDrawFirstItemIndex = getIndexByRawIndex(mCurrDrawFirstItemIndex, getOneRecycleSize(), wrap);
        if (mItemHeight == 0) {
            mCurrentItemIndexEffect = true;
        } else {
            mCurrDrawGlobalY = mCurrDrawFirstItemIndex * mItemHeight;

            mInScrollingPickedOldValue = mCurrDrawFirstItemIndex + mShownCount / 2;
            mInScrollingPickedOldValue = mInScrollingPickedOldValue % getOneRecycleSize();
            if (mInScrollingPickedOldValue < 0) {
                mInScrollingPickedOldValue = mInScrollingPickedOldValue + getOneRecycleSize();
            }
            mInScrollingPickedNewValue = mInScrollingPickedOldValue;
            calculateFirstItemParameterByGlobalY();
        }
    }

    //first shown item's content index, corresponding to the Index of mDisplayedValued
    private int mCurrDrawFirstItemIndex = 0;
    //the first shown item's Y
    private int mCurrDrawFirstItemY = 0;
    //global Y corresponding to scroller
    private int mCurrDrawGlobalY = 0;

    @Override
    public void computeScroll() {
        if (mItemHeight == 0) {
            return;
        }
        if (mScroller.computeScrollOffset()) {
            mCurrDrawGlobalY = mScroller.getCurrY();
            calculateFirstItemParameterByGlobalY();
            postInvalidate();
        }
    }

    private int mOffsetY;

    public void setOffsetY(int offsetY) {
        mOffsetY = offsetY;
    }

    private void calculateFirstItemParameterByGlobalY() {
        mCurrDrawFirstItemIndex = (int) Math.floor((float) mCurrDrawGlobalY / mItemHeight);
        mCurrDrawFirstItemY = -(mCurrDrawGlobalY - mCurrDrawFirstItemIndex * mItemHeight) - mOffsetY;
        if (mOnValueChangeListenerInScrolling != null) {
            if (-mCurrDrawFirstItemY > mItemHeight / 2) {
                mInScrollingPickedNewValue = mCurrDrawFirstItemIndex + 1 + mShownCount / 2;
            } else {
                mInScrollingPickedNewValue = mCurrDrawFirstItemIndex + mShownCount / 2;
            }
            mInScrollingPickedNewValue = mInScrollingPickedNewValue % getOneRecycleSize();
            if (mInScrollingPickedNewValue < 0) {
                mInScrollingPickedNewValue = mInScrollingPickedNewValue + getOneRecycleSize();
            }
            if (mInScrollingPickedOldValue != mInScrollingPickedNewValue) {
                respondPickedValueChangedInScrolling(mInScrollingPickedOldValue + mMinValue, mInScrollingPickedNewValue + mMinValue);
            }
            mInScrollingPickedOldValue = mInScrollingPickedNewValue;
        }
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void updateMaxWHOfDisplayedValues(boolean needRequestLayout) {
        updateMaxWidthOfDisplayedValues();
        updateMaxHeightOfDisplayedValues();
        if (needRequestLayout &&
                (mSpecModeW == MeasureSpec.AT_MOST || mSpecModeH == MeasureSpec.AT_MOST)) {
            mHandlerInMainThread.sendEmptyMessage(HANDLER_WHAT_REQUEST_LAYOUT);
        }
    }

    private int mSpecModeW = MeasureSpec.UNSPECIFIED;
    private int mSpecModeH = MeasureSpec.UNSPECIFIED;

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = mSpecModeW = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            int marginOfHint = Math.max(mWidthOfHintText, mWidthOfAlterHint) == 0 ? 0 : mMarginEndOfHint;
            int gapOfHint = Math.max(mWidthOfHintText, mWidthOfAlterHint) == 0 ? 0 : mMarginStartOfHint;

            int maxWidth = Math.max(mMaxWidthOfAlterArrayWithMeasureHint,
                    Math.max(mMaxWidthOfDisplayedValues, mMaxWidthOfAlterArrayWithoutMeasureHint)
                            + 2 * (gapOfHint + Math.max(mWidthOfHintText, mWidthOfAlterHint) + marginOfHint + 2 * mItemPaddingHorizontal));
            result = this.getPaddingLeft() + this.getPaddingRight() + maxWidth;//MeasureSpec.UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result;
        int specMode = mSpecModeH = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            int maxHeight = (mShownCount - 1) * (mNormalHeightOfDisplayedValues + mItemPaddingVertical) + mMaxHeightOfDisplayedValues;
            result = this.getPaddingTop() + this.getPaddingBottom() + maxHeight;//MeasureSpec.UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawContent(canvas);
        drawLine(canvas);
        drawHint(canvas);
    }

    private void drawContent(Canvas canvas) {
        int index;
        int textColor;
        float textSize;
        float fraction = 0f;// fraction of the item in state between normal and selected, in[0, 1]
        float textSizeCenterYOffset;
        int availableWidth = getWidth() - 2 * mItemPaddingHorizontal;

        for (int i = 0; i < mShownCount + 1; i++) {
            float y = mCurrDrawFirstItemY + mItemHeight * i;
            index = getIndexByRawIndex(mCurrDrawFirstItemIndex + i, getOneRecycleSize(), mWrapSelectorWheel && mWrapSelectorWheelCheck);
            CharSequence text = null;
            boolean autoTextSize = mAutoTextSize;
            if (0 <= index && index < getOneRecycleSize()) {
                text = mDisplayedValues.get(index + mMinShowIndex);
                if (mTextEllipsize != null) {
                    autoTextSize = false;
                    text = TextUtils.ellipsize(text, mTextPaint, availableWidth, getEllipsizeType());
                }
            } else if (!TextUtils.isEmpty(mEmptyItemHint)) {
                text = mEmptyItemHint;
            }
            if (text == null) {
                continue;
            }

            if (i == mShownCount / 2) {//this will be picked
                fraction = (float) (mItemHeight + mCurrDrawFirstItemY) / mItemHeight;
                textColor = getEvaluateColor(fraction, mTextColorNormal, mTextColorSelected);

                if (autoTextSize) {
                    int selectTextSize = getTextSize(text, availableWidth);
                    int normalTextSize = getNormalTextSize(selectTextSize);
                    textSize = getEvaluateSize(fraction, normalTextSize, selectTextSize);
                } else {
                    textSize = getEvaluateSize(fraction, mTextSizeNormal, mTextSizeSelected);
                }

                //textSize = getEvaluateSize(fraction, mTextSizeNormal, mTextSizeSelected);
                textSizeCenterYOffset = getEvaluateSize(fraction, mTextSizeNormalCenterYOffset,
                        mTextSizeSelectedCenterYOffset);
            } else if (i == mShownCount / 2 + 1) {
                textColor = getEvaluateColor(1 - fraction, mTextColorNormal, mTextColorSelected);

                if (autoTextSize) {
                    int selectTextSize = getTextSize(text, availableWidth);
                    int normalTextSize = getNormalTextSize(selectTextSize);
                    textSize = getEvaluateSize(1 - fraction, normalTextSize, selectTextSize);
                } else {
                    textSize = getEvaluateSize(1 - fraction, mTextSizeNormal, mTextSizeSelected);
                }

                //textSize = getEvaluateSize(1 - fraction, mTextSizeNormal, mTextSizeSelected);
                textSizeCenterYOffset = getEvaluateSize(1 - fraction, mTextSizeNormalCenterYOffset,
                        mTextSizeSelectedCenterYOffset);
            } else {
                textColor = mTextColorNormal;

                if (autoTextSize) {
                    int selectTextSize = getTextSize(text, availableWidth);
                    textSize = getNormalTextSize(selectTextSize);
                } else {
                    textSize = mTextSizeNormal;
                }

                //textSize = mTextSizeNormal;
                textSizeCenterYOffset = mTextSizeNormalCenterYOffset;
            }
            mTextPaint.setColor(textColor);
            mTextPaint.setTextSize(textSize);

            /*String familyName = "sans-serif-light";
            Typeface tf = Typeface.create(familyName, Typeface.NORMAL);
            mPaintText.setTypeface(tf);*/

            canvas.drawText(text.toString(), mViewCenterX, y + mItemHeight / 2f + textSizeCenterYOffset, mTextPaint);
        }
    }

    private int getTextSize(CharSequence text, int availableWidth) {
        Integer cacheTextSize = mDisplayedValueTextSizeMap.get(text);
        if (cacheTextSize != null) {
            return cacheTextSize;
        }
        int selectTextSize = mAutoTextSizeHelper.findLargestTextSizeWhichFits(text, availableWidth);
        if (selectTextSize > mTextSizeSelected) {
            selectTextSize = mTextSizeSelected;
        }
        mDisplayedValueTextSizeMap.put(text, selectTextSize);
        return selectTextSize;
    }

    private int getNormalTextSize(int selectTextSize) {
        return selectTextSize - (mTextSizeSelected - mTextSizeNormal);
    }

    private TextUtils.TruncateAt getEllipsizeType() {
        switch (mTextEllipsize) {
            case TEXT_ELLIPSIZE_START:
                return TextUtils.TruncateAt.START;
            case TEXT_ELLIPSIZE_MIDDLE:
                return TextUtils.TruncateAt.MIDDLE;
            case TEXT_ELLIPSIZE_END:
                return TextUtils.TruncateAt.END;
            default:
                throw new IllegalArgumentException("Illegal text ellipsize type.");
        }
    }

    public void setDividerMarginR(int margin) {
        mDividerMarginR = margin;
        requestLayout();
    }

    private int mDividerDrawable;

    private void drawLine(Canvas canvas) {
        if (mShowDivider) {
            if (mDividerDrawable != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mDividerDrawable);
                int x = (mViewWidth - bitmap.getWidth()) / 2;
                canvas.drawBitmap(bitmap, x, dividerY0, new Paint());
                canvas.drawBitmap(bitmap, x, dividerY1, new Paint());
                return;
            }
            canvas.drawLine(getPaddingLeft() + mDividerMarginL,
                    dividerY0, mViewWidth - getPaddingRight() - mDividerMarginR, dividerY0, mPaintDivider);
            canvas.drawLine(getPaddingLeft() + mDividerMarginL,
                    dividerY1, mViewWidth - getPaddingRight() - mDividerMarginR, dividerY1, mPaintDivider);
        }
    }

    private void drawHint(Canvas canvas) {
        if (TextUtils.isEmpty(mHintText)) {
            return;
        }
        canvas.drawText(mHintText,
                mViewCenterX + (mMaxWidthOfDisplayedValues + mWidthOfHintText) / 2f + mMarginStartOfHint,
                (dividerY0 + dividerY1) / 2 + mTextSizeHintCenterYOffset, mPaintHint);
    }

    private void updateMaxWidthOfDisplayedValues() {
        float savedTextSize = mTextPaint.getTextSize();
        mTextPaint.setTextSize(mTextSizeSelected);
        mMaxWidthOfDisplayedValues = getMaxWidthOfTextArray(mDisplayedValues, mTextPaint);
        mMaxWidthOfAlterArrayWithMeasureHint = getMaxWidthOfTextArray(mAlterTextArrayWithMeasureHint, mTextPaint);
        mMaxWidthOfAlterArrayWithoutMeasureHint = getMaxWidthOfTextArray(mAlterTextArrayWithoutMeasureHint, mTextPaint);
        mTextPaint.setTextSize(mTextSizeHint);
        mWidthOfAlterHint = getTextWidth(mAlterHint, mTextPaint);
        mTextPaint.setTextSize(savedTextSize);
    }

    private int getMaxWidthOfTextArray(List<? extends CharSequence> array, Paint paint) {
        if (array == null) {
            return 0;
        }
        int maxWidth = 0;
        for (CharSequence item : array) {
            if (item != null) {
                int itemWidth = getTextWidth(item, paint);
                maxWidth = Math.max(itemWidth, maxWidth);
            }
        }
        return maxWidth;
    }

    private int getTextWidth(CharSequence text, Paint paint) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        String key = text.toString();

        if (mTextWidthCache.containsKey(key)) {
            Integer integer = mTextWidthCache.get(key);
            if (integer != null) {
                return integer;
            }
        }

        int value = (int) (paint.measureText(key) + 0.5f);
        mTextWidthCache.put(key, value);
        return value;
    }

    private void updateMaxHeightOfDisplayedValues() {
        float savedTextSize = mTextPaint.getTextSize();
        mTextPaint.setTextSize(mTextSizeSelected);
        mMaxHeightOfDisplayedValues = (int) (mTextPaint.getFontMetrics().bottom - mTextPaint.getFontMetrics().top + 0.5);
        mTextPaint.setTextSize(mTextSizeNormal);
        mNormalHeightOfDisplayedValues = (int) (mTextPaint.getFontMetrics().bottom - mTextPaint.getFontMetrics().top + 0.5);
        mTextPaint.setTextSize(savedTextSize);
    }

    private void updateContentAndIndex(List<CharSequence> newDisplayedValues) {
        mMinShowIndex = 0;
        mMaxShowIndex = newDisplayedValues.size() - 1;
        mDisplayedValues = newDisplayedValues;
        updateWrapStateByContent();
    }

    private void updateContent(List<? extends CharSequence> newDisplayedValues) {
        mDisplayedValues = newDisplayedValues;
        updateWrapStateByContent();
    }

    //used in setDisplayedValues
    private void updateValue() {
        inflateDisplayedValuesIfNull();
        updateWrapStateByContent();
        mMinShowIndex = 0;
        mMaxShowIndex = mDisplayedValues.size() - 1;
    }

    private void updateValueForInit() {
        inflateDisplayedValuesIfNull();
        updateWrapStateByContent();
        if (mMinShowIndex == -1) {
            mMinShowIndex = 0;
        }
        if (mMaxShowIndex == -1) {
            mMaxShowIndex = mDisplayedValues.size() - 1;
        }
        setMinAndMaxShowIndex(mMinShowIndex, mMaxShowIndex, false);
    }

    private void inflateDisplayedValuesIfNull() {
        if (mDisplayedValues == null) {
            ArrayList<String> temp = new ArrayList<>(1);
            temp.add(0, "0");
            mDisplayedValues = temp;
        }
    }

    private void updateWrapStateByContent() {
        mWrapSelectorWheelCheck = mDisplayedValues.size() > mShownCount;
    }

    private int refineValueByLimit(int value, int minValue, int maxValue, boolean wrap) {
        if (wrap) {
            if (value > maxValue) {
                value = (value - maxValue) % getOneRecycleSize() + minValue - 1;
            } else if (value < minValue) {
                value = (value - minValue) % getOneRecycleSize() + maxValue + 1;
            }
        } else {
            if (value > maxValue) {
                value = maxValue;
            } else if (value < minValue) {
                value = minValue;
            }
        }
        return value;
    }

    private void stopRefreshing() {
        if (mHandlerInNewThread != null) {
            mHandlerInNewThread.removeMessages(HANDLER_WHAT_REFRESH);
        }
    }

    public void stopScrolling() {
        if (mScroller != null) {
            if (!mScroller.isFinished()) {
                mScroller.startScroll(0, mScroller.getCurrY(), 0, 0, 1);
                mScroller.abortAnimation();
                postInvalidate();
            }
        }
    }

    public void stopScrollingAndCorrectPosition() {
        stopScrolling();
        if (mHandlerInNewThread != null) {
            mHandlerInNewThread.sendMessageDelayed(getMsg(HANDLER_WHAT_REFRESH), 0);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private Message getMsg(int what) {
        return getMsg(what, 0, 0, null);
    }

    private Message getMsg(int what, int arg1, int arg2, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        return msg;
    }

    //===tool functions===//
    private boolean isStringEqual(String a, String b) {
        if (a == null) {
            return b == null;
        } else {
            return a.equals(b);
        }
    }

    private int sp2px(float spValue) {
        //final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        //return (int) (spValue * fontScale + 0.5f);
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }

    private int dp2px(float dpValue) {
        //final float densityScale = context.getResources().getDisplayMetrics().density;
        //return (int) (dpValue * densityScale + 0.5f);
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    private int getEvaluateColor(float fraction, int startColor, int endColor) {

        int a, r, g, b;

        int sA = (startColor & 0xff000000) >>> 24;
        int sR = (startColor & 0x00ff0000) >>> 16;
        int sG = (startColor & 0x0000ff00) >>> 8;
        int sB = (startColor & 0x000000ff);

        int eA = (endColor & 0xff000000) >>> 24;
        int eR = (endColor & 0x00ff0000) >>> 16;
        int eG = (endColor & 0x0000ff00) >>> 8;
        int eB = (endColor & 0x000000ff);

        a = (int) (sA + (eA - sA) * fraction);
        r = (int) (sR + (eR - sR) * fraction);
        g = (int) (sG + (eG - sG) * fraction);
        b = (int) (sB + (eB - sB) * fraction);

        return a << 24 | r << 16 | g << 8 | b;
    }

    private float getEvaluateSize(float fraction, float startSize, float endSize) {
        return startSize + (endSize - startSize) * fraction;
    }

    private List<CharSequence> convertCharSequenceArrayToList(CharSequence[] charSequences) {
        if (charSequences == null) {
            return null;
        }
        List<CharSequence> ret = new ArrayList<>(charSequences.length);
        for (CharSequence charSequence : charSequences) {
            ret.add(charSequence.toString());
        }
        return ret;
    }

    public void setAutoTextSize(boolean autoTextSize) {
        mAutoTextSize = autoTextSize;
    }

    public boolean getAutoTextSize() {
        return mAutoTextSize;
    }

    public void setNormalTextSize(float textSize) {
        mTextSizeNormal = sp2px(textSize);
    }

    public void setSelectTextSize(float textSize) {
        mTextSizeSelected = sp2px(textSize);
    }

    private class AutoTextSizeHelper {
        private TextPaint mTempTextPaint;
        private int[] mAutoSizeTextSizesInPx = new int[0];

        AutoTextSizeHelper() {
            setupAutoSizeText();
        }

        private void setupAutoSizeText() {
            int maxTextSizeInPx = mTextSizeSelected;
            int minTextSizeInPx = mTextSizeSelected / 2;
            float stepInPx = 20;
            // Calculate sizes to choose from based on the current auto-size configuration.
            final int autoSizeValuesLength = (int) (Math.floor((maxTextSizeInPx - minTextSizeInPx) / stepInPx)) + 1;
            final int[] autoSizeTextSizesInPx = new int[autoSizeValuesLength];
            for (int i = 0; i < autoSizeValuesLength; i++) {
                autoSizeTextSizesInPx[i] = Math.round(minTextSizeInPx + (i * stepInPx));
            }
            mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(autoSizeTextSizesInPx);
        }

        private int[] cleanupAutoSizePresetSizes(int[] presetValues) {
            final int presetValuesLength = presetValues.length;
            if (presetValuesLength == 0) {
                return presetValues;
            }
            Arrays.sort(presetValues);

            final List<Integer> uniqueValidSizes = new ArrayList<>();
            for (final int currentPresetValue : presetValues) {
                if (currentPresetValue > 0
                        && Collections.binarySearch(uniqueValidSizes, currentPresetValue) < 0) {
                    uniqueValidSizes.add(currentPresetValue);
                }
            }

            if (presetValuesLength == uniqueValidSizes.size()) {
                return presetValues;
            } else {
                final int uniqueValidSizesLength = uniqueValidSizes.size();
                final int[] cleanedUpSizes = new int[uniqueValidSizesLength];
                for (int i = 0; i < uniqueValidSizesLength; i++) {
                    cleanedUpSizes[i] = uniqueValidSizes.get(i);
                }
                return cleanedUpSizes;
            }
        }

        private int findLargestTextSizeWhichFits(CharSequence text, int availableWidth) {
            final int sizesCount = mAutoSizeTextSizesInPx.length;
            if (sizesCount == 0) {
                throw new IllegalStateException("No available text sizes to choose from.");
            }

            int bestSizeIndex = 0;
            int lowIndex = bestSizeIndex + 1;
            int highIndex = sizesCount - 1;
            int sizeToTryIndex;
            while (lowIndex <= highIndex) {
                sizeToTryIndex = (lowIndex + highIndex) / 2;
                if (suggestedSizeFitsInSpace(text, mAutoSizeTextSizesInPx[sizeToTryIndex], availableWidth)) {
                    bestSizeIndex = lowIndex;
                    lowIndex = sizeToTryIndex + 1;
                } else {
                    highIndex = sizeToTryIndex - 1;
                    bestSizeIndex = highIndex;
                }
            }
            return Math.min(mTextSizeSelected, mAutoSizeTextSizesInPx[bestSizeIndex]);
        }

        private boolean suggestedSizeFitsInSpace(CharSequence text, int suggestedSizeInPx, int availableWidth) {
            final int maxLines = 1;
            initTempTextPaint(suggestedSizeInPx);
            final StaticLayout layout = createLayout(text, Math.round(availableWidth));
            // Lines overflow.
            return layout.getLineCount() <= 1 && layout.getLineEnd(layout.getLineCount() - 1) == text.length();
        }

        @SuppressLint("ObsoleteSdkInt")
        StaticLayout createLayout(CharSequence text, int availableWidth) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return createStaticLayoutForMeasuring(text, availableWidth);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                return createStaticLayoutForMeasuringPre23(text, availableWidth);
            } else {
                return createStaticLayoutForMeasuringPre16(text, availableWidth);
            }
        }

        @SuppressLint("WrongConstant")
        @RequiresApi(23)
        private StaticLayout createStaticLayoutForMeasuring(CharSequence text, int availableWidth) {
            final StaticLayout.Builder layoutBuilder = StaticLayout.Builder.obtain(
                    text, 0, text.length(), mTempTextPaint, availableWidth);

            layoutBuilder.setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setLineSpacing(0, 1)
                    .setIncludePad(false)
                    .setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE)
                    .setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE)
                    .setMaxLines(1);

            layoutBuilder.setTextDirection(TextDirectionHeuristics.LTR);
            return layoutBuilder.build();
        }

        @RequiresApi(16)
        private StaticLayout createStaticLayoutForMeasuringPre23(CharSequence text, int availableWidth) {
            // The layout could not be constructed using the builder so fall back to the
            // most broad constructor.
            return new StaticLayout(text, mTempTextPaint, availableWidth, Layout.Alignment.ALIGN_CENTER, 1, 0, false);
        }

        private StaticLayout createStaticLayoutForMeasuringPre16(CharSequence text, int availableWidth) {
            // The default values have been inlined with the StaticLayout defaults.
            return new StaticLayout(text, mTempTextPaint, availableWidth, Layout.Alignment.ALIGN_CENTER, 1, 0, false);
        }

        void initTempTextPaint(final int suggestedSizeInPx) {
            if (mTempTextPaint == null) {
                mTempTextPaint = new TextPaint();
            } else {
                mTempTextPaint.reset();
            }
            mTempTextPaint.set(mTextPaint);
            mTempTextPaint.setTextSize(suggestedSizeInPx);
        }
    }
}