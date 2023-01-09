package com.navinfo.volvo.di.module

import androidx.datastore.core.Serializer
import com.navinfo.volvo.model.proto.LoginUser
import com.navinfo.volvo.model.proto.LoginUserSerializer
import com.navinfo.volvo.repository.preferences.PreferencesRepository
import com.navinfo.volvo.repository.preferences.PreferencesRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class UtilRepositoryModule {
    @Binds
    abstract fun providePreferencesRepository(preferencesRepositoryImp: PreferencesRepositoryImp): PreferencesRepository

    @Binds
    abstract fun userLocalSerializer(impl: LoginUserSerializer): Serializer<LoginUser>
}