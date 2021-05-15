package com.liabit.third.wx

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.liabit.base.BaseCompatActivity
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WxPayEntryActivity : BaseCompatActivity(), IWXAPIEventHandler {

    private val viewModel by viewModels<WxPayViewModel>()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!viewModel.handleIntent(intent, this)) {
            finish()
        }
        viewModel.livePayResult.observe(this) {
            finish()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (!viewModel.handleIntent(intent, this)) {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        setVisible(true)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!viewModel.handleIntent(intent, this)) {
            finish()
        }
    }

    override fun onReq(req: BaseReq?) {
    }

    override fun onResp(resp: BaseResp?) {
        // 请求返回结果后就 finish
        finish()
    }
}