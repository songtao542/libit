package com.liabit.citypicker.adapter;

import com.liabit.citypicker.model.City;

import java.util.List;

import androidx.annotation.NonNull;

public interface OnResultListener {
    void onResult(@NonNull List<City> data);
}
