package com.navinfo.volvo.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NavinfoVolvoCall {
    private val retrofit by lazy {
        Retrofit.Builder().baseUrl("http://ec2-52-81-73-5.cn-north-1.compute.amazonaws.com.cn:8088/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    companion object {
        private var instance: NavinfoVolvoCall? = null
            get() {
                if (field == null) {
                    field = NavinfoVolvoCall()
                }
                return field
            }
    }


}