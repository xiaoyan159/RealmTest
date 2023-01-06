package com.navinfo.volvo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.navinfo.volvo.database.dao.GreetingMessageDao
import com.navinfo.volvo.database.dao.UserDao
import com.navinfo.volvo.database.entity.Attachment
import com.navinfo.volvo.database.entity.GreetingMessage
import com.navinfo.volvo.database.entity.User

@Database(
    entities = [GreetingMessage::class, Attachment::class, User::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getMessageDao(): GreetingMessageDao

    abstract fun getUserDao(): UserDao
}