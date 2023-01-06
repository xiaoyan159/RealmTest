package com.navinfo.volvo.ui.fragments.login

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.navinfo.volvo.database.AppDatabase
import com.navinfo.volvo.database.entity.User
import com.navinfo.volvo.util.SharedPreferenceHelper
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val sharedPreferenceHelper: SharedPreferenceHelper) : ViewModel() {

//    val user: LiveData<User> = _user

    fun liveDataOnclick(view: View) {

    }

    fun userRegister(username: String, password: String) {

    }
}