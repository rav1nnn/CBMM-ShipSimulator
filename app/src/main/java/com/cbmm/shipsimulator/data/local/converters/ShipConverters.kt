package com.cbmm.shipsimulator.data.local.converters

import androidx.room.TypeConverter
import com.cbmm.shipsimulator.data.model.Location
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.data.model.ShipType
import com.google.gson.Gson

class ShipConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromShipType(value: ShipType): String = value.name

    @TypeConverter
    fun toShipType(value: String): ShipType = ShipType.valueOf(value)

    @TypeConverter
    fun fromShipStatus(value: ShipStatus): String = value.name

    @TypeConverter
    fun toShipStatus(value: String): ShipStatus = ShipStatus.valueOf(value)

    @TypeConverter
    fun fromLocation(value: Location): String = gson.toJson(value)

    @TypeConverter
    fun toLocation(value: String): Location = gson.fromJson(value, Location::class.java)
} 