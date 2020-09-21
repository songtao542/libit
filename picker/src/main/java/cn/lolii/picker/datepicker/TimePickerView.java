package cn.lolii.picker.datepicker;

import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.Locale;

import cn.lolii.picker.NumberPickerView;
import cn.lolii.picker.R;


public class TimePickerView extends LinearLayout implements NumberPickerView.OnValueChangeListener {
    private static final String TAG = "TimePickerView";
    private static final String FORMAT_TWO_NUMBER = "%02d";

    private NumberPickerView mPickerViewAmPm;
    private NumberPickerView mPickerViewTimeDivider;
    private NumberPickerView mPickerViewHour;
    private NumberPickerView mPickerViewMinute;
    private Calendar mCalendar;
    private boolean mIs24Hour;
    private boolean mAuto24Hour = true; //自动根据系统24小时显示不同view

    private String[] mDisplayHour24;
    private String[] mDisplayHour12;
    private String[] mDisplayMinute;
    private OnTimeChangeListener mOnTimeChangeListener;

    public TimePickerView(Context context) {
        this(context, null);
    }

    public TimePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInternal(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TimePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInternal(context);
    }

    private void initInternal(Context context) {
        final View inflate = View.inflate(context, R.layout.time_picker_layout, this);
        mPickerViewAmPm = (NumberPickerView) inflate.findViewById(R.id.picker_amPm);
        mPickerViewTimeDivider = (NumberPickerView) inflate.findViewById(R.id.picker_time_divider);
        mPickerViewHour = (NumberPickerView) inflate.findViewById(R.id.picker_hour);
        mPickerViewMinute = (NumberPickerView) inflate.findViewById(R.id.picker_min);
        mPickerViewAmPm.setOnValueChangedListener(this);
        mPickerViewHour.setOnValueChangedListener(this);
        mPickerViewMinute.setOnValueChangedListener(this);
        mPickerViewTimeDivider.setPickedIndexRelativeToMin(1);
        mPickerViewTimeDivider.setEnabled(false);
        mPickerViewTimeDivider.setOffsetY(6);

        mIs24Hour = DateFormat.is24HourFormat(context);
        mCalendar = Calendar.getInstance();
        initDisplayTime();
    }

