package com.navinfo.volvo.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.navinfo.volvo.db.dao.entity.Message

class HomeViewModel : ViewModel() {

    private val messageList: LiveData<MutableList<Message>> =
        MutableLiveData<MutableList<Message>>().apply {
            value = mutableListOf<Message>()
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
            value!!.add(Message())
        }

    fun getMessageList(): LiveData<MutableList<Message>> {
        return messageList
    }
}