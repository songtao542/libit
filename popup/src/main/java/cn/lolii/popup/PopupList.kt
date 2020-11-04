package cn.lolii.popup

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.*
import android.widget.ImageView.ScaleType
import androidx.appcompat.content.res.AppCompatResources
import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
class PopupList {
    private val context: Context
    private var popupWindow: PopupWindow? = null
    private var height = ViewGroup.LayoutParams.WRAP_CONTENT
    private var width = ViewGroup.LayoutParams.WRAP_CONTENT

    /**
     * -2 == LayoutParams.WRAP_CONTENT;
     * -1 == LayoutParams.MATCH_PARENT;
     * 0 == Absolutely dpi
     */
    private var widthMode = -2
    private var adapter: DropMenuAdapter = DropMenuAdapter()
    private lateinit var list: ListView
    private lateinit var maskView: View
    private lateinit var root: FrameLayout
    private var itemHeight = LinearLayout.LayoutParams.WRAP_CONTENT
    private var textColor = ColorStateList.valueOf(Color.BLACK)
    private var textSize = 15

    @SuppressLint("RtlHardcoded")
    private var textGravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
    private val menuItems: ArrayList<MenuItem> = ArrayList()
    private var onMenuItemClickListener: OnMenuItemClickListener? = null
    private var checkedPosition = -1
    private var checkedTitle: String? = null
    private var showMask = false

    constructor(context: Context) : super() {
        this.context = context
        init()
    }

    constructor(context: Context, arrayResId: Int) : super() {
        this.context = context
        init()
        initItems(listOf(*context.resources.getStringArray(arrayResId)))
    }

    constructor(context: Context, strings: List<String>) : super() {
        this.context = context
        init()
        initItems(strings)
    }

    fun setList(strings: List<String>) {
        initItems(strings)
    }

    fun clear() {
        menuItems.clear()
    }

    private fun initItems(items: List<String>?) {
        items?.let {
            for (item in it) {
                add(item)
            }
        }
    }

    private fun init() {
        root = FrameLayout(context)
        val pr = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        root.layoutParams = pr
        maskView = View(context)
        val pb = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        maskView.layoutParams = pb
        maskView.setBackgroundColor(-0x60cccccd)
        root.addView(maskView)
        list = ListView(context)
        val p = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        list.layoutParams = p
        list.setBackgroundColor(-0x1)
        list.setFooterDividersEnabled(false)
        list.setHeaderDividersEnabled(false)
        list.divider = ColorDrawable(-0x19191a)
        list.dividerHeight = 1
        list.adapter = adapter
        list.onItemClickListener = AdapterView.OnItemClickListener { _: AdapterView<*>?, view: View, position: Int, _: Long ->
            popupWindow?.dismiss()
            checkedPosition = position
            val menuItem = (view as MenuItemLayout).menuItem
            menuItem.isChecked = true
            for (item in menuItems) {
                if (menuItem === item) {
                    continue
                }
                item.isChecked = false
            }
            adapter.notifyDataSetChanged()
            if (onMenuItemClickListener != null) {
                onMenuItemClickListener!!.onOptionsItemSelected(menuItem)
            }
        }
        root.addView(list)
    }

    /**
     * 设置选择效果
     *
     * @param selector the selector
     */
    fun setItemSelector(selector: Int) {
        list.setSelector(selector)
    }

    /**
     * 设置选择效果
     *
     * @param textColor the text color
     */
    fun setItemTextColor(textColor: Int) {
        this.textColor = AppCompatResources.getColorStateList(context, textColor)
    }

    /**
     * 设置背景
     *
     * @param resId the resource id
     */
    fun setBackgroundResource(resId: Int) {
        list.setBackgroundResource(resId)
    }

    interface OnDismissListener {
        fun onDismiss()
    }

    private var onDismissListener: OnDismissListener? = null

    fun setOnDismissListener(onDismissListener: OnDismissListener?) {
        this.onDismissListener = onDismissListener
    }

    /**
     * 设置背景
     *
     * @param color default white
     */
    fun setBackgroundColor(color: Int) {
        list.setBackgroundColor(color)
    }

