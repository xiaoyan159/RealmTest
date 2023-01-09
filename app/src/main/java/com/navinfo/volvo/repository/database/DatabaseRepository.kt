package com.navinfo.volvo.repository.database

import androidx.paging.PagingData
import com.navinfo.volvo.database.entity.GreetingMessage
import kotlinx.coroutines.flow.Flow

/**
 * 数据库操作接口
 */
interface DatabaseRepository {
    fun getMessageByPaging(): Flow<PagingData<GreetingMessage>>
}