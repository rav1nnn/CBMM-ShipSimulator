package com.cbmm.shipsimulator.data.local.dao

import androidx.room.*
import com.cbmm.shipsimulator.data.local.entity.ShipEntity
import com.cbmm.shipsimulator.data.model.ShipStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ShipDao {
    @Query("SELECT * FROM ships")
    fun getAllShips(): Flow<List<ShipEntity>>

    @Query("SELECT * FROM ships WHERE id = :shipId")
    suspend fun getShipById(shipId: String): ShipEntity?

    @Query("SELECT * FROM ships WHERE status = :status")
    fun getShipsByStatus(status: ShipStatus): Flow<List<ShipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShip(ship: ShipEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShips(ships: List<ShipEntity>)

    @Update
    suspend fun updateShip(ship: ShipEntity)

    @Delete
    suspend fun deleteShip(ship: ShipEntity)

    @Query("DELETE FROM ships")
    suspend fun deleteAllShips()
}
