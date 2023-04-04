package com.navinfo.volvo.model

import io.realm.kotlin.types.RealmObject

class Dog : RealmObject {
    var name: String = ""
    var age: Int = 0
}