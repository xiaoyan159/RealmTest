package com.navinfo.volvo.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull

@Entity(tableName = "GreetingMessage")
@TypeConverters(AttachmentConverters::class)
data class GreetingMessage @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    var uuid: Long = 0,
    var id: Long = 0,
    var searchValue: String? = "",
    var createBy: String? = "",
    var createTime: String? = "",
    var updateBy: String? = "",
    var updateTime: String? = "",
    var remark: String? = "",
    var name: String? = "",
    var imageUrl: String? = "",
    var mediaUrl: String? = "",
    var who: String? = "",
    var toWho: String? = "",
    var sendDate: String? = "",
    var status: String? = "",
    var isSkip: String? = "",
    var skipUrl: String? = "",
    var startTime: String? = "",
    var endTime: String? = "",
    var sendVehicle: String? = "",
    var sendSex: String? = "",
    var sendAge: String? = "",
    var sendNum: String? = "",
    var sendVins: String? = "",
    var sendType: String? = "",
    var del: String? = "",
    var version: String? = "",
    /**
     * 附件列表
     */
    var attachment: MutableList<Attachment> = mutableListOf(),
    var read: Boolean = false,
)
