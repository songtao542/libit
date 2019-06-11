package cn.lolii.screencapture

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.MediaScannerConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.*
import android.text.format.DateFormat
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.nio.ByteBuffer

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ScreenCaptureUtil private constructor(context: Context) : ScreenCapture {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: ScreenCaptureUtil? = null

        fun getInstance(context: Context): ScreenCaptureUtil {
            if (instance == null) {
                synchronized(ScreenCaptureUtil::class.java) {
                    if (instance == null) {
                        instance = ScreenCaptureUtil(context.applicationContext)
                    }
                }
            }
            return instance!!
        }
    }

//    private var mScreenCapture: ScreenCapture = when {
//        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> ScreenCaptureApi21(context)
//        Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> ScreenCaptureApi18(context)
//        else -> ScreenCaptureApi14(context)
//    }

    private var mScreenCapture: ScreenCapture = ScreenCaptureApi21(context)

    override fun startCapture(delay: Long) {
        mScreenCapture.startCapture(delay)
    }

    override fun requestPermission(activity: Activity) {
        mScreenCapture.requestPermission(activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        mScreenCapture.onActivityResult(requestCode, resultCode, data)
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ScreenCaptureApi21(private val context: Context) : ScreenCapture {

    private var mMediaProjectionManager =
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mScreenDensity: Int = 0
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mResultCode: Int = 0
    private var mResultData: Intent? = null

    private var mImageReader: ImageReader? = null

    private var mHandler = Handler()

    private var mCallback: VirtualDisplay.Callback = object : VirtualDisplay.Callback() {
    }

    private var mOnImageAvailableListener = ImageReader.OnImageAvailableListener { imageReader ->
        imageReader?.let { reader ->
            try {
                reader.acquireNextImage()?.let { image ->
                    val planes = image.planes
                    val buffer = planes[0].buffer
                    val iw = image.width
                    val pixelStride = planes[0].pixelStride
                    val rowStride = planes[0].rowStride
                    val rowPadding = rowStride - pixelStride * iw
                    val width = iw + rowPadding / pixelStride
                    val height = image.height
                    reader.setOnImageAvailableListener(null, null)
                    SaveTask(context, reader).execute(width, height, buffer)
                    //image.close()
                    //saveImage(width + rowPadding / pixelStride, height, buffer)
                    stopScreenCapture()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@let
        }
    }

    class SaveTask(context: Context, private val imageReader: ImageReader) : AsyncTask<Any, Void, Boolean>() {

        private val mContext = WeakReference<Context>(context.applicationContext)

        override fun doInBackground(vararg params: Any?): Boolean {
            if (params.isNullOrEmpty()) return false
            val width = params[0] as? Int ?: return false
            val height = params[1] as? Int ?: return false
            val buffer = params[2] as? ByteBuffer ?: return false
            return saveImage(mContext.get(), width, height, buffer)
        }

        override fun onPostExecute(result: Boolean?) {
            imageReader.close()
        }
    }

    init {
        val displayMetrics = context.resources.displayMetrics
        mScreenDensity = displayMetrics.densityDpi
        mWidth = displayMetrics.widthPixels
        mHeight = displayMetrics.heightPixels
    }

    private fun ensureImageReader(): ImageReader? {
        if (mImageReader == null) {
            mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888/*ImageFormat.JPEG*/, 1)
        }
        return mImageReader
    }

    private fun ensureMediaProjection(): MediaProjection? {
        if (mMediaProjection == null && mResultCode != 0 && mResultData != null) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData)
        }
        return mMediaProjection
    }

    @MainThread
    override fun startCapture(delay: Long) {
        startCapture(true, delay)
    }

    private fun startCapture(request: Boolean = true, delay: Long = 0) {
        if (mMediaProjection != null || (mResultCode != 0 && mResultData != null)) {
            if (delay > 0) {
                mHandler.postDelayed({
                    startCaptureInternal()
                }, delay)
            } else {
                startCaptureInternal()
            }
        } else if (request) {
            RequestCapturePermissionActivity.start(context)
        }
    }

    private fun startCaptureInternal() {
        val imageReader = ensureImageReader() ?: return
        val mediaProjection = ensureMediaProjection() ?: return
        imageReader.setOnImageAvailableListener(mOnImageAvailableListener, null)
        mVirtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenCapture",
            imageReader.width, imageReader.height, mScreenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface, mCallback, mHandler
        )
    }

    private fun stopScreenCapture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mVirtualDisplay?.release()
            mVirtualDisplay = null
            mImageReader = null
        }
    }

    override fun requestPermission(activity: Activity) {
        // This initiates a prompt dialog for the user to confirm screen projection.
        activity.startActivityForResult(
            mMediaProjectionManager.createScreenCaptureIntent(),
            100
        )
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            mResultCode = resultCode
            mResultData = data
            mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, data)
            startCapture(false)
        }
    }
}

