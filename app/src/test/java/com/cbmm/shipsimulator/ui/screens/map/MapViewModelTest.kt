package com.cbmm.shipsimulator.ui.screens.map

import app.cash.turbine.test
import com.cbmm.shipsimulator.data.model.Location
import com.cbmm.shipsimulator.data.model.Port
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.data.model.ShipType
import com.cbmm.shipsimulator.data.repository.ShipRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<ShipRepository>()
    private lateinit var viewModel: MapViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MapViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when initialized, should load ships and ports`() = runTest {
        // Given
        val mockShips = listOf(createMockShip("1"))
        val mockPorts = listOf(createMockPort("1"))
        
        coEvery { repository.getAllShips() } returns flowOf(
            NetworkResult.Loading(),
            NetworkResult.Success(mockShips)
        )
        coEvery { repository.observePorts() } returns flowOf(mockPorts)

        // When & Then
        viewModel.uiState.test {
            // Initial state
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
            assertTrue(initialState.ships.isEmpty())
            assertTrue(initialState.ports.isEmpty())

            // After loading
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals(mockShips, loadedState.ships)
            assertEquals(mockPorts, loadedState.ports)
            
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `refreshData should update isRefreshing state`() = runTest {
        // Given
        val mockShips = listOf(createMockShip("1"))
        coEvery { repository.getAllShips() } returns flowOf(
            NetworkResult.Success(mockShips)
        )

        // When
        viewModel.refreshData()


        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isRefreshing)
            
            // Wait for refresh to complete
            awaitItem()
            
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when offline, should show offline state`() = runTest {
        // Given
        val mockShips = listOf(createMockShip("1"))
        coEvery { repository.getAllShips() } returns flowOf(
            NetworkResult.Loading(),
            NetworkResult.Success(mockShips, true) // isFromCache = true
        )

        // When & Then
        viewModel.uiState.test {
            // Skip loading state
            awaitItem()
            
            val loadedState = awaitItem()
            assertTrue(loadedState.isOffline)
            
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when error occurs, should update error state`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { repository.getAllShips() } returns flowOf(
            NetworkResult.Loading(),
            NetworkResult.Error(errorMessage)
        )

        // When & Then
        viewModel.uiState.test {
            // Skip loading state
            awaitItem()
            
            val errorState = awaitItem()
            assertEquals(errorMessage, errorState.error)
            
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onErrorShown should clear error state`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { repository.getAllShips() } returns flowOf(
            NetworkResult.Error(errorMessage)
        )

        // When
        viewModel.onErrorShown()


        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.error)
            
            cancelAndConsumeRemainingEvents()
        }
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

    private fun createMockPort(id: String): Port {
        return Port(
            id = id,
            name = "Port $id",
            location = Location(0.0, 0.0),
            capacity = 10,
            availableDocks = 5
        )
    }
}
