package com.scaffold.base

import android.content.Context
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.scaffold.util.Preference
import java.io.*
import java.net.NetworkInterface
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import kotlin.collections.ArrayList

object DeviceId {
    private const val TAG = "DeviceId"

    /**
     * Settings key
     */
    private const val DEVICE_ID_NAME = "dvcid"

    /**
     * Preference key
     */
    private const val DEVICE_ID_KEY = "deviceId"

    //保存文件的路径
    private const val CACHE_IMAGE_DIR = ".cache"

    //保存的文件 采用隐藏文件的形式进行保存
    private const val DEVICES_FILE_NAME = ".device"

    private var mDeviceId: String? = null

    /**
     * 获取设备唯一标识符
     *
     * @param context
     * @return
     */
    fun getDeviceId(context: Context): String {
        var deviceId = mDeviceId
        if (!deviceId.isNullOrEmpty()) {
            return deviceId
        }
        //读取保存的在sd卡中的唯一标识符
        deviceId = readDeviceID(context)
        Log.d(TAG, "getDeviceId 1-> readDeviceID=$deviceId")
        //判断是否已经生成过,
        if (!deviceId.isNullOrEmpty()) {
            mDeviceId = deviceId
            return deviceId
        }

        //用于生成最终的唯一标识符
        val s = StringBuilder()
        try {
            //获取设备的MACAddress地址 去掉中间相隔的冒号
            deviceId = getMacAddress().replace(":", "")
            s.append(deviceId)
            Log.d(TAG, "getDeviceId 2-> getMacAddress=$deviceId")
        } catch (e: Throwable) {
            Log.d(TAG, "getLocalMacAddress error: ", e)
        }

        //如果以上搜没有获取相应的则自己生成相应的UUID作为相应设备唯一标识符
        if (s.isEmpty()) {
            val uuid = UUID.randomUUID()
            deviceId = uuid.toString().replace("-", "")
            s.append(deviceId)
            Log.d(TAG, "getDeviceId 3-> randomUUID=$deviceId")
        }
        //为了统一格式对设备的唯一标识进行md5加密 最终生成32位字符串
        val md5 = getMD5(s.toString(), false)
        if (s.isNotEmpty()) {
            //持久化操作, 进行保存到SD卡中
            saveDeviceID(context, md5)
        }
        mDeviceId = md5
        return md5
    }

