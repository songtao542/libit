package com.liabit.viewmodel

import android.app.Application
import android.content.Context
import android.os.Handler
import android.util.ArrayMap
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 注意: Fragment和装载该Fragment的Activity 必须使用 @AndroidEntryPoint 注解标注。
 * 且继承 ApplicationViewModel 的类必须使用 @HiltViewModel 注解标注, 否则无法完成依赖注入
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class ApplicationViewModel : ViewModel() {

    @Inject
    lateinit var application: Application

    val context: Context get() = application

    @Suppress("PropertyName")
    @JvmField
    val TAG: String = javaClass.simpleName

    private val mLiveDialog by lazy { MutableLiveData<DialogMessage>() }

    private val mDialogMessage by lazy { DialogMessage(context) }

    private val mHandler by lazy { Handler(context.mainLooper) }

    class DialogMessage(
        val context: Context,
        internal var mShow: Boolean = false,
        internal var mMessage: String? = null,
        internal var mMessageResId: Int = 0,
        internal var mCancellable: Boolean = true
    ) {
        val show: Boolean get() = mShow

        val message: String?
            get() {
                return mMessage ?: if (mMessageResId != 0) context.getString(mMessageResId) else null
            }

        val cancellable: Boolean get() = mCancellable
    }

    fun observeDialog(lifecycleOwner: LifecycleOwner, observer: Observer<DialogMessage>) {
        mLiveDialog.removeObserver(observer)
        mLiveDialog.observe(lifecycleOwner, observer)
    }

    fun showDialog(cancellable: Boolean, message: String? = null) {
        mLiveDialog.postValue(mDialogMessage.apply {
            mShow = true
            mMessage = message
            mCancellable = cancellable
            mMessageResId = 0
        })
    }

    fun showDialog(message: String? = null) {
        showDialog(true, message)
    }

    fun showDialog(cancellable: Boolean, @StringRes messageResId: Int) {
        mLiveDialog.postValue(mDialogMessage.apply {
            mShow = true
            mMessage = null
            mCancellable = cancellable
            mMessageResId = messageResId
        })
    }

    fun showDialog(@StringRes messageResId: Int) {
        showDialog(true, messageResId)
    }

    fun hideDialog() {
        mLiveDialog.postValue(mDialogMessage.apply {
            mShow = false
            mMessage = null
            mMessageResId = 0
        })
    }

    fun hideDialog(delay: Long) {
        mHandler.postDelayed({ hideDialog() }, delay)
    }

    fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(context, start, block)

    fun <T> async(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
    ): Deferred<T> = viewModelScope.async(context, start, block)

    /**
     * @param block 异步执行 block
     * @param error block 执行异常时，执行 error
     */
    suspend fun <T> async(
        block: suspend CoroutineScope.() -> T,
        error: suspend CoroutineScope.(exception: Throwable) -> T
    ): T {
        return withContext(Dispatchers.Default) {
            return@withContext try {
                block.invoke(this)
            } catch (e: Throwable) {
                error.invoke(this, e)
            }
        }
    }

    /**
     * @param block 异步执行 block
     * @param onErrorReturn block 执行异常时，返回该值
     */
    suspend fun <T> async(
        block: suspend CoroutineScope.() -> T,
        onErrorReturn: T
    ): T {
        return withContext(Dispatchers.Default) {
            return@withContext try {
                block.invoke(this)
            } catch (e: Throwable) {
                onErrorReturn
            }
        }
    }

    /**
     * @param block 异步执行 block
     */
    suspend fun <T> async(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.Default, block)
    }

    /**
     * 执行异步任务
     * 会根据 [isLoading] 判断是否执行 [task]
     */
    fun launchTask(task: suspend CoroutineScope.() -> Unit) {
        if (isLoading()) return
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                try {
                    task.invoke(this)
                } catch (e: Throwable) {
                    Log.d(TAG, "error occurred: ", e)
                } finally {
                    setLoading(false)
                }
            }
        }
    }

    /**
     * 执行异步任务
     * @param key 当前任务的唯一标识, 会根据 [isLoading] 判断是否执行 [task]
     */
    fun launchTask(key: String, task: suspend CoroutineScope.() -> Unit) {
        if (isLoading(key)) return
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                try {
                    task.invoke(this)
                } catch (e: Throwable) {
                    Log.d(TAG, "error occurred: ", e)
                } finally {
                    setLoading(key, false)
                }
            }
        }
    }

    private val mStateMap by lazy { ArrayMap<String, Boolean>() }

    fun setLoading(state: Boolean) {
        mStateMap["__def__"] = state
    }

    /**
     * 如果 isLoading == false , 则自动设置为 true
     */
    fun isLoading(): Boolean {
        return isLoading("__def__")
    }

    fun setLoading(key: String, state: Boolean) {
        mStateMap[key] = state
    }

    /**
     * 如果 isLoading == false , 则自动设置为 true
     */
    fun isLoading(key: String): Boolean {
        val loading = mStateMap[key] ?: false
        if (!loading) {
            mStateMap[key] = true
        }
        return loading
    }

    /**
     * @Inject 注解的方法会在 @Inject注解的成员(application)完成注入完成后调用
     */
    @Inject
    open fun onFinishMemberInject() {
        onCreate(context)
    }

    /**
     * 如果想在(application 和 context)注入完成后做一些操作，可以 override 该方法
     */
    protected open fun onCreate(context: Context) {
    }

    fun newParamMap(): ParamMap {
        return ParamMap()
    }
}

class ParamMap : HashMap<String, Any>() {
    operator fun set(key: String, value: Any): ParamMap {
        put(key, value)
        return this
    }
}
