package com.savent.restaurant.domain.usecase

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.PaymentMethod
import com.savent.restaurant.ui.model.payment_method.PaymentMethodModel
import com.savent.restaurant.utils.PaymentMethodNames

class PaymentMethodUseCase {

    companion object{
        fun getList(): List<PaymentMethodModel> {
            val methods = PaymentMethod.values()
            val paymentMethods = mutableListOf<PaymentMethodModel>()
            methods.forEach { method ->
                paymentMethods.add(
                    when (method) {
                        PaymentMethod.Credit -> PaymentMethodModel(
                            R.drawable.ic_credit_card,
                            PaymentMethodNames.CREDIT,
                            method
                        )
                        PaymentMethod.Cash -> PaymentMethodModel(
                            R.drawable.ic_money,
                            PaymentMethodNames.CASH,
                            method
                        )
                        PaymentMethod.Debit -> PaymentMethodModel(
                            R.drawable.ic_credit_card,
                            PaymentMethodNames.DEBIT,
                            method
                        )
                        PaymentMethod.Transfer -> PaymentMethodModel(
                            R.drawable.ic_bank1,
                            PaymentMethodNames.TRANSFER,
                            method
                        )
                    }
                )
            }
            return paymentMethods
        }
        fun get(method: PaymentMethod): PaymentMethodModel {
            return when (method) {
                PaymentMethod.Credit -> PaymentMethodModel(
                    R.drawable.ic_credit_card,
                    PaymentMethodNames.CREDIT,
                    method
                )
                PaymentMethod.Cash -> PaymentMethodModel(
                    R.drawable.ic_money,
                    PaymentMethodNames.CASH,
                    method
                )
                PaymentMethod.Debit -> PaymentMethodModel(
                    R.drawable.ic_credit_card,
                    PaymentMethodNames.DEBIT,
                    method
                )
                PaymentMethod.Transfer -> PaymentMethodModel(
                    R.drawable.ic_bank1,
                    PaymentMethodNames.TRANSFER,
                    method
                )
            }
        }
    }

}