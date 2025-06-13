package com.cbmm.shipsimulator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa uma localização geográfica com latitude e longitude.
 * Pode ser usada para representar a posição atual de um navio ou de um porto.
 */
data class Location(
    @PrimaryKey
    val id: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
