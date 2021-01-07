package com.liabit.photoview

import android.animation.*
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.abs

@Suppress("unused", "MemberVisibilityCanBePrivate")
class DraggablePhotoView : PhotoView {

    companion object {
        private const val DURATION = 400L
        private const val MIN_TRANS_DEST = 5
    }

    enum class Status {
        STATE_NORMAL, STATE_IN, STATE_OUT, STATE_MOVE
    }

    private var mStatus = Status.STATE_NORMAL
    private var mPaint: Paint = Paint()
    private var mMatrix: Matrix = Matrix()
    private var mStartTransform: Transform? = null
    private var mEndTransform: Transform? = null
    private var mAnimTransform: Transform? = null
    private var mMarkTransform: Transform? = null

    private var mThumbRect: Rect? = null
    private var mTransformStart = false
    private var mBitmapWidth = 0
    private var mBitmapHeight = 0
    private var mDraggable = false
    private var mAnimator: ValueAnimator? = null

    private var mMaxTransScale = 0.5f

    private var mDownX = 0
    private var mDownY = 0
    private var mMoved = false
    private var mDownPhoto = false
    private var mAlpha = 0

    private var mClickToQuitWhenNoScale = true

    private var mAlphaChangeListener: OnAlphaChangeListener? = null
    private var mTransformListener: OnTransformListener? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attr: AttributeSet?, defStyle: Int) : super(context, attr, defStyle) {
        init()
    }

    private fun init() {
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.BLACK
        scaleType = ScaleType.FIT_CENTER
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mBitmapWidth = 0
        mBitmapHeight = 0
        mThumbRect = null
        mAnimator?.cancel()
        mAnimator = null
    }

    private fun isFullScreen(): Boolean {
        (context as? Activity)?.let {
            val layFull = it.window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (layFull == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) {
                return true
            }
        }
        return false
    }

    /***
     * 设置只有图片没有放大或者的缩小状态触退出
     * @param clickToQuitWhenNoScale    true false
     */
    fun setClickToQuitWhenNoScale(clickToQuitWhenNoScale: Boolean) {
        mClickToQuitWhenNoScale = clickToQuitWhenNoScale
    }

    fun checkMinScale(): Boolean {
        if (scale != 1f) {
            setScale(1f, true)
            return false
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            return
        }
        if (mStatus == Status.STATE_OUT || mStatus == Status.STATE_IN) {
            initTransformIfNeeded()
            val animTransform = mAnimTransform
            if (animTransform == null) {
                super.onDraw(canvas)
            } else {
                mPaint.alpha = animTransform.alpha
                canvas.drawPaint(mPaint)
                val saveCount = canvas.saveCount
                mMatrix.setScale(animTransform.scale, animTransform.scale)
                val translateX = -(mBitmapWidth * animTransform.scale - animTransform.width) / 2
                val translateY = -(mBitmapHeight * animTransform.scale - animTransform.height) / 2
                mMatrix.postTranslate(translateX, translateY)
                canvas.translate(animTransform.left, animTransform.top)
                canvas.clipRect(0f, 0f, animTransform.width, animTransform.height)
                canvas.concat(mMatrix)
                drawable.draw(canvas)
                canvas.restoreToCount(saveCount)
                if (mTransformStart) {
                    startTransformAnimation()
                }
            }
        } else if (mStatus == Status.STATE_MOVE) {
            mPaint.alpha = 0
            canvas.drawPaint(mPaint)
            super.onDraw(canvas)
        } else {
            mPaint.alpha = 255
            canvas.drawPaint(mPaint)
            super.onDraw(canvas)
        }
    }

    private fun actionDown(event: MotionEvent) {
        mDownX = event.x.toInt()
        mDownY = event.y.toInt()
        if (mMarkTransform == null) {
            initTransformIfNeeded()
        }
        mDownPhoto = false
        if (mMarkTransform != null) {
            val startY = mMarkTransform!!.top.toInt()
            val endY = (mMarkTransform!!.height + mMarkTransform!!.top).toInt()
            if (mDownY in startY..endY) {
                mDownPhoto = true
            }
        }
        mMoved = false
    }

    private fun actionMove(event: MotionEvent): Boolean {
        if (!mDownPhoto && event.pointerCount == 1) {
            return super.dispatchTouchEvent(event)
        }
        val mx = event.x.toInt()
        val my = event.y.toInt()
        val offsetX = mx - mDownX
        val offsetY = my - mDownY

        // 水平方向移动不予处理
        val s = !mMoved && (abs(offsetX) > abs(offsetY) || abs(offsetY) < MIN_TRANS_DEST)
        return if (s) {
            super.dispatchTouchEvent(event)
        } else {
            if (!mDraggable) {
                return super.dispatchTouchEvent(event)
            }
            // 一指滑动时，才对图片进行移动缩放处理
            if (event.pointerCount == 1) {
                mStatus = Status.STATE_MOVE
                offsetLeftAndRight(offsetX)
                offsetTopAndBottom(offsetY)
                val scale = moveScale()
                val scaleXY = 1 - scale * 0.1f
                scaleY = scaleXY
                scaleX = scaleXY
                mMoved = true
                mAlpha = (255 * (1 - scale * 0.5f)).toInt()
                invalidate()
                if (mAlpha < 0) {
                    mAlpha = 0
                }
                mAlphaChangeListener?.onAlphaChange(mAlpha)
                true
            } else {
                super.dispatchTouchEvent(event)
            }
        }
    }

    private fun actionCancel(): Boolean {
        if (mTransformListener == null || moveScale() <= mMaxTransScale) {
            animateToOriginPosition()
        } else {
            changeTransform()
            setTag(R.id.pv_image_key_tag, true)
            transformOut()
        }
        return true
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        return if (mClickToQuitWhenNoScale) {
            if (scale == 1f) {
                when (action) {
                    MotionEvent.ACTION_DOWN -> actionDown(event)
                    MotionEvent.ACTION_MOVE -> return actionMove(event)
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (mMoved) {
                        return actionCancel()
                    }
                }
            } else {
                when (action) {
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (mMoved) {
                        return actionCancel()
                    }
                }
            }
            super.dispatchTouchEvent(event)
        } else {
            when (action) {
                MotionEvent.ACTION_DOWN -> actionDown(event)
                MotionEvent.ACTION_MOVE -> return actionMove(event)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (mMoved) {
                    return actionCancel()
                }
            }
            super.dispatchTouchEvent(event)
        }
    }

    /**
     * 未达到关闭的阈值松手时，返回到初始位置
     */
    private fun animateToOriginPosition() {
        val va = ValueAnimator.ofInt(top, 0)
        va.addUpdateListener(object : AnimatorUpdateListener {
            var startValue = 0
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val value = animation.animatedValue as Int
                if (startValue != 0) {
                    offsetTopAndBottom(value - startValue)
                }
                startValue = value
            }
        })
        val leftAnim = ValueAnimator.ofInt(left, 0)
        leftAnim.addUpdateListener(object : AnimatorUpdateListener {
            var startValue = 0
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val value = animation.animatedValue as Int
                if (startValue != 0) {
                    offsetLeftAndRight(value - startValue)
                }
                startValue = value
            }
        })
        val alphaAnim = ValueAnimator.ofInt(mAlpha, 255)
        alphaAnim.addUpdateListener { animation ->
            mAlphaChangeListener?.onAlphaChange(animation.animatedValue as Int)
        }
        val scaleAnim = ValueAnimator.ofFloat(scaleX, 1f)
        scaleAnim.addUpdateListener { animation ->
            val scale = animation.animatedValue as Float
            scaleX = scale
            scaleY = scale
        }
        val animatorSet = AnimatorSet()
        animatorSet.duration = DURATION
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.playTogether(va, leftAnim, scaleAnim, alphaAnim)
        animatorSet.start()
    }

    private fun moveScale(): Float {
        if (mMarkTransform == null) {
            initTransformIfNeeded()
        }
        return abs(top / (mMarkTransform?.height ?: 1f))
    }

    private fun changeTransform() {
        val markTransform = mMarkTransform ?: return
        val tempTransform = markTransform.copy()
        tempTransform.top = markTransform.top + top
        tempTransform.left = markTransform.left + left
        tempTransform.alpha = mAlpha
        tempTransform.scale = markTransform.scale - (1 - scaleX) * markTransform.scale
        mAnimTransform = tempTransform.copy()
        mEndTransform = tempTransform.copy()
    }

    private fun startTransformAnimation() {
        mTransformStart = false
        val animTransform = mAnimTransform ?: return
        val startTransform = mStartTransform ?: return
        val endTransform = mEndTransform ?: return
        val animator = ValueAnimator().apply {
            duration = DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }.also {
            mAnimator = it
        }
        if (mStatus == Status.STATE_IN) {
            val scaleHolder = PropertyValuesHolder.ofFloat("animScale", startTransform.scale, endTransform.scale)
            val alphaHolder = PropertyValuesHolder.ofInt("animAlpha", startTransform.alpha, endTransform.alpha)
            val leftHolder = PropertyValuesHolder.ofFloat("animLeft", startTransform.left, endTransform.left)
            val topHolder = PropertyValuesHolder.ofFloat("animTop", startTransform.top, endTransform.top)
            val widthHolder = PropertyValuesHolder.ofFloat("animWidth", startTransform.width, endTransform.width)
            val heightHolder = PropertyValuesHolder.ofFloat("animHeight", startTransform.height, endTransform.height)
            animator.setValues(scaleHolder, alphaHolder, leftHolder, topHolder, widthHolder, heightHolder)
        } else if (mStatus == Status.STATE_OUT) {
            val scaleHolder = PropertyValuesHolder.ofFloat("animScale", endTransform.scale, startTransform.scale)
            val alphaHolder = PropertyValuesHolder.ofInt("animAlpha", endTransform.alpha, startTransform.alpha)
            val leftHolder = PropertyValuesHolder.ofFloat("animLeft", endTransform.left, startTransform.left)
            val topHolder = PropertyValuesHolder.ofFloat("animTop", endTransform.top, startTransform.top)
            val widthHolder = PropertyValuesHolder.ofFloat("animWidth", endTransform.width, startTransform.width)
            val heightHolder = PropertyValuesHolder.ofFloat("animHeight", endTransform.height, startTransform.height)
            animator.setValues(scaleHolder, alphaHolder, leftHolder, topHolder, widthHolder, heightHolder)
        }
        animator.addUpdateListener { animation ->
            animTransform.alpha = animation.getAnimatedValue("animAlpha") as Int
            animTransform.scale = animation.getAnimatedValue("animScale") as Float
            animTransform.left = animation.getAnimatedValue("animLeft") as Float
            animTransform.top = animation.getAnimatedValue("animTop") as Float
            animTransform.width = animation.getAnimatedValue("animWidth") as Float
            animTransform.height = animation.getAnimatedValue("animHeight") as Float
            mAlphaChangeListener?.onAlphaChange(animTransform.alpha)
            invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                if (getTag(R.id.pv_image_key_tag) != null) {
                    setTag(R.id.pv_image_key_tag, null)
                    setOnLongClickListener(null)
                }
            }

            override fun onAnimationEnd(animation: Animator?) {
                /*
                 * 如果是进入的话，当然是希望最后停留在center_crop的区域。但是如果是out的话，就不应该是center_crop的位置了 ，
                 * 而应该是最后变化的位置，因为当out的时候结束时，不回复视图是Normal，要不然会有一个突然闪动回去的bug
                 */
                mTransformListener?.onTransformCompleted(mStatus)
                if (mStatus == Status.STATE_IN) {
                    mStatus = Status.STATE_NORMAL
                }
            }
        })

        animator.start()
    }

    fun transformIn(listener: OnTransformListener? = null) {
        if (listener != null) {
            setOnTransformListener(listener)
        }
        mTransformStart = true
        mStatus = Status.STATE_IN
        invalidate()
    }

    fun transformOut(listener: OnTransformListener? = null) {
        if (top != 0) {
            offsetTopAndBottom(-top)
        }
        if (left != 0) {
            offsetLeftAndRight(-left)
        }
        if (scaleX != 1f) {
            scaleX = 1f
            scaleY = 1f
        }
        if (listener != null) {
            setOnTransformListener(listener)
        }
        mTransformStart = true
        mStatus = Status.STATE_OUT
        invalidate()
    }

    /**
     * 设置起始位置图片的Rect
     * g
     *
     * @param thumbRect 参数
     */
    fun setThumbRect(thumbRect: Rect?) {
        this.mThumbRect = thumbRect
    }

    private fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = 0
        val res = context.resources
        val resourceId = res.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

    private fun initTransformIfNeeded() {
        if (drawable == null) {
            return
        }
        if (mStartTransform != null && mEndTransform != null && mAnimTransform != null) {
            return
        }
        if (width == 0 || height == 0) {
            return
        }
        when (drawable) {
            is BitmapDrawable -> {
                val mBitmap = (drawable as BitmapDrawable).bitmap
                mBitmapWidth = mBitmap.width
                mBitmapHeight = mBitmap.height
            }
            is ColorDrawable -> {
                val colorDrawable = drawable as ColorDrawable
                mBitmapWidth = colorDrawable.intrinsicWidth
                mBitmapHeight = colorDrawable.intrinsicHeight
            }
            else -> {
                mBitmapWidth = drawable.intrinsicWidth
                mBitmapHeight = drawable.intrinsicHeight
            }
        }
        if (mBitmapWidth == 0 || mBitmapHeight == 0) {
            return
        }
        val startTransform = mStartTransform ?: Transform().also { mStartTransform = it }
        startTransform.alpha = 0
        val thumbRect = mThumbRect ?: Rect().also { mThumbRect = it }
        startTransform.left = thumbRect.left.toFloat()
        if (isFullScreen()) {
            startTransform.top = thumbRect.top.toFloat()
        } else {
            startTransform.top = (thumbRect.top - getStatusBarHeight(context)).toFloat()
        }
        startTransform.width = thumbRect.width().toFloat()
        startTransform.height = thumbRect.height().toFloat()
        //开始时以CenterCrop方式显示，缩放图片使图片的一边等于起始区域的一边，另一边大于起始区域
        val startScaleX = thumbRect.width().toFloat() / mBitmapWidth
        val startScaleY = thumbRect.height().toFloat() / mBitmapHeight
        startTransform.scale = if (startScaleX > startScaleY) startScaleX else startScaleY

        //结束时以fitCenter方式显示，缩放图片使图片的一边等于View的一边，另一边大于View
        val endScaleX = width.toFloat() / mBitmapWidth
        val endScaleY = height.toFloat() / mBitmapHeight
        val endTransform = mEndTransform ?: Transform().also { mEndTransform = it }
        endTransform.scale = if (endScaleX < endScaleY) endScaleX else endScaleY
        endTransform.alpha = 255
        val endBitmapWidth = (endTransform.scale * mBitmapWidth).toInt()
        val endBitmapHeight = (endTransform.scale * mBitmapHeight).toInt()
        endTransform.left = ((width - endBitmapWidth) / 2).toFloat()
        endTransform.top = ((height - endBitmapHeight) / 2).toFloat()
        endTransform.width = endBitmapWidth.toFloat()
        endTransform.height = endBitmapHeight.toFloat()
        if (mStatus == Status.STATE_IN) {
            mAnimTransform = startTransform.copy()
        } else if (mStatus == Status.STATE_OUT) {
            mAnimTransform = endTransform.copy()
        }
        mMarkTransform = endTransform
    }

    fun setAlphaChangeListener(alphaChangeListener: OnAlphaChangeListener?) {
        this.mAlphaChangeListener = alphaChangeListener
    }

    interface OnAlphaChangeListener {
        fun onAlphaChange(alpha: Int)
    }

    fun setOnTransformListener(onTransformListener: OnTransformListener?) {
        mTransformListener = onTransformListener
    }

    interface OnTransformListener {
        fun onTransformCompleted(status: Status?)
    }

    private data class Transform(
            var left: Float = 0f,
            var top: Float = 0f,
            var width: Float = 0f,
            var height: Float = 0f,
            var alpha: Int = 0,
            var scale: Float = 0f,
    )

    /***
     * 设置图片拖拽返回
     * @param draggable  true  可以 false 默认 true
     */
    fun setDraggable(draggable: Boolean, sensitivity: Float) {
        mDraggable = draggable
        mMaxTransScale = sensitivity
    }
}