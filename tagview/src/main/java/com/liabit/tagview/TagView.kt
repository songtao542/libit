package com.liabit.tagview

import android.R.attr.textColor
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.util.Log
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

    private var mTagPaddingLeft = 0
    private var mTagPaddingTop = 0
    private var mTagPaddingRight = 0
    private var mTagPaddingBottom = 0
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
        mTagPaddingLeft = dp2px(DEFAULT_PADDING)
        mTagPaddingTop = mTagPaddingLeft
        mTagPaddingRight = mTagPaddingLeft
        mTagPaddingBottom = mTagPaddingLeft
        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.TagView, defStyleAttr, 0)
            mTagRadius = typedArray.getDimensionPixelSize(R.styleable.TagView_tagRadius, dp2px(DEFAULT_RADIUS))
            typedArray.getTextArray(R.styleable.TagView_tags)?.let { tags ->
                setStringList(MutableList(tags.size) { tags[it].toString() })
            }
            mTagPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.TagView_tagPaddingLeft, mTagPaddingLeft)
            mTagPaddingTop = typedArray.getDimensionPixelSize(R.styleable.TagView_tagPaddingTop, mTagPaddingTop)
            mTagPaddingRight = typedArray.getDimensionPixelSize(R.styleable.TagView_tagPaddingRight, mTagPaddingRight)
            mTagPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.TagView_tagPaddingBottom, mTagPaddingBottom)
            val padding = typedArray.getDimensionPixelSize(R.styleable.TagView_tagPadding, 0)
            if (padding > 0) {
                mTagPaddingLeft = padding
                mTagPaddingTop = padding
                mTagPaddingRight = padding
                mTagPaddingBottom = padding
            }
            val paddingHorizontal = typedArray.getDimensionPixelSize(R.styleable.TagView_tagPaddingHorizontal, 0)
            if (paddingHorizontal > 0) {
                mTagPaddingLeft = paddingHorizontal
                mTagPaddingRight = paddingHorizontal
            }
            val paddingVertical = typedArray.getDimensionPixelSize(R.styleable.TagView_tagPaddingVertical, 0)
            if (paddingVertical > 0) {
                mTagPaddingTop = paddingVertical
                mTagPaddingBottom = paddingVertical
            }
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
            if (tag.color == Color.TRANSPARENT) {
                mTagColor?.let {
                    tag.color = it
                }
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
                tagPaddingLeft = mTagPaddingLeft,
                tagPaddingTop = mTagPaddingTop,
                tagPaddingRight = mTagPaddingRight,
                tagPaddingBottom = mTagPaddingBottom,
                tagRadius = mTagRadius.toFloat()
        )
    }

    class Tag(val tag: String, var color: Int = Color.TRANSPARENT)

    private class TagSpan(text: String,
                          textSize: Float,
                          textColor: Int,
                          bold: Boolean,
                          tagColor: Int,
                          tagPaddingLeft: Int,
                          tagPaddingTop: Int,
                          tagPaddingRight: Int,
                          tagPaddingBottom: Int,
                          tagRadius: Float) :
            ImageSpan(
                    TagDrawable(
                            text,
                            textSize,
                            textColor,
                            bold,
                            tagColor,
                            tagPaddingLeft,
                            tagPaddingTop,
                            tagPaddingRight,
                            tagPaddingBottom,
                            tagRadius)
            )

    private class TagDrawable(
            private val mText: String,
            private val mTextSize: Float,
            private val mTextColor: Int,
            private val mBold: Boolean,
            private val mTagColor: Int,
            private val mTagPaddingLeft: Int,
            private val mTagPaddingTop: Int,
            private val mTagPaddingRight: Int,
            private val mTagPaddingBottom: Int,
            private val mTagRadius: Float) : Drawable() {

        private val mTextPaint: TextPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
        private val mTagPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val mTagBounds: RectF

        init {
            mTextPaint.color = mTextColor
            mTextPaint.textSize = mTextSize
            mTextPaint.isFakeBoldText = mBold
            mTextPaint.style = Paint.Style.FILL
            mTextPaint.textAlign = Paint.Align.LEFT

            mTagPaint.color = mTagColor
            mTagPaint.style = Paint.Style.FILL

            val textWidth = (mTextPaint.measureText(mText) + mTagPaddingLeft + mTagPaddingRight).toInt()
            val textHeight = (mTextPaint.textSize + mTagPaddingTop + mTagPaddingBottom).toInt()
            setBounds(0, 0, textWidth, textHeight)
            mTagBounds = RectF(bounds)
        }

        override fun draw(canvas: Canvas) {
            canvas.drawRoundRect(mTagBounds, mTagRadius, mTagRadius, mTagPaint)
            val fm = mTextPaint.fontMetrics
            val textWidth: Float = mTextPaint.measureText(mText)
            val width = mTagBounds.width()
            val height = mTagBounds.height()
            val textCenterVerticalBaselineY: Float = height / 2f - fm.descent + (fm.descent - fm.ascent) / 2f
            val x = (width - textWidth) / 2f
            val y = textCenterVerticalBaselineY
            canvas.drawText(mText, x, y + 2, mTextPaint)
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

        /*
        companion object {
            const val TEXT_ALIGN_LEFT = 0x00000001
            const val TEXT_ALIGN_RIGHT = 0x00000010
            const val TEXT_ALIGN_CENTER_VERTICAL = 0x00000100
            const val TEXT_ALIGN_CENTER_HORIZONTAL = 0x00001000
            const val TEXT_ALIGN_TOP = 0x00010000
            const val TEXT_ALIGN_BOTTOM = 0x00100000
            const val TEXT_CENTER = TEXT_ALIGN_CENTER_HORIZONTAL or TEXT_ALIGN_CENTER_VERTICAL
        }

        private fun getTextLocation(text: String, textAlign: Int, paint: TextPaint, bound: RectF, location: FloatArray) {
            val fm = paint.fontMetrics
            val textWidth: Float = paint.measureText(text)
            val width = bound.width()
            val height = bound.height()
            val textCenterVerticalBaselineY: Float = height / 2f - fm.descent + (fm.descent - fm.ascent) / 2f
            when (textAlign) {
                TEXT_ALIGN_CENTER_HORIZONTAL or TEXT_ALIGN_CENTER_VERTICAL -> {
                    location[0] = (width - textWidth) / 2f
                    location[1] = textCenterVerticalBaselineY
                }
                TEXT_ALIGN_LEFT or TEXT_ALIGN_CENTER_VERTICAL -> {
                    location[0] = textWidth / 2f
                    location[1] = textCenterVerticalBaselineY
                }
                TEXT_ALIGN_RIGHT or TEXT_ALIGN_CENTER_VERTICAL -> {
                    location[0] = width - textWidth / 2
                    location[1] = textCenterVerticalBaselineY
                }
                TEXT_ALIGN_BOTTOM or TEXT_ALIGN_CENTER_HORIZONTAL -> {
                    location[0] = width / 2
                    location[1] = height - fm.bottom
                }
                TEXT_ALIGN_TOP or TEXT_ALIGN_CENTER_HORIZONTAL -> {
                    location[0] = width / 2
                    location[1] = -fm.ascent
                }
                TEXT_ALIGN_TOP or TEXT_ALIGN_LEFT -> {
                    location[0] = textWidth / 2
                    location[1] = -fm.ascent
                }
                TEXT_ALIGN_BOTTOM or TEXT_ALIGN_LEFT -> {
                    location[0] = textWidth / 2
                    location[1] = height - fm.bottom
                }
                TEXT_ALIGN_TOP or TEXT_ALIGN_RIGHT -> {
                    location[0] = width - textWidth / 2
                    location[1] = -fm.ascent
                }
                TEXT_ALIGN_BOTTOM or TEXT_ALIGN_RIGHT -> {
                    location[0] = width - textWidth / 2
                    location[1] = height - fm.bottom
                }
            }
        }*/

    }
}
