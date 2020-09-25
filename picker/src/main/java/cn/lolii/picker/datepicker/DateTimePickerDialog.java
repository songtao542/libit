
package cn.lolii.picker.datepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import java.util.Calendar;
import java.util.Locale;

import cn.lolii.picker.R;


@SuppressWarnings("unused")
public class DateTimePickerDialog {
    private static final String TAG = DateTimePickerDialog.class.getSimpleName();
    private static final String FORMAT_TWO_NUMBER = "%02d";

    private final Context mContext;
    private boolean mIsAutoUpdateTitle = true;
    private boolean mIsWithViewDate;
    private boolean mIsWithViewTime;
    private final GregorianLunarCalendarView mCalendarView;
    private final TimePickerView mTimePickerView;
    private GregorianLunarCalendarView.CalendarData mCalendarData;
    private TimePickerView.TimeData mTimeData;
    private final AlertDialog mDialog;
    private String mDateStr, mTimeStr;

    protected DateTimePickerDialog(Context context, AlertDialog dialog, GregorianLunarCalendarView calendarView, TimePickerView timePicker) {
        mContext = context;
        mDialog = dialog;
        mCalendarView = calendarView;
        mTimePickerView = timePicker;
    }

    public void updateTitle(GregorianLunarCalendarView.CalendarData calendarData) {
        mDateStr = getDateString(calendarData);
        String title = mDateStr;
        if (mIsWithViewTime) {
            title = mDateStr + "  " + mTimeStr;
        }
        updateTitle(title);
    }

    public void updateTitle(TimePickerView.TimeData timeData) {
        mTimeStr = getTimeString(timeData);
        String title = mTimeStr;
        if (mIsWithViewDate) {
            title = mDateStr + "  " + mTimeStr;
        }
        updateTitle(title);
    }

    public void updateTitle(CharSequence title) {
        if (mDialog != null) {
            mDialog.setTitle(title);
        }
    }

    public void setAutoUpdateTitle(boolean enable) {
        mIsAutoUpdateTitle = enable;
    }

    private void setWithView(boolean withDate, boolean withTime) {
        mIsWithViewDate = withDate;
        mIsWithViewTime = withTime;
    }

    private void setDateTimeChangeListener(final OnDateTimeChangeListener listener) {
        if (mCalendarView != null) {
            mCalendarView.setOnDateChangedListener(calendarData -> {
                mCalendarData = calendarData;
                if (mIsAutoUpdateTitle) {
                    updateTitle(calendarData);
                }
                if (listener != null) {
                    listener.onDateChanged(DateTimePickerDialog.this, calendarData);
                }
            });
        }
        if (mTimePickerView != null) {
            mTimePickerView.setOnTimeChangeListener(timeData -> {
                mTimeData = timeData;
                if (mIsAutoUpdateTitle) {
                    updateTitle(timeData);
                }
                if (listener != null) {
                    listener.onTimeChanged(DateTimePickerDialog.this, timeData);
                }
            });
        }
    }

    public TimePickerView.TimeData getSelectedTime() {
        if (mTimeData == null && mTimePickerView != null) {
            mTimeData = mTimePickerView.getTimeData();
        }
        return mTimeData;
    }

    public String getSelectedTimeToString() {
        return getTimeString(getSelectedTime());
    }

