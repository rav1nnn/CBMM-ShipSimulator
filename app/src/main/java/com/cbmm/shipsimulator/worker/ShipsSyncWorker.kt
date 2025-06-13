package com.cbmm.shipsimulator.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cbmm.shipsimulator.data.repository.ShipRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class ShipsSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ShipRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("Synchronizing ships data...")
            // Força a atualização dos dados
            repository.getAllShips().collect {}
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error synchronizing ships data")
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "ShipsSyncWorker"
    }
}
