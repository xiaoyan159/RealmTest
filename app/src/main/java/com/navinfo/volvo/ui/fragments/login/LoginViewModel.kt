package com.navinfo.volvo.ui.fragments.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navinfo.volvo.model.proto.LoginUser
import com.navinfo.volvo.repository.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: PreferencesRepository) :
    ViewModel() {
    var loginUser: LoginUser? = null

    init {
        Log.e("jingo", "LoginViewModel 是 ${hashCode()}")
        viewModelScope.launch {
            repository.loginUser().collectLatest {
                Log.e("jingo", "用户赋值结束 是 ${it.hashCode()}")
                loginUser = it
            }
        }
    }


    suspend fun onClickLogin(name: String, password: String) {
        repository.saveLoginUser(id = "", name = name, password = password)
    }

//    fun onClickLoginRegister(username: String, password: String) {
//        if (username != "") {
//            viewModelScope.launch {
//                repository.saveLoginUser(id = "", name = username, password = password)
//            }
//        }
//    }
}