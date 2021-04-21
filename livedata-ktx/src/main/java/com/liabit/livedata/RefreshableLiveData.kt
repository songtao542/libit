package com.liabit.livedata

import androidx.lifecycle.MediatorLiveData

open class RefreshableLiveData<T> : MediatorLiveData<T>(), Refreshable {

    override fun refresh() {
    }

}