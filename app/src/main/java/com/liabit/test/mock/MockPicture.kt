package com.liabit.test.mock

import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/11/6 10:53
 * 用于测试的图片
 */
object MockPicture {

    private var mArray = arrayOf(
        "https://cdn.pixabay.com/photo/2019/12/15/08/16/nude-4696548_960_720.jpg",
        "https://cdn.pixabay.com/photo/2017/03/23/20/57/girl-2169467_960_720.jpg",
        "https://images.pexels.com/photos/674337/pexels-photo-674337.jpeg?cs=srgb&dl=pexels-tofroscom-674337.jpg&fm=jpg",
        "https://images.pexels.com/photos/4814636/pexels-photo-4814636.jpeg?cs=srgb&dl=pexels-jonaorle-4814636.jpg&fm=jpg",
        "https://images.pexels.com/photos/3828241/pexels-photo-3828241.jpeg?cs=srgb&dl=pexels-jonaorle-3828241.jpg&fm=jpg",
        "https://images.pexels.com/photos/1406766/pexels-photo-1406766.jpeg?cs=srgb&dl=pexels-tu%E1%BA%A5n-ki%E1%BB%87t-jr-1406766.jpg&fm=jpg",
        "https://images.pexels.com/photos/1406764/pexels-photo-1406764.jpeg?cs=srgb&dl=pexels-tu%E1%BA%A5n-ki%E1%BB%87t-jr-1406764.jpg&fm=jpg",
        "https://cdn.pixabay.com/photo/2017/03/23/20/57/girl-2169467__340.jpg",
        "https://cdn.pixabay.com/photo/2019/12/16/04/37/nude-4698467__340.jpg",
        "https://images.pexels.com/photos/1391498/pexels-photo-1391498.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1319911/pexels-photo-1319911.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1382734/pexels-photo-1382734.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/884979/pexels-photo-884979.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1382726/pexels-photo-1382726.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1382728/pexels-photo-1382728.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/206395/pexels-photo-206395.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1308885/pexels-photo-1308885.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1308881/pexels-photo-1308881.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/675920/pexels-photo-675920.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/413959/pexels-photo-413959.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1391499/pexels-photo-1391499.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1386604/pexels-photo-1386604.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1462636/pexels-photo-1462636.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/245388/pexels-photo-245388.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1382730/pexels-photo-1382730.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1408978/pexels-photo-1408978.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1322137/pexels-photo-1322137.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1101596/pexels-photo-1101596.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1116308/pexels-photo-1116308.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1232594/pexels-photo-1232594.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1545589/pexels-photo-1545589.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/899954/pexels-photo-899954.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/235534/pexels-photo-235534.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/1322130/pexels-photo-1322130.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/774091/pexels-photo-774091.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/2681751/pexels-photo-2681751.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/732425/pexels-photo-732425.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/206585/pexels-photo-206585.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/6957991/pexels-photo-6957991.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/694445/pexels-photo-694445.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        "https://images.pexels.com/photos/3616937/pexels-photo-3616937.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
    )

    private val random = Random()

    @JvmStatic
    fun random(): String {
        return mArray[random.nextInt(20)]
    }

    val size: Int = mArray.size

    @JvmStatic
    operator fun get(i: Int): String {
        return mArray[i % 20]
    }
}