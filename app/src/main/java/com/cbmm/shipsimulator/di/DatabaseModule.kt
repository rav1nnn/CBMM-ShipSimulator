package com.cbmm.shipsimulator.di

import android.content.Context
import com.cbmm.shipsimulator.data.local.ShipDatabase
import com.cbmm.shipsimulator.data.local.dao.ShipDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideShipDatabase(
        @ApplicationContext context: Context
    ): ShipDatabase = ShipDatabase.getDatabase(context)
    
    @Provides
    fun provideShipDao(database: ShipDatabase): ShipDao = database.shipDao()
}
