package com.app.payloop.ui.mainscreen

import com.app.payloop.data.model.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class MainScreenState (
    val subscriptions: List<Subscription> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedSubscriptionId: Int? = null,
    val currency: String = "€",
    val globalReminder : Boolean = true,
    val hourlyWage: Double = 0.0,
    val monthlyCost: Double = 0.0,
    val hoursWorked: Double = 0.0
)


