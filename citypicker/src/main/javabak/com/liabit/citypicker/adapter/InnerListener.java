package com.liabit.citypicker.adapter;

import com.liabit.listpicker.model.Item;

public interface InnerListener {
    void dismiss(Item data);

    void requestLocation();
}
