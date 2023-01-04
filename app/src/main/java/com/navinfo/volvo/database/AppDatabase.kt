package com.navinfo.volvo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.navinfo.volvo.database.dao.MessageDao
import com.navinfo.volvo.database.dao.UserDao
import com.navinfo.volvo.model.Attachment
import com.navinfo.volvo.model.Message
import com.navinfo.volvo.model.User

@Database(
    entities = [Message::class, Attachment::class, User::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getMessageDao(): MessageDao

    abstract fun getUserDao(): UserDao
}