package com.liabit.photoview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.Matrix.ScaleToFit
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.OnDoubleTapListener
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.OverScroller
import com.liabit.photoview.*
import kotlin.math.*

/**
 * The component of [PhotoView] which does the work allowing for zooming, scaling, panning, etc.
 * It is made public in case you need to subclass something other than AppCompatImageView and still
 * gain the functionality that [PhotoView] offers
 */
@Suppress("LeakingThis")
@SuppressLint("ClickableViewAccessibility")
open class PhotoViewAttacher(private val mImageView: ImageView) : OnTouchListener, View.OnLayoutChangeListener {

    companion object {
        private const val DEFAULT_MAX_SCALE: Float = 3.0f
        private const val DEFAULT_MID_SCALE: Float = 1.75f
        private const val DEFAULT_MIN_SCALE: Float = 1.0f
        private const val DEFAULT_ZOOM_DURATION: Int = 200
        private const val HORIZONTAL_EDGE_NONE: Int = -1
        private const val HORIZONTAL_EDGE_LEFT: Int = 0
        private const val HORIZONTAL_EDGE_RIGHT: Int = 1
        private const val HORIZONTAL_EDGE_BOTH: Int = 2
        private const val VERTICAL_EDGE_NONE: Int = -1
        private const val VERTICAL_EDGE_TOP: Int = 0
        private const val VERTICAL_EDGE_BOTTOM: Int = 1
        private const val VERTICAL_EDGE_BOTH: Int = 2
        private const val SINGLE_TOUCH: Int = 1
    }

    private var mInterpolator: Interpolator = AccelerateDecelerateInterpolator()
    private var mZoomDuration: Int = DEFAULT_ZOOM_DURATION
    private var mMinScale: Float = DEFAULT_MIN_SCALE
    private var mMidScale: Float = DEFAULT_MID_SCALE
    private var mMaxScale: Float = DEFAULT_MAX_SCALE
    private var mAllowParentInterceptOnEdge: Boolean = true
    private var mBlockParentIntercept: Boolean = false

    // Gesture Detectors
    private val mGestureDetector: GestureDetector
    private val mScaleDragDetector: CustomGestureDetector

    // These are set so we don't keep allocating them on the heap
    private val mBaseMatrix = Matrix()
    private val mDrawMatrix = Matrix()
    private val mSuppMatrix = Matrix()
    private val mDisplayRect: RectF = RectF()
    private val mMatrixValues: FloatArray = FloatArray(9)

    // Listeners
    private var mMatrixChangeListener: OnMatrixChangedListener? = null
    private var mPhotoTapListener: OnPhotoTapListener? = null
    private var mOutsidePhotoTapListener: OnOutsidePhotoTapListener? = null
    private var mViewTapListener: OnViewTapListener? = null
    private var mOnClickListener: View.OnClickListener? = null
    private var mLongClickListener: OnLongClickListener? = null
    private var mScaleChangeListener: OnScaleChangedListener? = null
    private var mSingleFlingListener: OnSingleFlingListener? = null
    private var mOnViewDragListener: OnViewDragListener? = null
    private var mCurrentFlingRunnable: FlingRunnable? = null
    private var mHorizontalScrollEdge: Int = HORIZONTAL_EDGE_BOTH
    private var mVerticalScrollEdge: Int = VERTICAL_EDGE_BOTH
    private var mBaseRotation: Float
    private var isZoomEnabled: Boolean = true

    private var mScaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER

    private val mOnGestureListener: OnGestureListener

