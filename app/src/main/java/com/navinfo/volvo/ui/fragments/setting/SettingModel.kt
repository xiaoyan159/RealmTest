package com.navinfo.volvo.ui.fragments.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navinfo.volvo.repository.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingModel @Inject constructor(private val pre: PreferencesRepository) : ViewModel() {

    suspend fun clearUser() {
        Log.e("jingo", "SettingModel clearUser")
        pre.saveLoginUser("", "", "")
    }
}
