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
//
//import androidx.lifecycle.LiveData
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.Query
//import androidx.room.Update
//
///**
// * Defines methods for using the Profiles class with Room.
// */
//@Dao
//interface ProfileDatabaseDao {
//
//    @Insert
//    suspend fun insert(night: Profile)
//
//    /**
//     * When updating a row with a value already set in a column,
//     * replaces the old value with the new one.
//     *
//     * @param night new value to write
//     */
//    @Update
//    suspend fun update(night: Profile)
//
//    /**
//     * Selects and returns the row that matches the supplied start time, which is our key.
//     *
//     * @param key startTimeMilli to match
//
//    @Query("SELECT * from profile_table WHERE profileID = :key")
//    suspend fun get(key: Long): Profiles?
//     */
//
//    /**
//     * Deletes all values from the table.
//     *
//     * This does not delete the table, only its contents.
//     */
//    @Query("DELETE FROM profile_table")
//    suspend fun clear()
//
//    /**
//     * Selects and returns all rows in the table,
//     *
//     * sorted by start time in descending order.
//     */
//    @Query("SELECT * FROM profile_table ORDER BY profileID DESC")
//    fun getAllProfiles(): LiveData<List<Profile>>
//
//    /**
//     * Selects and returns the latest night.
//     */
//    @Query("SELECT * FROM profile_table ORDER BY profileID DESC LIMIT 1")
//    suspend fun getLastProfile(): Profile?
//
//    /**
//     * Selects and returns the night with given nightId.
//     */
//    @Query("SELECT * from profile_table WHERE profileID = :key")
//    fun getProfileWithId(key: Long): LiveData<Profile>
//}
//
