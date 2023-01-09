package com.navinfo.volvo.ui.fragments.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.navinfo.volvo.R
import com.navinfo.volvo.databinding.LoadStateViewBinding

class LoadStateViewHolder(parent: ViewGroup, var retry: () -> Unit) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.load_state_view, parent, false)
) {

    var itemLoadStateBindingUtil: LoadStateViewBinding = LoadStateViewBinding.bind(itemView)

    fun bindState(loadState: LoadState) {
        when (loadState) {
            is LoadState.Error -> {
                itemLoadStateBindingUtil.loadStateLayout.visibility = View.VISIBLE
//                itemLoadStateBindingUtil.btnRetry.setOnClickListener {
//                    retry()
//                }
                Log.d("jingo", "error了吧")
            }
            is LoadState.Loading -> {
                itemLoadStateBindingUtil.loadStateLayout.visibility = View.VISIBLE
                Log.d("jingo", "该显示了")
            }
            else -> {
                Log.d("jingo", "--其他的错误")
            }
        }

    }

}