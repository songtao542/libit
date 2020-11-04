//package cn.lolii.popup
//
//import android.annotation.SuppressLint
//import android.app.Dialog
//import android.content.Context
//import android.content.DialogInterface
//import android.graphics.drawable.ColorDrawable
//import android.graphics.drawable.Drawable
//import android.util.TypedValue
//import android.view.Gravity
//import android.view.View
//import android.view.ViewGroup
//import android.view.ViewTreeObserver.OnGlobalLayoutListener
//import android.view.Window
//import android.widget.*
//import android.widget.ImageView.ScaleType
//import java.util.*
//
//@Suppress("unused", "MemberVisibilityCanBePrivate")
//class PopupMenu : Dialog {
//    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?)
//            : super(context, cancelable, cancelListener) {
//        init()
//    }
//
//    @JvmOverloads
//    constructor(context: Context, theme: Int, arrayResId: Int = 0) : super(context, theme) {
//        init()
//        if (arrayResId != 0) {
//            initItems(listOf(*context.resources.getStringArray(arrayResId)))
//        }
//    }
//
//    constructor(context: Context) : super(context) {
//        init()
//    }
//
//    private var height = ViewGroup.LayoutParams.WRAP_CONTENT
//    private var width = ViewGroup.LayoutParams.WRAP_CONTENT
//    private var adapter: DropMenuAdapter = DropMenuAdapter()
//    private lateinit var list: ListView
//    private var itemHeight = LinearLayout.LayoutParams.WRAP_CONTENT
//    private var textColor = -0x1000000
//
//    @SuppressLint("RtlHardcoded")
//    private var textGravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
//    private val items: ArrayList<MenuItem> = ArrayList()
//    private var onMenuItemClickListener: OnMenuItemClickListener? = null
//    private var checkedPosition = 0
//    private var checkedTitle: String? = null
//
//    fun setList(strings: List<String>) {
//        initItems(strings)
//    }
//
//    private fun initItems(items: List<String>?) {
//        items?.let {
//            for (item in it) {
//                add(item)
//            }
//        }
//    }
//
//    private fun init() {
//        adapter = DropMenuAdapter()
//        list = ListView(context)
//        val p = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        list.layoutParams = p
//        list.setBackgroundColor(-0x1)
//        list.setFooterDividersEnabled(false)
//        list.setHeaderDividersEnabled(false)
//        list.divider = ColorDrawable(-0x222223)
//        list.dividerHeight = 1
//        list.adapter = adapter
//        list.onItemClickListener = AdapterView.OnItemClickListener { _: AdapterView<*>?, view: View, position: Int, _: Long ->
//            dismiss()
//            this.checkedPosition = position
//            onMenuItemClickListener?.onOptionsItemSelected((view as MenuItemLayout).menuItem)
//        }
//
//        //this.getWindow().setBackgroundDrawable(new ColorDrawable(0xffffffff));
//        this.window?.requestFeature(Window.FEATURE_NO_TITLE)
//        this.setContentView(list)
//    }
//
//    /**
//     * 设置选择效果
//     *
//     * @param selector
//     */
//    fun setItemSelector(selector: Int) {
//        list.setSelector(selector)
//    }
//
//    /**
//     * 设置背景
//     *
//     * @param resid
//     */
//    fun setBackgroundResource(resid: Int) {
//        list.setBackgroundResource(resid)
//    }
//
//    /**
//     * 设置背景
//     *
//     * @param color default white
//     */
//    fun setBackgroundColor(color: Int) {
//        list.setBackgroundColor(color)
//    }
//
//    /**
//     * 设置Divider
//     *
//     * @param drawable 设置颜色可使用如下方法：setDivider(new ColorDrawable(color))
//     */
//    fun setDivider(drawable: Drawable?) {
//        list.divider = drawable
//        list.dividerHeight = 1
//    }
//
//    /**
//     * 设置Divider
//     *
//     * @param drawable      设置颜色可使用如下方法：setDivider(new ColorDrawable(color))
//     * @param dividerHeight 高度
//     */
//    fun setDivider(drawable: Drawable?, dividerHeight: Int) {
//        list.divider = drawable
//        list.dividerHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerHeight.toFloat(), context.resources.displayMetrics).toInt()
//    }
//
//    /**
//     * 设置Divider颜色
//     *
//     * @param color
//     */
//    fun setDividerColor(color: Int) {
//        setDivider(ColorDrawable(color))
//    }
//
//    /**
//     * 设置是否显示 scrollBar
//     *
//     * @param scrollBarEnabled
//     */
//    fun setScrollBarEnabled(scrollBarEnabled: Boolean) {
//        list.isVerticalScrollBarEnabled = scrollBarEnabled
//    }
//
//    /**
//     * @param position the checked position
//     */
//    fun setCheckedPosition(position: Int) {
//        checkedPosition = position
//        setupMenuCheck()
//    }
//
//    /**
//     * 根据显示内容设置position
//     *
//     * @param title the checked title
//     */
//    fun setCheckedPosition(title: String) {
//        checkedTitle = title
//        setupMenuCheck()
//    }
//
//    /**
//     * 添加菜单项
//     */
//    fun setMenu(menuItems: List<MenuItem>?) {
//        if (menuItems.isNullOrEmpty()) {
//            return
//        }
//        this.items.clear()
//        this.items.addAll(menuItems)
//        setupMenuCheck()
//    }
//
//    private fun setupMenuCheck() {
//        var position = -1
//        if (checkedPosition != -1 && checkedPosition < items.size) {
//            position = checkedPosition
//        } else if (checkedTitle != null) {
//            for (i in items.indices) {
//                if (checkedTitle == items[i].title) {
//                    position = i
//                    break
//                }
//            }
//        }
//        if (position != -1) {
//            for (i in items.indices) {
//                val item = items[i]
//                item.isChecked = i == position
//            }
//        } else {
//            // 确保只有一个被选中
//            var checkedItem: MenuItem? = null
//            for (i in items.indices) {
//                val item = items[i]
//                if (checkedItem == null && item.isChecked) {
//                    checkedItem = item
//                } else {
//                    item.isChecked = false
//                }
//            }
//        }
//    }
//
//    /**
//     * 添加菜单项
//     */
//    fun add(resId: Int, title: String?) {
//        val item = MenuItem(resId, title)
//        item.mPosition = items.size
//        items.add(item)
//    }
//
//    fun add(title: String?) {
//        val item = MenuItem(title)
//        item.mPosition = items.size
//        items.add(item)
//    }
//
//    fun add(resId: Int) {
//        val item = MenuItem(resId)
//        item.mPosition = items.size
//        items.add(item)
//    }
//
//    /**
//     * add menu item 之前设置，否则不会生效
//     *
//     * @param color default black
//     */
//    fun setTextColor(color: Int) {
//        textColor = color
//        list.invalidate()
//    }
//
//    /**
//     * @param gravity Gravity.CENTER,Gravity.CENTER_HORIZONTAL,Gravity.LEFT,Gravity.RIGHT
//     */
//    @SuppressLint("RtlHardcoded")
//    fun setTextGravity(gravity: Int) {
//        when (gravity) {
//            Gravity.CENTER -> {
//                textGravity = Gravity.CENTER
//            }
//            Gravity.CENTER_HORIZONTAL -> {
//                textGravity = Gravity.CENTER
//            }
//            Gravity.LEFT -> {
//                textGravity = Gravity.CENTER_HORIZONTAL or Gravity.LEFT
//            }
//            Gravity.RIGHT -> {
//                textGravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
//            }
//        }
//    }
//
//    /**
//     * unit dip
//     *
//     * @param dip unit dip
//     */
//    fun setHeight(dip: Int) {
//        height = dp2px(dip)
//    }
//
//    /**
//     * @param dip    单位是否采用 TypedValue.COMPLEX_UNIT_DIP
//     * @param height 高度
//     */
//    fun setHeight(dip: Boolean, height: Int) {
//        if (dip) {
//            this.height = dp2px(height)
//        } else {
//            this.height = height
//        }
//    }
//
//    /**
//     * unit dip
//     *
//     * @param dip
//     */
//    fun setWidth(dip: Int) {
//        width = dp2px(dip)
//    }
//
//    /**
//     * @param dip   单位是否采用 TypedValue.COMPLEX_UNIT_DIP
//     * @param width 宽度
//     */
//    fun setWidth(dip: Boolean, width: Int) {
//        if (dip) {
//            this.width = dp2px(width)
//        } else {
//            this.width = width
//        }
//    }
//
//    /**
//     * @param height   每一行的高度,单位dip
//     * @param showSize 显示多少行
//     */
//    fun setItemHeight(height: Int, showSize: Int) {
//        itemHeight = dp2px(height)
//        this.height = showSize * itemHeight + showSize - 1
//    }
//
//    /**
//     * Display the content view in a popup window anchored to the bottom-left corner of the anchor view offset by the specified x and y coordinates. If there is
//     * not enough room on screen to show the popup in its entirety, this method tries to find a parent scroll view to scroll. If no parent scroll view can be
//     * scrolled, the bottom-left corner of the popup is pinned at the top left corner of the anchor view.
//     *
//     *
//     * If the view later scrolls to move anchor to a different location, the popup will be moved correspondingly.
//     */
//    override fun show() {
//        var lp = list.layoutParams
//        if (lp == null) {
//            lp = ViewGroup.LayoutParams(width, height)
//        } else {
//            lp.width = width
//            lp.height = height
//        }
//        // getWindow().setLayout(width, height);
//        list.layoutParams = lp
//        adapter.notifyDataSetChanged()
//        super.show()
//    }
//
//    private fun dp2px(dp: Int): Int {
//        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
//    }
//
//    private fun sp2px(sp: Int): Int {
//        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.resources.displayMetrics).toInt()
//    }
//
//    class MenuItem {
//        var mPosition = 0
//        var iconResId = 0
//        var tailIconResId = 0
//        var isChecked = false
//
//        /**
//         * @return menu item title
//         */
//        var title: String? = null
//
//        constructor(iconResId: Int, title: String?, tailIconResId: Int) {
//            this.iconResId = iconResId
//            this.title = title
//            this.tailIconResId = tailIconResId
//        }
//
//        constructor(iconResId: Int, title: String?) : this(iconResId, title, 0)
//
//        constructor(iconResId: Int) : this(iconResId, null)
//
//        constructor(title: String?) : this(0, title)
//    }
//
//    private inner class MenuItemLayout(context: Context, menuItem: MenuItem, height: Int) : LinearLayout(context) {
//        private val mMenuItemView: MenuItemView
//        var menuItem: MenuItem
//            get() = mMenuItemView.getMenuItem()
//            set(menuItem) {
//                mMenuItemView.setMenuItem(menuItem)
//            }
//
//        init {
//            orientation = VERTICAL
//            gravity = Gravity.CENTER
//            mMenuItemView = MenuItemView(getContext(), menuItem, height)
//            addView(mMenuItemView)
//        }
//    }
//
//    private inner class MenuItemView(context: Context, menuItem: MenuItem, height: Int) : LinearLayout(context) {
//        private var mIconView: ImageView? = null
//        private var mTailIconView: ImageView? = null
//        private var mTitleView: TextView? = null
//        private var mMenuItem: MenuItem = menuItem
//        private var mHeight: Int = height
//
//        init {
//            orientation = HORIZONTAL
//            setMenuItem(menuItem)
//        }
//
//        private fun addIconView(resId: Int) {
//            if (mIconView == null) {
//                mIconView = ImageView(context).apply {
//                    val p = LayoutParams(dp2px(30), dp2px(30))
//                    p.setMargins(dp2px(8), 0, 0, 0)
//                    p.gravity = Gravity.CENTER_VERTICAL
//                    this.layoutParams = p
//                    this.scaleType = ScaleType.CENTER_INSIDE
//                }
//            }
//            mIconView?.let {
//                addView(it)
//                it.setImageResource(resId)
//            }
//        }
//
//        private fun addTailIconView(resId: Int) {
//            if (mTailIconView == null) {
//                mTailIconView = ImageView(context).apply {
//                    val p = LayoutParams(dp2px(30), dp2px(30))
//                    p.setMargins(dp2px(8), 0, dp2px(8), 0)
//                    p.gravity = Gravity.CENTER_VERTICAL
//                    this.layoutParams = p
//                    this.scaleType = ScaleType.CENTER_INSIDE
//                }
//            }
//            mTailIconView?.let {
//                addView(it)
//                it.setImageResource(resId)
//            }
//        }
//
//        @SuppressLint("RtlHardcoded")
//        private fun addTitleView(height: Int, text: String?, paddingLeft: Int) {
//            if (mTitleView == null) {
//                val pl = dp2px(paddingLeft)
//                mTitleView = TextView(context).apply {
//                    this.minHeight = dp2px(42)
//                    this.setTextColor(textColor)
//                    this.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
//                    this.gravity = textGravity
//                    val p1 = LayoutParams(LayoutParams.WRAP_CONTENT, height)
//                    p1.gravity = Gravity.CENTER_VERTICAL
//                    if (mMenuItem.tailIconResId == 0) {
//                        p1.weight = 1f
//                    }
//                    this.layoutParams = p1
//                    if (textGravity == Gravity.CENTER_VERTICAL or Gravity.LEFT) {
//                        this.setPadding(if (pl == 0) dp2px(8) else pl, 0, dp2px(8), 0)
//                    }
//                }
//            }
//            mTitleView?.let {
//                addView(it)
//                it.text = text ?: ""
//            }
//        }
//
//        fun setMenuItem(menuItem: MenuItem) {
//            this.mMenuItem = menuItem
//            removeAllViews()
//            if (menuItem.iconResId == 0) { // 只有title
//                addTitleView(mHeight, menuItem.title, 25)
//            } else if (menuItem.title == null) { // 只有Icon
//                addIconView(menuItem.iconResId)
//            } else if (menuItem.iconResId != 0 && menuItem.title != null) {
//                addIconView(menuItem.iconResId)
//                addTitleView(mHeight, menuItem.title, 0)
//            }
//            if (menuItem.tailIconResId != 0) {
//                addTailIconView(menuItem.tailIconResId)
//            }
//            mIconView?.isSelected = menuItem.isChecked
//            mTitleView?.isSelected = menuItem.isChecked
//            mTailIconView?.isSelected = menuItem.isChecked
//        }
//
//        fun getMenuItem(): MenuItem {
//            return mMenuItem
//        }
//    }
//
//    /**
//     * 设置菜单监听器
//     *
//     * @param onMenuItemClickListener
//     */
//    fun setOnMenuItemClickListener(onMenuItemClickListener: OnMenuItemClickListener?) {
//        this.onMenuItemClickListener = onMenuItemClickListener
//    }
//
//    interface OnMenuItemClickListener {
//        fun onOptionsItemSelected(item: MenuItem?)
//    }
//
//    private inner class DropMenuAdapter : BaseAdapter() {
//        override fun getCount(): Int {
//            return items.size
//        }
//
//        override fun getItem(position: Int): Any {
//            return items[position]
//        }
//
//        override fun getItemId(position: Int): Long {
//            return position.toLong()
//        }
//
//        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//            var view = convertView
//            if (view == null) {
//                view = MenuItemLayout(context, items[position], itemHeight)
//                if (parent.width > 0) {
//                    view.layoutParams = AbsListView.LayoutParams(parent.width, itemHeight)
//                } else {
//                    parent.viewTreeObserver.addOnGlobalLayoutListener(ListViewLayoutListener(parent, view, itemHeight))
//                }
//            } else {
//                (view as MenuItemLayout).menuItem = items[position]
//            }
//            return view
//        }
//
//        inner class ListViewLayoutListener(private val mParent: View, private val mView: View, private val mHeight: Int) : OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                mView.layoutParams = AbsListView.LayoutParams(mParent.width, mHeight)
//                mParent.viewTreeObserver.removeOnGlobalLayoutListener(this)
//            }
//        }
//    }
//}