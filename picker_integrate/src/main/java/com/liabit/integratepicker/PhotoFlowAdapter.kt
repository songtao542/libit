package com.liabit.integratepicker

import android.content.Context
import android.net.Uri
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*

class PhotoFlowAdapter(private val context: Context) : FlowLayout.ViewAdapter {

    enum class AddButtonStyle {
        BORDER,
        SOLID;
    }

    private var mUris: List<Uri>? = null
    private var mMaxShow = Int.MAX_VALUE
    private var mShowLastAsAdd = true
    private var mShowAddWhenFull = true
    private var addStyle: AddButtonStyle = AddButtonStyle.SOLID

    private var viewerIsMember = false

    private var mOnAddClickListener: ((size: Int) -> Unit)? = null

    fun setOnAddClickListener(listener: (size: Int) -> Unit): PhotoFlowAdapter {
        mOnAddClickListener = listener
        return this
    }

    fun setViewerIsMember(isMember: Boolean): PhotoFlowAdapter {
        this.viewerIsMember = isMember
        return this
    }

    fun setUris(uris: List<Uri>): PhotoFlowAdapter {
        this.mUris = uris
        return this
    }

    fun setMaxShow(max: Int): PhotoFlowAdapter {
        mMaxShow = max
        return this
    }

    fun setAddButtonStyle(style: AddButtonStyle): PhotoFlowAdapter {
        this.addStyle = style
        return this
    }

    fun setShowAddWhenFull(show: Boolean): PhotoFlowAdapter {
        this.mShowAddWhenFull = show
        return this
    }

    fun setLastAsAdd(lastAsAdd: Boolean): PhotoFlowAdapter {
        mShowLastAsAdd = lastAsAdd
        return this
    }

    private val cache = ArrayDeque<View>()
    private var cachedAdd: View? = null

    private fun createImageView(url: String): View {
        val view = if (cache.size > 0) cache.pop() else AppCompatImageView(context)
        val imageView = if (view is TextImageView) {
            view.setTextVisibility(View.INVISIBLE)
            view.imageView
        } else view as ImageView
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(context).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.p_ic_placeholder)).load(url).into(imageView)
        return view
    }

    private fun createImageView(uri: Uri): View {
        val view = if (cache.size > 0) cache.pop() else AppCompatImageView(context)
        val imageView = if (view is TextImageView) {
            view.setTextVisibility(View.INVISIBLE)
            view.imageView
        } else view as ImageView
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(context).load(uri).into(imageView)
        return view
    }

    private fun createLastAdd(): View {
        cachedAdd?.let {
            return cachedAdd!!
        }
        val add = AddIconView(context, addStyle)
        if (mOnAddClickListener != null) {
            add.setOnClickListener { mOnAddClickListener?.invoke(getItemCount()) }
        }
        return add
    }

    override fun create(index: Int): View {
        if (mShowLastAsAdd && index == getItemCount() - 1) {
            val size = mUris?.size ?: 0
            if (size < mMaxShow || (size >= mMaxShow && mShowAddWhenFull)) {
                return createLastAdd()
            }
        }
        val uri = mUris!![index]
        return createImageView(uri).apply { setTag(R.id.p_photo_tag_id, uri) }
    }

    override fun onRecycleView(view: View) {
        when (view) {
            is AddIconView -> cachedAdd = view
            else -> cache.add(view)
        }
    }

    override fun getItemCount(): Int {
        val size = mUris?.size ?: 0
        val add = if (mShowLastAsAdd) 1 else 0
        return when {
            size == 0 -> add
            size < mMaxShow -> size + add
            else -> mMaxShow
        }
    }

    inner class TextImageView(context: Context) : FrameLayout(context) {

        val imageView: AppCompatImageView = AppCompatImageView(context)
        private val textView: AppCompatTextView = AppCompatTextView(context)

        init {
            imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            imageView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            addView(imageView)

            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            textView.setTextColor(context.colorOf(android.R.color.white))

            val dip5 = context.dip(5f)
            textView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                bottomMargin = dip5
            }
            addView(textView)
        }

        fun setTextPrimaryStyle(primary: Boolean) {
            if (primary) {
                val dip5 = context.dip(5f)
                val dip4 = context.dip(4f)
                textView.setPadding(dip5 * 2, dip4, dip5 * 2, dip4)
                textView.setBackgroundResource(R.drawable.p_button_primary_circle_corner_selector)
            } else {
                textView.setPadding(0, 0, 0, 0)
                textView.setBackgroundColor(context.colorOf(android.R.color.transparent))
            }
        }

        fun setTextVisibility(visibility: Int) {
            textView.visibility = visibility
        }

        fun setText(resId: Int) {
            textView.setText(resId)
        }

        fun setText(text: CharSequence) {
            textView.text = text
        }

        fun setTextSize(unit: Int, size: Float) {
            textView.setTextSize(unit, size)
        }

        fun setTextColor(color: Int) {
            textView.setTextColor(color)
        }

        @Suppress("unused")
        fun setTextBackgroundResource(resId: Int) {
            textView.setBackgroundResource(resId)
        }

        fun setImageResource(resId: Int) {
            imageView.setImageResource(resId)
        }

        fun setImageBackgroundResource(resId: Int) {
            imageView.setBackgroundResource(resId)
        }
    }

    inner class AddIconView(context: Context, addStyle: AddButtonStyle) : LinearLayout(context) {
        init {
            if (addStyle == AddButtonStyle.SOLID) {
                setBackgroundColor(context.colorOf(R.color.p_dddddd))
            } else {
                setBackgroundResource(R.drawable.p_add_button_border)
            }
            orientation = VERTICAL

            val spaceTop = Space(context)
            val topLp = LayoutParams(LayoutParams.MATCH_PARENT, 0)
            topLp.weight = 1f
            spaceTop.layoutParams = topLp

            val spaceBottom = Space(context)
            val bottomLp = LayoutParams(LayoutParams.MATCH_PARENT, 0)
            bottomLp.weight = 1f
            spaceBottom.layoutParams = bottomLp

            val icon = ImageView(context)
            val ilp = LayoutParams(context.dip(20f), context.dip(20f))
            ilp.gravity = Gravity.CENTER
            icon.layoutParams = ilp
            if (addStyle == AddButtonStyle.SOLID) {
                icon.setImageResource(R.drawable.p_ic_add)
            } else {
                icon.setImageResource(R.drawable.p_ic_add_darker)
            }
            icon.scaleType = ImageView.ScaleType.CENTER_INSIDE

            val text = TextView(context)
            val tlp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            tlp.gravity = Gravity.CENTER
            tlp.topMargin = context.dip(10f)
            text.layoutParams = tlp
            if (addStyle == AddButtonStyle.SOLID) {
                text.setTextColor(context.colorOf(R.color.p_white))
            } else {
                text.setTextColor(context.colorOf(R.color.p_darker))
            }
            text.setText(R.string.p_add_image)

            addView(spaceTop)
            addView(icon)
            addView(text)
            addView(spaceBottom)
        }
    }

}