package com.cbmm.shipsimulator.data.mapper

import com.cbmm.shipsimulator.data.local.entity.ShipEntity
import com.cbmm.shipsimulator.data.model.Location
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.data.model.ShipType
import com.google.gson.Gson

private val gson = Gson()

fun Ship.toEntity(): ShipEntity {
    return ShipEntity(
        id = id,
        name = name,
        type = type.name,
        status = status.name,
        capacity = capacity.toDouble(),
        currentLoad = currentLoad.toDouble(),
        currentLocation = currentLocation.toString(),
        destination = destination.toString(),
        speed = speed,
        heading = heading,
        lastUpdated = lastUpdated
    )
}

fun ShipEntity.toModel(): Ship {
    return Ship(
        id = id,
        name = name,
        type = ShipType.valueOf(type),
        status = ShipStatus.valueOf(status),
        capacity = capacity.toInt(),
        currentLoad = currentLoad.toInt(),
        currentLocation = Location.fromString(currentLocation) ?: Location(0.0, 0.0),
        destination = Location.fromString(destination) ?: Location(0.0, 0.0),
        speed = speed,
        heading = heading,
        lastUpdated = lastUpdated
    )
}

fun List<Ship>.toEntities(): List<ShipEntity> = map { it.toEntity() }

fun List<ShipEntity>.toModels(): List<Ship> = map { it.toModel() } 