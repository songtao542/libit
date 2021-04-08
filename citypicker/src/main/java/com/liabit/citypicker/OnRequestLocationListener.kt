package com.liabit.citypicker

import com.liabit.listpicker.IPicker
import com.liabit.listpicker.OnRequestVariableListener

interface OnRequestLocationListener : OnRequestVariableListener<City> {
    override fun requestVariable(picker: IPicker<City>) {
        requestLocation(picker)
    }

    fun requestLocation(picker: IPicker<City>)
}
