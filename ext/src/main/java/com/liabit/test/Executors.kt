package com.liabit.test

import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Author:         songtao
 * CreateDate:     2020/6/15 14:40
 */
object Executors {
    // These values are same as that in {@link AsyncTask}.
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val CORE_POOL_SIZE = CPU_COUNT + 1
    private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
    private const val KEEP_ALIVE = 1L

    /**
     * An [Executor] to be used with async task with no limit on the queue size.
     */
    val THREAD_POOL_EXECUTOR: Executor = ThreadPoolExecutor(
        CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, LinkedBlockingQueue()
    )

    /**
     * Returns the executor for running tasks on the main thread.
     */
    val MAIN_EXECUTOR: LooperExecutor = LooperExecutor(Looper.getMainLooper())

    /**
     * A background executor for using time sensitive actions where user is waiting for response.
     */
    val UI_HELPER_EXECUTOR: LooperExecutor = LooperExecutor(createAndStartNewForegroundLooper("UiThreadHelper"))

    /**
     * Executor used for running Launcher model related tasks (eg loading icons or updated db)
     */
    val BACKGROUND_EXECUTOR: LooperExecutor = LooperExecutor(createAndStartNewLooper("BackgroundThread"))

    /**
     * Utility method to get a started handler thread statically with the provided priority
     * Utility method to get a started handler thread statically
     */
    private fun createAndStartNewLooper(name: String, priority: Int = Process.THREAD_PRIORITY_DEFAULT): Looper {
        val thread = HandlerThread(name, priority)
        thread.start()
        return thread.looper
    }

    /**
     * Similar to [.createAndStartNewLooper], but starts the thread with
     * foreground priority.
     * Think before using
     */
    private fun createAndStartNewForegroundLooper(name: String): Looper {
        return createAndStartNewLooper(name, Process.THREAD_PRIORITY_FOREGROUND)
    }
}
