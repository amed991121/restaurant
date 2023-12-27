package com.savent.restaurant.domain.usecase

import com.savent.restaurant.R
import com.savent.restaurant.ui.model.SharedReceipt
import com.savent.restaurant.ui.model.share_by.ShareReceiptByModel
import com.savent.restaurant.utils.ShareByNames

class ShareReceiptByUseCase {

    companion object{
        fun getList(): List<ShareReceiptByModel> {
            val methods = SharedReceipt.Method.values()
            val shareByList = mutableListOf<ShareReceiptByModel>()
            methods.forEach { method ->
                shareByList.add(
                    when (method) {
                        SharedReceipt.Method.Email -> ShareReceiptByModel(
                            R.drawable.ic_arroba,
                            ShareByNames.EMAIL,
                            method
                        )
                        SharedReceipt.Method.Sms -> ShareReceiptByModel(
                            R.drawable.ic_sms,
                            ShareByNames.SMS,
                            method
                        )
                        SharedReceipt.Method.Whatsapp -> ShareReceiptByModel(
                            R.drawable.ic_whatsapp,
                            ShareByNames.WHATSAPP,
                            method
                        )
                        SharedReceipt.Method.Printer -> ShareReceiptByModel(
                            R.drawable.ic_printer,
                            ShareByNames.PRINTER,
                            method
                        )
                    }
                )
            }
            return shareByList
        }
        fun get(method: SharedReceipt.Method): ShareReceiptByModel {
            return when (method) {
                SharedReceipt.Method.Email -> ShareReceiptByModel(
                    R.drawable.ic_arroba,
                    ShareByNames.EMAIL,
                    method
                )
                SharedReceipt.Method.Sms -> ShareReceiptByModel(
                    R.drawable.ic_sms,
                    ShareByNames.SMS,
                    method
                )
                SharedReceipt.Method.Whatsapp -> ShareReceiptByModel(
                    R.drawable.ic_whatsapp,
                    ShareByNames.WHATSAPP,
                    method
                )
                SharedReceipt.Method.Printer -> ShareReceiptByModel(
                    R.drawable.ic_printer,
                    ShareByNames.PRINTER,
                    method
                )
            }
        }
    }

}