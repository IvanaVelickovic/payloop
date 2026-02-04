package com.app.payloop.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class Subscription(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val price: Long,    //in cents
    val isTrial: Boolean,
    val nextChargeTimestamp: Long,
    val isReminderEnabled: Boolean,
    val reminderDaysBefore: Int,
    val sharedWith: Int,
    val frequencyUnit: FrequencyUnit,
    val frequencyInterval: Int,
    val isManual: Boolean,
    val icon: String,
    val color: Int?,
)