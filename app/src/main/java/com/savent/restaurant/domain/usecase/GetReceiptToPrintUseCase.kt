package com.savent.restaurant.domain.usecase

import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.savent.restaurant.R
import com.savent.restaurant.data.common.model.PaymentMethod
import com.savent.restaurant.data.repository.*
import com.savent.restaurant.toLong
import com.savent.restaurant.utils.*
import com.savent.restaurant.withOutAccent
import java.text.Normalizer

class GetReceiptToPrintUseCase(
    private val sessionRepository: SessionRepository,
    private val ordersRepository: OrdersRepository,
    private val dishesRepository: DishesRepository,
    private val dinersRepository: DinersRepository,
    private val tablesRepository: TablesRepository,
    private val companiesRepository: CompaniesRepository,
    private val restaurantsRepository: RestaurantsRepository
) {
    suspend operator fun invoke(orderId: Int): Resource<ArrayList<Printable>> {

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
        val receiptFooter = "GRACIAS POR SU VISITA"

        val payMethod = when (order.paymentMethod) {
            PaymentMethod.Credit -> "CREDITO"
            PaymentMethod.Cash -> "EFECTIVO"
            PaymentMethod.Debit -> "DEBITO"
            PaymentMethod.Transfer -> "TRANSFERENCIA ELECTRÓNICA"
        }

        val printables = ArrayList<Printable>().apply {

            add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build())

            add(
                TextPrintable.Builder()
                    .setText("RECIBO DE COMPRA")
                    .setLineSpacing(10)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText(company.name.uppercase().withOutAccent())
                    .setLineSpacing(5)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText(restaurant.name.uppercase().withOutAccent())
                    .setLineSpacing(5)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText(table.name.uppercase().withOutAccent())
                    .setLineSpacing(5)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText(
                        DateFormat.format(
                            order.date_timestamp.toLong(),
                            "dd/MM/yyyy hh:mm a"
                        ).uppercase()
                    )
                    .setLineSpacing(5)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText("********************************")
                    .setLineSpacing(5)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText("CANT. DESCRIPCION        IMPORTE")
                    .setLineSpacing(5)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText("--------------------------------")
                    .setLineSpacing(5)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            val charsLengthFixed = 22
            val priceLength = 10
            var totalUnits = 0
            dishes.forEach { it->
                val product = dishesRepository.getDish(it.key).let { dish ->
                    if (dish is Resource.Error)
                        return Resource.Error(Message.StringResource(R.string.get_dishes_error))
                    (dish as Resource.Success).data
                }
                val name = product.name
                val price = product.price
                totalUnits += it.value
                val productStr = "${it.value.toString().padEnd(3)}${name}"
                    .uppercase().withOutAccent()
                var count = 0
                var newLines = 1
                while (count * charsLengthFixed < productStr.length) {
                    val start = count * charsLengthFixed
                    val end = start + charsLengthFixed
                    val str = try {
                        productStr.subSequence(start, end)
                    } catch (e: Exception) {
                        productStr.subSequence(start, productStr.length).padEnd(charsLengthFixed)
                    }
                    count++
                    if (count * charsLengthFixed >= productStr.length) newLines = 0
                    add(
                        TextPrintable.Builder()
                            .setText(str.toString())
                            .setLineSpacing(5)
                            .setNewLinesAfter(newLines)
                            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                            .build()
                    )

                }
                add(
                    TextPrintable.Builder()
                        .setText("$${price * it.value}".padStart(priceLength))
                        .setLineSpacing(5)
                        .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                        .setNewLinesAfter(1)
                        .build()
                )


            }

            add(
                TextPrintable.Builder()
                    .setText("NO. DE ARTICULOS: $totalUnits")
                    .setLineSpacing(5)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText("--------------------------------")
                    .setLineSpacing(5)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            if (subtotal != total) {
                add(
                    TextPrintable.Builder()
                        .setText("SUBTOTAL:".padEnd(charsLengthFixed))
                        .setLineSpacing(5)
                        .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                        .build()
                )

                add(
                    TextPrintable.Builder()
                        .setText(
                            "$${DecimalFormat.format(subtotal)}"
                                .padStart(priceLength)
                        )
                        .setLineSpacing(5)
                        .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                        .setNewLinesAfter(1)
                        .build()
                )

                if (discounts > 0) {
                    add(
                        TextPrintable.Builder()
                            .setText("DESCUENTOS:".padEnd(charsLengthFixed))
                            .setLineSpacing(5)
                            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                            .build()
                    )

                    add(
                        TextPrintable.Builder()
                            .setText(
                                "-$${DecimalFormat.format(discounts)}"
                                    .padStart(priceLength)
                            )
                            .setLineSpacing(5)
                            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                            .setNewLinesAfter(1)
                            .build()
                    )

                }

                add(
                    TextPrintable.Builder()
                        .setText("--------------------------------")
                        .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                        .setLineSpacing(5)
                        .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                        .setNewLinesAfter(1)
                        .build()
                )


            }

            add(
                TextPrintable.Builder()
                    .setText("TOTAL:".padEnd(charsLengthFixed))
                    .setLineSpacing(5)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText("$${DecimalFormat.format(total)}".padStart(priceLength))
                    .setLineSpacing(5)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText("SU PAGO:".padEnd(charsLengthFixed))
                    .setLineSpacing(5)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText("$${DecimalFormat.format(collected)}".padStart(priceLength))
                    .setLineSpacing(5)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText("SU CAMBIO:".padEnd(charsLengthFixed))
                    .setLineSpacing(5)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText("$${DecimalFormat.format(change)}".padStart(priceLength))
                    .setLineSpacing(5)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText("METODO DE PAGO: $payMethod")
                    .setLineSpacing(5)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )


            add(
                TextPrintable.Builder()
                    .setText("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                    .setLineSpacing(5)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )
            if (isCreditSale)
                add(
                    TextPrintable.Builder()
                        .setText("* VENTA A CREDITO *")
                        .setLineSpacing(5)
                        .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                        .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                        .setNewLinesAfter(1)
                        .build()
                )

            add(
                TextPrintable.Builder()
                    .setText("FIRMA DEL CLIENTE:")
                    .setLineSpacing(5)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            add(
                TextPrintable.Builder()
                    .setText("____________________")
                    .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                    .setNewLinesAfter(1)
                    .build()
            )

            if(diner != null){
                add(
                    TextPrintable.Builder()
                        .setText(diner.name.uppercase().withOutAccent())
                        .setLineSpacing(5)
                        .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                        .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                        .setNewLinesAfter(1)
                        .build()
                )
            }

            receiptFooter.split("\n").forEach {
                add(
                    TextPrintable.Builder()
                        .setText(it)
                        .setLineSpacing(5)
                        .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                        .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                        .setNewLinesAfter(1)
                        .build()
                )
            }
            add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build())

        }

        return Resource.Success(printables)

    }
}