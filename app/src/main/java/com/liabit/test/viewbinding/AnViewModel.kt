package com.liabit.test.viewbinding

import android.app.Application
import android.util.Log
import android.widget.Toast

class AnViewModel(application: Application) : BaseViewModel(application) {

    fun test() {
        Log.d("TTTT", "AnViewModel test() is invoked!!!")
        Toast.makeText(getApplication(), "AnViewModel test invoke", Toast.LENGTH_SHORT).show()
    }

}