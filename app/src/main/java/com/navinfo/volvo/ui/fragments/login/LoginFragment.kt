package com.navinfo.volvo.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.navinfo.volvo.R
import com.navinfo.volvo.databinding.FragmentLoginBinding
import com.navinfo.volvo.ui.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    //    private var loginViewModel:LoginViewModel by viewModel(get())
    private var viewBinding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = viewBinding!!

    private val viewModel by viewModels<LoginViewModel> { viewModelFactoryProvider }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewBinding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.loginFragmentRegisterButton.setOnClickListener {
        }
        binding.loginFragmentLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_home)
        }
        return root
    }


    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }
}