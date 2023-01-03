package com.navinfo.volvo

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.easytools.tools.FileUtils
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.interceptor.BlacklistTagsFilterInterceptor
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.ConsolePrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.navinfo.volvo.databinding.ActivityMainBinding
import com.navinfo.volvo.ui.message.MessageActivity
import com.navinfo.volvo.utils.SystemConstant


//@RuntimePermissions
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_obtain_message
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        findViewById<View>(R.id.fab_new_message).apply {
            this.setOnClickListener {
                // 跳转到Message的Fragment
                val messageIntent:Intent = Intent(this@MainActivity, MessageActivity::class.java)
                startActivity(messageIntent)
//                findNavController(R.id.layer_main_child_fragment).navigate(R.id.navigation_obtain_message)
            }
        }

        XXPermissions.with(this)
            // 申请单个权限
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .permission(Permission.READ_EXTERNAL_STORAGE)
            // 设置权限请求拦截器（局部设置）
            //.interceptor(new PermissionInterceptor())
            // 设置不触发错误检测机制（局部设置）
            //.unchecked()
            .request(object : OnPermissionCallback {

                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    if (!all) {
                        Toast.makeText(this@MainActivity, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show()
                        return
                    }
                    // 在SD卡创建项目目录
                    createRootFolder()
                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    if (never) {
                        Toast.makeText(this@MainActivity, "永久拒绝授权,请手动授权文件读写权限", Toast.LENGTH_SHORT).show()
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(this@MainActivity, permissions)
                    } else {
                        onSDCardDenied()
                        showRationaleForSDCard(permissions)
                    }
                }
            })
    }

//    @NeedsPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
    fun createRootFolder() {
        // 在SD卡创建项目目录
        val sdCardPath = getExternalFilesDir(null)
//        SystemConstant.ROOT_PATH = "${sdCardPath}/${SystemConstant.FolderName}"
        SystemConstant.ROOT_PATH = sdCardPath!!.absolutePath
        SystemConstant.LogFolder = "${sdCardPath!!.absolutePath}/log"
        FileUtils.createOrExistsDir(SystemConstant.LogFolder)
        SystemConstant.CameraFolder = "${sdCardPath!!.absolutePath}/camera"
        FileUtils.createOrExistsDir(SystemConstant.CameraFolder)
        SystemConstant.SoundFolder = "${sdCardPath!!.absolutePath}/sound"
        FileUtils.createOrExistsDir(SystemConstant.SoundFolder)
        xLogInit(SystemConstant.LogFolder)
    }

    fun xLogInit(logFolder: String) {
        val config = LogConfiguration.Builder()
            .logLevel(
                if (BuildConfig.DEBUG)
                    LogLevel.ALL // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
                else LogLevel.NONE
            )
            .tag("Volvo") // 指定 TAG，默认为 "X-LOG"
            .enableThreadInfo() // 允许打印线程信息，默认禁止
            .enableStackTrace(2) // 允许打印深度为 2 的调用栈信息，默认禁止
            .enableBorder() // 允许打印日志边框，默认禁止
            .addInterceptor(
                BlacklistTagsFilterInterceptor( // 添加黑名单 TAG 过滤器
                    "blacklist1", "blacklist2", "blacklist3"
                )
            )
            .build()

        val androidPrinter: Printer = AndroidPrinter(true) // 通过 android.util.Log 打印日志的打印器

        val consolePrinter: Printer = ConsolePrinter() // 通过 System.out 打印日志到控制台的打印器

        val filePrinter: Printer = FilePrinter.Builder("${SystemConstant.ROOT_PATH}/Logs") // 指定保存日志文件的路径
            .fileNameGenerator(DateFileNameGenerator()) // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
            .backupStrategy(NeverBackupStrategy()) // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
            .build()

        XLog.init( // 初始化 XLog
            config,  // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
            androidPrinter,  // 添加任意多的打印器。如果没有添加任何打印器，会默认使用 AndroidPrinter(Android)/ConsolePrinter(java)
            consolePrinter,
            filePrinter
        )
    }

//    @OnShowRationale(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
    fun showRationaleForSDCard(permissions: MutableList<String>) {
//        showRationaleDialog(R.string.permission_camera_rationale, request)
//        Toast.makeText(context, "当前操作需要您授权相机权限！", Toast.LENGTH_SHORT).show()
        MaterialAlertDialogBuilder(this)
            .setTitle("提示")
            .setMessage("当前操作需要您授权读写SD卡权限！")
            .setPositiveButton("确定", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
                XXPermissions.startPermissionActivity(this@MainActivity, permissions)
            })
            .show()
    }

//    @OnPermissionDenied(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
    fun onSDCardDenied() {
        Toast.makeText(this, "当前操作需要您授权读写SD卡权限！", Toast.LENGTH_SHORT).show()
    }
}