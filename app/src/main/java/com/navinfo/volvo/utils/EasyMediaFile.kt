package com.navinfo.volvo.utils

import android.app.Activity
import android.app.Fragment
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.os.Environment.*
import android.provider.MediaStore
import java.io.File
import java.util.*


/**
 * 创建日期：2018/8/21 0021on 下午 4:40
 * 描述：多媒体选择工具类
 * @author：Vincent
 */
class EasyMediaFile {
    /**
     * 设置图片选择结果回调
     */
    private var callback: ((file: File) -> Unit)? = null
    private var isCrop: Boolean = false
    private var error: ((error: Exception) -> Unit)? = null

    /**
     * 视频录制/音频录制/拍照/剪切后图片的存放位置(参考file_provider_paths.xml中的路径)
     */
    private var mFilePath: File? = null

    private val mainHandler = Handler(Looper.getMainLooper())

    fun setError(error: ((error: Exception) -> Unit)?): EasyMediaFile {
        this.error = error
        return this
    }

    fun setCallback(callback: ((file: File) -> Unit)): EasyMediaFile {
        this.callback = callback
        return this
    }

    fun setCrop(isCrop: Boolean): EasyMediaFile {
        this.isCrop = isCrop
        return this
    }

    /**
     * 修改图片的存储路径（默认的图片存储路径是SD卡上 Android/data/应用包名/时间戳.jpg）
     *
     * @param imgPath 图片的存储路径（包括文件名和后缀）
     */
    fun setFilePath(imgPath: String?): EasyMediaFile {
        if (imgPath.isNullOrEmpty()) {
            this.mFilePath = null
        } else {
            this.mFilePath = File(imgPath)
            this.mFilePath?.parentFile?.mkdirs()
        }
        return this
    }


    /**
     * 选择文件
     * 支持图片、音频、视频
     */
//    fun selectFile(activity: Activity) {
//        isCrop = false
//        val intent = Intent(Intent.ACTION_PICK, null).apply {
//            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "*/*")
//            setDataAndType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "*/*")
//            setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "*/*")
//        }
//        if (Looper.myLooper() == Looper.getMainLooper()) {
//            selectFileInternal(intent, activity, -1)
//        } else {
//            mainHandler.post { selectFileInternal(intent, activity, -1) }
//        }
//    }

    /**
     * 选择视频
     */
    fun selectVideo(activity: Activity) {
        isCrop = false
        val intent = Intent(Intent.ACTION_PICK, null).apply {
            setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*")
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            selectFileInternal(intent, activity, 2)
        } else {
            mainHandler.post { selectFileInternal(intent, activity, 2) }
        }
    }

