package com.liabit.popup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.transition.Fade
import android.transition.Transition
import android.util.TypedValue
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.*
import android.widget.*
import android.widget.ImageView.ScaleType
import androidx.appcompat.content.res.AppCompatResources
import java.util.*


@Suppress("unused", "MemberVisibilityCanBePrivate")
class PopupMenu {

    companion object {
        /**
         * Menu icon size of dip
         */
        private const val MENU_ICON_SIZE = 30

        /**
         * 菜单项内边距
         */
        private const val MENU_CONTENT_PADDING = 8

        /**
         * 菜单图标和文本之间的间距
         */
        private const val MENU_ICON_PADDING = 2

        /**
         * 菜单项最小高度
         */
        private const val MENU_ITEM_MIN_HEIGHT = 42
    }

    private val mContext: Context
    private var mPopupWindow: PopupWindow? = null
    private var mHeight = ViewGroup.LayoutParams.WRAP_CONTENT

    /**
     * -2 == LayoutParams.WRAP_CONTENT
     * -1 == LayoutParams.MATCH_PARENT
     *  0 == Absolutely dpi
     */
    private var mWidth = ViewGroup.LayoutParams.WRAP_CONTENT
    private var mVisibleCount = 0
    private var mLayoutAnimationEnabled = false
    private var mAdapter: DropMenuAdapter = DropMenuAdapter()
    private lateinit var mListView: ListView
    private lateinit var mMaskView: View
    private lateinit var mRootView: FrameLayout
    private var mItemHeight = LinearLayout.LayoutParams.WRAP_CONTENT
    private var mTextColor = ColorStateList.valueOf(Color.BLACK)
    private var mTextSize = 15
    private var mTextWidth = 0

    @SuppressLint("RtlHardcoded")
    private var mTextGravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
    private val mMenuItems: ArrayList<MenuItem> = ArrayList()
    private var mOnMenuItemClickListener: OnMenuItemClickListener? = null
    private var mDefaultCheckedPosition = -1
    private var mCheckedPosition = -1
    private var mCheckedTitle: String? = null
    private var mShowMask = false

    constructor(context: Context) : super() {
        mContext = context
        init()
    }

    constructor(context: Context, arrayResId: Int) : super() {
        mContext = context
        init()
        initItems(listOf(*context.resources.getStringArray(arrayResId)))
    }

    constructor(context: Context, strings: List<String>) : super() {
        mContext = context
        init()
        initItems(strings)
    }

    fun setList(strings: List<String>) {
        initItems(strings)
    }

    fun clear() {
        mMenuItems.clear()
    }

    private fun initItems(items: List<String>?) {
        items?.let {
            for (item in it) {
                add(item)
            }
        }
    }

    private fun init() {
        mRootView = FrameLayout(mContext)
        mRootView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        mMaskView = View(mContext)
        mMaskView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        //mMaskView.setBackgroundColor(0x99000000.toInt())
        mMaskView.setBackgroundColor(Color.TRANSPARENT)
        mRootView.addView(mMaskView)

        mListView = ListView(mContext)
        mListView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mListView.setBackgroundColor(Color.WHITE)
        mListView.setFooterDividersEnabled(false)
        mListView.setHeaderDividersEnabled(false)
        mListView.divider = ColorDrawable(0xffeeeeee.toInt())
        mListView.dividerHeight = 1
        mListView.adapter = mAdapter
        mListView.onItemClickListener = AdapterView.OnItemClickListener { _: AdapterView<*>?, view: View, position: Int, _: Long ->
            mPopupWindow?.dismiss()
            mCheckedPosition = position
            val menuItem = (view as MenuItemLayout).menuItem
            menuItem.setChecked(true)
            for (item in mMenuItems) {
                if (menuItem === item) {
                    continue
                }
                item.setChecked(false)
            }
            mAdapter.notifyDataSetChanged()
            mOnMenuItemClickListener?.onOptionsItemSelected(menuItem)
        }
        mRootView.addView(mListView)
    }

    /**
     * 设置选择效果
     *
     * @param selector the selector
     */
    fun setItemSelector(selector: Int) {
        mListView.setSelector(selector)
    }

