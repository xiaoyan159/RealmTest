package com.navinfo.volvo.repository.database

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.navinfo.volvo.database.dao.GreetingMessageDao
import com.navinfo.volvo.database.entity.GreetingMessage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepositoryImp @Inject constructor(
    private val messageDao: GreetingMessageDao
) : DatabaseRepository {
    companion object {
        const val PAGE_SIZE = 20
    }

    override fun getMessageByPaging(): Flow<PagingData<GreetingMessage>> {
        return Pager(PagingConfig(PAGE_SIZE)) {
            messageDao.findAllByDataSource()
        }.flow
    }
}