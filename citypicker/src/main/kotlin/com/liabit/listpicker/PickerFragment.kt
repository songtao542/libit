package com.liabit.listpicker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liabit.citypicker.R
import com.liabit.listpicker.decoration.DividerItemDecoration
import com.liabit.listpicker.decoration.SectionItemDecoration
import com.liabit.listpicker.model.HotItem
import com.liabit.listpicker.model.Item
import com.liabit.listpicker.model.VariableState

@Suppress("unused")
open class PickerFragment<I : Item> : AppCompatDialogFragment(), TextWatcher, View.OnClickListener, SideIndexBar.OnIndexTouchedChangedListener, InnerListener<I>, IPicker<I> {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mEmptyView: View
    private lateinit var mSearchBox: EditText
    private lateinit var mClearAllBtn: ImageView
    private var mLoadingView: View? = null
    private var mSideIndexBar: SideIndexBar? = null

    private var mSectionItemDecoration: SectionItemDecoration<I>? = null
    private var mAdapter: ItemListAdapter<I>? = null

    private var mUserDefaultCities = true
    private var mResults: List<Item>? = null
    private var mEnableAnim = false
    private var mAnimStyle: Int = R.style.DefaultCityPickerAnimation
    private var mMultipleMode = false
    private var mSearchHint: String? = null
    private var mEnableSection = true
    private var mHotItem: HotItem<I>? = null
    private var mVariableItem: I? = null
    private var mVariableState = 0
    private var mItems: List<I>? = null
    private var mOnResultListener: OnResultListener<I>? = null
    private var mOnRequestVariableListener: OnRequestVariableListener<I>? = null
    private var mOnPickerReadyListener: OnPickerReadyListener<I>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEnableAnim = arguments?.getBoolean("cp_enable_anim") ?: true
    }

    fun setVariableItem(variableItem: I?) {
        mVariableItem = variableItem
        mVariableState = if (mVariableItem == null) VariableState.FAILURE else VariableState.LOCATING
    }

    fun setHotItem(hotItem: HotItem<I>) {
        mHotItem = hotItem
    }

    @SuppressLint("ResourceType")
    fun setAnimationStyle(@StyleRes style: Int) {
        mEnableAnim = true
        mAnimStyle = if (style <= 0) R.style.DefaultCityPickerAnimation else style
    }

    fun setMultipleMode(multipleMode: Boolean) {
        mMultipleMode = multipleMode
    }

    fun setSearchHint(searchHint: String?) {
        mSearchHint = searchHint
    }

    fun setSectionEnabled(enable: Boolean) {
        mEnableSection = enable
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.DefaultCityPickerTheme);
        //LayoutInflater themeInflater = inflater.cloneInContext(contextThemeWrapper);
        //view = themeInflater.inflate(R.layout.cp_dialog_city_picker, container, false);
        inflater.context.theme.applyStyle(R.style.DefaultCityPickerTheme, false)
        val view: View = inflater.inflate(R.layout.cp_dialog_city_picker, container, false)
        view.findViewById<View>(R.id.toolbar).setPadding(0, getStatusBarHeight(requireContext()), 0, 0)

        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        mAdapter = ItemListAdapter<I>(inflater.context, mMultipleMode).also {
            it.setInnerListener(this)
            it.setLayoutManager(layoutManager)
        }
        if (mEnableSection) {
            mSectionItemDecoration = SectionItemDecoration(inflater.context)
        }
        mRecyclerView = view.findViewById<RecyclerView>(R.id.cp_city_recyclerview).also {
            it.layoutManager = layoutManager
            it.setHasFixedSize(true)
            var index = 0
            mSectionItemDecoration?.let { sd ->
                it.addItemDecoration(sd, index++)
            }
            it.addItemDecoration(DividerItemDecoration(inflater.context), index)
            it.adapter = mAdapter
            it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    //确保定位城市能正常刷新
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        mAdapter?.refresh()
                    }
                }
            })
        }
        mEmptyView = view.findViewById(R.id.cp_empty_view)
        val overlayTextView: TextView = view.findViewById(R.id.cp_overlay)
        mLoadingView = view.findViewById(R.id.cp_loading_view)
        mSideIndexBar = view.findViewById<SideIndexBar>(R.id.cp_side_index_bar).also {
            it.setOverlayTextView(overlayTextView).setOnIndexChangedListener(this)
            it.setVariableSection(mVariableItem?.getItemSection())
            it.setHotSection(mHotItem?.getItemSection())
            it.visibility = if (mEnableSection) View.VISIBLE else View.GONE
        }
        mSearchBox = view.findViewById(R.id.cp_search_box)
        mSearchHint?.let { mSearchBox.setHint(it) }
        mSearchBox.addTextChangedListener(this)
        val confirmBtn: TextView = view.findViewById(R.id.cp_confirm)
        confirmBtn.setOnClickListener(this)
        mClearAllBtn = view.findViewById(R.id.cp_clear_all)
        mClearAllBtn.setOnClickListener(this)
        view.findViewById<View>(R.id.cp_back).setOnClickListener(this)
        if (mMultipleMode) {
            confirmBtn.visibility = View.VISIBLE
            view.findViewById<View>(R.id.cp_divider).visibility = View.VISIBLE
        } else {
            confirmBtn.visibility = View.GONE
            view.findViewById<View>(R.id.cp_divider).visibility = View.GONE
        }

        mSectionItemDecoration?.setItem(mVariableItem, mHotItem, mItems)
        mAdapter?.setItem(mVariableItem, mHotItem, mItems)

        if (mVariableItem == null && mHotItem == null && mItems.isNullOrEmpty()) {
            mLoadingView?.visibility = View.VISIBLE
        } else {
            mLoadingView?.visibility = View.GONE
        }
        return view
    }

    private fun getItems(): List<Item> {
        val items = ArrayList<Item>()
        mVariableItem?.let {
            items.add(it)
        }
        mHotItem?.let {
            items
        }
        return items
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (mVariableItem == null && mHotItem == null && mItems.isNullOrEmpty()) {
            mOnPickerReadyListener?.onPickerReady(this)
        }
    }

    @SuppressLint("NewApi", "ObsoleteSdkInt")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        val window = dialog.window
        if (window != null) {
            window.decorView.setPadding(0, 0, 0, 0)
            window.setBackgroundDrawableResource(android.R.color.transparent)
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR)
            window.setDimAmount(0f)
            layoutUnderSystemUI(window, lightStatusBar = true, false)
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
            if (mEnableAnim) {
                window.setWindowAnimations(mAnimStyle)
            }
        }
        return dialog
    }

    private fun layoutUnderSystemUI(window: Window, lightStatusBar: Boolean, lightNavigationBar: Boolean) {
        val flag = window.decorView.systemUiVisibility
        var ls = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ls = flag and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR != 0
        }
        val lightStatus = lightStatusBar || ls
        var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (lightStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val lightNavigation = lightNavigationBar || flag and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR != 0
            if (lightNavigation) {
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = flags
    }

    private fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = 0
        val res = context.resources
        val resourceId = res.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

    /**
     * 搜索框监听
     */
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        val keyword: String = s.toString()
        if (TextUtils.isEmpty(keyword)) {
            mClearAllBtn.visibility = View.GONE
            mEmptyView.visibility = View.GONE
            mSectionItemDecoration?.setItem(mVariableItem, mHotItem, mItems)
            mAdapter?.setItem(mVariableItem, mHotItem, mItems)
        } else {
            mClearAllBtn.visibility = View.VISIBLE
            val searchResult = filter(keyword)
            mSectionItemDecoration?.setItem(null, null, searchResult)
            if (searchResult.isNullOrEmpty()) {
                mEmptyView.visibility = View.VISIBLE
            } else {
                mEmptyView.visibility = View.GONE
                mAdapter?.setItem(null, null, searchResult)
            }
        }
        mRecyclerView.scrollToPosition(0)
    }

    private fun filter(keyword: String): List<I> {
        val result = ArrayList<I>()
        mVariableItem?.let {
            if (it.getItemTitle().contains(keyword) || it.getItemPinyin().contains(keyword)) {
                result.add(it)
            }
        }
        mHotItem?.let {
            for (item in it.getHotItems()) {
                if (item.getItemTitle().contains(keyword) || item.getItemPinyin().contains(keyword)) {
                    result.add(item)
                }
            }
        }
        mItems?.let {
            for (item in it) {
                if (item.getItemTitle().contains(keyword) || item.getItemPinyin().contains(keyword)) {
                    result.add(item)
                }
            }
        }
        return result
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cp_back -> {
                dismiss()
            }
            R.id.cp_confirm -> {
                dismiss()
                mOnResultListener?.onResult(mAdapter?.selected ?: emptyList())
            }
            R.id.cp_clear_all -> {
                mSearchBox.setText("")
            }
        }
    }

    override fun onIndexChanged(index: String, position: Int) {
        //滚动RecyclerView到索引位置
        mAdapter?.scrollToSection(index)
    }

    override fun onItemClick(item: I?) {
        dismiss()
        mOnResultListener?.let {
            val result = ArrayList<I>()
            if (item != null) {
                result.add(item)
            }
            it.onResult(result)
        }
    }

    override fun requestVariable() {
        mOnRequestVariableListener?.requestVariable(this)
    }

    fun setOnResultListener(listener: OnResultListener<I>?) {
        mOnResultListener = listener
    }

    fun setOnRequestLocationListener(listener: OnRequestVariableListener<I>?) {
        mOnRequestVariableListener = listener
    }

    fun setOnRequestCitiesListener(listener: OnPickerReadyListener<I>?) {
        mOnPickerReadyListener = listener
    }

    override fun updateVariable(variableItem: I?, state: Int) {
        mAdapter?.updateVariableState(variableItem, state)
    }

    override fun setItem(variableItem: I?, hotItem: HotItem<I>?, items: List<I>?) {
        if (variableItem != null || hotItem != null || items != null) {
            mLoadingView?.visibility = View.GONE
        }
        mVariableItem = variableItem
        mHotItem = hotItem
        mItems = items
        mSideIndexBar?.setVariableSection(mVariableItem?.getItemSection())
        mSideIndexBar?.setHotSection(mHotItem?.getItemSection())
        mSectionItemDecoration?.setItem(variableItem, hotItem, items)
        mAdapter?.setItem(variableItem, hotItem, items)
    }

    class Builder<I : Item> {
        private var mFragmentManager: FragmentManager? = null
        private var mTargetFragment: Fragment? = null
        private var mMultipleMode = false
        private var mSearchHint: String? = null
        private var mEnableSection = true
        private var mAnimStyle = 0
        private var mVariableItem: I? = null
        private var mHotItem: HotItem<I>? = null
        private var mItems: List<I>? = null
        private var mOnResultListener: OnResultListener<I>? = null
        private var mOnRequestVariableListener: OnRequestVariableListener<I>? = null
        private var mOnPickerReadyListener: OnPickerReadyListener<I>? = null

        fun setFragmentManager(fm: FragmentManager?): Builder<I> {
            mFragmentManager = fm
            return this
        }

        fun setTargetFragment(targetFragment: Fragment?): Builder<I> {
            mTargetFragment = targetFragment
            return this
        }

        /**
         * 设置动画效果
         *
         * @param animStyle
         * @return
         */
        fun setAnimationStyle(@StyleRes animStyle: Int): Builder<I> {
            mAnimStyle = animStyle
            return this
        }

        /**
         * 设置多选模式
         *
         * @param multipleMode
         * @return
         */
        fun setMultipleMode(multipleMode: Boolean): Builder<I> {
            mMultipleMode = multipleMode
            return this
        }

        fun setHotItem(hotItem: HotItem<I>?): Builder<I> {
            mHotItem = hotItem
            return this
        }

        fun setItem(items: List<I>?): Builder<I> {
            mItems = items
            return this
        }

        fun setSearchHint(searchHint: String): Builder<I> {
            mSearchHint = searchHint
            return this
        }

        fun setSectionEnabled(enable: Boolean): Builder<I> {
            mEnableSection = enable
            return this
        }

        /**
         * 设置选择结果的监听器
         *
         * @param listener
         * @return
         */
        fun setOnResultListener(listener: OnResultListener<I>?): Builder<I> {
            mOnResultListener = listener
            return this
        }

        /**
         * 设置选择结果的监听器
         *
         * @param listener
         * @return
         */
        fun setOnRequestVariableListener(listener: OnRequestVariableListener<I>?): Builder<I> {
            mOnRequestVariableListener = listener
            return this
        }

        /**
         * 设置选择结果的监听器
         *
         * @param listener
         * @return
         */
        fun setOnPickerReadyListener(listener: OnPickerReadyListener<I>?): Builder<I> {
            mOnPickerReadyListener = listener
            return this
        }

        fun show(): PickerFragment<I> {
            val pickerFragment = PickerFragment<I>()
            pickerFragment.setItem(mVariableItem, mHotItem, mItems)
            pickerFragment.setSearchHint(mSearchHint)
            pickerFragment.setSectionEnabled(mEnableSection)
            pickerFragment.setAnimationStyle(mAnimStyle)
            pickerFragment.setMultipleMode(mMultipleMode)
            pickerFragment.setOnResultListener(mOnResultListener)
            pickerFragment.setOnRequestLocationListener(mOnRequestVariableListener)
            pickerFragment.setOnRequestCitiesListener(mOnPickerReadyListener)
            if (mTargetFragment != null) {
                pickerFragment.setTargetFragment(mTargetFragment, 0)
            }
            mFragmentManager?.let {
                var ft = it.beginTransaction()
                val prev = it.findFragmentByTag(TAG)
                if (prev != null) {
                    ft.remove(prev).commit()
                    ft = it.beginTransaction()
                }
                ft.addToBackStack(TAG)
                pickerFragment.show(ft, TAG)
            }
            return pickerFragment
        }
    }

    companion object {
        private const val TAG = "PickerFragment"

        /**
         * 获取实例
         *
         * @param enable 是否启用动画效果
         * @return
         */
        fun <I : Item> newInstance(enable: Boolean): PickerFragment<I> {
            return PickerFragment<I>().apply {
                arguments = Bundle().apply {
                    putBoolean("cp_enable_anim", enable)
                }
            }
        }
    }
}