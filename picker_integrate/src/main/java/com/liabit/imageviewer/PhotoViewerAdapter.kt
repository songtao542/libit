package com.liabit.imageviewer

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PhotoViewerAdapter(private val parentFragment: PhotoViewerFragment, private val deletable: Boolean)
    : FragmentStateAdapter(parentFragment) {

    private var forceNotify = false

    private val mUris = ArrayList<Uri>()

    fun setUris(uris: List<Uri>?) {
        if (uris.isNullOrEmpty()) {
            mUris.clear()
        } else {
            mUris.addAll(uris)
        }
        notifyDataSetChanged()
    }

    var onPhotoSingleTapListener: PhotoFragment.OnPhotoSingleTapListener? = null

    fun forceNotifyDataSetChanged() {
        forceNotify = true
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = mUris.size

    override fun createFragment(position: Int): Fragment {
        return parentFragment.newPhotoViewFragment(mUris[position]).also { it.setOnPhotoSingleTapListener(onPhotoSingleTapListener) }
    }
}