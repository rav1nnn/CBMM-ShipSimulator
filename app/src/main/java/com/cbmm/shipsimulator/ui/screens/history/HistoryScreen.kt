package com.cbmm.shipsimulator.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cbmm.shipsimulator.R
import com.cbmm.shipsimulator.data.model.ShipRoute
import com.cbmm.shipsimulator.ui.components.LoadingIndicator
import com.cbmm.shipsimulator.ui.navigation.Screen
import com.cbmm.shipsimulator.ui.screens.history.components.DateRangeSelector
import com.cbmm.shipsimulator.ui.screens.history.components.RouteList
import com.cbmm.shipsimulator.ui.theme.spacing
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onRouteClick: (ShipRoute) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val showClearDialog = remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    
    // Efeito para carregar as rotas iniciais
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }
    
    // Limpa mensagens de erro quando a tela é exibida novamente
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearError()
        }
    }
    
    // Exibe mensagens de erro
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Poderia usar um Snackbar ou Toast aqui
            // Snackbar não está disponível no Material3, então usamos um Toast por enquanto
            android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showClearDialog.value = true },
                        enabled = uiState.routes.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.clear_history)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spacing.medium)
            ) {
                // Seletor de data
                DateRangeSelector(
                    startDate = Date(uiState.selectedDateRange.start),
                    endDate = Date(uiState.selectedDateRange.endInclusive),
                    onDateRangeSelected = { start, end ->
                        viewModel.setDateRange(start, end)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Lista de rotas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    if (uiState.isLoading) {
                        LoadingIndicator()
                    } else {
                        RouteList(
                            routes = uiState.routes,
                            onRouteClick = onRouteClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
    
    // Diálogo de confirmação para limpar histórico
    if (showClearDialog.value) {
        AlertDialog(
            onDismissRequest = { showClearDialog.value = false },
            title = { Text(stringResource(R.string.clear_history)) },
            text = { Text(stringResource(R.string.clear_history_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteOldRoutes(30)
                        showClearDialog.value = false
                    }
                ) {
                    Text(stringResource(R.string.clear))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearDialog.value = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
