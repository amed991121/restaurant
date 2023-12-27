package com.savent.restaurant.data.repository

import com.savent.restaurant.R
import com.savent.restaurant.data.local.model.Printer
import com.savent.restaurant.data.local.database.dao.PrinterDao
import com.savent.restaurant.utils.Message
import com.savent.restaurant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrinterRepositoryImpl(private val printerDao: PrinterDao) : PrinterRepository {

    override suspend fun getPrinter(location: Printer.Location): Printer? =
        withContext(Dispatchers.IO) {
            printerDao.getPrinter(location)
        }

    override suspend fun savePrinter(printer: Printer): Resource<Int> =
        withContext(Dispatchers.IO) {
            val result = printerDao.upsertPrinter(printer)
            if (result > 0)
                return@withContext Resource.Success(result.toInt())
            Resource.Error(
                Message.StringResource(R.string.save_printer_error)
            )
        }

    override suspend fun deletePrinter(location: Printer.Location): Resource<Int> =
        withContext(Dispatchers.IO) {
            val result = printerDao.deletePrinter(location)
            if (result > 0) return@withContext Resource.Success(result)
            Resource.Error(
                Message.StringResource(R.string.delete_printer_error)
            )
        }

}