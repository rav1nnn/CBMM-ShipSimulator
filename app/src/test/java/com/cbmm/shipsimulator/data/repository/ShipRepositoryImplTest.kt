package com.cbmm.shipsimulator.data.repository

import app.cash.turbine.test
import com.cbmm.shipsimulator.data.api.ShipApiService
import com.cbmm.shipsimulator.data.local.dao.ShipDao
import com.cbmm.shipsimulator.data.local.dao.ShipRouteDao
import com.cbmm.shipsimulator.data.local.entity.ShipEntity
import com.cbmm.shipsimulator.data.model.Location
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.data.model.ShipType
import com.cbmm.shipsimulator.util.NetworkResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ShipRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val shipApiService = mockk<ShipApiService>()
    private val shipDao = mockk<ShipDao>()
    private val shipRouteDao = mockk<ShipRouteDao>()
    private lateinit var repository: ShipRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = ShipRepositoryImpl(shipApiService, shipDao, shipRouteDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllShips should emit loading and success when API call is successful`() = runTest {
        // Given
        val mockShips = listOf(createMockShip("1"), createMockShip("2"))
        coEvery { shipApiService.getAllShips() } returns Response.success(mockShips)
        coEvery { shipDao.insertShips(any()) } returns Unit

        // When & Then
        repository.getAllShips().test {
            val loading = awaitItem()
            assertTrue(loading is NetworkResult.Loading)

            val success = awaitItem()
            assertTrue(success is NetworkResult.Success)
            assertEquals(mockShips, (success as NetworkResult.Success).data)
            assertFalse(success.isFromCache)

            awaitComplete()
        }

        coVerify { shipDao.insertShips(mockShips) }
    }

    @Test
    fun `getAllShips should emit error when API call fails and no local data`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { shipApiService.getAllShips() } throws IOException(errorMessage)
        coEvery { shipDao.getAllShips() } returns flowOf(emptyList())

        // When & Then
        repository.getAllShips().test {
            val loading = awaitItem()
            assertTrue(loading is NetworkResult.Loading)

            val error = awaitItem()
            assertTrue(error is NetworkResult.Error)
            assertEquals(errorMessage, (error as NetworkResult.Error).message)

            awaitComplete()
        }
    }

    @Test
    fun `getAllShips should emit cached data when API call fails`() = runTest {
        // Given
        val mockShips = listOf(createMockShip("1"), createMockShip("2"))
        coEvery { shipApiService.getAllShips() } throws IOException("Network error")
        coEvery { shipDao.getAllShips() } returns flowOf(mockShips)

        // When & Then
        repository.getAllShips().test {
            val loading = awaitItem()
            assertTrue(loading is NetworkResult.Loading)

            val success = awaitItem()
            assertTrue(success is NetworkResult.Success)
            assertEquals(mockShips, (success as NetworkResult.Success).data)
            assertTrue(success.isFromCache)

            awaitComplete()
        }
    }

    @Test
    fun `getShipById should return ship from API when available`() = runTest {
        // Given
        val shipId = "1"
        val mockShip = createMockShip(shipId)
        coEvery { shipApiService.getShipById(shipId) } returns Response.success(mockShip)
        coEvery { shipDao.insertShip(any()) } returns Unit

        // When & Then
        repository.getShipByIdWithStatus(shipId).test {
            val loading = awaitItem()
            assertTrue(loading is NetworkResult.Loading)

            val success = awaitItem()
            assertTrue(success is NetworkResult.Success)
            assertEquals(mockShip, (success as NetworkResult.Success).data)
            assertFalse(success.isFromCache)

            awaitComplete()
        }

        coVerify { shipDao.insertShip(mockShip) }
    }

    @Test
    fun `getAllShips returns success with API data when API call is successful`() = runBlocking {
        // Arrange
        val ships = listOf(
            Ship(
                id = "1",
                name = "Test Ship",
                type = ShipType.CONTAINER,
                status = ShipStatus.SAILING,
                capacity = 1000,
                currentLoad = 500,
                currentLocation = Location(0.0, 0.0),
                destination = Location(1.0, 1.0),
                speed = 20.0,
                heading = 45.0,
                lastUpdated = System.currentTimeMillis()
            )
        )
        coEvery { shipApiService.getAllShips() } returns Response.success(ships)
        coEvery { shipDao.insertShips(any()) } returns Unit

        // Act
        val result = repository.getAllShips().first()

        // Assert
        assertTrue(result is NetworkResult.Success)
        assertEquals(ships, (result as NetworkResult.Success).data)
        coVerify { shipDao.insertShips(any()) }
    }

    @Test
    fun `getAllShips returns success with local data when API call fails`() = runBlocking {
        // Arrange
        val ships = listOf(
            ShipEntity(
                id = "1",
                name = "Test Ship",
                type = ShipType.CONTAINER.name,
                status = ShipStatus.SAILING.name,
                capacity = 1000.0,
                currentLoad = 500.0,
                currentLocation = "{\"latitude\":0.0,\"longitude\":0.0}",
                destination = "{\"latitude\":1.0,\"longitude\":1.0}",
                speed = 20.0,
                heading = 45.0,
                lastUpdated = System.currentTimeMillis()
            )
        )
        coEvery { shipApiService.getAllShips() } throws IOException("Network error")
        coEvery { shipDao.getAllShips() } returns flowOf(ships)

        // Act
        val result = repository.getAllShips().first()

        // Assert
        assertTrue(result is NetworkResult.Success)
        assertTrue((result as NetworkResult.Success).isFromCache)
    }

    @Test
    fun `getShipsByStatus returns success with filtered ships`() = runBlocking {
        // Arrange
        val ships = listOf(
            ShipEntity(
                id = "1",
                name = "Test Ship",
                type = ShipType.CONTAINER.name,
                status = ShipStatus.SAILING.name,
                capacity = 1000.0,
                currentLoad = 500.0,
                currentLocation = "{\"latitude\":0.0,\"longitude\":0.0}",
                destination = "{\"latitude\":1.0,\"longitude\":1.0}",
                speed = 20.0,
                heading = 45.0,
                lastUpdated = System.currentTimeMillis()
            )
        )
        coEvery { shipDao.getShipsByStatus(ShipStatus.SAILING) } returns flowOf(ships)

        // Act
        val result = repository.getShipsByStatus(ShipStatus.SAILING).first()

        // Assert
        assertTrue(result is NetworkResult.Success)
        assertEquals(1, (result as NetworkResult.Success).data.size)
    }

    private fun createMockShip(id: String): Ship {
        return Ship(
            id = id,
            name = "Ship $id",
            type = ShipType.CONTAINER,
            status = ShipStatus.SAILING,
            capacity = 1000,
            currentLoad = 500,
            currentLocation = Location(0.0, 0.0),
            destination = null,
            speed = 20.0,
            heading = 90.0,
            lastUpdated = System.currentTimeMillis()
        )
    }
}
