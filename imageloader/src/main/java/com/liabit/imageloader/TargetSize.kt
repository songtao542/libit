package com.liabit.imageloader

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.math.max

class TargetSize(target: Any?) {
    companion object {
        private const val TAG = "ViewSize"
        private const val PENDING_SIZE = 0
        private var maxDisplayLength: Int? = null
    }

    private var mTarget: WeakReference<Any>? = null

    init {
        mTarget = WeakReference(target)
    }

    suspend fun getSize(): Point? = withTimeoutOrNull(3000) {
        suspendCancellableCoroutine {
            val displayTarget = mTarget?.get()
            if (displayTarget is View) {
                val currentWidth = getTargetWidth(displayTarget)
                val currentHeight = getTargetHeight(displayTarget)
                if (isViewStateAndSizeValid(currentWidth, currentHeight)) {
                    it.resume(Point(currentWidth, currentHeight))
                } else {
                    val observer = displayTarget.viewTreeObserver
                    if (observer != null && observer.isAlive) {
                        observer.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                            private var mInvokeCount = AtomicInteger(0)
                            override fun onPreDraw(): Boolean {
                                val count = mInvokeCount.getAndIncrement()
                                val width = displayTarget.width
                                val height = displayTarget.height
                                Log.d(TAG, "getTargetSize invoke count: $count  width: $width  height: $height")
                                if (observer.isAlive) {
                                    observer.removeOnPreDrawListener(this)
                                }
                                if (count == 0) {
                                    it.resume(Point(width, height))
                                }
                                return true
                            }
                        })
                    } else {
                        it.resume(null)
                    }
                }
            } else if (displayTarget is ImageLoader.DisplayTarget) {
                it.resume(displayTarget.getSize())
            } else {
                it.resume(null)
            }
        }
    }

    private fun isViewStateAndSizeValid(width: Int, height: Int): Boolean {
        return width > 0 && height > 0
    }

    private fun getTargetHeight(view: View): Int {
        val verticalPadding: Int = view.paddingTop + view.paddingBottom
        val layoutParams = view.layoutParams
        val layoutParamSize = layoutParams?.height ?: PENDING_SIZE
        return getTargetDimen(view, view.height, layoutParamSize, verticalPadding)
    }

    private fun getTargetWidth(view: View): Int {
        val horizontalPadding: Int = view.paddingLeft + view.paddingRight
        val layoutParams = view.layoutParams
        val layoutParamSize = layoutParams?.width ?: PENDING_SIZE
        return getTargetDimen(view, view.width, layoutParamSize, horizontalPadding)
    }

    private fun getTargetDimen(view: View, viewSize: Int, paramSize: Int, paddingSize: Int): Int {
        val adjustedParamSize = paramSize - paddingSize
        if (adjustedParamSize > 0) {
            return adjustedParamSize
        }
        if (view.isLayoutRequested) {
            return PENDING_SIZE
        }
        val adjustedViewSize = viewSize - paddingSize
        if (adjustedViewSize > 0) {
            return adjustedViewSize
        }
        if (!view.isLayoutRequested && paramSize == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return getMaxDisplayLength(view.context)
        }
        return PENDING_SIZE
    }

    private fun getMaxDisplayLength(context: Context): Int {
        if (maxDisplayLength == null) {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display ?: windowManager.defaultDisplay
            } else {
                windowManager.defaultDisplay
            }
            val displayDimensions = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val metrics = windowManager.currentWindowMetrics
                displayDimensions.x = metrics.bounds.width()
                displayDimensions.y = metrics.bounds.height()
            } else {
                display?.getSize(displayDimensions)
            }
            maxDisplayLength = max(displayDimensions.x, displayDimensions.y)
        }
        return maxDisplayLength ?: 0
    }
}