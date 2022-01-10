package com.epp.easytracker.newprofile

import android.app.Application
import androidx.lifecycle.*
import com.epp.easytracker.database.Profile
import com.epp.easytracker.database.ProfileDatabaseDao
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

    private var _startConnection = MutableLiveData<Boolean>()
    private var _onConnected = MutableLiveData<Boolean>()

    val startConnection: LiveData<Boolean>
        get() = _startConnection
    val onConnected: LiveData<Boolean>
        get() = _onConnected

    fun onStartConnection(){
        viewModelScope.launch {
            _startConnection.value = true
        }
    }
    // signalize to view model the current state of fragment
    fun doneOnConnected(){
        viewModelScope.launch {
            _onConnected.value = true
        }
    }

    fun doneOnChangeScreen() {
        viewModelScope.launch {
            _onConnected.value = false
            _startConnection.value = false
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

    // Check if all values are valid, i.e., isn't a null or empty string
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
    fun updateDeclination(declination: String){
        magDeclination.value = declination
    }
}