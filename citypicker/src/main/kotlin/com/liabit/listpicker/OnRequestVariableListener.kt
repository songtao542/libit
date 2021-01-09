package com.liabit.listpicker

import com.liabit.listpicker.model.Item

interface OnRequestVariableListener<I : Item> {
    fun requestVariable(picker: IPicker<I>)
}