package com.savent.restaurant.ui.screen.login

import com.savent.restaurant.R
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource

data class Credentials(val rfc: String = "", val pin: String = ""){

    fun validate(): Resource<Int> {
        if (rfc.isEmpty())
            return Resource.Error(Message.StringResource(R.string.empty_rfc))
        if(!rfc.isLettersOrDigits()  || rfc.length < 12)
            return Resource.Error(Message.StringResource(R.string.invalid_rfc))
        if(pin.isEmpty())
            return Resource.Error(Message.StringResource(R.string.empty_pin))
        return Resource.Success(0)
    }

    private fun String.isLettersOrDigits(): Boolean = all {
        it.isLetterOrDigit()
    }
}