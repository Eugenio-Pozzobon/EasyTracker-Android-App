package com.example.StarTracker


import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.StarTracker.NewProfile.NewProfileFragment
import com.example.StarTracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)

        setSupportActionBar(binding.toolbar)

        drawerLayout = binding.drawerLayout

        val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView,navController)


        // prevent nav gesture if not on start destination
        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
            if (nd.id == nc.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController,drawerLayout)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.initial_menu, menu)

        val currentFragment = supportFragmentManager.fragments.last()?.getChildFragmentManager()?.getFragments()?.get(0)
        if (currentFragment is WelcomeFragment){
            //Log.i("CUSTOM LOG", "CLEAN")
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(false)
        }
        //Log.i("CUSTOM LOG", "ACTIVITY START")

        return true
    }

//    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        //Log.i("CUSTOM LOG", "ACTIVITY PREPARE")
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.aboutFragment -> _log()
//            R.id.howToUseFragment -> _log()
//            R.id.debugFragment -> _log()
//            android.R.id.home -> _onBackPressed()
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun _onBackPressed(): Boolean {
        onBackPressed()
        invalidateOptionsMenu()
        //Log.i("CUSTOM LOG", "BACK PRESSED START")
        return true
    }


//    private fun _log(): Boolean {
//        Log.i("CUSTOM LOG", "Option pressed")
//        return true
//    }
}