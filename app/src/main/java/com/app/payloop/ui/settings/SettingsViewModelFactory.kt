package com.app.payloop.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.payloop.data.repository.SettingsRepository
import com.app.payloop.data.repository.SubscriptionRepository
import com.app.payloop.ui.editsubscription.EditSubscriptionViewModel


class SettingsViewModelFactory(
    private val repository: SettingsRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository, subscriptionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}