package com.liabit.filter

interface FilterController : FilterLayout.OnCombinationResultListener, FilterLayout.OnResultListener {

    fun setup(filterLayout: FilterLayout)

    fun setOnResultListener(listener: FilterLayout.OnResultListener)

    fun setOnCombinationResultListener(listener: FilterLayout.OnCombinationResultListener)

    fun setLeftPageListPadding(left: Int, top: Int, right: Int, bottom: Int)

    fun setRightPageListPadding(left: Int, top: Int, right: Int, bottom: Int)

    fun setClickToReturnMode(leftPageClickToReturn: Boolean, rightPageClickToReturn: Boolean = false)

    fun setTab(leftPageTitle: String, rightPageTitle: String)

    fun setFilter(items: List<FilterItem>, configurator: FilterAdapter? = null)

    fun setLeftPageFilter(items: List<FilterItem>, configurator: FilterAdapter? = null)

    fun setRightPageFilter(items: List<FilterItem>, configurator: FilterAdapter? = null)

    fun setOnResetListener(listener: FilterLayout.OnResetListener)

    fun setOnConfirmListener(listener: FilterLayout.OnConfirmListener)

    fun setFilterPicker(picker: FilterPicker)

    fun getOnResultListener(): FilterLayout.OnResultListener?

    fun getOnCombinationResultListener(): FilterLayout.OnCombinationResultListener?

    fun getOnResetListener(): FilterLayout.OnResetListener?

    fun getOnConfirmListener(): FilterLayout.OnConfirmListener?

}