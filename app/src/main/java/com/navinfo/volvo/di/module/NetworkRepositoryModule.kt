package com.navinfo.volvo.di.module

import com.navinfo.volvo.repository.network.NetworkRepository
import com.navinfo.volvo.repository.network.NetworkRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class NetworkRepositoryModule {
    @Binds
    abstract fun bindNetworkRepository(networkRepositoryImp: NetworkRepositoryImp): NetworkRepository
}