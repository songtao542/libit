package com.liabit.picker.datetime

import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/10/10 17:21
 */
@Suppress("MemberVisibilityCanBePrivate")
data class TimeImpl(
        /** 24小时制(0,23) */
        override val hourOfDay: Int,
        /** 分钟 */
        override val minute: Int) : Time {

    override val calendar: Calendar
        get() = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }

    /**
     * 是否是上午
     */
    override val isAm: Boolean
        get() {
            return hourOfDay < 12
        }

    /**
     * 是否是下午
     */
    override val isPm: Boolean = !isAm
}

interface Time {
    /** 24小时制(0,23) */
    val hourOfDay: Int

    /** 分钟 */
    val minute: Int

    /**
     * 是否是上午
     */
    val isAm: Boolean

    /**
     * 是否是下午
     */
    val isPm: Boolean

    val calendar: Calendar

    companion object {
        internal fun from(calendar: Calendar): Time {
            return TimeImpl(calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE])
        }
    }
}