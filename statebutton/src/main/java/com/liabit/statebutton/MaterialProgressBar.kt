package com.liabit.statebutton

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.Cap
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.IntDef
import androidx.annotation.RestrictTo
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * Author:         songtao
 * CreateDate:     2020/12/17 11:33
 */
@Suppress("unused")
class MaterialProgressBar : View {

    companion object {
        private val LINEAR_INTERPOLATOR: Interpolator = LinearInterpolator()
        private val MATERIAL_INTERPOLATOR: Interpolator = FastOutSlowInInterpolator()

        /** @hide
         */
        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        @IntDef(LARGE, DEFAULT)
        annotation class ProgressDrawableSize

        /** Maps to ProgressBar.Large style.  */
        const val LARGE = 0

        private const val CENTER_RADIUS_LARGE = 11f
        private const val STROKE_WIDTH_LARGE = 3f
        private const val ARROW_WIDTH_LARGE = 12
        private const val ARROW_HEIGHT_LARGE = 6

        /** Maps to ProgressBar default style.  */
        const val DEFAULT = 1

        private const val CENTER_RADIUS = 7.5f
        private const val STROKE_WIDTH = 2.5f
        private const val ARROW_WIDTH = 10
        private const val ARROW_HEIGHT = 5

        /**
         * The value in the linear interpolator for animating the drawable at which
         * the color transition should start
         */
        private const val COLOR_CHANGE_OFFSET = 0.75f
        private const val SHRINK_OFFSET = 0.5f

        /** The duration of a single progress spin in milliseconds.  */
        private const val ANIMATION_DURATION = 1332

        /** Full rotation that's done for the animation duration in degrees.  */
        private const val GROUP_FULL_ROTATION = 1080f / 5f

        /** Maximum length of the progress arc during the animation.  */
        private const val MAX_PROGRESS_ARC = .8f

        /** Minimum length of the progress arc during the animation.  */
        private const val MIN_PROGRESS_ARC = .01f

        /** Rotation applied to ring during the animation, to complete it to a full circle.  */
        private const val RING_ROTATION = 1f - (MAX_PROGRESS_ARC - MIN_PROGRESS_ARC)

        private fun dp2px(context: Context, dp: Int): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics)
        }
    }

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }


    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        mRing = Ring()

        var innerRadius = 0f
        var strokeWidth = dp2px(context, 10)
        var color = Color.BLACK
        var colorScheme = 0
        var strokeJoin = Paint.Join.MITER
        var strokeCap = Cap.SQUARE

        var trimStart = 0f
        var trimEnd = 0f
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialProgressView, defStyleAttr, defStyleRes)
            innerRadius = typedArray.getDimension(R.styleable.MaterialProgressView_innerRadius, innerRadius)
            strokeWidth = typedArray.getDimension(R.styleable.MaterialProgressView_strokeWidth, strokeWidth)
            val mRingSize = typedArray.getDimension(R.styleable.MaterialProgressView_size, -1f)
            color = typedArray.getColor(R.styleable.MaterialProgressView_strokeColor, Color.BLACK)
            colorScheme = typedArray.getResourceId(R.styleable.MaterialProgressView_strokeColorScheme, 0)
            strokeJoin = when (typedArray.getInt(R.styleable.MaterialProgressView_strokeLineJoin, 0)) {
                Paint.Join.BEVEL.ordinal -> Paint.Join.BEVEL
                Paint.Join.ROUND.ordinal -> Paint.Join.ROUND
                else -> Paint.Join.MITER
            }
            strokeCap = when (typedArray.getInt(R.styleable.MaterialProgressView_strokeLineJoin, 0)) {
                Cap.BUTT.ordinal -> Cap.BUTT
                Cap.ROUND.ordinal -> Cap.ROUND
                else -> Cap.SQUARE
            }

            trimStart = typedArray.getFloat(R.styleable.MaterialProgressView_trimStart, 0f)
            trimEnd = typedArray.getFloat(R.styleable.MaterialProgressView_trimEnd, 0f)
            typedArray.recycle()
        }
        if (colorScheme != 0) {
            mRing.colors = context.resources.getIntArray(colorScheme)
        } else {
            mRing.colors = intArrayOf(color)
        }
        mRing.setStrokeCap(strokeCap)
        mRing.setStrokeJoin(strokeJoin)
        mRing.startTrim = trimStart
        mRing.endTrim = trimEnd

        setStrokeWidth(STROKE_WIDTH)
        setupAnimators()
    }

    /** The indicator ring, used to manage animation state.  */
    private lateinit var mRing: Ring

    /** Canvas rotation in degrees.  */
    private var mRotation = 0f

    private var mAnimator: Animator? = null
    var mRotationCount = 0f
    var mFinishing = false

    /** Sets all parameters at once in dp.  */
    private fun setSizeParameters(centerRadius: Float, strokeWidth: Float, arrowWidth: Float,
                                  arrowHeight: Float) {
        val screenDensity = resources.displayMetrics.density
        mRing.strokeWidth = strokeWidth * screenDensity
        mRing.centerRadius = centerRadius * screenDensity
        mRing.setColorIndex(0)
        mRing.setArrowDimensions(arrowWidth * screenDensity, arrowHeight * screenDensity)
    }

    /**
     * Sets the overall size for the progress spinner. This updates the radius
     * and stroke width of the ring, and arrow dimensions.
     *
     * @param size one of [.LARGE] or [.DEFAULT]
     */
    fun setStyle(@ProgressDrawableSize size: Int) {
        if (size == LARGE) {
            setSizeParameters(CENTER_RADIUS_LARGE, STROKE_WIDTH_LARGE, ARROW_WIDTH_LARGE.toFloat(),
                    ARROW_HEIGHT_LARGE.toFloat())
        } else {
            setSizeParameters(CENTER_RADIUS, STROKE_WIDTH, ARROW_WIDTH.toFloat(), ARROW_HEIGHT.toFloat())
        }
        invalidate()
    }

    /**
     * Returns the stroke width for the progress spinner in pixels.
     *
     * @return stroke width in pixels
     */
    fun getStrokeWidth(): Float {
        return mRing.strokeWidth
    }

    /**
     * Sets the stroke width for the progress spinner in pixels.
     *
     * @param strokeWidth stroke width in pixels
     */
    fun setStrokeWidth(strokeWidth: Float) {
        mRing.strokeWidth = strokeWidth
        invalidate()
    }

    /**
     * Returns the center radius for the progress spinner in pixels.
     *
     * @return center radius in pixels
     */
    fun getCenterRadius(): Float {
        return mRing.centerRadius
    }

    /**
     * Sets the center radius for the progress spinner in pixels. If set to 0, this drawable will
     * fill the bounds when drawn.
     *
     * @param centerRadius center radius in pixels
     */
    fun setCenterRadius(centerRadius: Float) {
        mRing.centerRadius = centerRadius
        invalidate()
    }

    /**
     * Sets the stroke cap of the progress spinner. Default stroke cap is [Paint.Cap.SQUARE].
     *
     * @param strokeCap stroke cap
     */
    fun setStrokeCap(strokeCap: Cap) {
        mRing.setStrokeCap(strokeCap)
        invalidate()
    }

    /**
     * Returns the arrow width in pixels.
     *
     * @return arrow width in pixels
     */
    fun getArrowWidth(): Int {
        return mRing.arrowWidth
    }

    /**
     * Returns the arrow height in pixels.
     *
     * @return arrow height in pixels
     */
    fun getArrowHeight(): Int {
        return mRing.arrowHeight
    }

    /**
     * Sets the dimensions of the arrow at the end of the spinner in pixels.
     *
     * @param width width of the baseline of the arrow in pixels
     * @param height distance from tip of the arrow to its baseline in pixels
     */
    fun setArrowDimensions(width: Float, height: Float) {
        mRing.setArrowDimensions(width, height)
        invalidate()
    }

    /**
     * Returns `true` if the arrow at the end of the spinner is shown.
     *
     * @return `true` if the arrow is shown, `false` otherwise.
     */
    fun getArrowEnabled(): Boolean {
        return mRing.showArrow
    }

    /**
     * Sets if the arrow at the end of the spinner should be shown.
     *
     * @param show `true` if the arrow should be drawn, `false` otherwise
     */
    fun setArrowEnabled(show: Boolean) {
        mRing.showArrow = show
        invalidate()
    }

    /**
     * Returns the scale of the arrow at the end of the spinner.
     *
     * @return scale of the arrow
     */
    fun getArrowScale(): Float {
        return mRing.arrowScale
    }

    /**
     * Sets the scale of the arrow at the end of the spinner.
     *
     * @param scale scaling that will be applied to the arrow's both width and height when drawing.
     */
    fun setArrowScale(scale: Float) {
        mRing.arrowScale = scale
        invalidate()
    }

    /**
     * Returns the start trim for the progress spinner arc
     *
     * @return start trim from [0..1]
     */
    fun getStartTrim(): Float {
        return mRing.startTrim
    }

    /**
     * Returns the end trim for the progress spinner arc
     *
     * @return end trim from [0..1]
     */
    fun getEndTrim(): Float {
        return mRing.endTrim
    }

    /**
     * Sets the start and end trim for the progress spinner arc. 0 corresponds to the geometric
     * angle of 0 degrees (3 o'clock on a watch) and it increases clockwise, coming to a full circle
     * at 1.
     *
     * @param start starting position of the arc from [0..1]
     * @param end ending position of the arc from [0..1]
     */
    fun setStartEndTrim(start: Float, end: Float) {
        mRing.startTrim = start
        mRing.endTrim = end
        invalidate()
    }

    /**
     * Returns the amount of rotation applied to the progress spinner.
     *
     * @return amount of rotation from [0..1]
     */
    fun getProgressRotation(): Float {
        return mRing.rotation
    }

    /**
     * Sets the amount of rotation to apply to the progress spinner.
     *
     * @param rotation rotation from [0..1]
     */
    fun setProgressRotation(rotation: Float) {
        mRing.rotation = rotation
        invalidate()
    }

    /**
     * Returns the background color of the circle drawn inside the drawable.
     *
     * @return an ARGB color
     */
    fun getBackgroundColor(): Int {
        return mRing.backgroundColor
    }

    /**
     * Sets the background color of the circle inside the drawable. Calling [ ][.setAlpha] does not affect the visibility background color, so it should be set
     * separately if it needs to be hidden or visible.
     *
     * @param color an ARGB color
     */
    override fun setBackgroundColor(color: Int) {
        mRing.backgroundColor = color
        invalidate()
    }

    /**
     * Returns the colors used in the progress animation
     *
     * @return list of ARGB colors
     */
    fun getColorSchemeColors(): IntArray {
        return mRing.colors
    }

    /**
     * Sets the colors used in the progress animation from a color list. The first color will also
     * be the color to be used if animation is not started yet.
     *
     * @param colors list of ARGB colors to be used in the spinner
     */
    fun setColorSchemeColors(vararg colors: Int) {
        mRing.colors = colors
        mRing.setColorIndex(0)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.rotate(mRotation, width / 2f, height / 2f)
        mRing.draw(canvas, 0, 0, width, height)
        canvas.restore()
    }

    fun setColorFilter(colorFilter: ColorFilter?) {
        mRing.setColorFilter(colorFilter)
        invalidate()
    }

    override fun setRotation(rotation: Float) {
        mRotation = rotation
    }

    override fun getRotation(): Float {
        return mRotation
    }

    fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    fun isRunning(): Boolean {
        return mAnimator!!.isRunning
    }

    /**
     * Starts the animation for the spinner.
     */
    fun start() {
        mAnimator!!.cancel()
        mRing.storeOriginals()
        // Already showing some part of the ring
        if (mRing.endTrim != mRing.startTrim) {
            mFinishing = true
            mAnimator!!.duration = (ANIMATION_DURATION / 2).toLong()
            mAnimator!!.start()
        } else {
            mRing.setColorIndex(0)
            mRing.resetOriginals()
            mAnimator!!.duration = ANIMATION_DURATION.toLong()
            mAnimator!!.start()
        }
    }

    /**
     * Stops the animation for the spinner.
     */
    fun stop() {
        mAnimator!!.cancel()
        rotation = 0f
        mRing.showArrow = false
        mRing.setColorIndex(0)
        mRing.resetOriginals()
        invalidate()
    }

    // Adapted from ArgbEvaluator.java
    private fun evaluateColorChange(fraction: Float, startValue: Int, endValue: Int): Int {
        val startA = startValue shr 24 and 0xff
        val startR = startValue shr 16 and 0xff
        val startG = startValue shr 8 and 0xff
        val startB = startValue and 0xff
        val endA = endValue shr 24 and 0xff
        val endR = endValue shr 16 and 0xff
        val endG = endValue shr 8 and 0xff
        val endB = endValue and 0xff
        return startA + (fraction * (endA - startA)).toInt() shl 24 or (startR + (fraction * (endR - startR)).toInt() shl 16
                ) or (startG + (fraction * (endG - startG)).toInt() shl 8
                ) or startB + (fraction * (endB - startB)).toInt()
    }

    /**
     * Update the ring color if this is within the last 25% of the animation.
     * The new ring color will be a translation from the starting ring color to
     * the next color.
     */
    private fun updateRingColor(interpolatedTime: Float, ring: Ring) {
        if (interpolatedTime > COLOR_CHANGE_OFFSET) {
            ring.setColor(evaluateColorChange((interpolatedTime - COLOR_CHANGE_OFFSET) / (1f - COLOR_CHANGE_OFFSET), ring.startingColor, ring.nextColor))
        } else {
            ring.setColor(ring.startingColor)
        }
    }

    /**
     * Update the ring start and end trim if the animation is finishing (i.e. it started with
     * already visible progress, so needs to shrink back down before starting the spinner).
     */
    private fun applyFinishTranslation(interpolatedTime: Float, ring: Ring) {
        // shrink back down and complete a full rotation before
        // starting other circles
        // Rotation goes between [0..1].
        updateRingColor(interpolatedTime, ring)
        val targetRotation = (floor((ring.startingRotation / MAX_PROGRESS_ARC).toDouble()) + 1f).toFloat()
        val startTrim = (ring.startingStartTrim + (ring.startingEndTrim - MIN_PROGRESS_ARC - ring.startingStartTrim) * interpolatedTime)
        ring.startTrim = startTrim
        ring.endTrim = ring.startingEndTrim
        val rotation = (ring.startingRotation + (targetRotation - ring.startingRotation) * interpolatedTime)
        ring.rotation = rotation
    }

    /**
     * Update the ring start and end trim according to current time of the animation.
     */
    private fun applyTransformation(interpolatedTime: Float, ring: Ring, lastFrame: Boolean) {
        if (mFinishing) {
            applyFinishTranslation(interpolatedTime, ring)
            // Below condition is to work around a ValueAnimator issue where onAnimationRepeat is
            // called before last frame (1f).
        } else if (interpolatedTime != 1f || lastFrame) {
            val startingRotation = ring.startingRotation
            val startTrim: Float
            val endTrim: Float
            if (interpolatedTime < SHRINK_OFFSET) { // Expansion occurs on first half of animation
                val scaledTime = interpolatedTime / SHRINK_OFFSET
                startTrim = ring.startingStartTrim
                endTrim = startTrim + ((MAX_PROGRESS_ARC - MIN_PROGRESS_ARC)
                        * MATERIAL_INTERPOLATOR.getInterpolation(scaledTime) + MIN_PROGRESS_ARC)
            } else { // Shrinking occurs on second half of animation
                val scaledTime = (interpolatedTime - SHRINK_OFFSET) / (1f - SHRINK_OFFSET)
                endTrim = ring.startingStartTrim + (MAX_PROGRESS_ARC - MIN_PROGRESS_ARC)
                startTrim = endTrim - ((MAX_PROGRESS_ARC - MIN_PROGRESS_ARC)
                        * (1f - MATERIAL_INTERPOLATOR.getInterpolation(scaledTime))
                        + MIN_PROGRESS_ARC)
            }
            val rotation = startingRotation + RING_ROTATION * interpolatedTime
            val groupRotation = GROUP_FULL_ROTATION * (interpolatedTime + mRotationCount)
            ring.startTrim = startTrim
            ring.endTrim = endTrim
            ring.rotation = rotation
            setRotation(groupRotation)
        }
    }

    private fun setupAnimators() {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener { animation ->
            val interpolatedTime = animation.animatedValue as Float
            updateRingColor(interpolatedTime, mRing)
            applyTransformation(interpolatedTime, mRing, false)
            invalidate()
        }
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.interpolator = LINEAR_INTERPOLATOR
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                mRotationCount = 0f
            }

            override fun onAnimationEnd(animator: Animator) {
                // do nothing
            }

            override fun onAnimationCancel(animation: Animator) {
                // do nothing
            }

            override fun onAnimationRepeat(animator: Animator) {
                applyTransformation(1f, mRing, true)
                mRing.storeOriginals()
                mRing.goToNextColor()
                if (mFinishing) {
                    // finished closing the last ring from the swipe gesture; go
                    // into progress mode
                    mFinishing = false
                    animator.cancel()
                    animator.duration = ANIMATION_DURATION.toLong()
                    animator.start()
                    mRing.showArrow = false
                } else {
                    mRotationCount += 1
                }
            }
        })
        mAnimator = animator
    }

    /**
     * A private class to do all the drawing of CircularProgressDrawable, which includes background,
     * progress spinner and the arrow. This class is to separate drawing from animation.
     */
    internal class Ring {
        private val mTempBounds = RectF()
        private val mPaint = Paint()
        private val mArrowPaint = Paint()
        private val mCirclePaint = Paint()
        private var mColorIndex = 0
        private var mStrokeWidth = 5f
        private var mColors: IntArray = intArrayOf(Color.BLUE)
        private var mShowArrow = false
        private var mArrow: Path = Path()
        private var mArrowScale = 1f
        private var mAlpha = 255
        private var mCurrentColor = 0

        var startTrim = 0f
        var endTrim = 0f
        var rotation = 0f

        var startingStartTrim = 0f
        var startingEndTrim = 0f

        /**
         * @return The amount the progress spinner is currently rotated, between [0..1].
         */
        var startingRotation = 0f

        /**
         * inner radius in px of the circle the progress spinner arc traces
         */
        var centerRadius = 0f
        var arrowWidth = 0
        var arrowHeight = 0

        init {
            mPaint.strokeCap = Cap.SQUARE
            mPaint.isAntiAlias = true
            mPaint.style = Paint.Style.STROKE
            mArrowPaint.style = Paint.Style.FILL
            mArrowPaint.isAntiAlias = true
            mCirclePaint.color = Color.TRANSPARENT
        }

        private val nextColorIndex: Int get() = (mColorIndex + 1) % mColors.size

        /**
         * Sets the colors the progress spinner alternates between.
         *
         * array of ARGB colors. Must be non-`null`.
         */
        var colors: IntArray
            get() = mColors
            set(colors) {
                mColors = colors
                // if colors are reset, make sure to reset the color index as well
                setColorIndex(0)
            }

        /**
         * @return int describing the next color the progress spinner should use when drawing.
         */
        val nextColor: Int get() = mColors[nextColorIndex]

        /**
         * Sets the background color of the circle inside the spinner.
         */
        var backgroundColor: Int
            get() = mCirclePaint.color
            set(color) {
                mCirclePaint.color = color
            }

        /**
         * set the stroke width of the progress spinner in pixels.
         */
        var strokeWidth: Float
            get() = mStrokeWidth
            set(strokeWidth) {
                mStrokeWidth = strokeWidth
                mPaint.strokeWidth = strokeWidth
            }

        val startingColor: Int get() = mColors[mColorIndex]

        /**
         * `true` if should show the arrow head on the progress spinner
         */
        var showArrow: Boolean
            get() = mShowArrow
            set(show) {
                if (mShowArrow != show) {
                    mShowArrow = show
                }
            }

        /**
         *  scale of the arrowhead for the spinner
         */
        var arrowScale: Float
            get() = mArrowScale
            set(scale) {
                if (scale != mArrowScale) {
                    mArrowScale = scale
                }
            }

        /**
         * Sets the absolute color of the progress spinner. This is should only
         * be used when animating between current and next color when the
         * spinner is rotating.
         *
         * @param color an ARGB color
         */
        fun setColor(color: Int) {
            mCurrentColor = color
        }

        /**
         * @param index index into the color array of the color to display in
         * the progress spinner.
         */
        fun setColorIndex(index: Int) {
            mColorIndex = index
            mCurrentColor = mColors[mColorIndex]
        }

        /**
         * Proceed to the next available ring color. This will automatically
         * wrap back to the beginning of colors.
         */
        fun goToNextColor() {
            setColorIndex(nextColorIndex)
        }

        fun setColorFilter(filter: ColorFilter?) {
            mPaint.colorFilter = filter
        }

        /**
         * Sets the dimensions of the arrowhead.
         *
         * @param width width of the hypotenuse of the arrow head
         * @param height height of the arrow point
         */
        fun setArrowDimensions(width: Float, height: Float) {
            arrowWidth = width.toInt()
            arrowHeight = height.toInt()
        }

        fun setStrokeCap(cap: Cap) {
            mPaint.strokeCap = cap
        }

        fun setStrokeJoin(join: Paint.Join) {
            mPaint.strokeJoin = join
        }

        /**
         * If the start / end trim are offset to begin with, store them so that animation starts
         * from that offset.
         */
        fun storeOriginals() {
            startingStartTrim = startTrim
            startingEndTrim = endTrim
            startingRotation = rotation
        }

        /**
         * Reset the progress spinner to default rotation, start and end angles.
         */
        fun resetOriginals() {
            startingStartTrim = 0f
            startingEndTrim = 0f
            startingRotation = 0f
            startTrim = 0f
            endTrim = 0f
            rotation = 0f
        }

        /**
         * Draw the progress spinner
         */
        fun draw(c: Canvas, left: Int, top: Int, right: Int, bottom: Int) {
            val arcBounds = mTempBounds
            var arcRadius = centerRadius + mStrokeWidth / 2f
            val width = right - left
            val height = bottom - top
            val centerX = (left + right) / 2
            val centerY = (top + bottom) / 2
            if (centerRadius <= 0) {
                // If center radius is not set, fill the bounds
                arcRadius = min(width, height) / 2f - max(arrowWidth * mArrowScale / 2f, mStrokeWidth / 2f)
            }
            arcBounds[centerX - arcRadius, centerY - arcRadius, centerX + arcRadius] = centerY + arcRadius
            val startAngle = (startTrim + rotation) * 360
            val endAngle = (endTrim + rotation) * 360
            val sweepAngle = endAngle - startAngle
            mPaint.color = mCurrentColor
            mPaint.alpha = mAlpha

            // Draw the background first
            val inset = mStrokeWidth / 2f // Calculate inset to draw inside the arc
            arcBounds.inset(inset, inset) // Apply inset
            c.drawCircle(arcBounds.centerX(), arcBounds.centerY(), arcBounds.width() / 2f, mCirclePaint)
            arcBounds.inset(-inset, -inset) // Revert the inset
            c.drawArc(arcBounds, startAngle, sweepAngle, false, mPaint)
            drawTriangle(c, startAngle, sweepAngle, arcBounds)
        }

        private fun drawTriangle(c: Canvas, startAngle: Float, sweepAngle: Float, bounds: RectF) {
            if (mShowArrow) {

                mArrow.fillType = Path.FillType.EVEN_ODD
                mArrow.reset()

                val centerRadius = min(bounds.width(), bounds.height()) / 2f
                val inset = arrowWidth * mArrowScale / 2f
                // Update the path each time. This works around an issue in SKIA
                // where concatenating a rotation matrix to a scale matrix
                // ignored a starting negative rotation. This appears to have
                // been fixed as of API 21.
                mArrow.moveTo(0f, 0f)
                mArrow.lineTo(arrowWidth * mArrowScale, 0f)
                mArrow.lineTo(arrowWidth * mArrowScale / 2, arrowHeight * mArrowScale)
                mArrow.offset(centerRadius + bounds.centerX() - inset, bounds.centerY() + mStrokeWidth / 2f)
                mArrow.close()
                // draw a triangle
                mArrowPaint.color = mCurrentColor
                mArrowPaint.alpha = mAlpha
                c.save()
                c.rotate(startAngle + sweepAngle, bounds.centerX(), bounds.centerY())
                c.drawPath(mArrow, mArrowPaint)
                c.restore()
            }
        }
    }

}
