package com.cbmm.shipsimulator.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.room.Room
import androidx.work.WorkManager
import com.cbmm.shipsimulator.data.api.ShipApiService
import com.cbmm.shipsimulator.data.local.AppDatabase
import com.cbmm.shipsimulator.data.local.dao.ShipDao
import com.cbmm.shipsimulator.data.local.dao.ShipRouteDao
import com.cbmm.shipsimulator.data.repository.FakeShipRepository
import com.cbmm.shipsimulator.data.repository.ShipRepository
import com.cbmm.shipsimulator.data.repository.ShipRepositoryImpl
import com.cbmm.shipsimulator.sync.ShipsSyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ship_simulator_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideShipDao(database: AppDatabase): ShipDao {
        return database.shipDao()
    }

    @Provides
    @Singleton
    fun provideShipRouteDao(database: AppDatabase): ShipRouteDao {
        return database.shipRouteDao()
    }

    @Provides
    @Singleton
    fun provideShipApiService(): ShipApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/") // Substitua pela URL real da API
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ShipApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideShipRepository(
        apiService: ShipApiService,
        shipDao: ShipDao,
        shipRouteDao: ShipRouteDao
    ): ShipRepository {
        return ShipRepositoryImpl(apiService, shipDao, shipRouteDao)
    }

    @Provides
    @Singleton
    fun provideFakeShipRepository(): FakeShipRepository {
        return FakeShipRepository()
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideShipsSyncManager(
        @ApplicationContext context: Context
    ): ShipsSyncManager {
        return ShipsSyncManager(context)
    }
}
