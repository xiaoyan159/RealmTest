package com.navinfo.volvo.http

class DefaultResponse<T> {
    var code: Int = 0
    var message: String = ""
    var data: T? = null
}