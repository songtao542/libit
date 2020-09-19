package com.lolii.filter;

import android.content.Context
import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/9/18 18:31
 */
object Picker {

    fun pickDate(context: Context, listener: OnDateSelectListener) {

    }

    interface OnDateSelectListener {
        fun onDateSelect(date: Date)
    }

}
