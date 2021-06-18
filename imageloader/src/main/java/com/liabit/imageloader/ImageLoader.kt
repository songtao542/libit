package com.liabit.imageloader

import android.R
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.coroutines.CoroutineContext

object ImageLoader : CoroutineScope {

    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + Job()

    private val mBitmapCache = LruCache<String, Bitmap>(20)
    private val mTempViewToSupportFragment = ArrayMap<View, Fragment>()

    private val mWeakHashMap = WeakHashMap<String, Any>()

    interface DisplayTarget {
        fun display(bitmap: Bitmap)
    }

    fun display(url: String?, imageView: ImageView?) {
        if (url.isNullOrEmpty() || imageView == null) return
        val context = imageView.context ?: return
        val activity = findActivity(context)
        if (activity == null) {
            mWeakHashMap[url] = imageView
        } else {
            if (activity is FragmentActivity) {
                val fragment = findFragment(imageView, activity)
                if (fragment != null) {
                      
                } else {

                }
            } else {

            }
        }
    }

    private fun findActivity(context: Context): Activity? {
        return when (context) {
            is Activity -> context
            is ContextWrapper -> findActivity(context.baseContext)
            else -> null
        }
    }

    private fun findFragment(target: View, activity: FragmentActivity): Fragment? {
        mTempViewToSupportFragment.clear()
        findAllFragmentsWithViews(activity.supportFragmentManager.fragments, mTempViewToSupportFragment)
        var result: Fragment? = null
        val activityRoot = activity.findViewById<View>(R.id.content)
        var current = target
        while (current != activityRoot) {
            result = mTempViewToSupportFragment[current]
            if (result != null) {
                break
            }
            current = if (current.parent is View) {
                current.parent as View
            } else {
                break
            }
        }
        mTempViewToSupportFragment.clear()
        return result
    }

    private fun findAllFragmentsWithViews(
        topLevelFragments: Collection<Fragment>?,
        result: MutableMap<View?, Fragment>
    ) {
        if (topLevelFragments == null) {
            return
        }
        for (fragment in topLevelFragments) {
            // getFragment()s in the support FragmentManager may contain null values, see #1991.
            if (fragment.view == null) {
                continue
            }
            result[fragment.view] = fragment
            findAllFragmentsWithViews(fragment.childFragmentManager.fragments, result)
        }
    }

    private suspend fun loadImage(context: Context, url: String?): Bitmap? = withContext(Dispatchers.IO) {
        if (url == null) return@withContext null
        val ctx = context.applicationContext
        val key = url.hashCode().toString().replace("-", "_")
        return@withContext mBitmapCache[key] ?: download(ctx, url, key)?.also { mBitmapCache.put(key, it) }
    }

    private fun download(context: Context, url: String, key: String): Bitmap? {
        kotlin.runCatching {
            val connection = (URL(url).openConnection() as? HttpURLConnection) ?: return null
            //设置请求方式
            connection.requestMethod = "GET"
            //连接
            connection.connect()
            //得到响应码
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //得到响应流
                val inputStream = connection.inputStream
                val bytes = ByteArray(1024)
                val cacheDir = context.externalCacheDir
                val cacheFile = File(cacheDir, key)
                val fileOutputStream = FileOutputStream(cacheFile)
                val bufferedOutputStream = BufferedOutputStream(fileOutputStream)
                val byteArrayOutputStream = ByteArrayOutputStream()
                var read: Int
                while (inputStream.read(bytes).also { read = it } > 0) {
                    bufferedOutputStream.write(bytes, 0, read)
                    byteArrayOutputStream.write(bytes, 0, read)
                }
                bufferedOutputStream.flush()
                fileOutputStream.flush()
                val byteArray = byteArrayOutputStream.toByteArray()
                return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            }
            return null
        }
        return null
    }


}