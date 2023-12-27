package com.savent.restaurant.domain.usecase

import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.savent.restaurant.R
import com.savent.restaurant.data.repository.*
import com.savent.restaurant.toLong
import com.savent.restaurant.utils.*
import com.savent.restaurant.withOutAccent

class GetKitchenNoteUseCase(
    private val sessionRepository: SessionRepository,
    private val ordersRepository: OrdersRepository,
    private val dinersRepository: DinersRepository,
    private val tablesRepository: TablesRepository,
    private val dishesRepository: DishesRepository
) {

    suspend operator fun invoke(orderId: Int, priorities: HashMap<Int, Int>):
            Resource<ArrayList<Printable>> {

        val session = sessionRepository.getSession().let {
            if (it is Resource.Error)
                return Resource.Error(Message.StringResource(R.string.get_session_error))
            (it as Resource.Success).data
        }

        val order = ordersRepository.getOrder(orderId).let {
            if (it is Resource.Error)
                return Resource.Error(Message.StringResource(R.string.get_orders_error))
            (it as Resource.Success).data
        }

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


        val printables = ArrayList<Printable>().apply {

            add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build())

            add(
                TextPrintable.Builder()
                    .setText("PEDIDO COCINA")
                    .setLineSpacing(10)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
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
                    .setText("MESERO: ${session.employeeName.uppercase().withOutAccent()}")
                    .setLineSpacing(5)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )
            if (diner != null)
                add(
                    TextPrintable.Builder()
                        .setText("COMENSAL: ${diner.name.uppercase().withOutAccent()}")
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
                    .setText("CANT. PRODUCTO        PRIOR")
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
            val priorityLength = 5
            priorities.forEach {
                val product = dishesRepository.getDish(it.key).let { dish ->
                    if (dish is Resource.Error)
                        return Resource.Error(Message.StringResource(R.string.get_dishes_error))
                    (dish as Resource.Success).data
                }
                val name = product.name
                val priority = it.value
                val unit = order.dishes.getOrDefault(it.key, 0)
                val productStr = "${unit.toString().padEnd(3)}${name}"
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
                        .setText("$priority".padStart(priorityLength))
                        .setLineSpacing(5)
                        .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                        .setNewLinesAfter(1)
                        .build()
                )

            }
            add(
                TextPrintable.Builder()
                    .setText("********************************")
                    .setLineSpacing(5)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )

            add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build())
        }

        return Resource.Success(printables)
    }
}
