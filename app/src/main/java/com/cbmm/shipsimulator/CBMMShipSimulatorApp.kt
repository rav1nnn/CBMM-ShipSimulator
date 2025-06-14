package com.cbmm.shipsimulator

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.hilt.work.HiltWorkerFactory
import com.cbmm.shipsimulator.service.ShipSimulatorService
import com.cbmm.shipsimulator.service.ShipTrackingService
import com.cbmm.shipsimulator.sync.ShipsSyncManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CBMMShipSimulatorApp : Application(), Configuration.Provider {

    @Inject
    lateinit var shipsSyncManager: ShipsSyncManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        
        // Inicializa o WorkManager
        WorkManager.initialize(this, workManagerConfiguration)
        
        // Inicia a sincronização periódica a cada 1 hora
        shipsSyncManager.startPeriodicSync()
        
        // Inicia o serviço de rastreamento de navios
        startShipTrackingService()
        
        // Inicia o serviço de simulação de navios
        startShipSimulatorService()
    }
    
    private fun startShipTrackingService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ShipTrackingService::class.java))
        } else {
            startService(Intent(this, ShipTrackingService::class.java))
        }
    }
    
    private fun startShipSimulatorService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ShipSimulatorService::class.java))
        } else {
            startService(Intent(this, ShipSimulatorService::class.java))
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ship_tracking_channel",
                "Ship Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for ship tracking notifications"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
