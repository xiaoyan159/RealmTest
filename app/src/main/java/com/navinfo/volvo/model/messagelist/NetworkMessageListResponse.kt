package com.navinfo.volvo.model.messagelist

import com.navinfo.volvo.database.entity.GreetingMessage

data class NetworkMessageListResponse(
    val total: Int,
    val rows: List<GreetingMessage>
)