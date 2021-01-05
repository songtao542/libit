package com.liabit.imageviewer

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.liabit.integratepicker.R
import com.liabit.swipeback.SwipeBackLayout

@Suppress("unused")
open class PhotoViewerFragment : Fragment(), PhotoFragment.OnPhotoSingleTapListener, KeyEventListener, SwipeBackLayout.OnSwipeBackListener {

    companion object {
        const val URI_LIST = "uri_list"
        const val INDEX = "index"
        const val DELETABLE = "deletable"
        const val DELETED = "deleted"

        @JvmStatic
        fun newInstance(uris: ArrayList<Uri>, currentIndex: Int, deletable: Boolean) = PhotoViewerFragment().apply {
            this.arguments = Bundle().apply {
                putParcelableArrayList(URI_LIST, uris)
                putInt(INDEX, currentIndex)
                putBoolean(DELETABLE, deletable)
            }
        }
    }

    protected lateinit var adapter: PhotoViewerAdapter
    private var uris: ArrayList<Uri>? = null
    private var index: Int = 0
    private var deletable = false

    private val deletedUris by lazy { ArrayList<Uri>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uris = it.getParcelableArrayList(URI_LIST)
            index = it.getInt(INDEX, 0)
            deletable = it.getBoolean(DELETABLE, false)
        }
    }

    private lateinit var viewPager: ViewPager2
    private lateinit var delete: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.p_fragment_photo_viewer, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        delete = view.findViewById(R.id.delete)
        return view
    }

    @Suppress("CascadeIf")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (deletable) {
            delete.setOnClickListener {
                delete()
            }
        } else {
            delete.visibility = View.GONE
        }
        viewPager.offscreenPageLimit = 3
        adapter = PhotoViewerAdapter(this, deletable)
        adapter.onPhotoSingleTapListener = this
        viewPager.adapter = adapter
        if (uris != null) {
            adapter.setUris(uris)
        }
        viewPager.currentItem = index
    }

    open fun newPhotoViewFragment(uri: Uri): PhotoFragment {
        return PhotoFragment.newInstance(uri, arguments)
    }

    override fun onPhotoSingleTap() {
        finish()
    }

    private fun delete() {
        deleteUri()
    }

    private fun deleteUri(): Boolean {
        if (uris != null) {
            val uris = uris!!
            if (uris.size > 0) {
                val index = viewPager.currentItem
                val item = uris[index]
                uris.remove(item)
                deletedUris.add(item)
                mOnDeleteListener?.invoke(index, item)
                if (uris.size == 0) {
                    finish()
                } else {
                    adapter.notifyDataSetChanged()
                }
            }
            return true
        } else {
            return false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setActivityResult()
        }
        return false
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    override fun onViewPositionChanged(view: View?, swipeBackFraction: Float, swipeBackFactor: Float) {
        view?.alpha = 1f - swipeBackFraction
    }

    override fun onViewSwipeFinished(view: View?, isEnd: Boolean) {
        if (isEnd) {
            finish()
        }
    }

    protected open fun finish() {
        setActivityResult()
        activity?.finish()
    }

    private fun setActivityResult() {
        if (uris != null) {
            if (mOnResultListener != null) {
                mOnResultListener?.invoke(deletedUris)
            } else {
                val result = Intent().apply {
                    putParcelableArrayListExtra(DELETED, deletedUris)
                }
                activity?.setResult(Activity.RESULT_OK, result)
            }
        }
    }

    private var mOnDeleteListener: ((index: Int, uri: Uri) -> Unit)? = null

    fun setOnDeleteListener(listener: ((index: Int, uri: Uri) -> Unit)?) {
        this.mOnDeleteListener = listener
    }

    private var mOnResultListener: ((deletedUris: List<Uri>) -> Unit)? = null

    fun setOnResultListener(listener: ((deletedUris: List<Uri>) -> Unit)) {
        mOnResultListener = listener
    }

}
