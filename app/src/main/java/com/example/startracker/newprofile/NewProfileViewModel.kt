package com.example.startracker.newprofile

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.*
import com.example.startracker.database.Profile
import com.example.startracker.database.ProfileDatabaseDao

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for NewProfileFragment.
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

    private var _onConnected = MutableLiveData<Boolean>()

    /**
     * If this is true, immediately `show()` a toast and call `doneShowingSnackbar()`.
     */
    val onConnected: LiveData<Boolean>
        get() = _onConnected

    fun doneOnConnected(){
        _onConnected.value = true
    }

    fun doneOnChangeScreen() {
        _onConnected.value = false
    }

    fun addProfileName(){
        profile_name = ""
    }

    val gpsTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // Do nothing.
        }
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            (s.toString()).toFloatOrNull() ?: gps_data
            Log.i("TextGPSChange", "CHANGED!")
        }
        override fun afterTextChanged(s: Editable) {
            // Do nothing.
        }
    }

    fun addMagDeclination(){
        mag_declination = 0.0
    }

    fun addBluetoothMac(){
        bluetooth_mac = ""
    }

    init {

    }

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

    suspend fun onConnect(){
        viewModelScope.launch {
            var newProfile = Profile()
            if (checkValues()) {
                newProfile.lastProfile = true
                newProfile.profileName = profile_name
                newProfile.gpsData = gps_data
                newProfile.declination = mag_declination
                newProfile.btAdress = bluetooth_mac
                insert(newProfile)
                doneOnConnected()
            }
        }
    }

    private fun checkValues(): Boolean {
        return true
    }

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