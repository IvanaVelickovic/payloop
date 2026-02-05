package com.app.payloop.ui.editsubscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.payloop.data.repository.SubscriptionRepository
import com.app.payloop.ui.subscriptionview.ViewSubscriptionViewModel

class EditSubscriptionViewModelFactory(
    private val repository: SubscriptionRepository,
    private val subscriptionId : Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditSubscriptionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditSubscriptionViewModel(repository, subscriptionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}