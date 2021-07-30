package com.scaffold.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var username: String? = null,
    var nickname: String? = null,
    var phone: String? = null
) : Parcelable