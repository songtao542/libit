package cn.lolii.test

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.lolii.picker.address.Address
import cn.lolii.picker.address.AddressPickerDialog
import cn.lolii.picker.datetime.DateTimePickerDialog
import cn.lolii.test.test.R
import com.lolii.filter.FilterDialogFragment
import com.lolii.filter.FilterItem
import com.lolii.filter.SimpleCheckableFilterItem
import com.lolii.filter.SimpleFilterGroup
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
                    .setOnAddressChangeListener(object : AddressPickerDialog.OnAddressChangeListener {
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
            val orderTypeGroup = SimpleFilterGroup("订单类型")
            orderTypeGroup.add(SimpleCheckableFilterItem("网批订单"))
            orderTypeGroup.add(SimpleCheckableFilterItem("标准订单"))
            orderTypeGroup.add(SimpleCheckableFilterItem("非标准订单"))
            orderTypeGroup.add(SimpleCheckableFilterItem("订货计划订单"))
            orderTypeGroup.setSingleChoice(true)
            filterData.add(orderTypeGroup)

            val orderStatusGroup = SimpleFilterGroup("订单状态")
            orderStatusGroup.add(SimpleCheckableFilterItem("草稿"))
            orderStatusGroup.add(SimpleCheckableFilterItem("待接单"))
            orderStatusGroup.add(SimpleCheckableFilterItem("已驳回"))
            orderStatusGroup.add(SimpleCheckableFilterItem("待发货"))
            orderStatusGroup.add(SimpleCheckableFilterItem("部分发货"))
            orderStatusGroup.add(SimpleCheckableFilterItem("全部发货"))
            orderStatusGroup.add(SimpleCheckableFilterItem("待付款"))
            orderStatusGroup.add(SimpleCheckableFilterItem("待收款确认"))
            orderStatusGroup.add(SimpleCheckableFilterItem("交易关闭"))
            orderStatusGroup.add(SimpleCheckableFilterItem("交易取消"))
            orderStatusGroup.add(SimpleCheckableFilterItem("计划发货"))
            orderStatusGroup.setSingleChoice(true)
            filterData.add(orderStatusGroup)

            val paymentStatusGroup = SimpleFilterGroup("付款状态")
            paymentStatusGroup.add(SimpleCheckableFilterItem("待付款"))
            paymentStatusGroup.add(SimpleCheckableFilterItem("已收讫"))
            paymentStatusGroup.add(SimpleCheckableFilterItem("部分付款"))
            paymentStatusGroup.setSingleChoice(true)
            filterData.add(paymentStatusGroup)

            val placeTime = SimpleFilterGroup("下单时间")
            placeTime.add(SimpleCheckableFilterItem("近三个月订单"))
            placeTime.add(SimpleCheckableFilterItem("今年内订单"))
            placeTime.add(SimpleCheckableFilterItem("2019年订单"))
            placeTime.add(SimpleCheckableFilterItem("2018年订单"))
            placeTime.add(SimpleCheckableFilterItem("2017年订单"))
            placeTime.add(SimpleCheckableFilterItem("2016年订单"))
            placeTime.add(SimpleCheckableFilterItem("2015年订单"))
            placeTime.add(SimpleCheckableFilterItem("2014年订单"))
            placeTime.add(SimpleCheckableFilterItem("2014年以前订单"))
            placeTime.setSingleChoice(true)
            filterData.add(placeTime)

            f.setFilter(filterData)
            f.setShowAsDialog(true)

            f.show(this)
        }
    }
}