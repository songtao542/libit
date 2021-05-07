package com.liabit.test.loadmore.train

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * {
 *     "id":1,
 *     "role":3,
 *     "account":"13580050152",
 *     "nickname":"妮妮",
 *     "photo":"http://xx.com/core/data/img/123.jpg",
 *     "wxopenid":""
 *     "name":"安妮",
 *     "sex":1,
 *     "height":172,
 *     "weight":127,
 *     "phone":"13580050152",
 *     "sign":"我吹过你吹过的晚风",
 *     "credit":2,
 *     "rtime":"2021-03-12 12:28:00",
 *     "status":2
 * }
 */
@Suppress("MemberVisibilityCanBePrivate")
@Parcelize
data class User(
    var id: Int? = null,
    var role: Int? = null,
    var account: String? = null,
    var nickname: String? = null,
    var photo: String? = null,
    var wxopenid: String? = null,
    var name: String? = null,
    var sex: Int? = null,
    var height: Int? = null,
    var weight: Double? = null,
    var phone: String? = null,
    var sign: String? = null,
    var credit: Int? = null,
    var rtime: String? = null,
    var status: Int? = null,
    /**
     *  记录sid(token)
     */
    var sid: String? = null
) : Parcelable {
    companion object {
        val Anonymous = User()

        //角色 1：超级管理员 2：管理员 3：普通用户 4：教职工 5：学生
        const val NORMAL = 3
        const val TEACHER = 4
        const val STUDENT = 5

        fun isRole(role: Int?, expect: Int): Boolean {
            return expect == role
        }

    }

    fun update(user: User?): Boolean {
        val u = user ?: return false
        u.id?.let { this.id = it }
        u.role?.let { this.role = it }
        u.account?.let { this.account = it }
        u.nickname?.let { this.nickname = it }
        u.photo?.let { this.photo = it }
        u.wxopenid?.let { this.wxopenid = it }
        u.name?.let { this.name = it }
        u.sex?.let { this.sex = it }
        u.height?.let { this.height = it }
        u.weight?.let { this.weight = it }
        u.phone?.let { this.phone = it }
        u.sign?.let { this.sign = it }
        u.credit?.let { this.credit = it }
        u.rtime?.let { this.rtime = it }
        u.status?.let { this.status = it }
        return true
    }

    val isStudent: Boolean get() = isRole(role, STUDENT)

    val isTeacher: Boolean get() = isRole(role, TEACHER)

    val isSchoolUser: Boolean get() = isStudent || isTeacher

    val isWeightValid: Boolean get() = weight != null && (weight ?: 0.0) > 0.0

    val isHeightValid: Boolean get() = height != null && (height ?: 0) > 0

    val isSexValid: Boolean get() = sex != null
}

