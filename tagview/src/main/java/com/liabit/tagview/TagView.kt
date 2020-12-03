package com.liabit.tagview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Selection
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.BaseMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import java.util.*
import kotlin.collections.ArrayList


@Suppress("MemberVisibilityCanBePrivate", "unused")
class TagView : AppCompatTextView {

    companion object {
        private const val DEFAULT_SEPARATOR = " "
    }

    private var mTagPaddingLeft = 0
    private var mTagPaddingTop = 0
    private var mTagPaddingRight = 0
    private var mTagPaddingBottom = 0
    private var mTagRadius = 0
    private var mTagUppercase = false
    private var mTagColor: Int = Color.TRANSPARENT
    private var mTagSeparator: String = DEFAULT_SEPARATOR
    private var mTags: MutableList<Tag> = ArrayList()

    private var mScrollable = false

    private var mOnTagClickListener: OnTagClickListener? = null

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
        mTagPaddingLeft = dp2px(10f)
        mTagPaddingTop = dp2px(5f)
        mTagPaddingRight = mTagPaddingLeft
        mTagPaddingBottom = mTagPaddingTop
        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.TagView, defStyleAttr, 0)
            mTagRadius = typedArray.getDimensionPixelSize(R.styleable.TagView_tagRadius, 0)
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
            mTagUppercase = typedArray.getBoolean(R.styleable.TagView_tagUppercase, false)
            mTagColor = typedArray.getColor(R.styleable.TagView_tagColor, Color.TRANSPARENT)
            mTagSeparator = typedArray.getString(R.styleable.TagView_tagSeparator) ?: mTagSeparator

            typedArray.getTextArray(R.styleable.TagView_tags)?.let { tags ->
                setStringList(MutableList(tags.size) { tags[it].toString() })
            }

            typedArray.recycle()
        }
    }

    private fun dp2px(dip: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.resources.displayMetrics).toInt()
    }

    override fun scrollTo(x: Int, y: Int) {
        if (mScrollable) {
            super.scrollTo(x, y)
        }
    }

    override fun scrollBy(x: Int, y: Int) {
        if (mScrollable) {
            super.scrollBy(x, y)
        }
    }

    fun setScrollable(scrollable: Boolean) {
        mScrollable = scrollable
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

    fun setStringArray(tags: Array<String>) {
        mTags.clear()
        for (tag in tags) {
            mTags.add(Tag(tag))
        }
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
        val sb = InnerSpannableStringBuilder()
        val iterator = mTags.iterator()
        while (iterator.hasNext()) {
            val tag = iterator.next()
            val text = if (mTagUppercase) tag.tag.toUpperCase(Locale.ROOT) else tag.tag
            val tagColor = tag.color ?: mTagColor
            val tagTextColor = tag.textColor ?: currentTextColor
            sb.append(text).setSpan(createSpan(tag, tagTextColor, tagColor), sb.length - text.length, sb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (iterator.hasNext() && mTagSeparator.isNotEmpty()) {
                sb.append(mTagSeparator)
            }
        }
        text = sb
    }

    @Suppress("SENSELESS_COMPARISON")
    override fun setText(text: CharSequence?, type: BufferType?) {
        if (text != null && text !is InnerSpannableStringBuilder) {
            // 此处 mTagSeparator 虽然为不可空类型，但由于父类构造函数中调用setText方法时，
            // mTagSeparator 还未被初始化，所以会报空指针异常
            if (mTagSeparator == null) {
                mTagSeparator = DEFAULT_SEPARATOR
            }
            // mTags 也可能由于构造函数的先后顺序问题而为null
            if (mTags == null) {
                mTags = ArrayList()
            }
            setStringList(text.split(mTagSeparator))
        } else {
            super.setText(text, type)
        }
    }

    private fun createSpan(tag: Tag, textColor: Int, color: Int): TagSpan {
        return TagSpan(tag, textColor, color)
    }

    fun setOnTagClickListener(listener: OnTagClickListener?) {
        mOnTagClickListener = listener
        if (movementMethod !is ClickableMovementMethod) {
            movementMethod = ClickableMovementMethod.instance
        }
    }

    fun setOnTagClickListener(listener: ((tag: Tag) -> Unit)?) {
        val clickListener: OnTagClickListener? = if (listener != null) {
            object : OnTagClickListener {
                override fun onTagClick(tag: Tag) {
                    listener.invoke(tag)
                }
            }
        } else {
            null
        }
        setOnTagClickListener(clickListener)
    }

    interface OnTagClickListener {
        fun onTagClick(tag: Tag)
    }

    private class InnerSpannableStringBuilder : SpannableStringBuilder {
        constructor() : super()
        constructor(text: CharSequence?) : super(text)
        constructor(text: CharSequence?, start: Int, end: Int) : super(text, start, end)
    }

    data class Tag(
            val tag: String,
            var color: Int? = null,
            val textColor: Int? = null) {
        constructor(tag: String, color: Int) : this(tag, color, null)

        override fun toString(): String {
            return "Tag(tag=$tag, color=${Integer.toHexString(color ?: 0)}, textColor=${Integer.toHexString(textColor ?: 0)})"
        }
    }

    private inner class TagSpan(private val mTag: Tag, textColor: Int, tagColor: Int)
        : ClickableImageSpan(TagDrawable(
            mTag.tag,
            textSize,
            textColor,
            typeface === Typeface.DEFAULT_BOLD,
            tagColor,
            mTagPaddingLeft,
            mTagPaddingTop,
            mTagPaddingRight,
            mTagPaddingBottom,
            mTagRadius.toFloat())) {
        override fun onClick(view: View) {
            mOnTagClickListener?.onTagClick(mTag)
        }
    }

    @Suppress("CanBeParameter")
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

    private abstract class ClickableImageSpan(drawable: Drawable) : ImageSpan(drawable) {
        abstract fun onClick(view: View)
    }

    class ClickableMovementMethod : BaseMovementMethod() {
        companion object {
            val instance: ClickableMovementMethod by lazy { ClickableMovementMethod() }
        }

        override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
            val action = event.action
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                var x = event.x.toInt()
                var y = event.y.toInt()
                if (x < widget.totalPaddingLeft || y < widget.totalPaddingTop
                        || x > widget.width - widget.totalPaddingRight
                        || y > widget.height - widget.totalPaddingBottom) return true
                x -= widget.totalPaddingLeft
                y -= widget.totalPaddingTop
                x += widget.scrollX
                y += widget.scrollY

                val layout = widget.layout
                val line = layout.getLineForVertical(y)
                val off = layout.getOffsetForHorizontal(line, x.toFloat())

                val links: Array<ClickableSpan> = buffer.getSpans(off, off, ClickableSpan::class.java)
                val imageSpans: Array<ClickableImageSpan> = buffer.getSpans(off, off, ClickableImageSpan::class.java)
                when {
                    links.isNotEmpty() -> {
                        if (action == MotionEvent.ACTION_UP) {
                            val span = links[0]
                            (widget.text as? Spannable)?.let {
                                val start = it.getSpanStart(span)
                                val end = it.getSpanEnd(span)
                                val l = layout.getPrimaryHorizontal(start)
                                val r = layout.getPrimaryHorizontal(end)
                                if (x >= l && x <= r) {
                                    span.onClick(widget)
                                }
                            }
                        } else if (action == MotionEvent.ACTION_DOWN) {
                            Selection.setSelection(buffer,
                                    buffer.getSpanStart(links[0]),
                                    buffer.getSpanEnd(links[0]))
                        }
                        return true
                    }
                    imageSpans.isNotEmpty() -> {
                        if (action == MotionEvent.ACTION_UP) {
                            val span = imageSpans[0]
                            (widget.text as? Spannable)?.let {
                                val start = it.getSpanStart(span)
                                val end = it.getSpanEnd(span)
                                val l = layout.getPrimaryHorizontal(start)
                                val r = layout.getPrimaryHorizontal(end)
                                if (x >= l && x <= r) {
                                    span.onClick(widget)
                                }
                            }
                        } else if (action == MotionEvent.ACTION_DOWN) {
                            Selection.setSelection(buffer,
                                    buffer.getSpanStart(imageSpans[0]),
                                    buffer.getSpanEnd(imageSpans[0]))
                        }
                        return true
                    }
                    else -> {
                        Selection.removeSelection(buffer)
                    }
                }
            }
            return true
        }
    }
}
