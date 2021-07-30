package com.scaffold.ui.register

import androidx.lifecycle.MutableLiveData
import com.scaffold.base.AppViewModel
import com.scaffold.network.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import com.scaffold.model.Error
import com.scaffold.model.toError

@HiltViewModel
class RegisterViewModel @Inject constructor() : AppViewModel() {

    companion object {
        const val ERROR_GET_MSG_CODE_FAILED = 1
        const val ERROR_REGISTER_FAILED = 2
    }

    val liveMsgCode = MutableLiveData<Pair<String, String>>()
    val liveRegisterResult = MutableLiveData<User>()

    fun registerByPhone(
        phone: String,
        password: String,
        msgCode: String,
        handleId: String? = null
    ) {
        launch {
            async {
                val params = HashMap<String, Any>()
                val success = Random.nextBoolean()
                if (success) {
                    liveRegisterResult.postValue(User())
                } else {
                    liveError.postValue(ERROR_REGISTER_FAILED.toError())
                }
            }
        }
    }

    fun getMsgCode(phone: String) {
        launch {
            async {
                val params = HashMap<String, Any>()
                val success = Random.nextBoolean()
                if (success) {
                    liveMsgCode.postValue(phone to "2343565")
                } else {
                    liveError.postValue(ERROR_GET_MSG_CODE_FAILED.toError())
                }
            }
        }
    }

}