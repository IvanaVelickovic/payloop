package com.app.payloop.ui.editsubscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.payloop.data.repository.SubscriptionRepository
import com.app.payloop.ui.subscriptionview.ViewSubscriptionEvent
import com.app.payloop.ui.subscriptionview.ViewSubscriptionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditSubscriptionViewModel(
    private val repository: SubscriptionRepository,
    private val subscriptionId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(EditSubscriptionState())
    val state: StateFlow<EditSubscriptionState> = _state

    init{
        loadSubscription(subscriptionId)
    }

    fun onEvent(event: EditSubscriptionEvent) {
        when (event) {
            is EditSubscriptionEvent.NameChanged -> {
                _state.update { it.copy(name = event.name) }
            }

            is EditSubscriptionEvent.PeopleCountChanged -> {
                _state.update { it.copy(peopleCount = event.peopleCount) }
            }

            is EditSubscriptionEvent.PriceChanged -> {
                _state.update { it.copy(price = event.price) }
            }

            is EditSubscriptionEvent.FrequencyChanged -> {
                _state.update { it.copy(selectedFrequency = event.frequency) }
            }

            is EditSubscriptionEvent.CustomFrequencyToggle -> {
                _state.update {
                    it.copy(customFrequencyEnabled = event.enabled)
                }
            }

            is EditSubscriptionEvent.CustomFrequencyValueChanged -> {
                _state.update {
                    it.copy(customFrequencyValue = event.value)
                }
            }

            is EditSubscriptionEvent.ReminderToggle -> {
                _state.update {
                    it.copy(reminderEnabled = event.enabled)
                }
            }

            is EditSubscriptionEvent.ReminderDaysChanged -> {
                _state.update {
                    it.copy(reminderDays = event.days)
                }
            }

            is EditSubscriptionEvent.DateChanged -> {
                _state.update {
                    it.copy(selectedDateTimestamp = event.timestamp)
                }
            }

            EditSubscriptionEvent.Save -> {
                saveEditChanges()
            }
        }
    }

    private fun loadSubscription(id: Long) {
        viewModelScope.launch {

            _state.update { it.copy(isLoading = true) }

            try {
                val sub = repository.getSubscriptionById(id)

                _state.update {
                    it.copy(
                        id = sub?.id,
                        name = sub!!.name,
                        price = (sub.price / 100f).toString(),
                        peopleCount = sub.sharedWith.toString(),
                        selectedFrequency = sub.frequencyUnit,
                        customFrequencyValue = sub.frequencyInterval.toString(),
                        customFrequencyEnabled = if (sub.frequencyInterval != 1) true else false,
                        reminderEnabled = sub.isReminderEnabled,
                        reminderDays = sub.reminderDaysBefore?.toString() ?: "",
                        selectedDateTimestamp = sub.nextChargeTimestamp,
                        isLoading = false,
                        error = null,
                        isSaved = false
                    )
                }

            } catch (e: Exception) {

                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }


    private fun saveEditChanges(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val currentState = _state.value

                if (currentState.name.isBlank()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Subscription name cannot be empty"
                        )
                    }
                    return@launch
                }

                val priceInCents = try {
                    (currentState.price.toFloat() * 100).toLong()
                } catch (e: NumberFormatException) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Invalid price format"
                        )
                    }
                    return@launch
                }

                val originalSub = repository.getSubscriptionById(subscriptionId)
                    ?: throw Exception("Subscription not found")  //originalni subscription

                val updatedSub = originalSub.copy(
                    name = currentState.name,
                    price = priceInCents,
                    sharedWith = currentState.peopleCount.toIntOrNull() ?: 0,
                    frequencyUnit = currentState.selectedFrequency,
                    frequencyInterval = currentState.customFrequencyValue.toIntOrNull() ?: 1,
                    isReminderEnabled = currentState.reminderEnabled,
                    reminderDaysBefore = currentState.reminderDays.toIntOrNull() ?: 1,
                    nextChargeTimestamp = currentState.selectedDateTimestamp ?: originalSub.nextChargeTimestamp
                )

                repository.updateSubscription(updatedSub)

                _state.update{
                    it.copy(
                        isLoading = false,
                        isSaved = true,
                        error = null
                    )
                }
            } catch(e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to save: ${e.message}"
                    )
                }
            }
        }
    }


}