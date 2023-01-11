package com.navinfo.volvo.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.navinfo.volvo.R
import com.navinfo.volvo.databinding.FragmentHomeBinding
import com.navinfo.volvo.databinding.HomeAdapterNotingBinding
import com.navinfo.volvo.tools.DisplayUtil
import com.navinfo.volvo.ui.fragments.BaseFragment
import com.yanzhenjie.recyclerview.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class HomeFragment : BaseFragment(), OnItemClickListener, OnItemMenuClickListener {

    private lateinit var _binding: FragmentHomeBinding
    private var headView: HomeAdapterNotingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.

    private val viewModel by viewModels<HomeViewModel>()

    private val messageAdapter by lazy { HomeAdapter(this) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = _binding.root
        _binding.lifecycleOwner = this

        headView = HomeAdapterNotingBinding.inflate(inflater, container, false)
        initView()
        return root
    }

    private fun initView() {
        //创建菜单选项
        //注意：使用滑动菜单不能开启滑动删除，否则只有滑动删除没有滑动菜单
        val mSwipeMenuCreator = SwipeMenuCreator { _, rightMenu, _ ->
            //预览
            val previewItem = SwipeMenuItem(context)
            previewItem.height = DisplayUtil.dip2px(requireContext(), 60f)
            previewItem.width = DisplayUtil.dip2px(requireContext(), 80f)
            previewItem.background = requireContext().getDrawable(R.color.yellow)
            previewItem.setTextColor(requireContext().getColor(R.color.white))
            previewItem.text = requireContext().getString(R.string.preview)
            rightMenu.addMenuItem(previewItem)

            //分享
            val shareItem = SwipeMenuItem(context)
            shareItem.height = DisplayUtil.dip2px(requireContext(), 60f)
            shareItem.width = DisplayUtil.dip2px(requireContext(), 80f)
            shareItem.background = requireContext().getDrawable(R.color.blue)
            shareItem.setTextColor(requireContext().getColor(R.color.white))
            shareItem.text = requireContext().getString(R.string.share)
            rightMenu.addMenuItem(shareItem)

            //添加菜单自动添加至尾部
            val deleteItem = SwipeMenuItem(context)
            deleteItem.height = DisplayUtil.dip2px(requireContext(), 60f)
            deleteItem.width = DisplayUtil.dip2px(requireContext(), 80f)
            deleteItem.text = requireContext().getString(R.string.delete)
            deleteItem.background = requireContext().getDrawable(R.color.red)
            deleteItem.setTextColor(requireContext().getColor(R.color.white))
            rightMenu.addMenuItem(deleteItem)

        }

        val layoutManager = LinearLayoutManager(context)
        _binding.homeRecyclerview.layoutManager = layoutManager
        //自动增加分割线
        _binding.homeRecyclerview.addItemDecoration(
            DividerItemDecoration(
                context, layoutManager.orientation
            )
        )
        //增加侧滑按钮
        _binding.homeRecyclerview.setSwipeMenuCreator(mSwipeMenuCreator)
        //单项点击
        _binding.homeRecyclerview.setOnItemClickListener(this)

        _binding.homeRecyclerview.setLoadMoreListener {
            Log.e("jingo", "下拉加载开始")

        } // 加载更多的监听。

        //开始下拉刷新
        _binding.homeSwipeRefreshLayout.setOnRefreshListener {
            Log.e("jingo", "开始刷新")
//                viewModel.getNetMessageList()
            messageAdapter.refresh()
        }

        //列表自动分页
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getNetMessageList().collect {
                    messageAdapter.submitData(it)
                }
            }
        }
        //侧滑菜单的监听必需在设置adapter之前
        _binding.homeRecyclerview.setOnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
//            val direction: Int = menuBridge.getDirection() // 左侧还是右侧菜单。

            when (menuBridge.position) {// 菜单在RecyclerView的Item中的Position。
                0 -> {//预览按钮
                }
                1 -> {//分享按钮
                }
                2 -> {//删除按钮
                    viewModel.deleteMessage(messageAdapter.getItemData(adapterPosition).id)
                }

            }
        }

        _binding.homeRecyclerview.adapter = messageAdapter

        //初始状态添加监听
        messageAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> {
                    _binding.homeSwipeRefreshLayout.isRefreshing = false
                    headView?.let {

                        if (messageAdapter.itemCount == 0) {
                            if (_binding.homeRecyclerview.headerCount == 0)
                                _binding.homeRecyclerview.addHeaderView(headView!!.root)
                        } else {
                            if (_binding.homeRecyclerview.headerCount > 0)
                                _binding.homeRecyclerview.removeHeaderView(headView!!.root)
                        }
                    }

                }
                is LoadState.Loading -> {
                    _binding.homeSwipeRefreshLayout.isRefreshing = true
                }
                is LoadState.Error -> {
                    _binding.homeSwipeRefreshLayout.isRefreshing = false
                    when ((it.refresh as LoadState.Error).error) {
                        is IOException -> {
                            Log.d("jingo", "刷新 IOException $")
                        }
                        else -> {
                            Log.d("jingo", "刷新 others exception")
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
        _binding.homeSwipeRefreshLayout.isRefreshing = false
        // 第一次加载数据：一定要掉用这个方法。
        // 第一个参数：表示此次数据是否为空，假如你请求到的list为空(== null || list.size == 0)，那么这里就要true。
        // 第二个参数：表示是否还有更多数据，根据服务器返回给你的page等信息判断是否还有更多，这样可以提供性能，如果不能判断则传true。
        _binding.homeRecyclerview.loadMoreFinish(false, true)
    }


    override fun onStart() {
        super.onStart()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        headView = null
    }

    //点击项
    override fun onItemClick(view: View?, adapterPosition: Int) {
        val directions = HomeFragmentDirections.actionHomeToObtainMessage()
        directions.arguments.putParcelable("message", messageAdapter.getItemData(adapterPosition))
        findNavController().navigate(directions)
    }


    override fun onItemClick(menuBridge: SwipeMenuBridge?, adapterPosition: Int) {

    }
}