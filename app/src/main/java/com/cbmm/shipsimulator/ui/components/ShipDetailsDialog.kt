package com.cbmm.shipsimulator.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cbmm.shipsimulator.R
import com.cbmm.shipsimulator.data.model.Location
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.data.model.ShipType
import com.cbmm.shipsimulator.ui.theme.CBMMBlue
import com.cbmm.shipsimulator.util.FormatterUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShipDetailsDialog(
    ship: Ship,
    onDismiss: () -> Unit,
    onDepart: (() -> Unit)? = null,
    onArrive: (() -> Unit)? = null,
    onLoad: (() -> Unit)? = null,
    onUnload: (() -> Unit)? = null,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with ship icon and name
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CBMMBlue)
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = FormatterUtils.getShipIcon(ship)),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(CBMMBlue),
                                modifier = Modifier.matchParentSize()
                            )
                        }
                        Text(
                            text = ship.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                // Ship details
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Basic info
                    InfoRow(
                        label = stringResource(R.string.ship_type),
                        value = FormatterUtils.formatShipType(ship.type)
                    )
                    
                    InfoRow(
                        label = stringResource(R.string.ship_status),
                        value = FormatterUtils.formatShipStatus(ship.status),
                        valueColor = Color(FormatterUtils.getStatusColor(ship.status))
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Location info
                    Text(
                        text = stringResource(R.string.ship_location),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    ship.currentLocation?.let { location ->
                        LocationInfo(location = location)
                    } ?: Text(
                        text = stringResource(R.string.not_available),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    // Destination info
                    if (ship.destination != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.ship_destination),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = ship.destination.portName ?: stringResource(R.string.not_available),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        ship.destination.let { location ->
                            LocationInfo(location = location)
                        }
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Capacity info
                    InfoRow(
                        label = stringResource(R.string.ship_capacity),
                        value = "${ship.currentLoad} / ${ship.capacity} TEU"
                    )
                    
                    // Last updated
                    Text(
                        text = "${stringResource(R.string.ship_last_update)}: ${FormatterUtils.formatDateTime(ship.lastUpdated)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                
                // Action buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.ship_actions),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    
                    when (ship.status) {
                        ShipStatus.DOCKED -> {
                            onDepart?.let { action ->
                                ActionButton(
                                    text = stringResource(R.string.action_depart),
                                    onClick = { action() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                onLoad?.let { action ->
                                    ActionButton(
                                        text = stringResource(R.string.action_load),
                                        onClick = { action() },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                
                                onUnload?.let { action ->
                                    ActionButton(
                                        text = stringResource(R.string.action_unload),
                                        onClick = { action() },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        ShipStatus.SAILING -> {
                            onArrive?.let { action ->
                                ActionButton(
                                    text = stringResource(R.string.action_arrive),
                                    onClick = { action() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        else -> { /* No action buttons for maintenance */ }
                    }
                    
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(text = stringResource(R.string.close))
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationInfo(location: Location) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.latitude, location.latitude),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = stringResource(R.string.longitude, location.longitude),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = CBMMBlue,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(text = text)
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.Medium
        )
    }
}
