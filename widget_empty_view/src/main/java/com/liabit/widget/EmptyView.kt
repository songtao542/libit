package com.liabit.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.liabit.widget.emptyview.R

/**
 * 修改样式，请 override style
 * <!-- ProgressBar 样式 -->
 * <style name="Widget.EmptyView.ProgressBar" parent="Widget.EmptyView.BaseProgressBar" />
 *
 * <!-- ImageView 样式 -->
 * <style name="Widget.EmptyView.Image" parent="Widget.EmptyView.BaseImage" />
 *      <!-- your text style here -->
 * </style>
 *
 * <!-- TextView 样式 -->
 * <style name="Widget.EmptyView.Text" parent="Widget.EmptyView.BaseText" />
 *
 * <!-- Guideline 样式 -->
 * <style name="Widget.EmptyView.Text.Guideline" parent="Widget.EmptyView.Text.BaseGuideline" />
 */
@Suppress("unused")
class EmptyView : ConstraintLayout, GestureDetector.OnGestureListener {

    companion object {
        const val NONE = 0x0000
        const val EMPTY = 0x0001
        const val LOADING = 0x0010
        const val NETWORK = 0x0100
        const val TIME = 0x1000
    }

    @IntDef(NONE, EMPTY, LOADING, NETWORK, TIME)
    @Retention(AnnotationRetention.SOURCE)
    annotation class State

    private lateinit var mTextView: TextView
    private lateinit var mImageView: ImageView
    private lateinit var mProgressBar: ProgressBar

    private var mTimeText: CharSequence? = null
    private var mNetworkText: CharSequence? = null
    private var mLoadingText: CharSequence? = null
    private var mEmptyText: CharSequence? = null

    private var mTimeDrawable: Drawable? = null
    private var mNetworkDrawable: Drawable? = null
    private var mEmptyDrawable: Drawable? = null

    private var mOnClickListener: OnClickListener? = null

    private var mState: Int = NONE

