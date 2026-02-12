package com.app.payloop.ui.subscriptionview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.payloop.data.repository.SettingsRepository
import com.app.payloop.data.repository.SubscriptionRepository

class ViewSubscriptionViewModelFactory(
    private val repository: SubscriptionRepository,
    private val settingsRepository: SettingsRepository,
    private val subscriptionId : Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewSubscriptionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewSubscriptionViewModel(repository, settingsRepository, subscriptionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}