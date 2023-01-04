package com.navinfo.volvo.repository

import com.navinfo.volvo.model.Message
import com.navinfo.volvo.model.network.NetworkPostMessage
import com.navinfo.volvo.util.NetResult

interface NetworkDataSource  {
    suspend fun getCardList(message: NetworkPostMessage): NetResult<List<Message>>
}