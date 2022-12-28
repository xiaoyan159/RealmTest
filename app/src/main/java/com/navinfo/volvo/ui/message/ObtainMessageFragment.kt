package com.navinfo.volvo.ui.message

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gredicer.datetimepicker.DateTimePickerFragment
import com.navinfo.volvo.databinding.FragmentObtainMessageBinding
import com.navinfo.volvo.ui.markRequiredInRed
import permissions.dispatcher.*
import java.util.*

class ObtainMessageFragment: Fragment() {
    private var _binding: FragmentObtainMessageBinding? = null
    private val obtainMessageViewModel by lazy {
        ViewModelProvider(requireActivity()).get(ObtainMessageViewModel::class.java)
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

        obtainMessageViewModel?.getMessageLiveData()?.observe(
            viewLifecycleOwner, Observer {
                // 初始化界面显示内容
                if(it.title!=null)
                    binding.tvMessageTitle?.setText(it.title)
                if (it.sendDate!=null) {
                    binding.btnSendTime.setText(it.sendDate)
                }
            }
        )
        initView()
        return root
    }

    fun initView() {
        // 设置问候信息提示的红色星号
        binding.tiLayoutTitle.markRequiredInRed()
        // 设置点击按钮选择发送时间
        binding.btnSendTime.setOnClickListener {
            val dialog = DateTimePickerFragment.newInstance().mode(0)
            dialog.listener = object : DateTimePickerFragment.OnClickListener {
                override fun onClickListener(selectTime: String) {
                    obtainMessageViewModel.updateMessageSendTime(selectTime)
                }

            }
            dialog.show(parentFragmentManager, "SelectSendTime")
        }

        // 点击按钮选择拍照
        binding.btnStartCamera.setOnClickListener {
            // 启动相机
            startCamera()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun startCamera() {

    }

    @OnShowRationale(Manifest.permission.CAMERA)
    fun showRationaleForCamera(request: PermissionRequest) {
//        showRationaleDialog(R.string.permission_camera_rationale, request)
//        Toast.makeText(context, "当前操作需要您授权相机权限！", Toast.LENGTH_SHORT).show()
        MaterialAlertDialogBuilder(context!!)
            .setTitle("提示")
            .setMessage("当前操作需要您授权相机权限！")
            .setPositiveButton("确定", DialogInterface.OnClickListener { dialogInterface, i ->
                startCamera()
                dialogInterface.dismiss()
             })
            .show()
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraDenied() {
        Toast.makeText(context, "当前操作需要您授权相机权限！", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun onCameraNeverAskAgain() {
        Toast.makeText(context, "您已永久拒绝授权相机权限！", Toast.LENGTH_SHORT).show()
    }
}