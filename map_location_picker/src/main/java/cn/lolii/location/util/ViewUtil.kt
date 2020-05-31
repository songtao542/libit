package cn.lolii.location.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

object ViewUtil {
    fun colorOf(context: Context, id: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getColor(id)
        } else {
            context.resources.getColor(id)
        }
    }

    fun dip(context: Context, dip: Int): Int {
        return dip(context, dip.toFloat())
    }

    fun dip(context: Context, dip: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.resources.displayMetrics).toInt()
    }
}

/*******************Context*******************/
fun Context.colorOf(id: Int): Int {
    return ViewUtil.colorOf(this, id)
}

fun Context.dip(dip: Int): Int {
    return ViewUtil.dip(this, dip)
}

fun Context.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return when {
        resourceId > 0 -> resources.getDimensionPixelSize(resourceId)
        else -> dip(24)
    }
}

/******************Activity********************/
/**
 * Method to try to hide soft keyboard
 */
fun Activity.hideSoftKeyboard(): Boolean {
    val view = currentFocus
    view?.let {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)
    }
    return false
}

fun Activity.colorOf(id: Int): Int {
    return ViewUtil.colorOf(this, id)
}

fun Activity.dip(dip: Int): Int {
    return ViewUtil.dip(this, dip)
}

/*******************Fragment*******************/

fun Fragment.colorOf(id: Int): Int {
    return ViewUtil.colorOf(requireContext(), id)
}

fun Fragment.dip(dip: Int): Int {
    return ViewUtil.dip(requireContext(), dip)
}

/*******************View*******************/
fun View.dip(dip: Float): Int {
    return ViewUtil.dip(context, dip)
}

fun View.dip(dip: Int): Int {
    return ViewUtil.dip(context, dip)
}
