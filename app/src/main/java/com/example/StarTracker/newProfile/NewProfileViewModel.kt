package com.example.StarTracker.newProfile

import android.app.Application
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.Profile
import com.example.android.trackmysleepquality.database.ProfileDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for SleepTrackerFragment.
 */
class NewProfileViewModel(
        val database: ProfileDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.

    private var viewModelJob = Job()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel.
     *
     * Because we pass it [viewModelJob], any coroutine started in this uiScope can be cancelled
     * by calling `viewModelJob.cancel()`
     *
     * By default, all coroutines started in uiScope will launch in [Dispatchers.Main] which is
     * the main thread on Android. This is a sensible default because most coroutines started by
     * a [ViewModel] update the UI after performing some processing.
    */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
     */

    var profile_name: String = ""
    var gps_data: Double = 0.0
    var mag_declination: Double = 0.0
    var bluetooth_mac: String = ""


//    fun onSleepNightClicked(id: Long) {
//        _navigateToSleepDataQuality.value = id
//    }

//    fun onSleepDataQualityNavigated() {
//        _navigateToSleepDataQuality.value = null
//    }

    init {

    }

    /**
     *  Handling the case of the stopped app or forgotten recording,
     *  the start and end times will be the same.j
     *
     *  If the start time and end time are not the same, then we do not have an unfinished
     *  recording.
     */
    private suspend fun updateLastProfile(){
        withContext(Dispatchers.IO) {
            var last_profile = database.getLastProfile(true)
            if (last_profile != null) {
                last_profile.lastProfile = false
                update(last_profile)
            }
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    private suspend fun update(profile: Profile) {
        withContext(Dispatchers.IO) {
            database.update(profile)
        }
    }

    private suspend fun insert(profile: Profile) {
        withContext(Dispatchers.IO) {
            database.insert(profile)
        }
    }

    fun checkValues(): Boolean{
        return true
    }

    suspend fun onConnect(){

        var newProfile = Profile()
        if (checkValues()){
            newProfile.lastProfile = true
            newProfile.profileName = profile_name
            newProfile.gpsData = gps_data
            newProfile.declination = mag_declination
            newProfile.btAdress = bluetooth_mac
            insert(newProfile)

        }
    }

    /**
     * Executes when the START button is clicked.
     */
//    fun onStartTracking() {
//        viewModelScope.launch {
//            // Create a new night, which captures the current time,
//            // and insert it into the database.
//            val newNight = SleepNight()
//
//            insert(newNight)
//
//            tonight.value = getTonightFromDatabase()
//        }
//    }

//    /**
//     * Executes when the STOP button is clicked.
//     */
//    fun onStopTracking() {
//        viewModelScope.launch {
//            // In Kotlin, the return@label syntax is used for specifying which function among
//            // several nested ones this statement returns from.
//            // In this case, we are specifying to return from launch(),
//            // not the lambda.
//            val oldNight = tonight.value ?: return@launch
//
//            // Update the night in the database to add the end time.
//            oldNight.endTimeMilli = System.currentTimeMillis()
//
//            update(oldNight)
//
//            // Set state to navigate to the SleepQualityFragment.
//            _navigateToSleepQuality.value = oldNight
//        }
//    }

    /**
     * Executes when the CLEAR button is clicked.
     */
    fun onClear() {
        viewModelScope.launch {
            // Clear the database table.
            clear()
        }
    }

    /**
     * Called when the ViewModel is dismantled.
     * At this point, we want to cancel all coroutines;
     * otherwise we end up with processes that have nowhere to return to
     * using memory and resources.

    override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
    }
     */
}