package com.scaffold.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.scaffold.R
import com.scaffold.base.startActivity
import com.scaffold.databinding.FragmentLoginByAccountBinding
import com.scaffold.ui.FormFragment
import com.scaffold.ui.MainActivity
import com.scaffold.ui.reset.ResetPasswordFragment
import com.scaffold.util.Toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginByAccountFragment : FormFragment<LoginViewModel, FragmentLoginByAccountBinding>() {
    companion object {

        private const val TAG = "LoginByAccountFragment"

        @JvmStatic
        fun newInstance(): LoginByAccountFragment {
            return LoginByAccountFragment()
        }
    }

    override fun onViewCreated(activity: FragmentActivity, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener {
            activity.onBackPressed()
        }
        setupCountrySelector(binding.country, binding.arrowIcon)
        setupEditText(binding.phoneNumber, binding.clearPhone)
        setupPasswordEditText(binding.password, binding.passwordEye, binding.clearPassword)

        binding.toResetPassword.setOnClickListener {
            startActivity(ResetPasswordFragment::class.java, Bundle().apply {
                putString("phone", binding.phoneNumber.text?.toString())
            })
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

        binding.login.setOnClickListener(View.OnClickListener {
            val phone = binding.phoneNumber.text.toString().trim { it <= ' ' }
            if (!InputValidator.isValidPhoneNumber(phone)) {
                Toast.show(R.string.phone_invalid)
                return@OnClickListener
            }
            val password = binding.password.text.toString().trim { it <= ' ' }
            if (!InputValidator.isValidPassword(password)) {
                Toast.show(R.string.password_too_short)
                return@OnClickListener
            }
            binding.login.asProgress()
            viewModel.loginByPhone(phone, password = password)
        })

        viewModel.liveLoginResult.observe(viewLifecycleOwner) {
            MainActivity.start(activity)
            activity.finish()
        }

        viewModel.liveError.observe(viewLifecycleOwner) {
            if (it.equals(LoginViewModel.ERROR_LOGIN_FAILED)) {
                binding.login.asButton()
                Toast.show(R.string.login_failed)
            }
        }
    }

}