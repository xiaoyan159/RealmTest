package com.navinfo.volvo.ui.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.navinfo.volvo.db.dao.entity.Message
import com.navinfo.volvo.db.dao.entity.attachmentType

class ObtainMessageViewModel: ViewModel() {
    private val msgLiveData: MutableLiveData<Message> by lazy {
        MutableLiveData<Message>()
    }

    fun setCurrentMessage(msg: Message) {
        msgLiveData.postValue(msg)
    }

    fun getMessageLiveData(): MutableLiveData<Message> {
        return msgLiveData
    }

    // 更新消息标题
    fun updateMessageTitle(title: String) {
        this.msgLiveData.value?.title = title
        this.msgLiveData.postValue(this.msgLiveData.value)
    }

    // 更新消息附件中的照片文件
    fun updateMessagePic(picUrl: String) {
        for (attachment in this.msgLiveData.value!!.attachment) {
            if (attachment.attachmentType == attachmentType.PIC) {
                attachment.path = picUrl
            }
        }
        this.msgLiveData.postValue(this.msgLiveData.value)
    }

    // 更新消息附件中的录音文件
    fun updateMessageAudio(audioUrl: String) {
        for (attachment in this.msgLiveData.value!!.attachment) {
            if (attachment.attachmentType == attachmentType.AUDIO) {
                attachment.path = audioUrl
            }
        }
        this.msgLiveData.postValue(this.msgLiveData.value)
    }

    // 更新发送人
    fun updateMessageSendFrom(sendFrom: String) {
        this.msgLiveData.value?.fromId = sendFrom
        this.msgLiveData.postValue(this.msgLiveData.value)
    }

    // 更新接收人
    fun updateMessageSendTo(sendTo: String) {
        this.msgLiveData.value?.toId = sendTo
        this.msgLiveData.postValue(this.msgLiveData.value)
    }

    // 更新发件时间
    fun updateMessageSendTime(sendTime: String) {
        this.msgLiveData.value?.sendDate = sendTime
        this.msgLiveData.postValue(this.msgLiveData.value)
    }
}