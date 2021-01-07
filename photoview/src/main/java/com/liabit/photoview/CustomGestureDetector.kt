package com.liabit.photoview

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.VelocityTracker
import android.view.ViewConfiguration
import java.lang.Float.isInfinite
import java.lang.Float.isNaN
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Does a whole lot of gesture detecting.
 */
internal class CustomGestureDetector(context: Context, listener: OnGestureListener) {
    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    private var mActivePointerId = INVALID_POINTER_ID
    private var mActivePointerIndex = 0
    private val mDetector: ScaleGestureDetector
    private var mVelocityTracker: VelocityTracker? = null
    private var mIsDragging = false
    private var mLastTouchX = 0f
    private var mLastTouchY = 0f
    private val mTouchSlop: Float
    private val mMinimumVelocity: Float
    private val mListener: OnGestureListener

    init {
        val configuration = ViewConfiguration.get(context)
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
        mTouchSlop = configuration.scaledTouchSlop.toFloat()
        mListener = listener
        mDetector = ScaleGestureDetector(context, object : OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                if (isNaN(scaleFactor) || isInfinite(scaleFactor)) return false
                if (scaleFactor >= 0) {
                    mListener.onScale(scaleFactor, detector.focusX, detector.focusY)
                }
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) { // NO-OP

            }
        })
    }

    private fun getActiveX(ev: MotionEvent): Float {
        return try {
            ev.getX(mActivePointerIndex)
        } catch (e: Exception) {
            ev.x
        }
    }

    private fun getActiveY(ev: MotionEvent): Float {
        return try {
            ev.getY(mActivePointerIndex)
        } catch (e: Exception) {
            ev.y
        }
    }

    val isScaling: Boolean
        get() = mDetector.isInProgress

    val isDragging: Boolean
        get() = mIsDragging

    fun onTouchEvent(ev: MotionEvent): Boolean {
        return try {
            mDetector.onTouchEvent(ev)
            processTouchEvent(ev)
        } catch (e: IllegalArgumentException) { // Fix for support lib bug, happening when onDestroy is called
            true
        }
    }

    private fun processTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = ev.getPointerId(0)
                mVelocityTracker = VelocityTracker.obtain()
                mVelocityTracker?.addMovement(ev)
                mLastTouchX = getActiveX(ev)
                mLastTouchY = getActiveY(ev)
                mIsDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                val x = getActiveX(ev)
                val y = getActiveY(ev)
                val dx = x - mLastTouchX
                val dy = y - mLastTouchY
                if (!isDragging) {
                    // Use Pythagoras to see if drag length is larger than touch slop
                    mIsDragging = sqrt(dx * dx + (dy * dy).toDouble()) >= mTouchSlop
                }
                if (isDragging) {
                    if (!isScaling) {
                        mListener.onDrag(dx, dy)
                    }
                    mLastTouchX = x
                    mLastTouchY = y
                    mVelocityTracker?.addMovement(ev)
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
                // Recycle Velocity Tracker
                mVelocityTracker?.recycle()
                mVelocityTracker = null
            }
            MotionEvent.ACTION_UP -> {
                mActivePointerId = INVALID_POINTER_ID
                if (isDragging) {
                    if (null != mVelocityTracker) {
                        mLastTouchX = getActiveX(ev)
                        mLastTouchY = getActiveY(ev)
                        mVelocityTracker?.let {
                            // Compute velocity within the last 1000ms
                            it.addMovement(ev)
                            it.computeCurrentVelocity(1000)
                            val vX = it.xVelocity
                            val vY = it.yVelocity
                            // If the velocity is greater than minVelocity, call listener
                            if (max(abs(vX), abs(vY)) >= mMinimumVelocity) {
                                mListener.onFling(mLastTouchX, mLastTouchY, -vX, -vY)
                            }
                        }
                    }
                }
                // Recycle Velocity Tracker
                mVelocityTracker?.recycle()
                mVelocityTracker = null
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex: Int = Util.getPointerIndex(ev.action)
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mActivePointerId = ev.getPointerId(newPointerIndex)
                    mLastTouchX = ev.getX(newPointerIndex)
                    mLastTouchY = ev.getY(newPointerIndex)
                }
            }
        }
        mActivePointerIndex =
            ev.findPointerIndex(if (mActivePointerId != INVALID_POINTER_ID) mActivePointerId else 0)
        return true
    }


}