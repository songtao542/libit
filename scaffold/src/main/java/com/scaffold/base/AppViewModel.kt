package com.scaffold.base

import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import com.scaffold.livedata.SingleLiveData
import com.scaffold.network.Api
import com.scaffold.viewmodel.ApplicationViewModel
import java.io.File
import javax.inject.Inject

/**
 * 注意： Fragment 必须使用 @AndroidEntryPoint 注解，且装载该Fragment的Activity也必须用 @AndroidEntryPoint 注解
 * ViewModel的基类, 继承 AppViewModel 的类, 请使用 @HiltViewModel 注解标注, 否则无法完成依赖注入
 */
open class AppViewModel : ApplicationViewModel() {

    @Inject
    lateinit var api: Api

    val liveTimeError = SingleLiveData<Boolean>()
    val liveEmpty by lazy { MutableLiveData<Boolean>() }
    val liveError by lazy { MutableLiveData<Boolean>() }

    @Inject
    override fun onFinishMemberInject() {
        super.onFinishMemberInject()
    }

    @CallSuper
    override fun onCreate(context: Context) {
    }

    fun deleteFile(path: String?) {
        val filePath = path ?: return
        try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Throwable) {
            com.scaffold.util.Log.d("AppViewModel", "delete file failed: ", e)
        }
    }

    fun newParams() = newParamMap()
}
