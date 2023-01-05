//package com.navinfo.volvo.ui.fragments.home
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.view.isVisible
//import androidx.paging.LoadState
//import androidx.paging.LoadStateAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.example.picsapp.R
//import com.example.picsapp.databinding.LoadStateViewBinding
//
//class RecLoadStateAdapter(
//    private val retry: () -> Unit
//) : LoadStateAdapter<RecLoadStateAdapter.LoadStateViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
//
//        val binding = LoadStateViewBinding
//            .inflate(LayoutInflater.from(parent.context), parent, false)
//
//        return LoadStateViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
//        holder.onBind(loadState)
//
//    }
//
//
//
//    inner class LoadStateViewHolder(private val binding: LoadStateViewBinding) : RecyclerView.ViewHolder(binding.root){
//        fun onBind(loadState: LoadState) {
//            val progress = binding.loadStateProgress
//            val btnRetry = binding.loadStateRetry
//            val txtErrorMessage = binding.loadStateErrorMessage
//
//            btnRetry.isVisible = loadState !is LoadState.Loading
//            txtErrorMessage.isVisible = loadState !is LoadState.Loading
//            progress.isVisible = loadState is LoadState.Loading
//
//            if (loadState is LoadState.Error){
//                txtErrorMessage.text = loadState.error.localizedMessage
//            }
//
//            btnRetry.setOnClickListener {
//                retry.invoke()
//            }
//        }
//    }
//}