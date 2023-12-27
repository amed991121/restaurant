package com.savent.restaurant.utils

import androidx.annotation.StringRes

sealed class Message {

    data class DynamicString(val value: String): Message()

    class StringResource(@StringRes val resId: Int, vararg val args: Any): Message()
}
