package com.liabit.picker.cascade

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.liabit.picker.NumberPickerView
import com.liabit.picker.R
import com.liabit.picker.datetime.ViewId

/**
 * Author:         songtao
 * CreateDate:     2020/10/9 14:00
 */
@Suppress("unused")
class CascadePickerView : LinearLayout, NumberPickerView.OnValueChangeListener {
    companion object {
        private const val TAG = "CascadePickerView"
    }

    private lateinit var mFirstPickerView: NumberPickerView
    private lateinit var mSecondPickerView: NumberPickerView
    private lateinit var mThirdPickerView: NumberPickerView

    private var mData: List<Cascade>? = null

    private var mFirstList: MutableList<String> = ArrayList(0)
    private var mSecondList: MutableList<String> = ArrayList(0)
    private var mThirdList: MutableList<String> = ArrayList(0)

    private var mOnValueChangeListener: OnValueChangeListener? = null

    constructor(context: Context) : super(context) {
        initInternal(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initInternal(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initInternal(context, attrs, defStyleAttr, 0)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initInternal(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun initInternal(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val inflate = inflate(context, R.layout.picker_cascade_layout, this)

        var autoTextSize = false
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CascadePickerView, defStyleAttr, defStyleRes)
            autoTextSize = a.getBoolean(R.styleable.CascadePickerView_autoTextSize, false)
            a.recycle()
        }

        mFirstPickerView = inflate.findViewById(R.id.picker_first)
        mSecondPickerView = inflate.findViewById(R.id.picker_second)
        mThirdPickerView = inflate.findViewById(R.id.picker_third)

        mFirstPickerView.autoTextSize = autoTextSize
        mSecondPickerView.autoTextSize = autoTextSize
        mThirdPickerView.autoTextSize = autoTextSize

        mFirstPickerView.wrapSelectorWheel = false
        mSecondPickerView.wrapSelectorWheel = false
        mThirdPickerView.wrapSelectorWheel = false

        mFirstPickerView.setOnValueChangedListener(this)
        mSecondPickerView.setOnValueChangedListener(this)
        mThirdPickerView.setOnValueChangedListener(this)
    }

    fun setData(cascade: List<Cascade>) {
        setData(cascade, 0, 0, 0)
    }

    fun setData(cascade: List<Cascade>, index1: Int, index2: Int, index3: Int) {
        mData = cascade
        var maxSecondListSize = 0
        var maxThirdListSize = 0
        for (c in cascade) {
            val cs = c.getChildren().size
            if (cs > maxSecondListSize) {
                maxSecondListSize = cs
            }
            val children = c.getChildren()
            for (cc in children) {
                val ccs = cc.getChildren().size
                if (ccs > maxThirdListSize) {
                    maxThirdListSize = ccs
                }
            }
        }
        mFirstList = ArrayList(cascade.size)
        mSecondList = ArrayList(maxSecondListSize)
        mThirdList = ArrayList(maxThirdListSize)
        for (c in cascade) {
            mFirstList.add(c.getDisplayName())
        }
        val firstIndex = if (index1 < mFirstList.size) index1 else 0
        setDisplayedValuesForPickerView(mFirstPickerView, firstIndex, 0, cascade.size - 1, mFirstList, needRespond = false, anim = false)
        updateSecondAndThirdPickerView(cascade, firstIndex, index2, index3, needRespond = false, anim = false)
        mOnValueChangeListener?.onValueChange(value)
    }

    override fun onValueChange(picker: NumberPickerView, oldVal: Int, newVal: Int) {
        Log.d(TAG, "onValueChange() ${ViewId.getViewId(picker)}  $oldVal -> $newVal")
        val data = mData ?: return
        when (picker) {
            mFirstPickerView -> {
                updateSecondAndThirdPickerView(data, newVal)
            }
            mSecondPickerView -> {
                val firstIndex = mFirstPickerView.value
                val second = data[firstIndex].getChildren()
                updateThirdPickerView(second, newVal)
            }
        }
        mOnValueChangeListener?.onValueChange(value)
    }

    private fun updateSecondAndThirdPickerView(data: List<Cascade>,
                                               index1: Int,
                                               index2: Int = -1,
                                               index3: Int = -1,
                                               needRespond: Boolean = true,
                                               anim: Boolean = true) {
        mSecondList.clear()
        val second = data[index1].getChildren()
        for (c in second) {
            mSecondList.add(c.getDisplayName())
        }
        var secondIndex = if (index2 == -1) mSecondPickerView.value else index2
        if (secondIndex >= second.size) {
            secondIndex = second.size - 1
        }
        setDisplayedValuesForPickerView(mSecondPickerView, secondIndex, 0, second.size - 1, mSecondList, needRespond, anim)
        updateThirdPickerView(second, secondIndex, index3, needRespond, anim)
    }

    private fun updateThirdPickerView(second: List<Cascade>,
                                      index2: Int,
                                      index3: Int = -1,
                                      needRespond: Boolean = true,
                                      anim: Boolean = true) {
        mThirdList.clear()
        val third = second[index2].getChildren()
        for (cc in third) {
            mThirdList.add(cc.getDisplayName())
        }
        var thirdIndex = if (index3 == -1) mThirdPickerView.value else index3
        if (thirdIndex >= third.size) {
            thirdIndex = third.size - 1
        }
        setDisplayedValuesForPickerView(mThirdPickerView, thirdIndex, 0, third.size - 1, mThirdList, needRespond, anim)
    }

    private fun setDisplayedValuesForPickerView(pickerView: NumberPickerView,
                                                newValue: Int,
                                                newStart: Int,
                                                newStop: Int,
                                                newDisplayedVales: List<String>,
                                                needRespond: Boolean = true,
                                                anim: Boolean = true) {
        if (newStart > newStop) { //规避一些错误
            Log.w(TAG, "setValuesForPickerView() newStart > newStop")
            return
        }
        require(newDisplayedVales.isNotEmpty()) { "newDisplayedVales's length should not be 0." }
        val newCount = newStop - newStart + 1
        require(newDisplayedVales.size >= newCount) { "newDisplayedVales's length should not be less than newSpan." }
        val oldStart = pickerView.minValue
        val oldStop = pickerView.maxValue
        val oldCount = oldStop - oldStart + 1
        var fromValue = pickerView.value
        if (newCount > oldCount) {
            pickerView.displayedValues = newDisplayedVales
            pickerView.minValue = newStart
            pickerView.maxValue = newStop
        } else {
            pickerView.minValue = newStart
            pickerView.maxValue = newStop
            pickerView.displayedValues = newDisplayedVales
        }
        if (anim) {
            if (fromValue < newStart) {
                fromValue = newStart
            }
            pickerView.smoothScrollToValue(fromValue, newValue, needRespond)
        } else {
            pickerView.value = newValue
        }
    }

    /**
     *  设置文字大小，单位 sp
     */
    fun setTextSize(normalTextSize: Float, selectTextSize: Float) {
        mFirstPickerView.setNormalTextSize(normalTextSize)
        mFirstPickerView.setSelectTextSize(selectTextSize)
        mSecondPickerView.setNormalTextSize(normalTextSize)
        mSecondPickerView.setSelectTextSize(selectTextSize)
        mThirdPickerView.setNormalTextSize(normalTextSize)
        mThirdPickerView.setSelectTextSize(selectTextSize)
    }

    fun setTextColor(selectedColor: Int, normalColor: Int) {
        setSelectedColor(selectedColor)
        setNormalColor(normalColor)
    }

    private fun setSelectedColor(selectedColor: Int) {
        mFirstPickerView.setSelectedTextColor(selectedColor)
        mFirstPickerView.setHintTextColor(selectedColor)
        mSecondPickerView.setSelectedTextColor(selectedColor)
        mSecondPickerView.setHintTextColor(selectedColor)
        mThirdPickerView.setSelectedTextColor(selectedColor)
        mThirdPickerView.setHintTextColor(selectedColor)
    }

    private fun setNormalColor(normalColor: Int) {
        mFirstPickerView.setNormalTextColor(normalColor)
        mSecondPickerView.setNormalTextColor(normalColor)
        mThirdPickerView.setNormalTextColor(normalColor)
    }

    fun setDividerColor(dividerColor: Int) {
        mFirstPickerView.setDividerColor(dividerColor)
        mSecondPickerView.setDividerColor(dividerColor)
        mThirdPickerView.setDividerColor(dividerColor)
    }

    data class Value(val index1: Int, val index2: Int, val index3: Int)

    val value: Value get() = Value(mFirstPickerView.value, mSecondPickerView.value, mThirdPickerView.value)

    fun setOnValueChangeListener(onTimeChangeListener: OnValueChangeListener?) {
        mOnValueChangeListener = onTimeChangeListener
    }

    interface OnValueChangeListener {
        fun onValueChange(value: Value)
    }

}