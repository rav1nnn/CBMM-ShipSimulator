package com.cbmm.shipsimulator.ui.screens.history.components

import android.app.DatePickerDialog
import android.widget.DatePicker
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
    
    // Atualiza as datas iniciais
    LaunchedEffect(startDate, endDate) {
        startDateState.value = startDate
        endDateState.value = endDate
    }
    
    // Cria um DatePickerDialog para a data inicial
    val startDatePicker = remember {
        val calendar = Calendar.getInstance().apply { time = startDateState.value }
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val newDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }.time
                startDateState.value = newDate
                onDateRangeSelected(newDate.time, endDateState.value.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
    
    // Cria um DatePickerDialog para a data final
    val endDatePicker = remember {
        val calendar = Calendar.getInstance().apply { time = endDateState.value }
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val newDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }.time
                endDateState.value = newDate
                onDateRangeSelected(startDateState.value.time, newDate.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
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
            onClick = { startDatePicker.show() },
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Seletor de data final
        DateSelectorButton(
            label = stringResource(R.string.end_date),
            date = endDateState.value,
            onClick = { endDatePicker.show() },
            modifier = Modifier.weight(1f)
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
