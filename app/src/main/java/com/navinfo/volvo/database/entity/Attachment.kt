package com.navinfo.volvo.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.navinfo.volvo.tools.GsonUtil

@Entity(tableName = "Attachment")
data class Attachment(
    @PrimaryKey
    var id: String,
    var pathUrl: String,
    var attachmentType: AttachmentType
)

enum class AttachmentType {
    PIC, AUDIO
}

class AttachmentConverters() {
    @TypeConverter
    fun stringToAttachment(value: String): Attachment {
        val type = object : TypeToken<Attachment>() {

        }.type
        return GsonUtil.getInstance().fromJson(value, type)
    }

    @TypeConverter
    fun attachmentToString(attachment: Attachment): String {
        return GsonUtil.getInstance().toJson(attachment)
    }

    @TypeConverter
    fun listToString(list: MutableList<Attachment>): String {
        return GsonUtil.getInstance().toJson(list)
    }

    @TypeConverter
    fun stringToList(value: String): MutableList<Attachment> {
        val type = object : TypeToken<MutableList<Attachment>>() {

        }.type
        return GsonUtil.getInstance().fromJson(value, type)
    }
}
