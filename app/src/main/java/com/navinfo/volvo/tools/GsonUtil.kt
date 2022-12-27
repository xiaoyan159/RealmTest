package com.navinfo.volvo.tools

import com.google.gson.Gson

class GsonUtil {
    companion object {
        fun getInstance() = InstanceHelper.gson
    }

    object InstanceHelper {
        val gson: Gson = Gson()
    }
}