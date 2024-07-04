package com.MartinBrnak.mediahub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

class MainActivity : AppCompatActivity() {


    /*
    * Every File has some help from AI / the book
    * Mostly helped from Claude
    *
    *
    *
    * */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text(text = "hello")
        }



        //setContentView(R.layout.activity_main)

        //val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        //val navController = navHostFragment.navController
        //findViewById<BottomNavigationView>(R.id.bottomNav)
        //    .setupWithNavController(navController)
        //handleShareIntent(intent)
    }

    //private fun handleShareIntent(intent: Intent) {
    //    if (intent.action == Intent.ACTION_SEND) {
    //        val sharedLink = intent.getStringExtra(Intent.EXTRA_TEXT)
    //        if (sharedLink != null) {
    //            // Navigate to the ProfileFragment with the shared link
    //            val navController = findNavController(R.id.nav_host_fragment)
    //            val bundle = Bundle().apply {
    //                putString("sharedLink", sharedLink)
    //            }
    //            navController.navigate(R.id.ImportFragment, bundle)
    //        }
    //    }
    //}
}


