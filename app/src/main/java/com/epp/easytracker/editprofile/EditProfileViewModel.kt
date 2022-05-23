package com.epp.easytracker.editprofile

import android.app.Application
import androidx.lifecycle.*
import com.epp.easytracker.database.Profile
import com.epp.easytracker.database.ProfileDatabaseDao
import kotlinx.coroutines.*


/**
 * ViewModel for EditProfileFragment.
 */
class EditProfileViewModel(
    val database: ProfileDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    // editable data
    var gpsData = MutableLiveData<String>()
    var magDeclination = MutableLiveData<String>()
    var bluetoothMac = MutableLiveData<String>()
    var profileName = MutableLiveData<String>()

    private lateinit var lastProfile: Profile

    private var _onEdit = MutableLiveData<Boolean>()
    private var _goToLoadProfiles = MutableLiveData<Boolean>()

    val onEdit: LiveData<Boolean>
        get() = _onEdit

    val goToLoadProfiles: LiveData<Boolean>
        get() = _goToLoadProfiles


    // init ViewModel and
    init {
        _goToLoadProfiles.value = false
        _onEdit.value = false
        getLastProfile()

        gpsData.value = ""
        magDeclination.value = ""
        bluetoothMac.value = ""
        profileName.value = ""
    }


    // get data from last profile and update local profile variable
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

    //update profile in database
    private suspend fun update(profile: Profile) {
        withContext(Dispatchers.IO) {
            database.update(profile)
        }
    }

    // delete last profile in database
    private suspend fun deleteByLastProfile() {
        withContext(Dispatchers.IO) {
            database.deleteLastProfile(true)
        }
    }

    // Variables that check if values are ok
    private var _setNameError = MutableLiveData<Boolean>()
    private var _setGpsDataError = MutableLiveData<Boolean>()
    private var _setMagDeclinationError = MutableLiveData<Boolean>()

    val setNameError: LiveData<Boolean>
        get() = _setNameError
    val setGpsDataError: LiveData<Boolean>
        get() = _setGpsDataError
    val setMagDeclinationError: LiveData<Boolean>
        get() = _setMagDeclinationError

    // Check if all values are valid, i.e., isn't a null or empty string
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

    // a public function that is called when the Edit button gets clicked
    fun onEdit(){
        viewModelScope.launch {
            if (checkValues()) {
                lastProfile.lastProfile = true
                lastProfile.profileName = profileName.value.toString()

                lastProfile.gpsData = gpsData.value.toString()
                lastProfile.declination = magDeclination.value.toString()

                update(lastProfile)
                doneOnEdit()
            }
        }
    }

    // change variable that is observed in fragment and signalizes
    // when data is edited on the database
    private fun doneOnEdit() {
        _onEdit.value = true
    }

    // delete current profile and signalize to fragment that it is done
    fun onDelete(){
        viewModelScope.launch {
            deleteByLastProfile()
            doneOnDelete()
        }
    }

    // a variable that indicates to fragment when profile gets  deleted
    private fun doneOnDelete() {
        _goToLoadProfiles.value = true
    }

    // a public function that fragment can indicate ViewModel when fragment changes
    fun doneOnChangeScreen() {
//        _goToNewProfile.value = false
        _goToLoadProfiles.value = false
        _onEdit.value = false
    }

    // public function for fragment update view model when the user get GPS lat
    fun updateGps(latitude:String){
        gpsData.value = latitude
    }
    fun updateDeclination(declination: String){
        magDeclination.value = declination
    }
}