package com.navinfo.volvo.di.module

import android.content.Context
import androidx.room.Room
import com.navinfo.volvo.database.AppDatabase
import com.navinfo.volvo.database.dao.MessageDao
import com.navinfo.volvo.database.dao.UserDao
import com.tencent.wcdb.database.SQLiteCipherSpec
import com.tencent.wcdb.room.db.WCDBOpenHelperFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): AppDatabase {
        val DB_PASSWORD = "123456";
        val cipherSpec = SQLiteCipherSpec()
            .setPageSize(1024)
            .setSQLCipherVersion(3)
        val factory = WCDBOpenHelperFactory()
            .passphrase(DB_PASSWORD.toByteArray())  // passphrase to the database, remove this line for plain-text
            .cipherSpec(cipherSpec)               // cipher to use, remove for default settings
            .writeAheadLoggingEnabled(true)       // enable WAL mode, remove if not needed
            .asyncCheckpointEnabled(true);            // enable asynchronous checkpoint, remove if not needed

        return Room.databaseBuilder(context, AppDatabase::class.java, "NavinfoVolvoDb")

            // [WCDB] Specify open helper to use WCDB database implementation instead
            // of the Android framework.
            .openHelperFactory(factory)

            // Wipes and rebuilds instead of migrating if no Migration object.
            // Migration is not part of this codelab.
//            .fallbackToDestructiveMigration().addCallback(sRoomDatabaseCallback)
            .build();
    }

    @Singleton
    @Provides
    fun provideMessageDao(database: AppDatabase): MessageDao {
        return database.getMessageDao()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.getUserDao()
    }
}
