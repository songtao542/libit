package cn.lolii.screenrecord

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.*
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import java.io.File

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ScreenRecordUtil private constructor(context: Context) : ScreenRecord {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: ScreenRecordUtil? = null

        fun getInstance(context: Context): ScreenRecordUtil {
            if (instance == null) {
                synchronized(ScreenRecordUtil::class.java) {
                    if (instance == null) {
                        instance = ScreenRecordUtil(context.applicationContext)
                    }
                }
            }
            return instance!!
        }
    }

    private var mScreenRecorder: ScreenRecord = ScreenRecordApi21(context)

    override fun startRecord(callback: ScreenRecord.Callback?) {
        mScreenRecorder.startRecord(callback)
    }

    override fun isRecording(): Boolean {
        return mScreenRecorder.isRecording()
    }

    override fun stopRecord() {
        mScreenRecorder.stopRecord()
    }

    override fun requestPermission(activity: Activity) {
        mScreenRecorder.requestPermission(activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        mScreenRecorder.onActivityResult(requestCode, resultCode, data)
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ScreenRecordApi21(private val context: Context) : ScreenRecord {

    @Suppress("PrivatePropertyName")
    private val MAX_DURATION = 1000 * 60 * 5L
    private var mMediaProjectionManager =
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mScreenDensity: Int = 0
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mResultCode: Int = 0
    private var mResultData: Intent? = null

    private var mHandler = Handler()

    private var mMediaRecorder: MediaRecorder? = null

    private var mOutputFilePath = ""

    private var mRecording = false
    private var mRecordCallback: ScreenRecord.Callback? = null

    private var mCallback: VirtualDisplay.Callback = object : VirtualDisplay.Callback() {
    }

    private var mOnInfoListener = MediaRecorder.OnInfoListener { mr, what, extra ->
        Log.d("ScreenRecord", "MediaRecorder.OnInfoListener what=$what extra=$extra")
        stopRecord()
    }

    private var mOnErrorListener = MediaRecorder.OnErrorListener { mr, what, extra ->
        Log.d("ScreenRecord", "MediaRecorder.OnErrorListener what=$what extra=$extra")
        stopRecord()
    }

    init {
        val displayMetrics = context.resources.displayMetrics
        mScreenDensity = displayMetrics.densityDpi
        mWidth = displayMetrics.widthPixels
        mHeight = displayMetrics.heightPixels
    }

    private fun ensureMediaRecorder(): MediaRecorder? {
        val savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val timeMillis = System.currentTimeMillis()
        val fileName = "screenrecord_${DateFormat.format("yyyyMMddkkmmss", timeMillis)}.mp4"
        mOutputFilePath = "$savePath${File.separator}$fileName"
        Log.d("ScreenRecord", "OutFilePath=$mOutputFilePath  MediaRecorder=$mMediaRecorder")
        if (mMediaRecorder == null) {
            mMediaRecorder = MediaRecorder()
        }
        mMediaRecorder?.let {
            try {
                //设置音频来源
                it.setAudioSource(MediaRecorder.AudioSource.MIC)
                //设置视频来源
                it.setVideoSource(MediaRecorder.VideoSource.SURFACE)
                //输出的录屏文件格式
                it.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                //录屏文件路径
                it.setOutputFile(mOutputFilePath)
                //视频尺寸
                it.setVideoSize(mWidth, mHeight)
                //音视频编码器
                it.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                //it.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                it.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                //比特率
                it.setVideoEncodingBitRate((mWidth * mHeight * 3.6).toInt())
                //视频帧率
                it.setVideoFrameRate(20)
                it.setOnInfoListener(mOnInfoListener)
                it.setOnErrorListener(mOnErrorListener)
                it.prepare()
                return it
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun ensureMediaProjection(): MediaProjection? {
        if (mMediaProjection == null && mResultCode != 0 && mResultData != null) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData)
        }
        return mMediaProjection
    }

    @MainThread
    @Synchronized
    override fun startRecord(callback: ScreenRecord.Callback?) {
        Log.d("ScreenRecord", "start record, recording=$mRecording")
        if (mRecording) return
        mRecordCallback = callback
        startRecord(true)
    }

    private val mStopRecorder = Runnable {
        stopRecord()
    }

    private fun startRecord(requestPermission: Boolean = true) {
        if (mMediaProjection != null || (mResultCode != 0 && mResultData != null)) {
            startRecordInternal()
        } else if (requestPermission) {
            mHandler.removeCallbacksAndMessages(null)
            RequestMediaProjectionPermissionActivity.start(context)
        }
    }

    private fun startRecordInternal() {
        Log.d("ScreenRecord", "start record screen, recording=$mRecording")
        try {
            mRecording = true
            mRecordCallback?.onStartRecord()
            val mediaRecorder = ensureMediaRecorder()
            val mediaProjection = ensureMediaProjection()
            if (mediaRecorder == null || mediaProjection == null) {
                abort()
                return
            }
            mVirtualDisplay = mediaProjection.createVirtualDisplay(
                "ScreenRecord",
                mWidth, mHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder.surface, mCallback, mHandler
            )
            mediaRecorder.start()
            mHandler.postDelayed(mStopRecorder, MAX_DURATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, ScreenRecordService::class.java))
            } else {
                context.startService(Intent(context, ScreenRecordService::class.java))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            abort()
        }
    }

    private fun abort() {
        mRecording = false
        mHandler.removeCallbacksAndMessages(null)
        mRecordCallback?.onEndRecord(null)
        mRecordCallback = null
    }

    @Synchronized
    override fun stopRecord() {
        Log.d("ScreenRecord", "stop record, recording=$mRecording")
        if (!mRecording) return
        mRecording = false
        mRecordCallback?.onEndRecord(mOutputFilePath)
        mHandler.removeCallbacksAndMessages(null)
        mVirtualDisplay?.release()
        mMediaProjection?.stop()
        mVirtualDisplay = null
        mMediaProjection = null
        mRecordCallback = null
        try {
            mMediaRecorder?.stop()
            //mMediaRecorder?.reset()
            ImageScannerConnectionClient(context, mOutputFilePath)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val file = File(mOutputFilePath)
                if (file.exists() && file.length() == 0L) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mMediaRecorder = null
    }

    override fun isRecording(): Boolean {
        return mRecording
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
            startRecord(false)
        }
    }
}

/**
 * 保存截图后，将图片路径添加到图库中
 */
class ImageScannerConnectionClient(context: Context, private val path: String) :
    MediaScannerConnection.MediaScannerConnectionClient {

    private val connection: MediaScannerConnection = MediaScannerConnection(context.applicationContext, this)

    init {
        try {
            connection.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMediaScannerConnected() {
        try {
            connection.scanFile(path, "image/jpeg")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onScanCompleted(path: String?, uri: Uri?) {
        connection.disconnect()
    }
}

interface ScreenRecord {
    fun isRecording(): Boolean

    fun startRecord(callback: Callback? = null)

    fun stopRecord()

    fun requestPermission(activity: Activity) {}

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {}

    interface Callback {
        fun onStartRecord()

        fun onEndRecord(path: String?)


    }
}

/**
 * 申请截屏和存储权限
 */
class RequestMediaProjectionPermissionActivity : Activity(), ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RequestMediaProjectionPermissionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val storage = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val audio = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
            val permissions =
                if (storage != PackageManager.PERMISSION_GRANTED && audio != PackageManager.PERMISSION_GRANTED) {
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.RECORD_AUDIO
                    )
                } else if (storage != PackageManager.PERMISSION_GRANTED) {
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else if (audio != PackageManager.PERMISSION_GRANTED) {
                    arrayOf(android.Manifest.permission.RECORD_AUDIO)
                } else {
                    null
                }
            if (permissions != null) {
                ActivityCompat.requestPermissions(this, permissions, 13)
            } else {
                ScreenRecordUtil.getInstance(this).requestPermission(this)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ScreenRecordUtil.getInstance(this).requestPermission(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val size = grantResults.size
        var granted = true
        for (i in 0 until size) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                granted = false
                break
            }
        }
        if (granted) {
            ScreenRecordUtil.getInstance(this).requestPermission(this)
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finish()
        val d = data ?: return
        Handler().postDelayed({
            ScreenRecordUtil.getInstance(this).onActivityResult(requestCode, resultCode, d)
        }, 500)
    }

    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }
}