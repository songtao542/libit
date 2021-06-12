package com.liabit.location

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liabit.location.util.AMapLocationProvider
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

typealias OnLocationChangeListener = (location: Location) -> Unit

class LocationViewModel constructor(context: Context) : ViewModel() {

    private var mLocating = false
    private var mWeakListener: WeakReference<OnLocationChangeListener>? = null
    private var locationControl: SmartLocation.LocationControl

    val liveLocation = MutableLiveData<Location>()

    init {
        val applicationContext = context.applicationContext
        val smartLocation = SmartLocation.Builder(applicationContext).logging(true).build()
        locationControl = smartLocation.location(AMapLocationProvider(applicationContext)).oneFix().config(LocationParams.NAVIGATION)
    }

    fun getMyLocation(listener: OnLocationChangeListener? = null) {
        if (mLocating) {
            return
        }
        mWeakListener = WeakReference(listener)
        mLocating = true
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                locationControl.start {
                    mLocating = false
                    it?.also { loc ->
                        liveLocation.value = loc
                        mWeakListener?.get()?.invoke(loc)
                    }
                }
            }
        }
    }
}