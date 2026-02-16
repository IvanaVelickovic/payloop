package com.app.payloop.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.payloop.data.local.AppDatabase
import com.app.payloop.data.local.SettingsDataStore
import com.app.payloop.data.model.Subscription
import com.app.payloop.data.repository.SettingsRepository
import com.app.payloop.data.repository.SubscriptionRepository
import com.app.payloop.util.NotificationHelper
import kotlinx.coroutines.flow.first

class ReminderWorker(
    context: Context,
    params: WorkerParameters
)  : CoroutineWorker(context, params){

    override suspend fun doWork(): Result {

        val id = inputData.getLong("subscriptionId", -1)

        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.subscriptionDao()
        val repository = SubscriptionRepository(dao, applicationContext)
        val sub = repository.getSubscriptionById(id) ?: return Result.success()

        val settingsDataStore = SettingsDataStore(applicationContext)
        val settingsRepository = SettingsRepository(settingsDataStore)
        val globalReminder = settingsRepository.getReminder().first()

        if(!globalReminder) return Result.success()

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        NotificationHelper().showReminder(applicationContext, sub, settingsRepository.getCurrency().first())

        return Result.success()
    }
}

