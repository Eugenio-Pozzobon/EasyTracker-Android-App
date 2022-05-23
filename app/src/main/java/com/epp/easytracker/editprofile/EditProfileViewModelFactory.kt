package com.epp.easytracker.editprofile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.epp.easytracker.database.ProfileDatabaseDao

//Use the view model factory for start ViewModels with parameters that is the app and profile database
@Suppress("UNCHECKED_CAST")
class EditProfileViewModelFactory (
    private val dataSource:ProfileDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T{
        if(modelClass.isAssignableFrom(EditProfileViewModel::class.java)){
            return EditProfileViewModel(dataSource,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}