    /**
     * 设置选择效果
     *
     * @param textColor the text color
     */
    fun setItemTextColor(textColor: Int) {
        mTextColor = AppCompatResources.getColorStateList(mContext, textColor)
    }

    /**
     * 设置背景
     *
     * @param resId the resource id
     */
    fun setBackgroundResource(resId: Int) {
        mListView.setBackgroundResource(resId)
    }

    interface OnDismissListener {
        fun onDismiss()
    }

    private var mOnDismissListener: OnDismissListener? = null

    fun setOnDismissListener(onDismissListener: OnDismissListener?) {
        mOnDismissListener = onDismissListener
    }

    /**
     * 设置背景
     *
     * @param color default white
     */
    fun setBackgroundColor(color: Int) {
        mListView.setBackgroundColor(color)
    }

    /**
     * 设置Divider
     *
     * @param drawable 设置颜色可使用如下方法：setDivider(new ColorDrawable(color))
     */
    fun setDivider(drawable: Drawable?) {
        mListView.divider = drawable
        mListView.dividerHeight = 1
    }

    /**
     * 设置Divider
     *
     * @param drawable      设置颜色可使用如下方法：setDivider(new ColorDrawable(color))
     * @param dividerHeight 分割线高度 单位 dip
     */
    fun setDivider(drawable: Drawable?, dividerHeight: Int) {
        mListView.divider = drawable
        mListView.dividerHeight = dp2px(dividerHeight)
    }

    /**
     * 设置Divider颜色
     *
     * @param color the divider color
     */
    fun setDividerColor(color: Int) {
        setDivider(ColorDrawable(color))
    }

    /**
     * 设置是否显示 scrollBar
     *
     * @param scrollBarEnabled scroll bar enabled
     */
    fun setScrollBarEnabled(scrollBarEnabled: Boolean) {
        mListView.isVerticalScrollBarEnabled = scrollBarEnabled
    }

    /**
     * @param position the checked position
     */
    fun setDefaultCheckedPosition(position: Int) {
        mDefaultCheckedPosition = position
        setupMenuCheck()
    }

    /**
     * @param position the checked position
     */
    fun setCheckedPosition(position: Int) {
        mCheckedPosition = position
        setupMenuCheck()
    }

    /**
     * 根据显示内容设置position
     *
     * @param title the checked title
     */
    fun setCheckedTitle(title: String) {
        mCheckedTitle = title
        setupMenuCheck()
    }

    /**
     * 添加菜单项
     */
    fun setMenu(menuItems: List<MenuItem>?) {
        if (menuItems.isNullOrEmpty()) {
            return
        }
        mMenuItems.clear()
        mMenuItems.addAll(menuItems)
        setupMenuCheck()
    }

    private fun setupMenuCheck() {
        var position = -1
        if (mCheckedPosition != -1 && mCheckedPosition < mMenuItems.size) {
            position = mCheckedPosition
        } else if (mCheckedTitle != null) {
            for (i in mMenuItems.indices) {
                if (mCheckedTitle == mMenuItems[i].title) {
                    position = i
                    break
                }
            }
        }
        if (position != -1) {
            for (i in mMenuItems.indices) {
                val item = mMenuItems[i]
                item.setChecked(i == position)
            }
        } else {
            // 确保只有一个被选中
            for (i in mMenuItems.indices) {
                val item = mMenuItems[i]
                if (mCheckedPosition == -1 && item.isChecked) {
                    mCheckedPosition = i
                    item.setChecked(true)
                } else {
                    item.setChecked(false)
                }
            }
            if (mCheckedPosition == -1
                    && mDefaultCheckedPosition >= 0
                    && mDefaultCheckedPosition < mMenuItems.size) {
                mMenuItems[mDefaultCheckedPosition].setChecked(true)
            }
        }
    }

    /**
     * 添加菜单项
     */
    fun add(resId: Int, title: String) {
        val menuItem = MenuItem(resId, title)
        menuItem.setPosition(mMenuItems.size)
        mMenuItems.add(menuItem)
    }

    fun add(title: String) {
        val menuItem = MenuItem(title)
        menuItem.setPosition(mMenuItems.size)
        mMenuItems.add(menuItem)
    }

    fun add(resId: Int) {
        val menuItem = MenuItem(resId)
        menuItem.setPosition(mMenuItems.size)
        mMenuItems.add(menuItem)
    }

