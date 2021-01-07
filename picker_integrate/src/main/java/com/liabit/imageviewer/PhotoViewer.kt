package com.liabit.imageviewer

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

@Suppress("MemberVisibilityCanBePrivate")
object PhotoViewer {

    const val REQUEST_CODE = 920
    const val DELETED = "deleted"

    @JvmStatic
    fun start(
            context: Context,
            uris: List<Uri>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
    ) {
        if (context is Activity) {
            PhotoViewerActivity.startActivityForResult(context, REQUEST_CODE, uris, currentIndex, deletable)
        } else {
            PhotoViewerActivity.start(context, uris, currentIndex, deletable)
        }
    }

    @JvmStatic
    fun start(
            fragment: Fragment,
            uris: List<Uri>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
    ) {
        PhotoViewerActivity.startActivityForResult(fragment, REQUEST_CODE, uris, currentIndex, deletable)
    }

    @JvmStatic
    fun startPhotoViewer(
            context: Context,
            photos: ArrayList<Photo>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
    ) {
        if (context is Activity) {
            PhotoViewerActivity.startPhotoViewer(context, REQUEST_CODE, photos, currentIndex, deletable)
        } else {
            PhotoViewerActivity.startPhotoViewer(context, photos, currentIndex, deletable)
        }
    }

    @JvmStatic
    fun startPhotoViewer(
            fragment: Fragment,
            photos: ArrayList<Photo>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
    ) {
        PhotoViewerActivity.startPhotoViewer(fragment, REQUEST_CODE, photos, currentIndex, deletable)
    }

    @JvmStatic
    fun start(
            fragmentManager: FragmentManager,
            @IdRes containerViewId: Int,
            uris: List<Uri>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
            listener: ((index: Int, photo: Photo) -> Unit)? = null,
    ) {
        val fragment = PhotoViewerFragment.fromUris(ArrayList(uris), currentIndex, deletable)
        fragment.setOnDeleteListener(listener)
        fragmentManager.beginTransaction()
                .add(containerViewId, fragment)
                .commitAllowingStateLoss()
    }

}