package com.cbmm.shipsimulator.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cbmm.shipsimulator.data.repository.ShipRepository
import android.util.Log
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ShipsSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val shipRepository: ShipRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val result = shipRepository.getAllShips().first()
            when (result) {
                is com.cbmm.shipsimulator.util.NetworkResult.Success -> Result.success()
                is com.cbmm.shipsimulator.util.NetworkResult.Error -> Result.retry()
                is com.cbmm.shipsimulator.util.NetworkResult.Loading -> Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "ShipsSyncWorker"
    }
}
