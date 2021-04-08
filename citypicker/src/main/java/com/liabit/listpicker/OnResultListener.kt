package com.liabit.listpicker

import androidx.annotation.NonNull
import com.liabit.listpicker.model.Item

interface OnResultListener<I : Item> {
    fun onResult(@NonNull data: List<I>)
}
