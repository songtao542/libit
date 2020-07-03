package com.lolii.extension

val Any.TAG: String
    get() {
        return "TMS.${this::class.java.simpleName}"
    }