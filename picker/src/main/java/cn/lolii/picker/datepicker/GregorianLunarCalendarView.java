package cn.lolii.picker.datepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.Locale;

import cn.lolii.picker.NumberPickerView;
import cn.lolii.picker.R;

@SuppressWarnings("unused")
public class GregorianLunarCalendarView extends LinearLayout implements NumberPickerView.OnValueChangeListener {
    private static final String TAG = "GLunarCalendarView";
    private static final String FORMAT_TWO_NUMBER = "%02d";

    private static final int MONTH_START = 1;
    private static final int MONTH_STOP_GREGORIAN = 12;
    private static final int MONTH_SPAN_GREGORIAN = MONTH_STOP_GREGORIAN - MONTH_START + 1;
    private static final int MONTH_STOP_LUNAR_LEAP = 13;

    private static final int DAY_START = 1;
    private static final int DAY_STOP_GREGORIAN = 31;
    private static final int DAY_SPAN_GREGORIAN = DAY_STOP_GREGORIAN - DAY_START + 1;

    private static final int DAY_STOP_LUNAR = 30;
    private static final int DAY_SPAN_LUNAR = DAY_STOP_LUNAR - DAY_START + 1;

    private int mYearStart = 1901;
    private int mYearStop = 2036;
    private int mYearSpan = mYearStop - mYearStart + 1;

    private int mMonthStart = 0;
    private int mDayStart = 1;

    private int mMonthEnd = 11;
    private int mDayEnd = 31;

    private NumberPickerView mYearPickerView;
    private NumberPickerView mMonthPickerView;
    private NumberPickerView mDayPickerView;

    /**
     * display values
     */
    private String[] mDisplayYearsGregorian;
    private String[] mDisplayMonthsGregorian;
    private String[] mDisplayDaysGregorian;
    private String[] mDisplayYearsLunar;
    private String[] mDisplayDaysLunar;

    private String[] mDisplayStartMonthsGregorian;
    private String[] mDisplayStartDaysGregorian;

    private String[] mDisplayEndMonthsGregorian;
    private String[] mDisplayEndDaysGregorian;

    private int mYear = mYearStart;
    private int mMonth = 1;
    private int mDay = 1;
    private boolean mIsGregorian = true;//true is gregorian mode
    private OnDateChangedListener mOnDateChangedListener;

    public GregorianLunarCalendarView(Context context) {
        this(context, null);
    }

    public GregorianLunarCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GregorianLunarCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInternal(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GregorianLunarCalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInternal(context);
    }

    private void initInternal(Context context) {
        View contentView = inflate(context, R.layout.gregorian_lunar_calendar, this);
        mYearPickerView = contentView.findViewById(R.id.picker_year);
        mMonthPickerView = contentView.findViewById(R.id.picker_month);
        mDayPickerView = contentView.findViewById(R.id.picker_day);
        mYearPickerView.setOnValueChangedListener(this);
        mMonthPickerView.setOnValueChangedListener(this);
        mDayPickerView.setOnValueChangedListener(this);
        LunarUtil.initStatic(context);
        init();
    }

    public void setMinValue(int minValue) {
        if (!LunarUtil.isValidYear(minValue)) {
            return;
        }
        if (mYearStart == minValue) {
            return;
        }
        mYearStart = minValue;
        mYearSpan = mYearStop - mYearStart + 1;
    }

    public void setMaxValue(int maxValue) {
        if (!LunarUtil.isValidYear(maxValue)) {
            return;
        }
        if (mYearStop == maxValue) {
            return;
        }
        mYearStop = maxValue;
        mYearSpan = mYearStop - mYearStart + 1;
    }

    public void setMinValue(Calendar minValue) {
        int year = minValue.get(Calendar.YEAR);
        if (!LunarUtil.isValidYear(year)) {
            return;
        }
        if (mYearStart == year) {
            return;
        }
        mYearStart = year;
        mYearSpan = mYearStop - mYearStart + 1;
        mMonthStart = minValue.get(Calendar.MONTH);
        mDayStart = minValue.get(Calendar.DATE);
        mYearPickerView.setWrapSelectorWheel(false);
        mMonthPickerView.setWrapSelectorWheel(false);
        mDayPickerView.setWrapSelectorWheel(false);
    }

