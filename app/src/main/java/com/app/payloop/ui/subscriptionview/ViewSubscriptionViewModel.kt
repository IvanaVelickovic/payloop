package com.app.payloop.ui.subscriptionview

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.payloop.data.local.SubscriptionDao
import com.app.payloop.data.model.FrequencyUnit
import com.app.payloop.data.model.Subscription
import com.app.payloop.data.repository.SettingsRepository
import com.app.payloop.data.repository.SubscriptionRepository
import com.app.payloop.settings.ViewSubscriptionScreen
import com.app.payloop.ui.subscriptionview.ViewSubscriptionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewSubscriptionViewModel(
    private val repository: SubscriptionRepository,
    private val settingsRepository: SettingsRepository,
    private val subscriptionId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(ViewSubscriptionState())
    val state: StateFlow<ViewSubscriptionState> = _state

    init{
        viewModelScope.launch {
            repository.observeSubscriptionById(subscriptionId)
                .collect { sub ->
                    _state.update {
                        it.copy(subscription = sub)
                    }
                }
        }
        loadCurrency()
        loadGlobalReminder()
    }

    fun onEvent(event: ViewSubscriptionEvent) {
        when (event) {
            is ViewSubscriptionEvent.ToggleReminder -> {
                toggleReminder(event.enabled)
            }
            ViewSubscriptionEvent.DeleteSubscription -> {
                deleteSubscription()
            }
        }
    }

    private fun toggleReminder(enabled : Boolean){
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
        }
    }

    private fun deleteSubscription(){
        val subscription = _state.value.subscription ?: return
        viewModelScope.launch {
                try{
                    repository.deleteSubscription(subscription)

                } catch(e: Exception) {
                    _state.value = _state.value.copy(
                        error = "Failed to delete subscription: ${e.message}"
                    )
                }

        }
    }

    private fun loadCurrency() {
        viewModelScope.launch {
            settingsRepository.getCurrency().collect { currency ->
                _state.value = _state.value.copy(currency = currency)
            }
        }
    }

    private fun loadGlobalReminder() {
        viewModelScope.launch {
            settingsRepository.getReminder().collect { reminder ->
                _state.value = _state.value.copy(globalReminder = reminder)
            }
        }
    }


}