package com.navinfo.volvo.ui.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.navinfo.volvo.db.dao.entity.Attachment
import com.navinfo.volvo.db.dao.entity.Message
import com.navinfo.volvo.db.dao.entity.AttachmentType
import java.util.UUID

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
    fun updateMessagePic(picUrl: String?) {
        var hasPic = false

        for (attachment in this.msgLiveData.value!!.attachment) {
            if (attachment.attachmentType == AttachmentType.PIC) {
                if (picUrl==null||picUrl.isEmpty()) {
                    this.msgLiveData.value!!.attachment.remove(attachment)
                } else {
                    attachment.pathUrl = picUrl
                }
                hasPic = true
            }
        }
        if (!hasPic&&picUrl!=null) {
            this.msgLiveData.value!!.attachment.add(Attachment(UUID.randomUUID().toString(), picUrl, AttachmentType.PIC))
        }
        this.msgLiveData.postValue(this.msgLiveData.value)
    }

    // 更新消息附件中的录音文件
    fun updateMessageAudio(audioUrl: String?) {
        var hasAudio = false
        for (attachment in this.msgLiveData.value!!.attachment) {
            if (attachment.attachmentType == AttachmentType.AUDIO) {
                if (audioUrl==null||audioUrl.isEmpty()) {
                    this.msgLiveData.value!!.attachment.remove(attachment)
                } else {
                    attachment.pathUrl = audioUrl
                }
                hasAudio = true
            }
        }
        if (!hasAudio&&audioUrl!=null) {
            this.msgLiveData.value!!.attachment.add(Attachment(UUID.randomUUID().toString(), audioUrl, AttachmentType.AUDIO))
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