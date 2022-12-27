package com.navinfo.volvo.db.dao.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Attachment {
    lateinit var path:String
    lateinit var attachmentType: attachmentType
}

enum class attachmentType {
    PIC, AUDIO
}

class AttachmentConvert {
    private val gson = Gson()

    @TypeConverter
    fun objectToString(list: List<Attachment>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToObject(json: String?): List<Attachment> {
        val listType: Type = object : TypeToken<List<Attachment>>() {}.type
        return gson.fromJson(json, listType)
    }
}