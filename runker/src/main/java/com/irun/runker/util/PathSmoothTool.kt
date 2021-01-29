package com.irun.runker.util

import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import java.util.*
import kotlin.math.sqrt

/**
 * 轨迹优化工具类
 *
 * 使用方法：
 * PathSmoothTool pathSmoothTool = new PathSmoothTool();
 * pathSmoothTool.setIntensity(2);//设置滤波强度，默认3
 * List<LatLng> mList = pathSmoothTool.kalmanFilterPath(list);
 */
class PathSmoothTool {
    companion object {
        private fun getLastLocation(oneGraspList: List<LatLng>): LatLng? {
            if (oneGraspList.isEmpty()) {
                return null
            }
            val locListSize = oneGraspList.size
            return oneGraspList[locListSize - 1]
        }

        /**
         * 计算当前点到线的垂线距离
         *
         * @param p     当前点
         * @param begin 线的起点
         * @param end   线的终点
         */
        private fun calculateDistanceFromPoint(p: LatLng, begin: LatLng, end: LatLng): Double {
            val dx = p.longitude - begin.longitude
            val dy = p.latitude - begin.latitude
            val rdx = end.longitude - begin.longitude
            val rdy = end.latitude - begin.latitude
            val dot = dx * rdx + dy * rdy
            val lenSquare = rdx * rdx + rdy * rdy
            val param = dot / lenSquare
            val x: Double
            val y: Double
            if (param < 0 || (begin.longitude == end.longitude && begin.latitude == end.latitude)) {
                x = begin.longitude
                y = begin.latitude
            } else if (param > 1) {
                x = end.longitude
                y = end.latitude
            } else {
                x = begin.longitude + param * rdx
                y = begin.latitude + param * rdy
            }
            return AMapUtils.calculateLineDistance(p, LatLng(y, x)).toDouble()
        }
    }

    private var mIntensity = 3
    private var mThreshold = 1.0f
    private var mNoiseThreshold = 10f

    fun setNoiseThreshold(noiseThreshold: Float): PathSmoothTool {
        mNoiseThreshold = noiseThreshold
        return this
    }

    fun setIntensity(intensity: Int): PathSmoothTool {
        mIntensity = intensity
        return this
    }

    fun setThreshold(threshold: Float): PathSmoothTool {
        mThreshold = threshold
        return this
    }

    /**
     * 轨迹平滑优化
     *
     * @param path 原始轨迹list,list.size大于2
     * @return 优化后轨迹list
     */
    fun pathOptimize(path: List<LatLng>): List<LatLng> {
        synchronized(this) {
            val list = removeNoisePoint(path) //去噪
            val afterList = kalmanFilterPath(list, mIntensity) //滤波
            return reducerVerticalThreshold(afterList, mThreshold) //抽稀
        }
    }

    /**
     * 轨迹线路滤波
     *
     * @param path 原始轨迹list,list.size大于2
     * @return 滤波处理后的轨迹list
     */
    fun kalmanFilterPath(path: List<LatLng>): List<LatLng> {
        return kalmanFilterPath(path, mIntensity)
    }

    /**
     * 轨迹去噪，删除垂距大于20m的点
     *
     * @param path 原始轨迹list,list.size大于2
     * @return
     */
    private fun removeNoisePoint(path: List<LatLng>): List<LatLng> {
        return reduceNoisePoint(path, mNoiseThreshold)
    }

    /**
     * 单点滤波
     *
     * @param lastLocation 上次定位点坐标
     * @param curLocation  本次定位点坐标
     * @return 滤波后本次定位点坐标值
     */
    fun kalmanFilterPoint(lastLocation: LatLng, curLocation: LatLng): LatLng? {
        return kalmanFilterPoint(lastLocation, curLocation, mIntensity)
    }

    /**
     * 轨迹抽稀
     *
     * @param path 待抽稀的轨迹list，至少包含两个点，删除垂距小于[mThreshold]的点
     * @return 抽稀后的轨迹list
     */
    fun reducerVerticalThreshold(path: List<LatLng>): List<LatLng>? {
        return reducerVerticalThreshold(path, mThreshold)
    }
    /** */
    /**
     * 轨迹线路滤波
     *
     * @param path 原始轨迹list,list.size大于2
     * @param intensity  滤波强度（1—5）
     * @return
     */
    private fun kalmanFilterPath(path: List<LatLng>, intensity: Int): List<LatLng> {
        synchronized(this) {
            val kalmanFilterList = ArrayList<LatLng>()
            if (path.size <= 2) {
                return kalmanFilterList
            }
            initial() //初始化滤波参数
            var lastLocation = path[0]
            kalmanFilterList.add(lastLocation)
            for (i in 1 until path.size) {
                val curLocation = path[i]
                val latLng = kalmanFilterPoint(lastLocation, curLocation, intensity)
                if (latLng != null) {
                    kalmanFilterList.add(latLng)
                    lastLocation = latLng
                }
            }
            return kalmanFilterList
        }
    }

