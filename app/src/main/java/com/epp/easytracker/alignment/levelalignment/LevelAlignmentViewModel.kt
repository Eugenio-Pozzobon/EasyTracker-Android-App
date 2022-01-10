package com.epp.easytracker.alignment.levelalignment

import android.app.Application
import androidx.lifecycle.*
import com.epp.easytracker.database.ProfileDatabaseDao

/**
 * ViewModel for LevelAlignmentFragment.
 */
class LevelAlignmentViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

}