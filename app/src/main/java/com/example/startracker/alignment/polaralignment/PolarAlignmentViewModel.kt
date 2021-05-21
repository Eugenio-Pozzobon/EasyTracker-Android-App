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

/**
 * ViewModel for PolarAlignmentFragment.
 */
class PolarAlignmentViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    // data that is updated to fragment use
    var declination = MutableLiveData<String>()

    private lateinit var lastProfile: Profile

    init {
        getLastProfile()
    }

    // get the last profile in the database and update the local variable
    private fun getLastProfile(){
        viewModelScope.launch{

            if(getLastProfileInDatabase() == null) {
                declination.value = ""

            }else {
                lastProfile = getLastProfileInDatabase()!!
                declination.value = lastProfile.declination
            }
        }
    }

    // get the last profile in the database
    private suspend fun getLastProfileInDatabase(): Profile?{
        var prof: Profile?
        withContext(Dispatchers.IO) {
            prof = database.getLastProfile(true)
        }
        return prof
    }

}