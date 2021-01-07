package com.liabit.imageviewer

import android.app.Activity
import android.content.Intent
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

@Suppress("unused")
open class PhotoViewerFragment : Fragment(), PhotoFragment.OnPhotoSingleTapListener, KeyEventListener {

    companion object {
        const val PHOTO_LIST = "photo_list"
        const val INDEX = "index"
        const val DELETABLE = "deletable"

        @JvmStatic
        fun fromUris(uris: ArrayList<Uri>, currentIndex: Int, deletable: Boolean) = PhotoViewerFragment().apply {
            this.arguments = Bundle().apply {
                putParcelableArrayList(PHOTO_LIST, Photo.fromUriList(uris))
                putInt(INDEX, currentIndex)
                putBoolean(DELETABLE, deletable)
            }
        }

        @JvmStatic
        fun fromPhotos(photos: ArrayList<Photo>, currentIndex: Int, deletable: Boolean) = PhotoViewerFragment().apply {
            this.arguments = Bundle().apply {
                putParcelableArrayList(PHOTO_LIST, photos)
                putInt(INDEX, currentIndex)
                putBoolean(DELETABLE, deletable)
            }
        }
    }

    private lateinit var mAdapter: PhotoViewerAdapter
    private var mPhotos: ArrayList<Photo>? = null
    private var mIndex: Int = 0
    private var mDeletable = false

    private val mDeletedPhotos by lazy { ArrayList<Photo>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPhotos = it.getParcelableArrayList(PHOTO_LIST)
            mIndex = it.getInt(INDEX, 0)
            mDeletable = it.getBoolean(DELETABLE, false)
        }
    }

    private lateinit var mViewPager: ViewPager2
    private lateinit var mDelete: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.p_fragment_photo_viewer, container, false)
        mViewPager = view.findViewById(R.id.viewPager)
        mDelete = view.findViewById(R.id.delete)
        return view
    }

    @Suppress("CascadeIf")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (mDeletable) {
            mDelete.setOnClickListener {
                deletePhoto()
            }
        } else {
            mDelete.visibility = View.GONE
        }
        mViewPager.offscreenPageLimit = 3
        mAdapter = PhotoViewerAdapter(this, mDeletable)
        mAdapter.onPhotoSingleTapListener = this
        mViewPager.adapter = mAdapter
        if (mPhotos != null) {
            mAdapter.setPhotos(mPhotos)
        }
        mViewPager.setCurrentItem(mIndex, false)
    }

    open fun newPhotoViewFragment(photo: Photo): PhotoFragment {
        return PhotoFragment.newInstance(photo, arguments)
    }

    override fun onPhotoSingleTap() {
        finish()
    }

    private fun deletePhoto(): Boolean {
        val photos = mPhotos ?: return false
        if (photos.size > 0) {
            val index = mViewPager.currentItem
            val item = photos[index]
            photos.remove(item)
            mDeletedPhotos.add(item)
            mOnDeleteListener?.invoke(index, item)
            if (photos.size == 0) {
                finish()
            } else {
                mAdapter.notifyDataSetChanged()
            }
        }
        return true
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

    protected open fun finish() {
        setActivityResult()
        activity?.finish()
    }

    private fun setActivityResult() {
        mPhotos?.also {
            if (mOnResultListener != null) {
                mOnResultListener?.invoke(mDeletedPhotos)
            } else {
                val result = Intent().apply {
                    putParcelableArrayListExtra(PhotoViewer.DELETED, mDeletedPhotos)
                }
                activity?.setResult(Activity.RESULT_OK, result)
            }
        }
    }

    private var mOnDeleteListener: ((index: Int, photo: Photo) -> Unit)? = null

    fun setOnDeleteListener(listener: ((index: Int, photo: Photo) -> Unit)?) {
        this.mOnDeleteListener = listener
    }

    private var mOnResultListener: ((deletedPhotos: List<Photo>) -> Unit)? = null

    fun setOnResultListener(listener: ((deletedPhotos: List<Photo>) -> Unit)) {
        mOnResultListener = listener
    }

}
