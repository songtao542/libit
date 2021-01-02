package com.liabit.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat


class BottomMenuDialog(private val context: Context) {

    private var mDialog: AlertDialog? = null

    private val mMenuItems = ArrayList<MenuItem>()

    private var mMenuItemClickListener: ((view: View) -> Unit)? = null

    private var mTopLeftRadius: Float = 0f
    private var mTopRightRadius: Float = 0f
    private var mBottomLeftRadius: Float = 0f
    private var mBottomRightRadius: Float = 0f

    private var mBackgroundColor: Int = Color.WHITE

    private var mTitleMatchParentWidth: Boolean = false
    private var mIconPadding = 8f

    fun menu(title: CharSequence, onClickListener: ((view: View) -> Unit)? = null): BottomMenuDialog {
        mMenuItems.add(MenuItem(title, null, null, null, false, onClickListener))
        return this
    }

    fun menu(@StringRes titleResId: Int, onClickListener: ((view: View) -> Unit)? = null): BottomMenuDialog {
        mMenuItems.add(MenuItem(context.getString(titleResId), null, null, null, false, onClickListener))
        return this
    }

    fun menu(@StringRes titleResId: Int, endIcon: Drawable, onClickListener: ((view: View) -> Unit)? = null): BottomMenuDialog {
        mMenuItems.add(MenuItem(context.getString(titleResId), null, endIcon, null, false, onClickListener))
        return this
    }

    fun menu(@StringRes titleResId: Int, startIcon: Drawable, endIcon: Drawable, onClickListener: ((view: View) -> Unit)? = null): BottomMenuDialog {
        mMenuItems.add(MenuItem(context.getString(titleResId), startIcon, endIcon, null, false, onClickListener))
        return this
    }

    fun menu(title: CharSequence, endIcon: Drawable, onClickListener: ((view: View) -> Unit)? = null): BottomMenuDialog {
        mMenuItems.add(MenuItem(title, null, endIcon, null, false, onClickListener))
        return this
    }

    fun menu(title: CharSequence, startIcon: Drawable, endIcon: Drawable, onClickListener: ((view: View) -> Unit)? = null): BottomMenuDialog {
        mMenuItems.add(MenuItem(title, startIcon, endIcon, null, false, onClickListener))
        return this
    }

    fun menu(@StringRes titleResId: Int, @DrawableRes endIconResId: Int, onClickListener: ((view: View) -> Unit)? = null): BottomMenuDialog {
        val title = context.getString(titleResId)
        val endIcon = ResourcesCompat.getDrawable(context.resources, endIconResId, context.theme)
        mMenuItems.add(MenuItem(title, null, endIcon, null, false, onClickListener))
        return this
    }

    fun menu(@StringRes titleResId: Int, @DrawableRes startIconResId: Int, @DrawableRes endIconResId: Int, onClickListener: ((view: View) -> Unit)? = null): BottomMenuDialog {
        val title = context.getString(titleResId)
        val startIcon = ResourcesCompat.getDrawable(context.resources, startIconResId, context.theme)
        val endIcon = ResourcesCompat.getDrawable(context.resources, endIconResId, context.theme)
        mMenuItems.add(MenuItem(title, startIcon, endIcon, null, false, onClickListener))
        return this
    }

    fun menu(title: CharSequence, @DrawableRes endIconResId: Int, onClickListener: ((view: View) -> Unit)? = null): BottomMenuDialog {
        val endIcon = ResourcesCompat.getDrawable(context.resources, endIconResId, context.theme)
        mMenuItems.add(MenuItem(title, null, endIcon, null, false, onClickListener))
        return this
    }

    fun menu(title: CharSequence, @DrawableRes startIconResId: Int, @DrawableRes endIconResId: Int, onClickListener: ((view: View) -> Unit)? = null): BottomMenuDialog {
        val startIcon = ResourcesCompat.getDrawable(context.resources, startIconResId, context.theme)
        val endIcon = ResourcesCompat.getDrawable(context.resources, endIconResId, context.theme)
        mMenuItems.add(MenuItem(title, startIcon, endIcon, null, false, onClickListener))
        return this
    }

    fun menu(menus: List<MenuItem>): BottomMenuDialog {
        mMenuItems.addAll(menus)
        return this
    }

    fun setOnMenuItemClickListener(listener: ((view: View) -> Unit)?): BottomMenuDialog {
        mMenuItemClickListener = listener
        return this
    }

    fun setRadius(leftTop: Float, rightTop: Float, leftBottom: Float, rightBottom: Float): BottomMenuDialog {
        mTopLeftRadius = leftTop
        mTopRightRadius = rightTop
        mBottomLeftRadius = leftBottom
        mBottomRightRadius = rightBottom
        return this
    }

    fun setRadius(top: Float, bottom: Float): BottomMenuDialog {
        mTopLeftRadius = top
        mTopRightRadius = top
        mBottomLeftRadius = bottom
        mBottomRightRadius = bottom
        return this
    }

    fun setBackgroundColor(@ColorInt color: Int): BottomMenuDialog {
        mBackgroundColor = color
        return this
    }

    fun setTitleMatchParentWidth(titleMatchParentWidth: Boolean): BottomMenuDialog {
        mTitleMatchParentWidth = titleMatchParentWidth
        return this
    }

    /**
     * @param padding dp
     */
    fun setIconPadding(padding: Float) {
        mIconPadding = padding
    }

