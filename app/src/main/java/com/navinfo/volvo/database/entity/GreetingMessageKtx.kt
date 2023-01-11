package com.navinfo.volvo.database.entity

import androidx.room.ColumnInfo

/**
 * 用来用较少的字段查询数是否存在
 */
data class GreetingMessageKtx(
    @ColumnInfo(name = "uuid")
    var uuid: Long = 0,
    @ColumnInfo(name = "id")
    var id: Long = 0,
    @ColumnInfo(name = "status")
    var status: String? = "",
)