package com.liabit.color

/**
 * Author:         songtao
 * CreateDate:     2020/12/15 12:50
 */
object Gradient4 {

    /*private var mArgbEvaluator: ArgbEvaluator = ArgbEvaluator()

    private fun getColor(fraction: Float, startColor: Int, endColor: Int): Int {
        return mArgbEvaluator.evaluate(fraction, startColor, endColor) as Int
    }

    private fun getFraction(i: Int, j: Int, matrix: Int): Float {
        return (i + j + 1f) / (matrix * 2f)
    }

    fun getColors(matrix: Int, leftTopColor: Int, rightTopColor: Int, leftBottomColor: Int, rightBottomColor: Int): IntArray {
        val result = IntArray(matrix * matrix)
        val leftTopToRightBottom = IntArray(matrix * matrix)
        for (i in 0 until matrix) {
            for (j in 0 until matrix) {
                val fraction = (i + j) / (matrix * 2f - 2f) // (i + j + 1f) / (matrix * 2f)
                leftTopToRightBottom[i * matrix + j] = getColor(fraction, leftTopColor, rightBottomColor)
            }
        }
        val leftBottomToRightTop = IntArray(matrix * matrix)
        for (i in 0 until matrix) {
            for (j in 0 until matrix) {
                val fraction = (matrix - 1 - i + j) / (matrix * 2f - 2f)  // (matrix - 1 - i + jf+f1f) / (matrix * 2f)
                leftBottomToRightTop[i * matrix + j] = getColor(fraction, leftBottomColor, rightTopColor)
            }
        }
        for (i in 0 until matrix * matrix) {
            val ltToRb = leftTopToRightBottom[i]
            val lbToRt = leftBottomToRightTop[i]
            result[i] = ColorUtils.blendARGB(ltToRb, lbToRt, 0.5f)
        }
        return result
    }*/


    private fun evaluate(value: Float, start: Float, end: Float): Float {
        return (end - start) * value + start
    }

    /*private fun evaluate(value: Float, start: Color, end: Color): Color {
        return Color.valueOf(evaluate(value, start.red(), end.red()),
                evaluate(value, start.green(), end.green()),
                evaluate(value, start.blue(), end.blue()), 1f)
    }

    private fun evaluate(point: PointF, topLeft: Color, topRight: Color, bottomLeft: Color, bottomRight: Color): Color {
        val top = evaluate(point.x, topLeft, topRight)
        val bottom = evaluate(point.x, bottomLeft, bottomRight)
        return evaluate(point.y, top, bottom)
    }

    fun getColors(matrix: Int, leftTopColor: Int, rightTopColor: Int, leftBottomColor: Int, rightBottomColor: Int): IntArray {
        val colors = IntArray(matrix * matrix)
        val leftTop: Color = Color.valueOf(leftTopColor)
        val rightTop = Color.valueOf(rightTopColor)
        val leftBottom = Color.valueOf(leftBottomColor)
        val rightBottom = Color.valueOf(rightBottomColor)
        for (y in 0 until matrix) {
            for (x in 0 until matrix) {
                val color = evaluate(PointF(x / (matrix - 1f), y / (matrix - 1f)), leftTop, rightTop, leftBottom, rightBottom)
                colors[(y * matrix + x)] = color.toArgb()
            }
        }
        return colors
    }*/

    private fun evaluate(value: Float, start: Int, end: Int): Int {
        return Color.toArgb(
                evaluate(value, Color.red(start), Color.red(end)),
                evaluate(value, Color.green(start), Color.green(end)),
                evaluate(value, Color.blue(start), Color.blue(end)),
                evaluate(value, Color.alpha(start), Color.alpha(end)))
    }

    private fun evaluate(x: Float, y: Float, leftTop: Int, rightTop: Int, leftBottom: Int, rightBottom: Int): Int {
        val top = evaluate(x, leftTop, rightTop)
        val bottom = evaluate(x, leftBottom, rightBottom)
        return evaluate(y, top, bottom)
    }

    fun getColorMatrix(matrix: Int, leftTopColor: Int, rightTopColor: Int, leftBottomColor: Int, rightBottomColor: Int): IntArray {
        val colors = IntArray(matrix * matrix)
        for (y in 0 until matrix) {
            for (x in 0 until matrix) {
                colors[(y * matrix + x)] = evaluate(x / (matrix - 1f), y / (matrix - 1f), leftTopColor, rightTopColor, leftBottomColor, rightBottomColor)
            }
        }
        return colors
    }


}