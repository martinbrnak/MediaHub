package com.MartinBrnak.mediahub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottomNav)
            .setupWithNavController(navController)
        handleShareIntent(intent)
    }

    private fun handleShareIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEND) {
            val sharedLink = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (sharedLink != null) {
                // Navigate to the ProfileFragment with the shared link
                val navController = findNavController(R.id.nav_host_fragment)
                val bundle = Bundle().apply {
                    putString("sharedLink", sharedLink)
                }
                navController.navigate(R.id.ImportFragment, bundle)
            }
        }
    }
}


