package com.cbmm.shipsimulator.data.repository

import com.cbmm.shipsimulator.data.model.Location
import com.cbmm.shipsimulator.data.model.Port
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.data.model.ShipType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FakeShipRepository @Inject constructor() : ShipRepository {
    
    private val ports = listOf(
        Port(
            id = "port_rio",
            name = "Port of Rio de Janeiro",
            location = Location(-22.8950, -43.1833, "port_rio", "Port of Rio de Janeiro"),
            totalDocks = 12,
            availableDocks = 5,
            containerCapacity = 50000,
            currentContainers = 34500,
            country = "Brazil",
            timeZone = "America/Sao_Paulo"
        ),
        Port(
            id = "port_santos",
            name = "Port of Santos",
            location = Location(-23.9608, -46.3336, "port_santos", "Port of Santos"),
            totalDocks = 15,
            availableDocks = 3,
            containerCapacity = 75000,
            currentContainers = 68000,
            country = "Brazil",
            timeZone = "America/Sao_Paulo"
        ),
        Port(
            id = "port_rotterdam",
            name = "Port of Rotterdam",
            location = Location(51.9225, 4.4792, "port_rotterdam", "Port of Rotterdam"),
            totalDocks = 20,
            availableDocks = 8,
            containerCapacity = 150000,
            currentContainers = 125000,
            country = "Netherlands",
            timeZone = "Europe/Amsterdam"
        ),
        Port(
            id = "port_shanghai",
            name = "Port of Shanghai",
            location = Location(31.2333, 121.4667, "port_shanghai", "Port of Shanghai"),
            totalDocks = 30,
            availableDocks = 2,
            containerCapacity = 200000,
            currentContainers = 195000,
            country = "China",
            timeZone = "Asia/Shanghai"
        )
    )
    
    private var ships = mutableListOf<Ship>()
    
    init {
        // Initialize with some ships
        repeat(10) { index ->
            val isDocked = Random.nextBoolean()
            val originPort = ports.random()
            val destinationPort = ports.filter { it.id != originPort.id }.random()
            
            ships.add(
                Ship(
                    id = "ship_${UUID.randomUUID()}",
                    name = "CBMM Vessel ${index + 1}",
                    type = ShipType.values().random(),
                    status = if (isDocked) ShipStatus.DOCKED else ShipStatus.SAILING,
                    capacity = when (Random.nextInt(3)) {
                        0 -> 5000
                        1 -> 10000
                        else -> 15000
                    },
                    currentLoad = 0,
                    currentLocation = if (isDocked) {
                        originPort.location
                    } else {
                        // Random position between origin and destination
                        val progress = Random.nextDouble(0.1, 0.9)
                        Location(
                            latitude = originPort.location.latitude + (destinationPort.location.latitude - originPort.location.latitude) * progress,
                            longitude = originPort.location.longitude + (destinationPort.location.longitude - originPort.location.longitude) * progress
                        )
                    },
                    destination = if (isDocked) {
                        destinationPort.location.copy(portName = destinationPort.name)
                    } else {
                        destinationPort.location.copy(portName = destinationPort.name)
                    },
                    speed = if (isDocked) 0.0 else Random.nextDouble(10.0, 25.0),
                    heading = if (isDocked) 0.0 else Random.nextDouble(0.0, 360.0),
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }
    
    override suspend fun getShips(): List<Ship> = ships
    
    override suspend fun getPorts(): List<Port> = ports
    
    override suspend fun getShipById(id: String): Ship? = ships.find { it.id == id }
    
    override suspend fun getPortById(id: String): Port? = ports.find { it.id == id }
    
    override fun observeShips(): Flow<List<Ship>> = flow {
        while (true) {
            emit(ships)
            kotlinx.coroutines.delay(5000) // Update every 5 seconds
            updateShipsPositions()
        }
    }
    
    override fun observePorts(): Flow<List<Port>> = flowOf(ports)
    
    override suspend fun updateShipStatus(shipId: String, status: ShipStatus) {
        ships = ships.map { ship ->
            if (ship.id == shipId) {
                ship.copy(status = status)
            } else {
                ship
            }
        }.toMutableList()
    }

    private fun updateShipsPositions() {
        ships = ships.map { ship ->
            if (ship.status == ShipStatus.SAILING) {
                // Simulate ship movement
                val destination = ship.destination
                if (destination != null) {
                    val distance = calculateDistance(
                        ship.currentLocation.latitude, 
                        ship.currentLocation.longitude,
                        destination.latitude,
                        destination.longitude
                    )
                    
                    if (distance < 5.0) {
                        // Arrived at destination
                        ship.copy(
                            status = ShipStatus.DOCKED,
                            currentLocation = destination,
                            speed = 0.0,
                            lastUpdated = System.currentTimeMillis()
                        )
                    } else {
                        // Move towards destination
                        val bearing = calculateBearing(
                            ship.currentLocation.latitude,
                            ship.currentLocation.longitude,
                            destination.latitude,
                            destination.longitude
                        )
                        
                        val distanceMoved = ship.speed * 5.0 / 60.0 / 60.0 // 5 seconds of movement in degrees
                        val newLocation = calculateNewLocation(
                            ship.currentLocation.latitude,
                            ship.currentLocation.longitude,
                            distanceMoved,
                            bearing
                        )
                        
                        ship.copy(
                            currentLocation = newLocation,
                            heading = bearing,
                            lastUpdated = System.currentTimeMillis()
                        )
                    }
                } else {
                    ship
                }
            } else {
                // Random chance to start sailing
                if (Random.nextDouble() < 0.05) { // 5% chance to start sailing
                    val destinationPort = ports.filter { it.id != ship.currentLocation.portId }.random()
                    ship.copy(
                        status = ShipStatus.SAILING,
                        destination = destinationPort.location.copy(portName = destinationPort.name),
                        speed = Random.nextDouble(10.0, 25.0),
                        lastUpdated = System.currentTimeMillis()
                    )
                } else {
                    ship
                }
            }
        }.toMutableList()
    }
    
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadiusKm * c
    }
    
    private fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLon = Math.toRadians(lon2 - lon1)
        val y = Math.sin(dLon) * Math.cos(Math.toRadians(lat2))
        val x = Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) - 
                Math.sin(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(dLon)
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360
    }
    
    private fun calculateNewLocation(lat: Double, lon: Double, distance: Double, bearing: Double): Location {
        val earthRadiusKm = 6371.0
        val bearingRad = Math.toRadians(bearing)
        val latRad = Math.toRadians(lat)
        val lonRad = Math.toRadians(lon)
        
        val newLat = Math.asin(
            Math.sin(latRad) * Math.cos(distance / earthRadiusKm) +
                    Math.cos(latRad) * Math.sin(distance / earthRadiusKm) * Math.cos(bearingRad)
        )
        
        val newLon = lonRad + Math.atan2(
            Math.sin(bearingRad) * Math.sin(distance / earthRadiusKm) * Math.cos(latRad),
            Math.cos(distance / earthRadiusKm) - Math.sin(latRad) * Math.sin(newLat)
        )
        
        return Location(
            latitude = Math.toDegrees(newLat),
            longitude = Math.toDegrees(newLon)
        )
    }
}
