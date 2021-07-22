package com.domain.scaffold.ui.tab.profile

import androidx.lifecycle.SavedStateHandle
import com.domain.scaffold.base.SavedStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : SavedStateViewModel(savedStateHandle) {

    companion object {
        const val KEY_DATA_LIST = "tl"
    }

    private var mDataList = ArrayList<String>()
    val liveDataList = getLiveData(KEY_DATA_LIST, mDataList)

    override fun onRestoreInstanceState(savedStateHandle: SavedStateHandle) {
        savedStateHandle.get<ArrayList<String>>(KEY_DATA_LIST)?.let {
            val tempList = if (mDataList === it) ArrayList<String>(it) else it
            liveDataList.postValue(mDataList)
        }
    }


    fun getDataList() {
        launch {
            async {
                val params = newParams()
                //params["userId"] = userId
                val response = api.getCollection(params)
                val data = response.data
                if (!data.isNullOrEmpty()) {
                    mDataList.addAll(data)
                }
                liveEmpty.postValue(mDataList.isEmpty())
                liveDataList.postValue(mDataList)
            }
        }
    }
}