package com.liabit.util

import android.content.Context
import kotlin.math.roundToInt

@Suppress("SameParameterValue")
object ByteFormatter {

    private const val FLAG_SHORTER = 1 shl 0
    private const val FLAG_CALCULATE_ROUNDED = 1 shl 1
    private const val FLAG_SI_UNITS = 1 shl 2
    private const val FLAG_IEC_UNITS = 1 shl 3

    data class BytesResult(val value: String, val units: String, val roundedBytes: Long)

    fun formatFileSize(context: Context, sizeBytes: Long): String {
        return formatFileSize(context, sizeBytes, FLAG_SI_UNITS)
    }

    private fun formatFileSize(context: Context, sizeBytes: Long, flags: Int): String {
        val res: BytesResult = formatBytes(context, sizeBytes, flags)
        return context.getString(R.string.fileSizeSuffix, res.value, res.units)
    }

    fun formatBytes(context: Context, sizeBytes: Long): BytesResult {
        return formatBytes(context, sizeBytes, FLAG_SI_UNITS)
    }

    private fun formatBytes(context: Context, sizeBytes: Long, flags: Int): BytesResult {
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
        return BytesResult(roundedString, units, roundedBytes)
    }


}