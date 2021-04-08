package com.liabit.addsub

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import com.liabit.addsub.R

/**
 * Author:         songtao
 * CreateDate:     2020/9/18 14:54
 */

@Suppress("unused", "MemberVisibilityCanBePrivate")
class AddSubView : LinearLayout, TextWatcher {

    companion object {
        private const val TAG = "AddSubView"

        @JvmStatic
        var MAX_VALUE = Int.MAX_VALUE
    }

    private lateinit var mAddButton: ImageView
    private lateinit var mSubButton: ImageView
    private lateinit var mNumEditor: EditText
    private lateinit var mNumTextView: TextView

    private var mNotifyChangeWhenActionDone: Boolean = false

    private val mEditorRect = Rect()
    private var mMin: Int? = null
    private var mMax: Int? = null
    private var mValue: Int = 0
    private var mOnValueChangedListener: ((view: AddSubView, value: Int, causeByEdit: Boolean) -> Unit)? = null
    private var mOnValueOutOfRangeListener: ((view: AddSubView, value: Int) -> Unit)? = null
    private var mOnEmptyListener: ((view: AddSubView) -> Unit)? = null
    private var mOnDialogActionListener: ((which: Int, value: Int) -> Unit)? = null
    private var mTextClickListener: OnClickListener? = null
    private var mShowEditDialog = false
    private var mShowEditDialogOptButton = false

    private var mDialogTheme = R.style.AddAndSubEditDialog
    private var mDialog: AlertDialog? = null

    private var mAddIcon: Drawable? = null
    private var mSubIcon: Drawable? = null
    private var mEditDialogEditTextBackground: Drawable? = null
    private var mEditDialogEditTextStrokeColor: Int = Color.TRANSPARENT

    private var mDialogTitle: CharSequence? = null

