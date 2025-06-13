package com.cbmm.shipsimulator.data.model

data class Ship(
    val id: String,
    val name: String,
    val type: ShipType,
    val status: ShipStatus,
    val capacity: Int,
    val currentLoad: Int,
    val currentLocation: Location,
    val destination: Location?,
    val speed: Double, // in knots
    val heading: Double, // in degrees
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
    CONTAINER,
    BULK_CARRIER,
    TANKER,
    RORO
}

enum class ShipStatus {
    DOCKED,
    SAILING,
    MAINTENANCE
}

data class Location(
    val latitude: Double,
    val longitude: Double,
    val portId: String? = null,
    val portName: String? = null
)
