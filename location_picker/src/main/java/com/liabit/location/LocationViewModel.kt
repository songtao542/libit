package com.liabit.location

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.liabit.location.extension.disposedBy
import com.liabit.location.util.AMapLocationProvider
import com.liabit.location.util.SingleLiveData
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import io.nlopez.smartlocation.rx.ObservableFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class LocationViewModel constructor(private var context: Context) : ViewModel() {

    private val disposeBag: CompositeDisposable = CompositeDisposable()

    val location = MutableLiveData<Location>()

    private var locationControl: SmartLocation.LocationControl

    init {
        val smartLocation = SmartLocation.Builder(context).logging(true).build()
        locationControl = smartLocation.location(AMapLocationProvider(context)).oneFix().config(LocationParams.NAVIGATION)
    }

    fun getLocation(): Observable<Location> {
        return ObservableFactory.from(locationControl)
    }

    private var locating = false

    val error: MutableLiveData<String> by lazy { SingleLiveData() }

    val success: MutableLiveData<String> by lazy { SingleLiveData() }

    fun getMyLocation(handler: ((Location) -> Unit)? = null) {
        if (locating) {
            return
        }
        locating = true
        getLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            locating = false
                            it?.let { loc ->
                                location.value = loc
                                handler?.invoke(loc)
                                return@let
                            }
                        },
                        onError = {
                            locating = false
                            error.value = context.getString(R.string.ml_cannot_get_location)
                        }
                ).disposedBy(disposeBag)
    }


}