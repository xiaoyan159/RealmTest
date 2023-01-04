package com.navinfo.volvo.ui.fragments.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gredicer.datetimepicker.DateTimePickerFragment
import com.navinfo.volvo.databinding.FragmentObtainMessageBinding
import com.navinfo.volvo.ui.markRequiredInRed

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

        obtainMessageViewModel.getMessageLiveData()?.observe(
            viewLifecycleOwner, Observer {
                // 初始化界面显示内容
                if(it.title!=null)
                    binding.tvMessageTitle.setText(it.title)
                if (it.sendDate!=null) {
                    binding.btnSendTime.text = it.sendDate
                }
            }
        )
        initView()
        return root
    }

    private fun initView() {
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
        binding.edtSendTo.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}