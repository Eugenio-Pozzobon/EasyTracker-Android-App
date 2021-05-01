package com.example.startracker.alignment.endalignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.startracker.database.ProfileDatabaseDao

class EndAlignmentViewModel(
val database: ProfileDatabaseDao,
application: Application
) : AndroidViewModel(application) {

}