package com.lolii.shimmer

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/**
 * The shimmer_baseColor and shimmer_highlightColor only worked when shimmer_alphaHighlight=false
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class ShimmerLayout : FrameLayout {
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    companion object {
        const val LEFT_TO_RIGHT = 0
        const val TOP_TO_BOTTOM = 1
        const val RIGHT_TO_LEFT = 2
        const val BOTTOM_TO_TOP = 3

        const val COMPONENT_COUNT = 4

        const val LINEAR = 0
        const val RADIAL = 1
    }

    private val mContentPaint = Paint()
    private val mShimmerPaint = Paint()
    private val mDrawRect = Rect()
    private val mShaderMatrix = Matrix()
    private var mValueAnimator: ValueAnimator? = null

    private var alphaShimmer = true

    private var repeatCount = ValueAnimator.INFINITE
    private var repeatMode = ValueAnimator.RESTART
    private var animationDuration = 5000L
    private var repeatDelay: Long = 0

    private var direction = LEFT_TO_RIGHT
    private val positions = FloatArray(COMPONENT_COUNT)
    private val colors = IntArray(COMPONENT_COUNT)

    private var angle = 0f

    private var highlightColor = Color.WHITE
    private var baseColor = 0x4cffffff

    private var intensity = 0f
    private var dropOff = 0.5f

    private var autoStart = true

    private var shape = LINEAR

    private var clipToChildren = true

    private var fixedWidth = 0
    private var fixedHeight = 0
    private var widthRatio = 1f
    private var heightRatio = 1f

    private var shaderWidth = 0f
    private var shaderHeight = 0f

    private val mUpdateListener = ValueAnimator.AnimatorUpdateListener { invalidate() }

    private fun clamp(min: Float, max: Float, value: Float): Float {
        return Math.min(max, Math.max(min, value))
    }

    /**
     * @param alpha from 0.0 to 1.0
     */
    fun setBaseAlpha(alpha: Float) {
        val intAlpha = (clamp(0f, 1f, alpha) * 255f).toInt()
        baseColor = intAlpha shl 24 or (baseColor and 0x00FFFFFF)
        update()
    }

    fun setBaseColor(color: Int) {
        baseColor = baseColor and -0x1000000 or (color and 0x00FFFFFF)
        update()
    }

    fun setHighlightColor(color: Int) {
        highlightColor = color
        update()
    }

    /**
     * @param alpha from 0.0 to 1.0
     */
    fun setHighlightAlpha(alpha: Float) {
        val intAlpha = (clamp(0f, 1f, alpha) * 255f).toInt()
        highlightColor = intAlpha shl 24 or (highlightColor and 0x00FFFFFF)
        update()
    }

    /**
     * Sets the direction of the shimmer's sweep.
     */
    fun setDirection(direction: Int) {
        if (direction == LEFT_TO_RIGHT
            || direction == TOP_TO_BOTTOM
            || direction == RIGHT_TO_LEFT
            || direction == BOTTOM_TO_TOP
        ) {
            this.direction = direction
            update()
        }
    }

    /**
     * Sets the shape of the shimmer.
     */
    fun setShape(shape: Int) {
        if (shape == LINEAR || shape == RADIAL) {
            this.shape = shape
            update()
        }
    }

    /**
     * Sets the fixed width of the shimmer, in pixels.
     */
    fun setFixedWidth(fixedWidth: Int) {
        if (fixedWidth > 0) {
            this.fixedWidth = fixedWidth
            update()
        }
    }

    /**
     * Sets the fixed height of the shimmer, in pixels.
     */
    fun setFixedHeight(fixedHeight: Int) {
        if (fixedHeight > 0) {
            this.fixedHeight = fixedHeight
            update()
        }
    }

    /**
     * Sets the width ratio of the shimmer, multiplied against the total width of the layout.
     */
    fun setWidthRatio(widthRatio: Float) {
        if (widthRatio > 0f) {
            this.widthRatio = widthRatio
            update()
        }
    }

    /**
     * Sets the height ratio of the shimmer, multiplied against the total height of the layout.
     */
    fun setHeightRatio(heightRatio: Float) {
        if (heightRatio > 0f) {
            this.heightRatio = heightRatio
            update()
        }
    }

    /**
     * Sets the intensity of the shimmer. A larger value causes the shimmer to be larger.
     */
    fun setIntensity(intensity: Float) {
        if (intensity > 0f) {
            this.intensity = intensity
            update()
        }
    }

    fun setDropOff(dropOff: Float) {
        if (dropOff > 0f) {
            this.dropOff = dropOff
            update()
        }
    }

    fun setAngle(angle: Float) {
        this.angle = angle
        update()
    }

    fun setClipToChildren(status: Boolean) {
        clipToChildren = status
        update()
    }

    fun setAutoStart(status: Boolean) {
        autoStart = status
    }

    fun setRepeatCount(count: Int) {
        repeatCount = count
        update()
    }

    fun setRepeatMode(mode: Int) {
        repeatMode = mode
        update()
    }

    fun setRepeatDelay(millis: Long) {
        if (millis > 0) {
            repeatDelay = millis
            update()
        }
    }

    fun setDuration(millis: Long) {
        if (millis > 0) {
            animationDuration = millis
            update()
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        setWillNotDraw(false)
        mShimmerPaint.isAntiAlias = true

        attrs?.let {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ShimmerLayout, 0, 0)

            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_clipToChildren)) {
                clipToChildren = a.getBoolean(R.styleable.ShimmerLayout_shimmer_clipToChildren, clipToChildren)
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_autoStart)) {
                autoStart = a.getBoolean(R.styleable.ShimmerLayout_shimmer_autoStart, autoStart)
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_baseAlpha)) {
                setBaseAlpha(a.getFloat(R.styleable.ShimmerLayout_shimmer_baseAlpha, 0.3f))
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_highlightAlpha)) {
                setHighlightAlpha(a.getFloat(R.styleable.ShimmerLayout_shimmer_highlightAlpha, 1f))
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_alphaHighlight)) {
                alphaShimmer = a.getBoolean(R.styleable.ShimmerLayout_shimmer_alphaHighlight, true)
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_baseColor)) {
                setBaseColor(a.getColor(R.styleable.ShimmerLayout_shimmer_baseColor, baseColor))
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_highlightColor)) {
                setHighlightColor(a.getColor(R.styleable.ShimmerLayout_shimmer_highlightColor, highlightColor))
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_duration)) {
                animationDuration =
                    a.getInt(R.styleable.ShimmerLayout_shimmer_duration, animationDuration.toInt()).toLong()
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_repeatCount)) {
                repeatCount = a.getInt(R.styleable.ShimmerLayout_shimmer_repeatCount, repeatCount)
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_repeatDelay)) {
                repeatDelay = a.getInt(R.styleable.ShimmerLayout_shimmer_repeatDelay, repeatDelay.toInt()).toLong()
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_repeatMode)) {
                repeatMode = a.getInt(R.styleable.ShimmerLayout_shimmer_repeatMode, repeatMode)
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_direction)) {
                val d = a.getInt(R.styleable.ShimmerLayout_shimmer_direction, direction)
                direction = when (d) {
                    LEFT_TO_RIGHT -> LEFT_TO_RIGHT
                    TOP_TO_BOTTOM -> TOP_TO_BOTTOM
                    RIGHT_TO_LEFT -> RIGHT_TO_LEFT
                    BOTTOM_TO_TOP -> BOTTOM_TO_TOP
                    else -> LEFT_TO_RIGHT
                }
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_shape)) {
                val s = a.getInt(R.styleable.ShimmerLayout_shimmer_shape, shape)
                shape = when (s) {
                    LINEAR -> LINEAR
                    RADIAL -> RADIAL
                    else -> LINEAR
                }
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_dropOff)) {
                dropOff = a.getFloat(R.styleable.ShimmerLayout_shimmer_dropOff, dropOff)
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_fixedWidth)) {
                fixedWidth = a.getDimensionPixelSize(R.styleable.ShimmerLayout_shimmer_fixedWidth, fixedWidth)
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_fixedHeight)) {
                fixedHeight = a.getDimensionPixelSize(R.styleable.ShimmerLayout_shimmer_fixedHeight, fixedHeight)
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_intensity)) {
                intensity = a.getFloat(R.styleable.ShimmerLayout_shimmer_intensity, intensity)
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_widthRatio)) {
                widthRatio = a.getFloat(R.styleable.ShimmerLayout_shimmer_widthRatio, widthRatio)
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_heightRatio)) {
                heightRatio = a.getFloat(R.styleable.ShimmerLayout_shimmer_heightRatio, heightRatio)
            }
            if (a.hasValue(R.styleable.ShimmerLayout_shimmer_angle)) {
                angle = a.getFloat(R.styleable.ShimmerLayout_shimmer_angle, angle)
            }
            a.recycle()
        }

        update()
    }

    private fun update() {
        if (clipToChildren) {
            setLayerType(View.LAYER_TYPE_HARDWARE, mContentPaint)
        } else {
            setLayerType(View.LAYER_TYPE_NONE, null)
        }

        mShimmerPaint.xfermode =
            PorterDuffXfermode(if (alphaShimmer) PorterDuff.Mode.DST_IN else PorterDuff.Mode.SRC_IN)

        updateShader()

        updateValueAnimator()
    }

    private fun updateColors() {
        when (shape) {
            RADIAL -> {
                colors[0] = highlightColor
                colors[1] = highlightColor
                colors[2] = baseColor
                colors[3] = baseColor
            }
            else -> {
                colors[0] = baseColor
                colors[1] = highlightColor
                colors[2] = highlightColor
                colors[3] = baseColor
            }
        }
    }

    private fun updatePositions() {
        when (shape) {
            RADIAL -> {
                positions[0] = 0f
                positions[1] = Math.min(intensity, 1f)
                positions[2] = Math.min(intensity + dropOff, 1f)
                positions[3] = 1f
            }
            else -> {
                positions[0] = Math.max((1f - intensity - dropOff) / 2f, 0f)
                positions[1] = Math.max((1f - intensity - 0.001f) / 2f, 0f)
                positions[2] = Math.min((1f + intensity + 0.001f) / 2f, 1f)
                positions[3] = Math.min((1f + intensity + dropOff) / 2f, 1f)
            }
        }
    }

    private fun updateValueAnimator() {
        var started = false
        mValueAnimator?.let {
            started = it.isStarted
            it.cancel()
            it.removeAllUpdateListeners()
        }
        mValueAnimator = ValueAnimator.ofFloat(0f, 1f + (repeatDelay / animationDuration).toFloat())
            .also {
                it.repeatMode = repeatMode
                it.repeatCount = repeatCount
                it.duration = animationDuration + repeatDelay
                it.addUpdateListener(mUpdateListener)
                if (started) {
                    it.start()
                }
            }
    }

    private fun updateShader() {
        val width = mDrawRect.width()
        val height = mDrawRect.height()

        if (width == 0 && height == 0) return

        updateColors()
        updatePositions()

        shaderWidth = if (fixedWidth > 0) fixedWidth.toFloat() else Math.round(widthRatio * width).toFloat()
        shaderHeight = if (fixedHeight > 0) fixedHeight.toFloat() else Math.round(heightRatio * height).toFloat()

        val shader: Shader = if (shape == LINEAR) {
            val vertical = direction == TOP_TO_BOTTOM || direction == BOTTOM_TO_TOP
            val endX = if (vertical) 0f else shaderWidth
            val endY = if (vertical) shaderHeight else 0f
            LinearGradient(0f, 0f, endX, endY, colors, positions, Shader.TileMode.CLAMP)
        } else {
            RadialGradient(
                shaderWidth / 2f,
                shaderHeight / 2f,
                (Math.max(shaderWidth, shaderHeight) / Math.sqrt(2.0)).toFloat(),
                colors,
                positions,
                Shader.TileMode.CLAMP
            )
        }
        mShimmerPaint.shader = shader
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mDrawRect.set(0, 0, width, height)
        updateShader()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (autoStart) {
            start()
        }
    }

    private fun start() {
        if (mValueAnimator == null) {
            updateValueAnimator()
        }
        mValueAnimator?.let {
            if (!it.isStarted) {
                it.removeAllUpdateListeners()
                it.addUpdateListener(mUpdateListener)
                it.start()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mValueAnimator?.let {
            if (it.isStarted) {
                it.cancel()
                it.removeAllUpdateListeners()
            }
        }
    }

    private fun offset(start: Float, end: Float, percent: Float): Float {
        return start + (end - start) * percent
    }

    public override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (mShimmerPaint.shader == null) {
            return
        }

        val angleTan = Math.tan(Math.toRadians(angle.toDouble())).toFloat()
        val translateHeight = mDrawRect.height() + angleTan * mDrawRect.width()
        val translateWidth = mDrawRect.width() + angleTan * mDrawRect.height()
        val dx: Float
        val dy: Float
        val animatedValue = mValueAnimator?.animatedFraction ?: 0f
        when (direction) {
            LEFT_TO_RIGHT -> {
                //dx = offset(-translateWidth, translateWidth, animatedValue)
                dx = offset(-shaderWidth, translateWidth, animatedValue)
                dy = 0f
            }
            RIGHT_TO_LEFT -> {
                //dx = offset(translateWidth, -translateWidth, animatedValue)
                dx = offset(translateWidth, -shaderWidth, animatedValue)
                dy = 0f
            }
            TOP_TO_BOTTOM -> {
                dx = 0f
                //dy = offset(-translateHeight, translateHeight, animatedValue)
                dy = offset(-shaderHeight, translateHeight, animatedValue)
            }
            BOTTOM_TO_TOP -> {
                dx = 0f
                //dy = offset(translateHeight, -translateHeight, animatedValue)
                dy = offset(translateHeight, -shaderHeight, animatedValue)
            }
            else -> {
                //dx = offset(-translateWidth, translateWidth, animatedValue)
                dx = offset(-shaderWidth, translateWidth, animatedValue)
                dy = 0f
            }
        }

        mShaderMatrix.reset()
        mShaderMatrix.setRotate(angle, mDrawRect.width() / 2f, mDrawRect.height() / 2f)
        mShaderMatrix.postTranslate(dx, dy)
        mShimmerPaint.shader.setLocalMatrix(mShaderMatrix)
        canvas.drawRect(mDrawRect, mShimmerPaint)
    }

}