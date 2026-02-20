package com.app.payloop.ui.add_subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.payloop.data.repository.SubscriptionRepository

class AddSubscriptionViewModelFactory(
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddSubscriptionViewModel::class.java)) {
            return AddSubscriptionViewModel(subscriptionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
