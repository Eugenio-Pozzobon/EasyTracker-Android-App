package com.example.startracker.currentprofile

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.startracker.database.Profile
import com.example.startracker.database.ProfileDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CurrentProfileViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    // TODO: Implement the ViewModel

    var connectionStatus = MutableLiveData<String>()
    var gpsData = MutableLiveData<String>()
    var magDeclination = MutableLiveData<String>()
    var bluetoothMac = MutableLiveData<String>()

    private var _profileName = MutableLiveData<String>()
    val profileName: LiveData<String>
        get() = _profileName

    private var _startAlignmentButtonVisible = MutableLiveData<Boolean>()
    val startAlignmentButtonVisible: LiveData<Boolean>
        get() = _startAlignmentButtonVisible


    private var _onConnected = MutableLiveData<Boolean>()
    val onConnected: LiveData<Boolean>
        get() = _onConnected

    private fun doneOnConnected(){
        _onConnected.value = false
        connectionStatus.value = "Conectado"
        _startAlignmentButtonVisible.value = true
    }

    private var _screenChange = MutableLiveData<Boolean>()
    val screenChange: LiveData<Boolean>
        get() = _screenChange

    fun doneOnChangeScreen() {
        _screenChange.value = false
        _onConnected.value = false
        connectionStatus.value = "Conectar"
        _startAlignmentButtonVisible.value = true
    }

    private lateinit var lastProfile: Profile

    init {
        _screenChange.value = false
        _onConnected.value = true
        connectionStatus.value = "Conectar"
        _startAlignmentButtonVisible.value = false
        getLastProfile()

    }

    private fun getLastProfile(){
        viewModelScope.launch{
            lastProfile = database.getLastProfile(true)!!
            _profileName.value = lastProfile.profileName
            gpsData.value = lastProfile.gpsData
            magDeclination.value = lastProfile.declination
            bluetoothMac.value = lastProfile.btAddress
        }
    }

    fun onConnect(){
        viewModelScope.launch {
            doneOnConnected()
        }
    }

    fun startAlignment(){
        viewModelScope.launch{
            _screenChange.value = true
        }
    }
}