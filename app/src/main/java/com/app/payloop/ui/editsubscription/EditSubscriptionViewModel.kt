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
                //saveSubscription()
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
                        error = null
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
        /*
        viewModelScope.launch {
            _state.value.subscription?.let { currentSubscription ->
                val updatedSubscription = currentSubscription.copy(
                    isReminderEnabled = enabled
                )

                try{
                    repository.updateSubscription(updatedSubscription)

                    _state.value = _state.value.copy(
                        subscription = updatedSubscription
                    )
                } catch(e: Exception) {
                    _state.value = _state.value.copy(
                        error = "Failed to update reminder: ${e.message}"
                    )
                }

            }
        } */
    }


}