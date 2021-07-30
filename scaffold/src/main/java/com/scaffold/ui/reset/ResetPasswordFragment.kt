package com.scaffold.ui.reset

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.scaffold.R
import com.scaffold.databinding.FragmentResetPasswordBinding
import com.scaffold.ui.FormFragment
import com.scaffold.ui.login.InputValidator
import com.scaffold.util.Toast
import dagger.hilt.android.AndroidEntryPoint

/**
 * 重置密码页面
 */
@AndroidEntryPoint
class ResetPasswordFragment : FormFragment<ResetPasswordViewModel, FragmentResetPasswordBinding>() {

    private var mHandleId: String? = null

    override fun onViewCreated(activity: FragmentActivity, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener { activity.onBackPressed() }

        setupCountrySelector(binding.country, binding.arrowIcon)
        setupEditText(binding.phoneNumber, binding.clearPhone)
        setupPasswordEditText(binding.password, binding.passwordEye, binding.clearPassword)
        setupEditText(binding.verificationCode, binding.clearVerificationCode)

        binding.clickToGetVerificationCode.setOnClickListener {
            val phone = binding.phoneNumber.text.toString().trim { it <= ' ' }
            if (!InputValidator.isValidPhoneNumber(phone)) {
                Toast.show(R.string.phone_invalid)
                return@setOnClickListener
            }
            binding.clickToGetVerificationCode.isEnabled = false
            binding.clickToGetVerificationCode.asProgress()
            viewModel.getMsgCode(phone)
        }
        binding.resetPassword.setOnClickListener {
            val phone = binding.phoneNumber.text.toString().trim { it <= ' ' }
            if (phone.isEmpty()) {
                Toast.show(R.string.phone_invalid)
                return@setOnClickListener
            }
            val password = binding.password.text.toString()
            if (password.isEmpty()) {
                Toast.show(R.string.pls_input_password)
                return@setOnClickListener
            }
            val code = binding.verificationCode.text.toString()
            if (code.isEmpty()) {
                Toast.show(R.string.pls_input_msg_code)
                return@setOnClickListener
            }
            val handleId = mHandleId ?: return@setOnClickListener
            viewModel.retrieveByPhone(phone, password, code, handleId)
        }

        viewModel.liveResetResult.observe(viewLifecycleOwner) {
            if (it) {
                Toast.show(R.string.reset_password_success)
                activity.finish()
            } else {
                Toast.show(R.string.reset_password_failed)
            }
        }

        viewModel.liveError.observe(viewLifecycleOwner) {
            if (it.equals(ResetPasswordViewModel.ERROR_GET_MSG_CODE_FAILED)) {
                Toast.show(R.string.get_msg_code_failed)
            }
        }
    }

}