package com.navinfo.volvo.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.navinfo.volvo.R
import com.navinfo.volvo.databinding.FragmentHomeBinding
import com.navinfo.volvo.tools.DisplayUtil
import com.navinfo.volvo.ui.fragments.BaseFragment
import com.yanzhenjie.recyclerview.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class HomeFragment : BaseFragment(), OnItemClickListener, OnItemMenuClickListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    private val messageAdapter by lazy { HomeAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initView()
        return root
    }

    private fun initView() {


        //创建菜单选项
        //注意：使用滑动菜单不能开启滑动删除，否则只有滑动删除没有滑动菜单
        var mSwipeMenuCreator = SwipeMenuCreator { _, rightMenu, _ ->
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
        //侧滑按钮
        binding.homeRecyclerview.setOnItemMenuClickListener { menuBridge, position ->
            menuBridge.closeMenu()
//            val direction: Int = menuBridge.getDirection() // 左侧还是右侧菜单。

            when (menuBridge.position) {// 菜单在RecyclerView的Item中的Position。
                0 -> {//删除按钮
                    viewModel.deleteMessage(messageAdapter.getItemData(position).id)
                }
                1 -> {//分享按钮
                }

            }
        }

        val layoutManager = LinearLayoutManager(context)
        binding.homeRecyclerview.layoutManager = layoutManager
        //自动增加分割线
        binding.homeRecyclerview.addItemDecoration(
            DividerItemDecoration(
                context, layoutManager.orientation
            )
        )
        //增加侧滑按钮
        binding.homeRecyclerview.setSwipeMenuCreator(mSwipeMenuCreator)
        //单项点击
        binding.homeRecyclerview.setOnItemClickListener(this)
        //使用下拉加载
//        binding.homeRecyclerview.useDefaultLoadMore() // 使用默认的加载更多的View。
        binding.homeRecyclerview.setLoadMoreListener {
            Log.e("jingo", "下拉加载开始")

        } // 加载更多的监听。

        //开始下拉刷新
        binding.homeSwipeRefreshLayout.setOnRefreshListener {
            Log.e("jingo", "开始刷新")
            viewModel.getNetMessageList()
        }

        //列表自动分页
        lifecycleScope.launch {
            viewModel.messageList.collect {
                messageAdapter.submitData(it)
            }
        }
        binding.homeRecyclerview.adapter = messageAdapter

        //初始状态添加监听
        messageAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> {
                    Log.d("jingo", "is NotLoading")
                }
                is LoadState.Loading -> {
                    Log.d("jingo", "is Loading")
                }
                is LoadState.Error -> {
                    Log.d("jingo", "is Error:")
                    when ((it.refresh as LoadState.Error).error) {
                        is IOException -> {
                            Log.d("jingo", "IOException")
                        }
                        else -> {
                            Log.d("jingo", "others exception")
                        }
                    }
                }
            }
        }
        loadMoreFinish()
        //监听数据请求是否结束
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (!it) loadMoreFinish()
        })


    }

    private fun loadMoreFinish() {
        binding.homeSwipeRefreshLayout.isRefreshing = false

        // 第一次加载数据：一定要掉用这个方法。
        // 第一个参数：表示此次数据是否为空，假如你请求到的list为空(== null || list.size == 0)，那么这里就要true。
        // 第二个参数：表示是否还有更多数据，根据服务器返回给你的page等信息判断是否还有更多，这样可以提供性能，如果不能判断则传true。
        binding.homeRecyclerview.loadMoreFinish(false, false)
    }


    override fun onStart() {
        super.onStart()
        viewModel.getNetMessageList()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(view: View?, adapterPosition: Int) {

    }

    override fun onItemClick(menuBridge: SwipeMenuBridge?, adapterPosition: Int) {
    }
}