package com.cbmm.shipsimulator.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbmm.shipsimulator.data.model.ShipRoute
import com.cbmm.shipsimulator.data.repository.ShipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val shipRepository: ShipRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private var currentShipId: String? = null
    private var currentDateRange: ClosedRange<Long> = HistoryUiState.getDefaultDateRange()

    init {
        loadRoutes()
    }

    fun setSelectedShip(shipId: String?) {
        currentShipId = shipId
        _uiState.update { it.copy(selectedShipId = shipId) }
        loadRoutes()
    }

    fun setDateRange(startDate: Long, endDate: Long) {
        currentDateRange = startDate..endDate
        _uiState.update { it.copy(selectedDateRange = currentDateRange) }
        loadRoutes()
    }

    fun refresh() {
        loadRoutes()
    }

    private fun loadRoutes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                currentShipId?.let { shipId ->
                    shipRepository.getShipRoutesInTimeRange(
                        shipId = shipId,
                        startTime = Date(currentDateRange.start),
                        endTime = Date(currentDateRange.endInclusive)
                    ).collect { routes ->
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                routes = routes.sortedByDescending { it.timestamp },
                                error = null
                            )
                        }
                    }
                } ?: run {
                    // Se nenhum navio estiver selecionado, limpa a lista
                    _uiState.update { it.copy(isLoading = false, routes = emptyList()) }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao carregar histórico"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun deleteOldRoutes(olderThanDays: Int = 30) {
        viewModelScope.launch {
            try {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -olderThanDays)
                shipRepository.deleteOldRoutes(calendar.time)
                // Recarrega as rotas após a exclusão
                loadRoutes()
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(error = "Falha ao limpar histórico: ${e.message}")
                }
            }
        }
    }
}
