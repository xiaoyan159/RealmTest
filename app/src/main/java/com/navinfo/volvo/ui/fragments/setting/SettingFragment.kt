package com.navinfo.volvo.ui.fragments.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.navinfo.volvo.R
import com.navinfo.volvo.databinding.FragmentSettingBinding
import com.navinfo.volvo.ui.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment() {

    private lateinit var viewBinding: FragmentSettingBinding
    private val viewModel by viewModels<SettingModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        viewBinding.lifecycleOwner = this
        initView()
        return viewBinding.root
    }

    private fun initView() {
        //退出登录
        viewBinding.loginFragmentLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_setting_to_login)
        }
    }
}