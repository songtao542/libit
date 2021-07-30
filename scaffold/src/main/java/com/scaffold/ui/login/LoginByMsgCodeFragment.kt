package com.scaffold.ui.login

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.scaffold.R
import com.scaffold.base.startActivity
import com.scaffold.databinding.FragmentLoginByMsgCodeBinding
import com.scaffold.dialog.AlertDialogBuilder
import com.scaffold.ui.FormFragment
import com.scaffold.ui.MainActivity
import com.scaffold.ui.register.RegisterFragment
import com.scaffold.util.Toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginByMsgCodeFragment : FormFragment<LoginViewModel, FragmentLoginByMsgCodeBinding>() {
    companion object {
        private const val TAG = "LoginFragment"

        @JvmStatic
        fun newInstance(): LoginByMsgCodeFragment {
            return LoginByMsgCodeFragment()
        }
    }

    private var mPhone: String? = null
    private var mCountryCode: String? = null
    private var mHandleId: String? = null

    override fun onViewCreated(activity: FragmentActivity, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener { activity.onBackPressed() }

        val phone = mPhone
        if (phone == null) {
            activity.onBackPressed()
            return
        }

        setupEditText(binding.inputMsgCode, binding.clearMsgCode)
        startCountDownTimer()

        binding.retryCountdown.setOnClickListener {
            viewModel.getMsgCode(phone)
        }

        binding.msgCodeHasSent.text = getString(R.string.msg_code_has_sent, phone)
        binding.msgCodeLogin.setOnClickListener(View.OnClickListener {
            val msgCode = binding.inputMsgCode.text?.toString()?.trim()
            if (msgCode.isNullOrBlank()) {
                Toast.show(R.string.pls_input_msg_code)
                return@OnClickListener
            }
            binding.msgCodeLogin.asProgress()
            viewModel.loginByMsgCode(phone, msgCode, mHandleId)
        })

        viewModel.liveLoginResult.observe(viewLifecycleOwner) {
            MainActivity.start(activity)
            activity.finish()
        }

        viewModel.liveMsgCode.observe(viewLifecycleOwner) {
            mHandleId = it.second
        }

        viewModel.liveError.observe(viewLifecycleOwner) {
            if (it.equals(LoginViewModel.ERROR_LOGIN_FAILED)) {
                binding.msgCodeLogin.asButton()
                Toast.show(R.string.login_failed)
            } else if (it.equals(LoginViewModel.ERROR_USER_NOT_EXIST)) {
                Toast.show(R.string.account_not_exist)
                val title = activity.getString(R.string.notice)
                val message = activity.getString(R.string.account_not_exist_and_to_register)
                AlertDialogBuilder(activity).setMessage(message)
                    .setTitle(title)
                    .setOnCancelListener {
                        activity.finish()
                    }
                    .setOnConfirmListener {
                        startActivity(RegisterFragment::class.java)
                        activity.finish()
                    }
                    .show()
            }
        }
    }

    fun setCountryCode(code: String?) {
        mCountryCode = code
    }

    fun setPhone(phone: String) {
        mPhone = phone
    }

    /**
     * 服务端返回的 id
     */
    fun setHandleId(handleId: String?) {
        mHandleId = handleId
    }

    override val verificationCodeCountDownText: TextView? get() = binding.retryCountdown

}