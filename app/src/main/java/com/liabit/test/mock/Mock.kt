package com.liabit.test.mock

import kotlin.math.abs
import kotlin.random.Random

object Mock {
    private val random = Random(System.currentTimeMillis())

    private const val n0 = "厦门大学"
    private const val n1 = "四川大学"
    private const val n2 = "武汉大学"
    private const val n3 = "北京大学"
    private const val n4 = "清华大学"
    private const val n5 = "中山大学"
    private const val n6 = "中南大学"
    private const val n7 = "浙江大学"
    private const val n8 = "南京大学"
    private const val n9 = "上海交通大学"
    private const val n10 = "天津大学"
    private const val n11 = "山东大学"
    private const val n12 = "湖南大学"
    private const val n13 = "同济大学"
    private const val n14 = "重庆大学"
    private const val n15 = "复旦大学"
    private const val n16 = "华中科技大学"
    private const val n17 = "华南理工大学"
    private const val n18 = "吉林大学"
    private const val n19 = "中国海洋大学"

    private const val c0 = "哲学"
    private const val c1 = "逻辑学"
    private const val c2 = "宗教学"
    private const val c3 = "伦理学"
    private const val c4 = "经济统计学"
    private const val c5 = "国民经济管理"
    private const val c6 = "资源与环境经济学"
    private const val c7 = "商务经济学"
    private const val c8 = "能源经济"
    private const val c9 = "劳动经济学"
    private const val c10 = "经济工程"
    private const val c11 = "数字经济"
    private const val c12 = "税收学"
    private const val c13 = "金融学类"
    private const val c14 = "金融工程"
    private const val c15 = "保险学"
    private const val c16 = "投资学"
    private const val c17 = "金融数学"
    private const val c18 = "信用管理"
    private const val c19 = "经济与金融"
    private const val c20 = "精算学"
    private const val c21 = "互联网金融"
    private const val c22 = "金融科技"

    private val nArray = arrayOf(n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19)

    private val cArray = arrayOf(c0, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19, c20, c21, c22)

    @JvmStatic
    operator fun get(i: Int): String {
        return abs(random.nextInt()).toString()
    }

    @JvmStatic
    fun schoolName(): String {
        return nArray[random.nextInt(20) % 20]
    }

    @JvmStatic
    fun schoolNameArray(): Array<String> {
        return nArray
    }

    @JvmStatic
    fun collegeName(): String {
        return cArray[random.nextInt(22) % 22]
    }

    @JvmStatic
    fun collegeNameArray(): Array<String> {
        return cArray
    }

    @JvmStatic
    fun nextInt(from: Int, until: Int): Int {
        return random.nextInt(from, until)
    }
}