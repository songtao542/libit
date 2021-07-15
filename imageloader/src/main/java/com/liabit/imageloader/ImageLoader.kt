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
import androidx.core.util.Pools
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun ImageView.load(url: String) {
    ImageLoader.load(url).into(this)
}

fun ImageView.load(url: String, error: Int) {
    ImageLoader.load(url).error(error).into(this)
}

object ImageLoader : CoroutineScope {

    private const val TAG = "ImageLoader"

    var DEBUG = true

    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + Job()

    private val mBitmapCache = LruCache<String, Bitmap>(Runtime.getRuntime().maxMemory() / 8)
    private val mTempViewToFragment = ArrayMap<View, Fragment>()

    private val mBytePool = Pools.SynchronizedPool<ByteArray>(4)

    private val mSemaphore = Semaphore(4)

    private val mUrlJobMap = HashMap<String, Job>()

    private class Target(val url: String, displayTarget: Any) {
        val target = WeakReference(displayTarget)

        fun isRequesting(): Boolean {
            val displayTarget = target.get()
            if (displayTarget is ImageView) {
                val oldUrl = displayTarget.getTag(R.id.image_loader_url_tag)
                if (oldUrl == url) {
                    return true
                }
            } else if (displayTarget is DisplayTarget) {
                val oldUrl = displayTarget.getTag(R.id.image_loader_url_tag)
                if (oldUrl == url) {
                    return true
                }
            }
            return false
        }
    }

    interface DisplayTarget {
        fun getTag(id: Int): Any?

        fun setTag(id: Int, tag: Any?): Any?

        fun getSize(): Point? {
            return null
        }

        fun display(bitmap: Bitmap)

        fun getContext(): Context?
    }

    class RequestBuilder constructor() {

        private var mFragment: Fragment? = null
        private var mActivity: Activity? = null
        private var mContext: Context? = null
        private var mUrl: String? = null
        private var mErrorResId: Int = 0

        internal constructor(fragment: Fragment) : this() {
            mFragment = fragment
        }

        internal constructor(activity: Activity) : this() {
            mActivity = activity
        }

        internal constructor(context: Context) : this() {
            mContext = context
        }

        fun load(url: String): RequestBuilder {
            mUrl = url
            return this
        }

        fun error(resId: Int): RequestBuilder {
            mErrorResId = resId
            return this
        }

        fun into(imageView: ImageView) {
            display(mUrl, imageView, mFragment, mActivity, mContext, mErrorResId)
        }

        fun into(target: DisplayTarget) {
            display(mUrl, target, mFragment, mActivity, mContext, mErrorResId)
        }
    }

    @JvmStatic
    fun with(fragment: Fragment): RequestBuilder {
        return RequestBuilder(fragment)
    }

    @JvmStatic
    fun with(activity: Activity): RequestBuilder {
        return RequestBuilder(activity)
    }

    @JvmStatic
    fun with(context: Context): RequestBuilder {
        return RequestBuilder(context)
    }

    @JvmStatic
    fun load(url: String): RequestBuilder {
        return RequestBuilder().load(url)
    }

