package com.savent.restaurant.domain.usecase

import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.PaymentMethod
import com.savent.restaurant.data.repository.*
import com.savent.restaurant.toLong
import com.savent.restaurant.ui.model.Contact
import com.savent.restaurant.ui.model.SharedReceipt
import com.savent.restaurant.utils.*


class GetReceiptToSendUseCase(
    private val sessionRepository: SessionRepository,
    private val ordersRepository: OrdersRepository,
    private val dishesRepository: DishesRepository,
    private val dinersRepository: DinersRepository,
    private val tablesRepository: TablesRepository,
    private val companiesRepository: CompaniesRepository,
    private val restaurantsRepository: RestaurantsRepository
) {
    suspend operator fun invoke(orderId: Int, method: SharedReceipt.Method): Resource<SharedReceipt> {

        val session = sessionRepository.getSession().let {
            if (it is Resource.Error)
                return Resource.Error(Message.StringResource(R.string.get_session_error))
            (it as Resource.Success).data
        }

        val order = ordersRepository.getOrderByRemoteId(orderId).let {
            if (it is Resource.Error)
                return Resource.Error(Message.StringResource(R.string.get_orders_error))
            (it as Resource.Success).data
        }

        val dishes = order.dishes

        val diner = dinersRepository.getDiner(order.dinerId).let {
            if (it is Resource.Success)
                it.data
            else null
        }

        val table = tablesRepository.getTable(order.tableId).let {
            if (it is Resource.Error)
                return Resource.Error(Message.StringResource(R.string.get_tables_error))
            (it as Resource.Success).data
        }

        val tableName = NameFormat.format(table.name)

        val company = companiesRepository.getCompany(session.companyId).let {
            if (it is Resource.Error)
                return Resource.Error(Message.StringResource(R.string.get_companies_error))
            (it as Resource.Success).data
        }

        val restaurant = restaurantsRepository.getRestaurant(
            id = session.restaurantId,
            companyId = session.companyId
        ).let {
            if (it is Resource.Error)
                return Resource.Error(Message.StringResource(R.string.get_restaurants_error))
            (it as Resource.Success).data
        }

        val subtotal = order.subtotal
        val collected = order.collected
        val total = order.total
        val change = if (collected < total) 0F else collected - total
        val isCreditSale = collected < total
        val discounts = order.discounts

        val payMethod = when (order.paymentMethod) {
            PaymentMethod.Credit -> "Tarjeta de Crédito"
            PaymentMethod.Cash -> "Efectivo"
            PaymentMethod.Debit -> "Tarjeta de Débito"
            PaymentMethod.Transfer -> "Transferencia Electrónica"
        }

        val descriptionLength = 22
        val priceLength = 10
        val totalLength = descriptionLength + priceLength
        var totalUnits = 0
        val productsStrBuilder = StringBuilder()
        dishes.forEach {
            val product = dishesRepository.getDish(it.key).let { dish ->
                if (dish is Resource.Error)
                    return Resource.Error(Message.StringResource(R.string.get_dishes_error))
                (dish as Resource.Success).data
            }
            val name = product.name
            val price = product.price
            totalUnits += it.value
            val productStr = "${it.value.toString().padEnd(3)}${name}"
            var count = 0
            while (count * descriptionLength < productStr.length) {
                val start = count * descriptionLength
                val end = start + descriptionLength
                val str = try {
                    productStr.subSequence(start, end)
                } catch (e: Exception) {
                    productStr.subSequence(start, productStr.length).padEnd(descriptionLength)
                }
                count++
                if (count * descriptionLength >= productStr.length) {
                    productsStrBuilder.append(str)
                    continue
                }
                productsStrBuilder.append("$str \n")

            }
            productsStrBuilder.append("$${price * it.value}".padStart(priceLength) + "\n")

        }

        val subtotalStrBuilder = StringBuilder("")
        if (subtotal != total) {
            subtotalStrBuilder.append("Subtotal:".padEnd(descriptionLength))
            subtotalStrBuilder.append(
                "$${DecimalFormat.format(subtotal)}".padStart(priceLength) + "\n"
            )
            if (discounts > 0) {
                subtotalStrBuilder.append("Descuentos:".padEnd(descriptionLength))
                subtotalStrBuilder.append(
                    "-$${DecimalFormat.format(discounts)}".padStart(priceLength) + "\n"
                )
            }
            subtotalStrBuilder.append("******************************** \n")
        }

        /*var receiptFooter =
            session.receiptFooter?.replace("\r", "")*/
        //if (receiptFooter.isNullOrBlank())
        val receiptFooter = "Gracias por su visita".alignCenter(totalLength) + " \n" +
                "Vuelva Pronto \uD83D\uDC4B".alignCenter(totalLength)

        val note = ("\uD83E\uDDFE Recibo de Compra \n".alignCenter(totalLength) +
                "\uD83C\uDFDB ${NameFormat.format(company.name)}".alignCenter(
                    totalLength
                ) + " \n" +
                "\uD83D\uDCCD ${NameFormat.format(restaurant.name)}".alignCenter(
                    totalLength
                ) + " \n" +
                "\uD83C\uDF7D️${NameFormat.format(tableName)} ".alignCenter(
                    totalLength
                ) + " \n" +
                "\uD83D\uDDD3 ${
                    DateFormat.format(
                        order.date_timestamp.toLong(),
                        "dd/MM/yyyy hh:mm a"
                    )
                } ".alignCenter(totalLength) + " \n" +
                "******************************** \n" +
                "Cant. Descripcion        Importe \n" +
                "******************************** \n" +
                productsStrBuilder.toString() +
                "******************************** \n" +
                "$totalUnits".padEnd(3) + "Arcticulos \n" +
                "******************************** \n" +
                subtotalStrBuilder.toString() +
                "Total:".padEnd(descriptionLength) +
                "$${DecimalFormat.format(total)}".padStart(priceLength) + "\n" +
                "Su Pago:".padEnd(descriptionLength) +
                "$${DecimalFormat.format(collected)}".padStart(priceLength) + "\n" +
                "Su Cambio:".padEnd(descriptionLength) +
                "$${DecimalFormat.format(change)}".padStart(priceLength) + "\n" +
                "******************************** \n" +
                "Método de Pago:".alignCenter(totalLength) + " \n" +
                payMethod.alignCenter(totalLength) + " \n" +
                "******************************** \n" +
                if (isCreditSale) " * Venta a crédito * \n".alignCenter(totalLength)
                        + "******************************** \n"
                else {
                    ""
                } +
                if (diner != null) {
                    "Firma del Cliente:".alignCenter(totalLength) + " \n \n \n" +
                            //"******************** \n" +
                            diner.name.alignCenter(totalLength).truncate(totalLength) + " \n" +
                            "******************************** \n"
                } else "" + receiptFooter).uppercase()


        val contact = diner?.let { Contact(it.phoneNumber, it.email) }

        return Resource.Success(SharedReceipt(note, contact, method))
    }

    private fun String.alignCenter(totalLength: Int): String {
        val lenStart = (totalLength - length) / 2
        return if (lenStart > 0) padStart(lenStart + length)
        else this
    }

    private fun String.truncate(totalLength: Int): String {
        return if (length <= totalLength) this
        else substring(0, totalLength - 1)
    }
}