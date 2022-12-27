package com.navinfo.volvo.db.dao.entity

import androidx.room.PrimaryKey

class User(
    @PrimaryKey()
    val id:String,
    var name:String,
    var nickname:String,

) {
}