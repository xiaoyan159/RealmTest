package com.navinfo.volvo.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.navinfo.volvo.R
import com.navinfo.volvo.databinding.FragmentLoginBinding
import com.navinfo.volvo.ui.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    //    private var loginViewModel:LoginViewModel by viewModel(get())
    private lateinit var viewBinding: FragmentLoginBinding


    private val viewModel by viewModels<LoginViewModel> { viewModelFactoryProvider }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewBinding.lifecycleOwner = this
        initView()
        return viewBinding.root
    }

    private fun initView() {
        viewBinding.loginFragmentRegisterButton.setOnClickListener {

        }
        viewBinding.loginFragmentLoginButton.setOnClickListener {
//            viewModel.login(viewBinding.loginFragmentUserLayout)
            findNavController().navigate(R.id.action_login_to_home)
        }
    }


}