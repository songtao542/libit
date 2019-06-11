package cn.lolii.test14

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import cn.lolii.screencapture.ScreenCaptureUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addFloat()
    }

    private fun addFloat() {
        try {
            val windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            val btn = ImageButton(application)
            btn.setImageResource(R.mipmap.cut)
            val dp100 =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, application.resources.displayMetrics)

            val lp = WindowManager.LayoutParams()


            lp.type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            /*WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY*/
                2038 - 1
            else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT

            lp.width = dp100.toInt()
            lp.height = dp100.toInt()

            lp.gravity = Gravity.RIGHT or Gravity.TOP
            lp.x = 0
            lp.y = dp100.toInt() * 2

            //lp.format = PixelFormat.RGBA_8888
            lp.flags =
                (WindowManager.LayoutParams.FLAG_FULLSCREEN
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        //or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        //or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                        or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        //or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        //or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        )



            btn.setOnClickListener {
                Log.d("TTTT", "bbbbbbbbb")
                ScreenCaptureUtil.getInstance(this).startCapture()
            }

            windowManager.addView(btn, lp)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("TTTT", "error:", e)
        }
    }


}
