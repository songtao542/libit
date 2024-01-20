package com.liabit.autoclear

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat

/**
 * @see [androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions]
 */
class RequestMultiplePermissions : ActivityResultContract<Array<String>, Boolean>() {
    companion object {
        /**
         * An {@link Intent} action for making a permission request via a regular
         * {@link Activity#startActivityForResult} API.
         *
         * Caller must provide a {@code String[]} extra {@link #EXTRA_PERMISSIONS}
         *
         * Result will be delivered via {@link Activity#onActivityResult(int, int, Intent)} with
         * {@code String[]} {@link #EXTRA_PERMISSIONS} and {@code int[]}
         * {@link #EXTRA_PERMISSION_GRANT_RESULTS}, similar to
         * {@link Activity#onRequestPermissionsResult(int, String[], int[])}
         *
         * @see Activity#requestPermissions(String[], int)
         * @see Activity#onRequestPermissionsResult(int, String[], int[])
         */
        private const val ACTION_REQUEST_PERMISSIONS = "androidx.activity.result.contract.action.REQUEST_PERMISSIONS"

        /**
         * Key for the extra containing all the requested permissions.
         *
         * @see .ACTION_REQUEST_PERMISSIONS
         */
        private const val EXTRA_PERMISSIONS = "androidx.activity.result.contract.extra.PERMISSIONS"

        /**
         * Key for the extra containing whether permissions were granted.
         *
         * @see .ACTION_REQUEST_PERMISSIONS
         */
        private const val EXTRA_PERMISSION_GRANT_RESULTS = "androidx.activity.result.contract.extra.PERMISSION_GRANT_RESULTS"

    }

    override fun createIntent(context: Context, input: Array<String>): Intent {
        return Intent(ACTION_REQUEST_PERMISSIONS).putExtra(EXTRA_PERMISSIONS, input)
    }

    override fun getSynchronousResult(context: Context, input: Array<String>): SynchronousResult<Boolean> {
        if (input.isNullOrEmpty()) {
            return SynchronousResult(false)
        }
        var allGranted = true
        for (permission in input) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false
                break
            }
        }
        return SynchronousResult(allGranted)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        if (resultCode != Activity.RESULT_OK) return false
        if (intent == null) return false
        val permissions = intent.getStringArrayExtra(EXTRA_PERMISSIONS)
        val grantResults = intent.getIntArrayExtra(EXTRA_PERMISSION_GRANT_RESULTS)
        if (grantResults == null || permissions == null) return false
        var i = 0
        val size = permissions.size
        var allGranted = true
        while (i < size) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                allGranted = false
                break
            }
            i++
        }
        return allGranted
    }
}