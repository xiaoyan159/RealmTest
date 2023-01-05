package com.navinfo.volvo.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.navinfo.volvo.database.entity.GreetingMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface GreetingMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg check: GreetingMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<GreetingMessage>)

    @Query("SELECT * FROM GreetingMessage where id =:id")
    fun findCheckManagerById(id: Long): GreetingMessage?

    @Query("SELECT * FROM GreetingMessage")
    fun findAllByFlow(): Flow<List<GreetingMessage>>

    @Query("SELECT * FROM GreetingMessage")
    fun findAllByDataSource(): PagingSource<Int, GreetingMessage>
}