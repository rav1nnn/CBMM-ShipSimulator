package com.cbmm.shipsimulator

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.cbmm.shipsimulator.service.ShipTrackingService
import com.cbmm.shipsimulator.sync.ShipsSyncManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CBMMShipSimulatorApp : Application() {

    @Inject
    lateinit var shipsSyncManager: ShipsSyncManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        
        // Inicia a sincronização periódica a cada 1 hora
        shipsSyncManager.scheduleSync(intervalHours = 1)
        
        // Inicia o serviço de rastreamento de navios
        startShipTrackingService()
    }
    
    private fun startShipTrackingService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ShipTrackingService::class.java))
        } else {
            startService(Intent(this, ShipTrackingService::class.java))
        }
    }
}
