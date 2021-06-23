package com.liabit.livedata

import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.RequiresApi
import androidx.arch.core.internal.SafeIterableMap
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.time.Duration
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.experimental.ExperimentalTypeInference

internal const val DEFAULT_TIMEOUT = 5000L

/**
 * Interface that allows controlling a [LiveData] from a coroutine block.
 *
 * @see liveData
 */
interface LiveDataScope<T> {
    /**
     * Set's the [LiveData]'s value to the given [value]. If you've called [emitSource] previously,
     * calling [emit] will remove that source.
     *
     * Note that this function suspends until the value is set on the [LiveData].
     *
     * @param value The new value for the [LiveData]
     *
     * @see emitSource
     */
    suspend fun emit(value: T)

    /**
     * Add the given [LiveData] as a source, similar to [MediatorLiveData.addSource]. Calling this
     * method will remove any source that was yielded before via [emitSource].
     *
     * @param source The [LiveData] instance whose values will be dispatched from the current
     * [LiveData].
     *
     * @see emit
     * @see MediatorLiveData.addSource
     * @see MediatorLiveData.removeSource
     */
    suspend fun emitSource(source: LiveData<T>): DisposableHandle

    /**
     * References the current value of the [LiveData].
     *
     * If the block never `emit`ed a value, [latestValue] will be `null`. You can use this
     * value to check what was then latest value `emit`ed by your `block` before it got cancelled.
     *
     * Note that if the block called [emitSource], then `latestValue` will be last value
     * dispatched by the `source` [LiveData].
     */
    val latestValue: T?
}

internal class LiveDataScopeImpl<T>(
    private var target: CoroutineLiveData<T>,
    context: CoroutineContext
) : LiveDataScope<T> {

    override val latestValue: T? get() = target.value

    // use `liveData` provided context + main dispatcher to communicate with the target
    // LiveData. This gives us main thread safety as well as cancellation cooperation
    private val coroutineContext = context + Dispatchers.Main.immediate

    override suspend fun emitSource(source: LiveData<T>): DisposableHandle =
        withContext(coroutineContext) {
            return@withContext target.emitSource(source)
        }

    override suspend fun emit(value: T) = withContext(coroutineContext) {
        target.clearSource()
        target.value = value
    }
}

internal suspend fun <T> MediatorLiveData<T>.addDisposableSource(
    source: LiveData<T>
): EmittedSource = withContext(Dispatchers.Main.immediate) {
    addSource(source) {
        value = it
    }
    EmittedSource(
        source = source,
        mediator = this@addDisposableSource
    )
}

/**
 * Holder class that keeps track of the previously dispatched [LiveData].
 * It implements [DisposableHandle] interface while also providing a suspend clear function
 * that we can use internally.
 */
internal class EmittedSource(
    private val source: LiveData<*>,
    private val mediator: MediatorLiveData<*>
) : DisposableHandle {
    // @MainThread
    private var disposed = false

    /**
     * Unlike [dispose] which cannot be sync because it not a coroutine (and we do not want to
     * lock), this version is a suspend function and does not return until source is removed.
     */
    suspend fun disposeNow() = withContext(Dispatchers.Main.immediate) {
        removeSource()
    }

    override fun dispose() {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            removeSource()
        }
    }

    @MainThread
    private fun removeSource() {
        if (!disposed) {
            mediator.removeSource(source)
            disposed = true
        }
    }
}

internal typealias Block<T> = suspend LiveDataScope<T>.() -> Unit

/**
 * Handles running a block at most once to completion.
 */
