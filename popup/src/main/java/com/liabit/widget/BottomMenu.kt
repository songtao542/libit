package com.liabit.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat

@Suppress("unused")
class BottomMenu(private val context: Context) {

    companion object {
        private fun dp(context: Context, dp: Float): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
        }
    }

    private var mDialog: AlertDialog? = null

    private val mMenuItems = ArrayList<MenuItem>()

    private var mMenuItemClickListener: ((menu: MenuItem) -> Unit)? = null

    private var mTopLeftRadius: Float = 0f
    private var mTopRightRadius: Float = 0f
    private var mBottomLeftRadius: Float = 0f
    private var mBottomRightRadius: Float = 0f

    private var mBackgroundColor: Int = Color.WHITE

    private var mTitleMatchParentWidth: Boolean = false
    private var mIconPadding = 8f
    private var mCancelDivider: Drawable? = null
    private var mCancelDividerHeight: Float? = null
    private var mCancelDividerMargin: Float? = null
    private var mItemHeight: Float? = null

    private var mFloating: Boolean = false

    private var mLastCheckedMenu: MenuItemView? = null

    fun menu(title: CharSequence, onClickListener: ((view: View) -> Unit)? = null): BottomMenu {
        mMenuItems.add(MenuItem(title, null, null, null, onClickListener))
        return this
    }

    fun menu(@StringRes titleResId: Int, onClickListener: ((view: View) -> Unit)? = null): BottomMenu {
        mMenuItems.add(MenuItem(context.getString(titleResId), null, null, null, onClickListener))
        return this
    }

    fun menu(@StringRes titleResId: Int, endIcon: Drawable, onClickListener: ((view: View) -> Unit)? = null): BottomMenu {
        mMenuItems.add(MenuItem(context.getString(titleResId), null, endIcon, null, onClickListener))
        return this
    }

    fun menu(@StringRes titleResId: Int, startIcon: Drawable, endIcon: Drawable, onClickListener: ((view: View) -> Unit)? = null): BottomMenu {
        mMenuItems.add(MenuItem(context.getString(titleResId), startIcon, endIcon, null, onClickListener))
        return this
    }

    fun menu(title: CharSequence, endIcon: Drawable, onClickListener: ((view: View) -> Unit)? = null): BottomMenu {
        mMenuItems.add(MenuItem(title, null, endIcon, null, onClickListener))
        return this
    }

    fun menu(title: CharSequence, startIcon: Drawable, endIcon: Drawable, onClickListener: ((view: View) -> Unit)? = null): BottomMenu {
        mMenuItems.add(MenuItem(title, startIcon, endIcon, null, onClickListener))
        return this
    }

    fun menu(@StringRes titleResId: Int, @DrawableRes endIconResId: Int, onClickListener: ((view: View) -> Unit)? = null): BottomMenu {
        val title = context.getString(titleResId)
        val endIcon = ResourcesCompat.getDrawable(context.resources, endIconResId, context.theme)
        mMenuItems.add(MenuItem(title, null, endIcon, null, onClickListener))
        return this
    }

    fun menu(@StringRes titleResId: Int, @DrawableRes startIconResId: Int, @DrawableRes endIconResId: Int, onClickListener: ((view: View) -> Unit)? = null): BottomMenu {
        val title = context.getString(titleResId)
        val startIcon = ResourcesCompat.getDrawable(context.resources, startIconResId, context.theme)
        val endIcon = ResourcesCompat.getDrawable(context.resources, endIconResId, context.theme)
        mMenuItems.add(MenuItem(title, startIcon, endIcon, null, onClickListener))
        return this
    }

    fun menu(title: CharSequence, @DrawableRes endIconResId: Int, onClickListener: ((view: View) -> Unit)? = null): BottomMenu {
        val endIcon = ResourcesCompat.getDrawable(context.resources, endIconResId, context.theme)
        mMenuItems.add(MenuItem(title, null, endIcon, null, onClickListener))
        return this
    }

    fun menu(title: CharSequence, @DrawableRes startIconResId: Int, @DrawableRes endIconResId: Int, onClickListener: ((view: View) -> Unit)? = null): BottomMenu {
        val startIcon = ResourcesCompat.getDrawable(context.resources, startIconResId, context.theme)
        val endIcon = ResourcesCompat.getDrawable(context.resources, endIconResId, context.theme)
        mMenuItems.add(MenuItem(title, startIcon, endIcon, null, onClickListener))
        return this
    }

    fun menu(menus: List<MenuItem>): BottomMenu {
        mMenuItems.addAll(menus)
        return this
    }

    fun setOnMenuItemClickListener(listener: ((menu: MenuItem) -> Unit)?): BottomMenu {
        mMenuItemClickListener = listener
        return this
    }

    fun setRadius(leftTop: Float, rightTop: Float, leftBottom: Float, rightBottom: Float): BottomMenu {
        mTopLeftRadius = leftTop
        mTopRightRadius = rightTop
        mBottomLeftRadius = leftBottom
        mBottomRightRadius = rightBottom
        return this
    }

    fun setRadius(top: Float, bottom: Float): BottomMenu {
        mTopLeftRadius = top
        mTopRightRadius = top
        mBottomLeftRadius = bottom
        mBottomRightRadius = bottom
        return this
    }

    fun setBackgroundColor(@ColorInt color: Int): BottomMenu {
        mBackgroundColor = color
        return this
    }

    fun setTitleMatchParentWidth(titleMatchParentWidth: Boolean): BottomMenu {
        mTitleMatchParentWidth = titleMatchParentWidth
        return this
    }

    fun setFloating(floating: Boolean): BottomMenu {
        mFloating = floating
        return this
    }

    /**
     * @param padding dp
     */
    fun setIconPadding(padding: Float): BottomMenu {
        mIconPadding = padding
        return this
    }

    fun setCancelDivider(divider: Drawable): BottomMenu {
        mCancelDivider = divider
        return this
    }

    fun setCancelDivider(color: Int): BottomMenu {
        mCancelDivider = ColorDrawable(color)
        return this
    }

    fun setCancelDividerMargin(margin: Float): BottomMenu {
        mCancelDividerMargin = dp(context, margin)
        return this
    }

    fun setCancelDividerHeight(height: Float): BottomMenu {
        mCancelDividerHeight = dp(context, height)
        return this
    }

    fun setCancelDividerHeightPx(height: Float): BottomMenu {
        mCancelDividerHeight = height
        return this
    }

    fun setItemHeight(height: Float): BottomMenu {
        mItemHeight = dp(context, height)
        return this
    }

    fun show(): AlertDialog {
        val view = CustomLinearLayout(context)
        val cancel = MenuItemView(context)
        cancel.setMenuItem(this, MenuItem.Builder(context).setTitle(android.R.string.cancel).build())
        cancel.setOnClickListener { mDialog?.dismiss() }

        val background = GradientDrawable()
        background.setColor(mBackgroundColor)
        val leftTop = dp(context, mTopLeftRadius)
        val rightTop = dp(context, mTopRightRadius)
        val leftBottom = dp(context, mBottomLeftRadius)
        val rightBottom = dp(context, mBottomRightRadius)
        val cornerRadii = floatArrayOf(leftTop, leftTop, rightTop, rightTop, leftBottom, leftBottom, rightBottom, rightBottom)
        background.cornerRadii = cornerRadii
        view.setRadius(cornerRadii)
        view.addView(cancel)

        mCancelDivider?.let {
            val divider = View(context)
            divider.background = it
            divider.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            mCancelDividerHeight?.let { height ->
                divider.layoutParams.height = height.toInt()
            }
            mCancelDividerMargin?.let { margin ->
                (divider.layoutParams as ViewGroup.MarginLayoutParams).let { lp ->
                    lp.marginStart = margin.toInt()
                    lp.marginEnd = margin.toInt()
                }
            }
            view.addView(divider, 0)
        }

        for (item in mMenuItems) {
            val menuView = MenuItemView(context)
            menuView.setMenuItem(this, item)
            if (item.checked) {
                mLastCheckedMenu = menuView
            }
            menuView.setOnClickListener {
                clearCheck(item)
                item.checked = (it as MenuItemView).toggle()
                if (mLastCheckedMenu != it) {
                    mLastCheckedMenu?.setChecked(false)
                }
                mLastCheckedMenu = it
                item.listener?.invoke(it)
                mMenuItemClickListener?.invoke(item)
            }
            view.addView(menuView, 0)
        }

        var winBackground: Drawable = background
        var contentView: View = view

        if (mFloating) {
            val screenWidth = context.resources.displayMetrics.widthPixels
            val bm = (screenWidth.toFloat() * 0.05f).toInt()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                winBackground = InsetDrawable(background, 0, 0, 0, bm)
            } else {
                contentView = FrameLayout(context).apply {
                    view.background = winBackground
                    addView(view)
                    setPadding(0, 0, 0, bm)
                }
            }
        }

        val builder = AlertDialog.Builder(context, if (mFloating) R.style.FloatBottomMenuStyle else R.style.BottomMenuStyle)
                .setView(contentView)
                .setOnDismissListener { mDialog = null }

        return builder.create().also {
            it.window?.let { win ->
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    win.setBackgroundDrawable(winBackground)
                }
                win.setDimAmount(0.4f)
                win.setGravity(Gravity.BOTTOM)
            }
            it.show()
            mDialog = it
        }
    }

    private class MenuItemView : ConstraintLayout {
        private lateinit var mTextView: AppCompatTextView
        private lateinit var mStartIconView: AppCompatImageView
        private lateinit var mEndIconView: AppCompatImageView

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
            val menu = LayoutInflater.from(context).inflate(R.layout.bottom_menu_item, this, true)
            mTextView = menu.findViewById(R.id.textView)
            mStartIconView = menu.findViewById(R.id.startIcon)
            mEndIconView = menu.findViewById(R.id.endIcon)
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 46f).toInt())
            val ta = context.obtainStyledAttributes(intArrayOf(android.R.attr.selectableItemBackground))
            background = ta.getDrawable(0)
            ta.recycle()
        }

        fun setMenuItem(bottomMenu: BottomMenu, menu: MenuItem) {
            bottomMenu.mItemHeight?.let {
                layoutParams?.height = it.toInt()
            }
            mTextView.layoutParams?.width = if (bottomMenu.mTitleMatchParentWidth) 0 else ViewGroup.LayoutParams.WRAP_CONTENT
            mTextView.text = menu.title
            val iconPadding = dp(context, bottomMenu.mIconPadding)
            mTextView.setPadding(iconPadding.toInt(), 0, iconPadding.toInt(), 0)

            menu.background?.let {
                background = it
            }

            menu.startIcon?.let {
                mStartIconView.setImageDrawable(it)
                mStartIconView.visibility = View.VISIBLE
            }

            menu.endIcon?.let {
                mEndIconView.setImageDrawable(it)
                mEndIconView.visibility = View.VISIBLE
            }

            if (menu.checked) {
                isSelected = menu.checked
                mTextView.isSelected = menu.checked
                mStartIconView.isSelected = menu.checked
                mEndIconView.isSelected = menu.checked
            } else {
                isSelected = false
                mTextView.isSelected = false
                mStartIconView.isSelected = false
                mEndIconView.isSelected = false
            }
        }

        fun toggle(): Boolean {
            val checked = !isSelected
            setChecked(checked)
            return checked
        }

        fun setChecked(checked: Boolean) {
            isSelected = checked
            mTextView.isSelected = checked
            mStartIconView.isSelected = checked
            mEndIconView.isSelected = checked
        }
    }

    private fun clearCheck(menu: MenuItem) {
        for (item in mMenuItems) {
            if (item != menu) {
                item.checked = false
            }
        }
    }

    class MenuItem(
            val title: CharSequence,
            val textColor: ColorStateList?,
            val startIcon: Drawable?,
            val endIcon: Drawable?,
            val background: Drawable?,
            internal var checked: Boolean = false,
            val listener: ((view: View) -> Unit)?
    ) {

        val isChecked: Boolean get() = checked

        constructor(
                title: CharSequence,
                startIcon: Drawable?,
                endIcon: Drawable?,
                background: Drawable?,
                listener: ((view: View) -> Unit)?
        ) : this(title, null, startIcon, endIcon, background, false, listener)

        class Builder(private val context: Context) {

            private lateinit var title: CharSequence
            private var startIcon: Drawable? = null
            private var endIcon: Drawable? = null
            private var background: Drawable? = null
            private var checked: Boolean = false
            private var listener: ((view: View) -> Unit)? = null

            private var textColor: ColorStateList? = null

            fun setTitle(title: CharSequence): Builder {
                this.title = title
                return this
            }

            fun setTitle(@StringRes titleResId: Int): Builder {
                this.title = context.getString(titleResId)
                return this
            }

            fun setTextColor(@ColorInt color: Int): Builder {
                this.textColor = ColorStateList.valueOf(color)
                return this
            }

            fun setTextColorResource(@ColorRes colorResId: Int): Builder {
                this.textColor = ResourcesCompat.getColorStateList(context.resources, colorResId, context.theme)
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
                return MenuItem(title, textColor, startIcon, endIcon, background, checked, listener)
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