package com.example.startracker.alignment.polaralignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.startracker.database.Profile
import com.example.startracker.database.ProfileDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PolarAlignmentViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    var gpsData = MutableLiveData<String>()
    var bluetoothMac = MutableLiveData<String>()
    var declination = MutableLiveData<String>()

    private lateinit var lastProfile: Profile

    init {
        getLastProfile()
    }

    private fun getLastProfile(){
        viewModelScope.launch{

            if(getLastProfile(true) == null) {
                gpsData.value = ""
                bluetoothMac.value = ""

            }else {
                lastProfile = getLastProfile(true)!!
                gpsData.value = lastProfile.gpsData
                bluetoothMac.value = lastProfile.btAddress
                declination.value = lastProfile.declination
            }
        }
    }

    private suspend fun getLastProfile(key: Boolean): Profile?{
        var prof: Profile?
        withContext(Dispatchers.IO) {
            prof = database.getLastProfile(key)
        }
        return prof
    }


}