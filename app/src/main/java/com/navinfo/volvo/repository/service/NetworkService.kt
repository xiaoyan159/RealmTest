package com.navinfo.volvo.repository.service

import com.navinfo.volvo.http.DefaultResponse
import com.navinfo.volvo.model.messagelist.NetworkMessageListResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkService {
    @POST("/navi/cardDelivery/queryCardListByApp")
    suspend fun queryCardListByApp(@Body body: RequestBody): Response<DefaultResponse<NetworkMessageListResponse>>
}