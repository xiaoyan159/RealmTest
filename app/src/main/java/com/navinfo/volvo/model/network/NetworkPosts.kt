package com.navinfo.volvo.model.network

data class NetworkMessageListPost(
    val name: String,//问候名称，非必填项
    val who: String, //我是谁
    val toWho: String, //发送给谁
    val startTime: String, //2023-01-02 15:49:50", //创建开始时间 非必填项 暂不支持按时间查询
    val endTime: String,//" 2023-01-03 15:52:50", //创建结束时间 非必填项 暂不支持按时间查询
    val pageSize: String, //查询数量
    val pageNum: String, //分页查询
) {
    constructor(who: String, toWho: String) : this("", who, toWho, "", "", "10", "1") {

    }
}

data class NetworkDeleteMessagePost(
    val id: Long
)