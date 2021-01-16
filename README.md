# libit
这是一个Android UI 组件及一些工具的集合。(未发布到jcenter或maven，有需要的直接下载代码吧！！！)<br/>
日期时间选择器<br/>

| 时间选择-12小时制                 | 时间选择-24小时制                   | 日期选择                       | 日期时间选择                       | 省市区选择                       |
|:------------------------------:|:---------------------------------:|:--------------------------------:|:--------------------------------:|:--------------------------------:|
|<img src="https://github.com/songtao542/libit/blob/master/screenshot/%E6%97%B6%E9%97%B4%E9%80%89%E6%8B%A912H%E5%88%B6.png" width="160"/> | <img src="https://github.com/songtao542/libit/blob/master/screenshot/%E6%97%B6%E9%97%B4%E9%80%89%E6%8B%A924H%E5%88%B6.png" width="160"/> | <img src="https://github.com/songtao542/libit/blob/master/screenshot/%E6%97%A5%E6%9C%9F%E9%80%89%E6%8B%A9.png" width="160"/>|<img src="https://github.com/songtao542/libit/blob/master/screenshot/%E6%97%A5%E6%9C%9F%E6%97%B6%E9%97%B4%E9%80%89%E6%8B%A9.png" width="160"/>|<img src="https://github.com/songtao542/libit/blob/master/screenshot/%E7%9C%81%E5%B8%82%E5%8C%BA%E9%80%89%E6%8B%A9.png" width="160"/>|

```kotlin
// 日期时间选择
val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)  // 是否选择时间
                    .set24HourFormat(false) // 是否24小时制
                    .setTitle("选择时间")    // dialog 标题
                    .setMinDateTime(2008, 9, 12, 8, 23)  //设置日期时间下限值
                    .setMaxDateTime(2030, 10, 20, 20, 40)  //设置日期时间上限值
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnTimeChangedListener(object : DateTimePickerDialog.OnTimeChangeListener {
                        override fun onTimeChanged(dialog: DateTimePickerDialog, time: Time) {
                            binding.dateTimeText.text = mTimeFormat.format(time.calendar.time)
                        }
                    })
                    .create()
            dialog.show()

// 省市区选择
val dialog = AddressPickerDialog.Builder(this)
                    .setAutoUpdateTitle(true)
                    .setDefaultAddress(mAddress)  //dialog show 时显示的地址
                    .setOnAddressChangedListener(object : AddressPickerDialog.OnAddressChangedListener {
                        override fun onAddressChanged(dialog: AddressPickerDialog, address: Address) {
                            mAddress = address
                            binding.addressText.text = address.formatted
                        }
                    })
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .create()
            dialog.show()
```

</br>
筛选</br>

| 筛选1                  | 筛选2                    | 筛选3                          |
|:------------------------------:|:---------------------------------:|:--------------------------------:|
|<img src="https://github.com/songtao542/libit/blob/master/screenshot/%E7%AD%9B%E9%80%891.png" width="160"/>| <img src="https://github.com/songtao542/libit/blob/master/screenshot/%E7%AD%9B%E9%80%892.png" width="160"/> | <img src="https://github.com/songtao542/libit/blob/master/screenshot/%E7%AD%9B%E9%80%893.png" width="160"/>|

```kotlin
val filterData = ArrayList<FilterItem>()

val checkableList1 = SimpleFilterGroup("服务/折扣")
checkableList1.add(SimpleCheckableFilterItem("极速物流"))
checkableList1.add(SimpleCheckableFilterItem("货到付款"))
checkableList1.add(SimpleCheckableFilterItem("新品"))
checkableList1.add(SimpleCheckableFilterItem("分期免息"))
checkableList1.setSingleChoice(false)
filterData.add(checkableList1)
val checkableList2 = SimpleFilterGroup("处理器")
checkableList2.add(SimpleCheckableFilterItem("Intel i3"))
checkableList2.add(SimpleCheckableFilterItem("Intel i5"))
checkableList2.add(SimpleCheckableFilterItem("Intel i7"))
checkableList2.add(SimpleCheckableFilterItem("Intel i9"))
checkableList2.add(SimpleCheckableFilterItem("AMD"))
checkableList2.add(SimpleCheckableFilterItem("Intel 至强"))
checkableList2.add(SimpleCheckableFilterItem("Intel 奔腾"))
checkableList2.add(SimpleCheckableFilterItem("Ryzen锐龙"))
filterData.add(checkableList2)
val date = SimpleFilterGroup("发布时间")
date.add(SimpleDateFilterItem("选择时间"))
filterData.add(date)
val dateRange = SimpleFilterGroup("可送货时间")
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
val f = FilterDialogFragment(FilterPicker.instance)
f.setFilter(filterData)
if (twoColumn) {
    f.setRightPageFilter(rightFilterData)
    f.setTab("按数量", "按质量")
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
f.setShowAsDialog(true)
f.show(this)
```

