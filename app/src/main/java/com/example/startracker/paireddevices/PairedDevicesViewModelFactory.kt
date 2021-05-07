package com.example.startracker.paireddevices;


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.startracker.database.ProfileDatabaseDao;

import kotlin.Suppress;

class PairedDevicesViewModelFactory(
    private val dataSource:ProfileDatabaseDao,
    private val application:Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PairedDevicesViewModel ::class.java)) {
            return PairedDevicesViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
