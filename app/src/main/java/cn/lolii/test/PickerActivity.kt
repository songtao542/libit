package cn.lolii.test

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.lolii.picker.address.Address
import cn.lolii.picker.address.AddressPickerDialog
import cn.lolii.picker.datetime.DateTimePickerDialog
import cn.lolii.test.R
import com.lolii.filter.*
import kotlinx.android.synthetic.main.activity_picker.*
import java.util.*

class PickerActivity : AppCompatActivity() {

    private var mAddress: Address? = null

    private lateinit var mMinDate: Calendar
    private lateinit var mMaxDate: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        mMinDate = Calendar.getInstance()
        mMaxDate = Calendar.getInstance()

        mMinDate.set(2020, 9, 10)
        mMinDate.set(2030, 12, 30)

        datePicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(false)
                    .setTitle("选择日期")
                    .setMinDate(2020, 9, 10)
                    .setMaxDate(2030, 10, 10)
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ -> })
                    .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { _, _ -> })
                    .create()
            dialog.show()
        }
        timePicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ -> })
                    .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { _, _ -> })
                    .create()
            dialog.show()
        }
        time24Picker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .set24HourFormat(false)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ -> })
                    .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { _, _ -> })
                    .create()
            dialog.show()
        }
        dateTimePicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(true)
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ -> })
                    .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { _, _ -> })
                    .create()
            dialog.show()
        }
        lunarPicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(true)
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ -> })
                    .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { _, _ -> })
                    .create()
            dialog.show()
        }
        addressPicker.setOnClickListener {
            val dialog = AddressPickerDialog.Builder(this)
                    .setAutoUpdateTitle(true)
                    .setDefaultAddress(mAddress)
                    .setOnAddressChangedListener(object : AddressPickerDialog.OnAddressChangedListener {
                        override fun onAddressChanged(dialog: AddressPickerDialog, address: Address) {
                            Log.d("TTTT", "picked address: $address")
                            mAddress = address
                        }
                    })
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ -> })
                    .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { _, _ -> })
                    .create()
            dialog.show()
        }

        filter.setOnClickListener {
            val f = FilterDialogFragment()

            val filterData: ArrayList<FilterItem> = ArrayList()

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
            editable.add(SimpleEditableFilterItem("单号"))
            filterData.add(editable)

            val editableRange = SimpleFilterGroup("发货数量")
            editableRange.add(SimpleEditableRangeFilterItem("最少", "最多"))
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

            f.setFilter(filterData)
            f.setShowAsDialog(true)

            f.show(this)
        }
    }
}