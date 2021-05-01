package com.example.startracker.alignment.polaralignment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.startracker.alignment.polaralignment.PolarAlignmentViewModel
import com.example.startracker.database.ProfileDatabaseDao

class PolarAlignmentViewModelFactory(
    private val dataSource: ProfileDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PolarAlignmentViewModel ::class.java)) {
            return PolarAlignmentViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}