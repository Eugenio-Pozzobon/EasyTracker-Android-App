package com.example.startracker.currentprofile

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.startracker.MainActivity
import com.example.startracker.database.Profile
import com.example.startracker.database.ProfileDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CurrentProfileViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    var connectionStatus = MutableLiveData<String>()
    var gpsDataString = MutableLiveData<String>()
    var magDeclinationString = MutableLiveData<String>()
    var gpsData = MutableLiveData<String>()
    var magDeclination = MutableLiveData<String>()
    var bluetoothMac = MutableLiveData<String>()

    private var _profileName = MutableLiveData<String>()
    val profileName: LiveData<String>
        get() = _profileName

    private var _startAlignmentButtonVisible = MutableLiveData<Boolean>()
    val startAlignmentButtonVisible: LiveData<Boolean>
        get() = _startAlignmentButtonVisible

    private var _noLastProfileAvailable = MutableLiveData<Boolean>()
    val noLastProfileAvailable: LiveData<Boolean>
        get() = _noLastProfileAvailable

    private var _newUserDetected = MutableLiveData<Boolean>()
    val newUserDetected: LiveData<Boolean>
        get() = _newUserDetected

    private var _onConnected = MutableLiveData<Boolean>()
    val onConnected: LiveData<Boolean>
        get() = _onConnected

    private var _screenChange = MutableLiveData<Boolean>()
    val screenChange: LiveData<Boolean>
        get() = _screenChange

    private lateinit var lastProfile: Profile

    init {

        _newUserDetected.value = false
        _noLastProfileAvailable.value = false

        _screenChange.value = false
        _onConnected.value = true
        _startAlignmentButtonVisible.value = false
        getLastProfile()
    }

    private fun getLastProfile(){
        viewModelScope.launch{

            if(getLastProfile(true) == null) {
                _profileName.value = ""
                gpsDataString.value = "Latitude: 0"
                magDeclinationString.value = "Declinação Magnética: 0"
                gpsData.value = ""
                magDeclination.value = ""
                bluetoothMac.value = ""

                if(databaseIsExists()) {
                    _noLastProfileAvailable.value = true
                }else{
                    _newUserDetected.value = true
                }

            }else {
                lastProfile = getLastProfile(true)!!
                _profileName.value = lastProfile.profileName
                gpsDataString.value = "Latitude: " + lastProfile.gpsData
                magDeclinationString.value = "Declinação Magnética: " + lastProfile.declination + "°"
                gpsData.value = lastProfile.gpsData
                magDeclination.value = lastProfile.declination
                bluetoothMac.value = lastProfile.btAddress
            }
        }
    }

    private suspend fun getLastProfile(key: Boolean): Profile?{
        var prof:Profile?
        withContext(Dispatchers.IO) {
            prof = database.getLastProfile(key)
        }
        return prof
    }

    private suspend fun databaseIsExists():Boolean{
        val result:Boolean
        withContext(Dispatchers.IO) {
            result = database.isExists()
        }
        return result
    }

    fun onConnect(){
        viewModelScope.launch {
            doneOnConnected()
        }
    }

    fun notConnect(){
        viewModelScope.launch {
            NOTdoneOnConnected()
        }
    }

    fun startAlignment(){
        viewModelScope.launch{
            _screenChange.value = true
        }
    }

    private fun doneOnConnected(){
        _onConnected.value = false
        _startAlignmentButtonVisible.value = true
    }

    private fun NOTdoneOnConnected(){
        _onConnected.value = true
        _startAlignmentButtonVisible.value = false
    }

    fun doneOnChangeScreen() {
        _screenChange.value = false
        _onConnected.value = false
        _startAlignmentButtonVisible.value = true
    }
}