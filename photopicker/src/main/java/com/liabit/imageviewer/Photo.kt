package com.liabit.imageviewer

import android.graphics.Rect
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(
        val uri: Uri,
        val bounds: Rect? = null,
        val isVideo: Boolean = false,
) : Parcelable {
    companion object {
        @JvmStatic
        fun fromUriList(uris: List<Uri>): ArrayList<Photo> {
            val photos = ArrayList<Photo>()
            uris.forEach { photos.add(Photo(it)) }
            return photos
        }

        @JvmStatic
        fun toUriList(photos: List<Photo>): List<Uri> {
            val uris = ArrayList<Uri>()
            photos.forEach { uris.add(it.uri) }
            return uris
        }
    }
}
