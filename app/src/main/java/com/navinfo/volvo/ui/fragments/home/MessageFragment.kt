package com.navinfo.volvo.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.navinfo.volvo.R
import com.navinfo.volvo.databinding.FragmentHomeBinding
import com.navinfo.volvo.tools.DisplayUtil
import com.navinfo.volvo.ui.BaseFragment
import com.navinfo.volvo.ui.adapter.MessageAdapter
import com.navinfo.volvo.ui.fragments.message.ObtainMessageViewModel
import com.yanzhenjie.recyclerview.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageFragment : BaseFragment(), OnItemClickListener, OnItemMenuClickListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by viewModels<MessageViewModel> { viewModelFactoryProvider }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this)[MessageViewModel::class.java]
//        val obtainMessageViewModel =
//            ViewModelProvider(requireActivity()).get(ObtainMessageViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initView()
        return root
    }


    private fun initView() {
        val recyclerview: SwipeRecyclerView = binding.homeMessageRecyclerview
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
        val adapter = MessageAdapter()
        recyclerview.layoutManager = layoutManager
        recyclerview.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
        recyclerview.setSwipeMenuCreator(mSwipeMenuCreator)
        recyclerview.setOnItemClickListener(this)
        recyclerview.useDefaultLoadMore()
        recyclerview.setLoadMoreListener {

        }
        recyclerview.adapter = adapter
//        homeViewModel.getMessageList().observe(viewLifecycleOwner, Observer { contacts ->
//            adapter.setItem(contacts)
//        })
    }

    override fun onStart() {
        super.onStart()
        getMessageList()
    }

    private fun getMessageList() {
        viewModel.getMessageList()
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