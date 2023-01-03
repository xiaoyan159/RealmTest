package com.navinfo.volvo.http

import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface NavinfoVolvoService {
    @POST("/navi/cardDelivery/insertCardByApp")
    fun insertCardByApp(@Body insertData: MutableMap<String, String>)
    @POST("/navi/cardDelivery/updateCardByApp")
    fun updateCardByApp(@Body updateData: MutableMap<String, String>)
    @POST("/navi/cardDelivery/queryCardListByApp")
    fun queryCardListByApp(@Body queryData: MutableMap<String, String>)
    @POST("/navi/cardDelivery/deleteCardByApp")
    fun deleteCardByApp(@Body deleteData: MutableMap<String, String>)
    @POST("/img/upload")
    @Multipart
    fun uploadAttachment(@Part("picture") attachmentFile: File)
    @POST("/img/download")
    fun downLoadAttachment(@Body downloadData: MutableMap<String, String>)
}