internal class BlockRunner<T>(
    private val liveData: CoroutineLiveData<T>,
    private val block: Block<T>,
    private val timeoutInMs: Long,
    private val scope: CoroutineScope,
    private val onDone: () -> Unit
) {
    // currently running block job.
    private var runningJob: Job? = null

    // cancellation job created in cancel.
    private var cancellationJob: Job? = null

    @MainThread
    fun maybeRun() {
        cancellationJob?.cancel()
        cancellationJob = null
        if (runningJob != null) {
            return
        }
        runningJob = scope.launch {
            val liveDataScope = LiveDataScopeImpl(liveData, coroutineContext)
            block(liveDataScope)
            onDone()
        }
    }

    @MainThread
    fun cancel() {
        if (cancellationJob != null) {
            error("Cancel call cannot happen without a maybeRun")
        }
        cancellationJob = scope.launch(Dispatchers.Main.immediate) {
            delay(timeoutInMs)
            if (!liveData.hasActiveObservers()) {
                // one last check on active observers to avoid any race condition between starting
                // a running coroutine and cancelation
                runningJob?.cancel()
                runningJob = null
            }
        }
    }
}

internal class CoroutineLiveData<T>(
    private val context: CoroutineContext = EmptyCoroutineContext,
    private val timeoutInMs: Long = DEFAULT_TIMEOUT,
    private val block: Block<T>
) : RefreshableLiveData<T>() {
    private var blockRunner: BlockRunner<T>? = null
    private var emittedSource: EmittedSource? = null

    init {
        refresh(false)
    }

    override fun refresh() {
        refresh(true)
    }

    private fun refresh(immediateRun: Boolean) {
        if (blockRunner == null) {
            // use an intermediate supervisor job so that if we cancel individual block runs due to losing
            // observers, it won't cancel the given context as we only cancel w/ the intention of possibly
            // relaunching using the same parent context.
            val supervisorJob = SupervisorJob(context[Job])

            // The scope for this LiveData where we launch every block Job.
            // We default to Main dispatcher but developer can override it.
            // The supervisor job is added last to isolate block runs.
            val scope = CoroutineScope(Dispatchers.Main.immediate + context + supervisorJob)
            blockRunner = BlockRunner(
                liveData = this,
                block = block,
                timeoutInMs = timeoutInMs,
                scope = scope
            ) {
                blockRunner = null
            }
        }
        if (immediateRun) {
            blockRunner?.maybeRun()
        }
    }

    internal suspend fun emitSource(source: LiveData<T>): DisposableHandle {
        clearSource()
        val newSource = addDisposableSource(source)
        emittedSource = newSource
        return newSource
    }

    internal suspend fun clearSource() {
        emittedSource?.disposeNow()
        emittedSource = null
    }

    override fun onActive() {
        super.onActive()
        blockRunner?.maybeRun()
    }

    override fun onInactive() {
        super.onInactive()
        blockRunner?.cancel()
    }
}

