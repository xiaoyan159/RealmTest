package com.navinfo.volvo.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NavinfoVolvoCall {
    companion object {
        private val service by lazy {
//            val baseUrl = "http://ec2-52-81-73-5.cn-north-1.compute.amazonaws.com.cn:8088/"
            val baseUrl = "http://54.223.225.147:8088/"

            Retrofit.Builder().baseUrl(baseUrl)
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