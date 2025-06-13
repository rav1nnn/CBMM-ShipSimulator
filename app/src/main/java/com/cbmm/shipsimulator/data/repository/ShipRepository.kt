package com.cbmm.shipsimulator.data.repository

import com.cbmm.shipsimulator.data.model.Port
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipRoute
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ShipRepository {
    // Métodos antigos mantidos para compatibilidade
    suspend fun getShips(): List<Ship>
    suspend fun getPorts(): List<Port>
    suspend fun getShipById(id: String): Ship?
    suspend fun getPortById(id: String): Port?
    fun observeShips(): Flow<List<Ship>>
    fun observePorts(): Flow<List<Port>>
    suspend fun updateShipStatus(shipId: String, status: ShipStatus)
    
    // Novos métodos com suporte a NetworkResult
    fun getAllShips(): Flow<NetworkResult<List<Ship>>>
    fun getShipByIdWithStatus(id: String): Flow<NetworkResult<Ship>>
    fun getActiveShips(): Flow<NetworkResult<List<Ship>>>
    
    // Métodos para gerenciamento de rotas
    suspend fun saveShipRoute(route: ShipRoute)
    fun getShipRoutes(shipId: String): Flow<List<ShipRoute>>
    fun getShipRoutesInTimeRange(
        shipId: String,
        startTime: Date,
        endTime: Date
    ): Flow<List<ShipRoute>>
    
    suspend fun deleteOldRoutes(olderThan: Date)
    suspend fun deleteRoutesForShip(shipId: String)
}
