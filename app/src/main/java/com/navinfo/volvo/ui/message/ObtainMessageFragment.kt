package com.navinfo.volvo.ui.message

import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.easytools.tools.DateUtils
import com.easytools.tools.ToastUtils
import com.elvishew.xlog.XLog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gredicer.datetimepicker.DateTimePickerFragment
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.navinfo.volvo.R
import com.navinfo.volvo.RecorderLifecycleObserver
import com.navinfo.volvo.databinding.FragmentObtainMessageBinding
import com.navinfo.volvo.db.dao.entity.AttachmentType
import com.navinfo.volvo.db.dao.entity.Message
import com.navinfo.volvo.ui.markRequiredInRed
import com.navinfo.volvo.utils.EasyMediaFile
import com.navinfo.volvo.utils.SystemConstant
import com.nhaarman.supertooltips.ToolTip
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
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

        obtainMessageViewModel.setCurrentMessage(Message())

        obtainMessageViewModel?.getMessageLiveData()?.observe(
            viewLifecycleOwner, Observer {
                // 初始化界面显示内容
                if(it.title?.isNotEmpty() == true)
                    binding.tvMessageTitle?.setText(it.title)
                if (it.sendDate?.isNotEmpty() == true) {
                    binding.btnSendTime.text = it.sendDate
                }
                var hasPhoto = false
                var hasAudio = false
                if (it.attachment.isNotEmpty()) {
                    // 展示照片文件或录音文件
                    for (attachment in it.attachment) {
                        if (attachment.attachmentType == AttachmentType.PIC) {
                            Glide.with(context!!)
                                .asBitmap().fitCenter()
                                .load(attachment.pathUrl)
                                .into(binding.imgMessageAttachment)
                            // 显示名称
                            binding.tvPhotoName.text = attachment.pathUrl.replace("\\", "/").substringAfterLast("/")
                            hasPhoto = true

                            // 如果当前attachment文件是本地文件，开始尝试网络上传
                            if (!attachment.pathUrl.startsWith("http")) {
                                obtainMessageViewModel.uploadAttachment(File(attachment.pathUrl))
                            }
                        }
                        if (attachment.attachmentType == AttachmentType.AUDIO) {
                            binding.tvAudioName.text = attachment.pathUrl.replace("\\", "/").substringAfterLast("/")
                            hasAudio = true
                        }
                    }
                }
                binding.layerPhotoResult.visibility = if (hasPhoto) VISIBLE else GONE
                binding.layerGetPhoto.visibility = if (hasPhoto) GONE else VISIBLE
                binding.layerAudioResult.visibility = if (hasAudio) VISIBLE else GONE
                binding.layerGetAudio.visibility = if (hasAudio) GONE else VISIBLE
            }
        )
        lifecycle.addObserver(recorderLifecycleObserver)
        initView()
        return root
    }

    fun initView() {
        // 设置问候信息提示的红色星号
        binding.tiLayoutTitle.markRequiredInRed()
        binding.tvMessageTitle.addTextChangedListener {
            obtainMessageViewModel.updateMessageTitle(it.toString())
        }

        binding.imgPhotoDelete.setOnClickListener {
            obtainMessageViewModel.updateMessagePic(null)
        }

        binding.imgAudioDelete.setOnClickListener {
            obtainMessageViewModel.updateMessageAudio(null)
        }

        val sendToArray = mutableListOf<String>("绑定车辆1(LYVXFEFEXNL754427)")
        binding.edtSendTo.adapter = ArrayAdapter<String>(context!!,
            android.R.layout.simple_dropdown_item_1line, android.R.id.text1, sendToArray)
        binding.edtSendTo.onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                obtainMessageViewModel.getMessageLiveData().value?.toId = sendToArray[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        // 设置点击按钮选择发送时间
        binding.btnSendTime.setOnClickListener {
            val dialog = DateTimePickerFragment.newInstance().mode(0)
            dialog.listener = object : DateTimePickerFragment.OnClickListener {
                override fun onClickListener(selectTime: String) {
                    val sendDate = DateUtils.str2Date(selectTime, "yyyy-MM-dd HH:mm")
                    if (sendDate <= Date()) {
                        obtainMessageViewModel.updateMessageSendTime("现在")
                    } else {
                        obtainMessageViewModel.updateMessageSendTime(selectTime)
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
        }

        // 开始录音
        binding.btnStartRecord.setOnClickListener {
            // 申请权限
            XXPermissions.with(this)
                // 申请单个权限
                .permission(Permission.RECORD_AUDIO)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                        if (!all) {
                            Toast.makeText(activity, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show()
                            return
                        }

                        if (it.isSelected) {
                            it.isSelected = false
                            val recorderAudioPath = recorderLifecycleObserver.stopAndReleaseRecorder()
                            if (File(recorderAudioPath).exists()) {
                                obtainMessageViewModel.updateMessageAudio(recorderAudioPath)
                            }
                        } else{
                            it.isSelected = true

                            recorderLifecycleObserver.initAndStartRecorder()
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
                                // 跳转回原Fragment，展示拍摄的照片
                                ViewModelProvider(requireActivity()).get(ObtainMessageViewModel::class.java).updateMessagePic(file!!.absolutePath)
                            }

                            override fun onError(e: Throwable) {
                                XLog.d("压缩图片失败:${e.message}")
                            }
                        }).launch()
                } else if (fileName.endsWith(".mp3")||fileName.endsWith(".wav")||fileName.endsWith(".amr")||fileName.endsWith(".m4a")) {
                    ToastUtils.showToast(it.absolutePath)
                    obtainMessageViewModel.updateMessageAudio(it.absolutePath)
                }
            }

        }
        photoHelper.setError {
            ToastUtils.showToast(it.message)
        }

        binding.btnObtainMessageBack.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

        binding.btnObtainMessageConfirm.setOnClickListener {
            // 检查当前输入数据
            val messageData = obtainMessageViewModel.getMessageLiveData().value
            if (messageData?.title?.isEmpty() == true) {
                val toolTipRelativeLayout =
                    binding.ttTitle
                val toolTip = ToolTip()
                    .withText("请输入问候信息")
                    .withColor(com.navinfo.volvo.R.color.purple_200)
                    .withShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                toolTipRelativeLayout.showToolTipForView(toolTip, binding.tiLayoutTitle)
            }
            var hasPic = false
            var hasAudio = false
            for (attachment in messageData?.attachment!!) {
                if (attachment.attachmentType == AttachmentType.PIC) {
                    hasPic = true
                }
                if (attachment.attachmentType == AttachmentType.AUDIO) {
                    hasAudio = true
                }
            }
            if (!hasPic) {
                val toolTipRelativeLayout =
                    binding.ttPic
                val toolTip = ToolTip()
                    .withText("需要提供照片文件")
                    .withColor(com.navinfo.volvo.R.color.purple_200)
                    .withShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                toolTipRelativeLayout.showToolTipForView(toolTip, binding.tvUploadPic)
            }
            if (!hasAudio) {
                val toolTipRelativeLayout =
                    binding.ttAudio
                val toolTip = ToolTip()
                    .withText("需要提供音频文件")
                    .withColor(com.navinfo.volvo.R.color.purple_200)
                    .withShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                toolTipRelativeLayout.showToolTipForView(toolTip, binding.tvUploadPic)
            }

            if (messageData?.fromId?.isEmpty()==true) {
                val toolTipRelativeLayout =
                    binding.ttSendFrom
                val toolTip = ToolTip()
                    .withText("请输入您的名称")
                    .withColor(com.navinfo.volvo.R.color.purple_200)
                    .withShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                toolTipRelativeLayout.showToolTipForView(toolTip, binding.edtSendFrom)
            }
            if (messageData?.toId?.isEmpty()==true) {
                val toolTipRelativeLayout =
                    binding.ttSendTo
                val toolTip = ToolTip()
                    .withText("请选择要发送的车辆")
                    .withColor(com.navinfo.volvo.R.color.purple_200)
                    .withShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                toolTipRelativeLayout.showToolTipForView(toolTip, binding.edtSendTo)
            }
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
}