    /**
     * 设置Divider
     *
     * @param drawable 设置颜色可使用如下方法：setDivider(new ColorDrawable(color))
     */
    fun setDivider(drawable: Drawable?) {
        list.divider = drawable
        list.dividerHeight = 1
    }

    /**
     * 设置Divider
     *
     * @param drawable      设置颜色可使用如下方法：setDivider(new ColorDrawable(color))
     * @param dividerHeight 高度
     */
    fun setDivider(drawable: Drawable?, dividerHeight: Int) {
        list.divider = drawable
        list.dividerHeight = dp2px(dividerHeight)
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
        list.isVerticalScrollBarEnabled = scrollBarEnabled
    }

    /**
     * @param position the checked position
     */
    fun setCheckedPosition(position: Int) {
        checkedPosition = position
        setupMenuCheck()
    }

    /**
     * 根据显示内容设置position
     *
     * @param title the checked title
     */
    fun setCheckedPosition(title: String) {
        checkedTitle = title
        setupMenuCheck()
    }

    /**
     * 添加菜单项
     */
    fun setMenu(menuItems: List<MenuItem>?) {
        if (menuItems.isNullOrEmpty()) {
            return
        }
        this.menuItems.clear()
        this.menuItems.addAll(menuItems)
        setupMenuCheck()
    }

    private fun setupMenuCheck() {
        var position = -1
        if (checkedPosition != -1 && checkedPosition < menuItems.size) {
            position = checkedPosition
        } else if (checkedTitle != null) {
            for (i in menuItems.indices) {
                if (checkedTitle == menuItems[i].title) {
                    position = i
                    break
                }
            }
        }
        if (position != -1) {
            for (i in menuItems.indices) {
                val item = menuItems[i]
                item.isChecked = i == position
            }
        } else {
            // 确保只有一个被选中
            var checkedItem: MenuItem? = null
            for (i in menuItems.indices) {
                val item = menuItems[i]
                if (checkedItem == null && item.isChecked) {
                    checkedItem = item
                } else {
                    item.isChecked = false
                }
            }
        }
    }

    /**
     * 添加菜单项
     */
    fun add(resId: Int, title: String) {
        val menuItem = MenuItem(resId, title)
        menuItem.mPosition = menuItems.size
        menuItems.add(menuItem)
    }

    fun add(title: String) {
        val menuItem = MenuItem(title)
        menuItem.mPosition = menuItems.size
        menuItems.add(menuItem)
    }

    fun add(resId: Int) {
        val menuItem = MenuItem(resId)
        menuItem.mPosition = menuItems.size
        menuItems.add(menuItem)
    }

    /**
     * add menu item 之前设置，否则不会生效
     *
     * @param color default black
     */
    fun setTextColor(color: Int) {
        textColor = ColorStateList.valueOf(color)
    }

    fun setTextSize(textSize: Int) {
        this.textSize = textSize
    }

    /**
     * @param gravity Gravity.CENTER,Gravity.CENTER_HORIZONTAL,Gravity.LEFT,Gravity.RIGHT
     */
    @SuppressLint("RtlHardcoded")
    fun setTextGravity(gravity: Int) {
        when (gravity) {
            Gravity.CENTER -> {
                textGravity = Gravity.CENTER
            }
            Gravity.CENTER_HORIZONTAL -> {
                textGravity = Gravity.CENTER
            }
            Gravity.LEFT -> {
                textGravity = Gravity.CENTER_HORIZONTAL or Gravity.LEFT
            }
            Gravity.RIGHT -> {
                textGravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            }
        }
    }

    /**
     * unit dip
     *
     * @param dip the unit is dip
     */
    fun setHeight(dip: Int) {
        height = dp2px(dip)
    }

    /**
     * @param dip    单位是否采用 TypedValue.COMPLEX_UNIT_DIP
     * @param height 高度
     */
    fun setHeight(dip: Boolean, height: Int) {
        if (dip) {
            this.height = dp2px(height)
        } else {
            this.height = height
        }
    }

    fun setFullScreenWidth() {
        setWidth(false, context.resources.displayMetrics.widthPixels)
    }

