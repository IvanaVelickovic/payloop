package com.app.payloop.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.payloop.data.repository.SettingsRepository
import com.app.payloop.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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

    val receiverName: StateFlow<String> = repository.getReceiverName()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val receiverIban: StateFlow<String> = repository.getReceiverIban()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val receiverBic: StateFlow<String> = repository.getReceiverBic()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val receiverPaymentNote: StateFlow<String> = repository.getReceiverPaymentNote()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
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

    fun updateReceiverName(value: String) {
        viewModelScope.launch {
            repository.insertReceiverName(value.trim())
        }
    }

    fun updateReceiverIban(value: String) {
        viewModelScope.launch {
            val normalized = value.uppercase().replace(" ", "")
            repository.insertReceiverIban(normalized)
        }
    }

    fun updateReceiverBic(value: String) {
        viewModelScope.launch {
            val normalized = value.uppercase().replace(" ", "")
            repository.insertReceiverBic(normalized)
        }
    }

    fun updateReceiverPaymentNote(value: String) {
        viewModelScope.launch {
            repository.insertReceiverPaymentNote(value.trim())
        }
    }

    fun deleteAll(){
        viewModelScope.launch {
            subscriptionRepository.deleteAllSubscriptions()
        }
    }


}
