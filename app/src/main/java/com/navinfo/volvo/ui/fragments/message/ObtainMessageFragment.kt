package com.navinfo.volvo.ui.fragments.message

import android.content.DialogInterface
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.easytools.tools.DateUtils
import com.easytools.tools.DeviceUtils
import com.easytools.tools.DisplayUtils
import com.easytools.tools.FileIOUtils
import com.easytools.tools.FileUtils
import com.easytools.tools.ResourceUtils
import com.easytools.tools.ToastUtils
import com.elvishew.xlog.XLog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gredicer.datetimepicker.DateTimePickerFragment
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.navinfo.volvo.R
import com.navinfo.volvo.RecorderLifecycleObserver
import com.navinfo.volvo.database.entity.Attachment
import com.navinfo.volvo.database.entity.AttachmentType
import com.navinfo.volvo.database.entity.GreetingMessage
import com.navinfo.volvo.databinding.FragmentObtainMessageBinding
import com.navinfo.volvo.http.DownloadCallback
import com.navinfo.volvo.ui.markRequiredInRed
import com.navinfo.volvo.util.PhotoLoader
import com.navinfo.volvo.utils.EasyMediaFile
import com.navinfo.volvo.utils.SystemConstant
import com.nhaarman.supertooltips.ToolTip
import indi.liyi.viewer.Utils
import indi.liyi.viewer.ViewData
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


//@RuntimePermissions
class ObtainMessageFragment: Fragment() {
    private var _binding: FragmentObtainMessageBinding? = null
    private val obtainMessageViewModel by lazy {
        ViewModelProvider(requireActivity()).get(ObtainMessageViewModel::class.java)
    }
    private val photoHelper by lazy {
        EasyMediaFile().setCrop(true)
    }
    private val recorderLifecycleObserver by lazy {
        RecorderLifecycleObserver()
    }

    private val dateSendFormat = "yyyy-MM-dd HH:mm:ss"
    private val dateShowFormat = "yyyy-MM-dd HH:mm"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentObtainMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root

       obtainMessageViewModel.setCurrentMessage(GreetingMessage())

        obtainMessageViewModel?.getMessageLiveData()?.observe(
            viewLifecycleOwner, Observer {
                // 初始化界面显示内容
                if(it.name?.isNotEmpty() == true)
                    binding.tvMessageTitle?.setText(it.name)
                if (it.sendDate?.isNotEmpty() == true) {
                    // 获取当前发送时间，如果早于当前时间，则显示现在
                    val sendDate = DateUtils.str2Date(it.sendDate, dateSendFormat)
                    if (sendDate<=Date()) {
                        binding.btnSendTime.text = "现在"
                    } else {
                        binding.btnSendTime.text = it.sendDate
                    }
                } else { // 如果发送时间此时为空，自动设置发送时间为当前时间
                    it.sendDate = DateUtils.date2Str(Date(), dateSendFormat)
                }
                var hasPhoto = false
                var hasAudio = false
                if (it.imageUrl!=null&&it.imageUrl?.isNotEmpty() == true) {
                    hasPhoto = true
//                    Glide.with(this@ObtainMessageFragment)
//                        .asBitmap().fitCenter()
//                        .load(it.imageUrl)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(binding.imgMessageAttachment)
                    // 如果当前attachment文件是本地文件，开始尝试网络上传
                    val str = it.imageUrl?.replace("\\", "/")
                    binding.tvPhotoName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG )
                    if (!str!!.startsWith("http")) {
                        obtainMessageViewModel.uploadAttachment(File(it.imageUrl), AttachmentType.PIC)
                        binding.tvPhotoName.text = str.substringAfterLast("/", "picture.jpg")
                    } else {
                        if (str.contains("?")) {
                            binding.tvPhotoName.text = str.substring(str.lastIndexOf("/")+1, str.indexOf("?"))
                        } else {
                            binding.tvPhotoName.text = str.substringAfterLast("/")
                        }
                    }
                }

                if (it.mediaUrl!=null&&it.mediaUrl?.isNotEmpty() == true) {
                    hasAudio = true
                    binding.tvAudioName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG )
                    // 如果当前attachment文件是本地文件，开始尝试网络上传
                    val str = it.mediaUrl?.replace("\\", "/")
                    if (!str!!.startsWith("http")) {
                        obtainMessageViewModel.uploadAttachment(File(it.mediaUrl),AttachmentType.AUDIO)
                        binding.tvAudioName.text = str.substringAfterLast("/", "audio.m4a")
                    } else {
                        if (str.contains("?")) {
                            binding.tvAudioName.text = str.substring(str.lastIndexOf("/")+1, str.indexOf("?"))
                        } else {
                            binding.tvAudioName.text = str.substringAfterLast("/")
                        }
                    }
                }
                binding.layerPhotoResult.visibility = if (hasPhoto) VISIBLE else GONE
                binding.layerGetPhoto.visibility = if (hasPhoto) GONE else VISIBLE
//                binding.imgMessageAttachment.visibility = if (hasPhoto) VISIBLE else GONE

