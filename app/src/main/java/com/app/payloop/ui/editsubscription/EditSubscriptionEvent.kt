package com.app.payloop.ui.editsubscription

import com.app.payloop.data.model.FrequencyUnit

sealed class EditSubscriptionEvent {
    data class NameChanged(val name: String) : EditSubscriptionEvent()

    data class PeopleCountChanged(val peopleCount: String) : EditSubscriptionEvent()

    data class PriceChanged(val price: String) : EditSubscriptionEvent()

    data class FrequencyChanged(val frequency: FrequencyUnit) : EditSubscriptionEvent()

    data class CustomFrequencyToggle(val enabled: Boolean) : EditSubscriptionEvent()

    data class CustomFrequencyValueChanged(val value: String) : EditSubscriptionEvent()

    data class ReminderToggle(val enabled: Boolean) : EditSubscriptionEvent()

    data class ReminderDaysChanged(val days: String) : EditSubscriptionEvent()

    data class DateChanged(val timestamp: Long?) : EditSubscriptionEvent()

    object Save : EditSubscriptionEvent()
}