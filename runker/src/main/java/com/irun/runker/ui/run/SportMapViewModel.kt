package com.irun.runker.ui.run

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.irun.runker.base.AppViewModel
import com.irun.runker.database.SportRecordDao
import com.irun.runker.extension.disposedBy
import com.irun.runker.model.Joke
import com.irun.runker.model.Response
import com.irun.runker.model.SportRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SportMapViewModel @Inject constructor(private val sportRecordDao: SportRecordDao) : AppViewModel() {

    val saveSportRecord = MutableLiveData<Boolean>()

    override fun onCreate(context: Context) {
        Log.d("TTTT", "onCreate application: $application   context: $context")
    }

    fun saveSportRecord(sportRecord: SportRecord) {
        Log.d("TTTT", "application: $application   context: $context")
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("TTTT", "saveSportRecord GlobalScope launch ${Thread.currentThread().name}")
            sportRecordDao.save(sportRecord)
            saveSportRecord.postValue(true)
        }

        launch {
            Log.d("TTTT", "saveSportRecord launch ${Thread.currentThread().name}")
        }

        launch(Dispatchers.IO) {
            Log.d("TTTT", "saveSportRecord launch(Dispatchers.IO) ${Thread.currentThread().name}")
        }

        async(Dispatchers.IO) {

        }
    }

    fun getJokeObservable() {
        showProgress("加载中")
        Log.d("TTTT", "application: $application   context: $context")
        api.getJokeObservable("sJvfllm3c58fc2576e67b675e0ae63d1296b1892f1f2949", 20)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                setLoading(false)
                Log.d("TTTT", "doFinally:=================")
            }
            .subscribeBy({
                Log.d("TTTT", "getJokeObservable: error: $it")
            }) {
                Log.d("TTTT", "getJokeObservable: success: $it")
            }
            .disposedBy(disposeBag)
    }

    fun getJokeLiveData(): LiveData<Response<List<Joke>>> {
        return api.getJokeLiveData("sJvfllm3c58fc2576e67b675e0ae63d1296b1892f1f2949", 20)
    }

    suspend fun getJoke(): Response<List<Joke>> {
        return async {
            api.getJoke("sJvfllm3c58fc2576e67b675e0ae63d1296b1892f1f2949", 20)
        }
    }
}