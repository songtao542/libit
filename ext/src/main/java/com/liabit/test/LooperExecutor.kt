package com.liabit.test

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.TimeUnit

/**
 * Author:         songtao
 * CreateDate:     2020/6/15 14:41
 */
@Suppress("MemberVisibilityCanBePrivate")
class LooperExecutor(looper: Looper) : AbstractExecutorService() {
    val handler: Handler = Handler(looper)

    /**
     * Returns the thread for this executor
     */

    val thread: Thread = handler.looper.thread

    /**
     * Returns the looper for this executor
     */
    val looper: Looper = handler.looper

    override fun execute(runnable: Runnable) {
        if (handler.looper == Looper.myLooper()) {
            runnable.run()
        } else {
            handler.post(runnable)
        }
    }

    /**
     * Same as execute, but never runs the action inline.
     */
    fun post(runnable: Runnable?) {
        handler.post(runnable!!)
    }

    fun postDelayed(runnable: Runnable?, delay: Long) {
        handler.postDelayed(runnable!!, delay)
    }

    /**
     * Set the priority of a thread, based on Linux priorities.
     *
     * @param priority Linux priority level, from -20 for highest scheduling priority
     * to 19 for lowest scheduling priority.
     * @see Process.setThreadPriority
     */
    fun setThreadPriority(priority: Int) {
        Process.setThreadPriority((thread as HandlerThread).threadId, priority)
    }

    override fun isShutdown(): Boolean {
        return false
    }

    override fun isTerminated(): Boolean {
        return false
    }

    /**
     * Not supported and throws an exception when used.
     */
    @Deprecated("")
    override fun awaitTermination(l: Long, timeUnit: TimeUnit): Boolean {
        throw UnsupportedOperationException()
    }

    /**
     * Not supported and throws an exception when used.
     */
    @Deprecated("")
    override fun shutdownNow(): List<Runnable> {
        throw UnsupportedOperationException()
    }

    /**
     * Not supported and throws an exception when used.
     */
    @Deprecated("", replaceWith = ReplaceWith(""))
    override fun shutdown() {
        throw UnsupportedOperationException()
    }
}