    /**
     * 读取固定的文件中的内容,这里就是读取sd卡中保存的设备唯一标识符
     *
     * @param context
     * @return
     */
    private fun readDeviceID(context: Context): String? {
        val deviceId = Preference.getStringOrNull(DEVICE_ID_KEY)
        if (!TextUtils.isEmpty(deviceId)) {
            // SharedPreferences读，清应用数据时获取失败，则从文件读
            return deviceId
        }
        val files = getSaveFiles(context)
        val builder = StringBuilder()
        for (file in files) {
            try {
                builder.clear()
                BufferedReader(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)).use { stream ->
                    val bytes = CharArray(64)
                    var count: Int
                    while (stream.read(bytes).also { count = it } > -1) {
                        if (count > 0) {
                            builder.append(bytes)
                        }
                    }
                }
                if (builder.length > 5) {
                    return builder.toString()
                }
            } catch (e: Throwable) {
                Log.d(TAG, "read device id fail: ", e)
            }
        }
        return null
    }

    /**
     * 获取设备MAC 地址 由于 6.0 以后 WifiManager 得到的 MacAddress得到都是 相同的没有意义的内容
     * 所以采用以下方法获取Mac地址
     *
     * @return
     */
    private fun getMacAddress(): String {
        val macAddress: String
        val buf = StringBuilder()
        var networkInterface: NetworkInterface?
        try {
            networkInterface = NetworkInterface.getByName("eth1")
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0")
            }
            if (networkInterface == null) {
                return ""
            }
            val hardwareAddress = networkInterface.hardwareAddress
            for (b in hardwareAddress) {
                buf.append(String.format("%02X:", b))
            }
            if (buf.isNotEmpty()) {
                buf.deleteCharAt(buf.length - 1)
            }
            macAddress = buf.toString()
        } catch (e: Throwable) {
            Log.d(TAG, "get network interface error: ", e)
            return ""
        }
        return macAddress
    }

    /**
     * 保存 内容到 SD卡中,  这里保存的就是 设备唯一标识符
     *
     * @param context
     * @param deviceId
     */
    private fun saveDeviceID(context: Context, deviceId: String) {
        if (TextUtils.isEmpty(deviceId)) return
        Log.d(TAG, "saveDeviceID : $deviceId")

        // 保存到SharedPreferences和文件
        Preference.putString(DEVICE_ID_KEY, deviceId)
        try {
            Settings.Global.putString(context.contentResolver, DEVICE_ID_NAME, deviceId)
        } catch (e: Throwable) {
            Log.d(TAG, "save device to settings error: ", e)
        }
        val files = getSaveFiles(context)
        for (file in files) {
            try {
                if (!file.exists()) {
                    file.createNewFile()
                }
                Log.d(TAG, "save device id to: $file")
                OutputStreamWriter(FileOutputStream(file), StandardCharsets.UTF_8).use {
                    it.write(deviceId)
                }
            } catch (e: Throwable) {
                Log.d(TAG, "save device id error: ", e)
            }
        }
    }

    /**
     * 对挺特定的 内容进行 md5 加密
     *
     * @param message   加密明文
     * @param upperCase 加密以后的字符串是是大写还是小写 true 大写 false 小写
     * @return
     */
    @Suppress("SameParameterValue")
    private fun getMD5(message: String, upperCase: Boolean): String {
        var md5str = ""
        try {
            val md = MessageDigest.getInstance("MD5")
            val input = message.toByteArray()
            val buff = md.digest(input)
            md5str = bytesToHexString(buff, upperCase)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return md5str
    }

    private fun bytesToHexString(bytes: ByteArray, upperCase: Boolean): String {
        val md5str = StringBuilder()
        var digital: Int
        for (i in bytes.indices) {
            digital = bytes[i].toInt()
            if (digital < 0) {
                digital += 256
            }
            if (digital < 16) {
                md5str.append("0")
            }
            md5str.append(Integer.toHexString(digital))
        }
        return if (upperCase) {
            md5str.toString().toUpperCase(Locale.getDefault())
        } else {
            md5str.toString().toLowerCase(Locale.getDefault())
        }
    }

    /**
     * 统一处理设备唯一标识 保存的文件的地址
     *
     * @param context
     * @return
     */
    private fun getSaveFile(context: Context, type: String): File {
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val cacheDir = File(Environment.getExternalStoragePublicDirectory(type), CACHE_IMAGE_DIR)
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            File(cacheDir, DEVICES_FILE_NAME).also {
                Log.d(TAG, "pub deviceId file: $it")
            }
        } else {
            val cacheDir = File(context.getExternalFilesDir(type), CACHE_IMAGE_DIR)
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            File(cacheDir, DEVICES_FILE_NAME).also {
                Log.d(TAG, "pri deviceId file: $it")
            }
        }
    }

    /**
     * 保存在多个文件夹中
     */
    private fun getSaveFiles(context: Context): List<File> {
        val result = ArrayList<File>()
        result.add(getSaveFile(context, Environment.DIRECTORY_DOWNLOADS))
        result.add(getSaveFile(context, Environment.DIRECTORY_DOCUMENTS))
        result.add(getSaveFile(context, Environment.DIRECTORY_DCIM))
        result.add(getSaveFile(context, Environment.DIRECTORY_PODCASTS))
        result.add(getSaveFile(context, Environment.DIRECTORY_NOTIFICATIONS))
        result.add(getSaveFile(context, Environment.DIRECTORY_ALARMS))
        result.add(getSaveFile(context, Environment.DIRECTORY_MOVIES))
        result.add(getSaveFile(context, Environment.DIRECTORY_MUSIC))
        return result
    }
}