/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class Profile(
        @PrimaryKey(autoGenerate = true)
        var profileID: Long = 0L,

        @ColumnInfo(name = "profile_name")
        val profileName: String = "",

        @ColumnInfo(name = "gpsData")
        var gpsData: Double = 0.0,

        @ColumnInfo(name = "declination")
        var declination: Double = 0.0,

        @ColumnInfo(name = "bluetooth_mac")
        var btAdress: String = ""
)
