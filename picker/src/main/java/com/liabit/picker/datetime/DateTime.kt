package com.liabit.picker.datetime

import java.util.Calendar


/**
 * Author:         songtao
 * CreateDate:     2020/10/10 17:20
 */
data class DateTimeImpl(override val year: Int,
                        override val month: Int,
                        override val dayOfMonth: Int,
                        override val hourOfDay: Int,
                        override val minute: Int) : DateTime {

    override val date: Date = DateImpl(year, month, dayOfMonth)
    override val time: Time = TimeImpl(hourOfDay, minute)
    override val isAm: Boolean get() = time.isAm
    override val isPm: Boolean get() = time.isPm
    override val calendar: Calendar get() = Calendar.getInstance().apply { set(year, month, dayOfMonth, hourOfDay, minute) }

}


interface DateTime : Date, Time {
    val date: Date
    val time: Time

    companion object {
        internal fun from(date: Date, time: Time): DateTime {
            return DateTimeImpl(date.year, date.month, date.dayOfMonth, time.hourOfDay, time.minute)
        }

        internal fun from(calendar: Calendar): DateTime {
            return DateTimeImpl(calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH],
                    calendar[Calendar.HOUR_OF_DAY],
                    calendar[Calendar.MINUTE]
            )
        }
    }
}
