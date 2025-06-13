package com.cbmm.shipsimulator.di

import android.content.Context
import androidx.work.WorkManager
import com.cbmm.shipsimulator.data.api.ShipApiService
import com.cbmm.shipsimulator.data.local.ShipDatabase
import com.cbmm.shipsimulator.data.local.dao.ShipDao
import com.cbmm.shipsimulator.data.local.dao.ShipRouteDao
import com.cbmm.shipsimulator.data.repository.FakeShipRepository
import com.cbmm.shipsimulator.data.repository.ShipRepository
import com.cbmm.shipsimulator.data.repository.ShipRepositoryImpl
import com.cbmm.shipsimulator.service.ShipTrackingService
import com.cbmm.shipsimulator.sync.ShipsSyncManager
import dagger.Binds
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
abstract class AppModule {
    
    @Binds
    @Singleton
    abstract fun bindShipRepository(
        shipRepositoryImpl: ShipRepositoryImpl
    ): ShipRepository
    
    companion object {
        @Provides
        @Singleton
        fun provideShipDatabase(@ApplicationContext context: Context): ShipDatabase {
            return ShipDatabase.getInstance(context)
        }
        
        @Provides
        fun provideShipDao(database: ShipDatabase): ShipDao = database.shipDao()
        
        @Provides
        fun provideShipRouteDao(database: ShipDatabase): ShipRouteDao = database.shipRouteDao()
        
        @Provides
        @Singleton
        fun provideShipApiService(): ShipApiService {
            return Retrofit.Builder()
                .baseUrl("https://api.example.com/") // Substitua pela URL base da sua API
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ShipApiService::class.java)
        }
        
        @Provides
        @Singleton
        fun provideFakeShipRepository(): FakeShipRepository = FakeShipRepository()
        
        @Provides
        @Singleton
        fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
            return WorkManager.getInstance(context)
        }
        
        @Provides
        @Singleton
        fun provideShipsSyncManager(
            @ApplicationContext context: Context,
            workerFactory: HiltWorkerFactory
        ): ShipsSyncManager {
            return ShipsSyncManager(context, workerFactory)
        }
        
        @Provides
        @Singleton
        fun provideShipTrackingServiceBinder() = ShipTrackingService.Binder()
    }
}
