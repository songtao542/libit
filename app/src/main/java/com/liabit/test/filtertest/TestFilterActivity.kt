package com.liabit.test.filtertest

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.liabit.extension.layoutUnderStatusBar
import com.liabit.filter.*
import com.liabit.test.R
import com.liabit.test.databinding.ActivityFilterTestBinding
import com.liabit.viewbinding.inflate

class TestFilterActivity : AppCompatActivity() {

    private val binding by inflate<ActivityFilterTestBinding>()

    private var mPopupFilter: PopupFilter? = null
    private var mTwoColumnPopupFilter: PopupFilter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        layoutUnderStatusBar(true)

        filterData = getFilterData()
        rightFilterData = getFilterData()

        shortFilterData = ArrayList()
        val checkableList1 = SimpleFilterGroup("订单类型")
        checkableList1.add(SimpleCheckableFilterItem("网批订单"))
        checkableList1.add(SimpleCheckableFilterItem("标准订单"))
        checkableList1.add(SimpleCheckableFilterItem("非标准订单"))
        checkableList1.add(SimpleCheckableFilterItem("订货计划订单"))
        checkableList1.setSingleChoice(true)
        shortFilterData.add(checkableList1)

        binding.openFragment.setOnClickListener {
            val fragment = TestFilterFragment()
            supportFragmentManager
                    .beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commitAllowingStateLoss()
        }

        binding.showAsPopup.setOnClickListener {
            getPopupFilter().show(binding.toolbar)
        }

        binding.showAsFragment.setOnClickListener {
            val f = getFilterDialogFragment()
            f.setShowAsDialog(false)
            f.show(this)
        }

        binding.showAsDialog.setOnClickListener {
            val f = getFilterDialogFragment()
            f.setShowAsDialog(true)
            f.show(this)
        }

        binding.showTwoColumnAsPopup.setOnClickListener {
            getTwoColumnPopupFilter().show(binding.toolbar)
        }

        binding.showTwoColumnAsFragment.setOnClickListener {
            val f = getFilterDialogFragment(true)
            f.setShowAsDialog(false)
            f.show(this)
        }

        binding.showTwoColumnAsDialog.setOnClickListener {
            val f = getFilterDialogFragment(true)
            f.setShowAsDialog(true)
            f.show(this)
        }

        binding.showShortListAsFragment.setOnClickListener {
            val f = FilterDialogFragment(FilterPicker.instance)
            f.setFilter(shortFilterData)
            f.setOnResultListener(object : FilterLayout.OnResultListener {
                override fun onResult(result: List<Filter>) {
                    for (filter in result) {
                        if (filter is FilterGroup) {
                            for (child in filter.getChildren()) {
                                if (child is EditableRangeFilterItem) {
                                    Log.d("TTTT", "${child.getStartHint()}: ${child.getStartText()}  ${child.getEndHint()}: ${child.getEndText()}")
                                } else if (child is EditableFilterItem) {
                                    Log.d("TTTT", "${child.getHint()}: ${child.getText()}")
                                }
                            }
                        }
                    }
                }
            })
            f.setShowAsDialog(false)
            f.show(this)
        }

        binding.showShortListAsPopup.setOnClickListener {
            val f = PopupFilter(this)
            f.setFilterPicker(FilterPicker.instance)
            f.setFilter(shortFilterData)
            f.setOnResultListener(object : FilterLayout.OnResultListener {
                override fun onResult(result: List<Filter>) {
                    for (filter in result) {
                        if (filter is FilterGroup) {
                            for (child in filter.getChildren()) {
                                if (child is EditableRangeFilterItem) {
                                    Log.d("TTTT", "${child.getStartHint()}: ${child.getStartText()}  ${child.getEndHint()}: ${child.getEndText()}")
                                } else if (child is EditableFilterItem) {
                                    Log.d("TTTT", "${child.getHint()}: ${child.getText()}")
                                }
                            }
                        }
                    }
                }
            })
            f.show(binding.toolbar)
        }

