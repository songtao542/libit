package cn.lolii.test14

import android.Manifest
import android.annotation.SuppressLint
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.lolii.location.extension.checkAndRequestPermission
import kotlinx.android.synthetic.main.test_record_activity.*
import java.io.File
import java.text.SimpleDateFormat


/**
 * Author:         songtao
 * CreateDate:     2020/6/1 15:02
 */
class TestMediaRecorder : AppCompatActivity() {

    private var mMediaRecorder: MediaRecorder? = null
    private var mFileName: String = ""
    private var mFilePath: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_record_activity)
        if (checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)) {
            startRecord()
        }

        startRecordButton.setOnClickListener {
            startRecord()
        }

        stopRecordButton.setOnClickListener {
            stopRecord()
        }
    }


    @SuppressLint("SimpleDateFormat")
    fun startRecord() {
        if (mMediaRecorder != null) {
            return
        }
        try {
            mMediaRecorder = MediaRecorder().apply {
                this.setAudioSource(MediaRecorder.AudioSource.MIC) // 设置麦克风
                this.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                this.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                mFileName = SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis()) + ".m4a"
                val destDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                val destFile = File(destDir, mFileName)
                mFilePath = destFile
                Log.d("TestMediaRecorder", "mFilePath: $mFilePath")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.setOutputFile(destFile)
                } else {
                    this.setOutputFile(destFile.absolutePath)
                }
                this.prepare()
                this.start()
            }
        } catch (e: IllegalStateException) {
            Log.w("TestMediaRecorder", e)
        } catch (e: Throwable) {
            Log.w("TestMediaRecorder", e)
        }
    }

    private fun stopRecord() {
        try {
            mMediaRecorder?.let {
                it.stop()
                it.release()
            }
            mMediaRecorder = null
        } catch (e: RuntimeException) {
            mMediaRecorder?.let {
                it.reset()
                it.release()
            }
            mMediaRecorder = null
            mFilePath?.let {
                if (it.exists()) {
                    it.delete()
                }
            }
        }
    }

}