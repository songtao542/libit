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
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.liabit.integratepicker.R
import cust.app.swipeback.SwipeBackHelper
import cust.app.swipeback.SwipeBackLayout

class PhotoViewerActivity : AppCompatActivity(), SwipeBackLayout.OnSwipeBackListener {

    companion object {

        @JvmStatic
        fun start(
                context: Context,
                uris: List<Uri>,
                currentIndex: Int = 0,
                deletable: Boolean = false,
        ) {
            val intent = Intent(context, PhotoViewerActivity::class.java)
            intent.putParcelableArrayListExtra(PhotoViewerFragment.URI_LIST, ArrayList(uris))
            intent.putExtra(PhotoViewerFragment.INDEX, currentIndex)
            intent.putExtra(PhotoViewerFragment.DELETABLE, deletable)
            context.startActivity(intent)
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
            intent.putParcelableArrayListExtra(PhotoViewerFragment.URI_LIST, ArrayList(uris))
            intent.putExtra(PhotoViewerFragment.INDEX, currentIndex)
            intent.putExtra(PhotoViewerFragment.DELETABLE, deletable)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private lateinit var swipeBackHelper: SwipeBackHelper

    private lateinit var rootView: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutUnderSystemUI(false)
        if (savedInstanceState == null) {
            theme.applyStyle(R.style.Theme_SwipeBack, true)
            rootView = FrameLayout(this).apply {
                id = R.id.p_photo_viewer_id
                setBackgroundColor(Color.TRANSPARENT)
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            swipeBackHelper = SwipeBackHelper(this)
            swipeBackHelper.setContentView(rootView)
            swipeBackHelper.setSwipeBackFactor(0.8f)
            swipeBackHelper.setMaskAlpha(0)
            swipeBackHelper.setTrackingDirection(SwipeBackLayout.FROM_TOP)
            swipeBackHelper.setTrackingEdge(true)
            swipeBackHelper.setOnSwipeBackListener(this)

            val uris = intent.getParcelableArrayListExtra<Uri>(PhotoViewerFragment.URI_LIST) ?: return
            val index = intent.getIntExtra(PhotoViewerFragment.INDEX, 0)
            val deletable = intent.getBooleanExtra(PhotoViewerFragment.DELETABLE, false)
            supportFragmentManager.beginTransaction()
                    .add(R.id.p_photo_viewer_id, PhotoViewerFragment.newInstance(uris, index, deletable))
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

    override fun onViewPositionChanged(view: View?, swipeBackFraction: Float, swipeBackFactor: Float) {
        dispatchViewPositionChanged(view, swipeBackFraction, swipeBackFactor)
    }

    override fun onViewSwipeFinished(view: View?, isEnd: Boolean) {
        dispatchSwipeFinished(view, isEnd)
    }

    private fun dispatchViewPositionChanged(view: View?, swipeBackFraction: Float, swipeBackFactor: Float) {
        val fragmentList = supportFragmentManager.fragments
        for (fragment in fragmentList) {
            if (fragment.isVisible && fragment is SwipeBackLayout.OnSwipeBackListener) {
                (fragment as SwipeBackLayout.OnSwipeBackListener).onViewPositionChanged(view, swipeBackFraction, swipeBackFactor)
            }
        }
    }

    private fun dispatchSwipeFinished(view: View?, isEnd: Boolean) {
        val fragmentList = supportFragmentManager.fragments
        for (fragment in fragmentList) {
            if (fragment.isVisible && fragment is SwipeBackLayout.OnSwipeBackListener) {
                (fragment as SwipeBackLayout.OnSwipeBackListener).onViewSwipeFinished(view, isEnd)
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, android.R.anim.fade_out)
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
