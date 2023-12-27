package com.savent.restaurant.ui.model

class SharedReceipt(val note: String, val contact: Contact?, val method: Method) {
    enum class Method {
        Email, Sms, Printer, Whatsapp
    }
}