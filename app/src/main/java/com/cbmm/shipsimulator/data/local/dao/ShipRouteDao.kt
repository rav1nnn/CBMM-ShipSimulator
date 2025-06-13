package com.cbmm.shipsimulator.data.local.dao

import androidx.room.*
import com.cbmm.shipsimulator.data.model.ShipRoute
import kotlinx.coroutines.flow.Flow

@Dao
interface ShipRouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: ShipRoute)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutes(routes: List<ShipRoute>)


    @Query("SELECT * FROM ship_routes WHERE shipId = :shipId ORDER BY timestamp DESC")
    fun getRoutesForShip(shipId: String): Flow<List<ShipRoute>>

    @Query("SELECT * FROM ship_routes WHERE shipId = :shipId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    fun getRoutesForShipInTimeRange(
        shipId: String,
        startTime: Long,
        endTime: Long
    ): Flow<List<ShipRoute>>

    @Query("DELETE FROM ship_routes WHERE timestamp < :timestamp")
    suspend fun deleteRoutesOlderThan(timestamp: Long)

    @Query("DELETE FROM ship_routes WHERE shipId = :shipId")
    suspend fun deleteRoutesForShip(shipId: String)
}
