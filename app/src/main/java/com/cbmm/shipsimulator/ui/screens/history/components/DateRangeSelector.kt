package com.cbmm.shipsimulator.ui.screens.history.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cbmm.shipsimulator.R
import com.cbmm.shipsimulator.ui.theme.spacing
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(
    startDate: Date,
    endDate: Date,
    onDateRangeSelected: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    val startDateState = remember { mutableStateOf(startDate) }
    val endDateState = remember { mutableStateOf(endDate) }
    
    val startDateDialogState = remember { MaterialDialogState() }
    val endDateDialogState = remember { MaterialDialogState() }
    
    // Atualiza as datas iniciais
    LaunchedEffect(startDate, endDate) {
        startDateState.value = startDate
        endDateState.value = endDate
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Seletor de data inicial
        DateSelectorButton(
            label = stringResource(R.string.start_date),
            date = startDateState.value,
            onClick = { startDateDialogState.show() },
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Seletor de data final
        DateSelectorButton(
            label = stringResource(R.string.end_date),
            date = endDateState.value,
            onClick = { endDateDialogState.show() },
            modifier = Modifier.weight(1f)
        )
    }
    
    // Diálogo para selecionar data inicial
    MaterialDialog(
        dialogState = startDateDialogState,
        buttons = {
            positiveButton(text = stringResource(R.string.ok)) {
                onDateRangeSelected(
                    startDateState.value.time,
                    endDateState.value.time
                )
            }
            negativeButton(text = stringResource(R.string.cancel))
        }
    ) {
        datepicker(
            initialDate = startDateState.value,
            title = stringResource(R.string.select_start_date),
            onDateChange = { startDateState.value = it }
        )
    }
    
    // Diálogo para selecionar data final
    MaterialDialog(
        dialogState = endDateDialogState,
        buttons = {
            positiveButton(text = stringResource(R.string.ok)) {
                onDateRangeSelected(
                    startDateState.value.time,
                    endDateState.value.time
                )
            }
            negativeButton(text = stringResource(R.string.cancel))
        }
    ) {
        datepicker(
            initialDate = endDateState.value,
            title = stringResource(R.string.select_end_date),
            onDateChange = { endDateState.value = it }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSelectorButton(
    label: String,
    date: Date,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = dateFormat.format(date),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
