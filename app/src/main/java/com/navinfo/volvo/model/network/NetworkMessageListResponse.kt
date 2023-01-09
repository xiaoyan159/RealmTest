package com.navinfo.volvo.model.network

import com.navinfo.volvo.database.entity.GreetingMessage

data class NetworkMessageListResponse(
    val total: Int,
    val rows: List<GreetingMessage>?
)