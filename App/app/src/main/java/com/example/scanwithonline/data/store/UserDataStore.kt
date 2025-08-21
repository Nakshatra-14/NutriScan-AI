package com.example.scanwithonline.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create the DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_profile")

class UserDataStore(context: Context) {

    private val appContext = context.applicationContext

    // Define keys for each piece of data
    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AGE = intPreferencesKey("user_age")
        val USER_GENDER = stringPreferencesKey("user_gender")
        val USER_ABOUT = stringPreferencesKey("user_about")
        val USER_OTHER_INFO = stringPreferencesKey("user_other_info")
    }

    // Flow to get the user's name
    val userName: Flow<String> = appContext.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: "User" // Default value if not set
    }

    // Flow to get the user's age
    val userAge: Flow<Int> = appContext.dataStore.data.map { preferences ->
        preferences[USER_AGE] ?: 0 // Default value
    }

    // You can create flows for other properties similarly if needed
    val userGender: Flow<String> = appContext.dataStore.data.map { it[USER_GENDER] ?: "Male" }
    val userAbout: Flow<String> = appContext.dataStore.data.map { it[USER_ABOUT] ?: "" }
    val userOtherInfo: Flow<String> = appContext.dataStore.data.map { it[USER_OTHER_INFO] ?: "" }


    // Function to save the entire profile
    suspend fun saveProfile(name: String, age: Int, gender: String, about: String, otherInfo: String) {
        appContext.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
            preferences[USER_AGE] = age
            preferences[USER_GENDER] = gender
            preferences[USER_ABOUT] = about
            preferences[USER_OTHER_INFO] = otherInfo
        }
    }
}
