package com.app.payloop.ui.add_subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.payloop.data.model.FrequencyUnit
import com.app.payloop.data.model.Subscription
import com.app.payloop.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeParseException

class AddSubscriptionViewModel(
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: AddSubscriptionEvent) {
        when (event) {
            AddSubscriptionEvent.OnNextStep -> {
                _uiState.update { state ->
                    val stepError = validateCurrentStep(state)
                    if (stepError == null) {
                        state.copy(
                            currentStep = (state.currentStep + 1).coerceAtMost(state.totalSteps),
                            saveError = null,
                        )
                    } else {
                        state.copy(saveError = stepError)
                    }
                }
            }
            AddSubscriptionEvent.OnPreviousStep -> {
                _uiState.update { state ->
                    state.copy(
                        currentStep = (state.currentStep - 1).coerceAtLeast(1),
                        saveError = null,
                    )
                }
            }
            is AddSubscriptionEvent.NameChanged -> _uiState.update { it.copy(name = event.value, saveError = null) }
            is AddSubscriptionEvent.EmojiChanged -> _uiState.update { it.copy(emoji = event.value) }
            is AddSubscriptionEvent.TrialChanged -> {
                _uiState.update { state ->
                    state.copy(
                        isTrial = event.value,
                        currentStep = if (event.value && state.currentStep > 3) 3 else state.currentStep,
                    )
                }
            }
            is AddSubscriptionEvent.FrequencyChanged -> _uiState.update { it.copy(frequency = event.value) }
            is AddSubscriptionEvent.FrequencyIntervalChanged -> {
                _uiState.update { it.copy(frequencyInterval = event.value) }
            }
            is AddSubscriptionEvent.ManualPaymentChanged -> _uiState.update { it.copy(isManualPayment = event.value) }
            is AddSubscriptionEvent.NextChargeChanged -> _uiState.update { it.copy(nextCharge = event.value) }
            is AddSubscriptionEvent.SharedSubscriptionChanged -> {
                _uiState.update {
                    it.copy(
                        isSharedSubscription = event.value,
                        sharedWith = if (event.value) {
                            it.sharedWith.takeIf { value -> value.toIntOrNull()?.let { num -> num > 0 } == true } ?: "1"
                        } else {
                            "1"
                        },
                    )
                }
            }
            is AddSubscriptionEvent.SharedWithChanged -> _uiState.update { it.copy(sharedWith = event.value) }
            is AddSubscriptionEvent.ReminderEnabledChanged -> _uiState.update { it.copy(reminderEnabled = event.value) }
            is AddSubscriptionEvent.ReminderDaysChanged -> _uiState.update { it.copy(reminderDays = event.value) }
            is AddSubscriptionEvent.PriceChanged -> _uiState.update { it.copy(price = event.value) }
            is AddSubscriptionEvent.TrialEndDateChanged -> _uiState.update { it.copy(trialEndDate = event.value) }
            is AddSubscriptionEvent.TrialReminderEnabledChanged -> {
                _uiState.update { it.copy(trialReminderEnabled = event.value) }
            }
            is AddSubscriptionEvent.TrialReminderDaysChanged -> {
                _uiState.update { it.copy(trialReminderDays = event.value) }
            }
            is AddSubscriptionEvent.PriceAfterTrialChanged -> {
                _uiState.update { it.copy(priceAfterTrial = event.value) }
            }
        }
    }

    fun submit(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isSaving) return
        if (state.name.isBlank()) {
            _uiState.update { it.copy(saveError = "Subscription name is required.") }
            return
        }

        val nextCharge = if (state.isTrial) state.trialEndDate else state.nextCharge
        val nextChargeTimestamp = parseDateToEpoch(nextCharge)
        if (nextChargeTimestamp == null) {
            _uiState.update { it.copy(saveError = "Please enter a valid date in YYYY-MM-DD format.") }
            return
        }

        _uiState.update { it.copy(isSaving = true, saveError = null) }

        viewModelScope.launch {
            runCatching {
                subscriptionRepository.insertSubscription(
                    Subscription(
                        name = state.name.trim(),
                        price = toPriceInCents(
                            if (state.isTrial) state.priceAfterTrial else state.price,
                        ),
                        isTrial = state.isTrial,
                        nextChargeTimestamp = nextChargeTimestamp,
                        isReminderEnabled = if (state.isTrial) {
                            state.trialReminderEnabled
                        } else {
                            state.reminderEnabled
                        },
                        reminderDaysBefore = toPositiveIntOrDefault(
                            if (state.isTrial) state.trialReminderDays else state.reminderDays,
                            defaultValue = 3,
                        ),
                        sharedWith = if (state.isSharedSubscription) {
                            toPositiveIntOrDefault(state.sharedWith, 1)
                        } else {
                            0
                        },
                        frequencyUnit = toFrequencyUnit(state.frequency),
                        frequencyInterval = toPositiveIntOrDefault(state.frequencyInterval, 1),
                        isManual = if (state.isTrial) false else state.isManualPayment,
                        icon = state.emoji,
                        color = null,
                    ),
                )
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveError = "Couldn't save subscription. Please try again.",
                    )
                }
            }
        }
    }

    private fun toPriceInCents(value: String): Long {
        val amount = value.trim().replace(",", ".").toDoubleOrNull() ?: 0.0
        return (amount * 100).toLong().coerceAtLeast(0L)
    }

    private fun parseDateToEpoch(value: String): Long? {
        return try {
            LocalDate.parse(value.trim())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } catch (_: DateTimeParseException) {
            null
        }
    }

    private fun toFrequencyUnit(value: BillingFrequency): FrequencyUnit {
        return when (value) {
            BillingFrequency.DAILY -> FrequencyUnit.DAY
            BillingFrequency.WEEKLY -> FrequencyUnit.WEEK
            BillingFrequency.MONTHLY -> FrequencyUnit.MONTH
            BillingFrequency.YEARLY -> FrequencyUnit.YEAR
        }
    }

    private fun toPositiveIntOrDefault(value: String, defaultValue: Int): Int {
        return value.trim().toIntOrNull()?.takeIf { it > 0 } ?: defaultValue
    }

    private fun isPositiveInt(value: String): Boolean {
        return value.trim().toIntOrNull()?.let { it > 0 } == true
    }

    private fun validateCurrentStep(state: SubscriptionUiState): String? {
        if (state.currentStep == 1) {
            if (state.name.isBlank()) return "Subscription name is required."
            return null
        }

        if (state.isTrial && state.currentStep == 2) {
            if (parseDateToEpoch(state.trialEndDate) == null) return "Please select a valid trial end date."
            if (state.trialReminderEnabled && !isPositiveInt(state.trialReminderDays)) {
                return "Trial reminder days must be greater than 0."
            }
            if (state.isSharedSubscription && !isPositiveInt(state.sharedWith)) {
                return "Shared with must be greater than 0."
            }
            return null
        }

        if (!state.isTrial && state.currentStep == 2) {
            if (!isPositiveInt(state.frequencyInterval)) return "Frequency interval must be greater than 0."
            if (parseDateToEpoch(state.nextCharge) == null) return "Please select a valid next charge date."
            return null
        }

        if (!state.isTrial && state.currentStep == 3) {
            if (state.reminderEnabled && !isPositiveInt(state.reminderDays)) {
                return "Reminder days must be greater than 0."
            }
            if (state.isSharedSubscription && !isPositiveInt(state.sharedWith)) {
                return "Shared with must be greater than 0."
            }
            return null
        }

        return null
    }
}
