package com.cbmm.shipsimulator.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.DirectionsBoat
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cbmm.shipsimulator.R
import com.cbmm.shipsimulator.ui.navigation.Screen

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem.Map,
        NavItem.Fleet,
        NavItem.History,
        NavItem.Analytics,
        NavItem.Settings
    )
    
    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route
        
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    val isSelected = currentRoute == item.screen.route
                    val icon = if (isSelected) item.selectedIcon else item.unselectedIcon
                    icon?.let { 
                        Icon(
                            imageVector = it,
                            contentDescription = stringResource(id = item.titleResId)
                        )
                    } ?: painterResource(id = item.iconResId!!)?.let {
                        Icon(
                            painter = it,
                            contentDescription = stringResource(id = item.titleResId)
                        )
                    }
                },
                label = { Text(stringResource(id = item.titleResId)) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class NavItem(
    val screen: Screen,
    val titleResId: Int,
    val selectedIcon: Any? = null,
    val unselectedIcon: Any? = null,
    val iconResId: Int? = null
) {
    object Map : NavItem(
        screen = Screen.Map,
        titleResId = R.string.map,
        selectedIcon = Icons.Filled.Map,
        unselectedIcon = Icons.Outlined.Map
    )
    
    object Fleet : NavItem(
        screen = Screen.Fleet,
        titleResId = R.string.fleet,
        selectedIcon = Icons.Filled.DirectionsBoat,
        unselectedIcon = Icons.Outlined.DirectionsBoat
    )
    
    object Analytics : NavItem(
        screen = Screen.Analytics,
        titleResId = R.string.analytics,
        selectedIcon = Icons.Filled.Analytics,
        unselectedIcon = Icons.Outlined.Analytics
    )
    
    object History : NavItem(
        screen = Screen.History,
        titleResId = R.string.history,
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    ),
    
    object Settings : NavItem(
        screen = Screen.Settings,
        titleResId = R.string.settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
}
