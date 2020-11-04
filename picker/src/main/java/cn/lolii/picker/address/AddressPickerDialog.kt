package cn.lolii.picker.address

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import cn.lolii.picker.R
import cn.lolii.picker.cascade.CascadePickerView

/**
 * 修改标题和底部Button样式
 * <style name="YourActivityOrDialogTheme" parent="Theme.AppCompat.Light">
 *     <!-- dialog 样式 -->
 *     <item name="android:buttonBarNegativeButtonStyle">@style/DialogButtonStyle</item>
 *     <item name="android:buttonBarPositiveButtonStyle">@style/DialogButtonStyle</item>
 *     <item name="android:windowTitleStyle">@style/DialogTitleStyle</item>
 * </style>
 *
 * <style name="DialogTitleStyle" parent="TextAppearance.AppCompat.Title">
 *     <item name="android:textColor">#007cee</item>
 * </style>

 * <style name="DialogButtonStyle" parent="Widget.AppCompat.Button.ButtonBar.AlertDialog">
 *     <item name="android:textColor">#007cee</item>
 * </style>
 */
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

    private fun setOnAddressChangedListener(listener: OnAddressChangedListener?) {
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

    interface OnAddressChangedListener {
        fun onAddressChanged(dialog: AddressPickerDialog, address: Address) {}
    }

    interface OnActionListener {
        /**
         * This method will be invoked when a button in the dialog is clicked.
         *
         * @param dialog the dialog that received the click
         * @param which the button that was clicked (ex.
         *              {@link DialogInterface#BUTTON_POSITIVE}) or the position
         *              of the item clicked
         */
        fun onAction(dialog: DialogInterface, which: Int, address: Address) {}
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
        private var mOnAddressChangeListener: OnAddressChangedListener? = null
        private var mCanceledOnTouchOutside = true
        private var mGravity = Gravity.BOTTOM

        private var mActionListener: OnActionListener? = null
        private var mPositiveClickListener: DialogInterface.OnClickListener? = null
        private var mNegativeClickListener: DialogInterface.OnClickListener? = null
        private var mPositiveText: CharSequence? = null
        private var mNegativeText: CharSequence? = null

        private var mDefaultAddress: Address? = null

        init {
            mBuilder.setTitle(mContext.getString(R.string.picker_select_address)) //避免外部未设置时无法显示title
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

        fun setOnAddressChangedListener(listener: OnAddressChangedListener?): Builder {
            mOnAddressChangeListener = listener
            return this
        }

        fun create(): AddressPickerDialog {
            val contentView = View.inflate(mContext, R.layout.picker_dialog_cascade, null)
            val cascadePickerView: CascadePickerView = contentView.findViewById(R.id.cascade_picker_view)
            cascadePickerView.setTextSize(14f, 15f)
            val addressData: List<Province> = AddressProvider.getProvince(mContext)
            val index = resolveDefaultAddress(addressData)
            cascadePickerView.setData(addressData, index[0], index[1], index[2])

            var wrapPositiveListener: WrapDialogOnClickListener? = null
            var wrapNegativeListener: WrapDialogOnClickListener? = null

            val actionListener = mActionListener
            val positiveClickListener = mPositiveClickListener
            val positiveText = mPositiveText ?: mContext.getText(R.string.picker_dialog_confirm)
            if (actionListener != null) {
                wrapPositiveListener = if (positiveClickListener == null) {
                    WrapDialogOnClickListener(actionListener)
                } else {
                    WrapDialogOnClickListener(actionListener, positiveClickListener)
                }
                mBuilder.setPositiveButton(positiveText, wrapPositiveListener)
            } else if (positiveClickListener != null) {
                mBuilder.setPositiveButton(positiveText, positiveClickListener)
            }

            val negativeClickListener = mNegativeClickListener
            val negativeText = mNegativeText ?: mContext.getText(R.string.picker_dialog_cancel)
            if (actionListener != null) {
                wrapNegativeListener = if (negativeClickListener == null) {
                    WrapDialogOnClickListener(actionListener)
                } else {
                    WrapDialogOnClickListener(actionListener, negativeClickListener)
                }
                mBuilder.setNegativeButton(negativeText, wrapNegativeListener)
            } else if (positiveClickListener != null) {
                mBuilder.setNegativeButton(negativeText, negativeClickListener)
            }

            mBuilder.setView(contentView)
            val dialog = mBuilder.create()
            val window = dialog.window
            window?.setGravity(mGravity)
            dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside)
            //先创建pickerDialog实例，后续设置数据回调onChange
            val pickerDialog = AddressPickerDialog(mContext, dialog, cascadePickerView, addressData)
            pickerDialog.setAutoUpdateTitle(mIsAutoUpdateTitle)
            pickerDialog.setOnAddressChangedListener(mOnAddressChangeListener)

            wrapPositiveListener?.setPickerDialog(pickerDialog)
            wrapNegativeListener?.setPickerDialog(pickerDialog)

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
            mPositiveClickListener = listener
            mPositiveText = mContext.getText(textResId)
            return this
        }

        fun setPositiveButton(text: CharSequence, listener: DialogInterface.OnClickListener): Builder {
            mPositiveClickListener = listener
            mPositiveText = text
            return this
        }

        fun setNegativeButton(textResId: Int, listener: DialogInterface.OnClickListener): Builder {
            mNegativeClickListener = listener
            mNegativeText = mContext.getText(textResId)
            return this
        }

        fun setNegativeButton(text: CharSequence, listener: DialogInterface.OnClickListener): Builder {
            mNegativeClickListener = listener
            mNegativeText = text
            return this
        }

        fun setPositiveButtonText(textResId: Int): Builder {
            mPositiveText = mContext.getText(textResId)
            return this
        }

        fun setPositiveButtonText(text: CharSequence): Builder {
            mPositiveText = text
            return this
        }

        fun setNegativeButtonText(textResId: Int): Builder {
            mNegativeText = mContext.getText(textResId)
            return this
        }

        fun setNegativeButtonText(text: CharSequence): Builder {
            mNegativeText = text
            return this
        }

        fun setActionListener(listener: OnActionListener): Builder {
            mActionListener = listener
            return this
        }

        fun setGravity(gravity: Int): Builder {
            mGravity = gravity
            return this
        }

        private class WrapDialogOnClickListener(private val actionListener: OnActionListener) : DialogInterface.OnClickListener {

            private var mPickerDialog: AddressPickerDialog? = null
            private var mWrappedListener: DialogInterface.OnClickListener? = null

            constructor(actionListener: OnActionListener, listener: DialogInterface.OnClickListener) : this(actionListener) {
                mWrappedListener = listener
            }

            fun setPickerDialog(pickerDialog: AddressPickerDialog) {
                mPickerDialog = pickerDialog
            }

            override fun onClick(dialog: DialogInterface, which: Int) {
                mWrappedListener?.onClick(dialog, which)
                mPickerDialog?.mAddress?.let {
                    actionListener.onAction(dialog, which, it)
                }
            }
        }
    }
}