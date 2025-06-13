package com.cbmm.shipsimulator.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cbmm.shipsimulator.ui.navigation.AppNavigation
import com.cbmm.shipsimulator.ui.theme.CBMMShipSimulatorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShipSimulatorApp() {
    CBMMShipSimulatorTheme {
        val navController = rememberNavController()
        
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold { paddingValues ->
                AppNavigation(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
