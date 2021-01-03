package com.liabit.picker

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.liabit.citypicker.R
import java.util.*

class PickerFragment : BottomSheetDialogFragment() {

    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_MIN = "min_value"
        const val EXTRA_MAX = "max_value"
        const val EXTRA_COLUMN_1_VALUES = "column1"
        const val EXTRA_COLUMN_2_VALUES = "column2"
        const val EXTRA_SHOW_PROGRESS = "progress"

        @JvmStatic
        fun newInstance(title: String) = PickerFragment().apply {
            arguments = Bundle().apply {
                this.putString(EXTRA_TITLE, title)
                this.putBoolean(EXTRA_SHOW_PROGRESS, true)
            }
        }

        @JvmStatic
        fun newInstance(title: String, minValue: Int, maxValue: Int) = PickerFragment().apply {
            arguments = Bundle().apply {
                this.putString(EXTRA_TITLE, title)
                this.putInt(EXTRA_MIN, minValue)
                this.putInt(EXTRA_MAX, maxValue)
            }
        }

        @JvmStatic
        fun newInstance(title: String, values: Array<String>) = PickerFragment().apply {
            arguments = Bundle().apply {
                this.putString(EXTRA_TITLE, title)
                this.putStringArray(EXTRA_COLUMN_1_VALUES, values)
            }
        }

