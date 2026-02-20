package com.app.payloop.ui.add_subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.payloop.data.local.SubscriptionDao
import com.app.payloop.data.model.FrequencyUnit
import com.app.payloop.data.model.Subscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeParseException

class AddSubscriptionViewModel(
    private val subscriptionDao: SubscriptionDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: AddSubscriptionEvent) {
        when (event) {
            AddSubscriptionEvent.OnNextStep -> {
                _uiState.update { state ->
                    state.copy(currentStep = (state.currentStep + 1).coerceAtMost(state.totalSteps))
                }
            }
            AddSubscriptionEvent.OnPreviousStep -> {
                _uiState.update { state ->
                    state.copy(currentStep = (state.currentStep - 1).coerceAtLeast(1))
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
            is AddSubscriptionEvent.CustomMonthsChanged -> _uiState.update { it.copy(customMonths = event.value) }
            is AddSubscriptionEvent.ManualPaymentChanged -> _uiState.update { it.copy(isManualPayment = event.value) }
            is AddSubscriptionEvent.NextChargeChanged -> _uiState.update { it.copy(nextCharge = event.value) }
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
                subscriptionDao.insertSubscription(
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
                        sharedWith = 0,
                        frequencyUnit = toFrequencyUnit(state.frequency),
                        frequencyInterval = if (state.frequency == BillingFrequency.CUSTOM) {
                            toPositiveIntOrDefault(state.customMonths, 1)
                        } else {
                            1
                        },
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
            BillingFrequency.MONTHLY -> FrequencyUnit.MONTH
            BillingFrequency.YEARLY -> FrequencyUnit.YEAR
            BillingFrequency.CUSTOM -> FrequencyUnit.MONTH
        }
    }

    private fun toPositiveIntOrDefault(value: String, defaultValue: Int): Int {
        return value.trim().toIntOrNull()?.takeIf { it > 0 } ?: defaultValue
    }
}
