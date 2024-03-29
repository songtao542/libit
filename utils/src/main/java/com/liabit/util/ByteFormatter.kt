package com.liabit.util

import android.content.Context
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Suppress("SameParameterValue", "MemberVisibilityCanBePrivate")
object ByteFormatter {

    const val FLAG_SHORTER = 1 shl 0
    const val FLAG_CALCULATE_ROUNDED = 1 shl 1
    const val FLAG_SI_UNITS = 1 shl 2
    const val FLAG_IEC_UNITS = 1 shl 3

    private val NUMBER_FORMAT by lazy {
        DecimalFormat.getInstance().also {
            it.maximumFractionDigits = 2
        }
    }

    fun formatFileSize(sizeBytes: Long, maximumFractionDigits: Int = -1, flags: Int = FLAG_SI_UNITS): String {
        val res: BytesResult = formatBytes(sizeBytes, flags)
        return if (maximumFractionDigits >= 0) {
            NUMBER_FORMAT.maximumFractionDigits = maximumFractionDigits
            "${NUMBER_FORMAT.format(res.value)}${res.units}"
        } else {
            "${res.value}${res.units}"
        }
    }

    fun formatBytes(sizeBytes: Long): BytesResult {
        return formatBytes(sizeBytes, FLAG_SI_UNITS)
    }

    fun formatBytes(sizeBytes: Long, flags: Int): BytesResult {
        val unit = if (flags and FLAG_IEC_UNITS != 0) 1024 else 1000
        val isNegative = sizeBytes < 0
        var result = if (isNegative) (-sizeBytes).toFloat() else sizeBytes.toFloat()
        var units = "B"
        var mult: Long = 1
        if (result > 900) {
            units = "kB"
            mult = unit.toLong()
            result /= unit
        }
        if (result > 900) {
            units = "MB"
            mult *= unit.toLong()
            result /= unit
        }
        if (result > 900) {
            units = "GB"
            mult *= unit.toLong()
            result /= unit
        }
        if (result > 900) {
            units = "TB"
            mult *= unit.toLong()
            result /= unit
        }
        if (result > 900) {
            units = "PB"
            mult *= unit.toLong()
            result /= unit
        }
        // Note we calculate the rounded long by ourselves, but still let String.format()
        // compute the rounded value. String.format("%f", 0.1) might not return "0.1" due to
        // floating point errors.
        val roundFactor: Int
        val roundFormat: String
        if (mult == 1L || result >= 100) {
            roundFactor = 1
            roundFormat = "%.0f"
        } else if (result < 1) {
            roundFactor = 100
            roundFormat = "%.2f"
        } else if (result < 10) {
            if (flags and FLAG_SHORTER != 0) {
                roundFactor = 10
                roundFormat = "%.1f"
            } else {
                roundFactor = 100
                roundFormat = "%.2f"
            }
        } else { // 10 <= result < 100
            if (flags and FLAG_SHORTER != 0) {
                roundFactor = 1
                roundFormat = "%.0f"
            } else {
                roundFactor = 100
                roundFormat = "%.2f"
            }
        }
        if (isNegative) {
            result = -result
        }
        val roundedString = String.format(roundFormat, result)

        // Note this might overflow if abs(result) >= Long.MAX_VALUE / 100, but that's like 80PB so
        // it's okay (for now)...
        val roundedBytes = if (flags and FLAG_CALCULATE_ROUNDED == 0) 0 else (result * roundFactor).roundToInt().toLong() * mult / roundFactor
        return BytesResult(roundedString, units, result, roundedBytes)
    }


    fun formatFileSize(context: Context, sizeBytes: Long): String {
        return formatFileSize(context, sizeBytes, FLAG_SI_UNITS)
    }

    fun formatFileSize(context: Context, sizeBytes: Long, flags: Int): String {
        val res: BytesResult = formatBytes(context, sizeBytes, flags)
        return context.getString(R.string.fileSizeSuffix, res.value.toString(), res.units)
    }

    fun formatBytes(context: Context, sizeBytes: Long): BytesResult {
        return formatBytes(context, sizeBytes, FLAG_SI_UNITS)
    }

    fun formatBytes(context: Context, sizeBytes: Long, flags: Int): BytesResult {
        val unit = if (flags and FLAG_IEC_UNITS != 0) 1024 else 1000
        val isNegative = sizeBytes < 0
        var result = if (isNegative) (-sizeBytes).toFloat() else sizeBytes.toFloat()
        var suffix: Int = R.string.byteShort
        var mult: Long = 1
        if (result > 900) {
            suffix = R.string.kilobyteShort
            mult = unit.toLong()
            result /= unit
        }
        if (result > 900) {
            suffix = R.string.megabyteShort
            mult *= unit.toLong()
            result /= unit
        }
        if (result > 900) {
            suffix = R.string.gigabyteShort
            mult *= unit.toLong()
            result /= unit
        }
        if (result > 900) {
            suffix = R.string.terabyteShort
            mult *= unit.toLong()
            result /= unit
        }
        if (result > 900) {
            suffix = R.string.petabyteShort
            mult *= unit.toLong()
            result /= unit
        }
        // Note we calculate the rounded long by ourselves, but still let String.format()
        // compute the rounded value. String.format("%f", 0.1) might not return "0.1" due to
        // floating point errors.
        val roundFactor: Int
        val roundFormat: String
        if (mult == 1L || result >= 100) {
            roundFactor = 1
            roundFormat = "%.0f"
        } else if (result < 1) {
            roundFactor = 100
            roundFormat = "%.2f"
        } else if (result < 10) {
            if (flags and FLAG_SHORTER != 0) {
                roundFactor = 10
                roundFormat = "%.1f"
            } else {
                roundFactor = 100
                roundFormat = "%.2f"
            }
        } else { // 10 <= result < 100
            if (flags and FLAG_SHORTER != 0) {
                roundFactor = 1
                roundFormat = "%.0f"
            } else {
                roundFactor = 100
                roundFormat = "%.2f"
            }
        }
        if (isNegative) {
            result = -result
        }
        val roundedString = String.format(roundFormat, result)

        // Note this might overflow if abs(result) >= Long.MAX_VALUE / 100, but that's like 80PB so
        // it's okay (for now)...
        val roundedBytes = if (flags and FLAG_CALCULATE_ROUNDED == 0) 0 else (result * roundFactor).roundToInt().toLong() * mult / roundFactor
        val units = context.getString(suffix)
        return BytesResult(roundedString, units, result, roundedBytes)
    }
}

data class BytesResult(val roundedString: String, val units: String, val value: Float, val roundedBytes: Long)