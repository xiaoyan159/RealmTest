package com.navinfo.vivo.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.navinfo.vivo.databinding.FragmentHomeBinding
import com.navinfo.vivo.databinding.FragmentObtainMessageBinding
import com.navinfo.vivo.ui.home.HomeViewModel

class ObtainMessageFragment: Fragment() {
    private var _binding: FragmentObtainMessageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val obtainMessageViewModel =
            ViewModelProvider(this).get(ObtainMessageViewModel::class.java)

        _binding = FragmentObtainMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        obtainMessageViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}