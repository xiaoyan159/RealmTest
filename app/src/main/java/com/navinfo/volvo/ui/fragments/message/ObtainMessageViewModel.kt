package com.navinfo.volvo.ui.fragments.message

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easytools.tools.FileIOUtils
import com.easytools.tools.FileUtils
import com.easytools.tools.ToastUtils
import com.elvishew.xlog.XLog
import com.navinfo.volvo.database.entity.AttachmentType
import com.navinfo.volvo.database.entity.GreetingMessage
import com.navinfo.volvo.http.DownloadCallback
import com.navinfo.volvo.http.DownloadManager
import com.navinfo.volvo.http.DownloadState
import com.navinfo.volvo.http.NavinfoVolvoCall
import com.navinfo.volvo.repository.preferences.PreferencesRepository
import com.navinfo.volvo.utils.SystemConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

@HiltViewModel
class ObtainMessageViewModel @Inject constructor(
    private val pre: PreferencesRepository,
) : ViewModel() {
    var username = ""

    init {
        viewModelScope.launch {
            pre.loginUser().collectLatest {
                username = it!!.username
                Log.e("jingo", "用户赋值结束 是 ${it.hashCode()}")
            }
        }
    }

    private val msgLiveData: MutableLiveData<GreetingMessage> by lazy {
        MutableLiveData<GreetingMessage>()
    }


    fun setCurrentMessage(msg: GreetingMessage) {
        msgLiveData.value = msg
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
//        var hasPic = false

        this.msgLiveData.value?.imageUrl = picUrl
//        for (attachment in this.msgLiveData.value!!.attachment) {
//            if (attachment.attachmentType == AttachmentType.PIC) {
//                if (picUrl==null||picUrl.isEmpty()) {
//                    this.msgLiveData.value!!.attachment.remove(attachment)
//                } else {
//                    attachment.pathUrl = picUrl
//                }
//                hasPic = true
//            }
//        }
//        if (!hasPic&&picUrl!=null) {
//            this.msgLiveData.value!!.attachment.add(Attachment(UUID.randomUUID().toString(), picUrl, AttachmentType.PIC))
//        }
        this.msgLiveData.postValue(this.msgLiveData.value)
    }

    // 更新消息附件中的录音文件
    fun updateMessageAudio(audioUrl: String?) {
//        var hasAudio = false
//        for (attachment in this.msgLiveData.value!!.attachment) {
//            if (attachment.attachmentType == AttachmentType.AUDIO) {
//                if (audioUrl==null||audioUrl.isEmpty()) {
//                    this.msgLiveData.value!!.attachment.remove(attachment)
//                } else {
//                    attachment.pathUrl = audioUrl
//                }
//                hasAudio = true
//            }
//        }
//        if (!hasAudio&&audioUrl!=null) {
//            this.msgLiveData.value!!.attachment.add(Attachment(UUID.randomUUID().toString(), audioUrl, AttachmentType.AUDIO))
//        }
        this.msgLiveData.value?.mediaUrl = audioUrl
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

//    // 获取照片url
//    fun getImageAttachment(attachementList: List<Attachment>): Attachment? {
//        for (attachment in attachementList) {
//            if (attachment.attachmentType == AttachmentType.PIC) {
//                return attachment
//            }
//        }
//        return null
//    }
//
//    // 获取音频url
//    fun getAudioAttachment(attachementList: List<Attachment>): Attachment? {
//        for (attachment in attachementList) {
//            if (attachment.attachmentType == AttachmentType.AUDIO) {
//                return attachment
//            }
//        }
//        return null
//    }

    // 上传附件文件
    fun uploadAttachment(attachmentFile: File, attachmentType: AttachmentType) {
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
                    val fileKey = result.data?.get("fileKey")
                    val newFileName = fileKey!!.substringAfterLast("/")
                    // 修改缓存文件名
                    if (attachmentType == AttachmentType.PIC) { // 修改当前文件在缓存文件夹的名称
                        val destFile = File(SystemConstant.CameraFolder, newFileName)
                        if (destFile.exists()) {
                            FileUtils.deleteFile(destFile)
                        }
                        val copyResult =
                            FileIOUtils.writeFileFromIS(destFile, FileInputStream(attachmentFile))
                        XLog.e("拷贝结果：" + copyResult)
                    } else {
                        val destFile = File(SystemConstant.SoundFolder, newFileName)
                        if (destFile.exists()) {
                            FileUtils.deleteFile(destFile)
                        }
                        val copyResult =
                            FileIOUtils.writeFileFromIS(destFile, FileInputStream(attachmentFile))
                        XLog.e("拷贝结果：" + copyResult)
                    }
                    if (fileKey != null) {
                        downloadAttachment(fileKey, attachmentType)
                    }
                } else {
                    ToastUtils.showToast(result.msg)
                    XLog.d(result.msg)
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
                    if (imageUrl != null) {
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

    fun downLoadFile(url: String, destFile: File, downloadCallback: DownloadCallback) {
        viewModelScope.launch {
            DownloadManager.download(
                url,
                destFile
            ).collect {
                when (it) {
                    is DownloadState.InProgress -> {
                        XLog.d("~~~", "download in progress: ${it.progress}.")
                        downloadCallback.progress(it.progress)
                    }
                    is DownloadState.Success -> {
                        XLog.d("~~~", "download finished.")
                        downloadCallback.success(it.file)
                    }
                    is DownloadState.Error -> {
                        XLog.d("~~~", "download error: ${it.throwable}.")
                        downloadCallback.error(it.throwable)
                    }
                }
            }
        }
    }

    fun insertCardByApp(confirmCallback: MyConfirmCallback) {
        viewModelScope.launch {
            try {
                // TODO 首先保存数据到本地
                val message = msgLiveData.value
                val insertData = mapOf(
                    "name" to message?.name,
                    "imageUrl" to message?.imageUrl,
                    "mediaUrl" to message?.mediaUrl,
                    "who" to message?.who,
                    "toWho" to message?.toWho,
                    "sendDate" to message?.sendDate,
                    "version" to message?.version
                )
                val result =
                    NavinfoVolvoCall.getApi().insertCardByApp(insertData as Map<String, String>)
                XLog.d("insertCardByApp:${result.code}")
                if (result.code == 200) { // 请求成功
                    // 获取上传后的结果
                    val netId = result.data
                    message?.id = netId!!.toLong()
                    ToastUtils.showToast("保存成功")
                    // TODO 尝试更新本地数据
                    confirmCallback.onSucess()
                } else {
                    confirmCallback.onFail(result.msg)
                }
            } catch (e: Exception) {
                XLog.d(e.message)
                confirmCallback.onFail(e.message!!)
            }
        }
    }

    fun updateCardByApp(confirmCallback: MyConfirmCallback) {
        viewModelScope.launch {
            try {
                val message = msgLiveData.value
                val updateData = mapOf(
                    "id" to message?.id,
                    "name" to message?.name,
                    "imageUrl" to message?.imageUrl,
                    "mediaUrl" to message?.mediaUrl,
                    "who" to message?.who,
                    "toWho" to message?.toWho,
                    "sendDate" to message?.sendDate,
                    "version" to message?.version
                )
                val result =
                    NavinfoVolvoCall.getApi().updateCardByApp(updateData as Map<String, String>)
                XLog.d("updateCardByApp:${result.code}")
                if (result.code == 200) { // 请求成功
                    // 数据更新成功
                    ToastUtils.showToast("更新成功")
                    // 尝试保存数据到本地
                    confirmCallback.onSucess()
                } else {
                    ToastUtils.showToast(result.msg)
                }
            } catch (e: Exception) {
                ToastUtils.showToast(e.message)
                XLog.d(e.message)
            }
        }
    }

    /**
     * 根据网络地址获取本地的缓存文件路径
     * */
    fun getLocalFileFromNetUrl(url: String, attachmentType: AttachmentType): File {
        if (url.startsWith("http")) {
            val folder = when (attachmentType) {
                AttachmentType.PIC -> SystemConstant.CameraFolder
                else -> SystemConstant.SoundFolder
            }
            var name = if (url.contains("?")) {
                url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"))
            } else {
                url.substringAfterLast("/")
            }
            return File(folder, name)
        } else {
            return File(url)
        }
    }

    interface MyConfirmCallback {
        fun onSucess()
        fun onFail(error: String)
    }
}