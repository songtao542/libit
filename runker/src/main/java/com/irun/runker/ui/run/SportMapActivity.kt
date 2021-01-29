package com.irun.runker.ui.run

import android.Manifest
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.*
import com.irun.runker.R
import com.irun.runker.base.BaseActivity
import com.irun.runker.databinding.ActivitySportmapBinding
import com.irun.runker.extension.checkAndRequestPermission
import com.irun.runker.extension.checkAppPermission
import com.irun.runker.extension.dp
import com.irun.runker.extension.permissions
import com.irun.runker.location.AMapLocationClient
import com.irun.runker.location.LocationListener
import com.irun.runker.model.SportRecord
import com.irun.runker.util.CalorieCalculator
import com.irun.runker.util.PathSmoothTool
import com.irun.runker.util.Timer
import com.liabit.autoclear.autoCleared
import com.liabit.widget.SpringButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*

@AndroidEntryPoint
class SportMapActivity : BaseActivity<SportMapViewModel, ActivitySportmapBinding>() {

    companion object {
        private const val TAG = "SportMapActivity"
    }

    //运动计算相关
    private val mDecimalFormat = DecimalFormat("0.00")

    //定位监听器
    private var mLocationListener = object : LocationListener<AMapLocation> {
        override fun onLocationChanged(location: AMapLocation) {
            Log.d(TAG, "Location: ${location.latitude} - ${location.longitude}")
            updateLocation(location)
            mOnLocationChangedListener?.onLocationChanged(location)
        }
    }

    private val mLocationClient by autoCleared { AMapLocationClient(this) }

    private var mSportRecord = SportRecord()
    private var mPathSmoothTool = PathSmoothTool()
    private var mSportLatLngList = ArrayList<LatLng>()

    private lateinit var mPolylineOptions: PolylineOptions

    private var mAMap: AMap? = null
    private var mPolyline: Polyline? = null
    private var mOnLocationChangedListener: LocationSource.OnLocationChangedListener? = null

    private var isStart = false
    private val mHandler: Handler = Handler(Looper.getMainLooper())

    private var mTimeRunnable = object : Runnable {
        override fun run() {
            binding.passedTimeTextView.text = mSportRecord.getDurationAndIncrement()
            mHandler.postDelayed(this, 1000)
        }
    }