                binding.layerAudioResult.visibility = if (hasAudio) VISIBLE else GONE
                binding.layerGetAudio.visibility = if (hasAudio) GONE else VISIBLE
//                binding.llAudioPlay.visibility = if (hasAudio) VISIBLE else GONE
            }
        )
        lifecycle.addObserver(recorderLifecycleObserver)
        initView()
        return root
    }

    fun initView() {
        // 设置问候信息提示的红色星号
        binding.tiLayoutTitle.markRequiredInRed()
        binding.tvMessageTitle.addTextChangedListener(afterTextChanged = {
            obtainMessageViewModel.getMessageLiveData().value?.name = it.toString()
        })

        binding.edtSendFrom.addTextChangedListener (afterTextChanged = {
            obtainMessageViewModel.getMessageLiveData().value?.who = it.toString()
        })

        binding.imgPhotoDelete.setOnClickListener {
            obtainMessageViewModel.updateMessagePic("")
        }

        binding.imgAudioDelete.setOnClickListener {
            obtainMessageViewModel.updateMessageAudio("")
        }

        val sendToArray = mutableListOf<String>("绑定车辆1(LYVXFEFEXNL754427)")
        binding.edtSendTo.adapter = ArrayAdapter<String>(context!!,
            android.R.layout.simple_dropdown_item_1line, android.R.id.text1, sendToArray)
        binding.edtSendTo.onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                obtainMessageViewModel.getMessageLiveData().value?.toWho = sendToArray[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        // 设置点击按钮选择发送时间
        binding.btnSendTime.setOnClickListener {
            val dialog = DateTimePickerFragment.newInstance().mode(0)
            dialog.listener = object : DateTimePickerFragment.OnClickListener {
                override fun onClickListener(selectTime: String) {
                    val sendDate = DateUtils.str2Date(selectTime, dateShowFormat)
                    if (sendDate <= Date()) {
                        obtainMessageViewModel.updateMessageSendTime(DateUtils.date2Str(Date(), dateSendFormat))
                    } else {
                        obtainMessageViewModel.updateMessageSendTime(DateUtils.date2Str(sendDate, dateSendFormat))
                    }
                }

            }
            dialog.show(parentFragmentManager, "SelectSendTime")
        }

        // 点击按钮选择拍照
        binding.btnStartCamera.setOnClickListener {
            // 启动相机
            XXPermissions.with(this)
                // 申请单个权限
                .permission(Permission.CAMERA)
                .request(object : OnPermissionCallback {

                    override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                        if (!all) {
                            Toast.makeText(activity, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show()
                            return
                        }
                        // 开始启动拍照界面
                        photoHelper.setCrop(true).takePhoto(activity!!)
                    }

                    override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                        if (never) {
                            Toast.makeText(activity, "永久拒绝授权,请手动授权拍照权限", Toast.LENGTH_SHORT).show()
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(context!!, permissions)
                        } else {
                            onCameraDenied()
                            showRationaleForCamera(permissions)
                        }
                    }
                })
//            startCamera(it)
        }

        binding.btnStartPhoto.setOnClickListener {
            photoHelper.setCrop(true).selectPhoto(activity!!)
        }

        // 用户选择录音文件
        binding.btnSelectSound.setOnClickListener {
            photoHelper.setCrop(false).selectAudio(activity!!)
//            SingleAudioPicker.showPicker(context!!) {
//                val audioFile = File(it.contentUri.path)
//                ToastUtils.showToast(audioFile.absolutePath)
//                if (!audioFile.parentFile.parentFile.absolutePath.equals(SystemConstant.SoundFolder)) {
//                    val copyResult = FileIOUtils.writeFileFromIS(File(SystemConstant.SoundFolder, audioFile.name), FileInputStream(audioFile))
//                    XLog.e("拷贝结果："+copyResult)
//                    obtainMessageViewModel.updateMessageAudio(File(SystemConstant.SoundFolder, audioFile.name).absolutePath)
//                } else {
//                    obtainMessageViewModel.updateMessageAudio(audioFile.absolutePath)
//                }
//            }
        }

        binding.btnStartRecord.setOnTouchListener { view, motionEvent ->
            // 申请权限
            XXPermissions.with(this@ObtainMessageFragment)
                // 申请单个权限
                .permission(Permission.RECORD_AUDIO)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                        if (!all) {
                            Toast.makeText(activity, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show()
                            return
                        }
                        when(motionEvent.action) {
                            MotionEvent.ACTION_DOWN-> {
                                // 申请权限
                                recorderLifecycleObserver.initAndStartRecorder()
                                ToastUtils.showToast("开始录音！")
                                false
                            }
                            MotionEvent.ACTION_UP -> {
                                val recorderAudioPath = recorderLifecycleObserver.stopAndReleaseRecorder()
                                if (File(recorderAudioPath).exists()) {
                                    obtainMessageViewModel.updateMessageAudio(recorderAudioPath)
                                }
                                false
                            }
                            else -> {
                                false
                            }
                        }
                    }

                    override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                        if (never) {
                            Toast.makeText(activity, "永久拒绝授权,请手动授权拍照权限", Toast.LENGTH_SHORT).show()
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(context!!, permissions)
                        } else {
                            onCameraDenied()
                            showRationaleForCamera(permissions)
                        }
                    }
                })
            false
        }

        // 获取照片文件和音频文件
        photoHelper.setCallback {
            if (it.exists()) {
                val fileName = it.name.lowercase()
                if (fileName.endsWith(".jpg")||fileName.endsWith(".jpeg")||fileName.endsWith(".png")) {
                    // 获取选中的图片,自动压缩图片质量
                    // 压缩图片文件
                    Luban.with(context)
                        .load<Any>(mutableListOf(it) as List<Any>?)
                        .ignoreBy(200)
                        .setTargetDir("${SystemConstant.CameraFolder}")
                        .filter { path ->
                            !(TextUtils.isEmpty(path) || path.lowercase(Locale.getDefault())
                                .endsWith(".gif"))
                        }
                        .setCompressListener(object : OnCompressListener {
                            override fun onStart() {
                                XLog.d("开始压缩图片/${it.absolutePath}")
                            }

                            override fun onSuccess(file: File?) {
                                XLog.d("压缩图片成功:${file?.absolutePath}")
                            // 删除源文件
                            if (!it.absolutePath.equals(file?.absolutePath)) {
                                it?.delete()
                            }
                                // 如果当前文件不在camera缓存文件夹下，则移动该文件
                                if (!file!!.parentFile.absolutePath.equals(SystemConstant.CameraFolder)) {
                                    val copyResult = FileIOUtils.writeFileFromIS(File(SystemConstant.CameraFolder, fileName), FileInputStream(file))
                                    XLog.e("拷贝结果："+copyResult)
                                    // 跳转回原Fragment，展示拍摄的照片
                                    ViewModelProvider(requireActivity()).get(ObtainMessageViewModel::class.java).updateMessagePic(File(SystemConstant.CameraFolder, fileName).absolutePath)
                                } else {
                                    // 跳转回原Fragment，展示拍摄的照片
                                    ViewModelProvider(requireActivity()).get(ObtainMessageViewModel::class.java).updateMessagePic(file!!.absolutePath)
                                }
                            }

                            override fun onError(e: Throwable) {
                                XLog.d("压缩图片失败:${e.message}")
                            }
                        }).launch()
                } else if (fileName.endsWith(".mp3")||fileName.endsWith(".wav")||fileName.endsWith(".amr")||fileName.endsWith(".m4a")) {
                    ToastUtils.showToast(it.absolutePath)
                    if (!it.parentFile.parentFile.absolutePath.equals(SystemConstant.SoundFolder)) {
                        val copyResult = FileIOUtils.writeFileFromIS(File(SystemConstant.SoundFolder, fileName), FileInputStream(it))
                        XLog.e("拷贝结果："+copyResult)
                        obtainMessageViewModel.updateMessageAudio(File(SystemConstant.SoundFolder, fileName).absolutePath)
                    } else {
                        obtainMessageViewModel.updateMessageAudio(it.absolutePath)
                    }
                }
            }

        }
        photoHelper.setError {
            ToastUtils.showToast(it.message)
        }

        binding.tvAudioName.setOnClickListener {
            binding.llAudioPlay.visibility = if (binding.llAudioPlay.visibility == VISIBLE) GONE else VISIBLE
            // 判断当前播放的文件是否在缓存文件夹内，如果不在首先下载该文件
            val fileUrl = obtainMessageViewModel.getMessageLiveData().value!!.mediaUrl!!
            val localFile = obtainMessageViewModel.getLocalFileFromNetUrl(fileUrl, AttachmentType.AUDIO)
            if (!localFile.exists()) {
                obtainMessageViewModel.downLoadFile(fileUrl, localFile, object: DownloadCallback {
                    override fun progress(progress: Int) {
                    }

                    override fun error(throwable: Throwable) {
                    }

                    override fun success(file: File) {
                        binding.voicePlayerView.setAudio(localFile.absolutePath)
                    }

                })
            } else {
                binding.voicePlayerView.setAudio(localFile.absolutePath)
            }
        }

        binding.btnObtainMessageBack.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

        binding.btnObtainMessageConfirm.setOnClickListener {
            var checkResult = true
            val toolTipBackColor = ResourceUtils.getColor(R.color.teal_200)
            val toolTipTextColor = ResourceUtils.getColor(R.color.black)
            // 检查当前输入数据
            val messageData = obtainMessageViewModel.getMessageLiveData().value
            if (messageData?.name?.isEmpty() == true) {
                val toolTipRelativeLayout =
                    binding.ttTitle
                val toolTip = ToolTip()
                    .withText("请输入问候信息")
                    .withColor(toolTipBackColor)
                    .withTextColor(toolTipTextColor)
                    .withoutShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                toolTipRelativeLayout.showToolTipForView(toolTip, binding.tiLayoutTitle)
                checkResult = false
            } else {
                if (messageData?.name!!.length>10) {
                    val toolTipRelativeLayout =
                        binding.ttTitle
                    val toolTip = ToolTip()
                        .withText("问候信息长度不能超过10")
                        .withColor(toolTipBackColor)
                        .withTextColor(toolTipTextColor)
                        .withoutShadow()
                        .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                    toolTipRelativeLayout.showToolTipForView(toolTip, binding.tiLayoutTitle)
                    checkResult = false
                }
            }
            if (messageData?.imageUrl?.isEmpty() == true) {
                val toolTipRelativeLayout =
                    binding.ttPic
                val toolTip = ToolTip()
                    .withText("需要提供照片文件")
                    .withColor(toolTipBackColor)
                    .withTextColor(toolTipTextColor)
                    .withoutShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                toolTipRelativeLayout.showToolTipForView(toolTip, binding.tvUploadPic)
                checkResult = false
            }
            if (messageData?.mediaUrl?.isEmpty() == true) {
                val toolTipRelativeLayout =
                    binding.ttAudio
                val toolTip = ToolTip()
                    .withText("需要提供音频文件")
                    .withColor(toolTipBackColor)
                    .withTextColor(toolTipTextColor)
                    .withoutShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                toolTipRelativeLayout.showToolTipForView(toolTip, binding.tvUploadPic)
                checkResult = false
            }

            if (messageData?.who?.isEmpty()==true) {
                val toolTipRelativeLayout =
                    binding.ttSendFrom
                val toolTip = ToolTip()
                    .withText("请输入您的名称")
                    .withColor(toolTipBackColor)
                    .withTextColor(toolTipTextColor)
                    .withoutShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                toolTipRelativeLayout.showToolTipForView(toolTip, binding.edtSendFrom)
                checkResult = false
            }
            if (messageData?.toWho?.isEmpty()==true) {
                val toolTipRelativeLayout =
                    binding.ttSendTo
                val toolTip = ToolTip()
                    .withText("请选择要发送的车辆")
                    .withColor(toolTipBackColor)
                    .withTextColor(toolTipTextColor)
                    .withoutShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                toolTipRelativeLayout.showToolTipForView(toolTip, binding.edtSendTo)
                checkResult = false
            }

            if (checkResult) { // 检查通过
                // 检查attachment是否为本地数据，如果是本地则弹出对话框尝试上传
                val localAttachmentList = mutableListOf<Attachment>()
                if (messageData?.imageUrl?.startsWith("http") == false) {
                    val imageAttachment = Attachment("", messageData.imageUrl!!, AttachmentType.PIC)
                    localAttachmentList.add(imageAttachment)
                }
                if (messageData?.mediaUrl?.startsWith("http") == false) {
                    val audioAttachment = Attachment("", messageData.mediaUrl!!, AttachmentType.AUDIO)
                    localAttachmentList.add(audioAttachment)
                }
                if (localAttachmentList.isNotEmpty()) {
                    MaterialAlertDialogBuilder(context!!)
                        .setTitle("提示")
                        .setMessage("当前照片及音频内容需首先上传，是否尝试上传?")
                        .setPositiveButton("确定", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                            for (attachment in localAttachmentList) {
                                obtainMessageViewModel.uploadAttachment(File(attachment.pathUrl), attachment.attachmentType)
                            }
                        })
                        .setNegativeButton("取消", DialogInterface.OnClickListener {
                            dialogInterface, i -> dialogInterface.dismiss()
                        })
                        .show()
                    return@setOnClickListener
                }

                // 检查发送时间
                val sendDate = DateUtils.str2Date(messageData?.sendDate, dateSendFormat)
                val cal = Calendar.getInstance()
                cal.time = Date()
                cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)+1)
                if (cal.time.time < sendDate.time) { // 发送时间设置小于当前时间1分钟前，Toast提示用户并自动设置发送时间
                    messageData?.sendDate = DateUtils.date2Str(cal.time, dateSendFormat)
                    ToastUtils.showToast("自动调整发送时间为1分钟后发送")
                }

                // 开始网络提交数据
                if (obtainMessageViewModel.getMessageLiveData().value?.id==0L) { // 如果网络id为空，则调用更新操作
                    obtainMessageViewModel.insertCardByApp()
                } else {
                    obtainMessageViewModel.updateCardByApp()

                }
            }
        }
        // 点击照片名称
        binding.tvPhotoName.setOnClickListener {
            val viewData = ViewData()
            viewData.imageSrc = obtainMessageViewModel.getMessageLiveData().value!!.imageUrl
            viewData.targetX = Utils.dp2px(context, 10F).toFloat()
            viewData.targetWidth = DisplayUtils.getScreenWidthPixels(activity) - Utils.dp2px(context, 20F)
            viewData.targetHeight = Utils.dp2px(context, 200F)
            val viewDataList = listOf(viewData)
            binding.imageViewer.overlayStatusBar(true) // ImageViewer 是否会占据 StatusBar 的空间
                .viewData(viewDataList) // 图片数据
                .imageLoader(PhotoLoader()) // 设置图片加载方式
                .showIndex(true) // 是否显示图片索引，默认为true
                .watch(0) // 开启浏览

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun startCamera(it: View) {
        Navigation.findNavController(binding.root).navigate(com.navinfo.volvo.R.id.nav_2_camera)
    }

    fun showRationaleForCamera(permissions: MutableList<String>) {
//        showRationaleDialog(R.string.permission_camera_rationale, request)
//        Toast.makeText(context, "当前操作需要您授权相机权限！", Toast.LENGTH_SHORT).show()
        MaterialAlertDialogBuilder(context!!)
            .setTitle("提示")
            .setMessage("当前操作需要您授权拍摄权限！")
            .setPositiveButton("确定", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
                XXPermissions.startPermissionActivity(activity!!, permissions)
            })
            .show()
    }

    //    @OnPermissionDenied(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
    fun onCameraDenied() {
        ToastUtils.showToast("当前操作需要您授权拍摄权限！")
    }

    fun showRationaleForRecorder(permissions: MutableList<String>) {
        MaterialAlertDialogBuilder(context!!)
            .setTitle("提示")
            .setMessage("当前操作需要您授权录音权限！")
            .setPositiveButton("确定", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
                XXPermissions.startPermissionActivity(activity!!, permissions)
            })
            .show()
    }
    fun onRecorderDenied() {
        ToastUtils.showToast("当前操作需要您授权录音权限！")
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(recorderLifecycleObserver)
    }

    companion object
}