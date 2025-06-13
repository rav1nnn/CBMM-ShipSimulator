package com.cbmm.shipsimulator.ui.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.cbmm.shipsimulator.R
import com.cbmm.shipsimulator.data.model.Port
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.ui.components.LoadingIndicator
import com.cbmm.shipsimulator.ui.components.PortDetailsDialog
import com.cbmm.shipsimulator.ui.components.ShipDetailsDialog
import com.cbmm.shipsimulator.ui.components.ShipMap
import com.cbmm.shipsimulator.ui.theme.CBMMBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedShip by remember { mutableStateOf<Ship?>(null) }
    var selectedPort by remember { mutableStateOf<Port?>(null) }
    
    // Show dialogs when a ship or port is selected
    selectedShip?.let { ship ->
        ShipDetailsDialog(
            ship = ship,
            onDismiss = { selectedShip = null },
            onDepart = {
                viewModel.updateShipStatus(ship.id, ShipStatus.SAILING)
                selectedShip = null
            },
            onArrive = {
                viewModel.updateShipStatus(ship.id, ShipStatus.DOCKED)
                selectedShip = null
            },
            onLoad = {
                // TODO: Implement load action
                selectedShip = null
            },
            onUnload = {
                // TODO: Implement unload action
                selectedShip = null
            }
        )
    }
    
    selectedPort?.let { port ->
        PortDetailsDialog(
            port = port,
            onDismiss = { selectedPort = null },
            onViewShips = {
                // TODO: Navigate to ships list filtered by port
            },
            onViewContainers = {
                // TODO: Show containers at this port
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.map_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CBMMBlue,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.refreshData() },
                containerColor = CBMMBlue,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.retry)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator()
                }
                uiState.error != null -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = uiState.error ?: stringResource(R.string.error_loading_data),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> {
                    ShipMap(
                        ships = uiState.ships,
                        ports = uiState.ports,
                        onShipSelected = { ship ->
                            selectedShip = ship
                        },
                        onPortSelected = { port ->
                            selectedPort = port
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
