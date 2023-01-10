package com.navinfo.volvo.repository.network

import com.navinfo.volvo.http.DefaultResponse
import com.navinfo.volvo.model.network.NetworkDeleteMessagePost
import com.navinfo.volvo.model.network.NetworkMessageListPost
import com.navinfo.volvo.model.network.NetworkMessageListResponse
import com.navinfo.volvo.util.NetResult

/**
 * 网络访问接口
 */
interface NetworkRepository {
    /**
     * 获取问候列表
     */
    suspend fun getMessageList(message: NetworkMessageListPost): NetResult<DefaultResponse<NetworkMessageListResponse>>

    /**
     *删除问候
     */
    suspend fun deleteMessage(message: NetworkDeleteMessagePost): NetResult<DefaultResponse<*>>
}