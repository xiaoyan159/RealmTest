package com.navinfo.volvo.http

import com.navinfo.volvo.db.dao.entity.Attachment
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.File

class NavinfoVolvoCall {
    companion object {
        private val service by lazy {
            Retrofit.Builder().baseUrl("http://ec2-52-81-73-5.cn-north-1.compute.amazonaws.com.cn:8088/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NavinfoVolvoService::class.java)
        }

        private var instance: NavinfoVolvoCall? = null
            get() {
                if (field == null) {
                    field = NavinfoVolvoCall()
                }
                return field
            }

        fun getApi(): NavinfoVolvoService {
            return service
        }
    }
}