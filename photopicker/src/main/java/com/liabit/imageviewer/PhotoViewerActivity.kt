package com.liabit.imageviewer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.liabit.photopicker.R

class PhotoViewerActivity : AppCompatActivity() {

    companion object {

        @JvmStatic
        fun start(
            context: Context,
            uris: List<Uri>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
        ) {
            val intent = Intent(context, PhotoViewerActivity::class.java)
            intent.putParcelableArrayListExtra(PhotoViewerFragment.PHOTO_LIST, Photo.fromUriList(uris))
            intent.putExtra(PhotoViewerFragment.INDEX, currentIndex)
            intent.putExtra(PhotoViewerFragment.DELETABLE, deletable)
            context.startActivity(intent)
        }

        @JvmStatic
        fun startActivityForResult(
            fragment: Fragment,
            requestCode: Int,
            uris: List<Uri>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
        ) {
            val intent = Intent(fragment.requireContext(), PhotoViewerActivity::class.java)
            intent.putParcelableArrayListExtra(PhotoViewerFragment.PHOTO_LIST, Photo.fromUriList(uris))
            intent.putExtra(PhotoViewerFragment.INDEX, currentIndex)
            intent.putExtra(PhotoViewerFragment.DELETABLE, deletable)
            fragment.startActivityForResult(intent, requestCode)
        }

        @JvmStatic
        fun startActivityForResult(
            activity: Activity,
            requestCode: Int,
            uris: List<Uri>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
        ) {
            val intent = Intent(activity, PhotoViewerActivity::class.java)
            intent.putParcelableArrayListExtra(PhotoViewerFragment.PHOTO_LIST, Photo.fromUriList(uris))
            intent.putExtra(PhotoViewerFragment.INDEX, currentIndex)
            intent.putExtra(PhotoViewerFragment.DELETABLE, deletable)
            activity.startActivityForResult(intent, requestCode)
        }

        @JvmStatic
        fun startPhotoViewer(
            context: Context,
            photos: ArrayList<Photo>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
        ) {
            val intent = Intent(context, PhotoViewerActivity::class.java)
            intent.putParcelableArrayListExtra(PhotoViewerFragment.PHOTO_LIST, photos)
            intent.putExtra(PhotoViewerFragment.INDEX, currentIndex)
            intent.putExtra(PhotoViewerFragment.DELETABLE, deletable)
            context.startActivity(intent)
        }

        @JvmStatic
        fun startPhotoViewer(
            fragment: Fragment,
            requestCode: Int,
            photos: ArrayList<Photo>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
        ) {
            val intent = Intent(fragment.requireContext(), PhotoViewerActivity::class.java)
            intent.putParcelableArrayListExtra(PhotoViewerFragment.PHOTO_LIST, photos)
            intent.putExtra(PhotoViewerFragment.INDEX, currentIndex)
            intent.putExtra(PhotoViewerFragment.DELETABLE, deletable)
            fragment.startActivityForResult(intent, requestCode)
        }

        @JvmStatic
        fun startPhotoViewer(
            activity: Activity,
            requestCode: Int,
            photos: ArrayList<Photo>,
            currentIndex: Int = 0,
            deletable: Boolean = false,
        ) {
            val intent = Intent(activity, PhotoViewerActivity::class.java)
            intent.putParcelableArrayListExtra(PhotoViewerFragment.PHOTO_LIST, photos)
            intent.putExtra(PhotoViewerFragment.INDEX, currentIndex)
            intent.putExtra(PhotoViewerFragment.DELETABLE, deletable)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutUnderSystemUI(false)
        if (savedInstanceState == null) {
            val photos = intent.getParcelableArrayListExtra<Photo>(PhotoViewerFragment.PHOTO_LIST) ?: return
            val index = intent.getIntExtra(PhotoViewerFragment.INDEX, 0)
            val deletable = intent.getBooleanExtra(PhotoViewerFragment.DELETABLE, false)
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, PhotoViewerFragment.fromPhotos(photos, index, deletable))
                .commitAllowingStateLoss()
        }
    }

    private fun layoutUnderSystemUI(lightStatusBar: Boolean? = null, lightNavigationBar: Boolean? = null) {
        val flag = window.decorView.systemUiVisibility
        val lightStatus = lightStatusBar ?: ((flag and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0)
        val lightNavigation = lightNavigationBar
            ?: ((flag and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR) != 0)
        var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (lightStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (lightNavigation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = flags
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.pv_fade_out)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (dispatchKeyDownEvent(keyCode, event)) true else super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return if (dispatchKeyUpEvent(keyCode, event)) true else super.onKeyUp(keyCode, event)
    }

    private fun dispatchKeyDownEvent(keyCode: Int, event: KeyEvent): Boolean {
        val fragmentList = supportFragmentManager.fragments
        for (fragment in fragmentList) {
            if (fragment.isVisible && fragment is KeyEventListener) {
                return (fragment as KeyEventListener).onKeyDown(keyCode, event)
            }
        }
        return false
    }

    private fun dispatchKeyUpEvent(keyCode: Int, event: KeyEvent): Boolean {
        val fragmentList = supportFragmentManager.fragments
        for (fragment in fragmentList) {
            if (fragment.isVisible && fragment is KeyEventListener) {
                return (fragment as KeyEventListener).onKeyUp(keyCode, event)
            }
        }
        return false
    }

}
