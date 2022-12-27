package com.navinfo.volvo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.navinfo.volvo.R
import com.navinfo.volvo.databinding.FragmentHomeBinding
import com.navinfo.volvo.db.dao.entity.Message
import com.navinfo.volvo.ui.message.ObtainMessageViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val obtainMessageViewModel = ViewModelProvider(requireActivity()).get(ObtainMessageViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvNewMessage
        textView.setOnClickListener {
            val message = Message(1, "新建标题", "", "", "", 0, "1", "2", mutableListOf())
            obtainMessageViewModel.setCurrentMessage(message)
            // 跳转到新建Message的Fragment
            Navigation.findNavController(it).navigate(R.id.home_2_obtain_message)
        }
        homeViewModel.text.observe(viewLifecycleOwner) {

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}