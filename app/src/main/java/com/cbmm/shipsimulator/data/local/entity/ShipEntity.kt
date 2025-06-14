package com.cbmm.shipsimulator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ships")
data class ShipEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val status: String,
    val capacity: Double,
    val currentLoad: Double,
    val currentLocation: String,
    val destination: String,
    val speed: Double,
    val heading: Double,
    val lastUpdated: Long
) 