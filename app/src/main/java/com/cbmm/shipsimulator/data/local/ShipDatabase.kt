package com.cbmm.shipsimulator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cbmm.shipsimulator.data.local.dao.ShipDao
import com.cbmm.shipsimulator.data.local.dao.ShipRouteDao
import com.cbmm.shipsimulator.data.local.migrations.MIGRATION_1_2
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipRoute
import com.cbmm.shipsimulator.util.Converters

@Database(
    entities = [Ship::class, ShipRoute::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ShipDatabase : RoomDatabase() {
    abstract fun shipDao(): ShipDao
    abstract fun shipRouteDao(): ShipRouteDao

    companion object {
        @Volatile
        private var instance: ShipDatabase? = null

        fun getInstance(context: Context): ShipDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): ShipDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ShipDatabase::class.java,
                "ship_database"
            )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    // Pode adicionar callbacks para pré-popular o banco de dados se necessário
                })
                .build()
        }
    }
}
