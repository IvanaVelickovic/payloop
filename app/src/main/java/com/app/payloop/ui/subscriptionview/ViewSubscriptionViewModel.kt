package com.app.payloop.ui.subscriptionview

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.payloop.data.local.SubscriptionDao
import com.app.payloop.data.model.FrequencyUnit
import com.app.payloop.data.model.Subscription
import com.app.payloop.data.repository.SubscriptionRepository
import com.app.payloop.settings.ViewSubscriptionScreen
import com.app.payloop.ui.subscriptionview.ViewSubscriptionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewSubscriptionViewModel(
    private val repository: SubscriptionRepository,
    private val subscriptionId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(ViewSubscriptionState())
    val state: StateFlow<ViewSubscriptionState> = _state

    init{
        loadSubscription(subscriptionId)
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

    private fun loadSubscription(id : Long) {
        viewModelScope.launch {
            try {
                val subscription = repository.getSubscriptionById(id)

                _state.value = ViewSubscriptionState(
                    subscription = subscription,
                    isLoading = false,
                    error = if (subscription == null) "Subscription not found" else null
                )
            } catch (e: Exception) {
                _state.value = ViewSubscriptionState(
                    subscription = null,
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
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


}