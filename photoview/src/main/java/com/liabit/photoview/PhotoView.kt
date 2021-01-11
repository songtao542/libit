package com.liabit.photoview

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector.OnDoubleTapListener
import androidx.appcompat.widget.AppCompatImageView

/**
 * A zoomable ImageView. See [PhotoViewAttacher] for most of the details on how the zooming
 * is accomplished
 */
@Suppress("unused")
open class PhotoView : AppCompatImageView {
    private var mAttacher: PhotoViewAttacher? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
        init(context)
    }

    constructor(context: Context, attr: AttributeSet?, defStyle: Int)
            : super(context, attr, defStyle) {
        init(context)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun init(context: Context) {
        super.setScaleType(ScaleType.MATRIX)
        mAttacher = PhotoViewAttacher(this)
    }

    /**
     * Get the current [PhotoViewAttacher] for this view. Be wary of holding on to references
     * to this attacher, as it has a reference to this view, which, if a reference is held in the
     * wrong place, can cause memory leaks.
     *
     * @return the attacher.
     */
    fun getAttacher(): PhotoViewAttacher? {
        return mAttacher
    }

    override fun getScaleType(): ScaleType {
        return mAttacher?.scaleType ?: ScaleType.FIT_CENTER
    }

    override fun getImageMatrix(): Matrix {
        return mAttacher?.imageMatrix ?: Matrix()
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        mAttacher?.setOnLongClickListener(l)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mAttacher?.setOnClickListener(l)
    }

    protected fun setSuperOnClickListener(listener: OnClickListener?) {
        super.setOnClickListener(listener)
    }

    override fun setScaleType(scaleType: ScaleType) {
        mAttacher?.setScaleType(scaleType)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        // setImageBitmap calls through to this method
        mAttacher?.update()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        mAttacher?.update()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        mAttacher?.update()
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val changed = super.setFrame(l, t, r, b)
        if (changed) {
            mAttacher?.update()
        }
        return changed
    }

    fun setRotationTo(rotationDegree: Float) {
        mAttacher?.setRotationTo(rotationDegree)
    }

    fun setRotationBy(rotationDegree: Float) {
        mAttacher?.setRotationBy(rotationDegree)
    }

    var isZoomable: Boolean
        get() = mAttacher?.isZoomable ?: false
        set(zoomable) {
            mAttacher?.setZoomable(zoomable)
        }

    val displayRect: RectF? get() = mAttacher?.displayRect

    fun getDisplayMatrix(matrix: Matrix) {
        mAttacher?.getDisplayMatrix(matrix)
    }

    fun setDisplayMatrix(finalRectangle: Matrix): Boolean {
        return mAttacher?.setDisplayMatrix(finalRectangle) ?: false
    }

    fun getSuppMatrix(matrix: Matrix) {
        mAttacher?.getSuppMatrix(matrix)
    }

    fun setSuppMatrix(matrix: Matrix): Boolean {
        return mAttacher?.setDisplayMatrix(matrix) ?: false
    }

    val minimumScale: Float get() = mAttacher?.minimumScale ?: 1f

    fun setMinimumScale(minimumScale: Float) {
        mAttacher?.setMinimumScale(minimumScale)
    }

    val mediumScale: Float get() = mAttacher?.mediumScale ?: 1f

    fun setMediumScale(mediumScale: Float) {
        mAttacher?.setMediumScale(mediumScale)
    }

    val maximumScale: Float
        get() = mAttacher?.maximumScale ?: 1f

    fun setMaximumScale(maximumScale: Float) {
        mAttacher?.setMaximumScale(maximumScale)
    }

    val scale: Float get() = mAttacher?.scale ?: 1f

    fun setScale(scale: Float) {
        mAttacher?.setScale(scale)
    }

    fun setAllowParentInterceptOnEdge(allow: Boolean) {
        mAttacher?.setAllowParentInterceptOnEdge(allow)
    }

    fun setScaleLevels(minimumScale: Float, mediumScale: Float, maximumScale: Float) {
        mAttacher?.setScaleLevels(minimumScale, mediumScale, maximumScale)
    }

    fun setOnMatrixChangeListener(listener: OnMatrixChangedListener?) {
        mAttacher?.setOnMatrixChangeListener(listener)
    }

    fun setOnPhotoTapListener(listener: OnPhotoTapListener?) {
        mAttacher?.setOnPhotoTapListener(listener)
    }

    fun setOnOutsidePhotoTapListener(listener: OnOutsidePhotoTapListener?) {
        mAttacher?.setOnOutsidePhotoTapListener(listener)
    }

    fun setOnViewTapListener(listener: OnViewTapListener?) {
        mAttacher?.setOnViewTapListener(listener)
    }

    fun setOnViewDragListener(listener: OnViewDragListener?) {
        mAttacher?.setOnViewDragListener(listener)
    }

    fun setScale(scale: Float, animate: Boolean) {
        mAttacher?.setScale(scale, animate)
    }

    fun setScale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
        mAttacher?.setScale(scale, focalX, focalY, animate)
    }

    fun setZoomTransitionDuration(milliseconds: Int) {
        mAttacher?.setZoomTransitionDuration(milliseconds)
    }

    fun setOnDoubleTapListener(onDoubleTapListener: OnDoubleTapListener?) {
        mAttacher?.setOnDoubleTapListener(onDoubleTapListener)
    }

    fun setOnScaleChangeListener(onScaleChangedListener: OnScaleChangedListener?) {
        mAttacher?.setOnScaleChangeListener(onScaleChangedListener)
    }

    fun setOnSingleFlingListener(onSingleFlingListener: OnSingleFlingListener?) {
        mAttacher?.setOnSingleFlingListener(onSingleFlingListener)
    }


}
