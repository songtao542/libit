package com.scaffold.ui.register

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.scaffold.R
import com.scaffold.databinding.FragmentRegisterBinding
import com.scaffold.ui.FormFragment
import com.scaffold.util.Toast
import com.scaffold.widget.ProgressButton
import dagger.hilt.android.AndroidEntryPoint

/**
 * 注册页面与找回密码同一个页面
 */
@AndroidEntryPoint
class RegisterFragment : FormFragment<RegisterViewModel, FragmentRegisterBinding>() {

    private var mHandleId: String? = null

    override fun onViewCreated(activity: FragmentActivity, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener { activity.onBackPressed() }
        setupCountrySelector(binding.country, binding.arrowIcon)
        setupPasswordEditText(binding.password, binding.passwordEye, binding.clearPassword)
        setupEditText(binding.verificationCode, binding.clearVerificationCode)
        setupEditText(binding.phoneNumber, binding.clearPhone)

        binding.clickToGetVerificationCode.setOnClickListener {
            val phone = binding.phoneNumber.text?.toString()?.trim()
            if (phone.isNullOrBlank()) {
                binding.info.setText(R.string.pls_input_phone_number)
                binding.info.visibility = View.VISIBLE
                return@setOnClickListener
            }
            binding.clickToGetVerificationCode.isEnabled = false
            binding.clickToGetVerificationCode.asProgress()
            viewModel.getMsgCode(phone)
        }

        binding.register.setOnClickListener {
            val phone = binding.phoneNumber.text.toString().trim { it <= ' ' }
            if (phone.isEmpty()) {
                Toast.show(R.string.pls_input_phone_number)
            }
            val password = binding.password.text.toString().trim { it <= ' ' }
            if (password.isEmpty()) {
                Toast.show(R.string.pls_input_password)
            }
            val code = binding.verificationCode.text.toString().trim { it <= ' ' }
            if (code.isEmpty()) {
                Toast.show(R.string.pls_input_msg_code)
            }
            viewModel.registerByPhone(phone, password, code, mHandleId)
        }

        viewModel.liveMsgCode.observe(viewLifecycleOwner) {
            mHandleId = it.second
            if (!mHandleId.isNullOrBlank()) {
                binding.info.setText(R.string.msg_code_has_been_sent)
                binding.info.visibility = View.VISIBLE
                startCountDownTimer()
            } else {
                binding.clickToGetVerificationCode.isEnabled = true
                Toast.show(R.string.get_msg_code_failed)
            }
            binding.clickToGetVerificationCode.asButton()
        }

        viewModel.liveRegisterResult.observe(viewLifecycleOwner) {
            Toast.show(R.string.register_success)
        }

        viewModel.liveError.observe(viewLifecycleOwner) {
            if (it.equals(RegisterViewModel.ERROR_REGISTER_FAILED)) {
                Toast.show(R.string.register_fail)
            }
        }
    }

    override val phoneEditText: EditText? get() = binding.phoneNumber

    override val getVerificationCodeButton: ProgressButton? get() = binding.clickToGetVerificationCode
}