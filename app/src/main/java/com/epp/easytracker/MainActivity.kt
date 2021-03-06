package com.epp.easytracker


import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
import com.epp.easytracker.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    var hc05 = BluetoothService()
    lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var destinationHandler: NavDestination

    override fun onCreate(savedInstanceState: Bundle?) {
        // set the Android Night Mode as default for the app
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        setTheme(R.style.Theme_MyApplication_NoActionBar)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get toolbar instance
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        drawerLayout = binding.drawerLayout

        //Get and setup navcontroller that will make the navigation between fragments
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.nav_graph)

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        //change home screen in agreement with the case of new user or not
        //TODO: check if this function can be removed
        navController.addOnDestinationChangedListener { nc: NavController,
                                                        nd: NavDestination,
                                                        args: Bundle? ->
            if (nd.id == R.id.currentProfileFragment) {
                navGraph.setStartDestination(R.id.currentProfileFragment)
                setDrawer_locked()
            }
        }

        // check if the screen is for new user, and remove/locking
        // navigation parts of the app, making it clean
        navController.addOnDestinationChangedListener { nc: NavController,
                                                        nd: NavDestination,
                                                        args: Bundle? ->
            if (nd.id == R.id.welcomeFragment) {
                navGraph.setStartDestination(R.id.welcomeFragment)
                setDrawer_locked()
                toolbar.navigationIcon = null
            }
        }

        //set the back button menu to be hide at home screen
        navController.addOnDestinationChangedListener { nc: NavController,
                                                        nd: NavDestination,
                                                        args: Bundle? ->
            destinationHandler = nd
            if (nd.id == nc.graph.startDestinationId) {
                //setDrawer_locked()
                setDrawer_unLocked()
            } else {
                //setDrawer_unLocked()
                setDrawer_locked()
            }
        }

    }

    //activate the navigation in acivity
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)

        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    //set the back button menu to be displayed or not
    fun setDrawer_locked() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        //toolbar.setNavigationIcon(null)
    }

    fun setDrawer_unLocked() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    //block native back button
    override fun onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed()
        }
    }

    //block native back button for ensure that the new user wont back to
    // the welcome screen manualy and/or replicate the same profile
    private fun shouldAllowBack(): Boolean {
        if (destinationHandler.id == R.id.currentProfileFragment) {
            return false
        }

        return true
    }

    //Hide Keyboard when user touch outside
    //https://stackoverflow.com/questions/8697499/
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view: View? = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP ||
                    ev.action == MotionEvent.ACTION_MOVE) && view is EditText
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x: Float = ev.rawX + view.getLeft() - scrcoords[0]
            val y: Float = ev.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop()
                || y > view.getBottom()
            ) (this.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow(
                this.window.decorView.applicationWindowToken, 0
            )
        }
        return super.dispatchTouchEvent(ev)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {} // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {} // Night mode is active, we're using dark theme
        }
    }
}