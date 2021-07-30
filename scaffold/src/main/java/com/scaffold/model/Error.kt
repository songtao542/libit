package com.scaffold.model

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Error(
    var message: String? = null,
    var code: Int? = null
) : Parcelable {

    constructor(code: Int?) : this(null, code)
    constructor(message: String?) : this(message, null)

    override fun equals(other: Any?): Boolean {
        if (other is Int) {
            return code == other
        }
        return super.equals(other)
    }
}

fun Int.toError(): Error {
    return Error(this)
}

fun Int.toError(context: Context): Error {
    return Error(context.getString(this))
}

fun String.toError(): Error {
    return Error(this)
}