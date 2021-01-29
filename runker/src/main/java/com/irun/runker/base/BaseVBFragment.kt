package com.irun.runker.base

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.irun.runker.R
import com.irun.runker.widget.ProgressBar
import com.liabit.viewbinding.genericBinding
import com.liabit.viewbinding.inflate
import kotlinx.coroutines.CoroutineScope
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:05
 */
abstract class BaseVBFragment<VB : ViewBinding> : Fragment(), CoroutineScope {

    protected val binding by genericBinding<VB>()

    override val coroutineContext: CoroutineContext get() = viewLifecycleOwner.lifecycleScope.coroutineContext

    private var mProgressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutResourceId = getLayoutResource()
        return if (layoutResourceId != 0) {
            inflater.inflate(getLayoutResource(), container, false)
        } else {
            inflate<VB>(this, inflater, container)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onInitialize(savedInstanceState)
    }

    protected open fun getLayoutResource(): Int = 0

    protected open fun onInitialize(savedInstanceState: Bundle?) {
    }

    protected open fun showToast(message: CharSequence?) {
        val context = context ?: return
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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
        val context = context ?: return null
        val decorView = getDecorView()
        if (decorView != null) {
            if (mProgressBar == null) {
                mProgressBar = ProgressBar(context)
                val size = resources.getDimension(R.dimen.progress_bar_size).toInt()
                val lp = FrameLayout.LayoutParams(size, size, Gravity.CENTER)
                decorView.addView(mProgressBar, lp)
            }
        }
        return mProgressBar
    }

    /**
     * @return 最靠近根节点的 FrameLayout
     */
    protected open fun getDecorView(): FrameLayout? {
        val view = view ?: return null
        val frameLayouts = ArrayList<FrameLayout>()
        var parent = view.parent
        while (parent != null) {
            if (parent is FrameLayout) {
                frameLayouts.add(parent)
            }
            parent = parent.parent
        }
        return if (frameLayouts.isEmpty()) null else frameLayouts[frameLayouts.size - 1]
    }

}