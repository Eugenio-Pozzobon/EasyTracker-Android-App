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

    private var _marginX = MutableLiveData<Float>()
    val marginX: LiveData<Float>
        get() = _marginX

    private var _marginY = MutableLiveData<Float>()
    val marginY: LiveData<Float>
        get() = _marginY

    private var _updateAlignmentCheck = MutableLiveData<Boolean>()
    val updateAlignmentCheck: LiveData<Boolean>
        get() = _updateAlignmentCheck


    init{
        _marginX.value =  0F
        _marginY.value =  0F
        updateAlignment()
    }

    fun updateAlignment(){
        viewModelScope.launch{
            val pitch:Float = 90F
            val roll:Float = 0F

            val valueMax:Float = 90F
            val valueMin:Float = -90F

            val paddingMax:Float = 115.0F
            val paddingMin:Float = -115.0F

            _marginX.value =  mapFloat(-pitch, valueMin, valueMax, paddingMin, paddingMax)
            _marginY.value = mapFloat(roll, valueMin, valueMax, paddingMin, paddingMax)
            _updateAlignmentCheck.value = true
        }
    }



    fun doneUpdateAlignment(){
        _updateAlignmentCheck.value = false
    }
}