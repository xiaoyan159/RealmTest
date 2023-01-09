package com.navinfo.volvo.repository.network

import com.navinfo.volvo.http.DefaultResponse
import com.navinfo.volvo.model.messagelist.NetworkMessageListPost
import com.navinfo.volvo.model.messagelist.NetworkMessageListResponse
import com.navinfo.volvo.util.NetResult

/**
 * 网络访问接口
 */
interface NetworkRepository  {
    suspend fun getCardList(message: NetworkMessageListPost): NetResult<DefaultResponse<NetworkMessageListResponse>>
}