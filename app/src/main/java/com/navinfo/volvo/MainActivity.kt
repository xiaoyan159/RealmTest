package com.navinfo.volvo

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.navinfo.volvo.R
import com.navinfo.volvo.databinding.ActivityMainBinding
import permissions.dispatcher.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @NeedsPermission(Manifest.permission.CAMERA)
    override fun onCreate(savedInstanceState: Bundle?) {
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
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    fun showRationaleForCamera(request: PermissionRequest) {
//        showRationaleDialog(R.string.permission_camera_rationale, request)
//        Toast.makeText(context, "当前操作需要您授权相机权限！", Toast.LENGTH_SHORT).show()
        MaterialAlertDialogBuilder(this)
            .setTitle("提示")
            .setMessage("当前操作需要您授权读写SD卡权限！")
            .setPositiveButton("确定", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
                // 在SD卡创建项目目录

            })
            .show()
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraDenied() {
        Toast.makeText(this, "当前操作需要您授权读写SD卡权限！", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun onCameraNeverAskAgain() {
        Toast.makeText(this, "您已永久拒绝授权读写SD卡权限！", Toast.LENGTH_SHORT).show()
    }
}