    /**
     * add menu item 之前设置，否则不会生效
     *
     * @param color default black
     */
    fun setTextColor(color: Int) {
        mTextColor = ColorStateList.valueOf(color)
    }

    /**
     * @param textSize 单位 sp
     */
    fun setTextSize(textSize: Int) {
        mTextSize = textSize
    }

    /**
     * @param gravity Gravity.CENTER,Gravity.CENTER_HORIZONTAL,Gravity.LEFT,Gravity.RIGHT
     */
    @SuppressLint("RtlHardcoded")
    fun setTextGravity(gravity: Int) {
        when (gravity) {
            Gravity.CENTER -> {
                mTextGravity = Gravity.CENTER
            }
            Gravity.CENTER_HORIZONTAL -> {
                mTextGravity = Gravity.CENTER
            }
            Gravity.LEFT -> {
                mTextGravity = Gravity.CENTER_HORIZONTAL or Gravity.LEFT
            }
            Gravity.RIGHT -> {
                mTextGravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            }
        }
    }

    /**
     * @param height 单位 dip
     */
    fun setHeight(height: Int) {
        mHeight = dp2px(height)
    }

    /**
     * 宽度撑满屏幕
     */
    fun setFullScreenWidth() {
        mWidth = mContext.resources.displayMetrics.widthPixels
    }

    /**
     * @param width 单位 dip
     */
    fun setWidth(width: Int) {
        mWidth = dp2px(width)
    }

    /**
     * @param width 单位 dip
     */
    fun setTextWidth(width: Int) {
        mTextWidth = dp2px(width)
    }

    fun setShowMask(showMask: Boolean) {
        mShowMask = showMask
    }

    /**
     * @param height   单位dp， 每一行的高度
     */
    fun setItemHeight(height: Int) {
        mItemHeight = dp2px(height)
    }

    fun setVisibleItemCount(visibleCount: Int) {
        mVisibleCount = visibleCount
    }

