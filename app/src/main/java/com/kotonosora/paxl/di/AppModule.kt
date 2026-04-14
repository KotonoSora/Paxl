package com.kotonosora.paxl.di

import android.content.Context
import com.kotonosora.paxl.repository.BillingRepository
import com.kotonosora.paxl.repository.DataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStoreRepository(@ApplicationContext context: Context): DataStoreRepository {
        return DataStoreRepository(context)
    }

    @Provides
    @Singleton
    fun provideBillingRepository(
        @ApplicationContext context: Context,
        dataStoreRepository: DataStoreRepository
    ): BillingRepository {
        return BillingRepository(context, dataStoreRepository)
    }
}
