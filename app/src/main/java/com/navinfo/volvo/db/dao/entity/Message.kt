package com.navinfo.volvo.db.dao.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "message")
@TypeConverters(AttachmentConverters::class)
data class Message @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var netId: String = "",
    /**
     *标题
     */
    var title: String = "",
    /**
     * 信息内容
     */
    var message: String = "",
    /**
     * 操作时间
     */
    var optionDate: String = "",
    /**
     * 发送时间
     */
    var sendDate: String = "",
    /**
     * 信息状态
     */
    var status: Int = 1,
    /**
     * 发送者ID
     */
    var fromId: String = "",
    /**
     * 接收者ID
     */
    var toId: String = "",
    /**
     * 附件列表
     */
    var attachment: MutableList<Attachment> = mutableListOf()
)