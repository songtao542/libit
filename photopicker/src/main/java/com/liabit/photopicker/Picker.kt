package com.liabit.photopicker

import android.app.Activity
import androidx.fragment.app.Fragment
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import com.zhihu.matisse.filter.GifSizeFilter


object Picker {
    const val REQUEST_CODE_PICK_PHOTO = 23

    @JvmStatic
    fun pickPhoto(
        fragment: Fragment? = null,
        max: Int = 1,
        crop: Boolean = false,
        requestCode: Int = REQUEST_CODE_PICK_PHOTO,
    ) {
        if (fragment == null) {
            return
        }
        val matisse = Matisse.from(fragment)
        val countable = max > 1
        val picker = matisse.choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
            .showSingleMediaType(true)
            .theme(R.style.Matisse_Zhihu)
            .countable(countable) //max == 1，则 countable = false
            .addFilter(GifSizeFilter(200, 200, 5 * Filter.K * Filter.K))
            .maxSelectable(max)
            .spanCount(4)
            .originalEnable(true)
            .maxOriginalSize(10)
            .capture(true)
            .imageEngine(GlideEngine())
        if (crop) {
            picker.crop(1f, 1f)
        } else {
            picker.crop(false)
        }
        picker.forResult(requestCode)
    }

    @JvmStatic
    fun pickPhoto(
        activity: Activity? = null,
        max: Int = 1,
        crop: Boolean = false,
        requestCode: Int = REQUEST_CODE_PICK_PHOTO,
    ) {
        if (activity == null) {
            return
        }
        val matisse = Matisse.from(activity)
        val countable = max > 1
        val picker = matisse.choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
            .showSingleMediaType(true)
            .theme(R.style.Matisse_Zhihu)
            .countable(countable) //max == 1，则 countable = false
            .addFilter(GifSizeFilter(200, 200, 5 * Filter.K * Filter.K))
            .maxSelectable(max)
            .spanCount(4)
            .originalEnable(true)
            .maxOriginalSize(10)
            .capture(true)
            .imageEngine(GlideEngine())
        if (crop) {
            picker.crop(1f, 1f)
        } else {
            picker.crop(false)
        }
        picker.forResult(requestCode)
    }

}