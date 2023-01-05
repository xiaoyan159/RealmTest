package com.navinfo.volvo.di.module

import com.navinfo.volvo.repository.database.DatabaseRepository
import com.navinfo.volvo.repository.database.DatabaseRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class DatabaseRepositoryModule {
    @Binds
    abstract fun bingDatabaseRepository(databaseRepositoryImp: DatabaseRepositoryImp): DatabaseRepository
}