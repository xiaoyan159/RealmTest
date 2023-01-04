package com.navinfo.volvo.ui.camera

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.Navigation
import com.easytools.tools.DateUtils
import com.easytools.tools.FileUtils
import com.elvishew.xlog.XLog
import com.navinfo.volvo.databinding.FragmentCameraBinding
import com.navinfo.volvo.ui.fragments.message.ObtainMessageViewModel
import com.navinfo.volvo.utils.SystemConstant
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.FileCallback
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
//    private val exportFolderPath by lazy {
//        "${SystemConstant.ROOT_PATH}/exportPic/"
//    }

    private val cameraLifeCycleObserver: CameraLifeCycleObserver by lazy {
        CameraLifeCycleObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        lifecycle.addObserver(cameraLifeCycleObserver)

        val cameraViewModel =
            ViewModelProvider(this).get(CameraViewModel::class.java)

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val cameraView: CameraView = binding.camera
        cameraView.setLifecycleOwner(this)
        cameraView.addCameraListener(object:CameraListener() { // 添加拍照回调
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
//                FileUtils.createOrExistsDir(cameraFolderPath)
                val resultFile = File("${SystemConstant.CameraFolder}/${DateUtils.date2Str(Date(), DateUtils.FORMAT_YMDHMS)}.jpg")
                result.toFile(resultFile, object: FileCallback {
                    override fun onFileReady(resultFile: File?) {
                        // 压缩图片文件
                        Luban.with(context)
                            .load<Any>(mutableListOf(resultFile) as List<Any>?)
                            .ignoreBy(200)
                            .setTargetDir("${SystemConstant.CameraFolder}")
                            .filter { path ->
                                !(TextUtils.isEmpty(path) || path.lowercase(Locale.getDefault())
                                    .endsWith(".gif"))
                            }
                            .setCompressListener(object : OnCompressListener {
                                override fun onStart() {
                                    XLog.d("开始压缩图片")
                                }

                                override fun onSuccess(file: File?) {
                                    XLog.d("压缩图片成功:${file?.absolutePath}")
                                    // 删除源文件
                                    if (!resultFile!!.absolutePath.equals(file!!.absolutePath)) {
                                        resultFile!!.delete()
                                    }
                                    // 跳转回原Fragment，展示拍摄的照片
                                    ViewModelProvider(requireActivity()).get(ObtainMessageViewModel::class.java).updateMessagePic(file!!.absolutePath)
                                    // 跳转回原界面
                                    Navigation.findNavController(root).popBackStack()
                                }

                                override fun onError(e: Throwable) {
                                    XLog.d("压缩图片失败:${e.message}")
                                }
                            }).launch()
                    }
                })
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
//        lifecycle.removeObserver(cameraLifeCycleObserver)
    }
}