    /**
     * 选择音频
     */
    fun selectAudio(activity: Activity) {
        isCrop = false
        val intent = Intent(Intent.ACTION_PICK, null).apply {
            setDataAndType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "*/*")
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            selectFileInternal(intent, activity, 1)
        } else {
            mainHandler.post { selectFileInternal(intent, activity, 1) }
        }
    }

    /**
     * 选择图片
     */
    fun selectPhoto(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK, null).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            selectFileInternal(intent, activity, 0)
        } else {
            mainHandler.post { selectFileInternal(intent, activity, 0) }
        }
    }


    /**
     * 选择文件
     */
    private fun selectFileInternal(intent: Intent, activity: Activity, type: Int) {
        var resolveInfoList = activity.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfoList.isEmpty()) {
            error?.invoke(IllegalStateException("No Activity found to handle Intent "))
        } else {
            PhotoFragment.findOrCreate(activity).start(intent, PhotoFragment.REQ_SELECT_FILE) { requestCode: Int, data: Intent? ->
                if (requestCode != PhotoFragment.REQ_SELECT_FILE) {
                    return@start
                }
                data ?: return@start
                data.data ?: return@start
                try {
                    val inputFile = if (type != -1) {
                        uriToFile(activity, data.data!!, type)
                    } else {
                        if (data.data!!.path!!.contains(".")) {
                            File(data.data!!.path!!)
                        } else {
                            when {
                                data.data!!.path!!.contains("images") -> {
                                    uriToFile(activity, data.data!!, 0)
                                }
                                data.data!!.path!!.contains("video") -> {
                                    uriToFile(activity, data.data!!, 2)
                                }
                                else -> {
                                    uriToFile(activity, data.data!!, 1)
                                }
                            }
                        }
                    }
                    if (isCrop) {//裁剪
                        zoomPhoto(inputFile, mFilePath
                            ?: File(generateFilePath(activity)), activity)
                    } else {//不裁剪
                        callback?.invoke(inputFile)
                    }
                } catch (e: Exception) {
                    error?.invoke(e)
                }
            }
        }

    }

    private fun uriToFile(activity: Activity, uri: Uri): File {

        // 首先使用系统提供的CursorLoader进行file获取
        val context = activity.application
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var path: String
        try {
            CursorLoader(context, uri, projection, null, null, null)
                .loadInBackground().apply {
                    getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    val index = getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    moveToFirst()
                    path = getString(index)
                    close()

                }
            return File(path)
        } catch (e: Exception) {
            // 当没获取到。再使用别的方式进行获取
            val scheme = uri.scheme
            path = uri.path ?: throw RuntimeException("Could not find path in this uri:[$uri]")
            when (scheme) {
                "file" -> {
                    val cr = context.contentResolver
                    val buff = StringBuffer()
                    buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'$path'").append(")")
                    cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DATA), buff.toString(), null, null).apply {
                        this ?: throw RuntimeException("cursor is null")
                        var dataIdx: Int
                        while (!this.isAfterLast) {
                            dataIdx = this.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                            path = this.getString(dataIdx)
                            this.moveToNext()
                        }
                        close()
                    }

                    return File(path)
                }
                "content" -> {

                    context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null).apply {
                        this ?: throw RuntimeException("cursor is null")
                        if (this.moveToFirst()) {
                            val columnIndex = this.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                            path = this.getString(columnIndex)
                        }
                        close()
                    }
                    return File(path)

                }
                else -> {
                    throw IllegalArgumentException("Could not find file by this uri：$uri")
                }
            }

        }
    }

    /**
     * 拍照获取
     */
    fun takePhoto(activity: Activity) {
        val imgFile = if (isCrop) {
            File(generateFilePath(activity))
        } else {
            mFilePath ?: File(generateFilePath(activity))
        }

        val imgUri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri.fromFile(imgFile)
        } else {
            //兼容android7.0 使用共享文件的形式
            val contentValues = ContentValues(1)
            contentValues.put(MediaStore.Images.Media.DATA, imgFile.absolutePath)
            activity.application.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        if (Looper.myLooper() == Looper.getMainLooper()) {
            takeFileInternal(imgFile, intent, activity)
        } else {
            mainHandler.post { takeFileInternal(imgFile, intent, activity) }
        }
    }

    /**
     * 音频录制
     */
    fun takeAudio(activity: Activity) {
        isCrop = false
        val imgFile = mFilePath ?: File(generateFilePath(activity, 1))
        val imgUri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri.fromFile(imgFile)
        } else {
            //兼容android7.0 使用共享文件的形式
            val contentValues = ContentValues(1)
            contentValues.put(MediaStore.Audio.Media.DATA, imgFile.absolutePath)
            activity.application.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
        }

        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        if (Looper.myLooper() == Looper.getMainLooper()) {
            takeFileInternal(imgFile, intent, activity, 1)
        } else {
            mainHandler.post { takeFileInternal(imgFile, intent, activity, 1) }
        }

    }

    /**
     * 视频录制
     */
    fun takeVideo(activity: Activity) {
        isCrop = false
        val imgFile = mFilePath ?: File(generateFilePath(activity, 2))
        val imgUri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri.fromFile(imgFile)
        } else {
            //兼容android7.0 使用共享文件的形式
            val contentValues = ContentValues(1)
            contentValues.put(MediaStore.Video.Media.DATA, imgFile.absolutePath)
            activity.application.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        }

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            // 默认录制时间10秒 部分手机该设置无效
//            putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10000)
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            takeFileInternal(imgFile, intent, activity, 2)
        } else {
            mainHandler.post { takeFileInternal(imgFile, intent, activity, 2) }
        }

    }

    /**
     * 拍照或选择
     */
    fun getImage(activity: Activity) {

        val imgFile = if (isCrop) {
            File(generateFilePath(activity))
        } else {
            mFilePath ?: File(generateFilePath(activity))
        }

        val imgUri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri.fromFile(imgFile)
        } else {
            //兼容android7.0 使用共享文件的形式
            val contentValues = ContentValues(1)
            contentValues.put(MediaStore.Images.Media.DATA, imgFile.absolutePath)
            activity.application.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        }


        val cameraIntents = ArrayList<Intent>()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = activity.packageManager
        val camList = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in camList) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            cameraIntents.add(intent)
        }
        val intent = Intent.createChooser(createPickMore(), "请选择").also {
            it.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())

        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            takeFileInternal(imgFile, intent, activity)
        } else {
            mainHandler.post { takeFileInternal(imgFile, intent, activity) }
        }
    }

    /**
     * 音频录制或选择
     */
    fun getAudio(activity: Activity) {
        isCrop = false
        val imgFile = mFilePath ?: File(generateFilePath(activity))

        val imgUri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri.fromFile(imgFile)
        } else {
            //兼容android7.0 使用共享文件的形式
            val contentValues = ContentValues(1)
            contentValues.put(MediaStore.Audio.Media.DATA, imgFile.absolutePath)
            activity.application.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
        }
        val cameraIntents = ArrayList<Intent>()
        val captureIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        val packageManager = activity.packageManager
        val camList = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in camList) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            cameraIntents.add(intent)
        }
        val intent = Intent.createChooser(createPickMore("audio/*"), "请选择").also {
            it.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())

        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            takeFileInternal(imgFile, intent, activity, 1)
        } else {
            mainHandler.post { takeFileInternal(imgFile, intent, activity, 1) }
        }
    }

    /**
     * 视频拍摄或选择
     */
    fun getVideo(activity: Activity) {
        isCrop = false
        val imgFile = mFilePath ?: File(generateFilePath(activity, 2))

        val imgUri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri.fromFile(imgFile)
        } else {
            //兼容android7.0 使用共享文件的形式
            val contentValues = ContentValues(1)
            contentValues.put(MediaStore.Video.Media.DATA, imgFile.absolutePath)
            activity.application.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        }
        val cameraIntents = ArrayList<Intent>()
        val captureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        // 某些手机此设置是不生效的，需要自行封装解决
