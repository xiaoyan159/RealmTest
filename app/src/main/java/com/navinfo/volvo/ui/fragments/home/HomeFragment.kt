package com.navinfo.volvo.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.navinfo.volvo.R
import com.navinfo.volvo.databinding.FragmentHomeBinding
import com.navinfo.volvo.tools.DisplayUtil
import com.navinfo.volvo.ui.BaseFragment
import com.yanzhenjie.recyclerview.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment(), OnItemClickListener, OnItemMenuClickListener {

    private val viewModel by viewModels<HomeViewModel> { viewModelFactoryProvider }

    private lateinit var messageAdapter: HomeAdapter
    private lateinit var mDataBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        mDataBinding.lifecycleOwner = this
        initView()
        return mDataBinding.root
    }


    private fun initView() {
        mDataBinding.homeViewModel = viewModel
        messageAdapter = HomeAdapter(this)
        val recyclerview: SwipeRecyclerView = mDataBinding.homeRecyclerview
        recyclerview.adapter = null //先设置null，否则会报错
        //创建菜单选项
        //注意：使用滑动菜单不能开启滑动删除，否则只有滑动删除没有滑动菜单
        var mSwipeMenuCreator =
            SwipeMenuCreator { _, rightMenu, position ->
                //添加菜单自动添加至尾部
                var deleteItem = SwipeMenuItem(context)
                deleteItem.height = DisplayUtil.dip2px(context!!, 60f)
                deleteItem.width = DisplayUtil.dip2px(context!!, 80f)
                deleteItem.background = context!!.getDrawable(R.color.red)
                deleteItem.text = context!!.getString(R.string.delete)
                rightMenu.addMenuItem(deleteItem)

                //分享
                var shareItem = SwipeMenuItem(context)
                shareItem.height = DisplayUtil.dip2px(context!!, 60f)
                shareItem.width = DisplayUtil.dip2px(context!!, 80f)
                shareItem.background = context!!.getDrawable(R.color.gray)
                shareItem.text = context!!.getString(R.string.share)
                shareItem.setTextColor(R.color.white)
                rightMenu.addMenuItem(shareItem)
            }
        val layoutManager = LinearLayoutManager(context)

        recyclerview.layoutManager = layoutManager
        recyclerview.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
        recyclerview.setSwipeMenuCreator(mSwipeMenuCreator)
        recyclerview.setOnItemClickListener(this)
//        recyclerview.useDefaultLoadMore()
//        recyclerview.setLoadMoreListener {
//
//        }
        lifecycleScope.launch {
            viewModel.messageList.collectLatest {
                messageAdapter.submitData(it)
            }
        }
//        messageAdapter.withLoadStateFooter(
//            footer = RecLoadStateAdapter { messageAdapter.retry() }
//        )

//        messageAdapter.withLoadStateHeader()
        recyclerview.adapter = messageAdapter

    }

    override fun onStart() {
        super.onStart()
        viewModel.getNetMessageList()
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onItemClick(view: View?, adapterPosition: Int) {

    }

    override fun onItemClick(menuBridge: SwipeMenuBridge?, adapterPosition: Int) {
    }
}