    fun setLayoutAnimationEnabled(enabled: Boolean) {
        mLayoutAnimationEnabled = enabled
    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), mContext.resources.displayMetrics).toInt()
    }

    private fun sp2px(sp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), mContext.resources.displayMetrics).toInt()
    }

    private fun calculateWidth() {
        if (mWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            val paint = TextPaint()
            paint.textSize = sp2px(mTextSize).toFloat()
            var hasIcon = false
            var hasTail = false
            for (menuItem in mMenuItems) {
                if (menuItem.icon != 0) {
                    hasIcon = true
                }
                if (menuItem.tail != 0) {
                    hasTail = true
                }
                if (!menuItem.title.isBlank()) {
                    val width = paint.measureText(menuItem.title).toInt()
                    if (width > mTextWidth) {
                        mTextWidth = width
                    }
                }
            }
            if (mTextWidth > 0) {
                mTextWidth += dp2px(8)
            }
            mWidth = 0
            if (hasIcon) {
                mWidth += dp2px(MENU_ICON_SIZE + MENU_CONTENT_PADDING + MENU_ICON_PADDING)
            }
            if (hasTail) {
                mWidth += dp2px(MENU_ICON_SIZE + MENU_CONTENT_PADDING + MENU_ICON_PADDING)
            }
            mWidth += mTextWidth
        }
        if (mVisibleCount > 0) {
            val itemHeight = if (mItemHeight > 0) mItemHeight else dp2px(MENU_ITEM_MIN_HEIGHT)
            mHeight = itemHeight * mVisibleCount
        }
    }

    private fun createPopupWindow() {
        calculateWidth()
        mRootView.layoutParams?.width = mWidth
        mListView.layoutParams?.let {
            it.width = mWidth
            it.height = mHeight
        }
        mMaskView.visibility = if (mShowMask) View.VISIBLE else View.GONE
        val height = if (mShowMask) ViewGroup.LayoutParams.WRAP_CONTENT else mHeight

        if (mShowMask) {
            val animation = AnimationSet(false)
            val translate = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, -1f,
                    Animation.RELATIVE_TO_SELF, 0f
            )
            translate.interpolator = AccelerateDecelerateInterpolator()
            val alpha = AlphaAnimation(0f, 1f)
            animation.addAnimation(translate)
            animation.addAnimation(alpha)
            animation.duration = 230
            if (mLayoutAnimationEnabled) {
                mListView.layoutAnimation = LayoutAnimationController(animation).apply {
                    order = LayoutAnimationController.ORDER_NORMAL
                }
            }
            mListView.animation = animation
        }

        mPopupWindow = PopupWindow(mRootView, mWidth, height, true).also {
            it.setBackgroundDrawable(if (mShowMask) ColorDrawable(0x88000000.toInt()) else ColorDrawable(Color.WHITE))
            it.isOutsideTouchable = true
            it.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            it.animationStyle = R.style.PopupMenuAnimation
            it.elevation = if (mShowMask) 0f else dp2px(20).toFloat()
            it.setOnDismissListener {
                mOnDismissListener?.onDismiss()
            }
            if (mShowMask && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                it.exitTransition = Fade(Fade.OUT).setDuration(150).addListener(object : Transition.TransitionListener {
                    override fun onTransitionStart(transition: Transition?) {
                        AnimatorSet().apply {
                            play(ObjectAnimator.ofFloat(mListView, View.ALPHA, 1f, 0f))
                            play(ObjectAnimator.ofFloat(mListView, View.TRANSLATION_Y, 0f, (-mListView.height).toFloat()))
                            interpolator = AccelerateInterpolator()
                            duration = 150
                        }.start()
                    }

                    override fun onTransitionEnd(transition: Transition?) {
                    }

                    override fun onTransitionCancel(transition: Transition?) {
                    }

                    override fun onTransitionPause(transition: Transition?) {
                    }

                    override fun onTransitionResume(transition: Transition?) {
                    }
                })
            }
        }

        mMaskView.setOnClickListener { mPopupWindow?.dismiss() }
    }

    /**
     * @param anchor  the view on which to pin the DripDownListView
     * @param gravity [Gravity] just Gravity.LEFT,Gravity.RIGHT has effect ,other will be as Gravity.CENTER;
     * @see PopupMenu.show
     */
    fun show(anchor: View, gravity: Int) {
        show(anchor, gravity, 0)
    }

    /**
     * Display the content view in a popup window anchored to the bottom-left corner of the anchor view offset by the specified x and y coordinates. If there is
     * not enough room on screen to show the popup in its entirety, this method tries to find a parent scroll view to scroll. If no parent scroll view can be
     * scrolled, the bottom-left corner of the popup is pinned at the top left corner of the anchor view.
     *
     *
     * If the view later scrolls to move anchor to a different location, the popup will be moved correspondingly.
     *
     * @param anchor  the view on which to pin the DripDownListView
     * @param gravity [Gravity] just Gravity.LEFT,Gravity.RIGHT has effect ,other will be as Gravity.CENTER
     * @param yOffset    dip
     */
    @SuppressLint("RtlHardcoded")
    fun show(anchor: View, gravity: Int, yOffset: Int) {
        mAdapter.notifyDataSetChanged()
        mListView.setSelection(mCheckedPosition)
        val anchorWidth = anchor.width.toFloat()
        if (mPopupWindow == null) {
            createPopupWindow()
        }
        val yOff = dp2px(yOffset)
        when (gravity) {
            Gravity.LEFT -> {
                mPopupWindow?.showAsDropDown(anchor, 0, yOff)
            }
            Gravity.RIGHT -> {
                mPopupWindow?.showAsDropDown(anchor, (anchorWidth - mWidth).toInt(), yOff)
            }
            else -> {
                mPopupWindow?.showAsDropDown(anchor, (anchorWidth - mWidth).toInt() / 2, yOff)
            }
        }
    }

    /**
     * Display the content view in a popup window anchored to the bottom-left corner of the anchor view. If there is not enough room on screen to show the popup
     * in its entirety, this method tries to find a parent scroll view to scroll. If no parent scroll view can be scrolled, the bottom-left corner of the popup
     * is pinned at the top left corner of the anchor view.
     *
     * @param anchor the view on which to pin the DripDownListView
     */
    fun show(anchor: View) {
        mAdapter.notifyDataSetChanged()
        if (mPopupWindow == null) {
            createPopupWindow()
        }
        mPopupWindow?.showAsDropDown(anchor)
    }

    /**
     * Display the content view in a popup window at the specified location. If the popup window cannot fit on screen, it will be clipped. See
     * android.view.WindowManager.LayoutParams for more information on how gravity and the x and y parameters are related. Specifying a gravity of
     * android.view.Gravity.NO_GRAVITY is similar to specifying Gravity.LEFT | Gravity.TOP.
     *
     * @param parent  a parent view to get the [View.getWindowToken] token from
     * @param gravity the gravity which controls the placement of the popup window
     * @param x       the popup's x location offset
     * @param y       the popup's y location offset
     */
    fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
        mAdapter.notifyDataSetChanged()
        if (mPopupWindow == null) {
            createPopupWindow()
        }
        mPopupWindow?.showAtLocation(parent, gravity, x, y)
    }

    open class MenuItem {
        private var mPosition = 0
        private var mIconResId = 0
        private var mTailIconResId = 0
        private var mIsChecked = false
        private var mTitle: String = ""

        constructor(title: String) {
            mTitle = title
        }

        constructor(iconResId: Int, title: String) : this(title) {
            mIconResId = iconResId
        }

        constructor(iconResId: Int, title: String, tailIconResId: Int) : this(iconResId, title) {
            mTailIconResId = tailIconResId
        }

        constructor(iconResId: Int) {
            mIconResId = iconResId
        }

        internal fun setPosition(position: Int) {
            mPosition = position
        }

        internal fun setChecked(checked: Boolean) {
            mIsChecked = checked
        }

        /**
         * 文字
         */
        val title: String get() = mTitle

        /**
         * 头部图标
         */
        val icon: Int get() = mIconResId

        /**
         * 尾部图标
         */
        val tail: Int get() = mTailIconResId

        /**
         * 是否被选中
         */
        val isChecked: Boolean get() = mIsChecked
    }

    private inner class MenuItemLayout(context: Context, menuItem: MenuItem, height: Int, textWidth: Int) : LinearLayout(context) {
        private val mMenuItemView: MenuItemView

        init {
            orientation = VERTICAL
            gravity = Gravity.CENTER
            mMenuItemView = MenuItemView(getContext(), menuItem, height, textWidth)
            addView(mMenuItemView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))
        }

        var menuItem: MenuItem
            get() = mMenuItemView.getMenuItem()
            set(menuItem) {
                mMenuItemView.setMenuItem(menuItem)
            }
    }

    private inner class MenuItemView(context: Context, menuItem: MenuItem, height: Int, textWidth: Int) : LinearLayout(context) {
        private var mIconView: ImageView? = null
        private var mTailIconView: ImageView? = null
        private var mTitleView: TextView? = null
        private var mMenuItem: MenuItem = menuItem
        private var mHeight: Int = height
        private var mTextWidth: Int = textWidth

        init {
            orientation = HORIZONTAL
            setMenuItem(menuItem)
        }

        private fun addIconView(menuItem: MenuItem) {
            val resId = menuItem.icon
            if (resId == 0) return
            if (mIconView == null) {
                mIconView = ImageView(context).apply {
                    layoutParams = LayoutParams(dp2px(MENU_ICON_SIZE), dp2px(MENU_ICON_SIZE)).apply {
                        setMargins(dp2px(MENU_CONTENT_PADDING), 0, dp2px(MENU_ICON_PADDING), 0)
                        gravity = Gravity.CENTER_VERTICAL
                    }
                    scaleType = ScaleType.CENTER_INSIDE
                }
            }
            mIconView?.let {
                addView(it)
                it.setImageResource(resId)
            }
        }

        private fun addTailIconView(menuItem: MenuItem) {
            val resId = menuItem.tail
            if (resId == 0) return
            if (mTailIconView == null) {
                mTailIconView = ImageView(context).apply {
                    layoutParams = LayoutParams(dp2px(MENU_ICON_SIZE), dp2px(MENU_ICON_SIZE)).apply {
                        setMargins(dp2px(MENU_ICON_PADDING), 0, dp2px(MENU_CONTENT_PADDING), 0)
                        gravity = Gravity.CENTER_VERTICAL
                    }
                    scaleType = ScaleType.CENTER_INSIDE
                }
            }
            mTailIconView?.let {
                addView(it)
                it.setImageResource(resId)
            }
        }

        @SuppressLint("RtlHardcoded")
        private fun addTitleView(height: Int, menuItem: MenuItem) {
            val text = menuItem.title
            if (text.isBlank()) return
            if (mTitleView == null) {
                mTitleView = TextView(context).apply {
                    minHeight = dp2px(MENU_ITEM_MIN_HEIGHT)
                    setTextColor(mTextColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize.toFloat())
                    gravity = mTextGravity
                    val textWidth = if (mTextWidth == 0) LayoutParams.WRAP_CONTENT else mTextWidth
                    layoutParams = LayoutParams(textWidth, height).apply {
                        gravity = Gravity.CENTER_VERTICAL
                    }
                }
            }
            mTitleView?.let {
                addView(it)
                it.text = text
            }
        }

        fun setMenuItem(menuItem: MenuItem) {
            mMenuItem = menuItem
            removeAllViews()
            addIconView(menuItem)
            addTitleView(mHeight, menuItem)
            addTailIconView(menuItem)
            mIconView?.isSelected = menuItem.isChecked
            mTitleView?.isSelected = menuItem.isChecked
            mTailIconView?.isSelected = menuItem.isChecked
        }

        fun getMenuItem(): MenuItem {
            return mMenuItem
        }
    }

    /**
     * 设置菜单监听器
     *
     * @param onMenuItemClickListener 菜单监听器
     */
    fun setOnMenuItemClickListener(onMenuItemClickListener: OnMenuItemClickListener?) {
        this.mOnMenuItemClickListener = onMenuItemClickListener
    }

    interface OnMenuItemClickListener {
        fun onOptionsItemSelected(item: MenuItem)
    }

    /**
     * 下拉列表适配器
     */
    private inner class DropMenuAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return mMenuItems.size
        }

        override fun getItem(position: Int): Any {
            return mMenuItems[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            val menuItem = mMenuItems[position]
            if (view == null) {
                view = MenuItemLayout(mContext, menuItem, mItemHeight, mTextWidth)
                if (parent.width > 0) {
                    view.layoutParams = AbsListView.LayoutParams(parent.width, mItemHeight)
                } else {
                    parent.viewTreeObserver.addOnGlobalLayoutListener(ListViewLayoutListener(parent, view, mItemHeight))
                }
            } else {
                (view as MenuItemLayout).menuItem = menuItem
            }
            return view
        }

        inner class ListViewLayoutListener(private val mParent: View, private val mView: View, private val mHeight: Int) : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mView.layoutParams = AbsListView.LayoutParams(mParent.width, mHeight)
                mParent.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
    }


    private class PopupDialog(context: Context) : Dialog(context) {

        init {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        /**
         * Display the content view in a popup window anchored to the bottom-left corner of the anchor view offset by the specified x and y coordinates. If there is
         * not enough room on screen to show the popup in its entirety, this method tries to find a parent scroll view to scroll. If no parent scroll view can be
         * scrolled, the bottom-left corner of the popup is pinned at the top left corner of the anchor view.
         *
         *
         * If the view later scrolls to move anchor to a different location, the popup will be moved correspondingly.
         */
        fun show(popupList: PopupMenu) {
            popupList.calculateWidth()
            popupList.mMaskView.visibility = View.GONE
            var rlp = popupList.mRootView.layoutParams
            if (rlp == null) {
                rlp = ViewGroup.LayoutParams(popupList.mWidth, popupList.mHeight)
            } else {
                rlp.width = popupList.mWidth
                rlp.height = popupList.mHeight
            }
            popupList.mRootView.layoutParams = rlp

            var llp = popupList.mListView.layoutParams
            if (llp == null) {
                llp = ViewGroup.LayoutParams(popupList.mWidth, popupList.mHeight)
            } else {
                llp.width = popupList.mWidth
                llp.height = popupList.mHeight
            }
            popupList.mListView.layoutParams = llp

            setContentView(popupList.mRootView)
            popupList.mAdapter.notifyDataSetChanged()
            show()
            window?.attributes = window?.attributes ?: WindowManager.LayoutParams().apply {
                width = popupList.mWidth
                height = popupList.mHeight
            }
        }
    }

    /**
     * 以 Dialog 方式显示
     */
    fun showAsDialog() {
        PopupDialog(mContext).show(this)
    }

}
