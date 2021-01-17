package com.liabit.test.mock

import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/11/6 10:53
 * 用于测试的图片
 */
object MockText {

    private const val t0 = "道可道，非常道；名可名，非常名。无名天地之始，有名万物之母。故常无欲，以观其妙；常有欲，以观其徼（jiào）。此两者同出而异名，同谓之玄，玄之又玄，众妙之门。"
    private const val t1 = "天下皆知美之为美，斯恶（è）已；皆知善之为善，斯不善已。故有无相生，难易相成，长短相较，高下相倾，音声相和（hè），前后相随。是以圣人处无为之事，行不言之教，万物作焉而不辞，生而不有，为而不恃，功成而弗居。夫（fú）唯弗居，是以不去。"
    private const val t2 = "不尚贤，使民不争；不贵难得之货，使民不为盗；不见（xiàn）可欲，使民心不乱。是以圣人之治，虚其心，实其腹；弱其志，强其骨。常使民无知无欲，使夫（fú）智者不敢为也。为无为，则无不治。"
    private const val t3 = "道冲而用之或不盈，渊兮似万物之宗。挫其锐，解其纷，和其光，同其尘。湛兮似或存，吾不知谁之子，象帝之先。"
    private const val t4 = "天地不仁，以万物为刍（chú）狗；圣人不仁，以百姓为刍狗。天地之间，其犹橐龠（tuóyuè）乎？虚而不屈，动而愈出。多言数（shuò）穷，不如守中。"
    private const val t5 = "谷神不死，是谓玄牝（pìn），玄牝之门，是谓天地根。绵绵若存，用之不勤。"
    private const val t6 = "天长地久。天地所以能长且久者，以其不自生，故能长生。是以圣人后其身而身先，外其身而身存。非以其无私邪（yé）？故能成其私。"
    private const val t7 = "上善若水。水善利万物而不争，处众人之所恶（wù），故几（jī）于道。居善地，心善渊，与善仁，言善信，正善治，事善能，动善时。夫唯不争，故无尤。"
    private const val t8 = "持而盈之，不如其已。揣(chuǎi)而锐之，不可长保。金玉满堂，莫之能守。富贵而骄，自遗（yí）其咎。功成身退，天之道。"
    private const val t9 = "载（zài）营魄抱一，能无离乎？专气致柔，能婴儿乎？涤除玄览，能无疵乎？爱民治国，能无知（zhì）乎？天门开阖（hé），能无雌乎？明白四达，能无为乎？生之、畜（xù）之，生而不有，为而不恃，长（zhǎng）而不宰，是谓玄德。"

    private val pArray = arrayOf(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9)

    private val random = Random()

    @JvmStatic
    fun random(): String {
        return pArray[random.nextInt(10)]
    }

    @JvmStatic
    operator fun get(i: Int): String {
        return pArray[i % 10]
    }
}