    private lateinit var mGestureDetector: GestureDetector

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

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        LayoutInflater.from(context).inflate(R.layout.empty_view, this, true)
        mProgressBar = findViewById(R.id.progressBar)
        mImageView = findViewById(R.id.imageView)
        mTextView = findViewById(R.id.textView)
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EmptyView, defStyleAttr, defStyleRes)
            mTimeText = typedArray.getString(R.styleable.EmptyView_timeText)
            mTimeDrawable = typedArray.getDrawable(R.styleable.EmptyView_timeIcon)
            mNetworkText = typedArray.getString(R.styleable.EmptyView_networkText)
            mNetworkDrawable = typedArray.getDrawable(R.styleable.EmptyView_networkIcon)
            mLoadingText = typedArray.getString(R.styleable.EmptyView_loadingText)
            mEmptyText = typedArray.getString(R.styleable.EmptyView_emptyText)
            mEmptyDrawable = typedArray.getDrawable(R.styleable.EmptyView_emptyIcon)
            typedArray.recycle()

            // 从主题样式中获取
            val ti = context.obtainStyledAttributes(R.style.Widget_EmptyView_Image, R.styleable.EmptyView)
            if (mTimeDrawable == null) {
                mTimeDrawable = ti.getDrawable(R.styleable.EmptyView_timeIcon)
            }
            if (mNetworkDrawable == null) {
                mNetworkDrawable = ti.getDrawable(R.styleable.EmptyView_networkIcon)
            }
            if (mEmptyDrawable == null) {
                mEmptyDrawable = ti.getDrawable(R.styleable.EmptyView_emptyIcon)
            }
            ti.recycle()

            val tt = context.obtainStyledAttributes(R.style.Widget_EmptyView_Text, R.styleable.EmptyView)
            if (mTimeText == null) {
                mTimeText = typedArray.getString(R.styleable.EmptyView_timeText)
            }
            if (mNetworkText == null) {
                mNetworkText = typedArray.getString(R.styleable.EmptyView_networkText)
            }
            if (mLoadingText == null) {
                mLoadingText = typedArray.getString(R.styleable.EmptyView_loadingText)
            }
            if (mEmptyText == null) {
                mEmptyText = typedArray.getString(R.styleable.EmptyView_emptyText)
            }
            tt.recycle()
        }
        mGestureDetector = GestureDetector(context, this)
        setBackgroundColor(ContextCompat.getColor(context, R.color.empty_view_background_color))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return event?.let { mGestureDetector.onTouchEvent(it) } ?: false
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mOnClickListener = l
    }

    fun setTimeText(text: CharSequence): EmptyView {
        mTimeText = text
        return this
    }

    fun setTimeText(@StringRes textResId: Int): EmptyView {
        mTimeText = context.getString(textResId)
        return this
    }

    fun setNetworkText(text: CharSequence): EmptyView {
        mNetworkText = text
        return this
    }

    fun setNetworkText(@StringRes textResId: Int): EmptyView {
        mNetworkText = context.getString(textResId)
        return this
    }

    fun setLoadingText(text: CharSequence): EmptyView {
        mLoadingText = text
        return this
    }

    fun setLoadingText(@StringRes textResId: Int): EmptyView {
        mLoadingText = context.getString(textResId)
        return this
    }

    fun setEmptyText(text: CharSequence): EmptyView {
        mEmptyText = text
        return this
    }

    fun setEmptyText(@StringRes textResId: Int): EmptyView {
        mEmptyText = context.getString(textResId)
        return this
    }

    fun setTimeDrawable(drawable: Drawable) {
        mTimeDrawable = drawable
    }

    fun setTimeImageResource(@DrawableRes resId: Int) {
        mTimeDrawable = ContextCompat.getDrawable(context, resId)
    }

    fun setNetworkDrawable(drawable: Drawable) {
        mNetworkDrawable = drawable
    }

    fun setNetworkImageResource(@DrawableRes resId: Int) {
        mNetworkDrawable = ContextCompat.getDrawable(context, resId)
    }

    fun setEmptyDrawable(drawable: Drawable) {
        mEmptyDrawable = drawable
    }

    fun setEmptyImageResource(@DrawableRes resId: Int) {
        mEmptyDrawable = ContextCompat.getDrawable(context, resId)
    }

    private fun onClick() {
        when {
            mState and TIME == TIME -> {
                val intent = Intent(Settings.ACTION_DATE_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }

            else -> {
                mOnClickListener?.onClick(this)
            }
        }
    }

    fun isEmpty(): Boolean {
        return mState and EMPTY == EMPTY
    }

    fun setState(state: Int) {
        mState = state
        updateState()
    }

    fun addState(@State state: Int) {
        mState = mState or state
        updateState()
    }

    /**
     * @param predicate If true add [state] else clear [state]
     */
    fun addStateIf(predicate: Boolean, @State state: Int) {
        mState = if (predicate) mState or state else mState and state.inv()
        updateState()
    }

    fun clearState(@State state: Int) {
        mState = mState and state.inv()
        updateState()
    }

    /**
     * @param predicate If true clear [state] else add [state]
     */
    fun clearStateIf(predicate: Boolean, @State state: Int) {
        mState = if (predicate) mState and state.inv() else mState or state
        updateState()
    }

    fun beginTransaction(): StateTransaction {
        return StateTransaction(this, mState)
    }

    class StateTransaction(private val mEmptyView: EmptyView, private var mState: Int) {

        fun clearState(@State state: Int): StateTransaction {
            mState = mState and state.inv()
            return this
        }

        /**
         * @param predicate If true clear [state] else add [state]
         */
        fun clearStateIf(predicate: Boolean, @State state: Int): StateTransaction {
            mState = if (predicate) mState and state.inv() else mState or state
            return this
        }

        fun addState(@State state: Int): StateTransaction {
            mState = mState or state
            return this
        }

        /**
         * @param predicate If true add [state] else clear [state]
         */
        fun addStateIf(predicate: Boolean, @State state: Int): StateTransaction {
            mState = if (predicate) mState or state else mState and state.inv()
            return this
        }

        fun commit() {
            mEmptyView.setState(mState)
        }
    }

    private fun updateState() {
        when {
            mState and TIME == TIME -> {
                visibility = View.VISIBLE
                mProgressBar.visibility = View.INVISIBLE
                mTimeDrawable?.let {
                    mImageView.visibility = View.VISIBLE
                    mImageView.setImageDrawable(it)
                } ?: kotlin.run {
                    mImageView.visibility = View.INVISIBLE
                }
                mTextView.text = mTimeText ?: context.getString(R.string.empty_view_time_not_right)
            }

            mState and NETWORK == NETWORK -> {
                visibility = View.VISIBLE
                mProgressBar.visibility = View.INVISIBLE
                mNetworkDrawable?.let {
                    mImageView.visibility = View.VISIBLE
                    mImageView.setImageDrawable(it)
                } ?: kotlin.run {
                    mImageView.visibility = View.INVISIBLE
                }
                mTextView.text = mNetworkText
                        ?: context.getString(R.string.empty_view_network_not_available)
            }

            mState and LOADING == LOADING -> {
                visibility = View.VISIBLE
                mProgressBar.visibility = View.VISIBLE
                mImageView.visibility = View.INVISIBLE
                mTextView.text = mLoadingText ?: context.getString(R.string.empty_view_loading)
            }

            mState and EMPTY == EMPTY -> {
                visibility = View.VISIBLE
                mProgressBar.visibility = View.INVISIBLE
                mEmptyDrawable?.let {
                    mImageView.visibility = View.VISIBLE
                    mImageView.setImageDrawable(it)
                } ?: kotlin.run {
                    mImageView.visibility = View.INVISIBLE
                }
                mTextView.text = mEmptyText ?: context.getString(R.string.empty_view_no_data)
            }

            else -> {
                visibility = View.GONE
            }
        }
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        onClick()
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

}