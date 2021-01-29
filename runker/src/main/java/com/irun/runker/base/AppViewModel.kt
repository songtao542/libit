package com.irun.runker.base

import android.app.Application
import android.content.Context
import android.util.SparseBooleanArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irun.runker.net.Api
import com.irun.runker.util.livedata.SingleLiveData
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

open class AppViewModel : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext get() = viewModelScope.coroutineContext

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var api: Api

    val context: Context get() = application

    protected val disposeBag = CompositeDisposable()

    val progress: MutableLiveData<CharSequence> by lazy { SingleLiveData() }

    val error: MutableLiveData<CharSequence> by lazy { SingleLiveData() }

    val success: MutableLiveData<CharSequence> by lazy { SingleLiveData() }

    private val mStates by lazy { SparseBooleanArray() }

    private var mState = false

    suspend fun <T> async(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.Default, block)
    }

    fun showProgress(message: CharSequence? = null) {
        progress.postValue(message)
    }

    fun showProgress(stringResId: Int? = null) {
        progress.postValue(getString(stringResId))
    }

    fun showError(message: CharSequence? = null) {
        error.postValue(message)
    }

    fun showSuccess(message: CharSequence? = null) {
        success.postValue(message)
    }

    fun setLoading(state: Boolean) {
        mState = state
    }

    /**
     * 如果 isLoading == false , 则自动设置为 true
     */
    fun isLoading(): Boolean {
        val state = mState
        if (!state) {
            mState = true
        }
        return state
    }

    /**
     * @param key 唯一标识
     * @param state 是否正在加载中
     */
    fun setLoading(key: String, state: Boolean) {
        mStates.put(key.hashCode(), state)
    }

    /**
     * 如果 isLoading == false , 则自动设置为 true
     */
    fun isLoading(key: String): Boolean {
        val loading = mStates[key.hashCode()]
        if (!loading) {
            mStates.put(key.hashCode(), true)
        }
        return loading
    }

    /**
     * @Inject 标注的方法会在 @Inject 标注的成员(application 和 context)注入完成后调用
     */
    @Inject
    fun onFinishMemberInject() {
        onCreate(context)
    }

    /**
     * 如果想在(application 和 context)注入完成后做一些操作，可以 override 该方法
     */
    open fun onCreate(context: Context) {
    }

    override fun onCleared() {
        disposeBag.dispose()
        disposeBag.clear()
    }

    fun getString(stringResId: Int?): String? {
        if (stringResId == null) return null
        return context.getString(stringResId)
    }
}