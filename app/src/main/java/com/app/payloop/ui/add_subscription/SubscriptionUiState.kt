package com.app.payloop.ui.add_subscription

import com.app.payloop.data.model.FrequencyUnit

// Prakticki kopija Subscription.kt koju koristimo za UI. Npr. kad User pise cijenu zelimo da je to
// string jer je moguce da upisuje i "." s cime bi imali problema da je tip podatka Long.
// Takoder iz principa se kao ne preporucuje mijesat Subscription.kt koji je vise vezan uz bazu,
// nego uz UI.
data class SubscriptionUiState (

    val currentStep: Int = 1, // 1. Naming 2. Frequency 3. Reminders and pricing 4. End success summary

    // 1. Naming
    val name: String = "",
    val color: Int? = null,
    val icon: String = "",

    // 2. Frequency
    val isTrial: Boolean = false,
    val nextChargeTimestamp: Long = 0,
    val frequencyUnit: FrequencyUnit = FrequencyUnit.MONTH,
    val frequencyInterval: Int = 1,
    val isManual: Boolean = false,

    // 3. Reminders and pricing
    val isReminderEnabled: Boolean = true,
    val price: String = "",
    val reminderDaysBefore: Int = 1,
    val sharedWith: Int = 0,
)