package com.cbmm.shipsimulator.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.cbmm.shipsimulator.R
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipRoute
import com.cbmm.shipsimulator.data.repository.ShipRepository
import com.cbmm.shipsimulator.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ShipTrackingService : Service() {

    @Inject
    lateinit var shipRepository: ShipRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var trackingJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTracking()
        return START_STICKY
    }

    private fun startTracking() {
        trackingJob?.cancel()
        trackingJob = serviceScope.launch {
            while (true) {
                try {
                    // Busca todos os navios ativos
                    shipRepository.getActiveShips().collectLatest { result ->
                        when (result) {
                            is com.cbmm.shipsimulator.util.NetworkResult.Success -> {
                                // Salva a rota de cada navio ativo
                                result.data?.forEach { ship ->
                                    saveShipPosition(ship)
                                }
                            }
                            else -> { /* Tratar erros se necessário */ }
                        }
                    }
                    // Aguarda 1 minuto antes da próxima atualização
                    delay(60_000)
                } catch (e: Exception) {
                    // Logar o erro e continuar
                    e.printStackTrace()
                    delay(10_000) // Espera 10 segundos antes de tentar novamente em caso de erro
                }
            }
        }
    }

    private suspend fun saveShipPosition(ship: Ship) {
        val route = ShipRoute(
            shipId = ship.id,
            latitude = ship.currentLocation.latitude,
            longitude = ship.currentLocation.longitude,
            speed = ship.speed,
            heading = ship.heading,
            timestamp = System.currentTimeMillis()
        )
        shipRepository.saveShipRoute(route)
    }

    private fun createNotification(): Notification {
        val channelId = "ship_tracking_channel"
        val channelName = "Ship Tracking Service"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Tracks ship positions in the background"
            }
            
            val notificationManager = getSystemService(NotificationManager::class)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Rastreando Navios")
            .setContentText("Monitorando posições dos navios...")
            .setSmallIcon(R.drawable.ic_ship)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        trackingJob?.cancel()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        
        fun startService(context: Context) {
            val intent = Intent(context, ShipTrackingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, ShipTrackingService::class.java)
            context.stopService(intent)
        }
    }
}
