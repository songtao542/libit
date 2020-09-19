package com.lolii.filter

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout

/**
 * Author:         songtao
 * CreateDate:     2020/9/18 14:54
 */

@Suppress("unused")
class AddAndSubView : LinearLayout, TextWatcher {

    private var mAddButton: ImageView? = null
    private var mSubButton: ImageView? = null
    private var mNumEditor: EditText? = null
    private var mMin: Int = 0
    private var mMax: Int = 0
    private var mValue: Int = 0
    private var mOnValueChangedListener: ((view: AddAndSubView, value: Int, causeByEdit: Boolean) -> Unit)? = null
    private var mOnValueOutOfRangeListener: ((view: AddAndSubView, value: Int) -> Unit)? = null
    private var mOnEmptyListener: ((view: AddAndSubView) -> Unit)? = null
    private var mValueChangedListener: OnValueChangedListener? = null
    private var mValueOutOfRangeListener: OnValueOutOfRangeListener? = null
    private var mEmptyListener: OnEmptyListener? = null

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
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.add_and_sub_view, this, true)

        var addIcon = R.drawable.ic_num_add
        var subIcon = R.drawable.ic_num_sub
        var editable = true
        var editTextBackground = 0
        var editTextWidth = 0f
        var editTextHeight = 0f
        var iconSize = 0f
        var textColor: ColorStateList? = null
        var textColorHint: ColorStateList? = null

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AddAndSubView, defStyleAttr, defStyleRes)
            addIcon = typedArray.getResourceId(R.styleable.AddAndSubView_addIcon, 0)
            subIcon = typedArray.getResourceId(R.styleable.AddAndSubView_addIcon, 0)
            editable = typedArray.getBoolean(R.styleable.AddAndSubView_editable, true)
            editTextBackground = typedArray.getResourceId(R.styleable.AddAndSubView_editTextBackground, 0)
            editTextWidth = typedArray.getDimension(R.styleable.AddAndSubView_editTextWidth, 0f)
            editTextHeight = typedArray.getDimension(R.styleable.AddAndSubView_editTextHeight, 0f)
            textColor = typedArray.getColorStateList(R.styleable.AddAndSubView_android_textColor)
            textColorHint = typedArray.getColorStateList(R.styleable.AddAndSubView_android_textColorHint)
            iconSize = typedArray.getDimension(R.styleable.AddAndSubView_iconSize, 0f)
            mValue = typedArray.getInt(R.styleable.AddAndSubView_value, 0)
            mMin = typedArray.getInt(R.styleable.AddAndSubView_minValue, 0)
            mMax = typedArray.getInt(R.styleable.AddAndSubView_maxValue, 0)
            typedArray.recycle()
        }

        mAddButton = findViewById(R.id.addButton)
        mSubButton = findViewById(R.id.subButton)
        mNumEditor = findViewById(R.id.numEditor)

        if (addIcon != 0) {
            mAddButton?.setImageResource(addIcon)
        }
        if (subIcon != 0) {
            mSubButton?.setImageResource(subIcon)
        }
        mNumEditor?.isEnabled = editable
        if (editTextBackground != 0) {
            mNumEditor?.setBackgroundResource(editTextBackground)
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
                val defaultWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, context.resources.displayMetrics).toInt()
                val w: Int = if (editTextWidth > 0) editTextWidth.toInt() else defaultWidth
                val h: Int = if (editTextHeight > 0) editTextHeight.toInt() else defaultWidth / 2
                lp = LayoutParams(w, h)
                lp.gravity = Gravity.CENTER_VERTICAL
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
                alp = LayoutParams(size, size)
                alp.gravity = Gravity.CENTER_VERTICAL
            }
            mAddButton?.layoutParams = alp
            var slp = mSubButton?.layoutParams
            if (slp != null) {
                slp.width = size
                slp.height = size
            } else {
                slp = LayoutParams(size, size)
                slp.gravity = Gravity.CENTER_VERTICAL
            }
            mSubButton?.layoutParams = slp
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
            if (mValue < mMax) {
                mValue += 1
                mOnValueChangedListener?.invoke(this, mValue, false)
                mValueChangedListener?.onValueChanged(this, mValue, false)
            }
        }
        mSubButton?.setOnClickListener {
            if (mValue > mMin) {
                mValue -= 1
                mOnValueChangedListener?.invoke(this, mValue, false)
                mValueChangedListener?.onValueChanged(this, mValue, false)
            }
        }
        mNumEditor?.addTextChangedListener(this)
        mNumEditor?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = mNumEditor?.text ?: return@setOnFocusChangeListener
                setAndCheckValue(text.toString())
            }
        }
        mNumEditor?.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = v.text ?: return@setOnEditorActionListener true
                setAndCheckValue(text.toString())
            }
            return@setOnEditorActionListener true
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val text = s?.toString() ?: return
        setAndCheckValue(text)
    }

    private fun setAndCheckValue(text: String) {
        if (text.isNotBlank() && TextUtils.isDigitsOnly(text)) {
            val number = text.toInt()
            if (number < mMin || number > mMax) {
                mOnValueOutOfRangeListener?.invoke(this, number)
                mValueOutOfRangeListener?.onValueOutOfRange(this, number)
                return
            }
            if (number != mValue) {
                mValue = number
                mOnValueChangedListener?.invoke(this, number, true)
                mValueChangedListener?.onValueChanged(this, number, true)
            }
        } else {
            mOnEmptyListener?.invoke(this)
            mEmptyListener?.onEmpty(this)
            if (mOnEmptyListener == null && mEmptyListener == null) {
                mNumEditor?.hint = mValue.toString()
            }
        }
    }


    fun setMinValue(min: Int) {
        mMin = min
    }

    fun setMaxValue(max: Int) {
        mMax = max
    }

    fun setValue(value: Int) {
        mValue = value
        mNumEditor?.setText("$value")
    }

    fun setHint(value: Int) {
        mValue = value
        mNumEditor?.hint = "$value"
    }

    fun setHint(value: Int, clearText: Boolean) {
        mValue = value
        mNumEditor?.hint = "$value"
        if (clearText) {
            mNumEditor?.removeTextChangedListener(this)
            mNumEditor?.setText("")
            mNumEditor?.addTextChangedListener(this)
        }
    }

    fun getValue(): Int {
        return mValue
    }

    fun setOnValueChangedListener(listener: ((view: AddAndSubView, value: Int, edited: Boolean) -> Unit)? = null) {
        mOnValueChangedListener = listener
    }

    fun setOnValueOutOfRangeListener(listener: ((view: AddAndSubView, value: Int) -> Unit)? = null) {
        mOnValueOutOfRangeListener = listener
    }

    fun setOnEmptyListener(listener: ((view: AddAndSubView) -> Unit)? = null) {
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

    interface OnValueChangedListener {
        fun onValueChanged(view: AddAndSubView, value: Int, edited: Boolean)
    }

    interface OnValueOutOfRangeListener {
        fun onValueOutOfRange(view: AddAndSubView, value: Int)
    }

    interface OnEmptyListener {
        fun onEmpty(view: AddAndSubView)
    }

}