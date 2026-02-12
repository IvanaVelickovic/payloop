package com.app.payloop.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.payloop.data.repository.SettingsRepository
import com.app.payloop.data.repository.SubscriptionRepository
import com.app.payloop.ui.editsubscription.EditSubscriptionEvent
import com.app.payloop.ui.editsubscription.EditSubscriptionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    val hourlyWage: StateFlow<Double> = repository.getHourlyWage()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val currency: StateFlow<String> = repository.getCurrency()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "€"
        )

    val reminderOn: StateFlow<Boolean> = repository.getReminder()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun updateHourlyWage(value: Double){
        viewModelScope.launch {
            repository.insertHourlyWage(value)
        }
    }

    fun updateCurrency(value: String){
        viewModelScope.launch {
            repository.insertCurrency(value)
        }
    }

    fun updateReminder(value: Boolean){
        viewModelScope.launch {
            repository.insertReminder(value)
        }
    }

    fun deleteAll(){
        viewModelScope.launch {
            subscriptionRepository.deleteAllSubscriptions()
        }
    }


}