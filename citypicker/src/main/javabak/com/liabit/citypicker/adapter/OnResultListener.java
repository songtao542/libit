package com.liabit.citypicker.adapter;

import com.liabit.listpicker.model.Item;

import java.util.List;

import androidx.annotation.NonNull;

public interface OnResultListener {
    void onResult(@NonNull List<Item> data);
}
