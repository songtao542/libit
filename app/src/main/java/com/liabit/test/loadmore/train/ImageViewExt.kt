package com.liabit.test.loadmore.train

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

fun ImageView.load(uri: Uri?, circle: Boolean = false) {
    if (uri == null) return
    Glide.with(context)
        .load(uri)
        .runIf(circle) {
            it.circleCrop()
        }
        .into(this)
}

fun ImageView.load(uri: Uri?, @DrawableRes placeholder: Int, circle: Boolean = false) {
    if (uri == null) {
        Glide.with(context)
            .load(placeholder)
            .runIf(circle) {
                it.circleCrop()
            }
            .into(this)
    } else {
        Glide.with(context)
            .load(uri)
            .runIf(circle) {
                it.circleCrop()
            }
            .into(this)
    }
}

fun ImageView.load(url: String?, circle: Boolean = false) {
    if (url.isNullOrEmpty()) return
    Glide.with(context)
        .load(url)
        .runIf(circle) {
            it.circleCrop()
        }
        .into(this)
}

fun ImageView.load(url: String?, @DrawableRes placeholder: Int, circle: Boolean = false) {
    if (url.isNullOrEmpty()) {
        Glide.with(context)
            .load(placeholder)
            .runIf(circle) {
                it.circleCrop()
            }
            .into(this)
    } else {
        Glide.with(context)
            .load(url)
            .placeholder(placeholder)
            .runIf(circle) {
                it.circleCrop()
            }
            .into(this)
    }
}

fun ImageView.load(
    url: String?,
    @DrawableRes placeholder: Int,
    @DrawableRes fallback: Int,
    @DrawableRes error: Int,
    circle: Boolean = false
) {
    when {
        url == null -> {
            Glide.with(context)
                .load(fallback)
                .runIf(circle) {
                    it.circleCrop()
                }
                .into(this)
        }
        url.isBlank() -> {
            Glide.with(context)
                .load(error)
                .runIf(circle) {
                    it.circleCrop()
                }
                .into(this)
        }
        else -> {
            Glide.with(context)
                .load(url)
                .placeholder(placeholder)
                .fallback(fallback)
                .error(error)
                .runIf(circle) {
                    it.circleCrop()
                }
                .into(this)
        }
    }
}

fun ImageView.load(
    url: String?,
    @DrawableRes placeholder: Int,
    @DrawableRes error: Int,
    circle: Boolean = false
) {
    when {
        url.isNullOrEmpty() -> {
            Glide.with(context)
                .load(error)
                .runIf(circle) {
                    it.circleCrop()
                }
                .into(this)
        }
        else -> {
            Glide.with(context)
                .load(url)
                .placeholder(placeholder)
                .error(error)
                .runIf(circle) {
                    it.circleCrop()
                }
                .into(this)
        }
    }


}