package com.example.startracker


import android.os.Bundle
import android.util.Log
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
import com.example.startracker.databinding.ActivityMainBinding

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



        navGraph.startDestination = R.id.welcomeFragment
        navController.graph = navGraph
        navGraph.label = ""
        navHostFragment.navController.graph = navGraph

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        if (true){
            //TODO: detect old user
            navGraph.startDestination = R.id.welcomeFragment

            navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
                if (nd.id == R.id.currentProfileFragment) {
                    navGraph.startDestination = R.id.currentProfileFragment
                    setDrawer_locked()
                }
            }

        }else {
            navGraph.startDestination = R.id.currentProfileFragment
            setDrawer_locked()
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
        Log.i("CUSTOM TAG", "SETTED LOCKED")
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        toolbar.setNavigationIcon(null)
    }
    fun setDrawer_unLocked(){
        Log.i("CUSTOM TAG", "SETTED LOCKED")
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