package com.navinfo.volvo.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.navinfo.volvo.Constant
import kotlinx.parcelize.Parcelize

@Entity(tableName = "GreetingMessage")
@TypeConverters(AttachmentConverters::class)
@Parcelize
data class GreetingMessage @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uuid")
    var uuid: Long = 0,
    @ColumnInfo(name = "id")
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
    @ColumnInfo(name = "status")
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
    @ColumnInfo(name = "version")
    var version: String? = Constant.message_version_right_off,
//    /**
//     * 附件列表
//     */
//    var attachment: MutableList<Attachment> = mutableListOf(),
    var read: Boolean = false,
) : Parcelable
