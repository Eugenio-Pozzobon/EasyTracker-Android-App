package com.example.startracker.paireddevices

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.startracker.database.Profile
import com.example.startracker.database.ProfileDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PairedDevicesViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var _onUpdated = MutableLiveData<Boolean>()
    val onUpdated: LiveData<Boolean>
        get() = _onUpdated

    lateinit var getprofile:Profile

    init{
        _onUpdated.value = false
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getprofile = getLastProfile()
        }
    }

    private suspend fun insert(profile: Profile) {

        withContext(Dispatchers.IO) {
            database.insert(profile)
        }
    }

    private suspend fun getLastProfile(): Profile {
        var getprofile: Profile
        withContext(Dispatchers.IO) {
            getprofile = database.getLastProfile(true)!!
        }
        deleteLastProfile()
        return getprofile
    }

    private suspend fun deleteLastProfile() {
        withContext(Dispatchers.IO) {
            database.deleteLastProfile(key = true)
        }
    }

    fun updateLastProfileWithBluetooth(address: String) {
        viewModelScope.launch {
            getprofile.btAddress = address
            insert(getprofile)
            _onUpdated.value = true
        }
    }

    fun doneChangeScreen(){
        _onUpdated.value = false
    }
}