package com.scaffold.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.scaffold.base.BaseCompatActivity
import com.scaffold.third.qq.QQAuthViewModel
import com.scaffold.third.weibo.WeiboAuthViewModel
import com.scaffold.third.wx.WxAuthViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseCompatActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.putExtra("fragment", LoginFragment::class.java.name)
            context.startActivity(intent)
        }
    }

    val weiboViewModel by viewModels<WeiboAuthViewModel>()
    val wxViewModel by viewModels<WxAuthViewModel>()
    val qqViewModel by viewModels<QQAuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val fragmentClass = intent.getStringExtra("fragment") ?: return
            val clazz = Class.forName(fragmentClass)
            val fragment = (clazz.newInstance() as? Fragment) ?: return
            fragment.arguments = intent.extras
            supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, fragment, clazz.simpleName)
                .commitAllowingStateLoss()
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        weiboViewModel.onActivityResult(this, requestCode, resultCode, data)
        qqViewModel.onActivityResult(requestCode, resultCode, data)
    }
}