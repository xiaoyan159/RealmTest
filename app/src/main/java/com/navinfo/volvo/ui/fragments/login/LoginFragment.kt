package com.navinfo.volvo.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.navinfo.volvo.R
import com.navinfo.volvo.databinding.FragmentLoginBinding
import com.navinfo.volvo.ui.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    //    private var loginViewModel:LoginViewModel by viewModel(get())
    private lateinit var viewBinding: FragmentLoginBinding


    private val viewModel by viewModels<LoginViewModel>()

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

        lifecycleScope.launch {
            viewModel.user.collect {
                if (it != null){
                    viewBinding.loginUser = it
                }
                cancel()
            }
        }
        viewBinding.loginFragmentRegisterButton.setOnClickListener {

        }
        viewBinding.loginFragmentLoginButton.setOnClickListener {
            if (viewBinding.loginUsername.text!!.isEmpty()) {
                Toast.makeText(context, "请输入用户名", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (viewBinding.loginPassword.text!!.isEmpty()) {
                Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.onClickLogin(
                viewBinding.loginUsername.text.toString(),
                viewBinding.loginPassword.text.toString()
            )
            findNavController().navigate(R.id.action_login_to_home)
        }
    }


}