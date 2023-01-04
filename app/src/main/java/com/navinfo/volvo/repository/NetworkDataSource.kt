package com.navinfo.volvo.repository

import com.navinfo.volvo.http.DefaultResponse
import com.navinfo.volvo.model.messagelist.NetworkMessageListPost
import com.navinfo.volvo.model.messagelist.NetworkMessageListResponse
import com.navinfo.volvo.util.NetResult

interface NetworkDataSource  {
    suspend fun getCardList(message: NetworkMessageListPost): NetResult<DefaultResponse<NetworkMessageListResponse>>
}