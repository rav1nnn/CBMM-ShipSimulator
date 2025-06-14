package com.cbmm.shipsimulator.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*
import com.google.gson.annotations.SerializedName

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
    @SerializedName("id")
    val id: String = UUID.randomUUID().toString(),
    @SerializedName("shipId")
    val shipId: String,
    @SerializedName("startLocation")
    val startLocation: Location,
    @SerializedName("endLocation")
    val endLocation: Location,
    @SerializedName("waypoints")
    val waypoints: List<Location>,
    @SerializedName("startTime")
    val startTime: Long = System.currentTimeMillis(),
    @SerializedName("endTime")
    val endTime: Long = System.currentTimeMillis(),
    @SerializedName("status")
    val status: ShipRouteStatus
)

enum class ShipRouteStatus {
    @SerializedName("PLANNED")
    PLANNED,
    @SerializedName("IN_PROGRESS")
    IN_PROGRESS,
    @SerializedName("COMPLETED")
    COMPLETED,
    @SerializedName("CANCELLED")
    CANCELLED
}
