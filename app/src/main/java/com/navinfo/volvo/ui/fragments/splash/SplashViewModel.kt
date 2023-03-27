package com.navinfo.volvo.ui.fragments.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navinfo.volvo.repository.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val pre: PreferencesRepository) : ViewModel() {

    var userName: String = ""

    init {
        Log.e("jingo", "SplashViewModel init ${hashCode()}")
        viewModelScope.launch {
            pre.loginUser().collectLatest {
                if (it != null) {
                    Log.e("jingo", "SplashViewModel 获取用户${it.username}")
                    userName = it.username
                }
            }
        }
    }


    suspend fun countDown(): Flow<Int> {
        Log.e("jingo", "viewModel countDown ${hashCode()}")
        var time = 3
        // 在这个范围内启动的协程会在Lifecycle被销毁的时候自动取消
        return flow<Int> {
            (time downTo 0).forEach() {
                emit(it)
                delay(1000)
            }
        }
//            .onStart {
//            // 倒计时开始 ，在这里可以让Button 禁止点击状态
//        }.onCompletion {
//            // 倒计时结束 ，在这里可以让Button 恢复点击状态
//        }.catch {
//            //错误
//        }
    }

    override fun onCleared() {
        Log.e("jingo", "viewModel onCleared ${hashCode()}")
        super.onCleared()
    }
}