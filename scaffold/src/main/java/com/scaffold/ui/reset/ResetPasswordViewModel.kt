package com.scaffold.ui.reset


import androidx.lifecycle.MutableLiveData
import com.scaffold.base.AppViewModel
import com.scaffold.model.toError
import com.scaffold.network.model.User
import com.scaffold.ui.register.RegisterViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ResetPasswordViewModel @Inject constructor() : AppViewModel() {

    companion object {
        const val ERROR_GET_MSG_CODE_FAILED = 1
    }

    val liveResetResult = MutableLiveData<Boolean>()
    val liveMsgCode = MutableLiveData<Pair<String, String>>()

    fun retrieveByPhone(
        phone: String,
        password: String,
        msgCode: String,
        handleId: String? = null
    ) {
        launch {
            async {
                val params = HashMap<String, Any>()
                val success = Random.nextBoolean()
                liveResetResult.postValue(success)
            }
        }
    }

    fun getMsgCode(phone: String) {
        launch {
            async {
                val params = HashMap<String, Any>()
                val success = Random.nextBoolean()
                if (success) {
                    liveMsgCode.postValue(phone to "235547457")
                } else {
                    liveError.postValue(ERROR_GET_MSG_CODE_FAILED.toError())
                }
            }
        }
    }


}