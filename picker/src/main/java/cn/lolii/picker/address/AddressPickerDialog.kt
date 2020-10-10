package cn.lolii.picker.address

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import cn.lolii.picker.R
import cn.lolii.picker.cascade.CascadePickerView

@Suppress("unused")
class AddressPickerDialog private constructor(private val mContext: Context,
                                              private val dialog: AlertDialog,
                                              private val cascadePickerView: CascadePickerView,
                                              private val addressData: List<Province>) {

    companion object {
        private const val TAG = "AddressPickerDialog"
    }

    private var mIsAutoUpdateTitle = true

    private var mAddressStr: String? = null
    private var mAddress: Address? = null

    private fun updateTitle(address: Address) {
        mAddressStr = address.formattedAddress
        updateTitle(mAddressStr)
    }

    private fun updateTitle(title: CharSequence?) {
        dialog.setTitle(title)
    }

    fun setAutoUpdateTitle(enable: Boolean) {
        mIsAutoUpdateTitle = enable
    }

    private fun setOnAddressChangeListener(listener: OnAddressChangeListener?) {
        cascadePickerView.setOnValueChangeListener(object : CascadePickerView.OnValueChangeListener {
            override fun onValueChange(value: CascadePickerView.Value) {
                try {
                    val province = addressData[value.index1]
                    val city = province.getChildren()[value.index2] as City
                    val district = city.getChildren()[value.index3] as District
                    val address = Address(province.code, province.name, city.code, city.name, district.code, district.name)
                    mAddress = address
                    if (mIsAutoUpdateTitle) {
                        updateTitle(address)
                    }
                    listener?.onAddressChanged(this@AddressPickerDialog, address)
                } catch (e: Throwable) {
                    Log.e(TAG, "error: ", e)
                }
            }
        })
    }

    interface OnAddressChangeListener {
        fun onAddressChanged(dialog: AddressPickerDialog, address: Address) {}
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    class Builder(private val mContext: Context, theme: Int = R.style.BottomSheetPickerDialog) {
        private val mBuilder: AlertDialog.Builder = AlertDialog.Builder(mContext, theme)

        private var mIsAutoUpdateTitle = true
        private var mOnAddressChangeListener: OnAddressChangeListener? = null
        private var mCanceledOnTouchOutside = true
        private var mGravity = Gravity.BOTTOM

        private var mDefaultAddress: Address? = null

        init {
            mBuilder.setTitle(mContext.getString(R.string.select_address)) //避免外部未设置时无法显示title
        }

        /**
         * 调用此方法后，将不默认自动更新title。如需自动，请再调用[.setAutoUpdateTitle]
         */
        fun setTitle(title: CharSequence?): Builder {
            mIsAutoUpdateTitle = false
            mBuilder.setTitle(title)
            return this
        }

        fun setAutoUpdateTitle(enable: Boolean): Builder {
            mIsAutoUpdateTitle = enable
            return this
        }

        fun setDefaultAddress(address: Address?): Builder {
            mDefaultAddress = address
            return this
        }

        fun setOnAddressChangeListener(listener: OnAddressChangeListener?): Builder {
            mOnAddressChangeListener = listener
            return this
        }

        fun create(): AddressPickerDialog {
            val contentView = View.inflate(mContext, R.layout.dialog_cascade_picker, null)
            val cascadePickerView: CascadePickerView = contentView.findViewById(R.id.cascade_picker_view)
            cascadePickerView.setTextSize(14f, 15f)
            val addressData: List<Province> = AddressProvider.getProvince(mContext)
            val index = resolveDefaultAddress(addressData)
            cascadePickerView.setData(addressData, index[0], index[1], index[2])
            mBuilder.setView(contentView)
            val dialog = mBuilder.create()
            val window = dialog.window
            window?.setGravity(mGravity)
            dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside)
            //先创建pickerDialog实例，后续设置数据回调onChange
            val pickerDialog = AddressPickerDialog(mContext, dialog, cascadePickerView, addressData)
            pickerDialog.setAutoUpdateTitle(mIsAutoUpdateTitle)
            pickerDialog.setOnAddressChangeListener(mOnAddressChangeListener)
            return pickerDialog
        }

        private fun resolveDefaultAddress(addressData: List<Province>): IntArray {
            val result = intArrayOf(0, 0, 0)
            val address = mDefaultAddress ?: return result
            for (i in addressData.indices) {
                val province = addressData[i]
                if (province.code == address.provinceCode) {
                    result[0] = i
                    val cities = province.cities
                    for (j in cities.indices) {
                        val city = cities[j]
                        if (city.code == address.cityCode) {
                            result[1] = j
                            val districts = city.districts
                            for (k in districts.indices) {
                                val district = districts[k]
                                if (district.code == address.districtCode) {
                                    result[2] = k
                                    break
                                }
                            }
                            break
                        }
                    }
                    break
                }
            }
            return result
        }

        fun show(): AddressPickerDialog {
            return create().also { it.dialog.show() }
        }

        fun setCanceledOnTouchOutside(enable: Boolean): Builder {
            mCanceledOnTouchOutside = enable
            return this
        }

        fun setCancelable(enable: Boolean): Builder {
            mBuilder.setCancelable(enable)
            return this
        }

        fun setPositiveButton(textResId: Int, listener: DialogInterface.OnClickListener): Builder {
            mBuilder.setPositiveButton(textResId, listener)
            return this
        }

        fun setNegativeButton(textResId: Int, listener: DialogInterface.OnClickListener): Builder {
            mBuilder.setNegativeButton(textResId, listener)
            return this
        }

        fun setPositiveButton(text: CharSequence, listener: DialogInterface.OnClickListener): Builder {
            mBuilder.setPositiveButton(text, listener)
            return this
        }

        fun setNegativeButton(text: CharSequence, listener: DialogInterface.OnClickListener): Builder {
            mBuilder.setNegativeButton(text, listener)
            return this
        }

        fun setGravity(gravity: Int): Builder {
            mGravity = gravity
            return this
        }
    }
}