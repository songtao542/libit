package com.irun.runker.net.util

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import java.nio.charset.Charset

class StringRequestBody(val name: String, val value: String) : RequestBody() {

    private val mCharset = Charset.forName("UTF-8")
    private var mContentLength = 0
    private var mContent: ByteArray

    init {
        mContent = value.toByteArray(mCharset)
        mContentLength = mContent.size
    }

    override fun contentType(): MediaType? {
        return MultipartBody.FORM
    }

    override fun contentLength(): Long {
        return mContentLength.toLong()
    }

    override fun writeTo(sink: BufferedSink) {
        sink.write(mContent, 0, mContentLength)
    }

}