package com.domain.scaffold.ui.start

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.view.Gravity
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.domain.scaffold.R
import com.domain.scaffold.autoclear.register
import com.domain.scaffold.base.BaseFragment
import com.domain.scaffold.const.Constant
import com.domain.scaffold.const.IntentKey
import com.domain.scaffold.databinding.FragmentStartBinding
import com.domain.scaffold.extension.hideSystemUI
import com.domain.scaffold.extension.setCornerRadius
import com.domain.scaffold.ui.MainActivity
import com.domain.scaffold.ui.web.WebViewActivity
import com.domain.scaffold.util.ClickSpan
import com.domain.scaffold.util.Log
import com.domain.scaffold.util.Preference
import com.domain.scaffold.widget.BottomAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class StartFragment : BaseFragment<StartViewMode, FragmentStartBinding>() {
    companion object {
        private const val TAG = "StartFragment"
        private const val PERMISSION_STATEMENT = "pers"

        fun newInstance(): StartFragment {
            return StartFragment()
        }
    }

    private val requestPermission by register(ActivityResultContracts.RequestMultiplePermissions())

    override fun onViewCreated(activity: FragmentActivity, savedInstanceState: Bundle?) {
        hideSystemUI(true)
        binding.logo.setOnClickListener {
        }
        Log.w(TAG, "startViewMode.requestStart()")
        binding.logo.setCornerRadius(R.dimen.logo_radius)
        binding.goToMain.setOnClickListener {
            startMainActivity()
        }
        binding.goToMain.setTimeEndListener {
            startMainActivity()
        }

        if (!Preference.getBoolean(PERMISSION_STATEMENT, false)) {
            binding.goToMain.visibility = View.GONE
            showPermissionStatementDialog {
                gotoMainActivity(activity)
            }
        } else {
            binding.goToMain.start(3000)
        }
    }

    private fun startMainActivity() {
        val activity = activity ?: return
        gotoMainActivity(activity)
    }

    private fun gotoMainActivity(activity: Activity) {
        binding.goToMain.setTimeEndListener(null)
        MainActivity.start(activity)
        activity.finish()
    }

    private fun showPermissionStatementDialog(action: () -> Unit) {
        val activity = activity ?: return

        val message = getString(R.string.permission_des_full)
        val agreement = getString(R.string.p_agreement)
        val privacy = getString(R.string.p_privacy)
        val spanMessage = SpannableString(message)

        val color = ContextCompat.getColor(activity, R.color.dialog_button_text_color)

        val aClick = ClickSpan(color, false) {
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra(IntentKey.TITLE, getString(R.string.user_agreement))
            intent.putExtra(IntentKey.URL, Constant.USER_AGREEMENT_URL)
            activity.startActivity(intent)
        }
        val ai = message.indexOf(agreement)
        spanMessage.setSpan(aClick, ai, ai + agreement.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        val pClick = ClickSpan(color, false) {
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra(IntentKey.TITLE, getString(R.string.privacy))
            intent.putExtra(IntentKey.URL, Constant.PRIVACY_URL)
            activity.startActivity(intent)
        }
        val pi = message.indexOf(privacy)
        spanMessage.setSpan(pClick, pi, pi + privacy.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        BottomAlertDialog(activity)
            .apply {
                setCancelable(false)
            }
            .setDialogTitle(R.string.welcome_to_use)
            .setMessage(spanMessage)
            .setMessageGravity(Gravity.CENTER_VERTICAL)
            .setOkText(R.string.allow)
            .setOnCancelClickListener {
                activity.finish()
            }
            .setOnOkClickListener {
                launch {
                    withContext(Dispatchers.Default) {
                        Preference.putBoolean(PERMISSION_STATEMENT, true)
                    }
                }
                requestPermissions(action)
            }
            .show()
    }

    private fun requestPermissions(action: () -> Unit) {
        requestPermission?.launch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
            )
        ) {
            action()
        } ?: kotlin.run {
            action()
        }
    }

}