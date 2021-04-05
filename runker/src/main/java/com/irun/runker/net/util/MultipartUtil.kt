package com.irun.runker.net.util

import android.graphics.BitmapFactory
import android.text.TextUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object MultipartUtil {

    fun getMultipartList(name: String, files: List<File>): List<MultipartBody.Part> {
        val parts = ArrayList<MultipartBody.Part>(files.size)
        for (file in files) {
            val mediaType = MediaFile.getFileType(file.path)
            val mimeType = when {
                mediaType != null -> mediaType.mimeType
                file.path.contains(".jpeg", true) -> "image/jpeg"
                file.path.contains(".jpg", true) -> "image/jpeg"
                file.path.contains(".png", true) -> "image/png"
                file.path.contains(".gif", true) -> "image/gif"
                else -> {
                    val mime = getFileMime(file)
                    if (!TextUtils.isEmpty(mime)) mime else "image/jpeg"
                }
            }
            val requestBody = RequestBody.create(MediaType.parse(mimeType), file)
            val part = MultipartBody.Part.createFormData(name, file.name, requestBody)
            parts.add(part)
        }
        return parts
    }


    fun getMultipart(name: String, file: File): MultipartBody.Part {
        val mediaType = MediaFile.getFileType(file.path)
        val mimeType = when {
            mediaType != null -> mediaType.mimeType
            file.path.contains(".jpeg", true) -> "image/jpeg"
            file.path.contains(".jpg", true) -> "image/jpeg"
            file.path.contains(".png", true) -> "image/png"
            file.path.contains(".gif", true) -> "image/gif"
            else -> {
                val mime = getFileMime(file)
                if (!TextUtils.isEmpty(mime)) mime else "image/jpeg"
            }
        }
        val requestBody = RequestBody.create(MediaType.parse(mimeType), file)
        return MultipartBody.Part.createFormData(name, file.name, requestBody)
    }

    fun getMultipart(name: String, value: String): MultipartBody.Part {
        val requestBody = StringRequestBody(name, value)
        return MultipartBody.Part.createFormData(name, null, requestBody)
    }


    fun getFileMime(file: File): String {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, options)
        return options.outMimeType
    }

}