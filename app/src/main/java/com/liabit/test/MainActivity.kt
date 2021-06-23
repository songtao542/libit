package com.liabit.test

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.liabit.test.databinding.ActivityMainBinding
import com.liabit.test.decorationtest.TestRecyclerViewDecorationActivity
import com.liabit.test.filtertest.TestFilterActivity
import com.liabit.test.gesturetest.TestDragActivity
import com.liabit.test.gesturetest.TestSwipeActivity
import com.liabit.test.imageloader.ImageLoaderActivity
import com.liabit.test.loadmore.TestLoadMoreMenuActivity
import com.liabit.test.nested.TestNestedRecyclerViewActivity
import com.liabit.test.tablayouttest.TestTabLayoutActivity
import com.liabit.test.tagviewtest.TestTagViewActivity
import com.liabit.test.viewbinding.TestBindingActivity
import com.liabit.viewbinding.inflate
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private val binding by inflate<ActivityMainBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Preference.init(applicationContext)

        val uri = "content://com.gree.themestore.fileprovider/share_files/Ringtones/shanhuhai.mp3"

        binding.notificationChannelTest.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val ringtones = getExternalFilesDir(Environment.DIRECTORY_RINGTONES)
                val shanhuhai = File(ringtones, "shanhuhai.mp3")
                val yequ = File(ringtones, "yequ.mp3")
                val sannianerban = File(ringtones, "sannianerban.mp3")

                var pref = Preference.getInt("channel", 3)
                pref = when (pref) {
                    1 -> 2
                    2 -> 3
                    else -> 1
                }
                val file = when (pref) {
                    1 -> shanhuhai
                    2 -> yequ
                    else -> sannianerban
                }

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val id = "984"
                val channel = NotificationChannel(id, "liabit", NotificationManager.IMPORTANCE_HIGH)
                channel.description = getString(R.string.text_test)
                val u = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
                Preference.putInt("channel", pref)
                channel.setSound(u, channel.audioAttributes)
                Log.d("TTTT", "file: $file  uri: $u")

                notificationManager.deleteNotificationChannel(id)
                notificationManager.createNotificationChannel(channel)
            }
        }

        binding.sendNotificationChannelTest.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var pref = Preference.getInt("channel", 3)
                pref = when (pref) {
                    1 -> 2
                    2 -> 3
                    else -> 1
                }
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notification = Notification.Builder(this, "984")
                    .setAutoCancel(true)
                    .setChannelId("984")
                    .setContentText("测试通知铃声-$pref")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setSubText("测试通知-$pref")
                    .setTicker("测试通知铃声-$pref")
                    .build()
                notificationManager.notify(245, notification)
            }
        }


        play(Uri.parse(uri))

        try {
            val inputStream = contentResolver.openInputStream(Uri.parse(uri)) ?: return
            val file = File(externalCacheDir, "test.mp3")
            val byteArrayOutputStream = ByteArrayOutputStream(1024)
            val byteArray = ByteArray(1024)
            while (inputStream.read(byteArray) > 0) {
                byteArrayOutputStream.write(byteArray)
            }
            byteArrayOutputStream.writeTo(FileOutputStream(file))

            play(Uri.fromFile(file))

        } catch (e: Throwable) {
            //Log.d("TTTT", "error: ", e)
        }
    }

    private fun play(uri: Uri) {
        try {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(this, uri)
            val attr = AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setAudioAttributes(attr.build())
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                // 装载完毕回调
                it.start()
            }
        } catch (e: Throwable) {
            //Log.d("TTTT", "error: ", e)
        }

    }

    fun onClick(view: View) {

        when (view.id) {
            R.id.stateButtonTest -> {
                startActivity(Intent(this, TestStateButtonActivity::class.java))
            }

            R.id.shimmerTest -> {
                startActivity(Intent(this, TestShimmerActivity::class.java))
            }

            R.id.gestureDragTest -> {
                startActivity(Intent(this, TestDragActivity::class.java))
            }

            R.id.gestureSwipeTest -> {
                startActivity(Intent(this, TestSwipeActivity::class.java))
            }

            R.id.pickerTest -> {
                startActivity(Intent(this, TestPickerActivity::class.java))
            }

            R.id.filterTest -> {
                startActivity(Intent(this, TestFilterActivity::class.java))
            }

            R.id.tabLayoutTest -> {
                startActivity(Intent(this, TestTabLayoutActivity::class.java))
            }

            R.id.decorationTest -> {
                startActivity(Intent(this, TestRecyclerViewDecorationActivity::class.java))
            }

            R.id.addSubViewTest -> {
                startActivity(Intent(this, TestAddSubViewActivity::class.java))
            }

            R.id.tagViewTest -> {
                startActivity(Intent(this, TestTagViewActivity::class.java))
            }

            R.id.popupTest -> {
                startActivity(Intent(this, TestPopupActivity::class.java))
            }

            R.id.viewBinding -> {
                startActivity(Intent(this, TestBindingActivity::class.java))
            }

            R.id.mapLocation -> {
                startActivity(Intent(this, TestMapLocationActivity::class.java))
            }

            R.id.settings -> {
                startActivity(Intent(this, TestSettingsActivity::class.java))
            }

            R.id.timerView -> {
                startActivity(Intent(this, TestTimerActivity::class.java))
            }

            R.id.colorTest -> {
                startActivity(Intent(this, TestGradient4Activity::class.java))
            }

            R.id.progressBarTest -> {
                startActivity(Intent(this, TestProgressBarActivity::class.java))
            }

            R.id.labelViewTest -> {
                startActivity(Intent(this, TestLabelViewActivity::class.java))
            }

            R.id.cityPickerTest -> {
                startActivity(Intent(this, TestCityPickerActivity::class.java))
            }

            R.id.nestedRecyclerView -> {
                startActivity(Intent(this, TestNestedRecyclerViewActivity::class.java))
            }

            R.id.loadMoreTest -> {
                startActivity(Intent(this, TestLoadMoreMenuActivity::class.java))
            }

            R.id.imageLoader -> {
                startActivity(Intent(this, ImageLoaderActivity::class.java))
            }

            R.id.otherTest -> {
                startActivity(Intent(this, TestFragmentVisibleActivity::class.java))
            }
        }

    }
}