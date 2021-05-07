package com.liabit.test.base

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 注意： Fragment 必须使用 @AndroidEntryPoint 注解，且装载该Fragment的Activity也必须用 @AndroidEntryPoint 注解
 * ViewModel的基类, 继承 AppViewModel 的类, 请使用 @HiltViewModel 注解标注, 否则无法完成依赖注入
 */
open class AppViewModel : ViewModel() {

    @Inject
    lateinit var application: Application

    //@Inject
    //lateinit var api: Api

    //@Inject
    //lateinit var userRepository: UserRepository

    val context: Context get() = application

    @Suppress("PropertyName")
    @JvmField
    val TAG: String = javaClass.simpleName

    protected open val disposeBag = CompositeDisposable()

    //val error: MutableLiveData<Error> by lazy { SingleLiveData() }

    //val success: MutableLiveData<CharSequence> by lazy { SingleLiveData() }

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

    suspend fun <T> async(block: suspend CoroutineScope.() -> T, error: suspend CoroutineScope.(exception: Throwable) -> T): T {
        return withContext(Dispatchers.Default) {
            return@withContext try {
                block.invoke(this)
            } catch (e: Throwable) {
                error.invoke(this, e)
            }
        }
    }

    suspend fun <T> async(block: suspend CoroutineScope.() -> T, onErrorReturn: T): T {
        return withContext(Dispatchers.Default) {
            return@withContext try {
                block.invoke(this)
            } catch (e: Throwable) {
                onErrorReturn
            }
        }
    }

    suspend fun <T> async(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.Default, block)
    }

    private val mStateMap by lazy { HashMap<String, Boolean>() }

    private var mState = false

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
    fun onFinishMemberInject() {
        onCreate(context)
    }

    /**
     * 如果想在(application 和 context)注入完成后做一些操作，可以 override 该方法
     */
    protected open fun onCreate(context: Context) {
    }

    override fun onCleared() {
        disposeBag.dispose()
        disposeBag.clear()
    }

    /**
     * sid
     */
    //val sid: String get() = userRepository.getUser()?.sid ?: ""

    //val user: User get() = userRepository.getUser() ?: User.Anonymous

    //val liveUser: LiveData<User> get() = userRepository.getLiveUser()

    //val uid: Int get() = user.id ?: -1

    fun newParams(): ParamBuilder {
        return ParamBuilder()
    }
}

class ParamBuilder : HashMap<String, Any>() {

    operator fun set(key: String, value: Any): ParamBuilder {
        put(key, value)
        return this
    }

}

/**
 * 请求过程中的所有异常信息
 */
class Error {
    var action = -1
    var code = -1
    var msg: String? = null
    var throwable: Throwable? = null

    constructor(action: Int, code: Int, msg: String?) {
        this.action = action
        this.code = code
        this.msg = msg
    }

    constructor(action: Int, throwable: Throwable?) {
        this.action = action
        this.throwable = throwable
    }

    val message: String? get() = msg ?: throwable?.message

    override fun toString(): String {
        return "action: $action message: $message"
    }
}
