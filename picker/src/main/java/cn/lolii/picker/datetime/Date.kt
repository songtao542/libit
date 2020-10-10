package cn.lolii.picker.datetime

import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/10/10 17:20
 */
data class Date(val year: Int, val month: Int, val day: Int) {
    val calendar: Calendar = Calendar.getInstance().apply { set(year, month, day) }
}