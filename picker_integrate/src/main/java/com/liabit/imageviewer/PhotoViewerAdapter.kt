package com.liabit.imageviewer

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

class PhotoViewerAdapter(private val parentFragment: PhotoViewerFragment, private val deletable: Boolean)
    : FragmentStateAdapter(parentFragment) {

    private var mPhotos: List<Photo> = Collections.emptyList()

    fun setPhotos(photos: List<Photo>?) {
        mPhotos = if (photos.isNullOrEmpty()) Collections.emptyList() else photos
        notifyDataSetChanged()
    }

    var onPhotoSingleTapListener: PhotoFragment.OnPhotoSingleTapListener? = null

    override fun getItemCount(): Int = mPhotos.size

    override fun getItemId(position: Int): Long {
        return mPhotos[position].uri.hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        for (photo in mPhotos) {
            if (itemId == photo.uri.hashCode().toLong()) {
                return true
            }
        }
        return false
    }

    override fun createFragment(position: Int): Fragment {
        return parentFragment.newPhotoViewFragment(mPhotos[position]).apply {
            setIndex(position)
            setOnPhotoSingleTapListener(onPhotoSingleTapListener)
        }
    }
}