    private String getTimeString(TimePickerView.TimeData timeData) {
        StringBuilder builder = new StringBuilder();
        if (timeData.mIs24HourMode) {
            builder.append(String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, timeData.mPickedHour));
        } else {
            builder.append(mContext.getResources().getStringArray(R.array.am_pm_entries)[timeData.mPickedAmPm]);
            builder.append(timeData.mPickedHour);
        }
        builder.append(mContext.getResources().getString(R.string.time_divider));
        builder.append(String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, timeData.mPickedMin));
        return builder.toString();
    }

    public GregorianLunarCalendarView.CalendarData getSelectedDate() {
        if (mCalendarData == null && mCalendarView != null) {
            mCalendarData = mCalendarView.getCalendarData();
        }
        return mCalendarData;
    }

    public String getSelectedDateToString() {
        return getDateString(getSelectedDate());
    }

    @SuppressLint("WrongConstant")
    private String getDateString(GregorianLunarCalendarView.CalendarData data) {
        ChineseCalendar calendar = data.getCalendar();
        StringBuilder builder = new StringBuilder();
        if (data.isGregorian) {
            builder.append(calendar.get(Calendar.YEAR)).append(mContext.getString(R.string.year));
            builder.append(calendar.get(Calendar.MONTH) + 1).append(mContext.getString(R.string.month));
            builder.append(calendar.get(Calendar.DAY_OF_MONTH)).append(mContext.getString(R.string.day));
        } else {
            builder.append(calendar.get(ChineseCalendar.CHINESE_YEAR)).append(mContext.getString(R.string.year));
            try {
                builder.append(LunarUtil.getLunarNameOfMonth(calendar.get(ChineseCalendar.CHINESE_MONTH)));
                builder.append(LunarUtil.getLunarNameOfDay(calendar.get(ChineseCalendar.CHINESE_DATE)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        builder.append("  ");
        builder.append(LunarUtil.getNameDayOfWeek(calendar.get(ChineseCalendar.DAY_OF_WEEK)));
        return builder.toString();
    }

    public interface OnDateTimeChangeListener {
        default void onDateChanged(DateTimePickerDialog dialog, GregorianLunarCalendarView.CalendarData calendarData) {
        }

        default void onTimeChanged(DateTimePickerDialog dialog, TimePickerView.TimeData timeData) {
        }
    }

    public TimePickerView getTimePickerView() {
        return mTimePickerView;
    }

    public GregorianLunarCalendarView getGregorianLunarCalendarView() {
        return mCalendarView;
    }

    public AlertDialog getDialog() {
        return mDialog;
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public boolean isGregorian() {
        if (mCalendarView == null) {
            return false;
        }
        return mCalendarView.isGregorian();
    }

    public boolean toGregorianMode() {
        if (mCalendarView == null) {
            return false;
        }
        return mCalendarView.toGregorianMode();
    }

    public boolean toLunarMode() {
        if (mCalendarView == null) {
            return false;
        }
        return mCalendarView.toLunarMode();
    }


    /**
     * -------   Builder   -------
     */
    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {
        private final Context mContext;
        private final ChineseCalendar mCalendar;
        private final AlertDialog.Builder mBuilder;
        private int mYearMin, mYearMax;
        private boolean mIsAutoUpdateTitle = true;
        private boolean mIsShowGregorian = true;
        private boolean mChangeDateModeEnable = false;

        private boolean mIsWithViewDate;
        private boolean mIsWithViewTime;
        private OnDateTimeChangeListener mDateTimeChangeListener;
        private int mShow24Hour = -1;

        private boolean mCanceledOnTouchOutside = true;
        private int mGravity = Gravity.BOTTOM;

        public Builder(Context context, int theme) {
            mContext = context;
            mCalendar = new ChineseCalendar();
            mBuilder = new AlertDialog.Builder(context, theme);
            mBuilder.setTitle(0 + ""); //避免外部未设置时无法显示title
        }

        public Builder(Context context) {
            this(context, R.style.PickerDialog);
        }

        public Builder(Context context, int year, int month, int day) {
            this(context, R.style.PickerDialog);
            setDefaultDate(year, month, day);
        }

        public Builder(Context context, int hour, int minute) {
            this(context, R.style.PickerDialog);
            setDefaultTime(hour, minute);
        }

        /**
         * 调用此方法后，将不默认自动更新title。如需自动，请再调用{@link #setAutoUpdateTitle(boolean)}
         */
        public Builder setTitle(CharSequence title) {
            mIsAutoUpdateTitle = false;
            mBuilder.setTitle(title);
            return this;
        }

        public Builder setAutoUpdateTitle(boolean enable) {
            mIsAutoUpdateTitle = enable;
            return this;
        }

        public Builder setWithDate(boolean isWith) {
            mIsWithViewDate = isWith;
            return this;
        }

        public Builder setWithTime(boolean isWith) {
            mIsWithViewTime = isWith;
            return this;
        }

        /**
         * @param hour [0,23] 设置到Calendar.HOUR_OF_DAY
         */
        public Builder setDefaultTime(int hour, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hour);
            mCalendar.set(Calendar.MINUTE, minute);
            mIsWithViewTime = true;
            return this;
        }

        /**
         * @param hour 12-hour clock (0 - 11).  0 在12小时制显示为12
         * @param amPm {@link Calendar#AM} or {@link Calendar#PM}
         */
        public Builder setDefaultTime(int hour, int minute, int amPm) {
            mCalendar.set(Calendar.HOUR, hour);
            mCalendar.set(Calendar.MINUTE, minute);
            if (amPm == Calendar.AM || amPm == Calendar.PM) {
                mCalendar.set(Calendar.AM_PM, amPm);
            }
            mIsWithViewTime = true;
            return this;
        }

        public Builder setTimeShow24Hour(boolean isShow24Hour) {
            mShow24Hour = isShow24Hour ? 1 : 0;
            return this;
        }


        public Builder setYearLimited(int minYear, int maxYear) {
            mYearMin = minYear;
            mYearMax = maxYear;
            return this;
        }

        /**
         * 以公历日期初始化
         */
        public Builder setDefaultDate(int year, int month, int day) {
            setDefaultDate(year, month, day, false);
            return this;
        }

        /**
         * 以公历/农历日期初始化
         *
         * @param isLunar true 农历
         */
        @SuppressLint("WrongConstant")
        public Builder setDefaultDate(int year, int month, int day, boolean isLunar) {
            if (isLunar) {
                mCalendar.set(ChineseCalendar.CHINESE_YEAR, year);
                mCalendar.set(ChineseCalendar.CHINESE_MONTH, month);
                mCalendar.set(ChineseCalendar.CHINESE_DATE, day);
            } else {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month - 1);
                mCalendar.set(Calendar.DAY_OF_MONTH, day);
            }
            mIsWithViewDate = true;
            return this;
        }

        public Builder setDateTimeChangeListener(OnDateTimeChangeListener listener) {
            mDateTimeChangeListener = listener;
            return this;
        }

        /**
         * 用于设置创造Dialog时参数，如已显示，需要切换请用toGregorianMode() or toLunarMode()
         *
         * @param isShowGregorian false 表示农历，默认true 公历
         */
        public Builder setShowGregorian(boolean isShowGregorian) {
            mIsShowGregorian = isShowGregorian;
            return this;
        }

        public Builder setChangeDateModeEnable(boolean enable) {
            mChangeDateModeEnable = enable;
            return this;
        }

        public DateTimePickerDialog create() {
            View contentView;
            GregorianLunarCalendarView calendarView = null;
            TimePickerView timePickerView = null;
            if (!mIsWithViewDate && mIsWithViewTime) {  // 只设置时间
                contentView = View.inflate(mContext, R.layout.dialog_time_picker, null);
                timePickerView = contentView.findViewById(R.id.time_picker_view);
                if (mShow24Hour >= 0) {
                    timePickerView.setIs24Hour(mShow24Hour == 1);
                }
            } else if (mIsWithViewDate && mIsWithViewTime) {    // 日期时间都设置
                contentView = View.inflate(mContext, R.layout.dialog_date_time_picker, null);
                calendarView = contentView.findViewById(R.id.date_picker_view);
                timePickerView = contentView.findViewById(R.id.time_picker_view);
                timePickerView.setIs24Hour(true);   // UI设计日期时间同时显示时，只有24h
                timePickerView.setItemWrapContent();
                timePickerView.setItemPadding(mContext.getResources().getDimensionPixelSize(R.dimen.dialog_time_item_padding));
            } else {    // 其他情况只设置日期，默认
                contentView = View.inflate(mContext, R.layout.dialog_date_picker, null);
                calendarView = contentView.findViewById(R.id.date_picker_view);

                GregorianLunarCalendarView cv = contentView.findViewById(R.id.date_picker_view);
                Calendar calendar = Calendar.getInstance();
                calendar.set(2020);
                cv.setMinValue();
            }
            mBuilder.setView(contentView);
            AlertDialog dialog = mBuilder.create();
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(mGravity);
            }
            dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
            //先创建pickerDialog实例，后续设置数据回调onChange
            DateTimePickerDialog pickerDialog = new DateTimePickerDialog(mContext, dialog, calendarView, timePickerView);
            pickerDialog.setDateTimeChangeListener(mDateTimeChangeListener);
            pickerDialog.setAutoUpdateTitle(mIsAutoUpdateTitle);
            pickerDialog.setWithView(mIsWithViewDate, mIsWithViewTime);

            //设置日期、时间数据，默认为当前系统日期时间
            if (calendarView != null) {
                Log.d(TAG, "show() mIsAutoUpdateTitle:" + mIsAutoUpdateTitle + ",mIsShowGregorian:" + mIsShowGregorian);
                calendarView.setMinValue(mYearMin);
                calendarView.setMaxValue(mYearMax);
                calendarView.init(mCalendar, mIsShowGregorian);
                if (mChangeDateModeEnable) {
                    initDateModeBtn(contentView, calendarView);
                }
            }
            if (timePickerView != null) {
                timePickerView.initDisplayTime(mCalendar);
            }
            Log.d(TAG, "show() withDate:" + mIsWithViewDate + ",withTime:" + mIsWithViewTime + ",autoUpdateTitle:" + mIsAutoUpdateTitle);
            return pickerDialog;
        }

        public DateTimePickerDialog show() {
            DateTimePickerDialog pickerDialog = create();
            pickerDialog.getDialog().show();
            return pickerDialog;
        }

        private void initDateModeBtn(View contentView, final GregorianLunarCalendarView dateView) {
            Button button = contentView.findViewById(R.id.btn_date_mode);
            button.setVisibility(View.VISIBLE);
            if (dateView.isGregorian()) {
                button.setText(R.string.gregorian);
            } else {
                button.setText(R.string.lunar);
            }
            button.setOnClickListener(v -> {
                if (dateView.isGregorian()) {
                    dateView.toLunarMode();
                    ((Button) v).setText(R.string.lunar);
                    ViewGroup.LayoutParams params = dateView.getLayoutParams();
                    params.width = mContext.getResources().getDimensionPixelOffset(R.dimen.dialog_date_picker_width2);
                    dateView.setLayoutParams(params);
                } else {
                    dateView.toGregorianMode();
                    ((Button) v).setText(R.string.gregorian);
                    ViewGroup.LayoutParams params = dateView.getLayoutParams();
                    params.width = mContext.getResources().getDimensionPixelOffset(R.dimen.dialog_date_picker_width);
                    dateView.setLayoutParams(params);
                }
            });
        }

        public Builder setCanceledOnTouchOutside(boolean enable) {
            mCanceledOnTouchOutside = enable;
            return this;
        }

        public Builder setCancelable(boolean enable) {
            mBuilder.setCancelable(enable);
            return this;
        }

        public Builder setPositiveButton(int textResId, DialogInterface.OnClickListener listener) {
            mBuilder.setPositiveButton(textResId, listener);
            return this;
        }

        public Builder setNegativeButton(int textResId, DialogInterface.OnClickListener listener) {
            mBuilder.setNegativeButton(textResId, listener);
            return this;
        }

        public Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
            mBuilder.setPositiveButton(text, listener);
            return this;
        }

        public Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
            mBuilder.setNegativeButton(text, listener);
            return this;
        }

        public Builder setGravity(int gravity) {
            mGravity = gravity;
            return this;
        }
    }

}