/**
 * Builds a LiveData that has values yielded from the given [block] that executes on a
 * [LiveDataScope].
 *
 * The [block] starts executing when the returned [LiveData] becomes active ([LiveData.onActive]).
 * If the [LiveData] becomes inactive ([LiveData.onInactive]) while the [block] is executing, it
 * will be cancelled after [timeoutInMs] milliseconds unless the [LiveData] becomes active again
 * before that timeout (to gracefully handle cases like Activity rotation). Any value
 * [LiveDataScope.emit]ed from a cancelled [block] will be ignored.
 *
 * After a cancellation, if the [LiveData] becomes active again, the [block] will be re-executed
 * from the beginning. If you would like to continue the operations based on where it was stopped
 * last, you can use the [LiveDataScope.latestValue] function to get the last
 * [LiveDataScope.emit]ed value.

 * If the [block] completes successfully *or* is cancelled due to reasons other than [LiveData]
 * becoming inactive, it *will not* be re-executed even after [LiveData] goes through active
 * inactive cycle.
 *
 * As a best practice, it is important for the [block] to cooperate in cancellation. See kotlin
 * coroutines documentation for details
 * https://kotlinlang.org/docs/reference/coroutines/cancellation-and-timeouts.html.
 *
 * ```
 * // a simple LiveData that receives value 3, 3 seconds after being observed for the first time.
 * val data : LiveData<Int> = liveData {
 *     delay(3000)
 *     emit(3)
 * }
 *
 *
 * // a LiveData that fetches a `User` object based on a `userId` and refreshes it every 30 seconds
 * // as long as it is observed
 * val userId : LiveData<String> = ...
 * val user = userId.switchMap { id ->
 *     liveData {
 *       while(true) {
 *         // note that `while(true)` is fine because the `delay(30_000)` below will cooperate in
 *         // cancellation if LiveData is not actively observed anymore
 *         val data = api.fetch(id) // errors are ignored for brevity
 *         emit(data)
 *         delay(30_000)
 *       }
 *     }
 * }
 *
 * // A retrying data fetcher with doubling back-off
 * val user = liveData {
 *     var backOffTime = 1_000
 *     var succeeded = false
 *     while(!succeeded) {
 *         try {
 *             emit(api.fetch(id))
 *             succeeded = true
 *         } catch(ioError : IOException) {
 *             delay(backOffTime)
 *             backOffTime *= minOf(backOffTime * 2, 60_000)
 *         }
 *     }
 * }
 *
 * // a LiveData that tries to load the `User` from local cache first and then tries to fetch
 * // from the server and also yields the updated value
 * val user = liveData {
 *     // dispatch loading first
 *     emit(LOADING(id))
 *     // check local storage
 *     val cached = cache.loadUser(id)
 *     if (cached != null) {
 *         emit(cached)
 *     }
 *     if (cached == null || cached.isStale()) {
 *         val fresh = api.fetch(id) // errors are ignored for brevity
 *         cache.save(fresh)
 *         emit(fresh)
 *     }
 * }
 *
 * // a LiveData that immediately receives a LiveData<User> from the database and yields it as a
 * // source but also tries to back-fill the database from the server
 * val user = liveData {
 *     val fromDb: LiveData<User> = roomDatabase.loadUser(id)
 *     emitSource(fromDb)
 *     val updated = api.fetch(id) // errors are ignored for brevity
 *     // Since we are using Room here, updating the database will update the `fromDb` LiveData
 *     // that was obtained above. See Room's documentation for more details.
 *     // https://developer.android.com/training/data-storage/room/accessing-data#query-observable
 *     roomDatabase.insert(updated)
 * }
 * ```
 *
 * @param context The CoroutineContext to run the given block in. Defaults to
 * [EmptyCoroutineContext] combined with
 * [Dispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate]
 * @param timeoutInMs The timeout in ms before cancelling the block if there are no active observers
 * ([LiveData.hasActiveObservers]. Defaults to [DEFAULT_TIMEOUT].
 * @param block The block to run when the [LiveData] has active observers.
 */
@OptIn(ExperimentalTypeInference::class)
fun <T> refreshableLiveData(
    context: CoroutineContext = EmptyCoroutineContext,
    timeoutInMs: Long = DEFAULT_TIMEOUT,
    @BuilderInference block: suspend LiveDataScope<T>.() -> Unit
): RefreshableLiveData<T> = CoroutineLiveData(context, timeoutInMs, block)


