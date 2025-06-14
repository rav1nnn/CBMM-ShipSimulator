package com.cbmm.shipsimulator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cbmm.shipsimulator.data.local.converters.ShipConverters
import com.cbmm.shipsimulator.data.local.converters.ShipRouteConverters
import com.cbmm.shipsimulator.data.local.dao.ShipDao
import com.cbmm.shipsimulator.data.local.dao.ShipRouteDao
import com.cbmm.shipsimulator.data.local.entity.ShipEntity
import com.cbmm.shipsimulator.data.local.entity.ShipRouteEntity

@Database(
    entities = [
        ShipEntity::class,
        ShipRouteEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(ShipConverters::class, ShipRouteConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shipDao(): ShipDao
    abstract fun shipRouteDao(): ShipRouteDao

    companion object {
        const val DATABASE_NAME = "ship_simulator.db"
    }
} 