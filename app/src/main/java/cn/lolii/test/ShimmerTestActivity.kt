package cn.lolii.test


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.lolii.test.R
import com.lolii.shimmer.ShimmerLayout


class ShimmerTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shimmer_test)

        val shimmerLayout = findViewById<ShimmerLayout>(R.id.shimmerLayout)
    }
}
