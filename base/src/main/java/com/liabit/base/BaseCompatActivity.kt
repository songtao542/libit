package com.liabit.base

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 */
@Suppress("MemberVisibilityCanBePrivate")
open class BaseCompatActivity : AppCompatActivity(), ProgressDialog {

    private val mHandler by lazy { Handler(Looper.getMainLooper()) }

    private var mLoadingDialog: LoadingDialog? = null

    private var mStartShowDialogTime = 0L
    private val mDismissDialogAction by lazy { Runnable { dismissDialog() } }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

    /**
     * @param resId 提示信息
     */
    override fun showDialog(@StringRes resId: Int, cancellable: Boolean) {
        showDialog(getString(resId), cancellable)
    }

    /**
     * @param msg 提示信息
     */
    override fun showDialog(msg: String?, cancellable: Boolean) {
        mStartShowDialogTime = SystemClock.elapsedRealtime()
        mHandler.removeCallbacks(mDismissDialogAction)
        getLoadingDialog().let {
            it.setCancelable(cancellable)
            it.setText(msg)
            it.show()
        }
    }

    /**
     * 隐藏等待框
     */
    override fun dismissDialog(delayMillis: Long) {
        if (delayMillis > 0) {
            val delay = delayMillis - (SystemClock.elapsedRealtime() - mStartShowDialogTime)
            if (delay > 0) {
                postDelayed(mDismissDialogAction, delay)
            } else {
                mLoadingDialog?.dismiss()
            }
        } else {
            mLoadingDialog?.dismiss()
        }
    }

    protected open fun onCreateLoadingDialog(): LoadingDialog {
        return LoadingDialogImpl(this)
    }

    private fun getLoadingDialog(): LoadingDialog {
        return mLoadingDialog ?: onCreateLoadingDialog().also { mLoadingDialog = it }
    }

    fun post(runnable: Runnable) {
        mHandler.post(runnable)
    }

    fun postDelayed(runnable: Runnable, delayMillis: Long) {
        mHandler.postDelayed(runnable, delayMillis)
    }

    /**
     * This method depend on every FragmentTransaction invoke
     * {@link FragmentTransaction#setPrimaryNavigationFragment(Fragment)}
     * so that the child Fragment can be retrieved with
     * {@link FragmentManager#getPrimaryNavigationFragment()}.
     */
    private fun dispatchBackPressedEvent(): Boolean {
        var primary = supportFragmentManager.primaryNavigationFragment
        if (primary is NavHostFragment) {
            primary = primary.childFragmentManager.primaryNavigationFragment
        }
        if ((primary as? BackEventDispatcher)?.dispatchBackEvent() == true) {
            return true
        }
        return false
    }

    override fun onBackPressed() {
        if (dispatchBackPressedEvent()) {
            return
        }
        super.onBackPressed()
    }

    fun launchWhenCreated(block: suspend CoroutineScope.() -> Unit): Job = lifecycleScope.launchWhenCreated(block)

    fun launchWhenStarted(block: suspend CoroutineScope.() -> Unit): Job = lifecycleScope.launchWhenStarted(block)

    fun launchWhenResumed(block: suspend CoroutineScope.() -> Unit): Job = lifecycleScope.launchWhenResumed(block)

    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = lifecycleScope.launch(context, start, block)

    fun <T> async(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
    ): Deferred<T> = lifecycleScope.async(context, start, block)
}