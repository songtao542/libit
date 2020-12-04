package com.liabit.picker.datetime

import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/10/10 17:20
 */
data class DateImpl(override val year: Int, override val month: Int, override val dayOfMonth: Int) : Date {
    override val calendar: Calendar get() = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
}

interface Date {
    val year: Int
    val month: Int
    val dayOfMonth: Int
    val calendar: Calendar

    companion object {
        internal fun from(calendar: Calendar): Date {
            return DateImpl(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        }
    }
}

