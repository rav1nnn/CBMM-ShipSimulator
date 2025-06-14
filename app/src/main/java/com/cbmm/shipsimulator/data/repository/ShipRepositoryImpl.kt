package com.cbmm.shipsimulator.data.repository

import com.cbmm.shipsimulator.data.api.ShipApiService
import com.cbmm.shipsimulator.data.local.dao.ShipDao
import com.cbmm.shipsimulator.data.local.dao.ShipRouteDao
import com.cbmm.shipsimulator.data.local.entity.ShipEntity
import com.cbmm.shipsimulator.data.local.entity.ShipRouteEntity
import com.cbmm.shipsimulator.data.model.Location
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipRoute
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShipRepositoryImpl @Inject constructor(
    private val shipApiService: ShipApiService,
    private val shipDao: ShipDao,
    private val shipRouteDao: ShipRouteDao
) : ShipRepository {

    override fun getAllShips(): Flow<NetworkResult<List<Ship>>> = flow {
        emit(NetworkResult.Loading())
        
        try {
            // Tenta buscar da API
            val response = shipApiService.getAllShips()
            if (response.isSuccessful) {
                response.body()?.let { ships ->
                    // Salva no banco local
                    shipDao.insertShips(ships.map { it.toEntity() })
                    emit(NetworkResult.Success(ships))
                } ?: emit(NetworkResult.Error("Resposta vazia da API"))
            } else {
                emit(NetworkResult.Error("Erro na API: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Erro de rede: ${e.message}"))
        }
    }

    override fun getShipByIdWithStatus(id: String): Flow<NetworkResult<Ship>> = flow {
        emit(NetworkResult.Loading())
        
        try {
            // Tenta buscar da API
            val response = shipApiService.getShipById(id)
            if (response.isSuccessful) {
                response.body()?.let { ship ->
                    // Atualiza o banco local
                    shipDao.insertShip(ship.toEntity())
                    emit(NetworkResult.Success(ship))
                } ?: emit(NetworkResult.Error("Navio não encontrado"))
            } else {
                // Se falhar, tenta buscar do banco local
                val localShip = shipDao.getShipById(id)?.toModel()
                if (localShip != null) {
                    emit(NetworkResult.Success(localShip, true))
                } else {
                    emit(NetworkResult.Error(response.message()))
                }
            }
        } catch (e: Exception) {
            // Se falhar, tenta buscar do banco local
            try {
                val localShip = shipDao.getShipById(id)?.toModel()
                if (localShip != null) {
                    emit(NetworkResult.Success(localShip, true))
                } else {
                    emit(NetworkResult.Error(e.localizedMessage ?: "Erro desconhecido"))
                }
            } catch (e: Exception) {
                emit(NetworkResult.Error("Erro ao acessar dados locais"))
            }
        }
    }

    override fun getShipsByStatus(status: ShipStatus): Flow<NetworkResult<List<Ship>>> = flow {
        emit(NetworkResult.Loading())
        try {
            shipDao.getShipsByStatus(status).collect { entities ->
                val ships = entities.map { it.toModel() }
                emit(NetworkResult.Success(ships))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Erro ao buscar navios: ${e.message}"))
        }
    }

    // Implementação dos métodos de gerenciamento de rotas
    
    override suspend fun saveShipRoute(route: ShipRoute) {
        shipRouteDao.insertRoute(route.toEntity())
    }

    override fun getShipRoute(shipId: String): Flow<NetworkResult<ShipRoute>> = flow {
        emit(NetworkResult.Loading())
        try {
            shipRouteDao.getRouteByShipId(shipId).collect { entity ->
                entity?.let {
                    emit(NetworkResult.Success(it.toModel()))
                } ?: emit(NetworkResult.Error("Rota não encontrada"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Erro ao buscar rota: ${e.message}"))
        }
    }

    override fun getShipRoutes(shipId: String): Flow<List<ShipRoute>> {
        return shipRouteDao.getRoutesForShip(shipId)
    }

    override fun getShipRoutesInTimeRange(
        shipId: String,
        startTime: Date,
        endTime: Date
    ): Flow<List<ShipRoute>> {
        return shipRouteDao.getRoutesForShipInTimeRange(
            shipId = shipId,
            startTime = startTime.time,
            endTime = endTime.time
        )
    }

    override suspend fun deleteOldRoutes(olderThan: Date) {
        try {
            shipRouteDao.deleteRoutesOlderThan(olderThan.time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteRoutesForShip(shipId: String) {
        try {
            shipRouteDao.deleteRoutesForShip(shipId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Métodos suspensos obrigatórios
    override suspend fun getShips(): List<Ship> = shipDao.getAllShips().first().toModels()
    override suspend fun getPorts(): List<Port> = emptyList()
    override suspend fun getShipById(id: String): Ship? = shipDao.getShipById(id)?.toModel()
    override suspend fun getPortById(id: String): Port? = null
    override fun observeShips(): Flow<List<Ship>> = shipDao.getAllShips().map { it.toModels() }
    override fun observePorts(): Flow<List<Port>> = flowOf(emptyList())
    override suspend fun updateShipStatus(shipId: String, status: ShipStatus) {
        // Implemente a lógica de atualização de status
    }

    private fun Ship.toEntity(): ShipEntity {
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

    private fun ShipEntity.toModel(): Ship {
        return Ship(
            id = id,
            name = name,
            type = ShipType.valueOf(type),
            status = ShipStatus.valueOf(status),
            capacity = capacity.toInt(),
            currentLoad = currentLoad.toInt(),
            currentLocation = Location.fromString(currentLocation),
            destination = Location.fromString(destination),
            speed = speed,
            heading = heading,
            lastUpdated = lastUpdated
        )
    }

    private fun ShipRoute.toEntity(): ShipRouteEntity {
        return ShipRouteEntity(
            id = id,
            shipId = shipId,
            startLocation = startLocation.toString(),
            endLocation = endLocation.toString(),
            waypoints = waypoints.map { it.toString() },
            startTime = startTime,
            endTime = endTime,
            status = status.name
        )
    }

    private fun ShipRouteEntity.toModel(): ShipRoute {
        return ShipRoute(
            id = id,
            shipId = shipId,
            startLocation = Location.fromString(startLocation),
            endLocation = Location.fromString(endLocation),
            waypoints = waypoints.map { Location.fromString(it) },
            startTime = startTime,
            endTime = endTime,
            status = ShipRouteStatus.valueOf(status)
        )
    }
}
