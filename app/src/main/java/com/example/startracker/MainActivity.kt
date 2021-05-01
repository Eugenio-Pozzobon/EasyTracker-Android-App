package com.example.startracker


import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.startracker.database.ProfileDatabase
import com.example.startracker.database.ProfileDatabaseDao
import com.example.startracker.databinding.ActivityMainBinding
import com.example.startracker.newprofile.NewProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var binding: ActivityMainBinding

    private lateinit var destinationHandler: NavDestination

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)

        toolbar = binding.toolbar
        //toolbar.setNavigationIcon(null)          // to hide Navigation icon
        setSupportActionBar(toolbar)

        drawerLayout = binding.drawerLayout

        val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController
        //Change default home screen
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.nav_graph)


        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
            if (nd.id == R.id.currentProfileFragment) {
                navGraph.startDestination = R.id.currentProfileFragment
                setDrawer_locked()
            }
        }

        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
            if (nd.id == R.id.welcomeFragment) {
                navGraph.startDestination = R.id.welcomeFragment
                setDrawer_locked()
            }
        }

        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
            destinationHandler = nd
            if (nd.id == nc.graph.startDestination) {
                setDrawer_locked()
            } else {
                setDrawer_unLocked()
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)

        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    fun setDrawer_locked(){
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        toolbar.setNavigationIcon(null)
    }
    fun setDrawer_unLocked(){
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed()
        }
    }

    private fun shouldAllowBack(): Boolean {
        if(destinationHandler.id == R.id.currentProfileFragment){
            return false
        }

        return true
    }
}