    private var mImm: InputMethodManager? = null
    private var mIgnoreZeroWhenShowInputDialog = true
    private var mAllowOutOfRange = false

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
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.add_sub_view, this, true)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            focusable = View.FOCUSABLE
        }
        isFocusableInTouchMode = true
        mImm = getSystemService(context, InputMethodManager::class.java)

        mAddButton = findViewById(R.id.addButton)
        mSubButton = findViewById(R.id.subButton)
        mNumEditor = findViewById(R.id.numEditor)
        mNumTextView = findViewById(R.id.numberTextView)

        mAddIcon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_num_add, context.theme)
        mSubIcon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_num_sub, context.theme)
        var editable = true
        var editTextBackground: Drawable? = null
        var editTextWidth = 0f
        var editTextHeight = 0f
        var iconSize = 0f
        var iconPadding = 0f
        var textColor: ColorStateList? = null
        var textColorHint: ColorStateList? = null

        var value = 0

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AddSubView, defStyleAttr, defStyleRes)
            mShowEditDialog = typedArray.getBoolean(R.styleable.AddSubView_showEditDialog, false)
            mShowEditDialogOptButton = typedArray.getBoolean(R.styleable.AddSubView_showEditDialogOptButton, false)
            mEditDialogEditTextBackground = typedArray.getDrawable(R.styleable.AddSubView_editDialogEditTextBackground)
            mEditDialogEditTextStrokeColor = typedArray.getColor(R.styleable.AddSubView_editDialogEditTextStrokeColor, Color.TRANSPARENT)
            mDialogTheme = typedArray.getResourceId(R.styleable.AddSubView_editDialogTheme, mDialogTheme)
            mDialogTitle = typedArray.getString(R.styleable.AddSubView_editDialogTitle)
            mAddIcon = typedArray.getDrawable(R.styleable.AddSubView_addIcon) ?: mAddIcon
            mSubIcon = typedArray.getDrawable(R.styleable.AddSubView_subIcon) ?: mSubIcon
            mIgnoreZeroWhenShowInputDialog = typedArray.getBoolean(R.styleable.AddSubView_ignoreZeroWhenShowInputDialog, true)
            mAllowOutOfRange = typedArray.getBoolean(R.styleable.AddSubView_allowEditOutOfRange, false)
            editable = if (mShowEditDialog) false else typedArray.getBoolean(R.styleable.AddSubView_editable, true)
            editTextBackground = typedArray.getDrawable(R.styleable.AddSubView_editBackground)
            editTextWidth = typedArray.getDimension(R.styleable.AddSubView_editWidth, 0f)
            editTextHeight = typedArray.getDimension(R.styleable.AddSubView_editHeight, 0f)
            textColor = typedArray.getColorStateList(R.styleable.AddSubView_android_textColor)
            textColorHint = typedArray.getColorStateList(R.styleable.AddSubView_android_textColorHint)
            iconSize = typedArray.getDimension(R.styleable.AddSubView_iconSize, 0f)
            iconPadding = typedArray.getDimension(R.styleable.AddSubView_iconPadding, 0f)
            val iconColorList = typedArray.getColorStateList(R.styleable.AddSubView_iconColor)
                    ?: ResourcesCompat.getColorStateList(resources, R.color.add_sub_icon_color, context.theme)
            if (iconColorList != null) {
                mAddIcon?.setTintList(iconColorList)
                mSubIcon?.setTintList(iconColorList)
            }
            if (typedArray.hasValue(R.styleable.AddSubView_minValue)) {
                mMin = typedArray.getInt(R.styleable.AddSubView_minValue, 0)
            }
            if (typedArray.hasValue(R.styleable.AddSubView_maxValue)) {
                val max = typedArray.getInt(R.styleable.AddSubView_maxValue, MAX_VALUE)
                mMax = if (max > MAX_VALUE) MAX_VALUE else max
            }
            if (typedArray.hasValue(R.styleable.AddSubView_value)) {
                value = typedArray.getInt(R.styleable.AddSubView_value, 0)
            }
            typedArray.recycle()
        }

        mAddIcon?.let { mAddButton.setImageDrawable(it) }
        mSubIcon?.let { mSubButton.setImageDrawable(it) }

        setValueInner(value)
        setupEditTextFilter(mMax ?: MAX_VALUE, mNumEditor)
        mNumEditor.setText(value.toString())
        mNumTextView.text = value.toString()
        mNumEditor.isEnabled = editable
        editTextBackground?.let { drawable ->
            mNumEditor.background = drawable
        }
        var lp = mNumEditor.layoutParams
        if (lp != null) {
            if (editTextWidth > 0) {
                lp.width = editTextWidth.toInt()
            }
            if (editTextHeight > 0) {
                lp.height = editTextHeight.toInt()
            }
        } else {
            val defaultWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, context.resources.displayMetrics).toInt()
            val w: Int = if (editTextWidth > 0) editTextWidth.toInt() else defaultWidth
            val h: Int = if (editTextHeight > 0) editTextHeight.toInt() else defaultWidth / 5 * 3
            lp = LayoutParams(w, h)
        }
        mNumEditor.layoutParams = lp

        var tlp = mNumTextView.layoutParams
        if (tlp != null) {
            if (editTextWidth > 0) {
                tlp.width = editTextWidth.toInt()
            }
            if (editTextHeight > 0) {
                tlp.height = editTextHeight.toInt()
            }
        } else {
            val defaultWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, context.resources.displayMetrics).toInt()
            val w: Int = if (editTextWidth > 0) editTextWidth.toInt() else defaultWidth
            val h: Int = if (editTextHeight > 0) editTextHeight.toInt() else defaultWidth / 5 * 3
            tlp = LayoutParams(w, h)
        }
        mNumTextView.layoutParams = tlp

        if (textColor != null) {
            mNumEditor.setTextColor(textColor)
            mNumTextView.setTextColor(textColor)
        }
        if (textColorHint != null) {
            mNumEditor.setHintTextColor(textColorHint)
        } else {
            mNumEditor.textColors?.let { textColors ->
                mNumEditor.setHintTextColor(textColors)
            }
        }
        if (iconSize > 0) {
            val size = iconSize.toInt()
            var alp = mAddButton.layoutParams
            if (alp != null) {
                alp.width = size
                alp.height = size
            } else {
                alp = LayoutParams(size, size)
            }
            mAddButton.layoutParams = alp
            var slp = mSubButton.layoutParams
            if (slp != null) {
                slp.width = size
                slp.height = size
            } else {
                slp = LayoutParams(size, size)
            }
            mSubButton.layoutParams = slp
        }
        if (iconPadding > 0) {
            val padding = iconPadding.toInt()
            mAddButton.setPadding(padding, padding, padding, padding)
            mSubButton.setPadding(padding, padding, padding, padding)
        }

        mAddButton.setOnClickListener {
            if (!isEnabled) {
                return@setOnClickListener
            }
            mMax?.let {
                if (mValue < it) {
                    setValue(mValue + 1, false)
                    mOnValueChangedListener?.invoke(this, mValue, false)
                }
            }
        }
        mSubButton.setOnClickListener {
            if (!isEnabled) {
                return@setOnClickListener
            }
            mMin?.let {
                if (mValue > it) {
                    setValue(mValue - 1, false)
                    mOnValueChangedListener?.invoke(this, mValue, false)
                }
            }
        }
        mNumEditor.addTextChangedListener(this)
        mNumEditor.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = v.text ?: return@setOnEditorActionListener true
                setAndCheckValue(text.toString(), updateTextView = false, actionDone = true)
                hideSoftInputFromWindow(v)
            }
            return@setOnEditorActionListener true
        }

        configEditDialog()
    }

    private fun configEditDialog() {
        if (mShowEditDialog) {
            mNumEditor.isEnabled = false
            mNumEditor.visibility = View.GONE
            mNumTextView.visibility = View.VISIBLE
            mNumTextView.setOnClickListener {
                if (isEnabled) {
                    showDialog()
                }
            }
        }
    }

    private fun hideSoftInputFromWindow(v: View) {
        val view = (context as? Activity)?.currentFocus ?: v
        mImm?.hideSoftInputFromWindow(view.windowToken, 0)
        v.clearFocus()
        view.clearFocus()
        requestFocus()
    }

    fun setDialogTitle(title: CharSequence) {
        mDialogTitle = title
    }

    fun setDialogTitle(titleResId: Int) {
        if (titleResId != 0) {
            mDialogTitle = context.getString(titleResId)
        }
    }

    fun showDialog(): Dialog? {
        try {
            return showDialogUnsafe()
        } catch (e: Throwable) {
            Log.e(TAG, "showDialog error: ", e)
        }
        return null
    }

    private fun showDialogUnsafe(): Dialog? {
        val ctx = context ?: return null
        if (ctx is Activity) {
            if (ctx.isFinishing || ctx.isDestroyed) {
                return null
            }
        }
        var dialog: AlertDialog? = mDialog
        if (dialog != null) {
            return dialog
        }
        val context = ContextThemeWrapper(ctx, mDialogTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.add_sub_edit_dialog, null)
        val contentView = view.findViewById<View>(R.id.addSubContent)

        val dialogContentLayout = view.findViewById<FrameLayout>(R.id.dialogContentLayout)
        val editText = view.findViewById<EditText>(R.id.editText)
        val addButton = view.findViewById<ImageView>(R.id.addButton)
        val subButton = view.findViewById<ImageView>(R.id.subButton)

        if (!mShowEditDialogOptButton) {
            addButton.visibility = View.GONE
            subButton.visibility = View.GONE
            editText.minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160f, context.resources.displayMetrics).toInt()
            editText.setBackgroundColor(Color.TRANSPARENT)
            if (mEditDialogEditTextBackground != null) {
                dialogContentLayout.background = mEditDialogEditTextBackground
            } else {
                val bg = ResourcesCompat.getDrawable(context.resources, R.drawable.add_sub_dialog_round_corner_content_background, context.theme)
                /*val ty = context.obtainStyledAttributes(intArrayOf(android.R.attr.colorPrimary))
                val colorPrimary = ty.getColor(0, Int.MIN_VALUE)
                if (colorPrimary != Int.MIN_VALUE) {
                    bg?.setTint(colorPrimary)
                }
                ty.recycle()*/
                if (mEditDialogEditTextStrokeColor != Color.TRANSPARENT) {
                    bg?.setTint(mEditDialogEditTextStrokeColor)
                }
                dialogContentLayout.background = bg
            }
        } else {
            addButton.visibility = View.VISIBLE
            subButton.visibility = View.VISIBLE
            editText.minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70f, context.resources.displayMetrics).toInt()
            editText.setBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.add_sub_editor_bg_color, context.theme))
            dialogContentLayout.setBackgroundResource(R.drawable.add_sub_dialog_content_background)
            contentView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    contentView.clipToOutline = true
                    contentView.outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View?, outline: Outline?) {
                            view?.let {
                                var radius = it.context.resources.getDimension(R.dimen.add_sub_dialog_content_bg_radius) - 1
                                if (radius < 0) {
                                    radius = 0f
                                }
                                outline?.setRoundRect(0, 0, it.width, it.height, radius)
                            }
                        }
                    }
                    contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }

        setupEditTextFilter(mMax ?: MAX_VALUE, editText)
        var value = mValue
        mMax?.let {
            if (value > it) {
                value = it
            }
        }
        mMin?.let {
            if (value < it) {
                value = it
            }
        }
        mMax?.let { max ->
            addButton?.isEnabled = value < max
        }
        mMin?.let { min ->
            subButton?.isEnabled = value > min
        }
        if (!mIgnoreZeroWhenShowInputDialog || value != 0) {
            editText.setText(value.toString())
        }
        editText.addTextChangedListener(onTextChanged = { text: CharSequence?, _, _, _ ->
            val txt = text?.toString() ?: return@addTextChangedListener
            txt.toLongOrNull()?.also {
                mMin?.let { min ->
                    mMax?.let { max ->
                        addButton?.isEnabled = it < max
                    }
                    subButton?.isEnabled = it > min
                }
                mMax?.let { max ->
                    addButton?.isEnabled = it < max
                    mMin?.let { min ->
                        subButton?.isEnabled = it > min
                    }
                }
                // 最大输入长度限制为 MAX_VALUE 的长度，即最多输入10个字符，所以这里转换成 Long 类型一定不会出错
                val number = if (it > MAX_VALUE) MAX_VALUE else it.toInt()
                val min = mMin
                val max = mMax
                if (min != null && max != null && min <= max && (number < min || number > max)) {
                    val updatedValue = if (mAllowOutOfRange) {
                        number
                    } else {
                        if (number < min) min else max
                    }
                    notifyOutOfRangeOrUpdateText(number, updatedValue, editText)
                    return@addTextChangedListener
                } else if (min != null && max == null && number < min) {
                    val updatedValue = if (mAllowOutOfRange) number else min
                    notifyOutOfRangeOrUpdateText(number, updatedValue, editText)
                    return@addTextChangedListener
                } else if (min == null && max != null && number > max) {
                    val updatedValue = if (mAllowOutOfRange) number else max
                    notifyOutOfRangeOrUpdateText(number, updatedValue, editText)
                    return@addTextChangedListener
                }
                if (it > MAX_VALUE) {
                    updateTextWithoutNotify(number.toString(), editText)
                }
            } ?: run {
                subButton.isEnabled = false
                addButton.isEnabled = false
            }
        })
        mAddIcon?.let { addButton?.setImageDrawable(it) }
        mSubIcon?.let { subButton?.setImageDrawable(it) }
        addButton.setOnClickListener {
            editText.text?.toString()?.toIntOrNull()?.let {
                var newValue = it + 1
                mMax?.let { max ->
                    addButton?.isEnabled = newValue < max
                    mMin?.let { min ->
                        subButton?.isEnabled = newValue > min
                    }
                    if (newValue > max) {
                        newValue = max
                    }
                }
                val newText = newValue.toString()
                editText.setText(newText)
                editText.setSelection(newText.length)
            }
        }
        subButton.setOnClickListener {
            editText.text?.toString()?.toIntOrNull()?.let {
                var newValue = it - 1
                mMin?.let { min ->
                    mMax?.let { max ->
                        addButton?.isEnabled = newValue < max
                    }
                    subButton?.isEnabled = newValue > min
                    if (newValue < min) {
                        newValue = min
                    }
                }
                val newText = newValue.toString()
                editText.setText(newText)
                editText.setSelection(newText.length)
            }
        }
        dialog = AlertDialog.Builder(context, mDialogTheme)
                .setView(view)
                .setTitle(mDialogTitle ?: context.getString(R.string.add_sub_dialog_title))
                .setNegativeButton(R.string.add_sub_dialog_cancel) { d, which ->
                    d.dismiss()
                    mOnDialogActionListener?.invoke(which, mValue)
                }
                .setPositiveButton(R.string.add_sub_dialog_confirm) { d, which ->
                    d.dismiss()
                    editText?.text?.let {
                        setAndCheckValue(it.toString(), updateTextView = true, actionDone = true)
                    }
                    mOnDialogActionListener?.invoke(which, mValue)
                }
                .setOnDismissListener {
                    mDialog = null
                }
                .create()
        editText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dialog.dismiss()
                val text = v.text ?: return@setOnEditorActionListener true
                setAndCheckValue(text.toString(), updateTextView = true, actionDone = true)
                hideSoftInputFromWindow(v)
            }
            return@setOnEditorActionListener true
        }
        dialog.show()
        editText.requestFocus()
        dialog.window?.let {
            it.setLayout((context.resources.displayMetrics.widthPixels / 4f * 3f).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            it.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        mDialog = dialog
        return dialog
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val text = s?.toString() ?: return
        setAndCheckValue(text, updateTextView = false, actionDone = false)
    }

    private fun setAndCheckValue(text: String, updateTextView: Boolean, actionDone: Boolean) {
        if (text.isNotBlank() && TextUtils.isDigitsOnly(text)) {
            // 最大输入长度限制为 MAX_VALUE 的长度，即最多输入10个字符，所以这里转换成 Long 类型一定不会出错
            val num = text.toLongOrNull() ?: return
            val number = if (num > MAX_VALUE) MAX_VALUE else num.toInt()
            val min = mMin
            val max = mMax
            if (min != null && max != null && min <= max && (number < min || number > max)) {
                val updatedValue = if (mAllowOutOfRange) {
                    number
                } else {
                    if (number < min) min else max
                }
                notifyOutOfRangeOrUpdateText(number, updatedValue, mNumEditor)
                return
            } else if (min != null && max == null && number < min) {
                val updatedValue = if (mAllowOutOfRange) number else min
                notifyOutOfRangeOrUpdateText(number, updatedValue, mNumEditor)
                return
            } else if (min == null && max != null && number > max) {
                val updatedValue = if (mAllowOutOfRange) number else max
                notifyOutOfRangeOrUpdateText(number, updatedValue, mNumEditor)
                return
            }

            var notifyValueChange = if (mNotifyChangeWhenActionDone) actionDone else false
            if (number != mValue) {
                setValueInner(number)
                if (!mNotifyChangeWhenActionDone) {
                    notifyValueChange = true
                }
            }
            if (updateTextView || num > MAX_VALUE) {
                updateTextWithoutNotify(number.toString(), mNumEditor)
            }
            if (notifyValueChange) {
                mOnValueChangedListener?.invoke(this, mValue, true)
            }
        } else {
            mOnEmptyListener?.invoke(this)
            mNumEditor.hint = mValue.toString()
            mNumTextView.text = mValue.toString()
        }
    }

    private fun notifyOutOfRangeOrUpdateText(number: Int, updateToNumber: Int, editText: EditText) {
        if (mOnValueOutOfRangeListener != null) {
            mOnValueOutOfRangeListener?.invoke(this, number)
        } else {
            updateTextWithoutNotify(updateToNumber.toString(), editText)
        }
    }

    private fun updateTextWithoutNotify(text: String, editText: EditText) {
        editText.removeTextChangedListener(this)
        editText.setText(text)
        try {
            if (text.isNotBlank()) {
                editText.setSelection(text.length)
            }
        } catch (e: Throwable) {
            Log.d(TAG, "updateTextWithoutNotify error: ", e)
        }
        if (editText == mNumEditor) {
            mNumTextView.text = text
        }
        editText.addTextChangedListener(this)
    }

    fun setMinValue(min: Int) {
        mMin = min
        updateButtonState()
    }

    fun setMaxValue(max: Int) {
        mMax = if (max > MAX_VALUE) MAX_VALUE else max
        setupEditTextFilter(max, mNumEditor)
        updateButtonState()
    }

    private fun setupEditTextFilter(max: Int, editText: EditText) {
        val filters = editText.filters
        if (!filters.isNullOrEmpty()) {
            editText.filters = arrayOf(*editText.filters, InputFilter.LengthFilter(max.toString().length))
        } else {
            editText.filters = arrayOf(InputFilter.LengthFilter(max.toString().length))
        }
    }

    private fun setValueInner(value: Int) {
        mValue = value
        updateButtonState()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        mNumEditor.isEnabled = enabled
        mAddButton.isEnabled = enabled
        mSubButton.isEnabled = enabled
    }

    private fun updateButtonState() {
        if (!isEnabled) {
            return
        }
        mMax?.let {
            mAddButton.isEnabled = mValue < it
        }
        mMin?.let {
            mSubButton.isEnabled = mValue > it
        }
    }

    fun setValue(value: Int) {
        setValue(value, true)
    }

    /**
     * 是否触发监听器
     */
    fun setValue(value: Int, notify: Boolean) {
        setValueInner(value)
        if (!notify) {
            updateTextWithoutNotify(value.toString(), mNumEditor)
        } else {
            mNumEditor.setText(value.toString())
            mNumTextView.text = value.toString()
        }
    }

    fun clearEditorFocus() {
        mNumEditor.clearFocus()
        mNumEditor.isFocusableInTouchMode = false
        mNumEditor.isFocusable = false
        mNumEditor.isFocusableInTouchMode = true
        mNumEditor.isFocusable = true
    }

    fun setHint(value: Int) {
        setHint(value, true)
    }

    /**
     * 是否清空文本框
     */
    fun setHint(value: Int, clear: Boolean) {
        setValueInner(value)
        mNumEditor.textColors?.let { textColors ->
            mNumEditor.setHintTextColor(textColors)
        }
        mNumEditor.hint = "$value"
        if (clear) {
            updateTextWithoutNotify("", mNumEditor)
        }
        mNumTextView.text = "$value"
    }

    fun getValue(): Int {
        return mValue
    }

    fun setNotifyChangeWhenActionDone(notifyChangeWhenActionDone: Boolean) {
        mNotifyChangeWhenActionDone = notifyChangeWhenActionDone
    }

    fun setOnValueChangedListener(listener: ((view: AddSubView, value: Int, edited: Boolean) -> Unit)? = null) {
        mOnValueChangedListener = listener
    }

    fun setOnValueOutOfRangeListener(listener: ((view: AddSubView, value: Int) -> Unit)? = null) {
        mOnValueOutOfRangeListener = listener
    }

    fun setOnEmptyListener(listener: ((view: AddSubView) -> Unit)? = null) {
        mOnEmptyListener = listener
    }

    fun setOnValueChangedListener(listener: OnValueChangedListener?) {
        mOnValueChangedListener = if (listener != null) listener::onValueChanged else null
    }

    fun setOnValueOutOfRangeListener(listener: OnValueOutOfRangeListener?) {
        mOnValueOutOfRangeListener = if (listener != null) listener::onValueOutOfRange else null
    }

    fun setOnEmptyListener(listener: OnEmptyListener?) {
        mOnEmptyListener = if (listener != null) listener::onEmpty else null
    }

    fun setOnTextViewClickListener(listener: OnClickListener?) {
        if (listener != null) {
            mNumEditor.isEnabled = false
            mNumEditor.visibility = View.GONE
            mNumTextView.visibility = View.VISIBLE
            mNumTextView.setOnClickListener(listener)
        } else {
            configEditDialog()
        }
    }

    /**
     * @param listener which the button that was clicked
     * [android.content.DialogInterface.BUTTON_POSITIVE] if the position of the item clicked;
     * [android.content.DialogInterface.BUTTON_NEGATIVE] if the negative of the item clicked;
     */
    fun setOnDialogActionListener(listener: ((which: Int, value: Int) -> Unit)?) {
        mOnDialogActionListener = listener
    }

    fun setOnDialogActionListener(listener: OnDialogActionListener?) {
        mOnDialogActionListener = if (listener != null) listener::onAction else null
    }

    interface OnDialogActionListener {
        /**
         * @param which the button that was clicked
         * [android.content.DialogInterface.BUTTON_POSITIVE] if the position of the item clicked;
         * [android.content.DialogInterface.BUTTON_NEGATIVE] if the negative of the item clicked;
         */
        fun onAction(which: Int, value: Int)
    }

    interface OnValueChangedListener {
        fun onValueChanged(view: AddSubView, value: Int, edited: Boolean)
    }

    interface OnValueOutOfRangeListener {
        fun onValueOutOfRange(view: AddSubView, value: Int)
    }

    interface OnEmptyListener {
        fun onEmpty(view: AddSubView)
    }

}
