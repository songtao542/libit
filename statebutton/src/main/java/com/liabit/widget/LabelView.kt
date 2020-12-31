package com.liabit.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt

/**
 * Author:         songtao
 * CreateDate:     2020/9/21 11:39
 */
class LabelView : LinearLayout {

    private lateinit var mLabelTextView: TextView
    private lateinit var mEditTextView: EditText
    private lateinit var mTextView: TextView
    private lateinit var mRightTextView: TextView
    private lateinit var mRightArrow: ImageView
    private var mLabelClickable = false
    private var mLabelWithColon = true
    private var mEditable = false

    private var mText: CharSequence = ""
    private var mHint: CharSequence = ""
    private var mRightText: CharSequence = ""
    private var mLabelText: CharSequence = ""
    private var mLabelTextSize: Int = 0
    private var mTextSize: Int = 0
    private var mRightTextSize: Int = 0

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
        init(context, attrs, defStyleAttr, defStyleAttr)
    }

    @SuppressLint("SetTextI18n")
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.label_view, this, true)
        isClickable = true
        mLabelTextView = findViewById(R.id.label)
        mTextView = findViewById(R.id.textView)
        mEditTextView = findViewById(R.id.editText)
        mRightTextView = findViewById(R.id.rightText)
        mRightArrow = findViewById(R.id.rightArrow)

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelView, defStyleAttr, defStyleRes)
            mEditable = typedArray.getBoolean(R.styleable.LabelView_android_editable, false)
            if (mEditable) {
                mEditTextView.visibility = View.VISIBLE
                mTextView.visibility = View.GONE
            } else {
                mTextView.visibility = View.VISIBLE
                mEditTextView.visibility = View.GONE
            }
            mLabelWithColon = typedArray.getBoolean(R.styleable.LabelView_labelWithColon, false)
            mLabelText = typedArray.getText(R.styleable.LabelView_android_label) ?: ""
            if (mLabelWithColon) {
                mLabelTextView.text = "$mLabelText${context.resources.getString(R.string.colon)}"
            } else {
                mLabelTextView.text = mLabelText
            }

            mText = typedArray.getText(R.styleable.LabelView_android_text) ?: ""
            textView.text = mText

            mHint = typedArray.getText(R.styleable.LabelView_android_hint) ?: ""
            textView.hint = mHint

            mLabelTextSize = typedArray.getDimensionPixelSize(R.styleable.LabelView_labelTextSize, 0)
            if (mLabelTextSize > 0) {
                mLabelTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLabelTextSize.toFloat())
            }
            mTextSize = typedArray.getDimensionPixelSize(R.styleable.LabelView_android_textSize, 0)
            if (mTextSize > 0) {
                mEditTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize.toFloat())
            }
            mRightTextSize = typedArray.getDimensionPixelSize(R.styleable.LabelView_rightTextSize, 0)
            if (mRightTextSize > 0) {
                mRightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRightTextSize.toFloat())
            }

            val textPaddingBottom = typedArray.getDimension(R.styleable.LabelView_textPaddingBottom, 0f)

            typedArray.getColorStateList(R.styleable.LabelView_android_textColor)?.let {
                textView.setTextColor(it)
            }
            typedArray.getColorStateList(R.styleable.LabelView_android_textColorHint)?.let {
                textView.setHintTextColor(it)
            }
            typedArray.getColorStateList(R.styleable.LabelView_labelTextColor)?.let {
                mLabelTextView.setTextColor(it)
            }
            typedArray.getColorStateList(R.styleable.LabelView_rightTextColor)?.let {
                mRightTextView.setTextColor(it)
            }
            mRightText = typedArray.getText(R.styleable.LabelView_rightText) ?: ""
            mRightTextView.text = mRightText
            mLabelClickable = typedArray.getBoolean(R.styleable.LabelView_labelClickable, false)

            if (typedArray.getBoolean(R.styleable.LabelView_showRightArrow, false)) {
                mRightArrow.visibility = View.VISIBLE
            } else {
                mRightArrow.visibility = View.GONE
            }

            val rightArrowIcon = typedArray.getDrawable(R.styleable.LabelView_rightArrow)
            if (rightArrowIcon != null) {
                mRightArrow.setImageDrawable(rightArrowIcon)
            }

            val rightArrowColor = typedArray.getColorStateList(R.styleable.LabelView_rightArrowColor)
            if (rightArrowColor != null) {
                mRightArrow.imageTintList = rightArrowColor
            }

            val rightArrowPadding = typedArray.getDimension(R.styleable.LabelView_rightArrowPadding, -1f)
            if (rightArrowPadding >= 0) {
                mRightTextView.setPadding(0, 0, rightArrowPadding.toInt(), textPaddingBottom.toInt())
            } else {
                mRightTextView.setPadding(0, 0, 0, textPaddingBottom.toInt())
            }

            mLabelTextView.setPadding(0, 0, 0, textPaddingBottom.toInt())
            textView.setPadding(0, 0, 0, textPaddingBottom.toInt())

            val rightArrowSize = typedArray.getDimension(R.styleable.LabelView_rightArrowSize, -1f)
            if (rightArrowSize > 0) {
                val lp = mRightArrow.layoutParams
                        ?: LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                            gravity = Gravity.CENTER_VERTICAL
                        }
                lp.width = rightArrowSize.toInt()
                lp.height = rightArrowSize.toInt()
                mRightArrow.layoutParams = lp
            }



            typedArray.recycle()
        }
    }

    private val textView: TextView
        get() {
            return if (mEditable) mEditTextView else mTextView
        }

    override fun setOnClickListener(listener: OnClickListener?) {
        if (mLabelClickable) {
            super.setOnClickListener(listener)
        } else {
            mEditTextView.setOnClickListener(listener)
        }
    }

    fun setLabelWithColon(withColon: Boolean) {
        mLabelWithColon = withColon
    }

    @SuppressLint("SetTextI18n")
    fun setLabel(label: CharSequence) {
        if (mLabelWithColon) {
            mLabelTextView.text = "$label${context.resources.getString(R.string.colon)}"
        } else {
            mLabelTextView.text = label
        }
    }

    fun setText(text: CharSequence) {
        mText = text
        textView.text = mText
    }

    fun setHint(hint: CharSequence) {
        mHint = hint
        textView.hint = mHint
    }

    fun setRightText(text: CharSequence) {
        mRightText = text
        mRightTextView.text = mRightText
    }

    fun setRightTextColor(@ColorInt textColor: Int) {
        mRightTextView.setTextColor(ColorStateList.valueOf(textColor))
    }

}