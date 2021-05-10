package com.example.startracker.alignment.levelalignment

import android.app.Application
import androidx.lifecycle.*
import com.example.startracker.database.ProfileDatabaseDao
import com.example.startracker.mapFloat
import kotlinx.coroutines.launch

class LevelAlignmentViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

}