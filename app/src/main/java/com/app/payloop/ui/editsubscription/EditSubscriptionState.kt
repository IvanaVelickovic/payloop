package com.app.payloop.ui.editsubscription

import com.app.payloop.data.model.FrequencyUnit
import com.app.payloop.data.model.Subscription

data class EditSubscriptionState (
    val id: Long? = null,

    val name: String = "",

    val peopleCount: String = "",

    val price: String = "",

    // Frequency
    val selectedFrequency: FrequencyUnit = FrequencyUnit.MONTH,
    val customFrequencyEnabled: Boolean = false,
    val customFrequencyValue: String = "",

    // Date
    val selectedDateTimestamp: Long? = null,

    // Reminder
    val reminderEnabled: Boolean = false,
    val reminderDays: String = "",

    // UI stanje
    val isLoading: Boolean = false,
    val error: String? = null,

    val isSaved: Boolean = false,

    val currency: String = "€",
    val globalReminder : Boolean = true,

    val isTrial : Boolean = false,
    val isManual : Boolean = false

)