package com.navinfo.volvo.http

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File

interface NavinfoVolvoService {
    @POST("/navi/cardDelivery/insertCardByApp")
    suspend fun insertCardByApp(@Body insertData: Map<String, String>):DefaultResponse<String>
    @POST("/navi/cardDelivery/updateCardByApp")
    suspend fun updateCardByApp(@Body updateData: Map<String, String>):DefaultResponse<String>
    @POST("/navi/cardDelivery/queryCardListByApp")
    fun queryCardListByApp(@Body queryData: MutableMap<String, String>)
    @POST("/navi/cardDelivery/deleteCardByApp")
    fun deleteCardByApp(@Body deleteData: MutableMap<String, String>)
    @POST("/img/upload")
    @Multipart
    suspend fun uploadAttachment(@Part attachmentFile: MultipartBody.Part):DefaultResponse<MutableMap<String, String>>
    @POST("/img/download")
    suspend fun downLoadAttachment(@Body downloadData: Map<String, String>):DefaultResponse<String>
    @Streaming
    @GET
    fun downloadFile(@Url url: String): Call<ResponseBody>
}