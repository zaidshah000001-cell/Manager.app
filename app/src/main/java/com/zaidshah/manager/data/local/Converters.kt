package com.zaidshah.manager.data.local

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun dateToTimestamp(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun fromLocalDateString(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun localDateToString(date: LocalDate?): String? = date?.toString()
}
