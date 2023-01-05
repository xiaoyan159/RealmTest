package com.navinfo.volvo.ui.fragments.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.navinfo.volvo.R
import com.navinfo.volvo.database.entity.GreetingMessage
import com.navinfo.volvo.databinding.AdapterHomeBinding

class HomeAdapter(fragment: Fragment) :
    PagingDataAdapter<GreetingMessage, HomeAdapter.MyViewHolder>(DiffCallback()) {

    val fragment = fragment
//    var itemList = ArrayList<GreetingMessage>()
//
//    fun addItem(message: GreetingMessage) {
//        itemList.add(message)
//        notifyItemInserted(itemList.size - 1)
//    }
//
//    fun setItems(messageList: List<GreetingMessage>) {
//        itemList.clear()
//        itemList.addAll(messageList)
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val mDataBinding: AdapterHomeBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(fragment.context),
                R.layout.adapter_home,
                parent,
                false
            )
        return MyViewHolder(mDataBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(position)
    }

//    override fun getItemCount(): Int {
//        return itemList.size
//    }

    inner class MyViewHolder(private val mDataBinding: AdapterHomeBinding) :
        RecyclerView.ViewHolder(mDataBinding.root) {
        fun onBind(position: Int) {
            var row = getItem(position)
            mDataBinding.greetingMessage = row
            Glide.with(fragment)
                .asBitmap().fitCenter()
                .load(row!!.imageUrl)
                .placeholder(R.mipmap.volvo_logo_small)
                .error(R.mipmap.volvo_logo_small)
                .into(mDataBinding.messageHeadIcon)
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<GreetingMessage>() {
        override fun areItemsTheSame(oldItem: GreetingMessage, newItem: GreetingMessage): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(
            oldItem: GreetingMessage,
            newItem: GreetingMessage
        ): Boolean {
            return oldItem == newItem
        }
    }
}