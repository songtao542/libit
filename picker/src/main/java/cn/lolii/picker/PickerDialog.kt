package cn.lolii.picker

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import kotlin.math.abs

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
class PickerDialog private constructor(private val mContext: Context,
                                       private val dialog: AlertDialog,
                                       private val pickerView: NumberPickerView,
                                       private val data: List<DisplayValue>) {

    companion object {
        private const val TAG = "PickerDialog"
    }

    private var mIsAutoUpdateTitle = false

    private var mAddressStr: String? = null
    private var mValueIndex: Int? = null
    private var mValue: DisplayValue? = null

    private fun updateTitle(title: CharSequence?) {
        dialog.setTitle(title)
    }

    fun setAutoUpdateTitle(enable: Boolean) {
        mIsAutoUpdateTitle = enable
    }

    private fun setOnValueChangedListener(listener: OnValueChangeListener?) {
        pickerView.setOnValueChangedListener { _, _, newVal ->
            notifyListener(newVal, listener)
        }
        notifyListener(pickerView.value, listener)
    }

    private fun notifyListener(index: Int, listener: OnValueChangeListener?) {
        mValueIndex = index
        mValue = data[index]
        if (mIsAutoUpdateTitle) {
            updateTitle(mValue?.getDisplayValue())
        }
        listener?.onValueChanged(this, index, data[index])
    }

    interface OnValueChangeListener {
        fun onValueChanged(dialog: PickerDialog, index: Int, value: DisplayValue) {}
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
        fun onAction(dialog: DialogInterface, which: Int, index: Int, value: DisplayValue) {}
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    class Builder(private val mContext: Context, theme: Int = R.style.PickerDialog) {
        private val mBuilder: AlertDialog.Builder = AlertDialog.Builder(mContext, theme)

        private var mIsAutoUpdateTitle = false
        private var mOnValueChangeListener: OnValueChangeListener? = null
        private var mCanceledOnTouchOutside = true
        private var mGravity = Gravity.BOTTOM

        private var mActionListener: OnActionListener? = null
        private var mPositiveClickListener: DialogInterface.OnClickListener? = null
        private var mNegativeClickListener: DialogInterface.OnClickListener? = null
        private var mPositiveText: CharSequence? = null
        private var mNegativeText: CharSequence? = null

        private var mDisplayValues: List<DisplayValue> = emptyList()
        private var mDefaultValue: DisplayValue? = null

        private var mCycle = true

        init {
            mBuilder.setTitle(mContext.getString(R.string.please_select))
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

        fun setDefaultValue(value: DisplayValue?): Builder {
            mDefaultValue = value
            return this
        }

        fun setDefaultValue(value: Int?): Builder {
            mDefaultValue = if (value != null) NumberDisplayValue(value) else null
            return this
        }

        fun setOnValueChangedListener(listener: OnValueChangeListener?): Builder {
            mOnValueChangeListener = listener
            return this
        }

        fun create(): PickerDialog {
            val contentView = View.inflate(mContext, R.layout.dialog_picker, null)
            val pickerView: NumberPickerView = contentView.findViewById(R.id.picker_view)

            pickerView.displayedValues = resolveDisplayValues()
            pickerView.minValue = 0
            pickerView.maxValue = mDisplayValues.size - 1
            pickerView.value = resolveCurrentValue()

            pickerView.wrapSelectorWheel = mCycle

            var wrapPositiveListener: WrapDialogOnClickListener? = null
            var wrapNegativeListener: WrapDialogOnClickListener? = null

            val actionListener = mActionListener
            val positiveClickListener = mPositiveClickListener
            val positiveText = mPositiveText ?: mContext.getText(R.string.dialog_confirm)
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
            val negativeText = mNegativeText ?: mContext.getText(R.string.dialog_cancel)
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
            val pickerDialog = PickerDialog(mContext, dialog, pickerView, mDisplayValues)
            pickerDialog.setAutoUpdateTitle(mIsAutoUpdateTitle)
            pickerDialog.setOnValueChangedListener(mOnValueChangeListener)

            wrapPositiveListener?.setPickerDialog(pickerDialog)
            wrapNegativeListener?.setPickerDialog(pickerDialog)

            return pickerDialog
        }

        private fun resolveCurrentValue(): Int {
            val current = mDefaultValue ?: return 0
            for (i in mDisplayValues.indices) {
                if (current == mDisplayValues[i]) {
                    return i
                }
            }
            return 0
        }

        private fun resolveDisplayValues(): List<CharSequence> {
            val result = ArrayList<CharSequence>(mDisplayValues.size)
            for (v in mDisplayValues) {
                result.add(v.getDisplayValue())
            }
            return result
        }

        fun show(): PickerDialog {
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

        fun setDisplayValues(values: List<DisplayValue>): Builder {
            mDisplayValues = values
            return this
        }

        fun setDisplayValues(from: Int, to: Int): Builder {
            val values = ArrayList<DisplayValue>(abs(to - from) + 1)
            for (i in from..to) {
                values.add(NumberDisplayValue(i))
            }
            mDisplayValues = values
            return this
        }

        fun setCycle(cycle: Boolean): Builder {
            mCycle = cycle
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

            private var mPickerDialog: PickerDialog? = null
            private var mWrappedListener: DialogInterface.OnClickListener? = null

            constructor(actionListener: OnActionListener, listener: DialogInterface.OnClickListener) : this(actionListener) {
                mWrappedListener = listener
            }

            fun setPickerDialog(pickerDialog: PickerDialog) {
                mPickerDialog = pickerDialog
            }

            override fun onClick(dialog: DialogInterface, which: Int) {
                mWrappedListener?.onClick(dialog, which)
                val index = mPickerDialog?.mValueIndex
                val value = mPickerDialog?.mValue
                if (index != null && value != null) {
                    actionListener.onAction(dialog, which, index, value)
                }
            }
        }
    }

    class NumberDisplayValue(private val value: Int) : DisplayValue {
        override fun getDisplayValue(): CharSequence {
            return "$value"
        }

        override fun equals(other: Any?): Boolean {
            if (other is NumberDisplayValue) {
                return other.value == value
            }
            return false
        }

        override fun hashCode(): Int {
            return value
        }
    }

    interface DisplayValue {
        fun getDisplayValue(): CharSequence
    }
}