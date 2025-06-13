package com.cbmm.shipsimulator.util

import com.cbmm.shipsimulator.R
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.data.model.ShipType
import java.text.SimpleDateFormat
import java.util.*

object FormatterUtils {
    
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    fun formatShipType(type: ShipType): String {
        return when (type) {
            ShipType.CONTAINER -> "Container Ship"
            ShipType.BULK_CARRIER -> "Bulk Carrier"
            ShipType.TANKER -> "Tanker"
            ShipType.RORO -> "Ro-Ro"
        }
    }
    
    fun formatShipStatus(status: ShipStatus): String {
        return when (status) {
            ShipStatus.DOCKED -> "Docked"
            ShipStatus.SAILING -> "Sailing"
            ShipStatus.MAINTENANCE -> "In Maintenance"
        }
    }
    
    fun formatDateTime(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
    
    fun formatSpeed(speed: Double): String {
        return "%.1f knots".format(speed)
    }
    
    fun formatDistance(distanceKm: Double): String {
        return if (distanceKm < 1) {
            "${(distanceKm * 1000).toInt()} m"
        } else {
            "%.1f km".format(distanceKm)
        }
    }
    
    fun formatUtilization(utilization: Float): String {
        return "${(utilization * 100).toInt()}%"
    }
    
    fun getShipIcon(ship: Ship): Int {
        return when (ship.type) {
            ShipType.CONTAINER -> R.drawable.ic_ship_container
            ShipType.BULK_CARRIER -> R.drawable.ic_ship_bulk
            ShipType.TANKER -> R.drawable.ic_ship_tanker
            ShipType.RORO -> R.drawable.ic_ship_roro
        }
    }
    
    fun getStatusColor(status: ShipStatus): Int {
        return when (status) {
            ShipStatus.DOCKED -> R.color.status_docked
            ShipStatus.SAILING -> R.color.status_sailing
            ShipStatus.MAINTENANCE -> R.color.status_maintenance
        }
    }
}
