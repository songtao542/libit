package com.liabit.citypicker.adapter;

import com.liabit.citypicker.model.City;

public interface InnerListener {
    void dismiss(City data);

    void requestLocation();
}
