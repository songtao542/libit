package com.liabit.filter

import android.content.Context
import android.view.View
import android.view.ViewGroup

class PopupFilter(context: Context) : FilterController by FilterControllerImpl() {

    private var mPopupWindow: PopupWindowCompat = PopupWindowCompat(context)
    private var mFilterLayout: FilterLayout = FilterLayout(context)
    private var mPopHeight: Int = 0

    init {
        val screenHeight = context.resources.displayMetrics.heightPixels
        if (screenHeight > 0) {
            mPopHeight = screenHeight / 4 * 3
        }
        mFilterLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mPopHeight)
    }

    fun show(anchor: View) {
        setup(mFilterLayout)
        mPopupWindow.setContentView(mFilterLayout)
        mPopupWindow.setFullScreenWidth()
        mPopupWindow.setShowMask(true)
        mPopupWindow.show(anchor)
    }

}
