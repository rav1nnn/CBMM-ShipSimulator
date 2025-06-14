package com.cbmm.shipsimulator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.cbmm.shipsimulator.data.local.converters.ShipRouteConverters

@Entity(tableName = "ship_routes")
@TypeConverters(ShipRouteConverters::class)
data class ShipRouteEntity(
    @PrimaryKey
    val id: String,
    val shipId: String,
    val startLocation: String,
    val endLocation: String,
    val waypoints: List<String>,
    val startTime: Long,
    val endTime: Long,
    val status: String
) 