//@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//class ScreenCaptureApi18(context: Context) : ScreenCapture {
//
//    private var mWidth = 0
//    private var mHeight = 0
//
//    init {
//        val displayMetrics = context.resources.displayMetrics
//        mWidth = displayMetrics.widthPixels
//        mHeight = displayMetrics.heightPixels
//    }
//
//    @SuppressLint("PrivateApi")
//    override fun startCapture(delay: Long) {
//        try {
//            val cl = Class.forName("android.view.SurfaceControl")
//            val screenshot =
//                cl.getDeclaredMethod(
//                    "screenshot",
//                    Int::class.javaPrimitiveType,
//                    Int::class.javaPrimitiveType
//                )
//            val bitmap = screenshot.invoke(null, mWidth, mHeight) as? Bitmap
//            if (bitmap != null) {
//                saveBitmap(bitmap, true)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}

//@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//class ScreenCaptureApi14(context: Context) : ScreenCapture {
//
//    private var mWidth = 0
//    private var mHeight = 0
//
//    init {
//        val displayMetrics = context.resources.displayMetrics
//        mWidth = displayMetrics.widthPixels
//        mHeight = displayMetrics.heightPixels
//    }
//
//    override fun startCapture(delay: Long) {
//        try {
//            val cl = Class.forName("android.view.Surface")
////            val screenshot =
////                cl.getDeclaredMethod(
////                    "screenshot",
////                    Int::class.javaPrimitiveType,
////                    Int::class.javaPrimitiveType
////                )
//            val screenshot = cl.getMethod(
//                "screenshot",
//                Int::class.javaPrimitiveType,
//                Int::class.javaPrimitiveType
//            )
//            val bitmap = screenshot.invoke(null, mWidth, mHeight) as? Bitmap
//            if (bitmap != null) {
//                saveBitmap(bitmap, true)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}

/**
 * @param context if not null, the MediaScanner will execute
 */
fun saveImage(context: Context?, width: Int, height: Int, buffer: ByteBuffer): Boolean {
    try {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        return saveBitmap(context, bitmap)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

/**
 * @param context if not null, the MediaScanner will execute
 * @param bitmap bitmap to save
 * @param recycle if true, the bitmap will be recycled after saved
 */
fun saveBitmap(context: Context?, bitmap: Bitmap, recycle: Boolean = false): Boolean {
    val start = System.currentTimeMillis()
    var fileOutputStream: FileOutputStream? = null
    var bufferedOutputStream: BufferedOutputStream? = null
    try {
        val savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val timeMillis = System.currentTimeMillis()
        val fileName = "${DateFormat.format("yyyyMMddkkmmss", timeMillis)}${timeMillis % 10000}.jpg"
        Log.d("ScreenCaptureUtil", "fileName=$fileName")
        val saveFile = File(savePath, fileName)
        fileOutputStream = FileOutputStream(saveFile)
        bufferedOutputStream = BufferedOutputStream(fileOutputStream)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream)
        //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        bufferedOutputStream.flush()
        Log.d("ScreenCaptureUtil", "bitmap width=${bitmap.width}  height=${bitmap.height}")
        if (recycle) {
            bitmap.recycle()
        }
        context?.let { ImageScannerConnectionClient(it, saveFile.absolutePath) }
        //val outChannel = Channels.newChannel(fileOutputStream)
        //buffer.rewind()
        //outChannel.write(buffer)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            fileOutputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            bufferedOutputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("ScreenCaptureUtil", "save time=${System.currentTimeMillis() - start}")
    }
    return false
}

/**
 * 保存截图后，将图片路径添加到图库中
 */
class ImageScannerConnectionClient(context: Context, private val path: String) :
    MediaScannerConnection.MediaScannerConnectionClient {

    private val connection: MediaScannerConnection = MediaScannerConnection(context.applicationContext, this)

    init {
        connection.connect()
    }

    override fun onMediaScannerConnected() {
        connection.scanFile(path, "image/jpeg")
    }

    override fun onScanCompleted(path: String?, uri: Uri?) {
        connection.disconnect()
    }
}

interface ScreenCapture {
    fun startCapture(delay: Long = 0)

    fun requestPermission(activity: Activity) {}

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {}
}

/**
 * 申请截屏和存储权限
 */
class RequestCapturePermissionActivity : Activity(), ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RequestCapturePermissionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val granted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (granted != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 12)
            } else {
                ScreenCaptureUtil.getInstance(this).requestPermission(this)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ScreenCaptureUtil.getInstance(this).requestPermission(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val size = permissions.size
        for (i in 0 until size) {
            if (permissions[i] == android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                val granted = grantResults[i] == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    ScreenCaptureUtil.getInstance(this).requestPermission(this)
                    break
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finish()
        val d = data ?: return
        Handler().postDelayed({
            ScreenCaptureUtil.getInstance(this).onActivityResult(requestCode, resultCode, d)
        }, 500)
    }
}