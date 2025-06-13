package com.cbmm.shipsimulator.sync

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.cbmm.shipsimulator.worker.ShipsSyncWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShipsSyncManager @Inject constructor(
    private val context: Context,
    private val workerFactory: HiltWorkerFactory
) {
    
    fun scheduleSync(intervalHours: Long = 1) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<ShipsSyncWorker>(
            repeatInterval = intervalHours,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 15,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ShipsSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            syncRequest
        )
    }

    fun cancelSync() {
        WorkManager.getInstance(context)
            .cancelUniqueWork(ShipsSyncWorker.WORK_NAME)
    }
}