    init {
        mImageView.setOnTouchListener(this)
        mImageView.addOnLayoutChangeListener(this)
        mBaseRotation = 0.0f
        mOnGestureListener = object : OnGestureListener {
            override fun onDrag(dx: Float, dy: Float) {
                mOnViewDragListener?.onDrag(dx, dy)
                mSuppMatrix.postTranslate(dx, dy)
                checkAndDisplayMatrix()
                val parent = mImageView.parent
                if (mAllowParentInterceptOnEdge && !mBlockParentIntercept) {
                    if (((mHorizontalScrollEdge == HORIZONTAL_EDGE_BOTH
                                    ) || (mHorizontalScrollEdge == HORIZONTAL_EDGE_LEFT && dx >= 1f)
                                    || (mHorizontalScrollEdge == HORIZONTAL_EDGE_RIGHT && dx <= -1f)
                                    || (mVerticalScrollEdge == VERTICAL_EDGE_TOP && dy >= 1f)
                                    || (mVerticalScrollEdge == VERTICAL_EDGE_BOTTOM && dy <= -1f))
                    ) {
                        parent?.requestDisallowInterceptTouchEvent(false)
                    }
                } else {
                    parent?.requestDisallowInterceptTouchEvent(true)
                }
            }

            override fun onFling(startX: Float, startY: Float, velocityX: Float, velocityY: Float) {
                mCurrentFlingRunnable = FlingRunnable(mImageView.context)
                mCurrentFlingRunnable?.fling(
                        getImageViewWidth(mImageView),
                        getImageViewHeight(mImageView), velocityX.toInt(), velocityY.toInt()
                )
                mImageView.post(mCurrentFlingRunnable)
            }

            override fun onScale(scaleFactor: Float, focusX: Float, focusY: Float) {
                if (scale < mMaxScale || scaleFactor < 1f) {
                    mImageView.parent?.requestDisallowInterceptTouchEvent(true)
                    mScaleChangeListener?.onScaleChange(scaleFactor, focusX, focusY)
                    mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
                    checkAndDisplayMatrix()
                }
            }
        }
        // Create Gesture Detectors...
        mScaleDragDetector = CustomGestureDetector(mImageView.context, mOnGestureListener)
        mGestureDetector = GestureDetector(mImageView.context, object : SimpleOnGestureListener() {
            // forward long click listener
            override fun onLongPress(e: MotionEvent) {
                mLongClickListener?.onLongClick(mImageView)
            }

            override fun onFling(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float,
            ): Boolean {
                if (mSingleFlingListener != null) {
                    if (scale > DEFAULT_MIN_SCALE) {
                        return false
                    }
                    if ((e1.pointerCount > SINGLE_TOUCH || e2.pointerCount > SINGLE_TOUCH)) {
                        return false
                    }
                    return mSingleFlingListener?.onFling(e1, e2, velocityX, velocityY)
                            ?: false
                }
                return false
            }
        })
        mGestureDetector.setOnDoubleTapListener(object : OnDoubleTapListener {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                mOnClickListener?.onClick(mImageView)
                val x: Float = e.x
                val y: Float = e.y
                mViewTapListener?.onViewTap(mImageView, x, y)
                displayRect?.let {
                    // Check to see if the user tapped on the photo
                    if (it.contains(x, y)) {
                        val xResult: Float = ((x - it.left) / it.width())
                        val yResult: Float = ((y - it.top) / it.height())
                        mPhotoTapListener?.onPhotoTap(mImageView, xResult, yResult)
                        return true
                    } else {
                        mOutsidePhotoTapListener?.onOutsidePhotoTap(mImageView)
                    }
                }
                return false
            }

            override fun onDoubleTap(ev: MotionEvent): Boolean {
                try {
                    val scale = scale
                    val x = ev.x
                    val y = ev.y
                    if (scale < mediumScale) {
                        setScale(mediumScale, x, y, true)
                    } else if (scale >= mediumScale && scale < maximumScale) {
                        setScale(maximumScale, x, y, true)
                    } else {
                        setScale(minimumScale, x, y, true)
                    }
                } catch (e: Exception) { // Can sometimes happen when getX() and getY() is called
                    Log.e("PhotoViewAttacher", "onDoubleTap ", e)
                }
                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent): Boolean { // Wait for the confirmed onDoubleTap() instead
                return false
            }
        })
    }

    fun setOnDoubleTapListener(newOnDoubleTapListener: OnDoubleTapListener?) {
        mGestureDetector.setOnDoubleTapListener(newOnDoubleTapListener)
    }

    fun setOnScaleChangeListener(onScaleChangeListener: OnScaleChangedListener?) {
        mScaleChangeListener = onScaleChangeListener
    }

    fun setOnSingleFlingListener(onSingleFlingListener: OnSingleFlingListener?) {
        mSingleFlingListener = onSingleFlingListener
    }

    val displayRect: RectF?
        get() {
            checkMatrixBounds()
            return getDisplayRect(drawMatrix)
        }

    fun setDisplayMatrix(finalMatrix: Matrix): Boolean {
        if (mImageView.drawable == null) {
            return false
        }
        mSuppMatrix.set(finalMatrix)
        checkAndDisplayMatrix()
        return true
    }

    @Suppress("unused")
    fun setBaseRotation(degrees: Float) {
        mBaseRotation = degrees % 360
        update()
        setRotationBy(mBaseRotation)
        checkAndDisplayMatrix()
    }

    fun setRotationTo(degrees: Float) {
        mSuppMatrix.setRotate(degrees % 360)
        checkAndDisplayMatrix()
    }

    fun setRotationBy(degrees: Float) {
        mSuppMatrix.postRotate(degrees % 360)
        checkAndDisplayMatrix()
    }

    val minimumScale: Float get() = mMinScale

    fun setMinimumScale(minimumScale: Float) {
        Util.checkZoomLevels(minimumScale, mMidScale, mMaxScale)
        mMinScale = minimumScale
    }

    val mediumScale: Float get() = mMidScale

    fun setMediumScale(mediumScale: Float) {
        Util.checkZoomLevels(mMinScale, mediumScale, mMaxScale)
        mMidScale = mediumScale
    }

    val maximumScale: Float get() = mMaxScale

    fun setMaximumScale(maximumScale: Float) {
        Util.checkZoomLevels(mMinScale, mMidScale, maximumScale)
        mMaxScale = maximumScale
    }

    val scale: Float
        get() = sqrt(
                getValue(mSuppMatrix, Matrix.MSCALE_X).toDouble().pow(2.0)
                        + getValue(mSuppMatrix, Matrix.MSKEW_Y).toDouble().pow(2.0)
        ).toFloat()


    fun setScale(scale: Float) {
        setScale(scale, false)
    }

    val scaleType: ImageView.ScaleType get() = mScaleType

    fun setScaleType(scaleType: ImageView.ScaleType) {
        if (Util.isSupportedScaleType(scaleType) && scaleType != mScaleType) {
            mScaleType = scaleType
            update()
        }
    }

    override fun onLayoutChange(
            v: View,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int,
    ) { // Update our base matrix, as the bounds have changed
        if ((left != oldLeft) || (top != oldTop) || (right != oldRight) || (bottom != oldBottom)) {
            updateBaseMatrix(mImageView.drawable)
        }
    }

    override fun onTouch(v: View, ev: MotionEvent): Boolean {
        var handled = false
        if (isZoomEnabled && Util.hasDrawable(v as ImageView)) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    // First, disable the Parent from intercepting the touch event
                    v.getParent()?.requestDisallowInterceptTouchEvent(true)
                    // If we're flinging, and the user presses down, cancel fling
                    cancelFling()
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP ->
                    // If the user has zoomed less than min scale, zoom back to min scale
                    if (scale < mMinScale) {
                        displayRect?.let {
                            v.post(
                                    AnimatedZoomRunnable(
                                            scale,
                                            mMinScale,
                                            it.centerX(),
                                            it.centerY()
                                    )
                            )
                        }
                    } else if (scale > mMaxScale) {
                        displayRect?.let {
                            v.post(
                                    AnimatedZoomRunnable(
                                            scale,
                                            mMaxScale,
                                            it.centerX(),
                                            it.centerY()
                                    )
                            )
                        }
                    }
            }
            // Try the Scale/Drag detector
            handled = mScaleDragDetector.onTouchEvent(ev)
            val didntScale: Boolean = !mScaleDragDetector.isScaling
            val didntDrag: Boolean = !mScaleDragDetector.isDragging
            mBlockParentIntercept = didntScale && didntDrag
            // Check to see if the user double tapped
            if (mGestureDetector.onTouchEvent(ev)) {
                handled = true
            }
        }
        return handled
    }

    fun setAllowParentInterceptOnEdge(allow: Boolean) {
        mAllowParentInterceptOnEdge = allow
    }

    fun setScaleLevels(minimumScale: Float, mediumScale: Float, maximumScale: Float) {
        try {
            Util.checkZoomLevels(minimumScale, mediumScale, maximumScale)
            mMinScale = minimumScale
            mMidScale = mediumScale
            mMaxScale = maximumScale
        } catch (e: Throwable) {
            Log.w("PhotoViewAttacher", "setScaleLevels error", e)
        }
    }

    fun setOnLongClickListener(listener: OnLongClickListener?) {
        mLongClickListener = listener
    }

    fun setOnClickListener(listener: View.OnClickListener?) {
        mOnClickListener = listener
    }

    fun setOnMatrixChangeListener(listener: OnMatrixChangedListener?) {
        mMatrixChangeListener = listener
    }

    fun setOnPhotoTapListener(listener: OnPhotoTapListener?) {
        mPhotoTapListener = listener
    }

    fun setOnOutsidePhotoTapListener(mOutsidePhotoTapListener: OnOutsidePhotoTapListener?) {
        this.mOutsidePhotoTapListener = mOutsidePhotoTapListener
    }

    fun setOnViewTapListener(listener: OnViewTapListener?) {
        mViewTapListener = listener
    }

    fun setOnViewDragListener(listener: OnViewDragListener?) {
        mOnViewDragListener = listener
    }

    fun setScale(scale: Float, animate: Boolean) {
        setScale(scale, mImageView.right.toFloat() / 2f, mImageView.bottom.toFloat() / 2f, animate)
    }

    fun setScale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        // Check to see if the scale is within bounds
        if (scale < mMinScale || scale > mMaxScale) {
            Log.d("PhotoViewAttacher", "Scale must be within the range of minScale and maxScale")
            return
        }
        if (animate) {
            mImageView.post(AnimatedZoomRunnable(this.scale, scale, focalX, focalY))
        } else {
            mSuppMatrix.setScale(scale, scale, focalX, focalY)
            checkAndDisplayMatrix()
        }
    }

    /**
     * Set the zoom interpolator
     *
     * @param interpolator the zoom interpolator
     */
    @Suppress("unused")
    fun setZoomInterpolator(interpolator: Interpolator) {
        mInterpolator = interpolator
    }

    val isZoomable: Boolean get() = isZoomEnabled

    fun setZoomable(zoomable: Boolean) {
        this.isZoomEnabled = zoomable
        update()
    }

    fun update() {
        if (isZoomEnabled) { // Update the base matrix using the current drawable
            updateBaseMatrix(mImageView.drawable)
        } else { // Reset the Matrix...
            resetMatrix()
        }
    }

    /**
     * Get the display matrix
     *
     * @param matrix target matrix to copy to
     */
    fun getDisplayMatrix(matrix: Matrix) {
        matrix.set(drawMatrix)
    }

    /**
     * Get the current support matrix
     */
    fun getSuppMatrix(matrix: Matrix) {
        matrix.set(mSuppMatrix)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val drawMatrix: Matrix
        get() {
            this.mDrawMatrix.set(mBaseMatrix)
            this.mDrawMatrix.postConcat(mSuppMatrix)
            return this.mDrawMatrix
        }

    val imageMatrix: Matrix get() = mDrawMatrix

    fun setZoomTransitionDuration(milliseconds: Int) {
        mZoomDuration = milliseconds
    }

    /**
     * Helper method that 'unpacks' a Matrix and returns the required value
     *
     * @param matrix     Matrix to unpack
     * @param whichValue Which value from Matrix.M* to return
     * @return returned value
     */
    private fun getValue(matrix: Matrix, whichValue: Int): Float {
        matrix.getValues(mMatrixValues)
        return mMatrixValues[whichValue]
    }

    /**
     * Resets the Matrix back to FIT_CENTER, and then displays its contents
     */
    private fun resetMatrix() {
        mSuppMatrix.reset()
        setRotationBy(mBaseRotation)
        setImageViewMatrix(drawMatrix)
        checkMatrixBounds()
    }

    private fun setImageViewMatrix(matrix: Matrix) {
        mImageView.imageMatrix = matrix
        // Call MatrixChangedListener if needed
        if (mMatrixChangeListener != null) {
            getDisplayRect(matrix)?.let {
                mMatrixChangeListener?.onMatrixChanged(it)
            }
        }
    }

    /**
     * Helper method that simply checks the Matrix, and then displays the result
     */
    private fun checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(drawMatrix)
        }
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    protected open fun getDisplayRect(matrix: Matrix): RectF? {
        val d = mImageView.drawable
        if (d != null) {
            mDisplayRect.set(0f, 0f, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())
            matrix.mapRect(mDisplayRect)
            return mDisplayRect
        }
        return null
    }

    /**
     * Calculate Matrix for FIT_CENTER
     *
     * @param drawable - Drawable being displayed
     */
    private fun updateBaseMatrix(drawable: Drawable?) {
        if (drawable == null) {
            return
        }
        val viewWidth = getImageViewWidth(mImageView).toFloat()
        val viewHeight = getImageViewHeight(mImageView).toFloat()
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        mBaseMatrix.reset()
        val widthScale = viewWidth / drawableWidth
        val heightScale = viewHeight / drawableHeight
        @Suppress("CascadeIf")
        if (mScaleType == ImageView.ScaleType.CENTER) {
            mBaseMatrix.postTranslate(
                    (viewWidth - drawableWidth) / 2f,
                    (viewHeight - drawableHeight) / 2f
            )
        } else if (mScaleType == ImageView.ScaleType.CENTER_CROP) {
            val scale = max(widthScale, heightScale)
            mBaseMatrix.postScale(scale, scale)
            mBaseMatrix.postTranslate(
                    (viewWidth - drawableWidth * scale) / 2f,
                    (viewHeight - drawableHeight * scale) / 2f
            )
        } else if (mScaleType == ImageView.ScaleType.CENTER_INSIDE) {
            val scale = min(1.0f, min(widthScale, heightScale))
            mBaseMatrix.postScale(scale, scale)
            mBaseMatrix.postTranslate(
                    (viewWidth - drawableWidth * scale) / 2f,
                    (viewHeight - drawableHeight * scale) / 2f
            )
        } else {
            var mTempSrc = RectF(0f, 0f, drawableWidth.toFloat(), drawableHeight.toFloat())
            val mTempDst = RectF(0f, 0f, viewWidth, viewHeight)
            if (mBaseRotation.toInt() % 180 != 0) {
                mTempSrc = RectF(0f, 0f, drawableHeight.toFloat(), drawableWidth.toFloat())
            }
            when (mScaleType) {
                ImageView.ScaleType.FIT_CENTER -> mBaseMatrix.setRectToRect(
                        mTempSrc,
                        mTempDst,
                        ScaleToFit.CENTER
                )
                ImageView.ScaleType.FIT_START -> mBaseMatrix.setRectToRect(
                        mTempSrc,
                        mTempDst,
                        ScaleToFit.START
                )
                ImageView.ScaleType.FIT_END -> mBaseMatrix.setRectToRect(
                        mTempSrc,
                        mTempDst,
                        ScaleToFit.END
                )
                ImageView.ScaleType.FIT_XY -> mBaseMatrix.setRectToRect(
                        mTempSrc,
                        mTempDst,
                        ScaleToFit.FILL
                )
                ImageView.ScaleType.MATRIX -> {
                }
                ImageView.ScaleType.CENTER -> {
                }
                ImageView.ScaleType.CENTER_CROP -> {
                }
                ImageView.ScaleType.CENTER_INSIDE -> {
                }
            }
        }
        resetMatrix()
    }

    private fun checkMatrixBounds(): Boolean {
        val rect = getDisplayRect(drawMatrix) ?: return false
        val height = rect.height()
        val width = rect.width()
        var deltaX = 0f
        var deltaY = 0f
        val viewHeight = getImageViewHeight(mImageView)
        when {
            height <= viewHeight -> {
                deltaY = when (mScaleType) {
                    ImageView.ScaleType.FIT_START -> -rect.top
                    ImageView.ScaleType.FIT_END -> viewHeight - height - rect.top
                    else -> (viewHeight - height) / 2 - rect.top
                }
                mVerticalScrollEdge = VERTICAL_EDGE_BOTH
            }
            rect.top > 0 -> {
                mVerticalScrollEdge = VERTICAL_EDGE_TOP
                deltaY = -rect.top
            }
            rect.bottom < viewHeight -> {
                mVerticalScrollEdge = VERTICAL_EDGE_BOTTOM
                deltaY = viewHeight - rect.bottom
            }
            else -> {
                mVerticalScrollEdge = VERTICAL_EDGE_NONE
            }
        }
        val viewWidth = getImageViewWidth(mImageView)
        when {
            width <= viewWidth -> {
                deltaX = when (mScaleType) {
                    ImageView.ScaleType.FIT_START -> -rect.left
                    ImageView.ScaleType.FIT_END -> viewWidth - width - rect.left
                    else -> (viewWidth - width) / 2 - rect.left
                }
                mHorizontalScrollEdge = HORIZONTAL_EDGE_BOTH
            }
            rect.left > 0 -> {
                mHorizontalScrollEdge = HORIZONTAL_EDGE_LEFT
                deltaX = -rect.left
            }
            rect.right < viewWidth -> {
                deltaX = viewWidth - rect.right
                mHorizontalScrollEdge = HORIZONTAL_EDGE_RIGHT
            }
            else -> {
                mHorizontalScrollEdge = HORIZONTAL_EDGE_NONE
            }
        }
        // Finally actually translate the matrix
        mSuppMatrix.postTranslate(deltaX, deltaY)
        return true
    }

    private fun getImageViewWidth(imageView: ImageView): Int {
        return imageView.width - imageView.paddingLeft - imageView.paddingRight
    }

    private fun getImageViewHeight(imageView: ImageView): Int {
        return imageView.height - imageView.paddingTop - imageView.paddingBottom
    }

    private fun cancelFling() {
        mCurrentFlingRunnable?.cancelFling()
        mCurrentFlingRunnable = null
    }

    private inner class AnimatedZoomRunnable(
            private val start: Float,
            private val target: Float,
            private val mFocalX: Float,
            private val mFocalY: Float,
    ) : Runnable {
        private val mStartTime: Long = System.currentTimeMillis()
        override fun run() {
            val t = interpolate()
            val scale = start + t * (target - start)
            val deltaScale = scale / this@PhotoViewAttacher.scale
            mOnGestureListener.onScale(deltaScale, mFocalX, mFocalY)
            // We haven't hit our target scale yet, so post ourselves again
            if (t < 1f) {
                Util.postOnAnimation(mImageView, this)
            }
        }

        private fun interpolate(): Float {
            var t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration
            t = min(1f, t)
            t = mInterpolator.getInterpolation(t)
            return t
        }

    }

    private inner class FlingRunnable(context: Context?) :
            Runnable {
        private val mScroller: OverScroller = OverScroller(context)
        private var mCurrentX: Int = 0
        private var mCurrentY: Int = 0
        fun cancelFling() {
            mScroller.forceFinished(true)
        }

        fun fling(
                viewWidth: Int, viewHeight: Int, velocityX: Int,
                velocityY: Int,
        ) {
            val rect: RectF = displayRect ?: return
            val startX: Int = (-rect.left).roundToInt()
            val minX: Int
            val maxX: Int
            val minY: Int
            val maxY: Int
            if (viewWidth < rect.width()) {
                minX = 0
                maxX = (rect.width() - viewWidth).roundToInt()
            } else {
                maxX = startX
                minX = maxX
            }
            val startY: Int = (-rect.top).roundToInt()
            if (viewHeight < rect.height()) {
                minY = 0
                maxY = (rect.height() - viewHeight).roundToInt()
            } else {
                maxY = startY
                minY = maxY
            }
            mCurrentX = startX
            mCurrentY = startY
            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
                mScroller.fling(
                        startX, startY, velocityX, velocityY, minX,
                        maxX, minY, maxY, 0, 0
                )
            }
        }

        override fun run() {
            if (mScroller.isFinished) {
                return  // remaining post that should not be handled
            }
            if (mScroller.computeScrollOffset()) {
                val newX: Int = mScroller.currX
                val newY: Int = mScroller.currY
                mSuppMatrix.postTranslate(mCurrentX - newX.toFloat(), mCurrentY - newY.toFloat())
                checkAndDisplayMatrix()
                mCurrentX = newX
                mCurrentY = newY
                // Post On animation
                Util.postOnAnimation(mImageView, this)
            }
        }

    }
}
