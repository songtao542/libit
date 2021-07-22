package com.liabit.extension

fun <T> ArrayList<T>.addAll(c: Collection<T>, checkIsSelf: Boolean) {
    if (checkIsSelf) {
        if (c !== this) {
            addAll(c)
        }
    } else {
        addAll(c)
    }
}