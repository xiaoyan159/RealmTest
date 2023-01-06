package com.navinfo.volvo.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.reflect.TypeToken
import com.navinfo.volvo.tools.GsonUtil
import org.jetbrains.annotations.NotNull
import javax.inject.Inject

@Entity(tableName = "User")
@TypeConverters()
data class User(
    @PrimaryKey()
    @NotNull
    val id: String,
    var name: String,
    var nickname: String,
){
    @Inject constructor():this("${System.currentTimeMillis()}","df","sdfds")
}

class UserConverters() {
    @TypeConverter
    fun stringToUser(value: String): User {
        val type = object : TypeToken<User>() {

        }.type
        return GsonUtil.getInstance().fromJson(value, type)
    }

    @TypeConverter
    fun userToString(user: User): String {
        return GsonUtil.getInstance().toJson(user)
    }

    @TypeConverter
    fun userListToString(list: List<User>): String {
        return GsonUtil.getInstance().toJson(list)
    }

    @TypeConverter
    fun stringToUserList(value: String): List<User> {
        val type = object : TypeToken<List<User>>() {

        }.type
        return GsonUtil.getInstance().fromJson(value, type)
    }
}