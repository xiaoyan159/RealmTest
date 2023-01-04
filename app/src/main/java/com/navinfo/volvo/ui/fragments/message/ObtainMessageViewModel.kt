package com.navinfo.volvo.ui.fragments.message

import androidx.lifecycle.*
import com.easytools.tools.ToastUtils
import com.elvishew.xlog.XLog
import com.navinfo.volvo.http.NavinfoVolvoCall
import com.navinfo.volvo.model.Attachment
import com.navinfo.volvo.model.AttachmentType
import com.navinfo.volvo.model.Message
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*


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

    // 获取照片url
    fun getImageAttachment(attachementList: List<Attachment>): Attachment? {
        for (attachment in attachementList) {
            if (attachment.attachmentType == AttachmentType.PIC) {
                return attachment
            }
        }
        return null
    }

    // 获取音频url
    fun getAudioAttachment(attachementList: List<Attachment>): Attachment? {
        for (attachment in attachementList) {
            if (attachment.attachmentType == AttachmentType.AUDIO) {
                return attachment
            }
        }
        return null
    }

    // 获取发送时间
    fun getSendDate(){

    }

    // 上传附件文件
    fun uploadAttachment(attachmentFile: File, attachmentType: AttachmentType) {
        // 启用协程调用网络请求
        viewModelScope.launch {
            try {
                val requestFile: RequestBody =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), attachmentFile)
                val body = MultipartBody.Part.createFormData("picture", attachmentFile.getName(), requestFile)
                val result = NavinfoVolvoCall.getApi().uploadAttachment(body)
                XLog.d(result.code)
                if (result.code == 200) { // 请求成功
                    // 获取上传后的结果
                    val fileKey = result.data?.get("fileKey")
                    if (fileKey!=null) {
                        downloadAttachment(fileKey, attachmentType)
                    }
                } else {
                    ToastUtils.showToast(result.msg)
                }
            } catch (e: Exception) {
                ToastUtils.showToast(e.message)
                XLog.d(e.message)
            }
        }
    }

    // 下载附件文件
    fun downloadAttachment(fileKey: String, attachmentType: AttachmentType) {
        // 启用协程调用网络请求
        viewModelScope.launch {
            try {
                val downloadParam = mapOf("fileKey" to fileKey)
                val result = NavinfoVolvoCall.getApi().downLoadAttachment(downloadParam)
                XLog.d(result.code)
                if (result.code == 200) { // 请求成功
                    // 获取上传后的结果
                    val imageUrl = result.data
                    if (imageUrl!=null) {
                        XLog.d("downloadAttachment-imageUrl:${imageUrl}")
                        // 获取到图片的网络地址
                        if (attachmentType == AttachmentType.PIC) {
                            updateMessagePic(imageUrl)
                        } else {
                            updateMessageAudio(imageUrl)
                        }
                    }
                } else {
                    ToastUtils.showToast(result.msg)
                }
            } catch (e: Exception) {
                ToastUtils.showToast(e.message)
                XLog.d(e.message)
            }
        }
    }

    fun insertCardByApp() {
        viewModelScope.launch {
            try {
                val message = msgLiveData.value
                val insertData = mapOf(
                    "name" to message?.title,
                    "imageUrl" to getImageAttachment(message?.attachment!!)?.pathUrl,
                    "mediaUrl" to getAudioAttachment(message?.attachment!!)?.pathUrl,
                    "who" to message?.fromId,
                    "toWho" to message?.toId,
                    "sendDate" to message?.sendDate
                )
                val result = NavinfoVolvoCall.getApi().insertCardByApp(insertData as Map<String, String>)
                XLog.d("insertCardByApp:${result.code}")
                if (result.code == 200) { // 请求成功
                    // 获取上传后的结果
                    val netId = result.data
                    message.netId = netId!!
                    // 尝试保存数据到本地
                } else {
                    ToastUtils.showToast(result.msg)
                }
            } catch (e: Exception) {
                ToastUtils.showToast(e.message)
                XLog.d(e.message)
            }
        }
    }

    fun updateCardByApp() {
        viewModelScope.launch {
            try {
                val message = msgLiveData.value
                val insertData = mapOf(
                    "id" to message?.netId,
                    "name" to message?.title,
                    "imageUrl" to getImageAttachment(message?.attachment!!)?.pathUrl,
                    "mediaUrl" to getAudioAttachment(message?.attachment!!)?.pathUrl,
                    "who" to message?.fromId,
                    "toWho" to message?.toId,
                    "sendDate" to message?.sendDate
                )
                val result = NavinfoVolvoCall.getApi().updateCardByApp(insertData as Map<String, String>)
                XLog.d("updateCardByApp:${result.code}")
                if (result.code == 200) { // 请求成功
                    // 获取上传后的结果
                    val netId = result.data
                    message.netId = netId!!
                    // 尝试保存数据到本地
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