package com.navinfo.volvo.model.network

import com.navinfo.volvo.Constant.Companion.MESSAGE_PAGE_SIZE

data class NetworkMessageListPost @JvmOverloads constructor(
    var name: String = "",//问候名称，非必填项
    var who: String, //我是谁
    var toWho: String = "", //发送给谁
    var startTime: String = "", //2023-01-02 15:49:50", //创建开始时间 非必填项 暂不支持按时间查询
    var endTime: String = "",//" 2023-01-03 15:52:50", //创建结束时间 非必填项 暂不支持按时间查询
    var pageSize: String = "$MESSAGE_PAGE_SIZE", //查询数量
    var pageNum: String = "1", //分页查询
)

data class NetworkDeleteMessagePost(
    val id: Long
)