package com.liabit.imageviewer

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager

object PhotoViewer {

    const val REQUEST_CODE = 920

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
            fragmentManager: FragmentManager,
            @IdRes containerViewId: Int,
            uris: List<Uri>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
            listener: ((index: Int, uri: Uri) -> Unit)? = null,
    ) {
        val fragment = PhotoViewerFragment.newInstance(ArrayList(uris), currentIndex, deletable)
        fragment.setOnDeleteListener(listener)
        fragmentManager.beginTransaction()
                .add(containerViewId, fragment)
                .commitAllowingStateLoss()
    }

}