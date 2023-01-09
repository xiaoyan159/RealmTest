package com.navinfo.volvo.ui

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.navinfo.volvo.database.dao.GreetingMessageDao
import com.navinfo.volvo.database.entity.GreetingMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val messageDao: GreetingMessageDao,
) : ViewModel() {


    fun getUnreadCount(): Flow<Long> = messageDao.countUnreadByFlow()

}