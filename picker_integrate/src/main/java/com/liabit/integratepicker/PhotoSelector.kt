package com.liabit.integratepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.zhihu.matisse.Matisse

/**
 * 配合 FlowLayout 用于选择图片
 * <p>
 * <com.liabit.integratepicker.FlowLayout
 *      android:id="@+id/photos"
 *      android:layout_width="match_parent"
 *      android:layout_height="wrap_content"
 *      android:layout_margin="16dp"
 *      app:alignContent="flex_start"
 *      app:alignItems="flex_start"
 *      app:column="4"
 *      app:flexWrap="wrap"
 *      app:justifyContent="flex_start"
 *      app:space="5dp"
 *      app:square="true" />
 * </p>
 */
class PhotoSelector(private val context: Context) {

    private val mAdapter by lazy { PhotoFlowAdapter(context) }

    private var mFlowLayout: FlowLayout? = null
    private var mFragment: Fragment? = null
    private var mMaxShow: Int? = null
    private var mIncrementalSelection: Boolean = false

    private var mUris = ArrayList<Uri>()

    /**
     * The select photo uri list
     */
    val uris: List<Uri> get() = mUris

    fun bind(flowLayout: FlowLayout): PhotoSelector {
        mFlowLayout = flowLayout
        mAdapter.setOnAddClickListener {
            val maxShow = mMaxShow ?: 1
            val max = if (mMaxShow != null && mIncrementalSelection) {
                maxShow - uris.size
            } else {
                maxShow
            }
            if (max <= 0) {
                return@setOnAddClickListener
            }
            mFragment?.also {
                Picker.pickPhoto(it, max = max)
            } ?: run {
                if (context is Activity) {
                    Picker.pickPhoto(context, max = max)
                }
            }
        }
        mFlowLayout?.setAdapter(mAdapter)
        return this
    }

    fun bind(fragment: Fragment, flowLayout: FlowLayout): PhotoSelector {
        mFragment = fragment
        bind(flowLayout)
        return this
    }

    fun setMaxShow(max: Int): PhotoSelector {
        mMaxShow = max
        mAdapter.setMaxShow(max)
        return this
    }

    /**
     * 是否使用增量选择
     */
    fun setIncrementalSelection(incrementalSelection: Boolean): PhotoSelector {
        mIncrementalSelection = incrementalSelection
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
        mFlowLayout?.notifyAdapterSizeChanged()
        return this
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Picker.REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            val uris = Matisse.obtainResult(data)
            if (uris != null) {
                if (!mIncrementalSelection) {
                    mUris.clear()
                }
                mUris.addAll(uris)
                mAdapter.setUris(mUris)
                mFlowLayout?.notifyAdapterSizeChanged()
            }
        }
    }

}