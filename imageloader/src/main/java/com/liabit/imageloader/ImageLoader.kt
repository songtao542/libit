package com.liabit.imageloader

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun ImageView.load(url: String) {
    ImageLoader.display(url, this)
}

fun ImageView.load(url: String, error: Int) {
    ImageLoader.display(url, this, error)
}

object ImageLoader : CoroutineScope {

    private const val TAG = "ImageLoader"

    var DEBUG = false

    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + Job()

    private val mBitmapCache = LruCache<String, Bitmap>(20)
    private val mTempViewToSupportFragment = ArrayMap<View, Fragment>()

    private class Target(val url: String, displayTarget: Any) {
        val target = WeakReference(displayTarget)
    }

    interface DisplayTarget {
        fun getTag(id: Int): Any?

        fun setTag(id: Int, tag: Any): Any?

        fun getSize(): Point? {
            return null
        }

        fun display(bitmap: Bitmap)

        fun getContext(): Context?
    }

    @JvmStatic
    fun display(url: String?, displayTarget: DisplayTarget) {
        display(url, displayTarget, 0)
    }

    @JvmStatic
    fun display(url: String?, displayTarget: DisplayTarget?, error: Int) {
        if (url.isNullOrEmpty() || displayTarget == null) return
        val context = displayTarget.getContext() ?: return
        displayTarget.setTag(R.id.image_loader_key_tag, url)
        displayTarget.setTag(R.id.image_loader_error_tag, error)
        val target = Target(url, displayTarget)
        val activity = findActivity(context)
        log("activity: $activity")
        if (activity == null) {
            val ctx = context.applicationContext
            loadImageAndDisplay(ctx, target)
        } else {
            when (activity) {
                is ComponentActivity -> {
                    loadImage(activity, activity, target)
                }
                else -> {
                    loadImage(activity, LifeCycleFragment.getLifecycleOwner(activity), target)
                }
            }
        }
    }

    @JvmStatic
    fun display(url: String?, imageView: ImageView?) {
        display(url, imageView, 0)
    }

