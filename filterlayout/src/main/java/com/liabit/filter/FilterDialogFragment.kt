package com.liabit.filter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
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
                it.attributes?.flags = (it.attributes?.flags ?: 0) or WindowManager.LayoutParams.FLAG_DIM_BEHIND or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                it.decorView.systemUiVisibility = it.decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    it.decorView.systemUiVisibility = it.decorView.systemUiVisibility or
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                val gestureDetector = GestureDetector(it.context, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapUp(e: MotionEvent?): Boolean {
                        back()
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
            back()
        }
        val filterLayout: FilterLayout = view.findViewById(R.id.filterLayout) ?: return
        setup(filterLayout)
        filterLayout.setOnConfirmListener(object : FilterLayout.OnConfirmListener {
            override fun onConfirm(view: View) {
                back()
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

    fun show(activity: FragmentActivity) {
        if (mShowAsDialog) {
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.addToBackStack(FILTER_TAG)
            show(transaction, FILTER_TAG)
        } else {
            if (activity.supportFragmentManager.findFragmentByTag(FILTER_TAG) == null) {
                activity.supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.filter_anim_right_enter, R.anim.filter_anim_right_exit,
                                R.anim.filter_anim_right_enter, R.anim.filter_anim_right_exit
                        )
                        .add(android.R.id.content, this, FILTER_TAG)
                        .addToBackStack(FILTER_TAG)
                        .commitAllowingStateLoss()
            } else {
                activity.supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.filter_anim_right_enter, R.anim.filter_anim_right_exit,
                                R.anim.filter_anim_right_enter, R.anim.filter_anim_right_exit
                        )
                        .addToBackStack(FILTER_TAG)
                        .show(this)
                        .commitAllowingStateLoss()
            }
        }
    }

    fun onBackPressed(): Boolean {
        val activity = activity ?: return false
        if (isVisible) {
            if (mShowAsDialog) {
                back()
            } else {
                activity.supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.filter_anim_right_enter, R.anim.filter_anim_right_exit,
                                R.anim.filter_anim_right_enter, R.anim.filter_anim_right_exit)
                        .hide(this)
                        .commitAllowingStateLoss()
                return true
            }
        }
        return false
    }

    private fun back() {
        val activity = activity ?: return
        if (activity.supportFragmentManager.backStackEntryCount > 0) {
            dialog?.let { dia ->
                dia.setOnDismissListener(null)
                dia.dismiss()
            }
            activity.supportFragmentManager.popBackStack()
        }
    }

    fun setShowAsDialog(showAsDialog: Boolean) {
        mShowAsDialog = showAsDialog
    }

}
