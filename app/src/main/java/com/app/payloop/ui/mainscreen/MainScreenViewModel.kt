package com.app.payloop.ui.mainscreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.payloop.data.local.SubscriptionDao
import com.app.payloop.data.model.FrequencyUnit
import com.app.payloop.data.model.Subscription
import com.app.payloop.data.repository.SettingsRepository
import com.app.payloop.data.repository.SubscriptionRepository
import com.app.payloop.navigation.NavRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val repository: SubscriptionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {


    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state

    init {
        viewModelScope.launch {
            seedDatabase()
            repository.refreshExpiredSubscriptions()
            insertCurrencyIfNotExists()
        }
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            combine(
                repository.getAllSubscriptions(),
                settingsRepository.getCurrency(),
                settingsRepository.getReminder(),
                settingsRepository.getHourlyWage()
            ) { subscriptions, currency, reminder, hourlyWage ->

                val monthlyCost = calculateMonthlyCost(subscriptions)
                val hoursWorked = if (hourlyWage > 0) monthlyCost / hourlyWage else 0.0

                MainScreenState(
                    subscriptions = subscriptions,
                    currency = currency,
                    globalReminder = reminder,
                    hourlyWage = hourlyWage,
                    monthlyCost = monthlyCost,
                    hoursWorked = hoursWorked
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    private suspend fun seedDatabase() {
        repository.seedDummyData()
    }

    private fun insertCurrencyIfNotExists(){
        viewModelScope.launch {
            settingsRepository.insertCurrencyIfNotExists("€")
        }
    }

    private fun calculateMonthlyCost(subscriptions: List<Subscription>) : Double {
        return subscriptions
            .filter { !it.isTrial }
            .sumOf { sub ->
                val currentPrice = sub.price / 100.0

                when(sub.frequencyUnit){
                    FrequencyUnit.DAY -> currentPrice * (30.44/sub.frequencyInterval)
                    FrequencyUnit.WEEK -> currentPrice * (4.35/sub.frequencyInterval)
                    FrequencyUnit.YEAR -> currentPrice / (12.0 * sub.frequencyInterval)
                    FrequencyUnit.MONTH -> currentPrice / sub.frequencyInterval
                }
            }
    }

}