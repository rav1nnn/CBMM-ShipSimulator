package com.cbmm.shipsimulator.util

import androidx.room.TypeConverter
import com.cbmm.shipsimulator.data.model.Location
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.data.model.ShipType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromLocation(location: Location?): String? {
        return gson.toJson(location)
    }

    @TypeConverter
    fun toLocation(value: String?): Location? {
        if (value == null) return null
        val type = object : TypeToken<Location>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromShipStatus(status: ShipStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toShipStatus(value: String?): ShipStatus? {
        return value?.let { ShipStatus.valueOf(it) }
    }

    @TypeConverter
    fun fromShipType(type: ShipType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toShipType(value: String?): ShipType? {
        return value?.let { ShipType.valueOf(it) }
    }
}
