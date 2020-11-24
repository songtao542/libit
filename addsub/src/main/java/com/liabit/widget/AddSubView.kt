package com.liabit.widget

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import com.liabit.addsub.R

/**
 * Author:         songtao
 * CreateDate:     2020/9/18 14:54
 */

@Suppress("unused", "MemberVisibilityCanBePrivate")
class AddSubView : RelativeLayout, TextWatcher {

    companion object {
        const val TAG = "AddSubView"
    }

    private var mNotifyChangeWhenActionDone: Boolean = false
    private var mAddButton: ImageView? = null
    private var mSubButton: ImageView? = null
    private var mNumEditor: EditText? = null
    private val mEditorRect = Rect()
    private var mEditorMaskView: View? = null
    private var mMin: Int? = null
    private var mMax: Int? = null
    private var mValue: Int = 0
    private var mOnValueChangedListener: ((view: AddSubView, value: Int, causeByEdit: Boolean) -> Unit)? = null
    private var mOnValueOutOfRangeListener: ((view: AddSubView, value: Int) -> Unit)? = null
    private var mOnEmptyListener: ((view: AddSubView) -> Unit)? = null
    private var mValueChangedListener: OnValueChangedListener? = null

    private var mValueOutOfRangeListener: OnValueOutOfRangeListener? = null
    private var mEmptyListener: OnEmptyListener? = null
    private var mTextClickListener: OnClickListener? = null
    private var mShowEditDialog = false

