package com.example.startracker.newprofile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.startracker.database.ProfileDatabaseDao

//Use the view model factory for start ViewModels with parameters that is the app and profile database
class NewProfileViewModelFactory(
    private val dataSource: ProfileDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewProfileViewModel ::class.java)) {
            return NewProfileViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}