//        captureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10000)
        val packageManager = activity.packageManager
        val camList = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in camList) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            cameraIntents.add(intent)
        }
        val intent = Intent.createChooser(createPickMore("video/*"), "请选择").also {
            it.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())

        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            takeFileInternal(imgFile, intent, activity, 2)
        } else {
            mainHandler.post { takeFileInternal(imgFile, intent, activity, 2) }
        }
    }

    /**
     * 向系统发出指令
     */
    private fun takeFileInternal(takePhotoPath: File, intent: Intent, activity: Activity, type: Int = 0) {
        val fragment = PhotoFragment.findOrCreate(activity)
        fragment.start(intent, PhotoFragment.REQ_TAKE_FILE) { requestCode: Int, data: Intent? ->
            if (requestCode == PhotoFragment.REQ_TAKE_FILE) {
                if (data?.data != null) {
                    mFilePath = when (type) {
                        0 -> {
                            uriToFile(activity, data.data!!)
                        }
                        else -> uriToFile(activity, data.data!!, type)
                    }

                    if (isCrop) {
                        zoomPhoto(takePhotoPath, mFilePath
                            ?: File(generateFilePath(activity)), activity)
                    } else {
                        callback?.invoke(mFilePath!!)
                        mFilePath = null
                    }
                    return@start
                }
                if (isCrop) {
                    zoomPhoto(takePhotoPath, mFilePath
                        ?: File(generateFilePath(activity)), activity)
                } else {
                    callback?.invoke(takePhotoPath)
                }
            }
        }
    }

    private fun uriToFile(activity: Activity, data: Uri, type: Int): File {
        val cursor = activity.managedQuery(data, arrayOf(if (type == 1) MediaStore.Audio.Media.DATA else MediaStore.Video.Media.DATA), null,
            null, null)
        val path = if (cursor == null) {
            data.path
        } else {
            val index = cursor.getColumnIndexOrThrow(if (type == 1) MediaStore.Audio.Media.DATA else MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(index)
        }
        // 手动关掉报错如下
//        Caused by: android.database.StaleDataException: Attempted to access a cursor after it has been closed.
//        cursor.close()
        return File(path!!)
    }

    /***
     * 图片裁剪
     */
    private fun zoomPhoto(inputFile: File?, outputFile: File, activity: Activity) {
        try {
            val intent = Intent("com.android.camera.action.CROP")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setDataAndType(getImageContentUri(activity, inputFile), "image/*")
            } else {
                intent.setDataAndType(Uri.fromFile(inputFile), "image/*")
            }
            intent.putExtra("crop", "true")

            // 是否返回uri
            intent.putExtra("return-data", false)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val imgFile = File("${Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)}/${outputFile.name}")
                // 通过 MediaStore API 插入file 为了拿到系统裁剪要保存到的uri（因为App没有权限不能访问公共存储空间，需要通过 MediaStore API来操作）
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, imgFile.getAbsolutePath());
                values.put(MediaStore.Images.Media.DISPLAY_NAME, outputFile.name);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                val uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile))
                zoomPhotoInternal(outputFile, intent, activity)
            }else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile))
                zoomPhotoInternal(outputFile, intent, activity)
            }
        } catch (e: Exception) {
            error?.invoke(e)
        }

    }

    private fun zoomPhotoInternal(outputFile: File, intent: Intent, activity: Activity) {
        PhotoFragment.findOrCreate(activity).start(intent, PhotoFragment.REQ_ZOOM_PHOTO) { requestCode: Int, data: Intent? ->
            if (requestCode == PhotoFragment.REQ_ZOOM_PHOTO) {
                data ?: return@start
                callback?.invoke(outputFile)
            }
        }
    }

    /**构建文件多选Intent*/
    private fun createPickMore(fileType: String = "image/*"): Intent {
        val pictureChooseIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = fileType
        }
        pictureChooseIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        /**临时授权app访问URI代表的文件所有权*/
        pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return pictureChooseIntent
    }
    /**
     * 产生图片的路径，带文件夹和文件名，文件名为当前毫秒数
     */
    private fun generateFilePath(activity: Activity, fileType: Int = 0): String {
        val file = when (fileType) {
            // 音频路径
            1 -> "${SystemConstant.SoundFolder}" + File.separator + System.currentTimeMillis().toString() + ".m4a"
            // 视频路径
            2 -> "${SystemConstant.CameraFolder}" + File.separator + System.currentTimeMillis().toString() + ".mp4"
            // 图片路径
            else -> "${SystemConstant.CameraFolder}" + File.separator + System.currentTimeMillis().toString() + ".jpg"
        }
        File(file).parentFile.mkdirs()
        return file
    }
    /**
     * 获取SD下的应用目录
     */
    private fun getExternalStoragePath(activity: Activity): String {
        val sb = "${activity.getExternalFilesDir(null)}/tmp"
        return sb
    }
    /**
     * 安卓7.0裁剪根据文件路径获取uri
     */
    private fun getImageContentUri(context: Context, imageFile: File?): Uri? {
        val filePath = imageFile?.absolutePath
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA + "=? ",
            arrayOf(filePath), null)
        cursor.use { _ ->
            return if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID))
                val baseUri = Uri.parse("content://media/external/images/media")
                Uri.withAppendedPath(baseUri, "" + id)
            } else {
                imageFile?.let {
                    if (it.exists()) {
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.DATA, filePath)
                        context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    } else {
                        null
                    }
                }
            }
        }
    }
    /**
     * 用于获取图片的Fragment
     */
    class PhotoFragment : Fragment() {
        /**
         * Fragment处理照片后返回接口
         */
        private var callback: ((requestCode: Int, intent: Intent?) -> Unit)? = null
        /**
         * 开启系统相册
         *      裁剪图片、打开相册选择单张图片、拍照
         */
        fun start(intent: Intent, requestCode: Int, callback: ((requestCode: Int, intent: Intent?) -> Unit)) {
            this.callback = callback
            startActivityForResult(intent, requestCode)
        }
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK) {
                callback?.invoke(requestCode, data)
            }
        }
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }
        companion object {
            const val REQ_TAKE_FILE = 10001
            const val REQ_SELECT_FILE = 10002
            const val REQ_ZOOM_PHOTO = 10003
            private const val TAG = "EasyPhoto:PhotoFragment"
            @JvmStatic
            fun findOrCreate(activity: Activity): PhotoFragment {
                var fragment: PhotoFragment? = activity.fragmentManager.findFragmentByTag(TAG) as PhotoFragment?
                if (fragment == null) {
                    fragment = PhotoFragment()
                    activity.fragmentManager.beginTransaction()
                        .add(fragment, TAG)
                        .commitAllowingStateLoss()
                    activity.fragmentManager.executePendingTransactions()
                }
                return fragment
            }
        }
    }
}