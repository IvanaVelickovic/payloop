package com.app.payloop.ui.mainscreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.payloop.data.local.SubscriptionDao
import com.app.payloop.data.model.FrequencyUnit
import com.app.payloop.data.model.Subscription
import com.app.payloop.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val repository: SubscriptionRepository
) : ViewModel() {



    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state

    init {
        viewModelScope.launch {
            seedDatabase()
            loadSubscriptions()
        }
    }

    private suspend fun seedDatabase() {
        val existing = repository.getAllSubscriptions()
        //if (existing.isEmpty()) {
            //for(dummy in dummySubscriptions){
        repository.seedDummyData()
            //}
        //}
    }

    private fun loadSubscriptions() {
        viewModelScope.launch {
            repository.getAllSubscriptions().collect { subs ->
                _state.value = MainScreenState(subscriptions = subs)
            }
        }
    }

}