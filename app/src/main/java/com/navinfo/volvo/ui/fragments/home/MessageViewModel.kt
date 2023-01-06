package com.navinfo.volvo.ui.fragments.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navinfo.volvo.database.entity.GreetingMessage
import com.navinfo.volvo.model.messagelist.NetworkMessageListPost
import com.navinfo.volvo.model.messagelist.NetworkMessageListResponse
import com.navinfo.volvo.repository.NetworkDataSource
import com.navinfo.volvo.util.NetResult
import com.navinfo.volvo.util.asLiveData
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessageViewModel @Inject constructor(
    private val repository: NetworkDataSource
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading.asLiveData()

    private val _messageList = MutableLiveData<List<GreetingMessage>>()
    val messageList = _messageList.asLiveData()

    fun getMessageList() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            val messagePost = NetworkMessageListPost(who = "北京测试", toWho = "volvo测试")
            when (val result = repository.getCardList(messagePost)) {
                is NetResult.Success -> {
                    _isLoading.value = false
                    if (result.data != null) {
                        val list = (result.data.data as NetworkMessageListResponse).rows
                        _messageList.value = list
                    }
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