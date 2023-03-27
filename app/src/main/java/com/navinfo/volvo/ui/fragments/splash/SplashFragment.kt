package com.navinfo.volvo.ui.fragments.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.navinfo.volvo.R
import com.navinfo.volvo.databinding.FragmentSplashBinding
import com.navinfo.volvo.ui.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment() {

    private val viewModel by viewModels<SplashViewModel>()
    private lateinit var viewBinding: FragmentSplashBinding

    // 位置的协程监听器
    private var timeDownJob: Job? = null
    override fun onStart() {
        Log.e("jingo", "onStart")
        super.onStart()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("jingo", "onStart")
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        Log.e("jingo", "onPause")
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e("jingo", "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onAttach(context: Context) {
        Log.e("jingo", "onAttach")
        super.onAttach(context)
    }

    override fun onStop() {
        Log.e("jingo", "onStop")
        super.onStop()
        timeDownJob?.apply {
            this.cancel()
        }
    }

    override fun onResume() {
        Log.e("jingo", "onResume")
        super.onResume()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e("jingo", "onCreateView")
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)
        viewBinding.lifecycleOwner = this
        return viewBinding.root
    }

    private fun initView() {
        //倒计时文字按钮
        viewBinding.timeDown.setOnClickListener {
            navigate()
        }
        viewBinding.splashGoWeb.setOnClickListener {
            //实现跳转网页的主要代码
            val intent = Intent();
            intent.action = "android.intent.action.VIEW";
            val contentUrl = Uri.parse("https://www.volvocars.com.cn/zh-cn");
            intent.data = contentUrl;
            startActivity(intent);
        }
        // 由于 repeatOnLifecycle 是一个挂起函数，
        // 因此从 lifecycleScope 中创建新的协程
        timeDownJob = lifecycleScope.launch {
            // 直到 lifecycle 进入 DESTROYED 状态前都将当前协程挂起。
            // repeatOnLifecycle 每当生命周期处于 STARTED 或以后的状态时会在新的协程中
            // 启动执行代码块，并在生命周期进入 STOPPED 时取消协程。
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 当生命周期处于 STARTED 时安全地从 locations 中获取数据
                // 当生命周期进入 STOPPED 时停止收集数据
                viewModel.countDown().collect {
                    Log.e("jingo", "collect $it")
                    if (it == 0) {
                        navigate()
                    } else {
                        viewBinding.timeDown.text = "跳过:${it}秒"
                    }
                }
                // 注意：运行到此处时，生命周期已经处于 DESTROYED 状态！
            }
        }
    }

    private fun navigate() {
        if (viewModel.userName == "") {
            findNavController().navigate(R.id.action_splash_to_login)
        } else {
            findNavController().navigate(R.id.action_splash_to_home)
        }
    }

    override fun onDestroy() {
        Log.e("jingo", "onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.e("jingo", "onDetach")
        super.onDetach()
    }

    override fun onDestroyView() {
        Log.e("jingo", "onDestroyView")
        super.onDestroyView()
    }
}