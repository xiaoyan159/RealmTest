package com.navinfo.volvo.db.dao.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data  class User(
    @PrimaryKey()
    val id:String,
    var name:String,
    var nickname:String,

)