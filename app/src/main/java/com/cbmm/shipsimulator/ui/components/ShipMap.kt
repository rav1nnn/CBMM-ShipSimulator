package com.cbmm.shipsimulator.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.cbmm.shipsimulator.data.model.Port
import com.cbmm.shipsimulator.data.model.Ship
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ShipMap(
    ships: List<Ship>,
    ports: List<Port>,
    onShipSelected: (Ship) -> Unit,
    onPortSelected: (Port) -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultLocation = LatLng(-23.5505, -46.6333) // São Paulo as default
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 4f)
    }
    
    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { /* Handle map click */ }
        ) {
            // Add port markers
            ports.forEach { port ->
                val position = remember(port.id) {
                    LatLng(port.location.latitude, port.location.longitude)
                }
                
                Marker(
                    state = MarkerState(position = position),
                    title = port.name,
                    snippet = "Docks: ${port.availableDocks}/${port.totalDocks}",
                    onClick = {
                        onPortSelected(port)
                        true
                    }
                )
            }
            
            // Add ship markers
            ships.forEach { ship ->
                val position = remember(ship.id) {
                    LatLng(ship.currentLocation.latitude, ship.currentLocation.longitude)
                }
                
                Marker(
                    state = MarkerState(position = position),
                    title = ship.name,
                    snippet = "${ship.type} - ${ship.status}",
                    rotation = ship.heading.toFloat(),
                    onClick = {
                        onShipSelected(ship)
                        true
                    }
                )
            }
        }
    }
}
