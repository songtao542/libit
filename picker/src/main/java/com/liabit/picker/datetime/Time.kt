package com.liabit.picker.datetime

import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/10/10 17:21
 */
@Suppress("MemberVisibilityCanBePrivate")
data class Time(
        /**
         * 24小时制(0,23)
         */
        val hour: Int,
        /**
         * 分钟
         */
        val minute: Int
) {

    val calendar: Calendar
        get() = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

    /**
     * 是否是24小时制
     */
    internal var is24HourFormat: Boolean = false

    /**
     * 是否是上午
     */
    val isAm: Boolean
        get() {
            return hour < 12
        }

    /**
     * 是否是下午
     */
    val isPm: Boolean = !isAm

    /**
     * [Calendar.AM] or [Calendar.PM]
     */
    internal val apm: Int
        get() {
            return if (isAm) Calendar.AM else Calendar.PM
        }
}