package com.irun.runker.model

import android.os.Parcelable
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.irun.runker.BuildConfig
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
@Entity(tableName = "SportRecord")
@TypeConverters(value = [RoomConverter::class])
data class SportRecord(
    @PrimaryKey
    var id: Long? = null,
    //用户ID
    var userId: Long? = null,
    //运动距离
    var distance: Double? = null,
    //运动时长
    var duration: Long = 0,
    //运动轨迹
    var path: ArrayList<LatLng> = ArrayList(),
    //运动开始点
    var startPoint: LatLng? = null,
    //运动结束点
    var endPoint: LatLng? = null,
    //运动开始时间
    var startTime: Long? = null,
    //运动结束时间
    var endTime: Long? = null,
    //消耗卡路里
    var calorie: Double? = null,
    //平均时速(公里/小时)
    var speed: Double? = null,
    //平均配速(分钟/公里)
    var distribution: Double? = null,
    //备注
    var remark: String? = null,
) : Parcelable {
    companion object {
        private const val FORMAT_TWO_NUMBER = "%02d:%02d:%02d"
    }

    fun addPoint(point: LatLng) {
        if (path.size > 0) {
            val lastLatLng: LatLng = path[path.size - 1]
            val distance = AMapUtils.calculateLineDistance(lastLatLng, point).toDouble()
            if (BuildConfig.DEBUG) {
                Log.d("SportRecord", "addPoint distance: $distance")
            }
            if (distance > 2) {
                path.add(point)
            }
        } else {
            path.add(point)
        }
    }

    val isValid: Boolean get() = path.size > 2

    val firstPoint: LatLng? get() = if (isValid) path[0] else null

    val lastPoint: LatLng? get() = if (isValid) path[path.size - 1] else null

    val meter: Double
        get() {
            //计算距离
            var distance = 0.0
            if (!isValid) {
                return distance
            }
            for (i in 0 until path.size - 1) {
                val firstLatLng: LatLng = path[i]
                val secondLatLng: LatLng = path[i + 1]
                distance += AMapUtils.calculateLineDistance(firstLatLng, secondLatLng).toDouble()
            }
            return distance
        }

    val kilometer: Double get() = meter / 1000.0

    fun getDurationAndIncrement(): String {
        val hh: Long = if (duration / 3600 > 9) duration / 3600 else duration / 3600
        val mm: Long = if (duration % 3600 / 60 > 9) duration % 3600 / 60 else duration % 3600 / 60
        val ss: Long = if (duration % 3600 % 60 > 9) duration % 3600 % 60 else duration % 3600 % 60
        duration++
        return String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, hh, mm, ss)
    }

}