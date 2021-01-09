package com.liabit.citypicker

import android.text.TextUtils
import com.liabit.listpicker.model.Item
import java.util.regex.Pattern

open class City(
        var name: String,
        var province: String,
        var code: String,
        var py: String) : Item {

    var mChecked = false

    override fun setItemChecked(checked: Boolean) {
        mChecked = checked
    }

    override fun isItemChecked(): Boolean {
        return mChecked
    }

    override fun getItemTitle(): String {
        return name
    }

    override fun getItemSubtitle(): String {
        return ""
    }

    override fun getItemPinyin(): String {
        return py
    }

    /***
     * 获取悬浮栏文本，（#、定位、热门 需要特殊处理）
     * @return
     */
    override fun getItemSection(): String {
        return if (TextUtils.isEmpty(py)) {
            "#"
        } else {
            val c = py.substring(0, 1)
            val p = Pattern.compile("[a-zA-Z]")
            val m = p.matcher(c)
            if (m.matches()) {
                c.toUpperCase()
            } else if (TextUtils.equals(c, "定") || TextUtils.equals(c, "热")) py else "#"
        }
    }

    override fun toString(): String {
        return "$province$name"
    }

}