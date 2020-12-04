package com.liabit.picker.datetime

import android.view.View

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 14:57
 */
internal class ViewId {

    companion object {
        internal fun getViewId(view: View): String {
            val s = view.toString()
            var i = s.indexOf("app:id")
            if (i < 0) {
                i = s.indexOf("android:id")
            }
            if (i >= 0) {
                return s.substring(i, s.length - 1)
            }
            return ""
        }
    }

}