package com.liabit.filter

import android.content.Context
import android.view.View
import android.view.ViewGroup

@Suppress("unused")
class PopupFilter(context: Context) : FilterController by FilterControllerImpl(), FilterLayout.OnConfirmListener {

    private var mPopupWindow: PopupWindowCompat = PopupWindowCompat(context)
    private var mFilterLayout: FilterLayout = FilterLayout(context)
    private var mPopHeight: Int = 0

    constructor(context: Context, picker: IPicker) : this(context) {
        setFilterPicker(picker)
    }

    init {
        val screenHeight = context.resources.displayMetrics.heightPixels
        if (screenHeight > 0) {
            mPopHeight = screenHeight / 4 * 3
        }
        mFilterLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mPopHeight)
    }

    override fun onConfirm(view: View) {
        mPopupWindow.dismiss()
        getOnConfirmListener()?.onConfirm(view)
    }

    fun show(anchor: View) {
        setup(mFilterLayout)
        mFilterLayout.setOnConfirmListener(this)
        mPopupWindow.setContentView(mFilterLayout)
        mPopupWindow.setFullScreenWidth()
        mPopupWindow.setShowMask(true)
        mPopupWindow.show(anchor)
    }

}
