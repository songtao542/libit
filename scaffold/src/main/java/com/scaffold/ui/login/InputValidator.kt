package com.scaffold.ui.login

object InputValidator {


    fun isValidPhoneNumber(number: String?): Boolean {
        return if (number.isNullOrBlank()) false else number.length > 7
    }

    fun isValidPassword(password: String?): Boolean {
        return if (password.isNullOrBlank()) false else password.length > 7
    }

}