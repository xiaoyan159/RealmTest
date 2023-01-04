package com.navinfo.volvo.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.navinfo.volvo.R
import com.navinfo.volvo.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = binding.navView
        val newMessageView = binding.newMessageFab
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_message, R.id.navigation_dashboard, R.id.navigation_notifications,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.navigation_message
                || destination.id == R.id.navigation_dashboard
                || destination.id == R.id.navigation_notifications
                || destination.id == R.id.navigation_obtain_message
            ) {
                runOnUiThread {
                    navView.visibility = View.VISIBLE
                    newMessageView.visibility = View.VISIBLE
                }
            } else {
                runOnUiThread {
                    navView.visibility = View.GONE
                    newMessageView.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp() =
        findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
}