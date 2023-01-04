package com.navinfo.volvo.di.module

import com.navinfo.volvo.repository.NetworkDataSource
import com.navinfo.volvo.repository.NetworkDataSourceImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class NetworkDataModule {
    @Binds
    abstract fun bindNetworkData(networkDataSourceImp: NetworkDataSourceImp): NetworkDataSource
}