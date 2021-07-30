package com.scaffold.ui

import android.os.CountDownTimer
import android.os.SystemClock
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.scaffold.R
import com.scaffold.base.BaseFragment
import com.scaffold.extension.alphaIn
import com.scaffold.extension.alphaOut
import com.scaffold.util.Log
import com.scaffold.widget.CountrySelector
import com.scaffold.widget.ProgressButton

open class FormFragment<VM : ViewModel, VB : ViewBinding> : BaseFragment<VM, VB>() {

    companion object {
        private const val TAG = "FormFragment"
    }

    private var mCountrySelector: CountrySelector? = null
    private var mCountDownTimer: CountDownTimer? = null

    private var mMillisUntilFinished = -1L
    private var mStopTime = -1L

    fun setupCountrySelector(countrySelectButton: TextView, arrowIcon: View) {
        countrySelectButton.setOnClickListener {
            (mCountrySelector ?: CountrySelector(countrySelectButton, { code: String, _: String ->
                countrySelectButton.text = code
            }) {
                arrowIcon.animate().rotation(0f).start()
            }.also {
                mCountrySelector = it
            }).show()
            arrowIcon.animate().rotation(90f).start()
        }
    }

    fun setupEditText(editText: EditText, clear: View) {
        clear.setOnClickListener {
            editText.setText("")
        }
        editText.addTextChangedListener(afterTextChanged = {
            if (!it.isNullOrBlank()) {
                if (!isVisible(clear)) {
                    clear.alphaIn()
                }
            } else {
                if (isVisible(clear)) {
                    clear.alphaOut()
                }
            }
        })
    }

    fun setupPasswordEditText(editText: EditText, eye: View, clear: View) {
        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        eye.setOnClickListener {
            if (editText.inputType != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                editText.inputType = EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                eye.isSelected = true
            } else {
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                eye.isSelected = false
            }
            try {
                editText.setSelection(editText.text.toString().length) //将光标移至文字末尾
            } catch (e: Throwable) {
                Log.d(TAG, "setSelection error: ", e)
            }
        }
        clear.setOnClickListener {
            editText.setText("")
        }
        editText.addTextChangedListener(afterTextChanged = {
            if (!it.isNullOrBlank()) {
                if (!isVisible(clear)) {
                    clear.alphaIn()
                }
            } else {
                if (isVisible(clear)) {
                    clear.alphaOut()
                }
            }
        })
    }

    private fun isVisible(view: View): Boolean {
        return view.isVisible && view.alpha != 0f
    }

    override fun onResume() {
        super.onResume()
        if (mStopTime > 0) {
            val passedTime = SystemClock.elapsedRealtime() - mStopTime
            mMillisUntilFinished -= passedTime
        }
        if (mMillisUntilFinished > 0) {
            startCountDownTimer(mMillisUntilFinished)
        }
        arguments?.let {
            val phone = it.getString("phone")
            it.remove("phone")
            if (phone != null) {
                phoneEditText?.setText(phone)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mStopTime = SystemClock.elapsedRealtime()
        mCountDownTimer?.cancel()
        mCountDownTimer = null
        mCountrySelector?.dismiss()
        mCountrySelector = null
    }

    fun startCountDownTimer(millisInFuture: Long = 60000) {
        mCountDownTimer?.cancel()
        getVerificationCodeButton?.isEnabled = false
        verificationCodeCountDownText?.isEnabled = false
        object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mMillisUntilFinished = millisUntilFinished
                val text = getString(R.string.msg_code_countdown, (millisUntilFinished / 1000).toInt())
                getVerificationCodeButton?.setText(text)
                verificationCodeCountDownText?.text = text
            }

            override fun onFinish() {
                getVerificationCodeButton?.setText(R.string.resend_msg_code)
                verificationCodeCountDownText?.setText(R.string.resend_msg_code)
                getVerificationCodeButton?.isEnabled = true
                verificationCodeCountDownText?.isEnabled = true
            }
        }.also {
            it.start()
            mCountDownTimer = it
        }
    }

    open val phoneEditText: EditText? get() = null

    open val getVerificationCodeButton: ProgressButton? get() = null
    open val verificationCodeCountDownText: TextView? get() = null
}