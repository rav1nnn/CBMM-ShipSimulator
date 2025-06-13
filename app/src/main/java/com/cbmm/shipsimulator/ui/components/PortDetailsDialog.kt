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
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cbmm.shipsimulator.R
import com.cbmm.shipsimulator.data.model.Port
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.ui.theme.CBMMBlue
import com.cbmm.shipsimulator.util.FormatterUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortDetailsDialog(
    port: Port,
    onDismiss: () -> Unit,
    onViewShips: (() -> Unit)? = null,
    onViewContainers: (() -> Unit)? = null,
) {
    val containerUtilization = port.containerUtilization
    val dockUtilization = port.dockUtilization
    
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
                // Header with port icon and name
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
                            Icon(
                                imageVector = Icons.Default.Anchor,
                                contentDescription = null,
                                tint = CBMMBlue,
                                modifier = Modifier.matchParentSize()
                            )
                        }
                        Text(
                            text = port.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Port details
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Location info
                    InfoItem(
                        icon = Icons.Default.LocationOn,
                        label = stringResource(R.string.port_location),
                        value = port.country,
                        secondaryValue = port.timeZone
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    
                    // Docks info
                    Text(
                        text = stringResource(R.string.port_available_docks),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.port_capacity, port.availableDocks, port.totalDocks),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "${(dockUtilization * 100).toInt()}% ${stringResource(R.string.port_utilization, "")}".trim(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    LinearProgressIndicator(
                        progress = dockUtilization,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = CBMMBlue,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    
                    // Containers info
                    Text(
                        text = stringResource(R.string.port_containers),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.port_capacity, port.currentContainers, port.containerCapacity),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = stringResource(R.string.port_utilization, "${(containerUtilization * 100).toInt()}%"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    LinearProgressIndicator(
                        progress = containerUtilization.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = CBMMBlue,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
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
                        text = stringResource(R.string.port_actions),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        onViewShips?.let { action ->
                            ActionButton(
                                text = stringResource(R.string.action_view_ships),
                                onClick = action,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        onViewContainers?.let { action ->
                            ActionButton(
                                text = stringResource(R.string.action_view_containers),
                                onClick = action,
                                modifier = Modifier.weight(1f)
                            )
                        }
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
private fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    secondaryValue: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
            secondaryValue?.let { value ->
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
        Text(text = text, textAlign = TextAlign.Center)
    }
}
