package com.example.startracker.newprofile

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.*
import com.example.startracker.database.Profile
import com.example.startracker.database.ProfileDatabaseDao
import kotlinx.coroutines.*


/**
 * ViewModel for NewProfileFragment.
 */
class EditProfileViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.

    private var viewModelJob = Job()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel.
     *
     * Because we pass it [viewModelJob], any coroutine started in this uiScope can be cancelled
     * by calling `viewModelJob.cancel()`
     *
     * By default, all coroutines started in uiScope will launch in [Dispatchers.Main] which is
     * the main thread on Android. This is a sensible default because most coroutines started by
     * a [ViewModel] update the UI after performing some processing.
    */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
     */

    var gpsData = MutableLiveData<String>()
    var magDeclination = MutableLiveData<String>()
    var bluetoothMac = MutableLiveData<String>()
    var profileName = MutableLiveData<String>()

    private lateinit var lastProfile: Profile

    private var _onEdit = MutableLiveData<Boolean>()
    //    private var _goToNewProfile = MutableLiveData<Boolean>()
    private var _goToLoadProfiles = MutableLiveData<Boolean>()

    val onEdit: LiveData<Boolean>
        get() = _onEdit

//    val goToNewProfile: LiveData<Boolean>
//        get() = _goToNewProfile

    val goToLoadProfiles: LiveData<Boolean>
        get() = _goToLoadProfiles


    private fun doneOnEdit() {
        _onEdit.value = true
    }

    init {
//        _goToNewProfile.value = false
        _goToLoadProfiles.value = false
        _onEdit.value = false
        getLastProfile()

        gpsData.value = ""
        magDeclination.value = ""
        bluetoothMac.value = ""
        profileName.value = ""
    }


    private fun getLastProfile(){
        viewModelScope.launch{
            lastProfile = database.getLastProfile(true)!!
            profileName.value = lastProfile.profileName
            gpsData.value = lastProfile.gpsData
            magDeclination.value = lastProfile.declination
            gpsData.value = lastProfile.gpsData
            magDeclination.value = lastProfile.declination
            bluetoothMac.value = lastProfile.btAddress
        }
    }

    private suspend fun update(profile: Profile) {
        withContext(Dispatchers.IO) {
            database.update(profile)
        }
    }

    private suspend fun deleteByLastProfile() {
        withContext(Dispatchers.IO) {
            database.deleteLastProfile(true)
        }
    }

//    Checa se os valores estão validadaos
    private var _setNameError = MutableLiveData<Boolean>()
    private var _setGpsDataError = MutableLiveData<Boolean>()
    private var _setMagDeclinationError = MutableLiveData<Boolean>()

    val setNameError: LiveData<Boolean>
        get() = _setNameError
    val setGpsDataError: LiveData<Boolean>
        get() = _setGpsDataError
    val setMagDeclinationError: LiveData<Boolean>
        get() = _setMagDeclinationError

    private fun checkValues(): Boolean {
        if(("null" == (profileName.value.toString())) || ("" == (profileName.value.toString()))){
            _setNameError.value = true
            return false
        }else{
            _setNameError.value = false
        }
        if(("null" == (gpsData.value.toString()))|| ("" == (gpsData.value.toString()))){
            _setGpsDataError.value = true
            return false
        }else{
            _setGpsDataError.value = false
        }
        if(("null" == (magDeclination.value.toString()))|| ("" == (magDeclination.value.toString()))){
            _setMagDeclinationError.value = true
            return false
        }else{
            _setMagDeclinationError.value = false
        }

        return true
    }


    fun onEdit(){
        viewModelScope.launch {
            if (checkValues()) {
                lastProfile.lastProfile = true
                lastProfile.profileName = profileName.value.toString()

                lastProfile.gpsData = gpsData.value.toString()
                lastProfile.declination = magDeclination.value.toString()

                //lastProfile.btAddress = bluetoothMac.value.toString()

                update(lastProfile)
                doneOnEdit()
            }
        }
    }

    fun onDelete(){
        viewModelScope.launch {
            deleteByLastProfile()
            doneOnDelete()
        }
    }

    private fun doneOnDelete() {
//        if(true){
//            _goToNewProfile.value = true
//        }else{
            _goToLoadProfiles.value = true
//        }
    }


    fun doneOnChangeScreen() {
//        _goToNewProfile.value = false
        _goToLoadProfiles.value = false
        _onEdit.value = false
    }

    /**
     * Called when the ViewModel is dismantled.
     * At this point, we want to cancel all coroutines;
     * otherwise we end up with processes that have nowhere to return to
     * using memory and resources.

    override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
    }
     */
}