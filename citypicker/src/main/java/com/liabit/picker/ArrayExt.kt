package com.liabit.picker

fun <T> Array<T>.toStringArray(): Array<String> {
    return Array(this.size) {
        "${get(it)}"
    }
}