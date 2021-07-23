package com.liabit.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class NetworkStateMonitor(
    context: Context
) : ConnectivityManager.NetworkCallback(), AutoCloseable {

    private val mContext: Context = context.applicationContext
    private val mConnectivityManager: ConnectivityManager = mContext.getSystemService(ConnectivityManager::class.java)

    private val mLiveNetworkState by lazy { MutableLiveData(isNetworkAvailable()) }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mConnectivityManager.registerDefaultNetworkCallback(this)
        } else {
            val request = NetworkRequest.Builder().build()
            mConnectivityManager.registerNetworkCallback(request, this)
        }
    }

    fun observe(lifecycleOwner: LifecycleOwner, observer: Observer<Boolean>) {
        mLiveNetworkState.observe(lifecycleOwner, observer)
    }

    override fun onAvailable(network: Network) {
        checkState()
    }

    override fun onLost(network: Network) {
        checkState()
    }

    private fun checkState() {
        val state = isNetworkAvailable()
        if (state != mLiveNetworkState.value) {
            mLiveNetworkState.postValue(state)
        }
    }

    fun isNetworkAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mConnectivityManager.activeNetwork != null
        } else {
            var isAvailable = false
            mConnectivityManager.allNetworkInfo.let {
                for (net in it) {
                    if (net.isAvailable) {
                        isAvailable = true
                        break
                    }
                }
            }
            isAvailable
        }
    }

    override fun close() {
        mConnectivityManager.unregisterNetworkCallback(this)
    }
}