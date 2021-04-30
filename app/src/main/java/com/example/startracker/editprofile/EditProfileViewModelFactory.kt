package com.example.startracker.editprofile;

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.startracker.database.ProfileDatabaseDao
import com.example.startracker.newprofile.EditProfileViewModel

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