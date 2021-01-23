package com.liabit.filter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2

/**
 * Author:         songtao
 * CreateDate:     2020/9/12 16:32
 */
@Suppress("unused")
class FilterDialogFragment() : AppCompatDialogFragment(), FilterController by FilterControllerImpl() {

    companion object {
        const val TAG = "FilterDialogFragment"
        const val FILTER_TAG = "filter"
    }

    constructor(picker: IPicker) : this() {
        setFilterPicker(picker)
    }

    private var mShowAsDialog: Boolean = true
    private var mMatchParentHeight: Boolean = true

    private var mEnterAnim: Int? = null
    private var mExitAnim: Int? = null
    private var mPopEnterAnim: Int? = null
    private var mPopExitAnim: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.filter_dialog_fragment, container, false)?.apply {
            if (mShowAsDialog) {
                val screenHeight = context?.resources?.displayMetrics?.heightPixels ?: 0
                if (screenHeight > 0) {
                    val maxHeight = getMaxHeight() ?: screenHeight / 4 * 3
                    (this as? MHLinearLayout)?.setMaxHeight(maxHeight)
                }
            } else if (mMatchParentHeight) {
                layoutParams = layoutParams?.also { it.height = ViewGroup.LayoutParams.MATCH_PARENT }
                        ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                findViewById<ViewPager2>(R.id.viewpager)?.let { viewpager ->
                    viewpager.layoutParams = viewpager.layoutParams?.also { it.height = ViewGroup.LayoutParams.MATCH_PARENT }
                            ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            }
        }
    }

    fun setMatchParentHeight(matchParentHeight: Boolean) {
        mMatchParentHeight = matchParentHeight
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.TopSheetDialog)
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            window?.let {
                //it.attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
                it.attributes?.gravity = Gravity.TOP
                it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR)
                it.decorView.systemUiVisibility = it.decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    it.decorView.systemUiVisibility = it.decorView.systemUiVisibility or
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                val gestureDetector = GestureDetector(it.context, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapUp(e: MotionEvent?): Boolean {
                        dismiss()
                        return true
                    }
                })
                it.decorView.setOnTouchListener { _, event ->
                    return@setOnTouchListener gestureDetector.onTouchEvent(event)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.context as? Activity)?.let {
            val layFull = it.window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (layFull == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) {
                view.findViewById<FrameLayout>(R.id.toolbar)?.setPadding(0, getStatusBarHeight(), 0, 0)
            }
        }
        view.findViewById<View>(R.id.backButton).setOnClickListener {
            dismiss()
        }
        val filterLayout: FilterLayout = view.findViewById(R.id.filterLayout) ?: return
        setup(filterLayout)
        filterLayout.setOnConfirmListener(object : FilterLayout.OnConfirmListener {
            override fun onConfirm(view: View) {
                dismiss()
                getOnConfirmListener()?.onConfirm(view)
            }
        })
    }

    private fun getStatusBarHeight(): Int {
        var height = 0
        try {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                height = resources.getDimensionPixelSize(resourceId)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics).toInt()
        }
        return height
    }

    /**
     * add to [android.R.id.content]
     * not addToBackStack
     */
    fun show(activity: FragmentActivity) {
        show(activity, true, null)
    }

    /**
     * add to [containerViewId]
     * not addToBackStack
     */
    fun show(activity: FragmentActivity, containerViewId: Int) {
        show(activity, true, containerViewId)
    }

    /**
     * add to [android.R.id.content]
     */
    fun show(activity: FragmentActivity, addToBackStack: Boolean) {
        show(activity, addToBackStack, null)
    }

    fun show(activity: FragmentActivity, addToBackStack: Boolean, containerViewId: Int?) {
        val fragmentManager = activity.supportFragmentManager
        if (mShowAsDialog) {
            show(fragmentManager, FILTER_TAG)
        } else {
            val transition = fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            mEnterAnim ?: R.anim.filter_anim_right_enter,
                            mExitAnim ?: R.anim.filter_anim_right_exit,
                            mPopEnterAnim ?: R.anim.filter_anim_right_enter,
                            mPopExitAnim ?: R.anim.filter_anim_right_exit
                    )
                    .add(containerViewId ?: android.R.id.content, this, FILTER_TAG)
            if (addToBackStack) {
                transition.addToBackStack(FILTER_TAG)
            }
            transition.commitAllowingStateLoss()
        }
    }

    fun setCustomAnimations(
            @AnimatorRes @AnimRes enter: Int,
            @AnimatorRes @AnimRes exit: Int,
            @AnimatorRes @AnimRes popEnter: Int,
            @AnimatorRes @AnimRes popExit: Int,
    ) {
        mEnterAnim = enter
        mExitAnim = exit
        mPopEnterAnim = popEnter
        mPopExitAnim = popExit
    }

    fun setShowAsDialog(showAsDialog: Boolean) {
        mShowAsDialog = showAsDialog
    }

    override fun dismiss() {
        if (mShowAsDialog) {
            super.dismiss()
        } else {
            activity?.onBackPressed()
        }
    }

}
