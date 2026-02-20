package com.app.payloop.ui.add_subscription

enum class BillingFrequency {
    MONTHLY,
    YEARLY,
    CUSTOM,
}

data class SubscriptionUiState(
    val currentStep: Int = 1,
    val name: String = "",
    val emoji: String = "",
    val isTrial: Boolean = false,
    val frequency: BillingFrequency = BillingFrequency.MONTHLY,
    val customMonths: String = "",
    val isManualPayment: Boolean = false,
    val nextCharge: String = "",
    val reminderEnabled: Boolean = false,
    val reminderDays: String = "3",
    val price: String = "",
    val trialEndDate: String = "",
    val trialReminderEnabled: Boolean = false,
    val trialReminderDays: String = "3",
    val priceAfterTrial: String = "",
    val isSaving: Boolean = false,
    val saveError: String? = null,
) {
    val totalSteps: Int
        get() = if (isTrial) 3 else 4
}
