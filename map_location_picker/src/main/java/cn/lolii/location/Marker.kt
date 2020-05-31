package cn.lolii.location

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import cn.lolii.location.model.Position
import cn.lolii.location.util.dip

data class Marker(val location: Position, val center: Boolean, var imageResourceId: Int? = null, var imageBitmap: Bitmap? = null) {

    companion object {
        @JvmStatic
        fun createBitmap(context: Context, imageResourceId: Int, text: String? = null, textColor: Int = 0): Bitmap {
            try {
                val size = context.dip(28)
                val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    context.getDrawable(imageResourceId)
                } else {
                    context.resources.getDrawable(imageResourceId)
                }
                drawable?.setBounds(0, 0, size, size)
                drawable?.draw(canvas)
                text?.let {
                    val targetRect = Rect(0, 0, size, size)
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                    paint.textSize = size / 4f
                    paint.color = textColor
                    val fontMetrics = paint.fontMetricsInt
                    val baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2
                    paint.textAlign = Paint.Align.CENTER
                    canvas.drawText(it, targetRect.centerX().toFloat(), baseline.toFloat(), paint)
                }
                return bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }
}