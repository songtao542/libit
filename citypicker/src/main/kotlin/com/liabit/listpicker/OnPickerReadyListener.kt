package com.liabit.listpicker

import com.liabit.listpicker.model.Item

interface OnPickerReadyListener<I : Item> {
    fun onPickerReady(picker: IPicker<I>)
}