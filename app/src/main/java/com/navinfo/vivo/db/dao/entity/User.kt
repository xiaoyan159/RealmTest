package com.navinfo.vivo.db.dao.entity

import androidx.room.PrimaryKey
import java.net.IDN

class User(
    @PrimaryKey()
    val id:String,
    var name:String,
    var nickname:String,

) {
}