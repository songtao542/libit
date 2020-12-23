package com.liabit.filter

import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup

internal class FilterControllerImpl : FilterController {

    private var mFilterLayout: FilterLayout? = null
    private var mLeftPageClickToReturn: Boolean = false
    private var mRightPageClickToReturn: Boolean = false
    private var mLeftPageTitle: String? = null
    private var mRightPageTitle: String? = null
    private var mLeftPageListPadding: Rect? = null
    private var mRightPageListPadding: Rect? = null

    private var mOnResultListener: FilterLayout.OnResultListener? = null
    private var mOnCombinationResultListener: FilterLayout.OnCombinationResultListener? = null
    private var mOnResetListener: FilterLayout.OnResetListener? = null
    private var mOnConfirmListener: FilterLayout.OnConfirmListener? = null

    private var mLeftFilterData: List<Filter>? = null
    private var mRightFilterData: List<Filter>? = null
    private var mLeftFilterConfigurator: FilterAdapter? = null
    private var mRightFilterConfigurator: FilterAdapter? = null

    private var mFilterPicker: IPicker? = null

    private var mMaxHeight: Int? = null

    override fun setup(filterLayout: FilterLayout) {
        mFilterLayout = filterLayout
        mFilterLayout?.setFilterPicker(mFilterPicker)
        mFilterLayout?.setOnResultListener(this)
        mFilterLayout?.setOnCombinationResultListener(this)
        mLeftPageListPadding?.let {
            mFilterLayout?.setLeftPageListPadding(it.left, it.top, it.right, it.bottom)
        }
        mRightPageListPadding?.let {
            mFilterLayout?.setRightPageListPadding(it.left, it.top, it.right, it.bottom)
        }
        mFilterLayout?.setTabTitle(mLeftPageTitle, mRightPageTitle)
        mLeftFilterData?.let {
            mFilterLayout?.setLeftPageFilter(it, mLeftFilterConfigurator)
        }
        mRightFilterData?.let {
            mFilterLayout?.setRightPageFilter(it, mRightFilterConfigurator)
        }
        mFilterLayout?.setClickToReturnMode(mLeftPageClickToReturn, mRightPageClickToReturn)
        mOnResetListener?.let { mFilterLayout?.setOnResetListener(it) }
        mFilterLayout?.setOnConfirmListener(object : FilterLayout.OnConfirmListener {
            override fun onConfirm(view: View) {
                mOnConfirmListener?.onConfirm(view)
            }
        })
    }

    override fun setOnResultListener(listener: FilterLayout.OnResultListener) {
        if (listener == this) {
            Log.e(FilterDialogFragment.TAG, "Can't set FilterDialogFragment it self as listener.")
            return
        }
        mOnResultListener = listener
    }

    override fun setOnCombinationResultListener(listener: FilterLayout.OnCombinationResultListener) {
        if (listener == this) {
            Log.e(FilterDialogFragment.TAG, "Can't set FilterDialogFragment it self as listener.")
            return
        }
        mOnCombinationResultListener = listener
    }

    override fun setLeftPageListPadding(left: Int, top: Int, right: Int, bottom: Int) {
        if (mLeftPageListPadding == null) {
            mLeftPageListPadding = Rect()
        }
        mLeftPageListPadding?.set(left, top, right, bottom)
        mFilterLayout?.setLeftPageListPadding(left, top, right, bottom)
    }

    override fun setRightPageListPadding(left: Int, top: Int, right: Int, bottom: Int) {
        if (mRightPageListPadding == null) {
            mRightPageListPadding = Rect()
        }
        mRightPageListPadding?.set(left, top, right, bottom)
        mFilterLayout?.setRightPageListPadding(left, top, right, bottom)
    }

    override fun setClickToReturnMode(leftPageClickToReturn: Boolean, rightPageClickToReturn: Boolean) {
        mLeftPageClickToReturn = leftPageClickToReturn
        mRightPageClickToReturn = rightPageClickToReturn
        mFilterLayout?.setClickToReturnMode(leftPageClickToReturn, rightPageClickToReturn)
    }

    override fun setTab(leftPageTitle: String, rightPageTitle: String) {
        mLeftPageTitle = leftPageTitle
        mRightPageTitle = rightPageTitle
        mFilterLayout?.setTabTitle(leftPageTitle, rightPageTitle)
    }

    override fun setFilter(items: List<Filter>, configurator: FilterAdapter?) {
        setLeftPageFilter(items, configurator)
    }

    override fun setLeftPageFilter(items: List<Filter>, configurator: FilterAdapter?) {
        mLeftFilterData = items
        mLeftFilterConfigurator = configurator
        mFilterLayout?.setLeftPageFilter(items, configurator)
    }

    override fun setRightPageFilter(items: List<Filter>, configurator: FilterAdapter?) {
        mRightFilterData = items
        mRightFilterConfigurator = configurator
        mFilterLayout?.setRightPageFilter(items, configurator)
    }

    override fun onResult(pageLeftResult: List<Filter>?, pageRightResult: List<Filter>?) {
        mOnCombinationResultListener?.onResult(pageLeftResult, pageRightResult)
    }

    override fun onResult(result: List<Filter>) {
        mOnResultListener?.onResult(result)
    }

    override fun setOnResetListener(listener: FilterLayout.OnResetListener) {
        mOnResetListener = listener
    }

    override fun setOnConfirmListener(listener: FilterLayout.OnConfirmListener) {
        mOnConfirmListener = listener
    }

    override fun setFilterPicker(picker: IPicker) {
        mFilterPicker = picker
    }

    override fun getOnResultListener(): FilterLayout.OnResultListener? {
        return mOnResultListener
    }

    override fun getOnCombinationResultListener(): FilterLayout.OnCombinationResultListener? {
        return mOnCombinationResultListener
    }

    override fun getOnResetListener(): FilterLayout.OnResetListener? {
        return mOnResetListener
    }

    override fun getOnConfirmListener(): FilterLayout.OnConfirmListener? {
        return mOnConfirmListener
    }

    override fun setMaxHeight(max: Int) {
        mMaxHeight = max
    }

    override fun getMaxHeight():Int? {
        return mMaxHeight
    }

}
