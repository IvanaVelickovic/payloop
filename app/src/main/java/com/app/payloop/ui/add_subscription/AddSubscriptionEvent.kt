package com.app.payloop.ui.add_subscription

sealed interface AddSubscriptionEvent {
    data object OnNextStep : AddSubscriptionEvent
    data object OnPreviousStep : AddSubscriptionEvent

    data class NameChanged(val value: String) : AddSubscriptionEvent
    data class EmojiChanged(val value: String) : AddSubscriptionEvent
    data class TrialChanged(val value: Boolean) : AddSubscriptionEvent

    data class FrequencyChanged(val value: BillingFrequency) : AddSubscriptionEvent
    data class FrequencyIntervalChanged(val value: String) : AddSubscriptionEvent
    data class ManualPaymentChanged(val value: Boolean) : AddSubscriptionEvent
    data class NextChargeChanged(val value: String) : AddSubscriptionEvent
    data class SharedSubscriptionChanged(val value: Boolean) : AddSubscriptionEvent
    data class SharedWithChanged(val value: String) : AddSubscriptionEvent

    data class ReminderEnabledChanged(val value: Boolean) : AddSubscriptionEvent
    data class ReminderDaysChanged(val value: String) : AddSubscriptionEvent
    data class PriceChanged(val value: String) : AddSubscriptionEvent

    data class TrialEndDateChanged(val value: String) : AddSubscriptionEvent
    data class TrialReminderEnabledChanged(val value: Boolean) : AddSubscriptionEvent
    data class TrialReminderDaysChanged(val value: String) : AddSubscriptionEvent
    data class PriceAfterTrialChanged(val value: String) : AddSubscriptionEvent
}
