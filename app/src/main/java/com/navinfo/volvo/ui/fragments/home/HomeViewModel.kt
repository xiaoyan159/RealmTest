package com.navinfo.volvo.ui.fragments.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.navinfo.volvo.database.dao.GreetingMessageDao
import com.navinfo.volvo.database.entity.GreetingMessage
import com.navinfo.volvo.model.messagelist.NetworkMessageListPost
import com.navinfo.volvo.repository.database.DatabaseRepository
import com.navinfo.volvo.repository.network.NetworkRepository
import com.navinfo.volvo.util.NetResult
import com.navinfo.volvo.util.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val netRepository: NetworkRepository,
    private val dataRepository: DatabaseRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading.asLiveData()

//    private val _messageList = MutableLiveData<List<GreetingMessage>>()
//    val messageList = _messageList.asLiveData()


    val messageList: Flow<PagingData<GreetingMessage>>
        get() = dataRepository.getMessageByPaging()


    fun getNetMessageList() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            val messagePost = NetworkMessageListPost(who = "", toWho = "")
            when (val result = netRepository.getCardList(messagePost)) {
                is NetResult.Success -> {
                    _isLoading.value = false
//                    if (result.data != null) {
//                        val list = (result.data.data as NetworkMessageListResponse).rows
//                        _messageList.value = list
//                    }
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