    private fun dp(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

    fun show(): AlertDialog {
        val view = CustomLinearLayout(context)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val cancel = LayoutInflater.from(context).inflate(R.layout.bottom_menu_item, view, false)
        val textView = cancel.findViewById<AppCompatTextView>(R.id.textView)
        textView.setText(android.R.string.cancel)
        cancel.setOnClickListener { mDialog?.dismiss() }
        textView.layoutParams?.width = if (mTitleMatchParentWidth) 0 else ViewGroup.LayoutParams.WRAP_CONTENT

        val iconPadding = dp(mIconPadding)
        textView.setPadding(iconPadding.toInt(), 0, iconPadding.toInt(), 0)

        val background = GradientDrawable()
        background.setColor(mBackgroundColor)
        val leftTop = dp(mTopLeftRadius)
        val rightTop = dp(mTopRightRadius)
        val leftBottom = dp(mBottomLeftRadius)
        val rightBottom = dp(mBottomRightRadius)
        val cornerRadii = floatArrayOf(leftTop, leftTop, rightTop, rightTop, leftBottom, leftBottom, rightBottom, rightBottom)
        background.cornerRadii = cornerRadii

        view.setRadius(cornerRadii)

        view.addView(cancel)

        var findChecked = false

        for (item in mMenuItems) {
            val menu = LayoutInflater.from(context).inflate(R.layout.bottom_menu_item, view, false)
            val tv = menu.findViewById<AppCompatTextView>(R.id.textView)
            tv.layoutParams?.width = if (mTitleMatchParentWidth) 0 else ViewGroup.LayoutParams.WRAP_CONTENT
            tv.text = item.title
            tv.setPadding(iconPadding.toInt(), 0, iconPadding.toInt(), 0)
            val startIconView = menu.findViewById<AppCompatImageView>(R.id.startIcon)
            val endIconView = menu.findViewById<AppCompatImageView>(R.id.endIcon)

            item.background?.let {
                menu.background = it
            }

            item.startIcon?.let {
                startIconView.setImageDrawable(it)
                startIconView.visibility = View.VISIBLE
            }

            item.endIcon?.let {
                endIconView.setImageDrawable(it)
                endIconView.visibility = View.VISIBLE
            }

            if (!findChecked && item.checked) {
                findChecked = true
                menu.isSelected = item.checked
                tv.isSelected = item.checked
                startIconView.isSelected = item.checked
                endIconView.isSelected = item.checked
            } else {
                menu.isSelected = false
                tv.isSelected = false
                startIconView.isSelected = false
                endIconView.isSelected = false
            }

            menu.setOnClickListener {
                item.listener?.invoke(it)
                mMenuItemClickListener?.invoke(it)
            }
            view.addView(menu, 0)
        }

        val builder = AlertDialog.Builder(context, R.style.BottomMenuStyle)
                .setView(view)
                .setOnDismissListener { mDialog = null }

        return builder.create().also {
            it.window?.let { win ->
                win.setBackgroundDrawable(background)
                win.setDimAmount(0.4f)
                win.setGravity(Gravity.BOTTOM)
            }
            it.show()
            mDialog = it
        }
    }

    class MenuItem(
            val title: CharSequence,
            val startIcon: Drawable?,
            val endIcon: Drawable?,
            val background: Drawable?,
            val checked: Boolean = false,
            val listener: ((view: View) -> Unit)?
    ) {

        class Builder(private val context: Context) {

            private lateinit var title: CharSequence
            private var startIcon: Drawable? = null
            private var endIcon: Drawable? = null
            private var background: Drawable? = null
            private var checked: Boolean = false
            private var listener: ((view: View) -> Unit)? = null

            fun setTitle(title: CharSequence): Builder {
                this.title = title
                return this
            }

            fun setTitle(@StringRes titleResId: Int): Builder {
                this.title = context.getString(titleResId)
                return this
            }

            fun setStartIcon(startIcon: Drawable): Builder {
                this.startIcon = startIcon
                return this
            }

            fun setStartIcon(@DrawableRes startIconResId: Int): Builder {
                this.startIcon = ResourcesCompat.getDrawable(context.resources, startIconResId, context.theme)
                return this
            }

            fun setEndIcon(endIcon: Drawable): Builder {
                this.endIcon = endIcon
                return this
            }

            fun setEndIcon(@DrawableRes endIconResId: Int): Builder {
                this.endIcon = ResourcesCompat.getDrawable(context.resources, endIconResId, context.theme)
                return this
            }

            fun setBackground(background: Drawable): Builder {
                this.background = background
                return this
            }

            fun setBackground(@DrawableRes backgroundResId: Int): Builder {
                this.background = ResourcesCompat.getDrawable(context.resources, backgroundResId, context.theme)
                return this
            }

            fun setChecked(checked: Boolean): Builder {
                this.checked = checked
                return this
            }

            fun setListener(listener: ((view: View) -> Unit)): Builder {
                this.listener = listener
                return this
            }

            fun build(): MenuItem {
                return MenuItem(title, startIcon, endIcon, background, checked, listener)
            }
        }
    }

    private class CustomLinearLayout : LinearLayout {
        private var mPath = Path()
        private var mRadii: FloatArray? = null

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

        @Suppress("UNUSED_PARAMETER")
        private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
            orientation = VERTICAL
        }

        fun setRadius(radii: FloatArray) {
            mRadii = radii
        }

        override fun dispatchDraw(c: Canvas?) {
            val canvas = c ?: return
            mRadii?.also {
                canvas.save()
                mPath.rewind()
                mPath.addRoundRect(0f, 0f, width.toFloat(), height.toFloat(), it, Path.Direction.CW)
                canvas.clipPath(mPath)
                super.dispatchDraw(canvas)
                canvas.restore()
            } ?: run {
                super.dispatchDraw(canvas)
            }
        }
    }

}