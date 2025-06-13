package com.cbmm.shipsimulator.ui.screens.history

import com.cbmm.shipsimulator.data.model.ShipRoute
import java.util.*

data class HistoryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val routes: List<ShipRoute> = emptyList(),
    val selectedDateRange: ClosedRange<Long> = getDefaultDateRange(),
    val selectedShipId: String? = null
) {
    companion object {
        fun getDefaultDateRange(): ClosedRange<Long> {
            val calendar = Calendar.getInstance()
            val endDate = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, -7) // Últimos 7 dias por padrão
            val startDate = calendar.timeInMillis
            return startDate..endDate
        }
    }
}
