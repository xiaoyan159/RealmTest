package com.navinfo.volvo.ui.fragments.home

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.navinfo.volvo.database.dao.GreetingMessageDao
import com.navinfo.volvo.database.entity.GreetingMessage
import com.navinfo.volvo.model.network.NetworkDeleteMessagePost
import com.navinfo.volvo.model.network.NetworkMessageListPost
import com.navinfo.volvo.repository.database.DatabaseRepository
import com.navinfo.volvo.repository.network.NetworkRepository
import com.navinfo.volvo.repository.network.NetworkService
import com.navinfo.volvo.repository.preferences.PreferencesRepository
import com.navinfo.volvo.util.NetResult
import com.navinfo.volvo.util.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Context,
    private val netRepository: NetworkRepository,
    private val messageDao: GreetingMessageDao,
    private val shard: PreferencesRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading.asLiveData()

//    private val _messageList = MutableLiveData<List<GreetingMessage>>()
//    val messageList = _messageList.asLiveData()

    var userName: String = ""

    init {
        Log.e("jingo", "当前的homeviewmodel 是 ${hashCode()}")
        viewModelScope.launch {
            shard.loginUser().collectLatest {
                if (it != null) {
                    userName = it.username
                }
                Log.e("jingo", "用户赋值结束 是 ${it.hashCode()}")
            }
        }
    }

    suspend fun getNetMessageList(): Flow<PagingData<GreetingMessage>> {
        Log.e("jingo", "用户赋值了吗？ $userName ${hashCode()}")
        val messagePost = NetworkMessageListPost(who = userName)
        return netRepository.getMessagePaging(context = application, messagePost)

//            when (val result = netRepository.getMessageList(messagePost)) {
//                is NetResult.Success -> {
//                    _isLoading.value = false
//                    if ((result.data!!.data != null) && (result.data.data!!.rows != null)) {
//                        messageDao.insertOrUpdate(result.data.data!!.rows!!)
//                    }
//                }
//                is NetResult.Failure -> {
//                    _isLoading.value = false
//                    Toast.makeText(application, "${result.code}:${result.msg}", Toast.LENGTH_SHORT)
//                        .show()
//                }
//                is NetResult.Error -> {
//                    _isLoading.value = false
//                }
//                is NetResult.Loading -> {
//                    _isLoading.postValue(true)
//                }
//            }
    }

    fun deleteMessage(id: Long) {
        viewModelScope.launch {
            val post = NetworkDeleteMessagePost(id)
            messageDao.deleteById(id)
            when (val result = netRepository.deleteMessage(post)) {
                is NetResult.Success -> {
                    _isLoading.value = false
                }
                is NetResult.Failure -> {
                    _isLoading.value = false
                    Toast.makeText(
                        application,
                        "服务返回信息：${result.code}:${result.msg}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                is NetResult.Error -> {
                    _isLoading.value = false
                }
                is NetResult.Loading -> {
                    _isLoading.postValue(true)
                }
            }
        }
    }

}