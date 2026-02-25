package com.app.payloop.ui.subscriptionview

import com.app.payloop.data.model.Subscription

data class ViewSubscriptionState (
    val subscription: Subscription? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currency : String = "€",
    val globalReminder : Boolean = true,
    val receiverName: String = "",
    val receiverIban: String = "",
    val receiverBic: String = "",
    val receiverPaymentNote: String = "",
)
