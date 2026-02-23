package com.app.payloop.data.repository

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import java.util.concurrent.TimeUnit
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.app.payloop.data.model.normalizeEpochSeconds
import com.app.payloop.data.model.Subscription
import com.app.payloop.worker.ReminderWorker
import java.time.Instant
import java.time.ZoneId

class NotificationScheduler(
    private val context: Context
) {

    fun scheduleReminder(subscription: Subscription){

        if(!subscription.isReminderEnabled) return

        val notifyTime = calculateReminderTime(subscription)

        val delay = notifyTime - System.currentTimeMillis()

        if (delay <= 0) return

        val work = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf("subscriptionId" to subscription.id)
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "reminder_${subscription.id}",
                ExistingWorkPolicy.REPLACE,
                work
            )

    }

    private fun calculateReminderTime(subscription: Subscription) : Long {

        val zone = ZoneId.systemDefault()

        val nextCharge = Instant.ofEpochSecond(normalizeEpochSeconds(subscription.nextChargeTimestamp))
            .atZone(zone)
            .toLocalDate()

        val reminderDate = nextCharge.minusDays(subscription.reminderDaysBefore.toLong())

        val reminderDateTime = reminderDate.atTime(12, 0)

        return reminderDateTime
            .atZone(zone)
            .toInstant()
            .toEpochMilli()

    }
}
