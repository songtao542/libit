package com.liabit.net.util

import android.util.Log
import com.liabit.net.BuildConfig
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object MD5 {
    @JvmStatic
    fun md5(string: String): String {
        return string.md5()
    }
}

fun String.md5(): String {
    if (BuildConfig.DEBUG) {
        Log.d("MD5", "to sign string: $this")
    }
    try {
        //获取md5加密对象
        val md5: MessageDigest = MessageDigest.getInstance("MD5")
        //对字符串加密，返回字节数组
        val digest: ByteArray = md5.digest(toByteArray())
        val sb = StringBuilder()
        for (b in digest) {
            //获取低八位有效值
            val i: Int = b.toInt() and 0xff
            //将整数转化为16进制
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                //如果是一位的话，补0
                hexString = "0$hexString"
            }
            sb.append(hexString)
        }
        val sign = sb.toString()
        if (BuildConfig.DEBUG) {
            Log.d("MD5", "signed string : $sign")
        }
        return sign
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}