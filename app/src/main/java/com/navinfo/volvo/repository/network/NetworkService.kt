package com.navinfo.volvo.repository.network

import com.navinfo.volvo.http.DefaultResponse
import com.navinfo.volvo.model.network.NetworkMessageListResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkService {
    /**
     * 获取问候列表
     */
    @POST("/navi/cardDelivery/queryCardListByApp")
    suspend fun queryMessageListByApp(@Body body: RequestBody): Response<DefaultResponse<NetworkMessageListResponse>>

    /**
     * 删除问候
     */
    @POST("/navi/cardDelivery/deleteCardByApp")
    suspend fun deleteMessage(@Body body: RequestBody): Response<DefaultResponse<*>>

}