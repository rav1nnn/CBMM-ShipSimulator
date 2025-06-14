package com.cbmm.shipsimulator.data.local.dao

import androidx.room.*
import com.cbmm.shipsimulator.data.local.entity.ShipRouteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShipRouteDao {
    @Query("SELECT * FROM ship_routes")
    fun getAllRoutes(): Flow<List<ShipRouteEntity>>

    @Query("SELECT * FROM ship_routes WHERE shipId = :shipId")
    fun getRoutesByShipId(shipId: String): Flow<List<ShipRouteEntity>>

    @Query("SELECT * FROM ship_routes WHERE id = :routeId")
    suspend fun getRouteById(routeId: String): ShipRouteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: ShipRouteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutes(routes: List<ShipRouteEntity>)

    @Update
    suspend fun updateRoute(route: ShipRouteEntity)

    @Delete
    suspend fun deleteRoute(route: ShipRouteEntity)

    @Query("DELETE FROM ship_routes")
    suspend fun deleteAllRoutes()
}
