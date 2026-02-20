package com.app.payloop.data.repository

import com.app.payloop.data.local.SettingsDataStore
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val dataStore: SettingsDataStore
) {

    fun getHourlyWage(): Flow<Double> {
        return dataStore.getHourlyWage()
    }
    fun getCurrency(): Flow<String> {
        return dataStore.getCurrency()
    }
    fun getReminder(): Flow<Boolean> {
        return dataStore.getReminder()
    }

    suspend fun insertHourlyWage(value : Double) {
        dataStore.saveHourlyWage(value)
    }

    suspend fun insertCurrency(value : String) {
        dataStore.saveCurrency(value)
    }

    suspend fun insertReminder(value : Boolean) {
        dataStore.saveReminder(value)
    }

    suspend fun insertCurrencyIfNotExists(value: String) {
        dataStore.saveCurrencyIfNotExists(value)
    }
}