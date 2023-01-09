package com.navinfo.volvo

import java.lang.Boolean

class Constant {
    companion object{
        /**
         * 服务器地址
         */
        const val SERVER_ADDRESS = "http://ec2-52-81-73-5.cn-north-1.compute.amazonaws.com.cn:8088/"
        val DEBUG = Boolean.parseBoolean("true")

        const val message_status_late = "预约，待发送"
    }

}