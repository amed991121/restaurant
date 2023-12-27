package com.savent.restaurant.ui.model.share_by

import androidx.annotation.DrawableRes
import com.savent.restaurant.ui.model.SharedReceipt

class ShareReceiptByModel(
    @DrawableRes val resId: Int,
    val name: String,
    val method: SharedReceipt.Method
)