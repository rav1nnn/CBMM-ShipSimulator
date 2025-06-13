package com.cbmm.shipsimulator.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "ship_routes",
    foreignKeys = [
        ForeignKey(
            entity = Ship::class,
            parentColumns = ["id"],
            childColumns = ["shipId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("shipId")]
)
data class ShipRoute(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val shipId: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val speed: Double,
    val heading: Double
)
