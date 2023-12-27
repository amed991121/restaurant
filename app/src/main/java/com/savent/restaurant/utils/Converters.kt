package com.savent.restaurant.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun toDishes(dishes: String): HashMap<Int,Int> =
        Gson().fromJson(dishes, object : TypeToken<HashMap<Int, Int>>() {}.type)

    @TypeConverter
    fun fromDishes(dishes: HashMap<Int,Int>): String =
        Gson().toJson(dishes)

    @TypeConverter
    fun toDatetime(datetime: String): DateTimeObj =
        Gson().fromJson(datetime, object : TypeToken<DateTimeObj>() {}.type)

    @TypeConverter
    fun fromDatetime(datetime: DateTimeObj): String =
        Gson().toJson(datetime)
}