    /**
     * 单点滤波
     *
     * @param lastLocation   上次定位点坐标
     * @param curLocation    本次定位点坐标
     * @param intensity 滤波强度（1—5）
     * @return 滤波后本次定位点坐标值
     */
    private fun kalmanFilterPoint(lastLocation: LatLng, curLocation: LatLng, intensity: Int): LatLng? {
        var curLoc = curLocation
        var theIntensity = intensity
        if (pdeltX == 0.0 || pdeltY == 0.0) {
            initial()
        }
        var kalmanLatLng: LatLng? = null
        if (theIntensity < 1) {
            theIntensity = 1
        } else if (theIntensity > 5) {
            theIntensity = 5
        }
        for (j in 0 until theIntensity) {
            kalmanLatLng = kalmanFilter(lastLocation.longitude, curLoc.longitude, lastLocation.latitude, curLoc.latitude)
            curLoc = kalmanLatLng
        }
        return kalmanLatLng
    }

    /*************卡尔曼滤波开始***************/
    private var lastLocationX = 0.0//上次位置
    private var currentLocationX = 0.0//这次位置
    private var lastLocationY = 0.0//上次位置
    private var currentLocationY = 0.0//这次位置
    private var estimateX = 0.0//修正后数据
    private var estimateY = 0.0//修正后数据
    private var pdeltX = 0.0//自预估偏差
    private var pdeltY = 0.0//自预估偏差
    private var mdeltX = 0.0//上次模型偏差
    private var mdeltY = 0.0//上次模型偏差
    private var gaussX = 0.0//高斯噪音偏差
    private var gaussY = 0.0//高斯噪音偏差
    private var kalmanGainX = 0.0//卡尔曼增益
    private var kalmanGainY = 0.0//卡尔曼增益
    private val mR = 0.0
    private val mQ = 0.0

    //初始模型
    private fun initial() {
        pdeltX = 0.001
        pdeltY = 0.001
        mdeltX = 5.698402909980532E-4
        mdeltY = 5.698402909980532E-4
    }

    private fun kalmanFilter(oldValue_x: Double, value_x: Double, oldValue_y: Double, value_y: Double): LatLng {
        lastLocationX = oldValue_x
        currentLocationX = value_x
        gaussX = sqrt(pdeltX * pdeltX + mdeltX * mdeltX) + mQ //计算高斯噪音偏差
        kalmanGainX = sqrt(gaussX * gaussX / (gaussX * gaussX + pdeltX * pdeltX)) + mR //计算卡尔曼增益
        estimateX = kalmanGainX * (currentLocationX - lastLocationX) + lastLocationX //修正定位点
        mdeltX = sqrt((1 - kalmanGainX) * gaussX * gaussX) //修正模型偏差
        lastLocationY = oldValue_y
        currentLocationY = value_y
        gaussY = sqrt(pdeltY * pdeltY + mdeltY * mdeltY) + mQ //计算高斯噪音偏差
        kalmanGainY = sqrt(gaussY * gaussY / (gaussY * gaussY + pdeltY * pdeltY)) + mR //计算卡尔曼增益
        estimateY = kalmanGainY * (currentLocationY - lastLocationY) + lastLocationY //修正定位点
        mdeltY = sqrt((1 - kalmanGainY) * gaussY * gaussY) //修正模型偏差
        return LatLng(estimateY, estimateX)
    }
    /************卡尔曼滤波结束**************/

    /*************抽稀算法***************/
    private fun reducerVerticalThreshold(points: List<LatLng>, threshold: Float): List<LatLng> {
        synchronized(this) {
            if (points.size <= 2) {
                return points
            }
            val result = ArrayList<LatLng>()
            for (i in points.indices) {
                val pre = getLastLocation(result)
                val cur = points[i]
                if (pre == null || i == points.size - 1) {
                    result.add(cur)
                    continue
                }
                val next = points[i + 1]
                val distance = calculateDistanceFromPoint(cur, pre, next)
                if (distance > threshold) {
                    result.add(cur)
                }
            }
            return result
        }
    }

    /**************抽稀算法结束**************/

    private fun reduceNoisePoint(inPoints: List<LatLng>, threshHold: Float): List<LatLng> {
        synchronized(this) {
            if (inPoints.size <= 2) {
                return inPoints
            }
            val result = ArrayList<LatLng>()
            for (i in inPoints.indices) {
                val pre = getLastLocation(result)
                val cur = inPoints[i]
                if (pre == null || i == inPoints.size - 1) {
                    result.add(cur)
                    continue
                }
                val next = inPoints[i + 1]
                val distance = calculateDistanceFromPoint(cur, pre, next)
                if (distance < threshHold) {
                    result.add(cur)
                }
            }
            return result
        }
    }
}