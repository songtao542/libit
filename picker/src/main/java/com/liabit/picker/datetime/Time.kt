package com.liabit.picker.datetime

/**
 * Author:         songtao
 * CreateDate:     2020/10/10 17:21
 */
data class Time(
        /**
         * 小时
         */
        val hour: Int,
        /**
         * 分钟
         */
        val minute: Int,
        /**
         * 上午下午
         */
        val apm: Int,
        /**
         * 是否24小时制
         */
        val is24HourFormat: Boolean)