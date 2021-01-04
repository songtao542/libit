package com.liabit.integratepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.zhihu.matisse.Matisse

class PhotoSelector(private val context: Context) {

    private val mAdapter by lazy { PhotoFlowAdapter(context) }

    private val mUris by lazy { ArrayList<Uri>() }

    private var mFlowLayout: FlowLayout? = null

    /**
     * The select photo uri list
     */
    val uris: List<Uri> get() = mUris

    fun bind(flowLayout: FlowLayout): PhotoSelector {
        mFlowLayout = flowLayout
        mFlowLayout?.setAdapter(mAdapter)
        return this
    }

    fun setMaxShow(max: Int): PhotoSelector {
        mAdapter.setMaxShow(max)
        return this
    }

    fun setAddButtonStyle(style: PhotoFlowAdapter.AddButtonStyle): PhotoSelector {
        mAdapter.setAddButtonStyle(style)
        return this
    }

    fun setShowAddWhenFull(showAddWhenFull: Boolean): PhotoSelector {
        mAdapter.setShowAddWhenFull(showAddWhenFull)
        return this
    }

    fun setLastAsAdd(lastAsAdd: Boolean): PhotoSelector {
        mAdapter.setLastAsAdd(lastAsAdd)
        return this
    }

    fun setOnAddClickListener(listener: ((size: Int) -> Unit)?): PhotoSelector {
        mAdapter.setOnAddClickListener(listener)
        return this
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Picker.REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            val paths = Matisse.obtainPathResult(data)
            val uris = Matisse.obtainResult(data)
            paths?.let {
                mUris.addAll(uris)
                mFlowLayout?.notifyAdapterSizeChanged()
                return@let
            }
        }
    }

}