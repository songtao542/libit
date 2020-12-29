package com.liabit.test.decorationtest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.liabit.test.R
import com.liabit.test.databinding.ActivityRecyclerViewDecorationBinding
import com.liabit.viewbinding.inflate

class TestRecyclerViewDecorationActivity : AppCompatActivity() {

    private val binding by inflate<ActivityRecyclerViewDecorationBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.horizontalLinear.setOnClickListener {
            val intent = Intent(this, ShowDecorationActivity::class.java)
            intent.putExtra("TYPE", "horizontalLinear")
            startActivity(intent)
        }
        binding.verticalLinear.setOnClickListener {
            val intent = Intent(this, ShowDecorationActivity::class.java)
            intent.putExtra("TYPE", "verticalLinear")
            startActivity(intent)
        }
        binding.horizontalGrid.setOnClickListener {
            val intent = Intent(this, ShowDecorationActivity::class.java)
            intent.putExtra("TYPE", "horizontalGrid")
            startActivity(intent)
        }
        binding.verticalGrid.setOnClickListener {
            val intent = Intent(this, ShowDecorationActivity::class.java)
            intent.putExtra("TYPE", "verticalGrid")
            startActivity(intent)
        }
        binding.horizontalStaggered.setOnClickListener {
            val intent = Intent(this, ShowDecorationActivity::class.java)
            intent.putExtra("TYPE", "horizontalStaggered")
            startActivity(intent)
        }
        binding.verticalStaggered.setOnClickListener {
            val intent = Intent(this, ShowDecorationActivity::class.java)
            intent.putExtra("TYPE", "verticalStaggered")
            startActivity(intent)
        }
    }

}