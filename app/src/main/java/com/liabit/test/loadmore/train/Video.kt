package com.liabit.test.loadmore.train

import android.content.Context
import android.os.Parcelable
import com.google.gson.Gson
import com.liabit.test.R
import kotlinx.parcelize.Parcelize

/**
 * 视频信息
 *
 *  {
 *      "id": 3,
 *      "uid": 1,
 *      "title": "这个是测试大文件上传，一样可以啊",
 *      "content": "<p>32r543</p>",
 *      "media": "http://8.131.231.118/xsoss/data/media/video/pv-4214900657286768-306-XS_345-446.jpg|http://8.131.231.118/xsoss/data/media/video/VIDEO4214900609090270-157-XS_345-446-2.avi",
 *      "calorie": 32,
 *      "level": 2,
 *      "levelName": "3",
 *      "minute": 0,
 *      "ptime": "2021-04-17 15:20:12",
 *      "praiseNum": 0,
 *      "commentNum": 0,
 *      "readNum": 0,
 *      "status": 0,
 *      "uobj": {
 *          "id": 1,
 *          "photo": null,
 *          "nickname": "XS"
 *      }
 *  }
 *
 */
@Parcelize
data class Video(
    var calorie: Int? = null,
    var commentNum: Int? = null,
    var content: String? = null,
    var id: Int? = null,
    var level: Int? = null,
    var levelName: String? = null,
    var media: String? = null,
    var minute: Int? = null,
    var praiseNum: Int? = null,
    var ptime: String? = null,
    var readNum: Int? = null,
    var status: Int? = null,
    var title: String? = null,
    var uid: Int? = null,
    var uobj: User? = null
) : Parcelable {

    val coverUrl: String?
        get() {
            val media = media ?: return null
            val medias = media.split("|")
            if (!medias.isNullOrEmpty()) {
                return medias[0]
            }
            return null
        }

    val videoUrl: String?
        get() {
            val media = media ?: return null
            val medias = media.split("|")
            if (!medias.isNullOrEmpty() && medias.size > 1) {
                return medias[1]
            }
            return null
        }


    companion object {
        fun mock1(): Video {
            val gson = Gson()
            val str = """{
            "id": 14,
            "uid": 1,
            "title": "6.上背部-倒三角的基础",
            "content": "<p>&nbsp; &nbsp; &nbsp; &nbsp;哑铃是一种简单、方便、实用的健身器材，它就像&ldquo;凿子&rdquo;，可以精确地雕琢人体的每一块肌肉。本站提供的这部哑铃最健身教程是著名健康教育专家赵之心为健美者以及办公一族专门设计的实用健身教程。想要拥有完美身材的你，赶快跟随练习吧!</p>\n<p>　　赵之心老师是著名的健康教育专家、健管家、国家级社会体育、指导员原北京体育大学、教师全国妇联与卫生部&ldquo;中国女性健康大讲堂&rdquo; 健康大使、国家体育总局体科所越野行走运动 首席讲师、北京市科学健身专家讲师团秘书长。赵之心老师主讲的这部哑铃最健身教程除了介绍38种经典的哑铃练习法之外，还设计了&ldquo;营养+运动+休息&rdquo;的全套健身方案，并针对肥胖者、健美者以及办公一族等不同人群，精心编排了科学、有效、合理的训练计划。</p>\n<p>　　每一种健身机械都能给你带来意想不到的惊喜。哑铃是一种用于增强肌肉力量训练的简单器材。它既可以训练单一肌肉;如增加重量，则需多个肌肉的协调，也可作为一种肌肉复合动作训练。另外，练习哑铃不受场地限制，无论走到哪里，无论多么繁忙，只要随身携带一副哑铃，即使&ldquo;偷着练&rdquo;，也能打造出完美身材。</p>",
            "media": "http://8.131.231.118/xsoss/data/media/video/pv-5021150340108414-364-XS_345-446.jpg|http://8.131.231.118/xsoss/data/media/video/VIDEO5021150319883999-480-XS_345-446-2.mp4",
            "calorie": 80,
            "level": 1,
            "levelName": "哑铃最健身",
            "minute": 0,
            "ptime": "2021-04-26 23:11:35",
            "praiseNum": 0,
            "commentNum": 0,
            "readNum": 0,
            "status": 0,
            "uobj": {
                "id": 1,
                "photo": null,
                "nickname": "XS"
            }
        }"""
            return gson.fromJson(str, Video::class.java)
        }

        fun mock2(): Video {
            val gson = Gson()
            val str = """{
            "id": 13,
            "uid": 1,
            "title": "5.性感的八块腹肌",
            "content": "<p>&nbsp; &nbsp; &nbsp; &nbsp;哑铃是一种简单、方便、实用的健身器材，它就像&ldquo;凿子&rdquo;，可以精确地雕琢人体的每一块肌肉。本站提供的这部哑铃最健身教程是著名健康教育专家赵之心为健美者以及办公一族专门设计的实用健身教程。想要拥有完美身材的你，赶快跟随练习吧!</p>\n<p>　　赵之心老师是著名的健康教育专家、健管家、国家级社会体育、指导员原北京体育大学、教师全国妇联与卫生部&ldquo;中国女性健康大讲堂&rdquo; 健康大使、国家体育总局体科所越野行走运动 首席讲师、北京市科学健身专家讲师团秘书长。赵之心老师主讲的这部哑铃最健身教程除了介绍38种经典的哑铃练习法之外，还设计了&ldquo;营养+运动+休息&rdquo;的全套健身方案，并针对肥胖者、健美者以及办公一族等不同人群，精心编排了科学、有效、合理的训练计划。</p>\n<p>　　每一种健身机械都能给你带来意想不到的惊喜。哑铃是一种用于增强肌肉力量训练的简单器材。它既可以训练单一肌肉;如增加重量，则需多个肌肉的协调，也可作为一种肌肉复合动作训练。另外，练习哑铃不受场地限制，无论走到哪里，无论多么繁忙，只要随身携带一副哑铃，即使&ldquo;偷着练&rdquo;，也能打造出完美身材。</p>",
            "media": "http://8.131.231.118/xsoss/data/media/video/pv-5021078528532282-475-XS_345-446.jpg|http://8.131.231.118/xsoss/data/media/video/VIDEO5021078519619982-844-XS_345-446-2.mp4",
            "calorie": 80,
            "level": 1,
            "levelName": "哑铃最健身",
            "minute": 0,
            "ptime": "2021-04-26 23:10:37",
            "praiseNum": 0,
            "commentNum": 0,
            "readNum": 0,
            "status": 0,
            "uobj": {
                "id": 1,
                "photo": null,
                "nickname": "XS"
            }
        }"""
            return gson.fromJson(str, Video::class.java)
        }
    }

}


fun Video.getLevelTitle(context: Context): String? {
    val ln = levelName ?: return null
    return when (ln) {
        "1" -> context.getString(R.string.primary)
        "2" -> context.getString(R.string.intermediate)
        "3" -> context.getString(R.string.advanced)
        else -> null
    }

}