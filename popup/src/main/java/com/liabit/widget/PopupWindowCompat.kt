package com.liabit.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionValues
import android.util.TypedValue
import android.view.*
import android.view.animation.*
import android.widget.FrameLayout
import android.widget.PopupWindow


@Suppress("unused", "MemberVisibilityCanBePrivate")
class PopupWindowCompat(context: Context) {

    private val mContext: Context = context
    private var mPopupWindow: PopupWindow? = null
    private var mHeight = ViewGroup.LayoutParams.WRAP_CONTENT

    /**
     * -2 == LayoutParams.WRAP_CONTENT
     * -1 == LayoutParams.MATCH_PARENT
     *  0 == Absolutely dpi
     */
    private var mWidth = ViewGroup.LayoutParams.WRAP_CONTENT
    private var mRootView: FrameLayout
    private var mMaskView: View
    private var mContentView: View? = null
    private var mShowMask = false

    init {
        mRootView = FrameLayout(mContext)
        mRootView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        mMaskView = View(mContext)
        mMaskView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        //mMaskView.setBackgroundColor(0x99000000.toInt())
        mMaskView.setBackgroundColor(Color.TRANSPARENT)
        mRootView.addView(mMaskView)
    }

    interface OnDismissListener {
        fun onDismiss()
    }

    private var mOnDismissListener: OnDismissListener? = null

    fun setOnDismissListener(onDismissListener: OnDismissListener?) {
        mOnDismissListener = onDismissListener
    }

    /**
     * @param height 单位 dip
     */
    fun setHeight(height: Int) {
        mHeight = dp2px(height)
    }

    /**
     * 宽度撑满屏幕
     */
    fun setFullScreenWidth() {
        mWidth = mContext.resources.displayMetrics.widthPixels
    }

    /**
     * @param width 单位 dip
     */
    fun setWidth(width: Int) {
        mWidth = dp2px(width)
    }

