package com.liabit.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
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
    private val mHandler by lazy { Handler(Looper.getMainLooper()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onInitialize(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return
        onBeforeBindView()
        onBindView(activity, savedInstanceState)
    }

    protected open fun onBeforeBindView() {}

    /**
     *  [onInitialize] 会在 [onCreate] 中回调，建议在此方法中做网络数据初始化，避免每次初始化view都去调用网络接口
     */
    protected open fun onInitialize(savedInstanceState: Bundle?) {
    }

    /**
     * [onBindView] 会在 [onViewCreated] 中回调，建议在这里处理view相关的初始化，
     * 比如：设置监听器，observe livedata
     */
    protected open fun onBindView(activity: FragmentActivity, savedInstanceState: Bundle?) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        mHandler.removeCallbacksAndMessages(null)
    }

    /**
     * @param resId 提示信息
     */
    override fun showDialog(@StringRes resId: Int, cancellable: Boolean) {
        showDialog(getString(resId), cancellable)
    }

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

    protected open fun getLoadingDialog(): LoadingDialog? {
        val context = context ?: return null
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialogImpl(context)
        }
        return mLoadingDialog
    }

    /**
     *
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