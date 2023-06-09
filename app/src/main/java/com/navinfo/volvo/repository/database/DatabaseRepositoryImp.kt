package com.navinfo.volvo.repository.database

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.navinfo.volvo.database.AppDatabase
import com.navinfo.volvo.database.dao.GreetingMessageDao
import com.navinfo.volvo.database.entity.GreetingMessage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepositoryImp @Inject constructor(
    private val messageDao: GreetingMessageDao,
) : DatabaseRepository {

    /**
     * 分页加载消息
     */
    override fun getMessageByPaging(): Flow<PagingData<GreetingMessage>> {
        return Pager(PagingConfig(10,2,false,10)) {
            messageDao.findAllByDataSource()
        }.flow
    }

}