package com.cbmm.shipsimulator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cbmm.shipsimulator.ui.screens.analytics.AnalyticsScreen
import com.cbmm.shipsimulator.ui.screens.fleet.FleetScreen
import com.cbmm.shipsimulator.ui.screens.map.MapScreen
import com.cbmm.shipsimulator.ui.screens.ports.PortsScreen
import com.cbmm.shipsimulator.ui.screens.settings.SettingsScreen
import com.cbmm.shipsimulator.ui.screens.history.HistoryScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Map.route,
        modifier = modifier
    ) {
        composable(Screen.Map.route) {
            MapScreen()
        }
        
        composable(Screen.Fleet.route) {
            FleetScreen()
        }
        
        composable(Screen.Ports.route) {
            PortsScreen()
        }
        
        composable(Screen.Analytics.route) {
            AnalyticsScreen()
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        
        composable(Screen.History.route) {
            HistoryScreen()
        }
    }
}
