package com.liabit.listpicker

import com.liabit.listpicker.model.HotItem
import com.liabit.listpicker.model.Item
import com.liabit.listpicker.model.VariableState

interface IPicker<I : Item> {
    fun updateVariable(variableItem: I?, @VariableState.State state: Int)
    fun setItem(variableItem: I?, hotItem: HotItem<I>?, items: List<I>?)
}