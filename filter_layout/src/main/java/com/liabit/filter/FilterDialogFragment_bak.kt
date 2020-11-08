//package com.liabit.filter
//
//import android.animation.AnimatorSet
//import android.animation.ObjectAnimator
//import android.annotation.SuppressLint
//import android.app.Dialog
//import android.content.Context
//import android.graphics.Color
//import android.graphics.Rect
//import android.graphics.drawable.ColorDrawable
//import android.os.Bundle
//import android.transition.Fade
//import android.transition.Transition
//import android.util.Log
//import android.view.*
//import android.view.animation.*
//import android.widget.PopupWindow
//import androidx.appcompat.app.AppCompatDialog
//import androidx.appcompat.app.AppCompatDialogFragment
//import androidx.fragment.app.FragmentActivity
//
///**
// * Author:         songtao
// * CreateDate:     2020/9/12 16:32
// */
//@Suppress("unused")
//class FilterDialogFragment_bak : AppCompatDialogFragment(), FilterController {
//
//    companion object {
//        const val TAG = "FilterDialogFragment"
//        const val FILTER_TAG = "filter"
//    }
//
//    private var mFilterLayout: FilterLayout? = null
//    private var mPopHeight = 0
//
//    private var mLeftPageClickToReturn: Boolean = false
//    private var mRightPageClickToReturn: Boolean = false
//    private var mLeftPageTitle: String? = null
//    private var mRightPageTitle: String? = null
//    private var mLeftPageListPadding: Rect? = null
//    private var mRightPageListPadding: Rect? = null
//    private var mOnResultListener: FilterLayout.OnResultListener? = null
//    private var mOnCombinationResultListener: FilterLayout.OnCombinationResultListener? = null
//    private var mOnResetListener: FilterLayout.OnResetListener? = null
//    private var mOnConfirmListener: FilterLayout.OnConfirmListener? = null
//
//    private var mLeftFilterData: List<FilterItem>? = null
//    private var mRightFilterData: List<FilterItem>? = null
//    private var mLeftFilterConfigurator: FilterAdapter? = null
//    private var mRightFilterConfigurator: FilterAdapter? = null
//
//    private var mShowAsDialog: Boolean = true
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.filter_dialog_fragment, container, false)?.apply {
//            if (mShowAsDialog) {
//                val screenHeight = context?.resources?.displayMetrics?.heightPixels ?: 0
//                if (screenHeight > 0) {
//                    mPopHeight = screenHeight / 4 * 3
//                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mPopHeight)
//                }
//            }
//        }
//    }
//
//    class InnerDialog(context: Context?, theme: Int) : AppCompatDialog(context, theme) {
//        init {
//            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
//            setCanceledOnTouchOutside(true)
//        }
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = InnerDialog(context, R.style.TopSheetDialog)
//        dialog.window?.let {
//            val lp = it.attributes ?: WindowManager.LayoutParams()
//            it.attributes?.let { attr -> lp.copyFrom(attr) }
//            val flag = lp.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND or
//                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//            lp.flags = flag
//            lp.dimAmount = 0.1f
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//            lp.gravity = Gravity.TOP
//            it.attributes = lp
//            it.statusBarColor = Color.TRANSPARENT
//            it.requestFeature(Window.FEATURE_NO_TITLE)
//            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            it.setGravity(Gravity.TOP)
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//                it.isStatusBarContrastEnforced = false
//                it.isNavigationBarContrastEnforced = false
//            }
//        }
//        dialog.setCancelable(true)
//        dialog.setCanceledOnTouchOutside(true)
//        return dialog
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        view.findViewById<View>(R.id.backButton).setOnClickListener {
//            back()
//        }
//        mFilterLayout = view.findViewById(R.id.filterLayout)
//        mFilterLayout?.setOnResultListener(this)
//        mFilterLayout?.setOnCombinationResultListener(this)
//        mLeftPageListPadding?.let {
//            mFilterLayout?.setLeftPageListPadding(it.left, it.top, it.right, it.bottom)
//        }
//        mRightPageListPadding?.let {
//            mFilterLayout?.setRightPageListPadding(it.left, it.top, it.right, it.bottom)
//        }
//        mFilterLayout?.setTabTitle(mLeftPageTitle, mRightPageTitle)
//        mLeftFilterData?.let {
//            mFilterLayout?.setLeftPageFilter(it, mLeftFilterConfigurator)
//        }
//        mRightFilterData?.let {
//            mFilterLayout?.setRightPageFilter(it, mRightFilterConfigurator)
//        }
//        mFilterLayout?.setClickToReturnMode(mLeftPageClickToReturn, mRightPageClickToReturn)
//        mOnResetListener?.let { mFilterLayout?.setOnResetListener(it) }
//        mFilterLayout?.setOnConfirmListener(object : FilterLayout.OnConfirmListener {
//            override fun onConfirm(view: View) {
//                back()
//                mOnConfirmListener?.onConfirm(view)
//            }
//        })
//    }
//
//    fun show(activity: FragmentActivity) {
//        if (mShowAsDialog) {
//            val transaction = activity.supportFragmentManager.beginTransaction()
//            transaction.addToBackStack(FILTER_TAG)
//            show(transaction, FILTER_TAG)
//        } else {
//            if (activity.supportFragmentManager.findFragmentByTag(FILTER_TAG) == null) {
//                activity.supportFragmentManager.beginTransaction()
//                        .setCustomAnimations(
//                                R.anim.filter_anim_right_enter, R.anim.filter_anim_right_exit,
//                                R.anim.filter_anim_right_enter, R.anim.filter_anim_right_exit
//                        )
//                        .add(android.R.id.content, this, FILTER_TAG)
//                        .addToBackStack(FILTER_TAG)
//                        .commitAllowingStateLoss()
//            } else {
//                activity.supportFragmentManager.beginTransaction()
//                        .setCustomAnimations(
//                                R.anim.filter_anim_right_enter, R.anim.filter_anim_right_exit,
//                                R.anim.filter_anim_right_enter, R.anim.filter_anim_right_exit
//                        )
//                        .addToBackStack(FILTER_TAG)
//                        .show(this)
//                        .commitAllowingStateLoss()
//            }
//        }
//    }
//
//    fun onBackPressed(): Boolean {
//        val activity = activity ?: return false
//        if (isVisible) {
//            if (mShowAsDialog) {
//                back()
//            } else {
//                activity.supportFragmentManager.beginTransaction()
//                        .setCustomAnimations(R.anim.filter_anim_right_enter, R.anim.filter_anim_right_exit,
//                                R.anim.filter_anim_right_enter, R.anim.filter_anim_right_exit)
//                        .hide(this)
//                        .commitAllowingStateLoss()
//                return true
//            }
//        }
//        return false
//    }
//
//    private fun back() {
//        val activity = activity ?: return
//        if (activity.supportFragmentManager.backStackEntryCount > 0) {
//            dialog?.let { dia ->
//                dia.setOnDismissListener(null)
//                dia.dismiss()
//            }
//            activity.supportFragmentManager.popBackStack()
//        }
//    }
//
//    fun setShowAsDialog(showAsDialog: Boolean) {
//        mShowAsDialog = showAsDialog
//    }
//
//    fun setOnResultListener(listener: FilterLayout.OnResultListener) {
//        if (listener == this) {
//            Log.e(TAG, "Can't set FilterDialogFragment it self as listener.")
//            return
//        }
//        mOnResultListener = listener
//    }
//
//    fun setOnCombinationResultListener(listener: FilterLayout.OnCombinationResultListener) {
//        if (listener == this) {
//            Log.e(TAG, "Can't set FilterDialogFragment it self as listener.")
//            return
//        }
//        mOnCombinationResultListener = listener
//    }
//
//    fun setLeftPageListPadding(left: Int, top: Int, right: Int, bottom: Int) {
//        if (mLeftPageListPadding == null) {
//            mLeftPageListPadding = Rect()
//        }
//        mLeftPageListPadding?.set(left, top, right, bottom)
//        mFilterLayout?.setLeftPageListPadding(left, top, right, bottom)
//    }
//
//    fun setRightPageListPadding(left: Int, top: Int, right: Int, bottom: Int) {
//        if (mRightPageListPadding == null) {
//            mRightPageListPadding = Rect()
//        }
//        mRightPageListPadding?.set(left, top, right, bottom)
//        mFilterLayout?.setRightPageListPadding(left, top, right, bottom)
//    }
//
//    fun setClickToReturnMode(leftPageClickToReturn: Boolean, rightPageClickToReturn: Boolean = false) {
//        mLeftPageClickToReturn = leftPageClickToReturn
//        mRightPageClickToReturn = rightPageClickToReturn
//        mFilterLayout?.setClickToReturnMode(leftPageClickToReturn, rightPageClickToReturn)
//    }
//
//    fun setTab(leftPageTitle: String, rightPageTitle: String) {
//        mLeftPageTitle = leftPageTitle
//        mRightPageTitle = rightPageTitle
//        mFilterLayout?.setTabTitle(leftPageTitle, rightPageTitle)
//    }
//
//    fun setFilter(items: List<FilterItem>, configurator: FilterAdapter? = null) {
//        setLeftPageFilter(items, configurator)
//    }
//
//    fun setLeftPageFilter(items: List<FilterItem>, configurator: FilterAdapter? = null) {
//        mLeftFilterData = items
//        mLeftFilterConfigurator = configurator
//        mFilterLayout?.setLeftPageFilter(items, configurator)
//    }
//
//    fun setRightPageFilter(items: List<FilterItem>, configurator: FilterAdapter? = null) {
//        mRightFilterData = items
//        mRightFilterConfigurator = configurator
//        mFilterLayout?.setRightPageFilter(items, configurator)
//    }
//
//    override fun onResult(pageLeftResult: List<Filter>?, pageRightResult: List<Filter>?) {
//        mOnCombinationResultListener?.onResult(pageLeftResult, pageRightResult)
//        back()
//    }
//
//    override fun onResult(result: List<Filter>) {
//        mOnResultListener?.onResult(result)
//        back()
//    }
//
//    fun setOnResetListener(listener: FilterLayout.OnResetListener) {
//        mOnResetListener = listener
//    }
//
//    fun setOnConfirmListener(listener: FilterLayout.OnConfirmListener) {
//        mOnConfirmListener = listener
//    }
//
//}