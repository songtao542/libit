package com.scaffold.ui.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.appcompat.widget.Toolbar
import com.scaffold.R
import com.scaffold.base.StubActivity
import com.scaffold.const.IntentKey
import com.scaffold.widget.EmptyView

class WebViewActivity : StubActivity() {

    companion object {
        private const val TAG = "WebViewActivity"
    }

    private var mToolbar: Toolbar? = null
    private var mWebView: WebView? = null
    private var mEmptyView: EmptyView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        mToolbar = findViewById(R.id.toolbar)
        mWebView = findViewById(R.id.web_view)
        mEmptyView = findViewById(R.id.empty_view)
        mToolbar?.setOnClickListener {
            onBackPressed()
        }

        val webView = mWebView ?: return

        mEmptyView?.addState(EmptyView.LOADING)

        configWebView(webView)

        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(webView: WebView?, url: String?): Boolean {
                val title = webView?.title
                Log.d(TAG, "shouldOverrideUrlLoading, title: $title, url: $url")
                url?.let {
                    webView?.loadUrl(it)
                }
                return true
            }

            override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
                val title = webView?.title
                Log.d(TAG, "onPageStarted, title: $title, url: $url")
            }

            override fun onPageFinished(webView: WebView?, url: String?) {
                val title = webView?.title
                Log.d(TAG, "onPageFinished, title: $title, url: $url")
                mEmptyView?.post {
                    mEmptyView?.visibility = View.GONE
                }
            }

            override fun onReceivedError(webView: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                Log.d(TAG, "onReceivedError, errorCode: $errorCode, description: $description, failingUrl: $failingUrl")
            }

            override fun onReceivedSslError(webView: WebView?, handler: SslErrorHandler, er: SslError?) {
                // Ignore SSL certificate errors
                handler.proceed()
            }
        }

        intent.getStringExtra(IntentKey.TITLE)?.let {
            mToolbar?.setTitle(it)
        }
        intent.getStringExtra(IntentKey.URL)?.let {
            //"file:///android_asset/user_agreement.html"
            mWebView?.loadUrl(it)
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    private fun configWebView(webView: WebView) {
        webView.clearCache(true)
        val webSettings: WebSettings = webView.settings
        val inject = WebViewJsInject()
        webView.addJavascriptInterface(inject, "KuYinExt")
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptThirdPartyCookies(webView, true)
        webSettings.mediaPlaybackRequiresUserGesture = false
        // 无缓存
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        // User settings
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.useWideViewPort = true //关键点
        //webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.displayZoomControls = false
        webSettings.javaScriptEnabled = true // 设置支持javascript脚本
        webSettings.allowFileAccess = true // 允许访问文件
        webSettings.builtInZoomControls = true // 设置显示缩放按钮
        webSettings.setSupportZoom(true) // 支持缩放
        webSettings.loadWithOverviewMode = true
        val metrics = resources.displayMetrics
        when (metrics.densityDpi) {
            240 -> {
                webSettings.defaultZoom = WebSettings.ZoomDensity.FAR
            }
            160 -> {
                webSettings.defaultZoom = WebSettings.ZoomDensity.MEDIUM
            }
            120 -> {
                webSettings.defaultZoom = WebSettings.ZoomDensity.CLOSE
            }
            DisplayMetrics.DENSITY_XHIGH -> {
                webSettings.defaultZoom = WebSettings.ZoomDensity.FAR
            }
            DisplayMetrics.DENSITY_TV -> {
                webSettings.defaultZoom = WebSettings.ZoomDensity.FAR
            }
            else -> {
                webSettings.defaultZoom = WebSettings.ZoomDensity.MEDIUM
            }
        }
        /**
         * 用WebView显示图片，可使用这个参数 设置网页布局类型：
         * 1、LayoutAlgorithm.NARROW_COLUMNS: 适应内容大小
         * 2、LayoutAlgorithm.SINGLE_COLUMN: 适应屏幕，内容将自动缩放
         */
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
    }

    inner class WebViewJsInject {
        @JavascriptInterface
        fun closeWindow() {
        }
    }


    override fun onBackPressed() {
        if (mWebView?.canGoBack() == true) {
            mWebView?.goBack()
            return
        }
        super.onBackPressed()
    }

}