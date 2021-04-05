package com.irun.runker.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    private val formatter = SimpleDateFormat("yyyyMMdd", Locale.CHINA)

    /**
     * 获取现在时间
     * @return 返回短时间字符串格式 yyyyMMdd
     */
    fun yyyyMMdd(time: Long): String {
        return formatter.format(Date(time))
    }
}