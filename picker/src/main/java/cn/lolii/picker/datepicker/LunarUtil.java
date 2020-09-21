package cn.lolii.picker.datepicker;

import android.content.Context;

import java.util.Calendar;
import java.util.GregorianCalendar;

import cn.lolii.picker.R;

public class LunarUtil {

    private static final boolean IS_CONVERT_YEAR = false;
    private static final String NON_VALUE = "NON";

    //数字对应的汉字
    private static final String[] NUMBER_OF_CHINESE = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    // 农历月、日的显示字符，公历直接用数字显示
    private static String[] LUNAR_MONTH_ENTRIES = {"正月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "冬月", "腊月"};
    private static String[] LUNAR_DAY_ENTRIES = {"初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
            "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "廿十",
            "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"};
    //星期显示字符
    private static String[] WEEK_ENTRIES = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    private static String LEAP_PRE = "闰";

    public static void initStatic(Context context) {
        LUNAR_MONTH_ENTRIES = context.getResources().getStringArray(R.array.lunar_month_entries);
        LUNAR_DAY_ENTRIES = context.getResources().getStringArray(R.array.lunar_day_entries);
        WEEK_ENTRIES = context.getResources().getStringArray(R.array.week_entries);
        LEAP_PRE = context.getResources().getString(R.string.leap);
    }

    public static boolean isValidYear(int year) {
        return year >= 1901 && year <= 2100;
    }

    public static int getMonthLeapByYear(int year) {
        return ChineseCalendar.getMonthLeapByYear(year);
    }

    /**
     * 根据year的阿拉伯数字生成显示字符
     * @param year 整型年份，如：1970
     * @return 转换后的字符
     */
    public static String getLunarNameOfYear(int year) {
        if (!IS_CONVERT_YEAR) {
            return String.valueOf(year);
        }
        StringBuilder sb = new StringBuilder();
        int digital;
        while (year > 0) {
            digital = year % 10;
            sb.insert(0, NUMBER_OF_CHINESE[digital]);
            year = year / 10;
        }
        return sb.toString();
    }

    /**
     * 获取农历月份的显示字符
     * @param month 月的范围[-12,1] or [1, 12]
     * @return 显示字符, 如：一月
     */
    public static String getLunarNameOfMonth(int month) {
        if (month > 0 && month < 13) {
            return LUNAR_MONTH_ENTRIES[month - 1];
        } else if (month < 0 && month > -13) {
            return LEAP_PRE + LUNAR_MONTH_ENTRIES[-month - 1];
        } else {
            return NON_VALUE;
        }
    }

    /**
     * 获取农历天的显示字符
     * @param day 农历天的范围 [1, 30]
     * @return 天的文字描述，如：初一
     */
    public static String getLunarNameOfDay(int day) {
        if (day > 0 && day < 31) {
            return LUNAR_DAY_ENTRIES[day - 1];
        } else {
            return NON_VALUE;
        }
    }
    /**
     * 获取星期几的显示字符
     * @param dayOfWeek 范围 [1, 7]
     * */
    public static String getNameDayOfWeek(int dayOfWeek) {
        if (dayOfWeek > 7 || dayOfWeek < 1) {
            return NON_VALUE;
        }
        return WEEK_ENTRIES[dayOfWeek - 1];
    }

    /**
     * 获取农历月份的显示字符数组，可包含闰月
     * @param monthLeap 范围 [-12, 0], 0表示无闰月
     */
    public static String[] getLunarMonthNamesWithLeap(int monthLeap) {
        if (monthLeap == 0) {
            return LUNAR_MONTH_ENTRIES;
        }
        if (monthLeap < -12 || monthLeap > 0) {
            throw new IllegalArgumentException("month should be in range of [-12, 0]");
        }
        int monthLeapAbs = -monthLeap;
        String[] monthsOut = new String[13];

        System.arraycopy(LUNAR_MONTH_ENTRIES, 0, monthsOut, 0, monthLeapAbs);
        monthsOut[monthLeapAbs] = getLunarNameOfMonth(monthLeap);
        System.arraycopy(LUNAR_MONTH_ENTRIES, monthLeapAbs, monthsOut, monthLeapAbs + 1, LUNAR_MONTH_ENTRIES.length - monthLeapAbs);
        return monthsOut;
    }

    /**
     * 通过月份的偏移值，获取当月对应的天数
     * @param year        年份
     * @param monthSway   索引从1开始
     * @param isGregorian 是否是公历
     * @return 月份包含的天数
     */
    public static int getDaysInMonthByMonthSway(int year, int monthSway, boolean isGregorian) {
        if (isGregorian) {
            return getGregorianDaysInMonthByMonthSway(year, monthSway);
        } else {
            return getLunarDaysInMonthByMonthSway(year, monthSway);
        }
    }

    /**
     * 获取公历year年month月的天数
     * @param year  年
     * @param month 月，从1开始计数
     * @return 月份包含的天数
     */
    public static int getGregorianDaysInMonthByMonthSway(int year, int month) {
        return new GregorianCalendar(year, month, 0).get(Calendar.DATE);
    }

    /**
     * 获取农历year年monthSway月的天数
     * @param year 年
     * @param monthSway 月，包含闰月，如闰五月，monthSway为1代表1月，5代表五月，6代表闰五月
     */
    public static int getLunarDaysInMonthByMonthSway(int year, int monthSway) {
        int monthLeap = ChineseCalendar.getMonthLeapByYear(year);
        int monthLunar = convertMonthSwayToMonthLunar(monthSway, monthLeap);
        return ChineseCalendar.daysInChineseMonth(year, monthLunar);
    }

    /**
     * 根据农历月份，获取月份的偏移值
     * @param monthLunar 小于0为闰月。取值范围是[-12,-1] + [1,12]
     * @param monthLeap  已知的闰月。取值范围是[-12,-1] + 0代表无闰月
     * @return  月份位置偏移[1,13]
     */
    public static int convertMonthLunarToMonthSway(int monthLunar, int monthLeap) {
        if (monthLeap > 0) {
            throw new IllegalArgumentException("convertChineseMonthToMonthSway monthLeap should be in range of [-12, 0]");
        }
        if (monthLeap == 0) {
            return monthLunar;
        }
        if (monthLunar == monthLeap) {
            return -monthLunar + 1;
        } else if (monthLunar < -monthLeap + 1) {
            return monthLunar;
        } else {
            return monthLunar + 1;
        }
    }

    /**
     * 根据农历月份的偏移值和闰月值，获取月份的值，负值为闰月
     *
     * @param monthSway 在NumberPicker中的value，取值范围[1,12] + 13
     * @param monthLeap 已知的闰月。取值范围是[-12,-1] + 0
     *                  0代表无闰月
     * @return 返回ChineseCalendar中需要的month，如果是闰月，传入负值
     * 返回值的范围是[-12,-1] + [1,12]
     */
    public static int convertMonthSwayToMonthLunar(int monthSway, int monthLeap) {
        if (monthLeap > 0) {
            throw new IllegalArgumentException("convertChineseMonthToMonthSway monthLeap should be in range of [-12, 0]");
        }
        if (monthLeap == 0) {
            return monthSway;
        }
        if (monthSway == -monthLeap + 1) {  //闰月
            return monthLeap;
        } else if (monthSway < -monthLeap + 1) {
            return monthSway;
        } else {
            return monthSway - 1;
        }
    }

    /**
     * 根据农历年份和月份偏移值，获取月份的值。负值为闰月
     * @param monthSway 农历月份view的游标值
     * @param year      农历年份
     * @return
     */
    public static int convertMonthSwayToMonthLunarByYear(int monthSway, int year) {
        int monthLeap = getMonthLeapByYear(year);
        return convertMonthSwayToMonthLunar(monthSway, monthLeap);
    }

}