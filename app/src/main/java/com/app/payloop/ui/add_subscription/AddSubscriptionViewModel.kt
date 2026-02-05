package com.app.payloop.ui.add_subscription

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddSubscriptionViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: AddSubscriptionEvent){
        when(event){
            is AddSubscriptionEvent.OnNextStep -> {
                // Mogao umjesto if-ova koristiti .coerceAtMost(4)
                _uiState.update { it.copy(currentStep = if (it.currentStep < 4) it.currentStep + 1 else it.currentStep) }
            }
            is AddSubscriptionEvent.OnPreviousStep -> {
                // i .coerceAtLeast(1)
                _uiState.update { it.copy(currentStep = if (it.currentStep > 1) it.currentStep - 1 else it.currentStep) }
            }

            // 1. Naming
            is AddSubscriptionEvent.EnteredName -> {
                _uiState.update { it.copy(name = event.value) }
            }
            is AddSubscriptionEvent.EnteredColor -> {
                _uiState.update { it.copy(color = event.value) }
            }
            is AddSubscriptionEvent.EnteredIcon -> {
                _uiState.update { it.copy(icon = event.value) }
            }

            // 2. Frequency
            is AddSubscriptionEvent.EnteredIsTrial -> {
                _uiState.update { it.copy(isTrial = event.value) }
            }
            is AddSubscriptionEvent.EnteredNextChargeTimeStamp -> {
                _uiState.update { it.copy(nextChargeTimestamp = event.value) }
            }
            is AddSubscriptionEvent.EnteredFrequencyUnit -> {
                _uiState.update { it.copy(frequencyUnit = event.value) }
            }
            is AddSubscriptionEvent.EnteredFrequencyInterval -> {
                _uiState.update { it.copy(frequencyInterval = event.value) }
            }
            is AddSubscriptionEvent.EnteredIsManual -> {
                _uiState.update { it.copy(isManual = event.value) }
            }

            // 3. Reminders and pricing
            is AddSubscriptionEvent.EnteredIsReminderEnabled -> {
                _uiState.update { it.copy(isReminderEnabled = event.value) }
            }
            is AddSubscriptionEvent.EnteredPrice -> {
                _uiState.update { it.copy(price = event.value) }
            }
            is AddSubscriptionEvent.EnteredReminderDaysBefore -> {
                _uiState.update { it.copy(reminderDaysBefore = event.value) }
            }
            is AddSubscriptionEvent.EnteredSharedWith -> {
                _uiState.update { it.copy(sharedWith = event.value) }
            }
        }

    }
}