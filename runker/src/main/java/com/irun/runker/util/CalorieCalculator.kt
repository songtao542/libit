package com.irun.runker.util

/**
 * Author:         songtao
 * CreateDate:     2020/12/7 18:55
 */
object CalorieCalculator {

    /**
     * 描述: 计算卡路里
     * 计算公式：体重（kg）* 距离（km）* 运动系数（k）
     * 运动系数：健走：k=0.8214；跑步：k=1.036；自行车：k=0.6142；轮滑、溜冰：k=0.518室外滑雪：k=0.888
     * @param weight   体重
     * @param distance 距离
     */
    fun getCalorie(weight: Double, distance: Double): Double {
        return weight * distance * 1.036
    }

}