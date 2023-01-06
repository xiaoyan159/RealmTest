package com.navinfo.volvo.ui.fragments.message

import androidx.lifecycle.*
import com.easytools.tools.ToastUtils
import com.elvishew.xlog.XLog
import com.navinfo.volvo.database.entity.Attachment
import com.navinfo.volvo.database.entity.AttachmentType
import com.navinfo.volvo.database.entity.GreetingMessage
import com.navinfo.volvo.http.NavinfoVolvoCall
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*
import javax.inject.Inject


class ObtainMessageViewModel @Inject constructor() : ViewModel() {
    private val msgLiveData: MutableLiveData<GreetingMessage> by lazy {
        MutableLiveData<GreetingMessage>()
    }

    fun setCurrentMessage(msg: GreetingMessage) {
        msgLiveData.postValue(msg)
    }

    fun getMessageLiveData(): MutableLiveData<GreetingMessage> {
        return msgLiveData
    }

    // 更新消息标题
    fun updateMessageTitle(title: String) {
        this.msgLiveData.value?.name = title
        this.msgLiveData.postValue(this.msgLiveData.value)
    }

    // 更新消息附件中的照片文件
    fun updateMessagePic(picUrl: String?) {
        var hasPic = false

        for (attachment in this.msgLiveData.value!!.attachment) {
            if (attachment.attachmentType == AttachmentType.PIC) {
                if (picUrl == null || picUrl.isEmpty()) {
                    this.msgLiveData.value!!.attachment.remove(attachment)
                } else {
                    attachment.pathUrl = picUrl
                }
                hasPic = true
            }
        }
        if (!hasPic && picUrl != null) {
            this.msgLiveData.value!!.attachment.add(
                Attachment(
                    UUID.randomUUID().toString(),
                    picUrl,
                    AttachmentType.PIC
                )
            )
        }
        this.msgLiveData.postValue(this.msgLiveData.value)
    }

    // 更新消息附件中的录音文件
    fun updateMessageAudio(audioUrl: String?) {
        var hasAudio = false
        for (attachment in this.msgLiveData.value!!.attachment) {
            if (attachment.attachmentType == AttachmentType.AUDIO) {
                if (audioUrl == null || audioUrl.isEmpty()) {
                    this.msgLiveData.value!!.attachment.remove(attachment)
                } else {
                    attachment.pathUrl = audioUrl
                }
                hasAudio = true
            }
        }
        if (!hasAudio && audioUrl != null) {
            this.msgLiveData.value!!.attachment.add(
                Attachment(
                    UUID.randomUUID().toString(),
                    audioUrl,
                    AttachmentType.AUDIO
                )
            )
        }
        this.msgLiveData.postValue(this.msgLiveData.value)
    }

    // 更新发送人
    fun updateMessageSendFrom(sendFrom: String) {
        this.msgLiveData.value?.who = sendFrom
        this.msgLiveData.postValue(this.msgLiveData.value)
    }

    // 更新接收人
    fun updateMessageSendTo(sendTo: String) {
        this.msgLiveData.value?.toWho = sendTo
        this.msgLiveData.postValue(this.msgLiveData.value)
    }

    // 更新发件时间
    fun updateMessageSendTime(sendTime: String) {
        this.msgLiveData.value?.sendDate = sendTime
        this.msgLiveData.postValue(this.msgLiveData.value)
    }

    fun uploadAttachment(attachmentFile: File) {
        // 启用协程调用网络请求
        viewModelScope.launch {
            try {
                val requestFile: RequestBody =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), attachmentFile)
                val body = MultipartBody.Part.createFormData(
                    "picture",
                    attachmentFile.getName(),
                    requestFile
                )
                val result = NavinfoVolvoCall.getApi().uploadAttachment(body)
                XLog.d(result.code)
                if (result.code == 200) { // 请求成功
                    // 获取上传后的结果
                } else {
                    ToastUtils.showToast(result.msg)
                }
            } catch (e: Exception) {
                ToastUtils.showToast(e.message)
                XLog.d(e.message)
            }
        }
    }
}