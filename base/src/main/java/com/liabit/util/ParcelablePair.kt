package com.liabit.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelablePair(var first: Parcelable? = null, var second: Parcelable? = null) : Parcelable {

    fun set(data: ParcelablePair?) {
        data?.let {
            this.first = it.first
            this.second = it.second
        }
    }

    inline fun <reified T> first(): T? {
        return first as? T
    }

    inline fun <reified T> second(): T? {
        return second as? T
    }
}

@Parcelize
data class ParcelableListPair(var first: Parcelable? = null, var second: List<Parcelable>? = null) : Parcelable {

    fun set(data: ParcelableListPair?) {
        data?.let {
            this.first = it.first
            this.second = it.second
        }
    }

    inline fun <reified T> first(): T? {
        return first as? T
    }

    inline fun <reified T> second(): T? {
        return second as? T
    }
}

@Parcelize
data class ListPair(var first: List<Parcelable>? = null, var second: List<Parcelable>? = null) : Parcelable {

    fun set(data: ListPair?) {
        data?.let {
            this.first = it.first
            this.second = it.second
        }
    }

    inline fun <reified T> first(): T? {
        return first as? T
    }

    inline fun <reified T> second(): T? {
        return second as? T
    }
}