    /**
     * @param dip the unit is dip
     */
    fun setWidth(dip: Int) {
        width = dp2px(dip)
        widthMode = 0
    }

    /**
     * @param dip   单位是否采用 TypedValue.COMPLEX_UNIT_DIP
     * @param width 宽度
     */
    fun setWidth(dip: Boolean, width: Int) {
        if (dip) {
            this.width = dp2px(width)
        } else {
            this.width = width
        }
        widthMode = 0
    }

    fun setShowMask(showMask: Boolean) {
        this.showMask = showMask
    }

    /**
     * @param height   单位dp， 每一行的高度
     * @param showSize 显示多少行
     */
    fun setItemHeight(height: Int, showSize: Int) {
        itemHeight = dp2px(height)
        this.height = showSize * itemHeight + 5
    }

    private fun createPopupWindow() {
        if (widthMode == ViewGroup.LayoutParams.WRAP_CONTENT) {
            val paint = TextPaint()
            paint.textSize = sp2px(textSize).toFloat()
            for (menuItem in menuItems) {
                var twidth = dp2px(60)
                if (menuItem.title != null) {
                    twidth += paint.measureText(menuItem.title).toInt()
                }
                if (twidth > width) {
                    width = twidth
                }
            }
        }
        popupWindow = PopupWindow(root, width, height, true).also {
            it.setBackgroundDrawable(ColorDrawable(0x00ffffff))
            it.isOutsideTouchable = true
            it.animationStyle = 0
            it.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            it.setOnDismissListener {
                onDismissListener?.onDismiss()
            }
        }
        if (!showMask) {
            maskView.visibility = View.GONE
        }
        maskView.setOnClickListener { popupWindow?.dismiss() }
    }

