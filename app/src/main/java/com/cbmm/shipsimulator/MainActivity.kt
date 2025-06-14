package com.cbmm.shipsimulator

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.cbmm.shipsimulator.ui.navigation.AppNavigation
import com.cbmm.shipsimulator.ui.screens.permission.PermissionDeniedScreen
import com.cbmm.shipsimulator.ui.theme.CBMMShipSimulatorTheme
import com.cbmm.shipsimulator.util.PermissionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            // Todas as permissões foram concedidas
        } else {
            // Algumas permissões foram negadas
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CBMMShipSimulatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PermissionHandler {
                        val navController = rememberNavController()
                        AppNavigation(navController = navController)
                    }
                }
            }
        }
    }

    @Composable
    private fun PermissionHandler(content: @Composable () -> Unit) {
        val context = LocalContext.current
        var hasPermissions by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            hasPermissions = PermissionManager.hasRequiredPermissions(context)
            if (!hasPermissions) {
                requestPermissionLauncher.launch(PermissionManager.requiredPermissions)
            }
        }

        if (hasPermissions) {
            content()
        } else {
            // Mostrar tela de permissões negadas
            PermissionDeniedScreen()
        }
    }
}

@Composable
fun MainScreen() {
    // Seu código da tela principal aqui
}
