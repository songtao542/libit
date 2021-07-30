package com.scaffold.ui.login

import androidx.lifecycle.MutableLiveData
import com.scaffold.base.AppViewModel
import com.scaffold.model.toError
import com.scaffold.network.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class LoginViewModel @Inject constructor() : AppViewModel() {

    companion object {
        const val ERROR_GET_MSG_CODE_FAILED = 1
        const val ERROR_LOGIN_FAILED = 2
        const val ERROR_USER_NOT_EXIST = 3
    }

    val liveMsgCode = MutableLiveData<Pair<String, String>>()
    val liveLoginResult = MutableLiveData<User>()

    /**
     * 获取验证码
     */
    fun getMsgCode(phone: String) {
        launch {
            async {
                val params = HashMap<String, Any>()
                val success = Random.nextBoolean()
                if (success) {
                    liveMsgCode.postValue(phone to "128433")
                } else {
                    liveError.postValue(ERROR_GET_MSG_CODE_FAILED.toError())
                }
            }
        }
    }

    fun loginByPhone(
        phone: String,
        password: String? = null
    ) {
        launch {
            async {
                val params = HashMap<String, Any>()
                val success = Random.nextBoolean()
                if (success) {
                    liveLoginResult.postValue(User())
                } else {
                    liveError.postValue(ERROR_LOGIN_FAILED.toError())
                }
            }
        }
    }

    fun loginByMsgCode(
        phone: String,
        msgCode: String,
        handleId: String? = null
    ) {
        launch {
            async {
                val params = HashMap<String, Any>()
                val success = Random.nextBoolean()
                if (success) {
                    liveLoginResult.postValue(User())
                } else {
                    liveError.postValue(ERROR_LOGIN_FAILED.toError())
                }
            }
        }
    }



}