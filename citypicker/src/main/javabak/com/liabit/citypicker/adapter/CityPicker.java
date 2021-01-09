package com.liabit.citypicker.adapter;

import com.liabit.listpicker.model.Item;
import com.liabit.listpicker.model.HotListItem;
import com.liabit.listpicker.model.VariableState;
import com.liabit.listpicker.model.VariableItem;

import java.util.List;

public interface CityPicker {
    void updateLocation(VariableItem location, @VariableState.State int state);

    void setCities(List<HotListItem> hotCities, List<Item> cities);
}
