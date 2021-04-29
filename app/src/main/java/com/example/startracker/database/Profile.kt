package com.example.startracker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class Profile(
        @PrimaryKey(autoGenerate = true)
        var profileId: Long = 0L,

        @ColumnInfo(name = "profile_name")
        var profileName: String = "",

        @ColumnInfo(name = "last_profile")
        var lastProfile: Boolean = false,

        @ColumnInfo(name = "gps_data")
        var gpsData: String = "",

        @ColumnInfo(name = "declination")
        var declination: String = "",

        @ColumnInfo(name = "bluetooth_mac")
        var btAddress: String = ""

//        @ColumnInfo(name = "start_time_milli")
//        val startTimeMilli: Long = System.currentTimeMillis()
)
