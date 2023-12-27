package com.savent.restaurant.ui.screen.sales

import com.savent.restaurant.ui.model.SharedReceipt

sealed class SalesEvent {
    class Search(val query: String): SalesEvent()
    class ShareReceipt(val orderId: Int, val method: SharedReceipt.Method): SalesEvent()
    object RemovePrintDevice: SalesEvent()
}