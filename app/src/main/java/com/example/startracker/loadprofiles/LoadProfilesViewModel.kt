package com.example.startracker.loadprofiles

import android.app.Application
import androidx.lifecycle.*
import com.example.startracker.database.ProfileDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoadProfilesViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    val profiles = database.getAllProfiles()

    val clearButtonVisible = Transformations.map(profiles) {
        it?.isNotEmpty()
    }

    init {

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