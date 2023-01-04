package com.navinfo.volvo.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.navinfo.volvo.database.entity.GreetingMessage

@Dao
interface GreetingMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg check: GreetingMessage)

    @Query("SELECT * FROM GreetingMessage where id =:id")
    fun findCheckManagerById(id: Long): GreetingMessage?

    @Query("SELECT * FROM GreetingMessage")
    fun findList(): List<GreetingMessage>
}