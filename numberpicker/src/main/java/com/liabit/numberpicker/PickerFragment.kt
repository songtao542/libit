package com.liabit.numberpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

@Suppress("unused")
class PickerFragment : BottomSheetDialogFragment() {

    private var mColumn1View: NumberPicker? = null
    private var mColumn2View: NumberPicker? = null
    private var mTitleTextView: TextView? = null
    private var mConfirmButton: TextView? = null
    private var mProgressView: View? = null
    private var mPickersWrapLayout: View? = null
    private var mOnValueListener: ((value1: CharSequence, value2: CharSequence) -> Unit)? = null
    private var mOnResultListener: ((value1: Int, value2: Int) -> Unit)? = null
    private var mTitle: CharSequence? = null
    private var mTitleResId: Int? = null
    private var mShowProgress: Boolean = false
    private var mValue1: Int = 0
    private var mValue2: Int = 0
    private var mMinValue: Int? = null
    private var mMaxValue: Int? = null
    private var mColumn1Values: Array<out CharSequence>? = null
    private var mColumn2Values: Array<out CharSequence>? = null
    private var mColumns: LinkedHashMap<out CharSequence, out List<CharSequence>>? = null
    private var mColumn2SubOfColumn1Type = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.p_fragment_picker, container, false)
        mTitleTextView = view.findViewById(R.id.title)
        mConfirmButton = view.findViewById(R.id.confirm)
        mProgressView = view.findViewById(R.id.progress)
        mPickersWrapLayout = view.findViewById(R.id.pickers)
        mColumn1View = view.findViewById(R.id.column1View)
        mColumn2View = view.findViewById(R.id.column2View)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mColumn1View?.let {
            it.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            it.wrapSelectorWheel = false
            it.setDividerHeight(0.4f)
            it.setDividerColor(requireContext().colorOf(R.color.p_picker_title))
        }

        mColumn2View?.let {
            it.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            it.wrapSelectorWheel = false
            it.setDividerHeight(0.4f)
            it.setDividerColor(requireContext().colorOf(R.color.p_picker_title))
        }

        mTitleTextView?.text = mTitle ?: getString(mTitleResId ?: R.string.np_please_select)
        showProgress(mShowProgress)

        val minValue = mMinValue
        val maxValue = mMaxValue
        if (minValue != null && maxValue != null) {
            setColumnInternal(minValue, maxValue)
        }

        if (mColumn1Values != null || mColumn2Values != null) {
            setColumnInternal(mColumn1Values, mColumn2Values)
        }

        mConfirmButton?.setOnClickListener {
            dismiss()
            val index1 = mColumn1View?.value ?: return@setOnClickListener
            val index2 = mColumn2View?.value ?: return@setOnClickListener
            mOnResultListener?.invoke(index1, index2)
            val value1 = mColumn1View?.displayedValues?.get(index1) ?: return@setOnClickListener
            val value2 = mColumn2View?.displayedValues?.get(index2) ?: return@setOnClickListener
            mOnValueListener?.invoke(value1, value2)
        }
    }

    fun setOnResultListener(onResultListener: ((value: Int, value2: Int) -> Unit)) {
        this.mOnResultListener = onResultListener
    }

    fun setOnValueListener(onValueListener: ((value: CharSequence, value2: CharSequence) -> Unit)) {
        this.mOnValueListener = onValueListener
    }

    fun setTitle(title: CharSequence) {
        mTitle = title
        mTitleTextView?.text = title
    }

    fun setTitle(titleResId: Int) {
        mTitleResId = titleResId
        mTitleTextView?.setText(titleResId)
    }

    fun setValue(value1: Int, value2: Int) {
        mValue1 = value1
        mValue2 = value2
    }

    fun setShowProgress(showProgress: Boolean) {
        mShowProgress = showProgress
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            mProgressView?.visibility = View.VISIBLE
            mPickersWrapLayout?.visibility = View.GONE
        } else {
            mProgressView?.visibility = View.GONE
            mPickersWrapLayout?.visibility = View.VISIBLE
        }
    }

    fun setColumn(minValue: Int, maxValue: Int) {
        mMinValue = minValue
        mMaxValue = maxValue
        setColumnInternal(minValue, maxValue)
    }

    private fun setColumnInternal(minValue: Int, maxValue: Int) {
        val cv = mColumn1View ?: return
        if (minValue >= 0 && maxValue >= 0) {
            cv.minValue = minValue
            cv.maxValue = maxValue
            if (mValue1 in minValue..maxValue) {
                cv.value = mValue1
            }
            showProgress(false)
        }
    }

    override fun onResume() {
        super.onResume()
        val minValue = mMinValue
        val maxValue = mMaxValue
        if (minValue != null && maxValue != null) {
            setColumnInternal(minValue, minValue)
        } else if (mColumn1Values != null || mColumn2Values != null) {
            setColumnInternal(mColumn1Values, mColumn2Values)
        } else if (mColumns != null) {
            setColumnInternal(mColumns)
        }
        if (mColumn2SubOfColumn1Type >= 0) {
            setColumn2SubOfColumn1Internal()
        }
    }

    fun setColumn(column1Values: Array<out CharSequence>?, column2Values: Array<out CharSequence>? = null) {
        mColumn1Values = column1Values
        mColumn2Values = column2Values
        setColumnInternal(column1Values, column2Values)
    }

    private fun setColumnInternal(column1Values: Array<out CharSequence>?, column2Values: Array<out CharSequence>? = null) {
        if (column1Values != null) {
            //先置空，以免数组越界
            mColumn1View?.let {
                it.displayedValues = null
                it.minValue = 0
                it.maxValue = column1Values.size - 1
                it.displayedValues = column1Values
                if (mValue1 >= 0 && mValue1 < column1Values.size) {
                    it.value = mValue1
                } else {
                    it.value = 0
                }
                showProgress(false)
            }
        }

        if (column2Values != null) {
            mColumn2View?.let {
                it.visibility = View.VISIBLE
                //先置空，以免数组越界
                it.displayedValues = null
                it.minValue = 0
                it.maxValue = column2Values.size - 1
                it.displayedValues = column2Values
                if (mValue2 >= 0 && mValue2 < column2Values.size) {
                    it.value = mValue2
                } else {
                    it.value = 0
                }
                showProgress(false)
            }
        }
    }

    /**
     * @param column2SubOfColumn1Type 0:取前半部分; 大于 0: 取后半部分
     */
    fun setColumn2SubOfColumn1(column2SubOfColumn1Type: Int) {
        this.mColumn2SubOfColumn1Type = column2SubOfColumn1Type
        setColumn2SubOfColumn1Internal()
    }

    private fun setColumn2SubOfColumn1Internal() {
        val column1Values = mColumn1Values ?: return
        val array: Array<out CharSequence> = if (mColumn2SubOfColumn1Type == 0) {
            column1Values.copyOfRange(0, 1)
        } else {
            column1Values.copyOfRange(1, column1Values.size)
        }
        setColumnInternal(null, array)
        mColumn1View?.setOnValueChangedListener { _, _, newVal ->
            val c1vs = this.mColumn1Values ?: return@setOnValueChangedListener
            val vs: Array<out CharSequence> = if (mColumn2SubOfColumn1Type == 0) {
                c1vs.copyOfRange(0, if (newVal == 0) 1 else newVal)
            } else {
                c1vs.copyOfRange(newVal, c1vs.size)
            }
            setColumnInternal(null, vs)
        }
    }

    /**
     * 使用 LinkedHashMap 是为了保持插入顺序
     */
    fun setColumn(columns: LinkedHashMap<out CharSequence, out List<CharSequence>>) {
        this.mColumns = columns
        setColumnInternal(columns)
    }

    private fun setColumnInternal(columns: LinkedHashMap<out CharSequence, out List<CharSequence>>?) {
        val column1View = mColumn1View ?: return
        if (!columns.isNullOrEmpty()) {
            mColumn1Values = columns.keys.toTypedArray().also {
                // 设置第一列数据
                setColumnInternal(it, null)
                val index1 = if (mValue1 >= 0 && mValue1 < it.size) mValue1 else 0
                columns[it[index1]]?.toTypedArray()?.let { array ->
                    // 设置第二列数据
                    setColumnInternal(null, array)
                }
                column1View.setOnValueChangedListener { _, _, newVal ->
                    // 第一列数据改变后联动更改第二列数据
                    columns[it[newVal]]?.toTypedArray()?.let { array ->
                        setColumnInternal(null, array)
                    }
                }
            }
        }
    }

    class Builder {
        private val mPicker = PickerFragment()

        fun setTitle(title: CharSequence): Builder {
            mPicker.setTitle(title)
            return this
        }

        fun setTitle(titleResId: Int): Builder {
            mPicker.setTitle(titleResId)
            return this
        }

        fun setShowProgress(showProgress: Boolean): Builder {
            mPicker.setShowProgress(showProgress)
            return this
        }

        fun setOnResultListener(onResultListener: ((value: Int, value2: Int) -> Unit)): Builder {
            mPicker.setOnResultListener(onResultListener)
            return this
        }

        fun setOnValueListener(onValueListener: ((value: CharSequence, value2: CharSequence) -> Unit)): Builder {
            mPicker.setOnValueListener(onValueListener)
            return this
        }

        fun setColumn(minValue: Int, maxValue: Int): Builder {
            mPicker.setColumn(minValue, maxValue)
            return this
        }

        fun setColumn(column1Values: Array<out CharSequence>?, column2Values: Array<out CharSequence>? = null): Builder {
            mPicker.setColumn(column1Values, column2Values)
            return this
        }

        fun setValue(value1: Int, value2: Int = 0): Builder {
            mPicker.setValue(value1, value2)
            return this
        }

        fun setColumn2SubOfColumn1(column2SubOfColumn1Type: Int): Builder {
            mPicker.setColumn2SubOfColumn1(column2SubOfColumn1Type)
            return this
        }

        fun setColumn(columns: LinkedHashMap<out CharSequence, out List<CharSequence>>): Builder {
            mPicker.setColumn(columns)
            return this
        }

        fun show(fragmentManager: FragmentManager): PickerFragment {
            mPicker.show(fragmentManager, "np")
            return mPicker
        }

    }

}
