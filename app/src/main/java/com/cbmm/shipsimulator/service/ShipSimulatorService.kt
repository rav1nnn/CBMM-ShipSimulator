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
import com.cbmm.shipsimulator.data.model.Location
import com.cbmm.shipsimulator.data.model.Ship
import com.cbmm.shipsimulator.data.model.ShipRoute
import com.cbmm.shipsimulator.data.model.ShipStatus
import com.cbmm.shipsimulator.data.repository.ShipRepository
import com.cbmm.shipsimulator.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import javax.inject.Inject

@AndroidEntryPoint
class ShipSimulatorService : Service() {

    @Inject
    lateinit var shipRepository: ShipRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var simulationJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startSimulation()
        return START_STICKY
    }

    private fun startSimulation() {
        simulationJob?.cancel()
        simulationJob = serviceScope.launch {
            while (true) {
                try {
                    shipRepository.getShipsByStatus(ShipStatus.SAILING).collectLatest { result ->
                        when (result) {
                            is com.cbmm.shipsimulator.util.NetworkResult.Success -> {
                                result.data?.forEach { ship ->
                                    updateShipPosition(ship)
                                }
                            }
                            else -> { /* Tratar erros se necessário */ }
                        }
                    }
                    delay(30_000) // Atualiza a cada 30 segundos
                } catch (e: Exception) {
                    e.printStackTrace()
                    delay(10_000)
                }
            }
        }
    }

    private suspend fun updateShipPosition(ship: Ship) {
        val newLocation = calculateNewPosition(
            currentLocation = ship.currentLocation,
            speed = ship.speed,
            heading = ship.heading
        )

        // Salva a rota do navio
        val route = ShipRoute(
            shipId = ship.id,
            latitude = newLocation.latitude,
            longitude = newLocation.longitude,
            speed = ship.speed,
            heading = ship.heading
        )
        shipRepository.saveShipRoute(route)

        // Atualiza a posição do navio
        val updatedShip = ship.copy(
            currentLocation = newLocation,
            lastUpdated = System.currentTimeMillis()
        )

        // Verifica se o navio chegou ao destino
        if (ship.destination != null) {
            val distance = calculateDistance(
                newLocation.latitude,
                newLocation.longitude,
                ship.destination.latitude,
                ship.destination.longitude
            )

            if (distance < 0.1) { // Se estiver a menos de 0.1 graus do destino
                shipRepository.updateShipStatus(ship.id, ShipStatus.DOCKED)
            }
        }
    }

    private fun calculateNewPosition(
        currentLocation: Location,
        speed: Double,
        heading: Double
    ): Location {
        // Converte velocidade de nós para graus por segundo
        val speedInDegrees = speed * 0.000277778 // Aproximadamente 1 nó = 0.000277778 graus por segundo

        // Converte heading para radianos
        val headingRad = Math.toRadians(heading)

        // Calcula a nova posição
        val newLat = currentLocation.latitude + (speedInDegrees * cos(headingRad))
        val newLon = currentLocation.longitude + (speedInDegrees * sin(headingRad))

        return Location(
            latitude = newLat,
            longitude = newLon
        )
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Raio da Terra em km

        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distance = r * c

        return distance
    }

    private fun createNotification(): Notification {
        val channelId = "ship_simulator_channel"
        val channelName = "Ship Simulator Service"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Simulates ship movements in the background"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
            .setContentTitle("Simulador de Navios")
            .setContentText("Simulando movimentos dos navios...")
            .setSmallIcon(R.drawable.ic_ship)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        simulationJob?.cancel()
    }

    companion object {
        private const val NOTIFICATION_ID = 1002
        
        fun startService(context: Context) {
            val intent = Intent(context, ShipSimulatorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, ShipSimulatorService::class.java)
            context.stopService(intent)
        }
    }
} 