    private var mImm: InputMethodManager? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        LayoutInflater.from(context).inflate(R.layout.add_sub_view, this, true)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            focusable = View.FOCUSABLE
        }
        isFocusableInTouchMode = true
        mImm = getSystemService(context, InputMethodManager::class.java)

        var addIcon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_num_add, context.theme)
        var subIcon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_num_sub, context.theme)
        var editable = true
        var editTextBackground: Drawable? = null
        var editTextWidth = 0f
        var editTextHeight = 0f
        var iconSize = 0f
        var iconPadding = 0f
        var textColor: ColorStateList? = null
        var textColorHint: ColorStateList? = null

        var value = 0

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AddSubView, defStyleAttr, defStyleRes)
            mShowEditDialog = typedArray.getBoolean(R.styleable.AddSubView_showEditDialog, false)
            addIcon = typedArray.getDrawable(R.styleable.AddSubView_addIcon) ?: addIcon
            subIcon = typedArray.getDrawable(R.styleable.AddSubView_subIcon) ?: subIcon
            editable = if (mShowEditDialog) false else typedArray.getBoolean(R.styleable.AddSubView_editable, true)
            editTextBackground = typedArray.getDrawable(R.styleable.AddSubView_editBackground)
            editTextWidth = typedArray.getDimension(R.styleable.AddSubView_editWidth, 0f)
            editTextHeight = typedArray.getDimension(R.styleable.AddSubView_editHeight, 0f)
            textColor = typedArray.getColorStateList(R.styleable.AddSubView_android_textColor)
            textColorHint = typedArray.getColorStateList(R.styleable.AddSubView_android_textColorHint)
            iconSize = typedArray.getDimension(R.styleable.AddSubView_iconSize, 0f)
            iconPadding = typedArray.getDimension(R.styleable.AddSubView_iconPadding, 0f)
            val iconColorList = typedArray.getColorStateList(R.styleable.AddSubView_iconColor)
            if (iconColorList != null) {
                addIcon?.setTintList(iconColorList)
                subIcon?.setTintList(iconColorList)
            }
            if (typedArray.hasValue(R.styleable.AddSubView_minValue)) {
                mMin = typedArray.getInt(R.styleable.AddSubView_minValue, 0)
            }
            if (typedArray.hasValue(R.styleable.AddSubView_maxValue)) {
                mMax = typedArray.getInt(R.styleable.AddSubView_maxValue, 0)
            }
            if (typedArray.hasValue(R.styleable.AddSubView_value)) {
                value = typedArray.getInt(R.styleable.AddSubView_value, 0)
            }
            typedArray.recycle()
        }

        mAddButton = findViewById(R.id.addButton)
        mSubButton = findViewById(R.id.subButton)
        mNumEditor = findViewById(R.id.numEditor)
        mEditorMaskView = findViewById(R.id.editorMaskView)

        mAddButton?.setImageDrawable(addIcon)
        mSubButton?.setImageDrawable(subIcon)

        setupEditorFilter(mMax ?: Int.MAX_VALUE)

        mNumEditor?.setText(value.toString())
        setValueInner(value)
        mNumEditor?.isEnabled = editable
        editTextBackground?.let {
            mNumEditor?.setBackground(it)
        }

        mNumEditor?.let {
            var lp = it.layoutParams
            if (lp != null) {
                if (editTextWidth > 0) {
                    lp.width = editTextWidth.toInt()
                }
                if (editTextHeight > 0) {
                    lp.height = editTextHeight.toInt()
                }
            } else {
                val defaultWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, context.resources.displayMetrics).toInt()
                val w: Int = if (editTextWidth > 0) editTextWidth.toInt() else defaultWidth
                val h: Int = if (editTextHeight > 0) editTextHeight.toInt() else defaultWidth / 5 * 3
                lp = LayoutParams(w, h).apply {
                    addRule(CENTER_VERTICAL, 1)
                }
            }
            it.layoutParams = lp
        }
        if (iconSize > 0) {
            val size = iconSize.toInt()
            var alp = mAddButton?.layoutParams
            if (alp != null) {
                alp.width = size
                alp.height = size
            } else {
                alp = LayoutParams(size, size).apply {
                    addRule(CENTER_VERTICAL, 1)
                }
            }
            mAddButton?.layoutParams = alp
            var slp = mSubButton?.layoutParams
            if (slp != null) {
                slp.width = size
                slp.height = size
            } else {
                slp = LayoutParams(size, size).apply {
                    addRule(CENTER_VERTICAL, 1)
                }
            }
            mSubButton?.layoutParams = slp
        }
        if (iconPadding > 0) {
            val padding = iconPadding.toInt()
            mAddButton?.setPadding(padding, padding, padding, padding)
            mSubButton?.setPadding(padding, padding, padding, padding)
        }

        mNumEditor?.let {
            if (textColor != null) {
                it.setTextColor(textColor)
            }
            if (textColorHint != null) {
                it.setHintTextColor(textColorHint)
            }
        }

        mAddButton?.setOnClickListener {
            mMax?.let {
                if (mValue < it) {
                    setValue(mValue + 1, false)
                    mOnValueChangedListener?.invoke(this, mValue, false)
                    mValueChangedListener?.onValueChanged(this, mValue, edited = false)
                }
            }
        }
        mSubButton?.setOnClickListener {
            mMin?.let {
                if (mValue > it) {
                    setValue(mValue - 1, false)
                    mOnValueChangedListener?.invoke(this, mValue, false)
                    mValueChangedListener?.onValueChanged(this, mValue, edited = false)
                }
            }
        }
        mNumEditor?.addTextChangedListener(this)
        mNumEditor?.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = v.text ?: return@setOnEditorActionListener true
                setAndCheckValue(text.toString(), updateTextView = false, actionDone = true)
                hideSoftInputFromWindow(v)
            }
            return@setOnEditorActionListener true
        }

        configEditDialog()
    }

    private fun configEditDialog() {
        if (mShowEditDialog) {
            mNumEditor?.isClickable = false
            mEditorMaskView?.setOnClickListener {
                showInputDialog()
            }
        }
    }

    private fun hideSoftInputFromWindow(v: View) {
        val view = (context as? Activity)?.currentFocus ?: v
        mImm?.hideSoftInputFromWindow(view.windowToken, 0)
        v.clearFocus()
        view.clearFocus()
        requestFocus()
    }

    fun showInputDialog(): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.add_sub_edit_dialog, null)
        val editText = view.findViewById<EditText>(R.id.editText)
        val dialog = AlertDialog.Builder(context, R.style.AddAndSubEditDialog)
                .setView(view)
                .setNegativeButton(R.string.add_sub_dialog_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.add_sub_dialog_confirm) { dialog, _ ->
                    dialog.dismiss()
                    editText?.text?.let {
                        setAndCheckValue(it.toString(), updateTextView = true, actionDone = true)
                    }
                }
                .create()
        editText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dialog.dismiss()
                val text = v.text ?: return@setOnEditorActionListener true
                setAndCheckValue(text.toString(), updateTextView = true, actionDone = true)
                hideSoftInputFromWindow(v)
            }
            return@setOnEditorActionListener true
        }
        dialog.show()
        editText.requestFocus()
        dialog.window?.let {
            it.setLayout((context.resources.displayMetrics.widthPixels / 4f * 3f).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            it.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        return dialog
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val text = s?.toString() ?: return
        setAndCheckValue(text, updateTextView = false, actionDone = false)
    }

    private fun setAndCheckValue(text: String, updateTextView: Boolean, actionDone: Boolean) {
        if (text.isNotBlank() && TextUtils.isDigitsOnly(text)) {
            // 最大输入长度限制为 Int.MAX_VALUE 的长度，即最多输入10个字符，所以这里转换成 Long 类型一定不会出错
            val num = text.toLong()
            var number = if (num > Int.MAX_VALUE) Int.MAX_VALUE else num.toInt()
            val min = mMin
            val max = mMax
            if (min != null && number < min) {
                if (mValueOutOfRangeListener != null || mValueOutOfRangeListener != null) {
                    mOnValueOutOfRangeListener?.invoke(this, number)
                    mValueOutOfRangeListener?.onValueOutOfRange(this, number)
                } else {
                    if (number < min) number = min
                    updateTextWithoutNotify(number.toString())
                }
                return
            } else if (max != null && number > max) {
                if (mValueOutOfRangeListener != null || mValueOutOfRangeListener != null) {
                    mOnValueOutOfRangeListener?.invoke(this, number)
                    mValueOutOfRangeListener?.onValueOutOfRange(this, number)
                } else {
                    if (number > max) number = max
                    updateTextWithoutNotify(number.toString())
                }
                return
            }

            if (num > Int.MAX_VALUE) {
                updateTextWithoutNotify(number.toString())
            }

            var notifyValueChange = if (mNotifyChangeWhenActionDone) actionDone else false
            if (number != mValue) {
                setValueInner(number)
                if (updateTextView) {
                    updateTextWithoutNotify(number.toString())
                }
                if (!mNotifyChangeWhenActionDone) {
                    notifyValueChange = true
                }
            }
            if (notifyValueChange) {
                mOnValueChangedListener?.invoke(this, mValue, true)
                mValueChangedListener?.onValueChanged(this, mValue, true)
            }
        } else {
            mOnEmptyListener?.invoke(this)
            mEmptyListener?.onEmpty(this)
            mNumEditor?.hint = mValue.toString()
        }
    }

    private fun updateTextWithoutNotify(text: String) {
        mNumEditor?.let {
            it.removeTextChangedListener(this)
            it.setText(text)
            try {
                if (text.isNotBlank()) {
                    it.setSelection(text.length)
                }
            } catch (e: Throwable) {
                Log.d(TAG, "updateTextWithoutNotify error: ", e)
            }
            it.addTextChangedListener(this)
        }
    }

    fun setMinValue(min: Int) {
        mMin = min
        updateButtonState()
    }

    fun setMaxValue(max: Int) {
        mMax = max
        setupEditorFilter(max)
        updateButtonState()
    }

    private fun setupEditorFilter(max: Int) {
        mNumEditor?.let {
            val filters = it.filters
            if (!filters.isNullOrEmpty()) {
                it.filters = arrayOf(*it.filters, InputFilter.LengthFilter(max.toString().length))
            } else {
                it.filters = arrayOf(InputFilter.LengthFilter(max.toString().length))
            }
        }
    }

    private fun setValueInner(value: Int) {
        mValue = value
        updateButtonState()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (!enabled) {
            mAddButton?.isEnabled = false
            mSubButton?.isEnabled = false
        }
    }

    private fun updateButtonState() {
        if (!isEnabled) {
            return
        }
        mMax?.let {
            mAddButton?.isEnabled = mValue < it
        }
        mMin?.let {
            mSubButton?.isEnabled = mValue > it
        }
    }

    fun setValue(value: Int) {
        setValue(value, true)
    }

    /**
     * 是否触发监听器
     */
    fun setValue(value: Int, notify: Boolean) {
        setValueInner(value)
        if (!notify) {
            updateTextWithoutNotify(value.toString())
        } else {
            mNumEditor?.setText(value.toString())
        }
    }

    fun clearEditorFocus() {
        mNumEditor?.let {
            it.clearFocus()
            it.isFocusableInTouchMode = false
            it.isFocusable = false
            it.isFocusableInTouchMode = true
            it.isFocusable = true
        }
    }

    fun setHint(value: Int) {
        setValueInner(value)
        mNumEditor?.hint = "$value"
    }

    /**
     * 是否清空文本框
     */
    fun setHint(value: Int, clear: Boolean) {
        setValueInner(value)
        mNumEditor?.hint = "$value"
        if (clear) {
            updateTextWithoutNotify("")
        }
    }

    fun getValue(): Int {
        return mValue
    }

    fun setNotifyChangeWhenActionDone(notifyChangeWhenActionDone: Boolean) {
        mNotifyChangeWhenActionDone = notifyChangeWhenActionDone
    }

    fun setOnValueChangedListener(listener: ((view: AddSubView, value: Int, edited: Boolean) -> Unit)? = null) {
        mOnValueChangedListener = listener
    }

    fun setOnValueOutOfRangeListener(listener: ((view: AddSubView, value: Int) -> Unit)? = null) {
        mOnValueOutOfRangeListener = listener
    }

    fun setOnEmptyListener(listener: ((view: AddSubView) -> Unit)? = null) {
        mOnEmptyListener = listener
    }

    fun setOnValueChangedListener(listener: OnValueChangedListener) {
        mValueChangedListener = listener
    }

    fun setOnValueOutOfRangeListener(listener: OnValueOutOfRangeListener) {
        mValueOutOfRangeListener = listener
    }

    fun setOnEmptyListener(listener: OnEmptyListener) {
        mEmptyListener = listener
    }

    fun setOnTextViewClickListener(listener: OnClickListener?) {
        if (listener != null) {
            mNumEditor?.isEnabled = false
            mEditorMaskView?.setOnClickListener(listener)
        } else {
            configEditDialog()
        }
    }

    interface OnValueChangedListener {
        fun onValueChanged(view: AddSubView, value: Int, edited: Boolean)
    }

    interface OnValueOutOfRangeListener {
        fun onValueOutOfRange(view: AddSubView, value: Int)
    }

    interface OnEmptyListener {
        fun onEmpty(view: AddSubView)
    }

}