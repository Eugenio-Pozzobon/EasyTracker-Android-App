package com.epp.easytracker.loadprofiles

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.epp.easytracker.convertLongToDateString
import com.epp.easytracker.database.Profile

// use bind adapters to Show List of profiles in database.
// Show Profile Name and Profile Save Data

@BindingAdapter("ProfileName")
fun TextView.setProfileNameString(item: Profile?) {
    item?.let {
        text = it.profileName
    }
}

@BindingAdapter("ProfileData")
fun TextView.setProfileDataString(item: Profile?) {
    item?.let {
        text = convertLongToDateString(it.startTimeMilli, "'Criado no dia 'dd'/'mm'/'yyyy")
    }
}