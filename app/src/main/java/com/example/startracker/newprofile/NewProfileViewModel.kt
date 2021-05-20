package com.example.startracker.newprofile

import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
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

    var profileName = MutableLiveData<String>()
    var gpsData = MutableLiveData<String>()
    var magDeclination = MutableLiveData<String>()
    var bluetoothMac = MutableLiveData<String>()

    private var _onConnected = MutableLiveData<Boolean>()

    val onConnected: LiveData<Boolean>
        get() = _onConnected

    // signalize to view model the current state of fragment
    fun doneOnConnected(){
        _onConnected.value = true
    }

    fun doneOnChangeScreen() {
        viewModelScope.launch {
            _onConnected.value = false
        }
    }

    init {

    }

    //update last profile at database making it not more the last profile,
    // so now the new profile can get this condition
    private suspend fun updateLastProfile(){
        withContext(Dispatchers.IO) {
            val lastProfile = database.getLastProfile(true)
            if (lastProfile != null) {
                lastProfile.lastProfile = false
                update(lastProfile)
            }
        }
    }

    // update database
    private suspend fun update(profile: Profile) {
        withContext(Dispatchers.IO) {
            database.update(profile)
        }
    }

    // insert new profile in database
    private suspend fun insert(profile: Profile) {
        withContext(Dispatchers.IO) {
            database.insert(profile)
        }
    }

    private var _setNameError = MutableLiveData<Boolean>()
    private var _setGpsDataError = MutableLiveData<Boolean>()
    private var _setMagDeclinationError = MutableLiveData<Boolean>()

    val setNameError: LiveData<Boolean>
        get() = _setNameError
    val setGpsDataError: LiveData<Boolean>
        get() = _setGpsDataError
    val setMagDeclinationError: LiveData<Boolean>
        get() = _setMagDeclinationError

    // Check if all values are valid, i.e., isn't an null or empty string
    private fun checkValues(): Boolean {
        if(("null" == (profileName.value.toString())) || ("" == (profileName.value.toString()))){
            _setNameError.value = true
            return false
        }else{
            _setNameError.value = false
        }
        if(("null" == (gpsData.value.toString()))|| ("" == (gpsData.value.toString()))){
            _setGpsDataError.value = true
            return false
        }else{
            _setGpsDataError.value = false
        }
        if(("null" == (magDeclination.value.toString()))|| ("" == (magDeclination.value.toString()))){
            _setMagDeclinationError.value = true
            return false
        }else{
            _setMagDeclinationError.value = false
        }

        return true
    }

    // handle intent for go to paired devices, saving the current data in database if it is valid
    fun onConnect(){
        viewModelScope.launch {
            if (checkValues()) {
                val newProfile = Profile()
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

    // public function for fragment update view model when the user get GPS lat
    fun updateGps(latitude: String){
        gpsData.value = latitude
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