    fun setShowMask(showMask: Boolean) {
        mShowMask = showMask
    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), mContext.resources.displayMetrics).toInt()
    }

    private fun sp2px(sp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), mContext.resources.displayMetrics).toInt()
    }

    fun setContentView(contentView: View) {
        mContentView = contentView
    }

    fun getRootView(): FrameLayout {
        return mRootView
    }

    private fun createPopupWindow() {
        mContentView?.let {
            (it.parent as? ViewGroup)?.removeView(it)
            mRootView.addView(it)
        }
        mRootView.layoutParams?.width = mWidth
        mMaskView.visibility = if (mShowMask) View.VISIBLE else View.GONE
        val height = if (mShowMask) ViewGroup.LayoutParams.WRAP_CONTENT else mHeight

        if (mShowMask) {
            mContentView?.animation = createContentAnimation()
        }

        mPopupWindow = PopupWindow(mRootView, mWidth, height, true).also {
            it.setBackgroundDrawable(if (mShowMask) ColorDrawable(0x88000000.toInt()) else ColorDrawable(Color.WHITE))
            it.isOutsideTouchable = true
            it.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            it.animationStyle = R.style.PopupMenuAnimation
            it.elevation = if (mShowMask) 0f else dp2px(20).toFloat()
            it.setOnDismissListener {
                mOnDismissListener?.onDismiss()
            }
            setupExitTransition(it)
        }

        mMaskView.setOnClickListener { mPopupWindow?.dismiss() }
    }

    /**
     * @param anchor  the view on which to pin the DripDownListView
     * @param gravity [Gravity] just Gravity.LEFT,Gravity.RIGHT has effect ,other will be as Gravity.CENTER;
     */
    fun show(anchor: View, gravity: Int) {
        show(anchor, gravity, 0)
    }

    /**
     * Display the content view in a popup window anchored to the bottom-left corner of the anchor view offset by the specified x and y coordinates. If there is
     * not enough room on screen to show the popup in its entirety, this method tries to find a parent scroll view to scroll. If no parent scroll view can be
     * scrolled, the bottom-left corner of the popup is pinned at the top left corner of the anchor view.
     *
     *
     * If the view later scrolls to move anchor to a different location, the popup will be moved correspondingly.
     *
     * @param anchor  the view on which to pin the DripDownListView
     * @param gravity [Gravity] just Gravity.LEFT,Gravity.RIGHT has effect ,other will be as Gravity.CENTER
     * @param yOffset    dip
     */
    @SuppressLint("RtlHardcoded")
    fun show(anchor: View, gravity: Int, yOffset: Int) {
        val anchorWidth = anchor.width.toFloat()
        if (mPopupWindow == null) {
            createPopupWindow()
        } else {
            mPopupWindow?.let { setupExitTransition(it) }
            if (mShowMask) {
                mContentView?.animation = createContentAnimation()
            }
        }
        val yOff = dp2px(yOffset)
        when (gravity) {
            Gravity.LEFT -> {
                mPopupWindow?.showAsDropDown(anchor, 0, yOff)
            }
            Gravity.RIGHT -> {
                mPopupWindow?.showAsDropDown(anchor, (anchorWidth - mWidth).toInt(), yOff)
            }
            else -> {
                mPopupWindow?.showAsDropDown(anchor, (anchorWidth - mWidth).toInt() / 2, yOff)
            }
        }
    }

    /**
     * Display the content view in a popup window anchored to the bottom-left corner of the anchor view. If there is not enough room on screen to show the popup
     * in its entirety, this method tries to find a parent scroll view to scroll. If no parent scroll view can be scrolled, the bottom-left corner of the popup
     * is pinned at the top left corner of the anchor view.
     *
     * @param anchor the view on which to pin the DripDownListView
     */
    fun show(anchor: View) {
        if (mPopupWindow == null) {
            createPopupWindow()
        } else {
            mPopupWindow?.let { setupExitTransition(it) }
            if (mShowMask) {
                mContentView?.animation = createContentAnimation()
            }
        }
        mPopupWindow?.showAsDropDown(anchor)
    }

    /**
     * Display the content view in a popup window at the specified location. If the popup window cannot fit on screen, it will be clipped. See
     * android.view.WindowManager.LayoutParams for more information on how gravity and the x and y parameters are related. Specifying a gravity of
     * android.view.Gravity.NO_GRAVITY is similar to specifying Gravity.LEFT | Gravity.TOP.
     *
     * @param parent  a parent view to get the [View.getWindowToken] token from
     * @param gravity the gravity which controls the placement of the popup window
     * @param x       the popup's x location offset
     * @param y       the popup's y location offset
     */
    fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
        if (mPopupWindow == null) {
            createPopupWindow()
        } else {
            mPopupWindow?.let { setupExitTransition(it) }
            if (mShowMask) {
                mContentView?.animation = createContentAnimation()
            }
        }
        mPopupWindow?.showAtLocation(parent, gravity, x, y)
    }

    fun dismiss() {
        mPopupWindow?.dismiss()
    }

    private fun setupExitTransition(popupWindow: PopupWindow) {
        if (mShowMask && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mContentView?.let {
                popupWindow.exitTransition = PopupExitTransition(popupWindow, it)
            }
        }
    }

    private fun createContentAnimation(): Animation {
        val animation = AnimationSet(false)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0f
        )
        translate.interpolator = AccelerateDecelerateInterpolator()
        val alpha = AlphaAnimation(0f, 1f)
        animation.addAnimation(translate)
        animation.addAnimation(alpha)
        animation.duration = 230
        return animation
    }

    class PopupExitTransition(
            private val popupWindow: PopupWindow,
            private val view: View) : Fade(), Transition.TransitionListener {

        override fun onTransitionStart(transition: Transition?) {
            AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f))
                play(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, (-view.height).toFloat()))
                interpolator = AccelerateInterpolator()
                duration = 150
            }.start()
        }

        override fun onTransitionEnd(transition: Transition?) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                popupWindow.exitTransition = null
            }
        }

        override fun onTransitionCancel(transition: Transition?) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                popupWindow.exitTransition = null
            }
        }

        override fun onTransitionPause(transition: Transition?) {
        }

        override fun onTransitionResume(transition: Transition?) {
        }

        override fun onAppear(sceneRoot: ViewGroup, view: View, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
            return createAnimation(view, -1f, 1f)
        }

        override fun onDisappear(sceneRoot: ViewGroup, view: View, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
            return createAnimation(view, 1f, -1f)
        }

        private fun createAnimation(view: View, startAlpha: Float, endAlpha: Float): Animator? {
            if (startAlpha == endAlpha) {
                return null
            }
            view.alpha = startAlpha
            val anim = ValueAnimator.ofFloat(startAlpha, endAlpha)
            anim.addUpdateListener {
                var alpha = it.animatedValue as Float
                if (alpha < 0f) {
                    alpha = 0f
                }
                view.alpha = alpha
            }
            return anim
        }
    }

    private class PopupDialog(context: Context) : Dialog(context) {
        init {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        /**
         * Display the content view in a popup window anchored to the bottom-left corner of the anchor view offset by the specified x and y coordinates. If there is
         * not enough room on screen to show the popup in its entirety, this method tries to find a parent scroll view to scroll. If no parent scroll view can be
         * scrolled, the bottom-left corner of the popup is pinned at the top left corner of the anchor view.
         *
         *
         * If the view later scrolls to move anchor to a different location, the popup will be moved correspondingly.
         */
        fun show(popupList: PopupWindowCompat) {
            popupList.mMaskView.visibility = View.GONE
            var rlp = popupList.mRootView.layoutParams
            if (rlp == null) {
                rlp = ViewGroup.LayoutParams(popupList.mWidth, popupList.mHeight)
            } else {
                rlp.width = popupList.mWidth
                rlp.height = popupList.mHeight
            }
            popupList.mRootView.layoutParams = rlp

            popupList.mContentView?.let {
                var llp = it.layoutParams
                if (llp == null) {
                    llp = ViewGroup.LayoutParams(popupList.mWidth, popupList.mHeight)
                } else {
                    llp.width = popupList.mWidth
                    llp.height = popupList.mHeight
                }
                it.layoutParams = llp

                setContentView(popupList.mRootView)
                show()
                window?.attributes = window?.attributes ?: WindowManager.LayoutParams().apply {
                    width = popupList.mWidth
                    height = popupList.mHeight
                }
            }
        }
    }

    /**
     * 以 Dialog 方式显示
     */
    fun showAsDialog() {
        PopupDialog(mContext).show(this)
    }

}
