package com.cbmm.shipsimulator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.cbmm.shipsimulator.data.local.converters.ShipConverters
import com.google.gson.annotations.SerializedName

@Entity(tableName = "ships")
@TypeConverters(ShipConverters::class)
data class Ship(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: ShipType,
    @SerializedName("status")
    val status: ShipStatus,
    @SerializedName("capacity")
    val capacity: Int,
    @SerializedName("currentLoad")
    val currentLoad: Int,
    @SerializedName("currentLocation")
    val currentLocation: Location,
    @SerializedName("destination")
    val destination: Location,
    @SerializedName("speed")
    val speed: Double, // in knots
    @SerializedName("heading")
    val heading: Double, // in degrees
    @SerializedName("lastUpdated")
    val lastUpdated: Long
) {
    val availableCapacity: Int
        get() = capacity - currentLoad
    
    val isDocked: Boolean
        get() = status == ShipStatus.DOCKED
    
    val isSailing: Boolean
        get() = status == ShipStatus.SAILING
}

enum class ShipType {
    @SerializedName("CONTAINER")
    CONTAINER,
    @SerializedName("BULK")
    BULK,
    @SerializedName("TANKER")
    TANKER
}

enum class ShipStatus {
    @SerializedName("SAILING")
    SAILING,
    @SerializedName("DOCKED")
    DOCKED,
    @SerializedName("LOADING")
    LOADING,
    @SerializedName("UNLOADING")
    UNLOADING,
    @SerializedName("MAINTENANCE")
    MAINTENANCE
}
