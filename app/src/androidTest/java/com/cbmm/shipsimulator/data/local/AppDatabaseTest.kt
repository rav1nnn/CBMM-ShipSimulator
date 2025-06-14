package com.cbmm.shipsimulator.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cbmm.shipsimulator.data.local.dao.ShipDao
import com.cbmm.shipsimulator.data.local.dao.ShipRouteDao
import com.cbmm.shipsimulator.data.local.entity.ShipEntity
import com.cbmm.shipsimulator.data.local.entity.ShipRouteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var shipDao: ShipDao
    private lateinit var shipRouteDao: ShipRouteDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        shipDao = db.shipDao()
        shipRouteDao = db.shipRouteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadShip() = runBlocking {
        val ship = ShipEntity(
            id = "1",
            name = "Test Ship",
            type = "Cargo",
            status = "Active",
            capacity = 1000.0,
            currentLoad = 500.0,
            currentLocation = "Port A",
            destination = "Port B",
            speed = 20.0,
            heading = 45.0,
            lastUpdated = System.currentTimeMillis()
        )
        shipDao.insertShip(ship)
        val ships = shipDao.getAllShips().first()
        assertEquals(ship, ships[0])
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadShipRoute() = runBlocking {
        val route = ShipRouteEntity(
            id = "1",
            shipId = "1",
            latitude = -23.5505,
            longitude = -46.6333,
            timestamp = System.currentTimeMillis(),
            speed = 20.0,
            heading = 45.0
        )
        shipRouteDao.insertRoute(route)
        val routes = shipRouteDao.getAllRoutes().first()
        assertEquals(route, routes[0])
    }
} 