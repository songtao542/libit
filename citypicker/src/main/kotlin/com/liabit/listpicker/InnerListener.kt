package com.liabit.listpicker

import com.liabit.listpicker.model.Item

interface InnerListener<I : Item> {
    fun onItemClick(item: I?)
    fun requestVariable()
}
