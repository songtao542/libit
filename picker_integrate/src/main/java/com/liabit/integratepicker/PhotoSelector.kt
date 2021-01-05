package com.liabit.integratepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
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

    private val mUris by lazy { ArrayList<Uri>() }

    private var mFlowLayout: FlowLayout? = null
    private var mFragment: Fragment? = null
    private var mMaxShow: Int? = null

    /**
     * The select photo uri list
     */
    val uris: List<Uri> get() = mUris

    fun bind(flowLayout: FlowLayout): PhotoSelector {
        mFlowLayout = flowLayout
        mFlowLayout?.setAdapter(mAdapter)
        mAdapter.setOnAddClickListener {
            val max = mMaxShow ?: 1
            mFragment?.also {
                Picker.pickPhoto(it, max = max)
            } ?: run {
                if (context is Activity) {
                    Picker.pickPhoto(context, max = max)
                }
            }
        }
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