package com.navinfo.volvo.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.navinfo.volvo.database.entity.GreetingMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface GreetingMessageDao {

    @Query("DELETE from GreetingMessage WHERE id=:id")
    suspend fun deleteById(id: Long)

    @Insert
    suspend fun insert(message: GreetingMessage): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(message: GreetingMessage)

    /**
     * 未读消息统计
     */
    @Query("SELECT count(id) FROM GreetingMessage WHERE read = 0")
    fun countUnreadByFlow(): Flow<Long>

    /**
     * 分页查询
     */
    @Query("SELECT * FROM GreetingMessage order by sendDate DESC")
    fun findAllByDataSource(): PagingSource<Int, GreetingMessage>

    /**
     * 检查某条数据是否存在
     */
    @Query("SELECT id From GreetingMessage WHERE id = :id LIMIT 1")
    suspend fun getMessageId(id: Long): Long

    /**
     *
     */
    @Transaction
    suspend fun insertOrUpdate(list: List<GreetingMessage>) {
        for (message in list) {
            val id = getMessageId(message.id)
            if (id == 0L) {
                insert(message)
            } else {
                update(message)
            }
        }
    }


}