</br>
选择</br>

| 城市选择-单选                    | 城市选择-多    选                   | 其他选择                          |
|:------------------------------:|:---------------------------------:|:--------------------------------:|
|<img src="https://github.com/songtao542/libit/blob/master/screenshot/%E5%9F%8E%E5%B8%82%E9%80%89%E6%8B%A9.png" width="160"/>| <img src="https://github.com/songtao542/libit/blob/master/screenshot/%E5%9F%8E%E5%B8%82%E5%A4%9A%E9%80%89.png" width="160"/> | <img src="https://github.com/songtao542/libit/blob/master/screenshot/%E9%80%89%E6%8B%A9.png" width="160"/>|

```kotlin
// 选择城市
CityPickerFragment.Builder()
        .setFragmentManager(activity?.supportFragmentManager)
        .setMultipleMode(multipleMode)
        .setDefaultHotCitiesEnabled(true)
        .setLocatedCityEnable(true)
        .setUseDefaultCities(true)
        .setOnResultListener(object : OnResultListener<City> {
            override fun onResult(data: List<City>) {
                handler.invoke(data)
            }
       })
       .show()

// 选择
PickerFragment.Builder<>()
        .setFragmentManager(supportFragmentManager)
        .setAnimationStyle(R.style.DefaultListPickerAnimation)
        .setMultipleMode(false)
        .setSectionEnabled(false)
        .setSearchHint("学校名称")
        .setItem(MutableList(40) {
            (Mock.schoolName())
        })
        .setOnResultListener(object : OnResultListener<> {
            override fun onResult(data: List<>) {
                .makeText(this@TestCityPickerActivity, data.toString(), .LENGTH_SHORT).show()
            }
        })
        .show()
```

</br>
倒计时</br>

| 倒计时                    |
|:------------------------------:|
|<img src="https://github.com/songtao542/libit/blob/master/screenshot/%E5%80%92%E8%AE%A1%E6%97%B6.png" width="160"/>|

</br>
RecyclerView列表嵌套(仿京东主页效果)</br>

| RecyclerView列表嵌套(仿京东主页效果)                 |
|:------------------------------:|
|<img src="https://github.com/songtao542/libit/blob/master/screenshot/%E4%BB%BF%E4%BA%AC%E4%B8%9C%E4%B8%BB%E9%A1%B5.gif" width="160"/>|


</br>
TagView</br>

| TagView                   |
|:------------------------------:|
|<img src="https://github.com/songtao542/libit/blob/master/screenshot/TagView.png" width="160"/>|

```kotlin
    <com.liabit.tagview.TagView
        android:id="@+id/tagView4"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:gravity="left"
        android:paddingHorizontal="30dp"
        android:textColor="#ffffffff"
        app:tagColor="#ff03A9F4"
        app:tagPaddingHorizontal="15dp"
        app:tagRadius="50dp"
        app:tags="@array/words1" />
```

</br>
RecyclerView分割线</br>

| RecyclerView分割线                   | RecyclerView分割线                   |
|:------------------------------:|:------------------------------:|
|<img src="https://github.com/songtao542/libit/blob/master/screenshot/RecyclerView%E5%88%86%E5%89%B2%E7%BA%BF.png" width="160"/>|<img src="https://github.com/songtao542/libit/blob/master/screenshot/RecyclerView%E5%88%86%E5%89%B2%E7%BA%BF1.png" width="160"/>|

```kotlin
val space = 10.dp(this)
recyclerView.addItemDecoration(SpaceDecoration(space,
        SpaceDecoration.ALL or SpaceDecoration.IGNORE_CROSS_AXIS_START, 3f).apply {
    setDrawable(ColorDrawable(0xff888888.toInt()))
})
```

