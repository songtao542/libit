package com.liabit.filter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity

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
    private var mPopHeight = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.filter_dialog_fragment, container, false)?.apply {
            if (mShowAsDialog) {
                val screenHeight = context?.resources?.displayMetrics?.heightPixels ?: 0
                if (screenHeight > 0) {
                    mPopHeight = screenHeight / 4 * 3
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mPopHeight)
                }
            }
        }
    }

    class InnerDialog(context: Context?, theme: Int) : AppCompatDialog(context, theme) {
        init {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            setCanceledOnTouchOutside(true)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = InnerDialog(context, R.style.TopSheetDialog)
        dialog.window?.let {
            val lp = it.attributes ?: WindowManager.LayoutParams()
            it.attributes?.let { attr -> lp.copyFrom(attr) }
            val flag = lp.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            lp.flags = flag
            lp.dimAmount = 0.1f
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.TOP
            it.attributes = lp
            it.statusBarColor = Color.TRANSPARENT
            it.requestFeature(Window.FEATURE_NO_TITLE)
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setGravity(Gravity.TOP)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                it.isStatusBarContrastEnforced = false
                it.isNavigationBarContrastEnforced = false
            }
        }
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
