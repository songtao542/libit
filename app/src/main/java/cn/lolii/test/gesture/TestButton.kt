package cn.lolii.test.gesture

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi


class TestButton : FrameLayout {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
            context, attrs, defStyleAttr, defStyleRes
    ) {
        init(context)
    }


    private fun init(context: Context) {
        setOnClickListener {
            Log.d("TTTT", "Button clicked ================ ${getIdName(id)}")
            Toast.makeText(context, "${getIdName(id)} clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getIdName(id: Int?): String {
        var i = id
        if (id == null) {
            i = getId()
        }
        if (i == null) return ""
        return resources.getResourceEntryName(i)
    }
}