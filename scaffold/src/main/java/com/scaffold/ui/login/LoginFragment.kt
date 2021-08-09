package com.scaffold.ui.login

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.scaffold.R
import com.scaffold.base.startActivity
import com.scaffold.databinding.FragmentLoginBinding
import com.scaffold.ui.FormFragment
import com.scaffold.ui.MainActivity
import com.scaffold.ui.register.RegisterFragment
import com.scaffold.util.Log
import com.scaffold.util.Toast
import com.scaffold.widget.ProgressButton
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : FormFragment<LoginViewModel, FragmentLoginBinding>() {
    companion object {
        private const val TAG = "LoginFragment"

        // 是否将 获取验证码/登录 分为两个页面
        private const val TWO_STEP_MODE = false
    }

    /**
     * first: phone
     * second: handleId
     */
    private var mPhoneMsgHandleId: Pair<String, String>? = null

    override fun onViewCreated(activity: FragmentActivity, savedInstanceState: Bundle?) {
        setupCountrySelector(binding.country, binding.arrowIcon)
        setupEditText(binding.phoneNumber, binding.clearPhone)
        if (TWO_STEP_MODE) {
            binding.commitButton.setText(R.string.get_msg_code)
            binding.row2.visibility = View.GONE
        } else {
            binding.commitButton.setText(R.string.login)
            binding.row2.visibility = View.VISIBLE
            binding.clickToGetVerificationCode.setOnClickListener {
                val phone = binding.phoneNumber.text?.toString()?.trim()
                if (phone.isNullOrBlank()) {
                    Toast.show(R.string.pls_input_phone_number)
                    return@setOnClickListener
                }
                binding.clickToGetVerificationCode.isEnabled = false
                binding.clickToGetVerificationCode.asProgress()
                viewModel.getMsgCode(phone)
            }
        }
        binding.commitButton.setOnClickListener(View.OnClickListener {
            val phone = binding.phoneNumber.text?.toString()?.trim()
            if (phone.isNullOrBlank() || !InputValidator.isValidPhoneNumber(phone)) {
                Toast.show(R.string.phone_invalid)
                return@OnClickListener
            }
            if (TWO_STEP_MODE) {
                binding.commitButton.asProgress()
                viewModel.getMsgCode(phone)
            } else {
                val msgCode = binding.verificationCode.text?.toString()?.trim()
                if (msgCode.isNullOrBlank()) {
                    Toast.show(R.string.pls_input_msg_code)
                    return@OnClickListener
                }
                binding.commitButton.asProgress()
                viewModel.loginByMsgCode(phone, msgCode)
            }
        })
        binding.accountLogin.setOnClickListener {
            activity.supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, LoginByAccountFragment.newInstance())
                .addToBackStack("byAccount")
                .commit()
        }

        viewModel.liveMsgCode.observe(viewLifecycleOwner) {
            if (TWO_STEP_MODE) {
                val fragment = LoginByMsgCodeFragment.newInstance()
                fragment.setCountryCode(binding.country.text.toString())
                fragment.setPhone(it.first)
                fragment.setHandleId(it.second)
                activity.supportFragmentManager
                    .beginTransaction()
                    .add(android.R.id.content, fragment)
                    .addToBackStack("byMsgCode")
                    .commit()
                binding.commitButton.asButton()
            } else {
                mPhoneMsgHandleId = it
                binding.clickToGetVerificationCode.asButton()
                binding.clickToGetVerificationCode.setText(R.string.msg_code_has_been_sent)
                binding.clickToGetVerificationCode.isEnabled = false
                startCountDownTimer()
            }
        }

        viewModel.liveError.observe(viewLifecycleOwner) {
            if (it.equals(LoginViewModel.ERROR_GET_MSG_CODE_FAILED)) {
                binding.commitButton.asButton()
                binding.clickToGetVerificationCode.asButton()
                binding.clickToGetVerificationCode.isEnabled = true
                Toast.show(R.string.get_msg_code_failed)
            }
        }

        viewModel.liveLoginResult.observe(viewLifecycleOwner) {
            MainActivity.start(activity)
            activity.finish()
        }

        (activity as? LoginActivity)?.let { a ->
            a.weiboViewModel.liveResult.observe(viewLifecycleOwner) {
                Log.d(TAG, "weibo result: $it")
            }
            a.wxViewModel.liveResult.observe(viewLifecycleOwner) {
                Log.d(TAG, "wx result: $it")
            }
            a.qqViewModel.liveResult.observe(viewLifecycleOwner) {
                Log.d(TAG, "qq result: $it")
            }
        }

        binding.weibo.setOnClickListener {
            launch {
                (activity as? LoginActivity)?.weiboViewModel?.authorize(activity)
            }
        }
        binding.weixin.setOnClickListener {
            launch {
                (activity as? LoginActivity)?.wxViewModel?.authorize()
            }
        }
        binding.qq.setOnClickListener {
            launch {
                (activity as? LoginActivity)?.qqViewModel?.authorize(activity)
            }
        }

        binding.register.setOnClickListener {
            Log.d(TAG, "go to register")
            startActivity(RegisterFragment::class.java, Bundle().apply {
                putString("phone", binding.phoneNumber.text?.toString())
            })
        }

    }

    override val phoneEditText: EditText? get() = binding.phoneNumber

    override val getVerificationCodeButton: ProgressButton? get() = binding.clickToGetVerificationCode

}