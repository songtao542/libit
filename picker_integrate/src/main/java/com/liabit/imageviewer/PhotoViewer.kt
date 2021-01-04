package com.liabit.imageviewer

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager

object PhotoViewer {

    @JvmStatic
    fun start(
            context: Context,
            uris: List<Uri>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
    ) {
        PhotoViewerActivity.start(context, uris, currentIndex, deletable)
    }

    @JvmStatic
    fun startForResult(
            activity: Activity,
            requestCode: Int,
            uris: List<Uri>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
    ) {
        PhotoViewerActivity.startActivityForResult(activity, requestCode, uris, currentIndex, deletable)
    }

    @JvmStatic
    fun start(
            fragmentManager: FragmentManager,
            @IdRes containerViewId: Int,
            uris: List<Uri>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
    ) {
        fragmentManager.beginTransaction()
                .add(containerViewId, PhotoViewerFragment.newInstance(ArrayList(uris), currentIndex, deletable))
                .commitAllowingStateLoss()
    }

}