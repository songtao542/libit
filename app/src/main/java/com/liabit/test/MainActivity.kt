package com.liabit.test

import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.liabit.test.databinding.ActivityMainBinding
import com.liabit.test.decorationtest.TestRecyclerViewDecorationActivity
import com.liabit.test.filtertest.TestFilterActivity
import com.liabit.test.gesturetest.TestDragActivity
import com.liabit.test.gesturetest.TestSwipeActivity
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
        val uri = "content://com.gree.themestore.fileprovider/share_files/Ringtones/shanhuhai.mp3"

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
            Log.d("TTTT", "error: ", e)
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
            Log.d("TTTT", "error: ", e)
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

            R.id.otherTest -> {
                startActivity(Intent(this, TestFragmentVisibleActivity::class.java))
            }
        }

    }
}