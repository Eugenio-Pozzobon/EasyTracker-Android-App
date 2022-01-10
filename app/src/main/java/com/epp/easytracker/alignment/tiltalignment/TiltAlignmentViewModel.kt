package com.epp.easytracker.alignment.tiltalignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.epp.easytracker.database.Profile
import com.epp.easytracker.database.ProfileDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for TiltAlignmentFragment.
 */
class TiltAlignmentViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    // data that is updated to fragment use
    var gpsData = MutableLiveData<String>()
    var bluetoothMac = MutableLiveData<String>()

    private lateinit var lastProfile: Profile

    init {
        getLastProfile()
    }

    // get the last profile in the database and update the local variable
    private fun getLastProfile(){
        viewModelScope.launch{

            if(getLastProfileInDatabase() == null) {
                gpsData.value = ""
                bluetoothMac.value = ""

            }else {
                lastProfile = getLastProfileInDatabase()!!
                gpsData.value = lastProfile.gpsData
                bluetoothMac.value = lastProfile.btAddress
            }
        }
    }

    // get the last profile in the database
    private suspend fun getLastProfileInDatabase(): Profile?{
        var prof:Profile?
        withContext(Dispatchers.IO) {
            prof = database.getLastProfile(true)
        }
        return prof
    }
}