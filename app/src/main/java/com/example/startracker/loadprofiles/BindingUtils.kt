package com.example.startracker.loadprofiles

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.startracker.convertLongToDateString
import com.example.startracker.database.Profile

@BindingAdapter("ProfileName")
fun TextView.setProfileNameString(item: Profile?) {
    item?.let {
        text = it.profileName
    }
}

@BindingAdapter("ProfileData")
fun TextView.setProfileDataString(item: Profile?) {
    item?.let {
        text = convertLongToDateString(it.startTimeMilli)
    }
}