    public void setItemWrapContent() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPickerViewAmPm.setLayoutParams(params);
        mPickerViewHour.setLayoutParams(params);
        mPickerViewMinute.setLayoutParams(params);
    }

    public void setItemPadding(int paddingPx) {
        mPickerViewAmPm.setPadding(paddingPx, 0, paddingPx, 0);
        mPickerViewHour.setPadding(paddingPx, 0, paddingPx, 0);
        mPickerViewMinute.setPadding(paddingPx, 0, paddingPx, 0);
    }

    private ViewTreeObserver.OnWindowFocusChangeListener mOnWindowFocusChangeListener;


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (mOnWindowFocusChangeListener == null) {
                mOnWindowFocusChangeListener = new ViewTreeObserver.OnWindowFocusChangeListener() {
                    @Override
                    public void onWindowFocusChanged(boolean hasFocus) {
                        if (!hasFocus || !mAuto24Hour) {
                            return;
                        }
                        boolean is24Hour = DateFormat.is24HourFormat(getContext());
                        Log.d(TAG, "onWindowFocusChanged() hasFocus is24Hour:" + is24Hour);
                        if (mIs24Hour != is24Hour) {
                            mIs24Hour = is24Hour;
                            initDisplayTime();
                        }
                    }
                };
                getViewTreeObserver().addOnWindowFocusChangeListener(mOnWindowFocusChangeListener);
            }
        }
    }

    public void setTime(int hour, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        initDisplayTime();
    }

    /**
     * @param hour 12-hour clock (0 - 11).  0 在12小时制显示为12
     * @param amPm {@link Calendar#AM} or {@link Calendar#PM}
     */
    public void setTime(int hour, int minute, int amPm) {
        mCalendar.set(Calendar.HOUR, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        if (amPm == Calendar.AM || amPm == Calendar.PM) {
            mCalendar.set(Calendar.AM_PM, amPm);
        }
        initDisplayTime();
    }

    public void setIs24Hour(boolean is24Hour) {
        mAuto24Hour = false;
        if (mIs24Hour == is24Hour) {
            return;
        }
        mIs24Hour = is24Hour;
        initDisplayTime();
    }

    private void initDisplayTime() {
        initDisplayTime(mCalendar);
    }

    public void initDisplayTime(Calendar calendar) {
        if (mIs24Hour) {
            mPickerViewAmPm.setVisibility(View.GONE);
            mPickerViewTimeDivider.setVisibility(VISIBLE);
        } else {
            mPickerViewTimeDivider.setVisibility(GONE);
            mPickerViewAmPm.setVisibility(View.VISIBLE);
            mPickerViewAmPm.setDisplayedValues(getContext().getResources().getStringArray(R.array.am_pm_entries), false);
            initPickerViewData(mPickerViewAmPm, 0, 1, calendar.get(Calendar.AM_PM));
        }
        // hour
        if (mIs24Hour && mDisplayHour24 == null) {
            mDisplayHour24 = new String[24];
            for (int i = 0; i < 24; i++) {
                mDisplayHour24[i] = String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, i);
            }
        }
        if (!mIs24Hour && mDisplayHour12 == null) {
            mDisplayHour12 = new String[12];
            for (int i = 0; i < 12; i++) {
                if (i == 0) {
                    mDisplayHour12[i] = String.valueOf(12);
                } else {
                    mDisplayHour12[i] = String.valueOf(i);
                }
            }
        }
        String[] displayHour = mIs24Hour ? mDisplayHour24 : mDisplayHour12;
        mPickerViewHour.setDisplayedValues(displayHour, false);
        int hour = mIs24Hour ? calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR);
        initPickerViewData(mPickerViewHour, 0, displayHour.length - 1, hour);

        // minute
        if (mDisplayMinute == null) {
            mDisplayMinute = new String[60];
            for (int i = 0; i < 60; i++) {
                mDisplayMinute[i] = String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, i);
            }
        }
        mPickerViewMinute.setDisplayedValues(mDisplayMinute, false);
        initPickerViewData(mPickerViewMinute, 0, 59, calendar.get(Calendar.MINUTE));
        if (mOnTimeChangeListener != null) {
            mOnTimeChangeListener.onTimeChange(getTimeData());
        }
    }

    private void initPickerViewData(NumberPickerView picker, int minValue, int maxValue, int value) {
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setValue(value);
    }

    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
        Log.d(TAG, "onValueChange() picker new:" + newVal);
        if (picker == mPickerViewAmPm) {
            mCalendar.set(Calendar.AM_PM, newVal);
        } else if (picker == mPickerViewHour) {
            if (mIs24Hour) {
                mCalendar.set(Calendar.HOUR_OF_DAY, newVal);
            } else {
                mCalendar.set(Calendar.HOUR, newVal);
            }
        } else if (picker == mPickerViewMinute) {
            mCalendar.set(Calendar.MINUTE, newVal);
        }

        if (mOnTimeChangeListener != null) {
            mOnTimeChangeListener.onTimeChange(getTimeData());
        }
    }

    public NumberPickerView getPickerViewAmPm() {
        return mPickerViewAmPm;
    }

    public NumberPickerView getPickerViewTimeDivider() {
        return mPickerViewTimeDivider;
    }

    public NumberPickerView getPickerViewHour() {
        return mPickerViewHour;
    }

    public NumberPickerView getPickerViewMinute() {
        return mPickerViewMinute;
    }

    public static class TimeData {
        public int mPickedAmPm;
        public int mPickedHour;
        public int mPickedMin;
        public boolean mIs24HourMode;

        public TimeData(int pickedAmPm, int pickedHour, int pickedMin, boolean is24HourMode) {
            this.mPickedAmPm = pickedAmPm;
            if (!is24HourMode && pickedHour == 0) { //12小时制0 == 12
                this.mPickedHour = 12;
            } else {
                this.mPickedHour = pickedHour;
            }
            this.mPickedMin = pickedMin;
            this.mIs24HourMode = is24HourMode;
        }
    }

    public TimeData getTimeData() {
        return new TimeData(mPickerViewAmPm.getValue(), mPickerViewHour.getValue(), mPickerViewMinute.getValue(), mIs24Hour);
    }

    public void setOnTimeChangeListener(OnTimeChangeListener onTimeChangeListener) {
        mOnTimeChangeListener = onTimeChangeListener;
    }

    public interface OnTimeChangeListener {
        void onTimeChange(TimeData timeData);
    }

    public boolean is24HourView() {
        return mIs24Hour;
    }
}

