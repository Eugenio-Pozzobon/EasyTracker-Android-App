package com.example.startracker.newprofile

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.startracker.database.Profile
import com.example.startracker.database.ProfileDatabaseDao
import kotlinx.coroutines.*


/**
 * ViewModel for NewProfileFragment.
 */
class NewProfileViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

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


    var profileName = MutableLiveData<String>()
    var gpsData = MutableLiveData<String>()
    var magDeclination = MutableLiveData<String>()
    var bluetoothMac = MutableLiveData<String>()

    private var _onConnected = MutableLiveData<Boolean>()

    val onConnected: LiveData<Boolean>
        get() = _onConnected

    fun doneOnConnected(){
        _onConnected.value = true
    }

    fun doneOnChangeScreen() {
        _onConnected.value = false
    }

    init {

    }

    private suspend fun updateLastProfile(){
        withContext(Dispatchers.IO) {
            var lastProfile = database.getLastProfile(true)
            if (lastProfile != null) {
                lastProfile.lastProfile = false
                update(lastProfile)
            }
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


    private fun checkValues(): Boolean {
        return true
    }

    fun onConnect(){
        viewModelScope.launch {
            var newProfile = Profile()
            if (checkValues()) {
                updateLastProfile()
                newProfile.lastProfile = true
                newProfile.profileName = profileName.value.toString()

                newProfile.gpsData = gpsData.value.toString()
                newProfile.declination = magDeclination.value.toString()

                newProfile.btAddress = bluetoothMac.value.toString()
                insert(newProfile)
                doneOnConnected()
            }
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