    @JvmStatic
    fun display(url: String?, imageView: ImageView?, error: Int) {
        if (url.isNullOrEmpty() || imageView == null) return
        val context = imageView.context ?: return
        imageView.setTag(R.id.image_loader_key_tag, url)
        imageView.setTag(R.id.image_loader_error_tag, error)
        val target = Target(url, imageView)
        val activity = findActivity(context)
        log("activity: $activity")
        if (activity == null) {
            val ctx = context.applicationContext
            loadImageAndDisplay(ctx, target)
        } else {
            when (activity) {
                is FragmentActivity -> {
                    val fragment = findFragment(imageView, activity)
                    log("fragment: $fragment")
                    loadImage(activity, fragment?.viewLifecycleOwner ?: activity, target)
                }
                is ComponentActivity -> {
                    loadImage(activity, activity, target)
                }
                else -> {
                    loadImage(activity, LifeCycleFragment.getLifecycleOwner(activity), target)
                }
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
        val activityRoot = activity.findViewById<View>(android.R.id.content)
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

    private fun findAllFragmentsWithViews(topLevelFragments: Collection<Fragment>?, result: MutableMap<View?, Fragment>) {
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

    private fun loadImageAndDisplay(context: Context, target: Target) {
        launch {
            val bitmap = loadBitmap(context, target)
            displayImage(context, target, bitmap)
        }
    }

    private fun loadImage(context: Context, lifecycleOwner: LifecycleOwner, target: Target) {
        val ctx = context.applicationContext
        loadImage(ctx, target).observe(lifecycleOwner) {
            displayImage(ctx, target, it)
        }
    }

    private fun loadImage(context: Context, target: Target): LiveData<Bitmap?> {
        val liveData = MutableLiveData<Bitmap?>()
        launch {
            val bitmap = loadBitmap(context, target)
            liveData.postValue(bitmap)
        }
        return liveData
    }

    @Suppress("SameParameterValue")
    private fun assertMainThread(methodName: String) {
        if (Looper.getMainLooper().thread !== Thread.currentThread()) {
            throw IllegalStateException("Cannot invoke $methodName on a background thread")
        }
    }

    private fun displayImage(context: Context, target: Target, bitmap: Bitmap?) {
        assertMainThread("displayImage")
        val displayTarget = target.target.get() ?: return
        val url = target.url
        if (bitmap != null) {
            if (displayTarget is ImageView) {
                val tag = displayTarget.getTag(R.id.image_loader_key_tag)
                if (tag == url) {
                    displayTarget.setImageDrawable(BitmapDrawable(context.resources, bitmap))
                }
            } else if (displayTarget is DisplayTarget) {
                val tag = displayTarget.getTag(R.id.image_loader_key_tag)
                if (tag == url) {
                    displayTarget.display(bitmap)
                }
            }
        } else {
            if (displayTarget is ImageView) {
                val tag = displayTarget.getTag(R.id.image_loader_key_tag)
                val errorResourceId = displayTarget.getTag(R.id.image_loader_error_tag)
                if (tag == url && errorResourceId != null) {
                    displayTarget.setImageResource(errorResourceId as Int)
                }
            } else if (displayTarget is DisplayTarget) {
                val tag = displayTarget.getTag(R.id.image_loader_key_tag)
                val errorResourceId = displayTarget.getTag(R.id.image_loader_error_tag)
                if (tag == url && errorResourceId != null) {
                    displayTarget.display(BitmapFactory.decodeResource(context.resources, errorResourceId as Int))
                }
            }
        }
    }

    private fun generateKey(url: String): String {
        return url.hashCode().toString().replace("-", "_")
    }

    private suspend fun loadBitmap(context: Context, target: Target): Bitmap? = withContext(Dispatchers.IO) {
        val ctx = context.applicationContext
        val url = target.url
        val size = getTargetSize(target)
        val key = generateKey(url)
        val appendSizeKey = if (size != null) "${key}_x_${size.x}_y_${size.y}" else key
        return@withContext mBitmapCache[key] ?: download(ctx, url, key, appendSizeKey, size)?.also { mBitmapCache.put(key, it) }
    }

    private fun download(context: Context, url: String, key: String, appendSizeKey: String, size: Point?): Bitmap? {
        try {
            log("target size: $size")
            val cacheDir = context.externalCacheDir ?: return null
            var cacheFile = File(cacheDir, appendSizeKey)
            // 先判断是否已缓存本地文件，如果已下载，则直接解码本地文件
            if (cacheFile.exists()) {
                try {
                    log("decode cached sized file")
                    return BitmapFactory.decodeFile(cacheFile.absolutePath)
                } catch (e: Throwable) {
                    log("decode cached file failed: $cacheFile", e)
                }
            } else if (key != appendSizeKey) {
                cacheFile = File(cacheDir, key)
                try {
                    log("decode cached no sized file")
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.RGB_565
                    if (size != null) {
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(cacheFile.absolutePath, options)
                        options.inSampleSize = calculateInSampleSize(options, size.x, size.y)
                        options.inJustDecodeBounds = false
                    }
                    log("decode cached no sized file, options.inSampleSize=${options.inSampleSize}")
                    val bitmap = BitmapFactory.decodeFile(cacheFile.absolutePath, options)
                    if (bitmap != null) {
                        saveDecodedBitmap(context, bitmap, appendSizeKey)
                        return bitmap
                    }
                } catch (e: Throwable) {
                    log("decode cached file failed: $cacheFile", e)
                }
            }

            // 尝试网络下载图片
            val connection = (URL(url).openConnection() as? HttpURLConnection) ?: return null
            connection.requestMethod = "GET"
            connection.connect()
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val bytes = ByteArray(1024)
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
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565
                if (size != null) {
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
                    options.inSampleSize = calculateInSampleSize(options, size.x, size.y)
                    options.inJustDecodeBounds = false
                }
                log("decode net source, options.inSampleSize=${options.inSampleSize}")
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
                if (key != appendSizeKey && bitmap != null) {
                    saveDecodedBitmap(context, bitmap, appendSizeKey)
                }
                return bitmap
            }
        } catch (e: Throwable) {
            log("download image failed: ", e)
        }
        return null
    }

    private fun saveDecodedBitmap(context: Context, bitmap: Bitmap, appendSizeKey: String) {
        try {
            val cacheDir = context.externalCacheDir ?: return
            val cacheFile = File(cacheDir, appendSizeKey)
            val fileOutputStream = FileOutputStream(cacheFile)
            val bufferedOutputStream = BufferedOutputStream(fileOutputStream)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream)
        } catch (e: Throwable) {
            log("save decoded image failed: ", e)
        }
    }

    private suspend fun getTargetSize(target: Target?): Point? = suspendCoroutine {
        val displayTarget = target?.target?.get()
        if (displayTarget is ImageView) {
            if (displayTarget.width > 0 && displayTarget.height > 0) {
                it.resume(Point(displayTarget.width, displayTarget.height))
            } else {
                displayTarget.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    private var mInvokeCount = AtomicInteger(0)
                    override fun onPreDraw(): Boolean {
                        val count = mInvokeCount.getAndIncrement()
                        log("getTargetSize invoke count: $count  width: ${displayTarget.width}  height: ${displayTarget.height}")
                        displayTarget.viewTreeObserver.removeOnPreDrawListener(this)
                        if (count == 0) {
                            it.resume(Point(displayTarget.width, displayTarget.height))
                        }
                        return true
                    }
                })
            }
        } else if (displayTarget is DisplayTarget) {
            it.resume(displayTarget.getSize())
        } else {
            it.resume(null)
        }
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        log("calculateInSampleSize for ($width, $height)")
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun log(message: String, exception: Throwable? = null) {
        if (!DEBUG) return
        if (exception == null) {
            Log.d(TAG, message)
        } else {
            Log.d(TAG, message, exception)
        }
    }

}