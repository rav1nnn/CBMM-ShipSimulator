package com.cbmm.shipsimulator.data.local.dao

import androidx.room.*
import com.cbmm.shipsimulator.data.model.Ship
import kotlinx.coroutines.flow.Flow

@Dao
interface ShipDao {
    @Query("SELECT * FROM ships")
    fun getAllShips(): Flow<List<Ship>>

    @Query("SELECT * FROM ships WHERE id = :id")
    suspend fun getShipById(id: String): Ship?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShip(ship: Ship)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShips(ships: List<Ship>)

    @Query("DELETE FROM ships")
    suspend fun deleteAllShips()

    @Query("SELECT * FROM ships WHERE isSailing = 1")
    fun getActiveShips(): Flow<List<Ship>>
}
