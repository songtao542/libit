package com.scaffold.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.scaffold.R
import com.scaffold.base.startActivity
import com.scaffold.databinding.FragmentLoginBinding
import com.scaffold.ui.FormFragment
import com.scaffold.ui.register.RegisterFragment
import com.scaffold.util.Log
import com.scaffold.util.Toast
import com.scaffold.widget.CountrySelector
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : FormFragment<LoginViewModel, FragmentLoginBinding>() {
    companion object {
        private const val TAG = "LoginFragment"
    }

    override fun onViewCreated(activity: FragmentActivity, savedInstanceState: Bundle?) {
        setupCountrySelector(binding.country, binding.arrowIcon)
        setupEditText(binding.phoneNumber, binding.clearPhone)

        binding.getMsgCode.setOnClickListener(View.OnClickListener {
            val phone = binding.phoneNumber.text?.toString()?.trim()
            if (phone.isNullOrBlank() || !InputValidator.isValidPhoneNumber(phone)) {
                Toast.show(R.string.phone_invalid)
                return@OnClickListener
            }
            binding.getMsgCode.asProgress()
            viewModel.getMsgCode(phone)
        })
        binding.accountLogin.setOnClickListener {
            activity.supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, LoginByAccountFragment.newInstance())
                .addToBackStack("byAccount")
                .commit()
        }

        viewModel.liveMsgCode.observe(viewLifecycleOwner) {
            val fragment = LoginByMsgCodeFragment.newInstance()
            fragment.setCountryCode(binding.country.text.toString())
            fragment.setPhone(it.first)
            fragment.setHandleId(it.second)
            activity.supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, fragment)
                .addToBackStack("byMsgCode")
                .commit()
            binding.getMsgCode.asButton()
        }

        viewModel.liveError.observe(viewLifecycleOwner) {
            if (it.equals(LoginViewModel.ERROR_GET_MSG_CODE_FAILED)) {
                binding.getMsgCode.asButton()
                Toast.show(R.string.get_msg_code_failed)
            }
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

}