package com.scaffold.base

import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.SavedStateHandle
import com.scaffold.viewmodel.SavedStateHandler
import com.scaffold.viewmodel.SavedStateHelper

/**
 * 注意： Fragment 必须使用 @AndroidEntryPoint 注解，且装载该Fragment的Activity也必须用 @AndroidEntryPoint 注解
 * ViewModel的基类, 继承 AppViewModel 的类, 请使用 @HiltViewModel 注解标注, 否则无法完成依赖注入
 */
open class SavedStateViewModel(private val savedStateHandle: SavedStateHandle) : AppViewModel(), SavedStateHandler by SavedStateHelper(savedStateHandle) {

    @CallSuper
    override fun onCreate(context: Context) {
        super.onCreate(context)
        onRestoreInstanceState(savedStateHandle)
    }

}