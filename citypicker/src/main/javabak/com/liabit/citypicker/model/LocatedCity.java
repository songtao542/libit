package com.liabit.citypicker.model;

import com.liabit.listpicker.model.Item;

public class LocatedCity extends Item {

    public LocatedCity(String name, String province, String code) {
        super(name, province, "定位城市", code);
    }
}
