package com.example.startracker.loadprofiles

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.startracker.database.ProfileDatabaseDao
import com.example.startracker.loadprofiles.LoadProfilesViewModel

//Use the view model factory for start ViewModels with parameters that is the app and profile database
class LoadProfilesViewModelFactory(
    private val dataSource: ProfileDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoadProfilesViewModel ::class.java)) {
            return LoadProfilesViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}