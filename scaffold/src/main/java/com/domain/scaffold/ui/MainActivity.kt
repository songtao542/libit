package com.domain.scaffold.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.domain.scaffold.R
import com.domain.scaffold.base.BaseActivity
import com.domain.scaffold.databinding.ActivityMainBinding
import com.domain.scaffold.extension.isMultiTouchEnabled
import com.domain.scaffold.extension.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    companion object {
        private const val TAG = "MainActivity"

        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = (supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment).navController
        binding.bottomNavigationView.isMultiTouchEnabled = false
        binding.bottomNavigationView.setupWithNavController(navController)

        binding.toolbar.toolbar.setOnClickListener {

        }
    }

    /*override fun onSupportNavigateUp(): Boolean {
        return (supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment).navController.navigateUp()
    }*/

    private var mBackPressTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if ((System.currentTimeMillis() - mBackPressTime) > 2000) {
                    Toast.makeText(this, getString(R.string.click_again_to_exit), Toast.LENGTH_SHORT).show()
                    mBackPressTime = System.currentTimeMillis()
                } else {
                    moveTaskToBack(true)
                }
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}