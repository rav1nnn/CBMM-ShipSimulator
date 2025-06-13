package com.cbmm.shipsimulator.data.model

data class Port(
    val id: String,
    val name: String,
    val location: Location,
    val totalDocks: Int,
    val availableDocks: Int,
    val containerCapacity: Int,
    val currentContainers: Int,
    val country: String,
    val timeZone: String
) {
    val dockUtilization: Float
        get() = if (totalDocks > 0) {
            (totalDocks - availableDocks).toFloat() / totalDocks
        } else 0f
    
    val containerUtilization: Float
        get() = if (containerCapacity > 0) {
            currentContainers.toFloat() / containerCapacity
        } else 0f
    
    val isFull: Boolean
        get() = availableDocks == 0
}
