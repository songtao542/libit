package com.liabit.util

import android.graphics.BitmapFactory
import android.text.TextUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Suppress("unused")
object Multipart {
    @JvmStatic
    fun getMultipartList(name: String, files: List<File>): List<MultipartBody.Part> {
        return files.asMultipartList(name)
    }

    @JvmStatic
    fun getMultipart(name: String, content: String): MultipartBody.Part {
        return content.asMultipart(name)
    }

    @JvmStatic
    fun getMime(file: File): String {
        return file.getMime()
    }

    @JvmStatic
    fun getMultipart(name: String, file: File): MultipartBody.Part {
        return file.asMultipart(name)
    }
}

@Suppress("unused")
fun List<File>.asMultipartList(name: String): List<MultipartBody.Part> {
    val parts = ArrayList<MultipartBody.Part>(size)
    for (file in this) {
        parts.add(file.asMultipart(name))
    }
    return parts
}

@Suppress("unused")
fun String.asMultipart(name: String): MultipartBody.Part {
    val requestBody = StringRequestBody(name, this)
    return MultipartBody.Part.createFormData(name, null, requestBody)
}

fun File.getMime(): String {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)
    return options.outMimeType ?: ""
}

fun File.asMultipart(name: String): MultipartBody.Part {
    val fileMimeType = MediaFile.getMimeTypeForFile(path)
    val mimeType = when {
        fileMimeType.isNotEmpty() -> fileMimeType
        path.contains(".jpeg", true) -> "image/jpeg"
        path.contains(".jpg", true) -> "image/jpeg"
        path.contains(".png", true) -> "image/png"
        path.contains(".gif", true) -> "image/gif"
        else -> {
            val mime = getMime()
            if (!TextUtils.isEmpty(mime)) mime else "image/jpeg"
        }
    }
    val requestBody = asRequestBody(mimeType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(name, this.name, requestBody)
}