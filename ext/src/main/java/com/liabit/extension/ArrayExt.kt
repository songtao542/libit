package com.liabit.extension

fun <T> Array<T>.toStringArray(): Array<String> {
    return Array(this.size) {
        "${get(it)}"
    }
}