        @JvmStatic
        fun newInstance(title: String, column1: Array<String>, column2: Array<String>) = PickerFragment().apply {
            arguments = Bundle().apply {
                this.putString(EXTRA_TITLE, title)
                this.putStringArray(EXTRA_COLUMN_1_VALUES, column1)
                this.putStringArray(EXTRA_COLUMN_2_VALUES, column2)
            }
        }
    }

    private var column1View: NumberPicker? = null
    private var column2View: NumberPicker? = null
    private var title: TextView? = null
    private var confirm: TextView? = null
    private var progress: View? = null
    private var pickers: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.cp_fragment_picker, container, false)
        title = view.findViewById(R.id.title)
        confirm = view.findViewById(R.id.confirm)
        progress = view.findViewById(R.id.progress)
        pickers = view.findViewById(R.id.pickers)
        column1View = view.findViewById(R.id.column1View)
        column2View = view.findViewById(R.id.column2View)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        column1View?.let {
            it.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            it.wrapSelectorWheel = false
            setDividerHeight(it, 0.4f)
            setDividerColor(it, colorOf(R.color.cp_picker_title))
        }

        column2View?.let {
            it.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            it.wrapSelectorWheel = false
            setDividerHeight(it, 0.4f)
            setDividerColor(it, colorOf(R.color.cp_picker_title))
        }

        arguments?.let {
            val title = it.getString(EXTRA_TITLE)
            this.title?.text = title ?: getString(R.string.cp_please_select)
            val showProgress = it.getBoolean(EXTRA_SHOW_PROGRESS, false)
            showProgress(showProgress)

            val minValue = it.getInt(EXTRA_MIN, -1)
            val maxValue = it.getInt(EXTRA_MAX, -1)
            if (minValue != -1 && maxValue != -1) {
                setColumnInternal(minValue, maxValue)
            }

            val column1Values = it.getStringArray(EXTRA_COLUMN_1_VALUES)
            val column2Values = it.getStringArray(EXTRA_COLUMN_2_VALUES)
            if (column1Values != null) {
                this.column1Values = column1Values
            }
            if (column2Values != null) {
                this.column2Values = column2Values
            }
            if (column1Values != null || column2Values != null) {
                setColumnInternal(column1Values, column2Values)
            }
        }

        confirm?.setOnClickListener {
            dismiss()
            val index1 = column1View?.value ?: return@setOnClickListener
            val index2 = column2View?.value ?: return@setOnClickListener
            onResultListener?.invoke(index1, index2)
            val value1 = column1View?.displayedValues?.get(index1) ?: return@setOnClickListener
            val value2 = column2View?.displayedValues?.get(index2) ?: return@setOnClickListener
            onValueListener?.invoke(value1, value2)
        }
    }

    private var onResultListener: ((value1: Int, value2: Int) -> Unit)? = null

    fun setOnResultListener(onResultListener: ((value: Int, value2: Int) -> Unit)) {
        this.onResultListener = onResultListener
    }

    private var onValueListener: ((value1: String, value2: String) -> Unit)? = null

    fun setOnValueListener(onValueListener: ((value: String, value2: String) -> Unit)) {
        this.onValueListener = onValueListener
    }

    var value1: Int = 0
    var value2: Int = 0

    private var minValue: Int = -1
    private var maxValue: Int = -1

    private var column1Values: Array<String>? = null
    private var column2Values: Array<String>? = null

    private var columns: LinkedHashMap<String, out List<String>>? = null

    private fun showProgress(show: Boolean) {
        if (show) {
            progress?.visibility = View.VISIBLE
            pickers?.visibility = View.GONE
        } else {
            progress?.visibility = View.GONE
            pickers?.visibility = View.VISIBLE
        }
    }

    fun setColumn(minValue: Int, maxValue: Int) {
        this.minValue = minValue
        this.maxValue = maxValue
        setColumnInternal(minValue, maxValue)
    }

    private fun setColumnInternal(minValue: Int, maxValue: Int) {
        val cv = column1View ?: return
        if (minValue >= 0 && maxValue >= 0) {
            cv.minValue = minValue
            cv.maxValue = maxValue
            if (value1 in minValue..maxValue) {
                cv.value = value1
            }
            showProgress(false)
        }
    }

    override fun onResume() {
        super.onResume()
        if (minValue != -1 && maxValue != -1) {
            setColumnInternal(minValue, maxValue)
        } else if (column1Values != null || column2Values != null) {
            setColumnInternal(column1Values, column2Values)
        } else if (columns != null) {
            setColumnInternal(columns)
        }
        if (mIsColumn2SubOfColumn1) {
            setColumn2SubOfColumn1Internal(mIsColumn2SubOfColumn1)
        }
    }

    fun setColumn(column1Values: Array<String>?, column2Values: Array<String>? = null) {
        this.column1Values = column1Values
        this.column2Values = column2Values
        setColumnInternal(column1Values, column2Values)
    }

    private fun setColumnInternal(column1Values: Array<String>?, column2Values: Array<String>? = null) {
        if (column1Values != null) {
            //先置空，以免数组越界
            column1View?.let {
                it.displayedValues = null
                it.minValue = 0
                it.maxValue = column1Values.size - 1
                it.displayedValues = column1Values
                if (value1 >= 0 && value1 < column1Values.size) {
                    it.value = value1
                } else {
                    it.value = 0
                }
                showProgress(false)
            }
        }

        if (column2Values != null) {
            column2View?.let {
                it.visibility = View.VISIBLE
                //先置空，以免数组越界
                it.displayedValues = null
                it.minValue = 0
                it.maxValue = column2Values.size - 1
                it.displayedValues = column2Values
                if (value2 >= 0 && value2 < column2Values.size) {
                    it.value = value2
                } else {
                    it.value = 0
                }
                showProgress(false)
            }
        }
    }

    private var mIsColumn2SubOfColumn1 = false

    fun setColumn2SubOfColumn1(isColumn2SubOfColumn1: Boolean) {
        this.mIsColumn2SubOfColumn1 = isColumn2SubOfColumn1
        setColumn2SubOfColumn1Internal(mIsColumn2SubOfColumn1)
    }

    private fun setColumn2SubOfColumn1Internal(isColumn2SubOfColumn1: Boolean) {
        if (isColumn2SubOfColumn1 && column1Values != null) {
            val array: Array<String> = column1Values?.copyOfRange(0, 1) ?: emptyArray()
            setColumnInternal(null, array)
            column1View?.setOnValueChangedListener { _, _, newVal ->
                val array2: Array<String> = column1Values?.copyOfRange(0, if (newVal == 0) 1 else newVal) ?: emptyArray()
                setColumnInternal(null, array2)
            }
        }
    }

    /**
     * 使用 LinkedHashMap 是为了保持插入顺序
     */
    fun setColumn(columns: LinkedHashMap<String, out List<String>>) {
        this.columns = columns
        setColumnInternal(columns)
    }

    private fun setColumnInternal(columns: LinkedHashMap<String, out List<String>>?) {
        val column1View = column1View ?: return
        if (!columns.isNullOrEmpty()) {
            column1Values = columns.keys.toTypedArray().also {
                setColumnInternal(it, null)
                val index = if (value1 >= 0 && value1 < it.size) value1 else 0
                columns[it[index]]?.toTypedArray()?.let { array ->
                    setColumnInternal(null, array)
                }
                column1View.setOnValueChangedListener { _, _, newVal ->
                    columns[it[newVal]]?.toTypedArray()?.let { array ->
                        setColumnInternal(null, array)
                    }
                }
            }
        }
    }

    private fun colorOf(id: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireContext().getColor(id)
        } else {
            @Suppress("DEPRECATION")
            requireContext().resources.getColor(id)
        }
    }

    /**
     * 设置picker分割线的颜色
     */
    private fun setDividerColor(numberPicker: NumberPicker, color: Int) {
        NumberPicker::class.java.getDeclaredField("mSelectionDivider")?.let {
            it.isAccessible = true
            it.set(numberPicker, ColorDrawable(color))
        }
    }

    /**
     * 设置picker分割线的宽度
     */
    private fun setDividerHeight(numberPicker: NumberPicker, height: Float) {
        val fields = NumberPicker::class.java.declaredFields
        for (field in fields) {
            if (field.name == "mSelectionDividerHeight") {
                field.isAccessible = true
                field.set(numberPicker, dip(height))
                break
            }
        }
    }

    private fun dip(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
    }

}
