package com.savent.restaurant.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.savent.restaurant.data.common.model.*
import com.savent.restaurant.data.local.database.dao.*
import com.savent.restaurant.data.local.model.KitchenNote
import com.savent.restaurant.data.local.model.OrderEntity
import com.savent.restaurant.data.local.model.Printer
import com.savent.restaurant.utils.Converters

@Database(
    entities = [
        Company::class,
        Restaurant::class,
        DiningRoom::class,
        Table::class,
        Diner::class,
        Debt::class,
        Dish::class,
        OrderEntity::class,
        Payment::class,
        Printer::class,
        KitchenNote::class
    ],
    version = 1
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun companyDao(): CompanyDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun dinerDao(): DinerDao
    abstract fun tableDao(): TableDao
    abstract fun diningRoomDao(): DiningRoomDao
    abstract fun dishDao(): DishDao
    abstract fun orderDao(): OrderDao
    abstract fun debtDao(): DebtDao
    abstract fun paymentDao(): PaymentDao
    abstract fun printerDao(): PrinterDao
    abstract fun kitchenNoteDao(): KitchenNoteDao
}