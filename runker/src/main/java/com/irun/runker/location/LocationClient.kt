package com.irun.runker.location

import android.location.Location

interface LocationClient<L : Location> : AutoCloseable {
    fun setLocationListener(listener: LocationListener<L>)
    fun getLastKnownLocation(): L?
    fun startLocation()
    fun stopLocation()
}

interface LocationListener<L : Location> {
    fun onLocationChanged(location: L)
}