    public void setMaxValue(Calendar maxValue) {
        int year = maxValue.get(Calendar.YEAR);
        if (!LunarUtil.isValidYear(year)) {
            return;
        }
        if (mYearStop == year) {
            return;
        }
        mYearStop = year;
        mYearSpan = mYearStop - mYearStart + 1;
        mMonthEnd = maxValue.get(Calendar.MONTH);
        mDayEnd = maxValue.get(Calendar.DATE);
        mYearPickerView.setWrapSelectorWheel(false);
        mMonthPickerView.setWrapSelectorWheel(false);
        mDayPickerView.setWrapSelectorWheel(false);
    }

    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
        if (picker == null) {
            return;
        }
        if (picker == mYearPickerView) {
            Log.d("TTTT", "yyyyyyyyyyyyyyy");
            passiveUpdateMonthAndDay(oldVal, newVal, mIsGregorian);
        } else if (picker == mMonthPickerView) {
            Log.d("TTTT", "mmmmmmmmmmmmmmm");
            int fixYear = mYearPickerView.getValue();
            passiveUpdateDay(fixYear, fixYear, oldVal, newVal, mIsGregorian);
        } else if (picker == mDayPickerView) {
            Log.d("TTTT", "ddddddddddddddd");
            if (mOnDateChangedListener != null) {
                mOnDateChangedListener.onDateChanged(getCalendarData());
            }
        }
    }

    /**
     * 初始化日期数据
     */
    public void init() {
        init(Calendar.getInstance());
    }

    public void init(Calendar calendar) {
        init(calendar, mIsGregorian);
    }

    public void init(Calendar calendar, boolean isGregorian) {
        setConfigs(calendar, isGregorian, false);
    }

    private void setConfigs(Calendar c, boolean isGregorian, boolean anim) {
        mIsGregorian = isGregorian;
        if (c == null) {
            c = Calendar.getInstance();
        }
        if (!checkCalendarAvailable(c, isGregorian)) {
            c = adjustCalendarByLimit(c, mYearStart, mYearStop, isGregorian);
        }
        ChineseCalendar cc;
        if (c instanceof ChineseCalendar) {
            cc = (ChineseCalendar) c;
        } else {
            cc = new ChineseCalendar(c);
        }
        setDisplayValuesForAll(cc, mIsGregorian, anim);
    }

    private boolean checkCalendarAvailable(Calendar cc, boolean isGregorian) {
        if (mYearStart > mYearStop) {
            int temp = mYearStart;
            mYearStart = mYearStop;
            mYearStop = temp;
            mYearSpan = mYearStop - mYearStart + 1;
        }
        @SuppressLint("WrongConstant")
        int year = isGregorian ? cc.get(Calendar.YEAR) : cc.get(ChineseCalendar.CHINESE_YEAR);
        return (year >= mYearStart) && (year <= mYearStop);
    }

    private Calendar adjustCalendarByLimit(Calendar c, int yearStart, int yearStop, boolean isGregorian) {
        int yearSet = c.get(Calendar.YEAR);
        Log.w(TAG, "adjustCalendarByLimit() Calendar year:" + yearSet);
        if (isGregorian) {
            if (yearSet < yearStart) {
                c.set(Calendar.YEAR, yearStart);
                c.set(Calendar.MONTH, MONTH_START);
                c.set(Calendar.DAY_OF_MONTH, DAY_START);
            }
            if (yearSet > yearStop) {
                c.set(Calendar.YEAR, yearStop);
                c.set(Calendar.MONTH, MONTH_STOP_GREGORIAN - 1);
                c.set(Calendar.DAY_OF_MONTH, DAY_STOP_GREGORIAN);
            }
        } else {
            if (Math.abs(yearSet - yearStart) < Math.abs(yearSet - yearStop)) {
                c = new ChineseCalendar(true, yearStart, MONTH_START, DAY_START);
            } else {
                c = new ChineseCalendar(true, yearStop, MONTH_STOP_GREGORIAN, DAY_START);
            }
        }
        return c;
    }

    private void setDisplayValuesForAll(ChineseCalendar cc, boolean isGregorian, boolean anim) {
        initDisplayData(isGregorian);
        initValuesForY(cc, isGregorian, anim);
        initValuesForM(cc, isGregorian, anim);
        initValuesForD(cc, isGregorian, anim);
        if (mOnDateChangedListener != null) {
            mOnDateChangedListener.onDateChanged(getCalendarData(mYear, mMonth, mDay, isGregorian()));
        }
    }

    private void initDisplayData(boolean isGregorian) {
        if (isGregorian) {
            if (needResetDisplayYears(mDisplayYearsGregorian)) {
                mDisplayYearsGregorian = new String[mYearSpan];
                for (int i = 0; i < mYearSpan; i++) {
                    mDisplayYearsGregorian[i] = String.valueOf(mYearStart + i);
                }
            }
            if (mDisplayMonthsGregorian == null) {
                mDisplayMonthsGregorian = new String[MONTH_SPAN_GREGORIAN];
                for (int i = 0; i < MONTH_SPAN_GREGORIAN; i++) {
                    mDisplayMonthsGregorian[i] = String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, MONTH_START + i);
                }
            }
            if (mDisplayDaysGregorian == null) {
                mDisplayDaysGregorian = new String[DAY_SPAN_GREGORIAN];
                for (int i = 0; i < DAY_SPAN_GREGORIAN; i++) {
                    mDisplayDaysGregorian[i] = String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, DAY_START + i);
                }
            }
        } else {
            if (needResetDisplayYears(mDisplayYearsLunar)) {
                mDisplayYearsLunar = new String[mYearSpan];
                for (int i = 0; i < mYearSpan; i++) {
                    mDisplayYearsLunar[i] = LunarUtil.getLunarNameOfYear(i + mYearStart);
                }
            }
            if (mDisplayDaysLunar == null) {
                mDisplayDaysLunar = new String[DAY_SPAN_LUNAR];
                for (int i = 0; i < DAY_SPAN_LUNAR; i++) {
                    mDisplayDaysLunar[i] = LunarUtil.getLunarNameOfDay(i + 1);
                }
            }
        }
    }

    private boolean needResetDisplayYears(String[] displayYears) {
        if (displayYears == null || displayYears.length < 1) {
            return true;
        }
        if (displayYears[0].equals(String.valueOf(mYearStart)) &&
                displayYears[displayYears.length - 1].equals(String.valueOf(mYearStop))) {
            Log.d(TAG, "needResetDisplayYears() return false");
            return false;
        }
        Log.d(TAG, "needResetDisplayYears() mIsGregorian:" + mIsGregorian + " mYearStart:" + mYearStart + "," + displayYears[0]
                + " mYearStop:" + mYearStop + "," + displayYears[displayYears.length - 1]);
        return true;
    }

    @SuppressLint("WrongConstant")
    private void initValuesForY(ChineseCalendar cc, boolean isGregorian, boolean anim) {
        mYearPickerView.setWrapSelectorWheel(false);
        int yearSway;
        if (isGregorian) {
            yearSway = cc.get(Calendar.YEAR);
            setValuesForPickerView(mYearPickerView, yearSway, mYearStart, mYearStop, mDisplayYearsGregorian, false, anim);
        } else {
            yearSway = cc.get(ChineseCalendar.CHINESE_YEAR);
            setValuesForPickerView(mYearPickerView, yearSway, mYearStart, mYearStop, mDisplayYearsLunar, false, anim);
        }
        mYear = yearSway;
    }

    @SuppressLint("WrongConstant")
    private void initValuesForM(ChineseCalendar cc, boolean isGregorian, boolean anim) {
        int monthStop = MONTH_STOP_GREGORIAN;
        int monthSway;
        String[] newDisplayedVales;
        if (isGregorian) {
            monthSway = cc.get(Calendar.MONTH) + 1;
            newDisplayedVales = mDisplayMonthsGregorian;
        } else {
            int monthLeap = LunarUtil.getMonthLeapByYear(cc.get(ChineseCalendar.CHINESE_YEAR));
            if (monthLeap != 0) {
                monthStop = MONTH_STOP_LUNAR_LEAP;
            }
            monthSway = LunarUtil.convertMonthLunarToMonthSway(cc.get(ChineseCalendar.CHINESE_MONTH), monthLeap);
            newDisplayedVales = LunarUtil.getLunarMonthNamesWithLeap(monthLeap);
        }
        mMonth = monthSway;
        setValuesForPickerView(mMonthPickerView, monthSway, MONTH_START, monthStop, newDisplayedVales, false, anim);
    }

    @SuppressLint("WrongConstant")
    private void initValuesForD(ChineseCalendar cc, boolean isGregorian, boolean anim) {
        int dayStop;
        int daySway;
        if (isGregorian) {//公历
            dayStop = LunarUtil.getGregorianDaysInMonthByMonthSway(cc.get(Calendar.YEAR), cc.get(Calendar.MONTH) + 1);
            daySway = cc.get(Calendar.DAY_OF_MONTH);
            setValuesForPickerView(mDayPickerView, daySway, DAY_START, dayStop, mDisplayDaysGregorian, false, anim);
        } else {
            dayStop = ChineseCalendar.daysInChineseMonth(cc.get(ChineseCalendar.CHINESE_YEAR), cc.get(ChineseCalendar.CHINESE_MONTH));
            daySway = cc.get(ChineseCalendar.CHINESE_DATE);
            setValuesForPickerView(mDayPickerView, daySway, DAY_START, dayStop, mDisplayDaysLunar, false, anim);
        }
        mDay = daySway;
    }

    private void setValuesForPickerView(NumberPickerView pickerView, int newSway, int newStart, int newStop,
                                        String[] newDisplayedVales, boolean needRespond, boolean anim) {

        if (newStart > newStop) { //规避一些错误
            Log.w(TAG, "setValuesForPickerView() newStart > newStop");
            return;
        }
        if (newDisplayedVales == null) {
            throw new IllegalArgumentException("newDisplayedVales should not be null.");
        } else if (newDisplayedVales.length == 0) {
            throw new IllegalArgumentException("newDisplayedVales's length should not be 0.");
        }
        int newSpan = newStop - newStart + 1;
        if (newDisplayedVales.length < newSpan) {
            throw new IllegalArgumentException("newDisplayedVales's length should not be less than newSpan.");
        }
        int oldStart = pickerView.getMinValue();
        int oldStop = pickerView.getMaxValue();
        int oldSpan = oldStop - oldStart + 1;
        int fromValue = pickerView.getValue();
        Log.d("TTTT", "newSway=" + newSway + "  newStart=" + newStart + "  newStop=" + newStop + "  newSpan=" + newSpan
                + "  oldSpan:" + oldSpan
        );
        if (newSpan > oldSpan) {
            pickerView.setDisplayedValues(newDisplayedVales);
            pickerView.setMinValue(newStart);
            pickerView.setMaxValue(newStop);
        } else {
            pickerView.setMinValue(newStart);
            pickerView.setMaxValue(newStop);
            pickerView.setDisplayedValues(newDisplayedVales);
        }
        if (anim) {
            if (fromValue < newStart) {
                fromValue = newStart;
            }
            pickerView.smoothScrollToValue(fromValue, newSway, needRespond);
        } else {
            pickerView.setValue(newSway);
        }
    }

    private String[] getStartMonthsGregorian() {
        if (mMonthStart != 0 ||
                mDisplayStartMonthsGregorian == null ||
                mDisplayStartMonthsGregorian.length != (MONTH_STOP_GREGORIAN - mMonthStart)) {
            if (mDisplayStartMonthsGregorian == null) {
                Log.d("TTTT", "-------------------mMonthStart--------------------" + mMonthStart);
                for (int k = 0; k < mDisplayMonthsGregorian.length; k++) {
                    Log.d("TTTT", "mmm: " + k + "  " + mDisplayMonthsGregorian[k]);
                }
                mDisplayStartMonthsGregorian = new String[MONTH_STOP_GREGORIAN - mMonthStart];
                for (int i = mMonthStart, j = 0; i < MONTH_STOP_GREGORIAN; i++, j++) {
                    mDisplayStartMonthsGregorian[j] = mDisplayMonthsGregorian[i];
                    Log.d("TTTT", "vvvvvvvv: " + i + "->" + j + "  " + mDisplayMonthsGregorian[i]);
                }
            }
            Log.d("TTTT", "---------------------------------------");
            return mDisplayStartMonthsGregorian;
        } else {
            return mDisplayMonthsGregorian;
        }
    }

    private String[] getStartDaysGregorian() {
        if (mDayStart != 1 ||
                mDisplayStartDaysGregorian == null ||
                mDisplayStartDaysGregorian.length != (DAY_STOP_GREGORIAN - mDayStart + 1)) {
            if (mDisplayStartDaysGregorian == null) {
                for (int k = 0; k < mDisplayDaysGregorian.length; k++) {
                    Log.d("TTTT", "ddd: " + k + "  " + mDisplayDaysGregorian[k]);
                }
                Log.d("TTTT", "--------------------mDayStart-------------------" + mDayStart);
                mDisplayStartDaysGregorian = new String[DAY_STOP_GREGORIAN - mDayStart + 1];
                for (int i = mDayStart - 1, j = 0; i < DAY_STOP_GREGORIAN; i++, j++) {
                    mDisplayStartDaysGregorian[j] = mDisplayDaysGregorian[i];
                    Log.d("TTTT", "vvvvvvvv: " + i + "->" + j + "  " + mDisplayDaysGregorian[i]);
                }
            }
            Log.d("TTTT", "---------------------------------------");
            return mDisplayStartDaysGregorian;
        } else {
            return mDisplayDaysGregorian;
        }
    }

    private String[] getEndMonthsGregorian() {
        if (mMonthEnd != 11 ||
                mDisplayEndMonthsGregorian == null ||
                mDisplayEndMonthsGregorian.length != (mMonthEnd + 1)) {
            if (mDisplayEndMonthsGregorian == null) {
                mDisplayEndMonthsGregorian = new String[mMonthEnd + 1];
                for (int i = 0, j = 0; i < mMonthEnd + 1; i++, j++) {
                    mDisplayEndMonthsGregorian[j] = mDisplayMonthsGregorian[i];
                }
            }
            return mDisplayEndMonthsGregorian;
        } else {
            return mDisplayMonthsGregorian;
        }
    }

    private String[] getEndDaysGregorian() {
        if (mDayEnd != 31 ||
                mDisplayEndDaysGregorian == null ||
                mDisplayEndDaysGregorian.length != mDayEnd) {
            if (mDisplayEndDaysGregorian == null) {
                mDisplayEndDaysGregorian = new String[mDayEnd];
                for (int i = 0, j = 0; i < mDayEnd; i++, j++) {
                    mDisplayEndDaysGregorian[j] = mDisplayDaysGregorian[i];
                }
            }
            return mDisplayEndDaysGregorian;
        } else {
            return mDisplayDaysGregorian;
        }
    }

    private void passiveUpdateMonthAndDay(int oldYear, int newYear, boolean isGregorian) {
        int oldMonthSway = mMonthPickerView.getValue();
        int oldDaySway = mDayPickerView.getValue();
        Log.d("TTTT", "oldMonthSway===>" + oldMonthSway
                + "  oldDaySway=" + oldDaySway
                + "  mMonthStart=" + mMonthStart
        );
        if (isGregorian) {
            int newDaySway = oldDaySway;
            /*if (oldMonthSway == 2  ) {    //公历只有2月变化
                int newDayStop = LunarUtil.getDaysInMonthByMonthSway(newYear, oldMonthSway, true);
                newDaySway = Math.min(oldDaySway, newDayStop);
                setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, newDayStop, mDisplayDaysGregorian, true, true);
            }*/
            int newMonthSway = oldMonthSway;
            boolean shouldUpdateDays = false;
            if (newYear == mYearStart) {
                if (oldMonthSway < mMonthStart + 1) {
                    newMonthSway = mMonthStart + 1;
                }
                setValuesForPickerView(mMonthPickerView, newMonthSway, mMonthStart + 1, MONTH_STOP_GREGORIAN, getStartMonthsGregorian(), true, true);
                if (oldMonthSway == mMonthStart + 1) {
                    if (oldDaySway < mDayStart) {
                        newDaySway = mDayStart;
                    }
                    int newDayStop = LunarUtil.getDaysInMonthByMonthSway(newYear, newMonthSway, true);
                    setValuesForPickerView(mDayPickerView, newDaySway, mDayStart, newDayStop, getStartDaysGregorian(), false, true);
                } else {
                    shouldUpdateDays = true;
                }
            } else if (newYear == mYearStop) {
                if (oldMonthSway > mMonthEnd + 1) {
                    newMonthSway = mMonthEnd + 1;
                }
                setValuesForPickerView(mMonthPickerView, newMonthSway, MONTH_START, mMonthEnd + 1, getEndMonthsGregorian(), true, true);
                if (oldMonthSway == mMonthEnd + 1) {
                    if (oldDaySway > mDayEnd) {
                        newDaySway = mDayEnd;
                    }
                    setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, mDayEnd, getEndDaysGregorian(), false, true);
                } else {
                    shouldUpdateDays = true;
                }
            } else {
                setValuesForPickerView(mMonthPickerView, newMonthSway, MONTH_START, MONTH_STOP_GREGORIAN, mDisplayMonthsGregorian, true, true);
                shouldUpdateDays = true;
            }
            if (shouldUpdateDays) {
                int newDayStop = LunarUtil.getDaysInMonthByMonthSway(newYear, oldMonthSway, true);
                newDaySway = Math.min(oldDaySway, newDayStop);
                setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, newDayStop, mDisplayDaysGregorian, false, true);
            }

            if (mOnDateChangedListener != null) {
                mOnDateChangedListener.onDateChanged(getCalendarData(newYear, newMonthSway, newDaySway, true));
            }
        } else {
            int newYearMonthOfLeap = LunarUtil.getMonthLeapByYear(newYear);//1.计算当前year是否有闰月
            int oldYearMonthOfLeap = LunarUtil.getMonthLeapByYear(oldYear);//2.计算之前year是否有闰月

            int newMonthSway = oldMonthSway;
            if (newYearMonthOfLeap != oldYearMonthOfLeap) {
                //闰月不同，更新newMonthSway，月视图需要更新
                String[] currDisplayMonthsLunar = LunarUtil.getLunarMonthNamesWithLeap(newYearMonthOfLeap);
                //优化方案
                int oldMonthLunar = LunarUtil.convertMonthSwayToMonthLunar(oldMonthSway, oldYearMonthOfLeap);
                int oldMonthLunarAbs = Math.abs(oldMonthLunar);
                newMonthSway = LunarUtil.convertMonthLunarToMonthSway(oldMonthLunarAbs, newYearMonthOfLeap);
                setValuesForPickerView(mMonthPickerView, newMonthSway, MONTH_START,
                        newYearMonthOfLeap == 0 ? MONTH_STOP_GREGORIAN : MONTH_STOP_LUNAR_LEAP, currDisplayMonthsLunar, false, true);
            }
            //处理日视图
            passiveUpdateDay(oldYear, newYear, oldMonthSway, newMonthSway, false);
        }
    }

    private void passiveUpdateDay(int oldYear, int newYear, int oldMonthSway, int newMonthSway, boolean isGregorian) {
        int oldDayStop = LunarUtil.getDaysInMonthByMonthSway(oldYear, oldMonthSway, isGregorian);
        int newDayStop = LunarUtil.getDaysInMonthByMonthSway(newYear, newMonthSway, isGregorian);
        if (newDayStop == -1) {
            return;
        }
        int oldDaySway = mDayPickerView.getValue();
        int newDaySway = oldDaySway;

        if (newYear == mYearStart && newMonthSway == mMonthStart + 1) {
            if (oldDaySway < mDayStart) {
                newDaySway = mDayStart;
            }
            newDayStop = LunarUtil.getDaysInMonthByMonthSway(newYear, newMonthSway, true);
            setValuesForPickerView(mDayPickerView, newDaySway, mDayStart, newDayStop, getStartDaysGregorian(), false, true);
        } else if (newYear == mYearStop && newMonthSway == mMonthEnd + 1) {
            if (oldDaySway > mDayEnd) {
                newDaySway = mDayEnd;
            }
            setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, mDayEnd, getEndDaysGregorian(), false, true);
        } else if (oldDayStop != newDayStop || newYear == mYearStart || newYear == mYearStop) {
            newDaySway = Math.min(oldDaySway, newDayStop);
            setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, newDayStop, isGregorian ? mDisplayDaysGregorian : mDisplayDaysLunar, true, true);
        }
        if (mOnDateChangedListener != null) {
            mOnDateChangedListener.onDateChanged(getCalendarData(newYear, newMonthSway, newDaySway, isGregorian));
        }
    }

    public void setGregorian(boolean isGregorian, boolean anim) {
        if (mIsGregorian == isGregorian) {
            return;
        }
        ChineseCalendar cc = getCalendarData().getCalendar();//根据mIsGregorian收集数据

        mIsGregorian = isGregorian;
        setConfigs(cc, isGregorian, anim);//重新更新界面数据
    }

    public boolean toGregorianMode() {
        /*if(isScrollAnim()){
            return false;
        }*/
        setGregorian(true, false);
        return true;
    }

    public boolean toLunarMode() {
        /*if(isScrollAnim()) {
            return false;
        }*/
        setGregorian(false, false);
        return true;
    }

    public void setTextColor(int selectedColor, int normalColor) {
        setSelectedColor(selectedColor);
        setNormalColor(normalColor);
    }

    public void setSelectedColor(int selectedColor) {
        mYearPickerView.setSelectedTextColor(selectedColor);
        mYearPickerView.setHintTextColor(selectedColor);
        mMonthPickerView.setSelectedTextColor(selectedColor);
        mMonthPickerView.setHintTextColor(selectedColor);
        mDayPickerView.setSelectedTextColor(selectedColor);
        mDayPickerView.setHintTextColor(selectedColor);
    }

    public void setNormalColor(int normalColor) {
        mYearPickerView.setNormalTextColor(normalColor);
        mMonthPickerView.setNormalTextColor(normalColor);
        mDayPickerView.setNormalTextColor(normalColor);
    }

    public void setDividerColor(int dividerColor) {
        mYearPickerView.setDividerColor(dividerColor);
        mMonthPickerView.setDividerColor(dividerColor);
        mDayPickerView.setDividerColor(dividerColor);
    }

    public View getNumberPickerYear() {
        return mYearPickerView;
    }

    public View getNumberPickerMonth() {
        return mMonthPickerView;
    }

    public View getNumberPickerDay() {
        return mDayPickerView;
    }

    public void setNumberPickerYearVisibility(int visibility) {
        setNumberPickerVisibility(mYearPickerView, visibility);
    }

    public void setNumberPickerMonthVisibility(int visibility) {
        setNumberPickerVisibility(mMonthPickerView, visibility);
    }

    public void setNumberPickerDayVisibility(int visibility) {
        setNumberPickerVisibility(mDayPickerView, visibility);
    }

    public void setNumberPickerVisibility(NumberPickerView view, int visibility) {
        if (view.getVisibility() == visibility) {
            return;
        }
        if (visibility == View.GONE || visibility == View.VISIBLE || visibility == View.INVISIBLE) {
            view.setVisibility(visibility);
        }
    }

    public boolean isGregorian() {
        return mIsGregorian;
    }

    private CalendarData getCalendarData(int pickedYear, int pickedMonthSway, int pickedDay, boolean mIsGregorian) {
        return new CalendarData(pickedYear, pickedMonthSway, pickedDay, mIsGregorian);
    }

    public CalendarData getCalendarData() {
        int pickedYear = mYearPickerView.getValue();
        int pickedMonthSway = mMonthPickerView.getValue();
        int pickedDay = mDayPickerView.getValue();
        return new CalendarData(pickedYear, pickedMonthSway, pickedDay, mIsGregorian);
    }

    public static class CalendarData {
        public boolean isGregorian;
        public int pickedYear;
        public int pickedMonthSway;
        public int pickedDay;
        /**
         * 获取数据示例与说明：
         * Gregorian : //公历
         * chineseCalendar.get(Calendar.YEAR)              //获取公历年份，范围[1900 ~ 2100]
         * chineseCalendar.get(Calendar.MONTH) + 1         //获取公历月份，范围[1 ~ 12]
         * chineseCalendar.get(Calendar.DAY_OF_MONTH)      //返回公历日，范围[1 ~ 30]
         * <p>
         * Lunar
         * chineseCalendar.get(ChineseCalendar.CHINESE_YEAR)   //返回农历年份，范围[1900 ~ 2100]
         * chineseCalendar.get(ChineseCalendar.CHINESE_MONTH)) //返回农历月份，范围[(-12) ~ (-1)] || [1 ~ 12]
         * //当有月份为闰月时，返回对应负值
         * //当月份非闰月时，返回对应的月份值
         * calendar.get(ChineseCalendar.CHINESE_DATE)         //返回农历日，范围[1 ~ 30]
         */
        private ChineseCalendar chineseCalendar;

        /**
         * model类的构造方法
         *
         * @param pickedYear      年
         * @param pickedMonthSway 月，公历农历均从1开始。农历如果有闰年，按照实际的顺序添加
         * @param pickedDay       日，从1开始，日期在月份中的显示数值
         * @param isGregorian     是否为公历
         */
        public CalendarData(int pickedYear, int pickedMonthSway, int pickedDay, boolean isGregorian) {
            this.pickedYear = pickedYear;
            this.pickedMonthSway = pickedMonthSway;
            this.pickedDay = pickedDay;
            this.isGregorian = isGregorian;
            initChineseCalendar();
        }

        /**
         * 初始化成员变量chineseCalendar，用来记录当前选中的时间。此成员变量同时存储了农历和公历的信息
         */
        private void initChineseCalendar() {
            if (isGregorian) {
                chineseCalendar = new ChineseCalendar(pickedYear, pickedMonthSway - 1, pickedDay);//公历日期构造方法
            } else {
                int y = pickedYear;
                int m = LunarUtil.convertMonthSwayToMonthLunarByYear(pickedMonthSway, pickedYear);
                int d = pickedDay;
                chineseCalendar = new ChineseCalendar(true, y, m, d);
            }
        }

        public ChineseCalendar getCalendar() {
            return chineseCalendar;
        }
    }

    public interface OnDateChangedListener {
        void onDateChanged(CalendarData calendarData);
    }

    public void setOnDateChangedListener(OnDateChangedListener listener) {
        mOnDateChangedListener = listener;
    }

    public void dateChangedCallBack() {
        if (mOnDateChangedListener != null) {
            mOnDateChangedListener.onDateChanged(getCalendarData(mYear, mMonth, mDay, mIsGregorian));
        }
    }

}