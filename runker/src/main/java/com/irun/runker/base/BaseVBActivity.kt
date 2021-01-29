package com.irun.runker.base

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.irun.runker.R
import com.irun.runker.widget.ProgressBar
import com.liabit.viewbinding.genericBinding
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 */
open class BaseVBActivity<VB : ViewBinding> : AppCompatActivity(), CoroutineScope {

    protected val binding by genericBinding<VB>()

    private var mProgressBar: ProgressBar? = null

    override val coroutineContext: CoroutineContext get() = lifecycleScope.coroutineContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!onSetContentView()) {
            setContentView(binding.root)
        }
        onInitialize(savedInstanceState)
    }

    protected open fun onSetContentView(): Boolean {
        return false
    }

    protected open fun onInitialize(savedInstanceState: Bundle?) {
    }

    protected open fun showToast(message: CharSequence?) {
        message?.let {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 显示用户等待框
     *
     * @param message 提示信息
     */
    protected open fun showProgressBar(message: CharSequence?) {
        getProgressBar()?.let {
            it.setText(message)
            it.visibility = View.VISIBLE
        }
    }

    /**
     * 隐藏等待框
     */
    protected open fun dismissDialog() {
        getProgressBar()?.visibility = View.GONE
    }

    protected open fun getProgressBar(): ProgressBar? {
        val decorView: View = window.decorView
        if (decorView is FrameLayout) {
            if (mProgressBar == null) {
                mProgressBar = ProgressBar(this)
                val size = resources.getDimension(R.dimen.progress_bar_size).toInt()
                val lp = FrameLayout.LayoutParams(size, size, Gravity.CENTER)
                decorView.addView(mProgressBar, lp)
            }
        }
        return mProgressBar
    }


}