package com.navinfo.volvo

import android.media.MediaRecorder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.easytools.tools.DateUtils
import com.elvishew.xlog.XLog
import com.navinfo.volvo.utils.SystemConstant
import java.util.*

class RecorderLifecycleObserver: DefaultLifecycleObserver {
    private var mediaRecorder: MediaRecorder? = null
    private lateinit var recorderAudioPath: String

    fun initAndStartRecorder() {
        recorderAudioPath = "${SystemConstant.SoundFolder}/${DateUtils.date2Str(Date(), DateUtils.FORMAT_YMDHMS)}.m4a"
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            // 开始录音
            setOutputFile(recorderAudioPath)
            try {
                prepare()
            } catch (e: Exception) {
                XLog.e("prepare() failed")
            }
            start()
        }
    }

    fun stopAndReleaseRecorder(): String {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
        return recorderAudioPath
    }

//    override fun onCreate(owner: LifecycleOwner) {
//        super.onCreate(owner)
//
//    }
//
//    override fun onStart(owner: LifecycleOwner) {
//        super.onStart(owner)
//    }
//
//    override fun onResume(owner: LifecycleOwner) {
//        super.onResume(owner)
//    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        mediaRecorder?.pause()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        mediaRecorder?.stop()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        mediaRecorder?.release()
    }
}