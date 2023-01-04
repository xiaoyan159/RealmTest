package com.navinfo.volvo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.navinfo.volvo.R
import com.navinfo.volvo.database.entity.GreetingMessage

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    var itemList: MutableList<GreetingMessage> = mutableListOf()

    fun addItem(message: GreetingMessage) {
        itemList.add(message)
        notifyItemInserted(itemList.size - 1)
    }

    fun setItem(messageList: MutableList<GreetingMessage>){
        itemList = messageList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_message, parent, false)
        var viewHolder = MyViewHolder(view)
        viewHolder.itemView.setOnClickListener {

        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = itemList[position]
        holder.toName.text = message.toWho
        holder.messageText.text = message.name
        holder.sendTime.text = message.sendDate
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.message_head_icon)
        var toName: TextView = itemView.findViewById(R.id.message_to_username)
        var sendTime: TextView = itemView.findViewById(R.id.message_send_time)
        var status: TextView = itemView.findViewById(R.id.message_status)
        var messageText: TextView = itemView.findViewById(R.id.message_text)
    }
}