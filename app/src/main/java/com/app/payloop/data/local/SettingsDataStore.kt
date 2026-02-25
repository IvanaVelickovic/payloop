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
        val receiverName = stringPreferencesKey("receiverName")
        val receiverIban = stringPreferencesKey("receiverIban")
        val receiverBic = stringPreferencesKey("receiverBic")
        val receiverPaymentNote = stringPreferencesKey("receiverPaymentNote")
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

    suspend fun saveReceiverName(value: String) {
        context.dataStore.edit { data ->
            data[SettingsKeys.receiverName] = value
        }
    }

    fun getReceiverName(): Flow<String> {
        return context.dataStore.data.map { data ->
            data[SettingsKeys.receiverName] ?: ""
        }
    }

    suspend fun saveReceiverIban(value: String) {
        context.dataStore.edit { data ->
            data[SettingsKeys.receiverIban] = value
        }
    }

    fun getReceiverIban(): Flow<String> {
        return context.dataStore.data.map { data ->
            data[SettingsKeys.receiverIban] ?: ""
        }
    }

    suspend fun saveReceiverBic(value: String) {
        context.dataStore.edit { data ->
            data[SettingsKeys.receiverBic] = value
        }
    }

    fun getReceiverBic(): Flow<String> {
        return context.dataStore.data.map { data ->
            data[SettingsKeys.receiverBic] ?: ""
        }
    }

    suspend fun saveReceiverPaymentNote(value: String) {
        context.dataStore.edit { data ->
            data[SettingsKeys.receiverPaymentNote] = value
        }
    }

    fun getReceiverPaymentNote(): Flow<String> {
        return context.dataStore.data.map { data ->
            data[SettingsKeys.receiverPaymentNote] ?: ""
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
