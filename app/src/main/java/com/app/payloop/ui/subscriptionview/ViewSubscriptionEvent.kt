package com.app.payloop.ui.subscriptionview

sealed class ViewSubscriptionEvent {
    data class ToggleReminder(val enabled: Boolean) : ViewSubscriptionEvent()
    object DeleteSubscription : ViewSubscriptionEvent()
}