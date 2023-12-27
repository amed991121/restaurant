package com.savent.restaurant.ui.screen.sales

import com.mazenrashed.printooth.data.printable.Printable
import com.savent.restaurant.ui.model.sale.SaleModel
import com.savent.restaurant.ui.model.share_by.ShareReceiptByModel

data class SalesState (
    val sales: List<SaleModel> = listOf(),
    val isLoading: Boolean = false,
    val shareReceiptMethods: List<ShareReceiptByModel> = listOf(),
)