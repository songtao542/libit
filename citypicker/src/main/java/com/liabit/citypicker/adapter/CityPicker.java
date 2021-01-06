package com.liabit.citypicker.adapter;

import com.liabit.citypicker.model.City;
import com.liabit.citypicker.model.HotCity;
import com.liabit.citypicker.model.LocateState;
import com.liabit.citypicker.model.LocatedCity;

import java.util.List;

public interface CityPicker {
    void updateLocation(LocatedCity location, @LocateState.State int state);

    void setCities(List<HotCity> hotCities, List<City> cities);
}
