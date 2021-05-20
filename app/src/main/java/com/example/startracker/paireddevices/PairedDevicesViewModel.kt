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

    //indicate that viewModel has initialized.
    // Its used just for emulator doesn't crash because it hasn't bluetooth adapter
    private var _initialized = MutableLiveData<Boolean>()
    val initialized: LiveData<Boolean>
        get() = _initialized

    lateinit var getprofile: Profile
    init {
        _initialized.value = false
        _onUpdated.value = false
        loadProfile()
    }

    //load last profile updated in database
    private fun loadProfile() {
        viewModelScope.launch {
            getprofile = getLastProfile()
            _initialized.value = true
        }
    }

    //insert an new profile
    private suspend fun insert(profile: Profile) {

        withContext(Dispatchers.IO) {
            database.insert(profile)
        }
    }

    // get last profile and store this info in a local variable
    // delete last profile when get, because if user back to previous screen,
    //  it will save an duplicate
    private suspend fun getLastProfile(): Profile {
        var getprofile: Profile
        withContext(Dispatchers.IO) {
            getprofile = database.getLastProfile(true)!!
        }
        deleteLastProfile()
        return getprofile
    }

    // delete last profile
    private suspend fun deleteLastProfile() {
        withContext(Dispatchers.IO) {
            database.deleteLastProfile(key = true)
        }
    }

    // update profile local variable and insert it at database
    fun updateLastProfileWithBluetooth(address: String) {
        viewModelScope.launch {
            getprofile.btAddress = address
            insert(getprofile)
            _onUpdated.value = true
        }
    }

    //reset viewModel variables when fragment get paused
    fun doneChangeScreen() {
        _onUpdated.value = false
    }
}