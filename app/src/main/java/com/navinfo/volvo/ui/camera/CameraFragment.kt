package com.navinfo.volvo.ui.camera

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.navinfo.volvo.databinding.FragmentCameraBinding
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.*


class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val cameraLifeCycleObserver: CameraLifeCycleObserver by lazy {
        CameraLifeCycleObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lifecycle.addObserver(cameraLifeCycleObserver)

        val cameraViewModel =
            ViewModelProvider(this).get(CameraViewModel::class.java)

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val cameraView: CameraView = binding.camera
        cameraView.setLifecycleOwner(this)
        cameraView.addCameraListener(object:CameraListener() { // 添加拍照回调
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                result.toFile()
                // 压缩图片文件
                Luban.with(context)
                    .load<Any>(photos)
                    .ignoreBy(100)
                    .setTargetDir(getPath())
                    .filter { path ->
                        !(TextUtils.isEmpty(path) || path.lowercase(Locale.getDefault())
                            .endsWith(".gif"))
                    }
                    .setCompressListener(object : OnCompressListener {
                        override fun onStart() {
                            // TODO 压缩开始前调用，可以在方法内启动 loading UI
                        }

                        override fun onSuccess(file: File?) {
                            // TODO 压缩成功后调用，返回压缩后的图片文件
                        }

                        override fun onError(e: Throwable) {
                            // TODO 当压缩过程出现问题时调用
                        }
                    }).launch()
            }
        })
        // 点击拍照
        binding.imgStartCamera.setOnClickListener {
            cameraView.takePicture()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        lifecycle.removeObserver(cameraLifeCycleObserver)
    }
}