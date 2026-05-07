package com.zichan.app.di

import android.content.Context
import androidx.room.Room
import com.zichan.app.data.database.AppDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "zichan.db").build()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase) = database.categoryDao()

    @Provides
    fun provideLocationDao(database: AppDatabase) = database.locationDao()

    @Provides
    fun provideAssetDao(database: AppDatabase) = database.assetDao()

    @Provides
    fun providePersonDao(database: AppDatabase) = database.personDao()

    @Provides
    fun provideLendRecordDao(database: AppDatabase) = database.lendRecordDao()

    @Provides
    fun provideAssetLogDao(database: AppDatabase) = database.assetLogDao()
}