    override fun onInitialize(savedInstanceState: Bundle?) {
        binding.mapView.onCreate(savedInstanceState) // 此方法必须重写
        if (checkAndRequestPermission(*permissions)) {
            start()
        }

        binding.finishButton.setOnLongPressListener(object : SpringButton.OnLongPressListener {
            override fun onLongClick(view: View) {
                isStart = false
                mHandler.removeCallbacks(mTimeRunnable)
                stopLocation()
                if (mSportRecord.isValid) {
                    //保存数据
                    saveRecord()
                } else {
                    finish()
                }
            }

            override fun onLongClickAbort(view: View) {
            }
        })
        binding.finishButton.setOnPressListener(object : SpringButton.OnPressListener {
            override fun onClick(view: View, isLongPressStart: Boolean) {
                if (!isLongPressStart) {
                    Toast.makeText(this@SportMapActivity, R.string.long_press_to_finish, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.saveSportRecord.observe(this) {
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show()
            finish()
        }

        /*lifecycleScope.launch {
            Log.d("TTTT", " lifecycleScope.launch: ${Thread.currentThread().name}")
            val params = TextViewCompat.getTextMetricsParams(binding.mileageTextView)
            val precomputedText = withContext(Dispatchers.Default) {
                Log.d("TTTT", " lifecycleScope.launch: withContext: ${Thread.currentThread().name}")
                PrecomputedTextCompat.create("new text", params)
            }
            TextViewCompat.setPrecomputedText(binding.mileageTextView, precomputedText)
        }*/
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (checkAppPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            start()
        }
    }

    private fun start() {
        Timer(3).apply {
            setTextView(binding.countdownTextView)
            setTextWhenFinished("GO")
            setTextViewGoneWhenFinished(true)
        }.start(object : Timer.Callback {
            override fun onEnd() {
                isStart = true
                mSportRecord.duration = 0
                mSportRecord.startTime = System.currentTimeMillis()
                binding.passedTimeTextView.base = SystemClock.elapsedRealtime()
                binding.pauseButton.animate().translationY(0f).start()
                mHandler.postDelayed(mTimeRunnable, 0)
                startLocation()
            }
        })
        mAMap = binding.mapView.map
        mAMap?.let { setupMap(it) }
        mPathSmoothTool.setIntensity(4)
        mPolylineOptions = PolylineOptions().apply {
            this.color(ResourcesCompat.getColor(resources, R.color.spring_continue_button_bg_color, theme))
            this.useGradient(true)
            this.width(20f)
        }
    }

    /**
     * 开始定位
     */
    private fun startLocation() {
        //屏幕保持常亮
        binding.sportContent.keepScreenOn = true
        mLocationClient.setLocationListener(mLocationListener)
        mLocationClient.startLocation()
    }

    private fun stopLocation() {
        //屏幕取消常亮
        binding.sportContent.keepScreenOn = false
        //停止定位
        mLocationClient.stopLocation()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.continueButton -> {
                mHandler.post(mTimeRunnable)
                startLocation()
                // 隐藏 结束/继续 按钮, 显示 暂停 按钮
                binding.finishButton.animate().translationY(150.dp(this)).start()
                binding.continueButton.animate().translationY(150.dp(this)).start()
                binding.pauseButton.animate().translationY(0f).start()
            }
            R.id.pauseButton -> {
                launch {
                    val joke = viewModel.getJoke()
                    Log.d("TTTT", "getJoke: $joke")
                }

                viewModel.getJokeLiveData().observe(this) {
                    Log.d("TTTT", "getJokeLiveData: $it")
                }

                viewModel.getJokeObservable()

                mHandler.removeCallbacks(mTimeRunnable)
                stopLocation()
                // 隐藏 暂停 按钮, 显示 结束/继续 按钮
                binding.finishButton.animate().translationY(0f).start()
                binding.continueButton.animate().translationY(0f).start()
                binding.pauseButton.animate().translationY(150.dp(this)).start()
            }
        }
    }

    private fun saveRecord() {
        try {
            if (mSportRecord.isValid) {
                val meter = mSportRecord.meter
                mSportRecord.id = System.currentTimeMillis()
                mSportRecord.userId = 0
                mSportRecord.startPoint = mSportRecord.firstPoint
                mSportRecord.endPoint = mSportRecord.lastPoint
                mSportRecord.distance = meter
                mSportRecord.endTime = System.currentTimeMillis()
                val kilometer = meter / 1000.0
                //体重先写120斤
                mSportRecord.calorie = CalorieCalculator.getCalorie(60.0, kilometer)
                mSportRecord.speed = kilometer / (mSportRecord.duration.toDouble() / 3600)
                mSportRecord.distribution = mSportRecord.distribution
                viewModel.saveSportRecord(mSportRecord)
            }
        } catch (e: Throwable) {
            Log.d(TAG, "save record error: ", e)
        }
        //finish()
    }

    /**
     * 设置一些amap的属性
     */
    private fun setupMap(aMap: AMap) {
        aMap.setLocationSource(mLocationSource) // 设置定位监听
        // 自定义系统定位小蓝点
        val myLocationStyle = MyLocationStyle()
        // 设置定位的类型为定位模式 ，定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
        // 设置小蓝点的图标
        // myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation_point))
        // 设置圆形的边框颜色
        myLocationStyle.strokeColor(Color.TRANSPARENT)
        // 设置圆形的边框粗细
        myLocationStyle.strokeWidth(0f)
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(33, 0, 0, 0))
        // 设置小蓝点的锚点
        // myLocationStyle.anchor(int,int)
        // 设置发起定位请求的时间间隔
        // myLocationStyle.interval(interval)
        // 设置是否显示定位小蓝点，true 显示，false不显示
        myLocationStyle.showMyLocation(true)
        aMap.myLocationStyle = myLocationStyle
        // 设置默认定位按钮是否显示
        aMap.uiSettings.isMyLocationButtonEnabled = true
        // 设置默认缩放按钮是否显示
        aMap.uiSettings.isZoomControlsEnabled = false
        // 设置默认指南针是否显示
        aMap.uiSettings.isCompassEnabled = false
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.isMyLocationEnabled = true
        mLocationClient.getLastKnownLocation()?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
        }
    }

    private val mLocationSource: LocationSource = object : LocationSource {
        override fun activate(onLocationChangedListener: LocationSource.OnLocationChangedListener) {
            mOnLocationChangedListener = onLocationChangedListener
        }

        override fun deactivate() {
            mOnLocationChangedListener = null
        }
    }

    private fun updateLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        mSportRecord.addPoint(latLng)
        // 运动千米数
        val kilometer = mSportRecord.kilometer
        //运动距离大于0.2公里再计算配速
        if (mSportRecord.duration > 0 && kilometer > 0.2) {
            //计算配速
            val distribution = mSportRecord.duration.toDouble() / 60.0 / kilometer
            mSportRecord.distribution = distribution
            binding.speedTextView.text = mDecimalFormat.format(distribution)
            binding.mileageTextView.text = mDecimalFormat.format(kilometer)
        } else {
            mSportRecord.distribution = 0.0
            binding.speedTextView.setText(R.string.two_decimal_zero)
            binding.mileageTextView.setText(R.string.two_decimal_zero)
        }
        mSportLatLngList.clear()
        //轨迹平滑优化
        mSportLatLngList = ArrayList<LatLng>(mPathSmoothTool.pathOptimize(mSportRecord.path))
        //抽稀
        // mSportLatLngList = new ArrayList<>(mPathSmoothTool.reducerVerticalThreshold(MotionUtils.parseLatLngList(mPathRecord.path)));
        mAMap?.let {
            if (mSportLatLngList.isNotEmpty()) {
                mPolylineOptions.add(mSportLatLngList[mSportLatLngList.size - 1])
                // aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getBounds(mSportLatLngList), 18));
                it.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
            }
            mPolyline = it.addPolyline(mPolylineOptions)
        }
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        binding.countdownTextView.clearAnimation()
        mHandler.removeCallbacks(mTimeRunnable)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    private fun getBounds(list: List<LatLng>?): LatLngBounds {
        val builder = LatLngBounds.builder()
        if (list == null) {
            return builder.build()
        }
        for (latLng in list) {
            builder.include(latLng)
        }
        return builder.build()
    }

    override fun onBackPressed() {
        if (isStart) {
            if (binding.finishButton.translationY == 0f) {
                Toast.makeText(this, R.string.please_finish_to_quit, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.please_pause_and_finish_to_quit, Toast.LENGTH_SHORT).show()
            }
            return
        }
        if (mSportRecord.isValid) {
            AlertDialog.Builder(this)
                .setTitle(R.string.confirm_to_quit)
                .setMessage(R.string.quit_tip)
                .setPositiveButton(R.string.confirm) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return
        }
        super.onBackPressed()
    }
}