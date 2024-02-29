package com.epp.easytracker.loadprofiles

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.epp.easytracker.database.Profile
import com.epp.easytracker.database.ProfileDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoadProfilesViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    var profiles = database.getAllProfiles()
    val profilesFlow = profiles.asFlow()
    val clearButtonVisible = profilesFlow
        .map { profiles ->
            profiles.isNotEmpty()
        }
        .asLiveData() // Only if you need to observe this as LiveData in the UI

//    val clearButtonVisible = Transformations.map(profiles) {
//        it?.isNotEmpty()
//    }

    private val _navigateToEditProfile = MutableLiveData<Boolean>()
    val navigateToEditProfile
        get() = _navigateToEditProfile


    private val _navigateToCurrentProfile = MutableLiveData<Boolean>()
    val navigateToCurrentProfile
        get() = _navigateToCurrentProfile

    private lateinit var profileSelected:Profile
    private lateinit var lastProfileSelected:Profile

    fun onProfileClicked(id: Long) {
        Log.i("VIEWMODELDEBUG", id.toString())
        viewModelScope.launch {
            if (getLastProfile(true) != null) {
                lastProfileSelected = getLastProfile(true)!!
                lastProfileSelected.lastProfile = false
                update(lastProfileSelected)
            }

            Log.i("VIEWMODELDEBUG", "END UPDATE")
            profileSelected = getProfileWithId(id)
            profileSelected.lastProfile = true
            update(profileSelected)
            allowLoadToCurrentFragment()
        }
    }

    fun onEditClicked(id: Long) {
        Log.i("VIEWMODELDEBUG", id.toString())
        viewModelScope.launch {
            if (getLastProfile(true) != null) {
                lastProfileSelected = getLastProfile(true)!!
                lastProfileSelected.lastProfile = false
                update(lastProfileSelected)
            }

            Log.i("VIEWMODELDEBUG", "END UPDATE")
            profileSelected = getProfileWithId(id)
            profileSelected.lastProfile = true
            update(profileSelected)
            allowLoadToEditFragment()
        }
    }

    fun onDeleteClicked(id: Long) {
        Log.i("VIEWMODELDEBUG", id.toString())
        viewModelScope.launch {
            deleteProfileWithId(id)
            allowLoadToCurrentFragment()
        }
    }

    private fun allowLoadToCurrentFragment(){
        Log.i("VIEWMODELDEBUG", "allowLoadToCurrentFragment")
        _navigateToCurrentProfile.value = true
    }

    private fun allowLoadToEditFragment(){
        Log.i("VIEWMODELDEBUG", "allowLoadToEditFragment")
        _navigateToEditProfile.value = true
    }

    init {
        _navigateToCurrentProfile.value = false
        _navigateToEditProfile.value = false
    }

    private suspend fun getLastProfile(key: Boolean): Profile?{
        var prof:Profile?
        withContext(Dispatchers.IO) {
            prof = database.getLastProfile(key)
        }
        return prof
    }

    private suspend fun getProfileWithId(key: Long): Profile{
        var prof:Profile
        withContext(Dispatchers.IO) {
            prof = database.getProfileWithId(key)
        }
        return prof
    }

    private suspend fun deleteProfileWithId(key: Long){
        var prof:Profile
        withContext(Dispatchers.IO) {
            database.deleteProfileWithId(key)
        }
    }

    private suspend fun update(profile: Profile) {
        withContext(Dispatchers.IO) {
            database.update(profile)
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
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
}