package com.example.startracker.alignment.endalignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.startracker.database.ProfileDatabaseDao

class EndAlignmentViewModel(
val database: ProfileDatabaseDao,
application: Application
) : AndroidViewModel(application) {

    private var _trackingTime = MutableLiveData<String>()
    val trackingTime: LiveData<String>
        get() = _trackingTime

    fun updateTextTimer(value:String){
        _trackingTime.value = value
    }
}