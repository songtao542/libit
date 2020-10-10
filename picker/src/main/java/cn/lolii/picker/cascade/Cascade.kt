package cn.lolii.picker.cascade

/**
 * Author:         songtao
 * CreateDate:     2020/10/9 18:04
 */

interface Cascade {
    fun getDisplayName(): String
    fun getChildren(): List<Cascade>
}