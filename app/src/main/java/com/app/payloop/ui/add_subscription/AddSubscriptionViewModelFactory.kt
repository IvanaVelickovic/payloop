package com.app.payloop.ui.add_subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.payloop.data.local.SubscriptionDao

class AddSubscriptionViewModelFactory(
    private val subscriptionDao: SubscriptionDao,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddSubscriptionViewModel::class.java)) {
            return AddSubscriptionViewModel(subscriptionDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