/**
 * Builds a LiveData that has values yielded from the given [block] that executes on a
 * [LiveDataScope].
 *
 * The [block] starts executing when the returned [LiveData] becomes active ([LiveData.onActive]).
 * If the [LiveData] becomes inactive ([LiveData.onInactive]) while the [block] is executing, it
 * will be cancelled after the [timeout] duration unless the [LiveData] becomes active again
 * before that timeout (to gracefully handle cases like Activity rotation). Any value
 * [LiveDataScope.emit]ed from a cancelled [block] will be ignored.
 *
 * After a cancellation, if the [LiveData] becomes active again, the [block] will be re-executed
 * from the beginning. If you would like to continue the operations based on where it was stopped
 * last, you can use the [LiveDataScope.latestValue] function to get the last
 * [LiveDataScope.emit]ed value.

 * If the [block] completes successfully *or* is cancelled due to reasons other than [LiveData]
 * becoming inactive, it *will not* be re-executed even after [LiveData] goes through active
 * inactive cycle.
 *
 * As a best practice, it is important for the [block] to cooperate in cancellation. See kotlin
 * coroutines documentation for details
 * https://kotlinlang.org/docs/reference/coroutines/cancellation-and-timeouts.html.
 *
 * ```
 * // a simple LiveData that receives value 3, 3 seconds after being observed for the first time.
 * val data : LiveData<Int> = liveData {
 *     delay(3000)
 *     emit(3)
 * }
 *
 *
 * // a LiveData that fetches a `User` object based on a `userId` and refreshes it every 30 seconds
 * // as long as it is observed
 * val userId : LiveData<String> = ...
 * val user = userId.switchMap { id ->
 *     liveData {
 *       while(true) {
 *         // note that `while(true)` is fine because the `delay(30_000)` below will cooperate in
 *         // cancellation if LiveData is not actively observed anymore
 *         val data = api.fetch(id) // errors are ignored for brevity
 *         emit(data)
 *         delay(30_000)
 *       }
 *     }
 * }
 *
 * // A retrying data fetcher with doubling back-off
 * val user = liveData {
 *     var backOffTime = 1_000
 *     var succeeded = false
 *     while(!succeeded) {
 *         try {
 *             emit(api.fetch(id))
 *             succeeded = true
 *         } catch(ioError : IOException) {
 *             delay(backOffTime)
 *             backOffTime *= minOf(backOffTime * 2, 60_000)
 *         }
 *     }
 * }
 *
 * // a LiveData that tries to load the `User` from local cache first and then tries to fetch
 * // from the server and also yields the updated value
 * val user = liveData {
 *     // dispatch loading first
 *     emit(LOADING(id))
 *     // check local storage
 *     val cached = cache.loadUser(id)
 *     if (cached != null) {
 *         emit(cached)
 *     }
 *     if (cached == null || cached.isStale()) {
 *         val fresh = api.fetch(id) // errors are ignored for brevity
 *         cache.save(fresh)
 *         emit(fresh)
 *     }
 * }
 *
 * // a LiveData that immediately receives a LiveData<User> from the database and yields it as a
 * // source but also tries to back-fill the database from the server
 * val user = liveData {
 *     val fromDb: LiveData<User> = roomDatabase.loadUser(id)
 *     emitSource(fromDb)
 *     val updated = api.fetch(id) // errors are ignored for brevity
 *     // Since we are using Room here, updating the database will update the `fromDb` LiveData
 *     // that was obtained above. See Room's documentation for more details.
 *     // https://developer.android.com/training/data-storage/room/accessing-data#query-observable
 *     roomDatabase.insert(updated)
 * }
 * ```
 *
 * * @param context The CoroutineContext to run the given block in. Defaults to
 * [EmptyCoroutineContext] combined with
 * [Dispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate].
 * @param timeout The timeout duration before cancelling the block if there are no active observers
 * ([LiveData.hasActiveObservers].
 * @param block The block to run when the [LiveData] has active observers.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalTypeInference::class)
fun <T> refreshableLiveData(
    context: CoroutineContext = EmptyCoroutineContext,
    timeout: Duration,
    @BuilderInference block: suspend LiveDataScope<T>.() -> Unit
): RefreshableLiveData<T> = CoroutineLiveData(context, timeout.toMillis(), block)

/**
 * Remove all Observer and notify change with [value]
 */
@MainThread
fun <T> LiveData<T>.removeObservers(value: T? = null) {
    if (Looper.getMainLooper().thread !== Thread.currentThread()) {
        throw IllegalStateException("Cannot invoke removeObservers on a background thread")
    }
    try {
        val field = LiveData::class.java.getDeclaredField("mObservers")
        field.isAccessible = true
        val mObservers = (field.get(this) as? SafeIterableMap<*, *>) ?: return
        for ((key, _) in mObservers) {
            @Suppress("UNCHECKED_CAST")
            val observer = key as Observer<in T>
            value?.let {
                observer.onChanged(it)
            }
            removeObserver(observer)
        }
        field.isAccessible = false
    } catch (e: Throwable) {
        Log.d("LiveDataExt", "removeObservers error: ", e)
    }
}

/**
 * Only notify change once， then remove the [observer]
 */
fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer.onChanged(value)
        }
    })
}

/**
 * Only notify change once， then remove the [observer]
 */
fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
    observe(owner, object : Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer.onChanged(value)
        }
    })
}