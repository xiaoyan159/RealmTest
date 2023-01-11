package com.navinfo.volvo

class Constant {
    companion object {
        /**
         * 服务器地址
         */
        const val SERVER_ADDRESS = "http://ec2-52-81-73-5.cn-north-1.compute.amazonaws.com.cn:8088/"
        const val DEBUG = true

        const val message_status_late = "预约，待发送"
        const val message_status_send_over = "已发送"
        const val message_version_right_off = "1" //立即发送

        const val MESSAGE_PAGE_SIZE = 30 //消息列表一页最多数量
    }

}