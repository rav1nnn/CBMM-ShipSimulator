package com.cbmm.shipsimulator.data.repository

import com.cbmm.shipsimulator.data.api.ShipApiService
import com.cbmm.shipsimulator.data.local.dao.ShipDao
import com.cbmm.shipsimulator.data.local.dao.ShipRouteDao
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipRoute
import com.cbmm.shipsimulator.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
                    shipDao.insertShips(ships)
                    emit(NetworkResult.Success(ships))
                } ?: emit(NetworkResult.Error("Dados inválidos"))
            } else {
                emit(NetworkResult.Error(response.message()))
            }
        } catch (e: HttpException) {
            // Se falhar, tenta buscar do banco local
            try {
                val localShips = shipDao.getAllShips()
                emit(NetworkResult.Success(localShips, true))
            } catch (e: Exception) {
                emit(NetworkResult.Error(e.localizedMessage ?: "Erro desconhecido"))
            }
        } catch (e: IOException) {
            // Se não houver conexão, busca do banco local
            try {
                val localShips = shipDao.getAllShips()
                emit(NetworkResult.Success(localShips, true))
            } catch (e: Exception) {
                emit(NetworkResult.Error("Sem conexão e sem dados locais"))
            }
        }
    }

    override fun getShipById(id: String): Flow<NetworkResult<Ship>> = flow {
        emit(NetworkResult.Loading())
        
        try {
            // Tenta buscar da API
            val response = shipApiService.getShipById(id)
            if (response.isSuccessful) {
                response.body()?.let { ship ->
                    // Atualiza o banco local
                    shipDao.insertShip(ship)
                    emit(NetworkResult.Success(ship))
                } ?: emit(NetworkResult.Error("Navio não encontrado"))
            } else {
                // Se falhar, tenta buscar do banco local
                val localShip = shipDao.getShipById(id)
                if (localShip != null) {
                    emit(NetworkResult.Success(localShip, true))
                } else {
                    emit(NetworkResult.Error(response.message()))
                }
            }
        } catch (e: Exception) {
            // Se falhar, tenta buscar do banco local
            try {
                val localShip = shipDao.getShipById(id)
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

    override fun getActiveShips(): Flow<NetworkResult<List<Ship>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val activeShips = shipDao.getActiveShips()
            emit(NetworkResult.Success(activeShips))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage ?: "Erro ao buscar navios ativos"))
        }
    }

    // Implementação dos métodos de gerenciamento de rotas
    
    override suspend fun saveShipRoute(route: ShipRoute) {
        try {
            shipRouteDao.insertRoute(route)
        } catch (e: Exception) {
            // Logar o erro ou tratar conforme necessário
            e.printStackTrace()
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
            // Logar o erro ou tratar conforme necessário
            e.printStackTrace()
        }
    }

    override suspend fun deleteRoutesForShip(shipId: String) {
        try {
            shipRouteDao.deleteRoutesForShip(shipId)
        } catch (e: Exception) {
            // Logar o erro ou tratar conforme necessário
            e.printStackTrace()
        }
    }
}
