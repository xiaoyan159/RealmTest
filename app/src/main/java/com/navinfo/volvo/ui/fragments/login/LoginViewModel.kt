package com.navinfo.volvo.ui.fragments.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navinfo.volvo.repository.preferences.PreferencesRepository
import com.navinfo.volvo.util.asLiveData
//import com.navinfo.volvo.repository.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: PreferencesRepository) :
    ViewModel() {

    val user = repository.loginUser()

    fun onClickLogin(name: String, password: String) {
        if (name != "") {
            viewModelScope.launch {
                repository.saveLoginUser(id = "", name = name, password = password)
            }
        }
    }

//    fun onClickLoginRegister(username: String, password: String) {
//        if (username != "") {
//            viewModelScope.launch {
//                repository.saveLoginUser(id = "", name = username, password = password)
//            }
//        }
//    }
}