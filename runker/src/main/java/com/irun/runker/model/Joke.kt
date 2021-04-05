package com.irun.runker.model

import android.os.Parcelable

import kotlinx.parcelize.Parcelize


/**
 * Author:         songtao
 * CreateDate:     2020/12/10 17:26
 */
@Parcelize
data class Joke(
        var id: Int? = null,
        var content: String? = null,
        var updateTime: String? = null
) : Parcelable