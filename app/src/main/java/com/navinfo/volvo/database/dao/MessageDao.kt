package com.navinfo.volvo.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.navinfo.volvo.model.Message

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg check: Message)

    @Query("SELECT * FROM Message where id =:id")
    fun findCheckManagerById(id: Long): Message?

    @Query("SELECT * FROM Message")
    fun findList(): List<Message>
}