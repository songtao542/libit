package com.liabit.test.viewbinding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.liabit.test.R

class TestBindingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_binding)
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.viewBinding -> {
                startActivity(Intent(this, TestViewBindingActivity::class.java))
            }
            R.id.dataBinding -> {
                startActivity(Intent(this, TestDataBindingActivity::class.java))
            }
        }
    }
}