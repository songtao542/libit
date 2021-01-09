package com.liabit.listpicker.model

import com.github.promeg.pinyinhelper.Pinyin

interface Item {
    fun getItemTitle(): String

    fun getItemSubtitle(): String

    fun isItemChecked(): Boolean {
        return false
    }

    fun setItemChecked(checked: Boolean) {}

    fun getItemSection(): String {
        return Pinyin.toPinyin(getItemTitle()[0])
    }

    fun getItemPinyin(): String {
        return Pinyin.toPinyin(getItemTitle() + getItemSubtitle(), "")
    }
}