    private fun display(
        url: String?,
        displayTarget: Any?,
        fragment: Fragment?,
        activity: Activity?,
        context: Context?,
        error: Int
    ) {
        if (url.isNullOrEmpty() || displayTarget == null) return
        val target = Target(url, displayTarget)
        if (target.isRequesting()) return
        cancelJobForTarget(displayTarget)
        val ctx = when (displayTarget) {
            is ImageView -> {
                displayTarget.setTag(R.id.image_loader_url_tag, url)
                displayTarget.setTag(R.id.image_loader_error_tag, error)
                context ?: displayTarget.context
            }
            is DisplayTarget -> {
                displayTarget.setTag(R.id.image_loader_url_tag, url)
                displayTarget.setTag(R.id.image_loader_error_tag, error)
                context ?: displayTarget.getContext()
            }
            else -> null
        } ?: return
        if (fragment != null) {
            log("fragment: $fragment")
            loadImage(ctx, fragment.viewLifecycleOwner, target)
        } else {
            val targetActivity = activity ?: findActivity(ctx)
            log("activity: $targetActivity")
            if (targetActivity != null) {
                when (targetActivity) {
                    is FragmentActivity -> {
                        val targetFragment = findFragment(displayTarget as? ImageView, targetActivity)
                        log("target fragment: $targetFragment")
                        loadImage(targetActivity, targetFragment?.viewLifecycleOwner ?: targetActivity, target)
                    }
                    is ComponentActivity -> {
                        loadImage(targetActivity, targetActivity, target)
                    }
                    else -> {
                        loadImage(targetActivity, LifeCycleFragment.getLifecycleOwner(targetActivity), target)
                    }
                }
            } else {
                loadImageAndDisplay(ctx.applicationContext, target)
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

    private fun findFragment(target: View?, activity: FragmentActivity): Fragment? {
        if (target == null) return null
        mTempViewToFragment.clear()
        findAllFragmentsWithViews(activity.supportFragmentManager.fragments, mTempViewToFragment)
        var result: Fragment? = null
        val activityRoot = activity.findViewById<View>(android.R.id.content)
        var current: View = target
        while (current != activityRoot) {
            result = mTempViewToFragment[current]
            if (result != null) {
                break
            }
            current = if (current.parent is View) {
                current.parent as View
            } else {
                break
            }
        }
        mTempViewToFragment.clear()
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
        loadBitmap(context, target) {
            launch(Dispatchers.Main) {
                displayImage(context, target, it)
            }
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
        loadBitmap(context, target) {
            liveData.postValue(it)
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
                val tag = displayTarget.getTag(R.id.image_loader_url_tag)
                if (tag == url) {
                    displayTarget.setImageDrawable(BitmapDrawable(context.resources, bitmap))
                    displayTarget.setTag(R.id.image_loader_url_tag, null)
                }
            } else if (displayTarget is DisplayTarget) {
                val tag = displayTarget.getTag(R.id.image_loader_url_tag)
                if (tag == url) {
                    displayTarget.display(bitmap)
                    displayTarget.setTag(R.id.image_loader_url_tag, null)
                }
            }
        } else {
            if (displayTarget is ImageView) {
                val tag = displayTarget.getTag(R.id.image_loader_url_tag)
                val errorResourceId = displayTarget.getTag(R.id.image_loader_error_tag)
                if (tag == url && errorResourceId != null) {
                    displayTarget.setImageResource(errorResourceId as Int)
                    displayTarget.setTag(R.id.image_loader_url_tag, null)
                    displayTarget.setTag(R.id.image_loader_error_tag, null)
                }
            } else if (displayTarget is DisplayTarget) {
                val tag = displayTarget.getTag(R.id.image_loader_url_tag)
                val errorResourceId = displayTarget.getTag(R.id.image_loader_error_tag)
                if (tag == url && errorResourceId != null) {
                    displayTarget.display(BitmapFactory.decodeResource(context.resources, errorResourceId as Int))
                    displayTarget.setTag(R.id.image_loader_url_tag, null)
                    displayTarget.setTag(R.id.image_loader_error_tag, null)
                }
            }
        }
    }

    private fun generateKey(url: String): String {
        return url.hashCode().toString().replace("-", "_")
    }

    private fun cancelJobForTarget(displayTarget: Any) {
        if (displayTarget is ImageView) {
            (displayTarget.getTag(R.id.image_loader_job_tag) as? Job)?.cancel()
        } else if (displayTarget is DisplayTarget) {
            (displayTarget.getTag(R.id.image_loader_job_tag) as? Job)?.cancel()
        }
    }

    private fun bindJobToTarget(target: Target, job: Job) {
        val displayTarget = target.target.get() ?: return
        if (displayTarget is ImageView) {
            displayTarget.setTag(R.id.image_loader_job_tag, job)
        } else if (displayTarget is DisplayTarget) {
            displayTarget.setTag(R.id.image_loader_job_tag, job)
        }
    }

    private fun loadBitmap(context: Context, target: Target, onComplete: (bitmap: Bitmap?) -> Unit) {
        launch {
            val ctx = context.applicationContext
            val url = target.url
            val size = getTargetSize(target)
            val key = generateKey(url)
            val appendSizeKey = if (size != null) "${key}_x_${size.x}_y_${size.y}" else key
            val existJob = mUrlJobMap[appendSizeKey]
            val job = coroutineContext[Job]
            if (job != null) {
                bindJobToTarget(target, job)
                if (existJob == null) {
                    mUrlJobMap[appendSizeKey] = job
                }
                // 如果已经有一个任务在加载相同的URL，且显示的尺寸也完全相同，则只需等待那个任务结束
                if (existJob != null && existJob != job) {
                    existJob.join()
                }
            }
            val bitmap = mBitmapCache[key] ?: download(ctx, url, key, appendSizeKey, size)?.also { mBitmapCache.put(key, it) }
            onComplete.invoke(bitmap)
            mUrlJobMap.remove(appendSizeKey)
        }
    }

    private suspend fun download(context: Context, url: String, key: String, appendSizeKey: String, size: Point?): Bitmap? {
        mSemaphore.acquire()
        try {
            log("target size: $size")
            val cacheDir = context.externalCacheDir ?: return null
            var cacheFile = File(cacheDir, appendSizeKey)
            if (!isActive) {
                return null
            }
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
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(cacheFile.absolutePath, options)
                    if (size != null) {
                        options.inSampleSize = calculateInSampleSize(options, size.x, size.y)
                    }
                    options.inJustDecodeBounds = false
                    if ("image/png" == options.outMimeType) {
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    } else {
                        options.inPreferredConfig = Bitmap.Config.RGB_565
                    }
                    log("decode cached no sized file, options.inSampleSize=${options.inSampleSize}")
                    if (!isActive) {
                        return null
                    }
                    val bitmap = BitmapFactory.decodeFile(cacheFile.absolutePath, options)
                    if (bitmap != null) {
                        saveDecodedBitmap(context, bitmap, appendSizeKey, options.outMimeType)
                        return bitmap
                    }
                } catch (e: Throwable) {
                    log("decode cached file failed: $cacheFile", e)
                }
            }

            if (!isActive) {
                return null
            }

            var connectionRef: HttpURLConnection? = null
            var bitmap: Bitmap? = null
            kotlin.runCatching {
                // 尝试网络下载图片
                val connection = (URL(url).openConnection() as? HttpURLConnection) ?: return null
                connectionRef = connection
                connection.requestMethod = "GET"
                connection.connect()
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val buffer = mBytePool.acquire() ?: ByteArray(1024)
                    FileOutputStream(cacheFile).use { fileOutputStream ->
                        BufferedOutputStream(fileOutputStream).use { bufferedOutputStream ->
                            var read: Int
                            while (inputStream.read(buffer).also { read = it } > 0) {
                                bufferedOutputStream.write(buffer, 0, read)
                            }
                        }

                        val options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(cacheFile.absolutePath, options)
                        if (size != null) {
                            options.inSampleSize = calculateInSampleSize(options, size.x, size.y)
                        }
                        options.inJustDecodeBounds = false
                        if ("image/png" == options.outMimeType) {
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888
                        } else {
                            options.inPreferredConfig = Bitmap.Config.RGB_565
                        }
                        log("decode net source, options.inSampleSize=${options.inSampleSize}")
                        if (!isActive) {
                            return null
                        }
                        bitmap = BitmapFactory.decodeFile(cacheFile.absolutePath, options)?.also {
                            if (key != appendSizeKey) {
                                saveDecodedBitmap(context, it, appendSizeKey, options.outMimeType)
                            }
                        }
                    }
                    //回收 buffer
                    mBytePool.release(buffer)
                }
            }
            connectionRef?.disconnect()
            return bitmap
        } catch (e: Throwable) {
            log("download image failed: ", e)
        } finally {
            mSemaphore.release()
        }
        return null
    }

    private fun saveDecodedBitmap(context: Context, bitmap: Bitmap, appendSizeKey: String, mimeType: String?) {
        try {
            val cacheDir = context.externalCacheDir ?: return
            val cacheFile = File(cacheDir, appendSizeKey)
            FileOutputStream(cacheFile).use { fileOutputStream ->
                BufferedOutputStream(fileOutputStream).use { bufferedOutputStream ->
                    val format = if ("image/png" == mimeType) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
                    bitmap.compress(format, 100, bufferedOutputStream)
                }
            }
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

    /*private fun initCacheSize(context: Context) {
       (context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.let {
           val memoryClassBytes = it.memoryClass * 1024 * 1024
           val cacheSize = memoryClassBytes / 8
           val multiplier = cacheSize / 20f
           mBitmapCache.setSizeMultiplier(multiplier)
       }
    }*/
}