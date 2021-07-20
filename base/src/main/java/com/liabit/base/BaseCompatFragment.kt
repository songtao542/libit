package com.liabit.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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
 * CreateDate:     2020/12/4 18:05
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseCompatFragment : Fragment(), Toolbar.OnMenuItemClickListener, ProgressDialog, OnBackListener, BackEventDispatcher {

    @Suppress("PrivatePropertyName")
    private val TAG = javaClass.simpleName

    private var mLoadingDialog: LoadingDialog? = null

    private var mStartShowDialogTime = 0L
    private val mDismissDialogAction by lazy { Runnable { dismissDialog() } }
    private val mHandler = Handler(Looper.getMainLooper())

    private val mNetworkStateMonitor by autoClear { NetworkStateMonitor(requireContext()) }

    /**
     * 注意在 Context 初始化之后调用
     */
    val isNetworkAvailable: Boolean get() = mNetworkStateMonitor.isNetworkAvailable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onInitialize(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return
        onViewCreated(activity)
        onViewCreated(activity, savedInstanceState)
    }

    protected open fun onViewCreated(activity: FragmentActivity) {}

    /**
     *  [onInitialize] 会在 [onCreate] 中回调，建议在此方法中做网络数据初始化，避免每次初始化view都去调用网络接口
     */
    protected open fun onInitialize(savedInstanceState: Bundle?) {
    }

    /**
     * [onViewCreated] 会在 [onViewCreated] 中回调，建议在这里处理view相关的初始化，
     * 比如：设置监听器，observe livedata
     */
    protected open fun onViewCreated(activity: FragmentActivity, savedInstanceState: Bundle?) {
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

    fun enableOptionsMenu(toolbar: Toolbar, showTitle: Boolean = true, menu: Int = 0) {
        activity?.let {
            setHasOptionsMenu(true)
            if (it is AppCompatActivity) {
                if (menu != 0) {
                    toolbar.inflateMenu(menu)
                }
                toolbar.setOnMenuItemClickListener(this)
                it.supportActionBar?.setDisplayShowTitleEnabled(showTitle)
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return false
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        mHandler.removeCallbacksAndMessages(null)
    }

    /**
     * @param resId 提示信息
     */
    @MainThread
    override fun showDialog(@StringRes resId: Int, cancellable: Boolean) {
        showDialog(getString(resId), cancellable)
    }

    @MainThread
    override fun showDialog(msg: String?, cancellable: Boolean) {
        mStartShowDialogTime = SystemClock.elapsedRealtime()
        mHandler.removeCallbacks(mDismissDialogAction)
        getLoadingDialog()?.let {
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
    protected open fun onCreateLoadingDialog(): LoadingDialog? {
        val context = context ?: return null
        return LoadingDialogImpl(context)
    }

    @MainThread
    private fun getLoadingDialog(): LoadingDialog? {
        return mLoadingDialog ?: onCreateLoadingDialog()?.also { mLoadingDialog = it }
    }

    /**
     * 往 Child Fragment 中分发 BackEvent
     */
    override fun dispatchBackEvent(): Boolean {
        var primary = childFragmentManager.primaryNavigationFragment
        if (primary is NavHostFragment) {
            primary = primary.childFragmentManager.primaryNavigationFragment
        }
        if ((primary as? BackEventDispatcher)?.dispatchBackEvent() == true) {
            return true
        }
        return onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        return handleBackEvent(view)
    }

    /**
     * 在控件树中查找需要处理 BackEvent 的控件
     */
    private fun handleBackEvent(view: View?): Boolean {
        if (view is OnBackListener) {
            val handled = view.onBackPressed()
            if (handled) {
                return true
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                val handled = handleBackEvent(child)
                if (handled) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 只能在 [onCreateView] 之后调用，因为使用了 [getViewLifecycleOwner] lifecycleScope
     */
    fun launchWhenCreated(block: suspend CoroutineScope.() -> Unit): Job = viewLifecycleOwner.lifecycleScope.launchWhenCreated(block)

    /**
     * 只能在 [onCreateView] 之后调用，因为使用了 [getViewLifecycleOwner] lifecycleScope
     */
    fun launchWhenStarted(block: suspend CoroutineScope.() -> Unit): Job = viewLifecycleOwner.lifecycleScope.launchWhenStarted(block)

    /**
     * 只能在 [onCreateView] 之后调用，因为使用了 [getViewLifecycleOwner] lifecycleScope
     */
    fun launchWhenResumed(block: suspend CoroutineScope.() -> Unit): Job = viewLifecycleOwner.lifecycleScope.launchWhenResumed(block)

    /**
     * 只能在 [onCreateView] 之后调用，因为使用了 [getViewLifecycleOwner] lifecycleScope
     */
    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewLifecycleOwner.lifecycleScope.launch(context, start, block)

    /**
     * 只能在 [onCreateView] 之后调用，因为使用了 [getViewLifecycleOwner] lifecycleScope
     */
    fun <T> async(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
    ): Deferred<T> = viewLifecycleOwner.lifecycleScope.async(context, start, block)
}