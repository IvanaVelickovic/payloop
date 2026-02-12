package com.app.payloop.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_data")

class SettingsDataStore(private val context: Context) {

    private object SettingsKeys {
        val hourlyWage = doublePreferencesKey("hourlyWage")
        val currency = stringPreferencesKey("currency")
        val reminderOn = booleanPreferencesKey("reminderOn")
    }

    suspend fun saveHourlyWage(value: Double){
        context.dataStore.edit { data ->
            data[SettingsKeys.hourlyWage] = value
        }
    }
    fun getHourlyWage(): Flow<Double> {
        return context.dataStore.data.map { data ->
            data[SettingsKeys.hourlyWage] ?: 0.0
        }
    }

    suspend fun saveCurrency(value: String){
        context.dataStore.edit { data ->
            data[SettingsKeys.currency] = value
        }
    }

    fun getCurrency(): Flow<String> {
        return context.dataStore.data.map { data ->
            data[SettingsKeys.currency] ?: ""
        }
    }

    suspend fun saveReminder(value: Boolean){
        context.dataStore.edit { data ->
            data[SettingsKeys.reminderOn] = value
        }
    }

    fun getReminder(): Flow<Boolean> {
        return context.dataStore.data.map { data ->
            data[SettingsKeys.reminderOn] ?: true
        }
    }

    suspend fun saveCurrencyIfNotExists(default: String) {
        context.dataStore.edit { data ->
            if (!data.contains(SettingsKeys.currency)) {
                data[SettingsKeys.currency] = default
            }
        }
    }


}