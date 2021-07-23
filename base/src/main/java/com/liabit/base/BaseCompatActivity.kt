package com.liabit.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.liabit.autoclear.autoClear
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 */
@Suppress("MemberVisibilityCanBePrivate")
open class BaseCompatActivity : AppCompatActivity(), ProgressDialog {

    private val mHandler = Handler(Looper.getMainLooper())

    private var mLoadingDialog: LoadingDialog? = null

    private var mStartShowDialogTime = 0L
    private val mDismissDialogAction by lazy { Runnable { dismissDialog() } }

    private val mNetworkStateMonitor by autoClear { NetworkStateMonitor(this) }

    /**
     * 网络是否可用
     */
    val isNetworkAvailable: Boolean get() = mNetworkStateMonitor.isNetworkAvailable()

    /**
     * 网络是否不可用
     */
    val isNetworkUnavailable: Boolean get() = !isNetworkAvailable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = onCreateView(LayoutInflater.from(this), savedInstanceState)
        if (view != null) {
            setContentView(view)
            onViewCreated(view, savedInstanceState)
        }
    }

    protected open fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        return null
    }

    protected open fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

    /**
     * @param resId 提示信息
     */
    @MainThread
    override fun showDialog(@StringRes resId: Int, cancellable: Boolean) {
        showDialog(getString(resId), cancellable)
    }

    /**
     * @param msg 提示信息
     */
    @MainThread
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

    @MainThread
    protected open fun onCreateLoadingDialog(): LoadingDialog {
        return LoadingDialogImpl(this)
    }

    @MainThread
    private fun getLoadingDialog(): LoadingDialog {
        return mLoadingDialog ?: onCreateLoadingDialog().also { mLoadingDialog = it }
    }

    fun observeNetwork(lifecycleOwner: LifecycleOwner, observer: Observer<Boolean>) {
        mNetworkStateMonitor.observe(lifecycleOwner, observer)
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