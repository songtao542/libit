package com.liabit.test.loadmore.train

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.liabit.test.base.SavedStateViewModel
import javax.inject.Inject

class TrainViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : SavedStateViewModel(savedStateHandle) {

    companion object {
        private const val KEY_VIDEOS = "videos"
    }

    private var mVideoList = ArrayList<Video>()

    val liveVideoList get() = getLiveData(KEY_VIDEOS, mVideoList)

    val livePageEnd = MutableLiveData<Boolean>()

    /**
     *  恢复状态
     */
    override fun onRestoreInstanceState(savedStateHandle: SavedStateHandle) {
        val videos = savedStateHandle.get<ArrayList<Video>>(KEY_VIDEOS)
        if (!videos.isNullOrEmpty()) {
            mVideoList.addAll(videos)
            liveVideoList.value = mVideoList
        }
    }

    private fun getPageIndex(): Int {
        return get("v_p_i", 1)
    }

    private fun setPageIndex(pageIndex: Int) {
        return set("v_p_i", pageIndex)
    }

    private var mPage = 0

    fun listVideo(refresh: Boolean, level: Int) {
        //Log.d("TTTT","listVideo---->", Throwable())
        val data = ArrayList<Video>()
        if (livePageEnd.value == null || livePageEnd.value == false) {
            data.add(Video.mock1())
            data.add(Video.mock2())
            data.add(Video.mock1())
        }
        if (!data.isNullOrEmpty()) {
            if (refresh) {
                livePageEnd.postValue(false)
                mVideoList.clear()
            }
            if (mPage >= 4) {
                livePageEnd.postValue(true)
            } else {
                mPage++
            }
            // 记住更新前的 size
            mVideoList.addAll(data)
        }
        liveVideoList.value = mVideoList
    }


}