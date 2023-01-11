package com.navinfo.volvo.database.dao

import android.util.Log
import androidx.paging.PagingSource
import androidx.room.*
import com.navinfo.volvo.Constant
import com.navinfo.volvo.database.entity.GreetingMessage
import com.navinfo.volvo.database.entity.GreetingMessageKtx
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
    @Query("SELECT count(id) FROM GreetingMessage WHERE status = '${Constant.message_status_late}'")
    fun countUnreadByFlow(): Flow<Long>

    /**
     * 分页查询
     */
    @Query("SELECT * FROM GreetingMessage order by sendDate DESC")
    fun findAllByDataSource(): PagingSource<Int, GreetingMessage>

    /**
     * 检查某条数据是否存在
     */
    @Query("SELECT uuid,id,status From GreetingMessage WHERE id = :id LIMIT 1")
    suspend fun getMessageId(id: Long): GreetingMessageKtx?

    /**
     *
     */
    @Transaction
    suspend fun insertOrUpdate(list: List<GreetingMessage>) {
        for (message in list) {
            val locMessage = getMessageId(message.id)
            if (message.version == Constant.message_version_right_off && message.status == Constant.message_status_late)
                message.status = Constant.message_status_send_over
            if (locMessage == null) {
                Log.e("jingo", "插入数据 id=${message.id} ")
                insert(message)
            } else {
                if (locMessage.status != message.status) {
                    message.uuid = locMessage.uuid
                    update(message)
                }
            }
            Log.e("jingo", "insertOrUpdate end")
        }
    }


}