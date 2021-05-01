package com.example.startracker.alignment.polaralignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.startracker.database.ProfileDatabaseDao

class PolarAlignmentViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

}