    /**
     * @param anchor  the view on which to pin the DripDownListView
     * @param gravity [Gravity] just Gravity.LEFT,Gravity.RIGHT has effect ,other will be as Gravity.CENTER;
     * @see PopupList.show
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
     * @param gravity [Gravity] just Gravity.LEFT,Gravity.RIGHT has effect ,other will be as Gravity.CENTER;
     * @param yoffset    dip
     */
    @SuppressLint("RtlHardcoded")
    fun show(anchor: View, gravity: Int, yoffset: Int) {
        adapter.notifyDataSetChanged()
        list.setSelection(checkedPosition)
        val xx = anchor.width.toFloat()
        if (popupWindow == null) {
            createPopupWindow()
        }
        val yoff = dp2px(yoffset)
        when (gravity) {
            Gravity.LEFT -> {
                popupWindow?.showAsDropDown(anchor, 0, yoff)
            }
            Gravity.RIGHT -> {
                popupWindow?.showAsDropDown(anchor, (xx - width).toInt(), yoff)
            }
            else -> {
                popupWindow?.showAsDropDown(anchor, (xx - width).toInt() / 2, yoff)
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
        adapter.notifyDataSetChanged()
        if (popupWindow == null) {
            createPopupWindow()
        }
        popupWindow?.showAsDropDown(anchor)
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
        adapter.notifyDataSetChanged()
        if (popupWindow == null) {
            createPopupWindow()
        }
        popupWindow?.showAtLocation(parent, gravity, x, y)
    }

    class MenuItem {
        var mPosition = 0
        var iconResId = 0
        var tailIconResId = 0
        var isChecked = false

        /**
         * @return menu item title
         */
        var title: String? = null

        constructor(iconResId: Int, title: String?, tailIconResId: Int) {
            this.iconResId = iconResId
            this.title = title
            this.tailIconResId = tailIconResId
        }

        constructor(iconResId: Int, title: String?) : this(iconResId, title, 0)

        constructor(iconResId: Int) : this(iconResId, null)

        constructor(title: String?) : this(0, title)
    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    private fun sp2px(sp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.resources.displayMetrics).toInt()
    }

    private inner class MenuItemLayout(context: Context, menuItem: MenuItem, height: Int) : LinearLayout(context) {
        private val mMenuItemView: MenuItemView
        var menuItem: MenuItem
            get() = mMenuItemView.getMenuItem()
            set(menuItem) {
                mMenuItemView.setMenuItem(menuItem)
            }

        init {
            orientation = VERTICAL
            gravity = Gravity.CENTER
            mMenuItemView = MenuItemView(getContext(), menuItem, height)
            addView(mMenuItemView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))
        }
    }

    private inner class MenuItemView(context: Context, menuItem: MenuItem, height: Int) : LinearLayout(context) {
        private var mIconView: ImageView? = null
        private var mTailIconView: ImageView? = null
        private var mTitleView: TextView? = null
        private var mMenuItem: MenuItem = menuItem
        private var mHeight: Int = height

        init {
            orientation = HORIZONTAL
            setMenuItem(menuItem)
        }

        private fun addIconView(resId: Int) {
            if (mIconView == null) {
                mIconView = ImageView(context).apply {
                    val p = LayoutParams(dp2px(30), dp2px(30))
                    p.setMargins(dp2px(8), 0, 0, 0)
                    p.gravity = Gravity.CENTER_VERTICAL
                    this.layoutParams = p
                    this.scaleType = ScaleType.CENTER_INSIDE
                }
            }
            mIconView?.let {
                addView(it)
                it.setImageResource(resId)
            }
        }

        private fun addTailIconView(resId: Int) {
            if (mTailIconView == null) {
                mTailIconView = ImageView(context).apply {
                    val p = LayoutParams(dp2px(30), dp2px(30))
                    p.setMargins(dp2px(8), 0, dp2px(8), 0)
                    p.gravity = Gravity.CENTER_VERTICAL
                    this.layoutParams = p
                    this.scaleType = ScaleType.CENTER_INSIDE
                }
            }
            mTailIconView?.let {
                addView(it)
                it.setImageResource(resId)
            }
        }

        @SuppressLint("RtlHardcoded")
        private fun addTitleView(height: Int, text: String?, paddingLeft: Int) {
            if (mTitleView == null) {
                val pl = dp2px(paddingLeft)
                mTitleView = TextView(context).apply {
                    this.minHeight = dp2px(42)
                    this.setTextColor(textColor)
                    this.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
                    this.gravity = textGravity
                    val p1 = LayoutParams(LayoutParams.WRAP_CONTENT, height)
                    p1.gravity = Gravity.CENTER_VERTICAL
                    if (mMenuItem.tailIconResId == 0) {
                        p1.weight = 1f
                    }
                    this.layoutParams = p1
                    if (textGravity == Gravity.CENTER_VERTICAL or Gravity.LEFT) {
                        this.setPadding(if (pl == 0) dp2px(8) else pl, 0, dp2px(8), 0)
                    }
                }
            }
            mTitleView?.let {
                addView(it)
                it.text = text ?: ""
            }
        }

        fun setMenuItem(menuItem: MenuItem) {
            this.mMenuItem = menuItem
            removeAllViews()
            if (menuItem.iconResId == 0) { // 只有title
                addTitleView(mHeight, menuItem.title, 25)
            } else if (menuItem.title == null) { // 只有Icon
                addIconView(menuItem.iconResId)
            } else if (menuItem.iconResId != 0 && menuItem.title != null) {
                addIconView(menuItem.iconResId)
                addTitleView(mHeight, menuItem.title, 0)
            }
            if (menuItem.tailIconResId != 0) {
                addTailIconView(menuItem.tailIconResId)
            }
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
        this.onMenuItemClickListener = onMenuItemClickListener
    }

    interface OnMenuItemClickListener {
        fun onOptionsItemSelected(item: MenuItem?)
    }

    private inner class DropMenuAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return menuItems.size
        }

        override fun getItem(position: Int): Any {
            return menuItems[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            val menuItem = menuItems[position]
            if (view == null) {
                view = MenuItemLayout(context, menuItem, itemHeight)
                if (parent.width > 0) {
                    view.layoutParams = AbsListView.LayoutParams(parent.width, itemHeight)
                } else {
                    parent.viewTreeObserver.addOnGlobalLayoutListener(ListViewLayoutListener(parent, view, itemHeight))
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
}