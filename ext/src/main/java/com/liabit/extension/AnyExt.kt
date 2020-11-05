package com.liabit.extension

val Any.TAG: String
    get() {
        return "TMS.${this::class.java.simpleName}"
    }