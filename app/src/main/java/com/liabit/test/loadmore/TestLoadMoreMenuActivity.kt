package com.liabit.test.loadmore

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.liabit.test.R
import com.liabit.base.startActivity
import com.liabit.test.loadmore.train.TrainTabFragment

class TestLoadMoreMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_load_more_menu)
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.loadMore -> {
                startActivity(Intent(this, TestLoadMoreActivity::class.java))
            }
            R.id.absLoadMore -> {
                startActivity(Intent(this, TestAbsLoadMoreActivity::class.java))
            }
            R.id.tab -> {
                startActivity(TrainTabFragment::class.java)
            }
        }
    }
}