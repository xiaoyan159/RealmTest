package com.navinfo.volvo.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.navinfo.volvo.model.proto.LoginUser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class UtilModule {

    @Provides
    @Singleton
    fun provideLoginUserDataStore(
        context: Context,
        serializer: Serializer<LoginUser>
    ): DataStore<LoginUser> = DataStoreFactory.create(
        serializer = serializer,
        produceFile = { context.dataStoreFile("login_user") }
    )

}