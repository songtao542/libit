package com.liabit.listpicker.model

interface HotItem<I : Item> : Item {
    fun getHotItems(): List<I>
}
