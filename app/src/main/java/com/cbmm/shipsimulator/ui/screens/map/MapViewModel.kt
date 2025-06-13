package com.cbmm.shipsimulator.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbmm.shipsimulator.data.model.Port
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.data.repository.ShipRepository
import com.cbmm.shipsimulator.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val ships: List<Ship> = emptyList(),
    val ports: List<Port> = emptyList(),
    val isOffline: Boolean = false
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: ShipRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadShips()
        loadPorts()
    }

    private fun loadShips() {
        viewModelScope.launch {
            repository.getAllShips().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = !it.isRefreshing) }
                    }
                    is NetworkResult.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                isRefreshing = false,
                                ships = result.data ?: emptyList(),
                                isOffline = result.isFromCache
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = result.message,
                                isOffline = state.ships.isNotEmpty()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadPorts() {
        viewModelScope.launch {
            repository.observePorts().collect { ports ->
                _uiState.update { it.copy(ports = ports) }
            }
        }
    }

    fun refreshData() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadShips()
    }

    fun onErrorShown() {
        _uiState.update { it.copy(error = null) }
    }
    
    suspend fun getShipById(shipId: String): Ship? {
        return repository.getShipById(shipId)
    }
    
    suspend fun getPortById(portId: String): Port? {
        return repository.getPortById(portId)
    }
    
    fun updateShipStatus(shipId: String, status: ShipStatus) {
        viewModelScope.launch {
            repository.updateShipStatus(shipId, status)
        }
    }
}
