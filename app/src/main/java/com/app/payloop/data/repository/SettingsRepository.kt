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
    fun getReceiverName(): Flow<String> {
        return dataStore.getReceiverName()
    }
    fun getReceiverIban(): Flow<String> {
        return dataStore.getReceiverIban()
    }
    fun getReceiverBic(): Flow<String> {
        return dataStore.getReceiverBic()
    }
    fun getReceiverPaymentNote(): Flow<String> {
        return dataStore.getReceiverPaymentNote()
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
    suspend fun insertReceiverName(value: String) {
        dataStore.saveReceiverName(value)
    }
    suspend fun insertReceiverIban(value: String) {
        dataStore.saveReceiverIban(value)
    }
    suspend fun insertReceiverBic(value: String) {
        dataStore.saveReceiverBic(value)
    }
    suspend fun insertReceiverPaymentNote(value: String) {
        dataStore.saveReceiverPaymentNote(value)
    }

    suspend fun insertCurrencyIfNotExists(value: String) {
        dataStore.saveCurrencyIfNotExists(value)
    }
}
