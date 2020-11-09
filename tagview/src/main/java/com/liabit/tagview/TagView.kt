package com.liabit.tagview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import java.util.*
import kotlin.collections.ArrayList

@Suppress("MemberVisibilityCanBePrivate", "unused")
class TagView : AppCompatTextView {

    companion object {
        private const val DEFAULT_PADDING = 8f
        private const val DEFAULT_RADIUS = 6f
        private const val DEFAULT_UPPERCASE = false
        private const val DEFAULT_COLOR = 0xff888888.toInt()
    }

    private var mTagPadding = 0
    private var mTagRadius = 0
    private var mTagUppercase = DEFAULT_UPPERCASE
    private var mTagColor: Int? = null
    private var mTagSeparator: String? = null
    private var mTags: MutableList<Tag> = ArrayList()

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        mTagRadius = dp2px(DEFAULT_RADIUS)
        mTagPadding = dp2px(DEFAULT_PADDING)
        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.TagView, defStyleAttr, 0)
            mTagRadius = typedArray.getDimensionPixelSize(R.styleable.TagView_tagRadius, dp2px(DEFAULT_RADIUS))
            mTagPadding = typedArray.getDimensionPixelSize(R.styleable.TagView_tagPadding, dp2px(DEFAULT_PADDING))
            mTagUppercase = typedArray.getBoolean(R.styleable.TagView_tagUppercase, DEFAULT_UPPERCASE)
            mTagColor = typedArray.getColor(R.styleable.TagView_tagColor, DEFAULT_COLOR)
            mTagSeparator = typedArray.getString(R.styleable.TagView_tagSeparator)
            typedArray.recycle()
        }
    }

    private fun dp2px(dip: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.resources.displayMetrics).toInt()
    }

    fun setTagColor(color: Int) {
        mTagColor = color
        updateText()
    }

    fun setTagSeparator(separator: String) {
        mTagSeparator = separator
        updateText()
    }

    fun setTagArray(tags: Array<Tag>) {
        setTagList(listOf(*tags))
    }

    fun setTagList(tags: List<Tag>) {
        mTags.clear()
        mTags.addAll(tags)
        updateText()
    }

    fun setStringList(tags: List<String>) {
        mTags.clear()
        for (tag in tags) {
            mTags.add(Tag(tag))
        }
        updateText()
    }

    private fun updateText() {
        if (mTags.isEmpty()) return
        val sb = SpannableStringBuilder()
        val iterator = mTags.iterator()
        while (iterator.hasNext()) {
            val tag = iterator.next()
            val text = if (mTagUppercase) tag.tag.toUpperCase(Locale.ROOT) else tag.tag
            mTagColor?.let {
                tag.color = it
            }
            sb.append(text).setSpan(createSpan(text, tag.color), sb.length - text.length, sb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (iterator.hasNext() && mTagSeparator != null) {
                sb.append(mTagSeparator)
            }
        }
        text = sb
    }

    private fun createSpan(text: String, color: Int): TagSpan {
        return TagSpan(
                text = text,
                textSize = textSize,
                textColor = currentTextColor,
                bold = typeface === Typeface.DEFAULT_BOLD,
                tagColor = color,
                tagPadding = mTagPadding,
                tagRadius = mTagRadius.toFloat()
        )
    }

    class Tag(val tag: String, var color: Int = Color.TRANSPARENT)

    private class TagSpan(text: String,
                          textSize: Float,
                          textColor: Int,
                          bold: Boolean,
                          tagColor: Int,
                          tagPadding: Int,
                          tagRadius: Float) :
            ImageSpan(
                    TagDrawable(
                            textSize = textSize,
                            textColor = textColor,
                            bold = bold,
                            tagColor = tagColor,
                            text,
                            tagPadding,
                            tagRadius)
            )

    private class TagDrawable(textSize: Float,
                              textColor: Int,
                              bold: Boolean,
                              tagColor: Int,
                              private val mText: String,
                              private val mTagPadding: Int,
                              private val mTagRadius: Float) : Drawable() {

        companion object {
            private const val MAGIC_PADDING_LEFT = 0
            private const val MAGIC_PADDING_BOTTOM = 3
        }

        private val mTextPaint: Paint
        private val mTagPaint: Paint
        private val mTagBounds: RectF

        init {
            mTextPaint = TextPaint()
            mTextPaint.color = textColor
            mTextPaint.textSize = textSize
            mTextPaint.isAntiAlias = true
            mTextPaint.isFakeBoldText = bold
            mTextPaint.style = Paint.Style.FILL
            mTextPaint.textAlign = Paint.Align.LEFT

            mTagPaint = Paint()
            mTagPaint.color = tagColor
            mTagPaint.style = Paint.Style.FILL
            mTagPaint.isAntiAlias = true

            val textWidth = (mTextPaint.measureText(mText) + mTagPadding + mTagPadding).toInt()
            val textHeight = (mTextPaint.textSize + mTagPadding + mTagPadding).toInt()
            setBounds(0, 0, textWidth, textHeight)
            mTagBounds = RectF(bounds)
        }

        override fun draw(canvas: Canvas) {
            canvas.drawRoundRect(mTagBounds, mTagRadius, mTagRadius, mTagPaint)
            canvas.drawText(mText, (mTagPadding + MAGIC_PADDING_LEFT).toFloat(), mTextPaint.textSize + mTagPadding - MAGIC_PADDING_BOTTOM, mTextPaint)
        }

        override fun setAlpha(alpha: Int) {
            mTextPaint.alpha = alpha
            mTagPaint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            mTextPaint.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }
    }
}