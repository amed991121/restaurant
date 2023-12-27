package com.savent.restaurant.data.repository
import com.savent.restaurant.data.local.model.Printer
import com.savent.restaurant.utils.Resource


interface PrinterRepository {

    suspend fun getPrinter(location: Printer.Location): Printer?

    suspend fun savePrinter(printer: Printer): Resource<Int>

    suspend fun deletePrinter(location: Printer.Location): Resource<Int>

}