        binding.showShortListAsDialog.setOnClickListener {
            val f = FilterDialogFragment(FilterPicker.instance)
            f.setFilter(shortFilterData)
            f.setOnResultListener(object : FilterLayout.OnResultListener {
                override fun onResult(result: List<Filter>) {
                    for (filter in result) {
                        if (filter is FilterGroup) {
                            for (child in filter.getChildren()) {
                                if (child is EditableRangeFilterItem) {
                                    Log.d("TTTT", "${child.getStartHint()}: ${child.getStartText()}  ${child.getEndHint()}: ${child.getEndText()}")
                                } else if (child is EditableFilterItem) {
                                    Log.d("TTTT", "${child.getHint()}: ${child.getText()}")
                                }
                            }
                        }
                    }
                }
            })
            f.setShowAsDialog(true)
            f.show(this)
        }
    }

    private fun getPopupFilter(): PopupFilter {
        if (mPopupFilter == null) {
            mPopupFilter = PopupFilter(this)
        }
        mPopupFilter?.setFilterPicker(FilterPicker())
        mPopupFilter?.setFilter(filterData)
        mPopupFilter?.setOnResultListener(object : FilterLayout.OnResultListener {
            override fun onResult(result: List<Filter>) {
                for (filter in result) {
                    if (filter is FilterGroup) {
                        for (child in filter.getChildren()) {
                            if (child is EditableRangeFilterItem) {
                                Log.d("TTTT", "${child.getStartHint()}: ${child.getStartText()}  ${child.getEndHint()}: ${child.getEndText()}")
                            } else if (child is EditableFilterItem) {
                                Log.d("TTTT", "${child.getHint()}: ${child.getText()}")
                            }
                        }
                    }
                }
            }
        })
        return mPopupFilter!!
    }

    private fun getTwoColumnPopupFilter(): PopupFilter {
        if (mTwoColumnPopupFilter == null) {
            mTwoColumnPopupFilter = PopupFilter(this)
        }
        mTwoColumnPopupFilter?.setFilterPicker(FilterPicker())
        mTwoColumnPopupFilter?.setFilter(filterData)
        mTwoColumnPopupFilter?.setRightPageFilter(rightFilterData)
        mTwoColumnPopupFilter?.setTab("按价格", "按订单")
        mTwoColumnPopupFilter?.setOnResultListener(object : FilterLayout.OnResultListener {
            override fun onResult(result: List<Filter>) {
                for (filter in result) {
                    if (filter is FilterGroup) {
                        for (child in filter.getChildren()) {
                            if (child is EditableRangeFilterItem) {
                                Log.d("TTTT", "${child.getStartHint()}: ${child.getStartText()}  ${child.getEndHint()}: ${child.getEndText()}")
                            } else if (child is EditableFilterItem) {
                                Log.d("TTTT", "${child.getHint()}: ${child.getText()}")
                            }
                        }
                    }
                }
            }
        })
        return mTwoColumnPopupFilter!!
    }

    private fun getFilterDialogFragment(twoColumn: Boolean = false): FilterDialogFragment {
        val f = FilterDialogFragment(FilterPicker.instance)
        f.setFilter(filterData)
        if (twoColumn) {
            f.setRightPageFilter(rightFilterData)
            f.setTab("按价格", "按订单")
        }
        f.setOnResultListener(object : FilterLayout.OnResultListener {
            override fun onResult(result: List<Filter>) {
                for (filter in result) {
                    if (filter is FilterGroup) {
                        for (child in filter.getChildren()) {
                            if (child is EditableRangeFilterItem) {
                                Log.d("TTTT", "${child.getStartHint()}: ${child.getStartText()}  ${child.getEndHint()}: ${child.getEndText()}")
                            } else if (child is EditableFilterItem) {
                                Log.d("TTTT", "${child.getHint()}: ${child.getText()}")
                            }
                        }
                    }
                }
            }
        })
        return f
    }

    private lateinit var filterData: List<FilterItem>
    private lateinit var shortFilterData: ArrayList<FilterItem>
    private lateinit var rightFilterData: List<FilterItem>

    private fun getFilterData(): List<FilterItem> {
        val filterData = ArrayList<FilterItem>()

        val checkableList1 = SimpleFilterGroup("订单类型")
        checkableList1.add(SimpleCheckableFilterItem("网批订单"))
        checkableList1.add(SimpleCheckableFilterItem("标准订单"))
        checkableList1.add(SimpleCheckableFilterItem("非标准订单"))
        checkableList1.add(SimpleCheckableFilterItem("订货计划订单"))
        checkableList1.setSingleChoice(true)
        filterData.add(checkableList1)

        val checkableList2 = SimpleFilterGroup("订单状态")
        checkableList2.add(SimpleCheckableFilterItem("草稿"))
        checkableList2.add(SimpleCheckableFilterItem("待接单"))
        checkableList2.add(SimpleCheckableFilterItem("已驳回"))
        checkableList2.add(SimpleCheckableFilterItem("待发货"))
        checkableList2.add(SimpleCheckableFilterItem("部分发货"))
        checkableList2.add(SimpleCheckableFilterItem("全部发货"))
        checkableList2.add(SimpleCheckableFilterItem("待付款"))
        checkableList2.add(SimpleCheckableFilterItem("待收款确认"))
        checkableList2.add(SimpleCheckableFilterItem("交易关闭"))
        checkableList2.add(SimpleCheckableFilterItem("交易取消"))
        checkableList2.add(SimpleCheckableFilterItem("计划发货"))
        filterData.add(checkableList2)

        val date = SimpleFilterGroup("发货时间")
        date.add(SimpleDateFilterItem("选择时间"))
        filterData.add(date)

        val dateRange = SimpleFilterGroup("发货时间")
        dateRange.add(SimpleDateRangeFilterItem("起始时间", "截止时间"))
        filterData.add(dateRange)

        val editable = SimpleFilterGroup("发货单号")
        editable.add(object : SimpleEditableFilterItem("单号") {
            override fun onTextChanged(editable: Editable) {
                Log.d("TTTT", "单号 onTextChanged: $editable")
                if (editable.toString() == "100") {
                    editable.replace(0, editable.length, "新的单号")
                }
            }

            override fun getInputFilters(): Array<InputFilter> {
                return arrayOf(InputFilter.LengthFilter(6))
            }
        })
        filterData.add(editable)

        val editableRange = SimpleFilterGroup("发货数量")
        editableRange.add(object : SimpleEditableRangeFilterItem("最少", "最多") {
            override fun onStartTextChanged(editable: Editable) {
                Log.d("TTTT", "最少 onTextChanged: $editable")
                if (editable.toString() == "100") {
                    editable.replace(0, editable.length, "新的最少")
                }
            }

            override fun onEndTextChanged(editable: Editable) {
                Log.d("TTTT", "最多 onTextChanged: $editable")
                if (editable.toString() == "100") {
                    editable.replace(0, editable.length, "新的最多")
                }
            }

            override fun getStartInputFilters(): Array<InputFilter> {
                return arrayOf(InputFilter.LengthFilter(6))
            }

            override fun getEndInputFilters(): Array<InputFilter> {
                return arrayOf(InputFilter.LengthFilter(3))
            }
        })
        filterData.add(editableRange)

        val address = SimpleFilterGroup("发货地址")
        address.add(SimpleAddressFilterItem("选择发货地址"))
        filterData.add(address)

        val number = SimpleFilterGroup("发货数量")
        number.add(SimpleNumberFilterItem("选择发货数量", 1, 200))
        filterData.add(number)

        val numberRange = SimpleFilterGroup("发货数量")
        numberRange.add(SimpleNumberRangeFilterItem("最少数量", "最多数量", 1, 200))
        filterData.add(numberRange)

        return filterData
    }

}