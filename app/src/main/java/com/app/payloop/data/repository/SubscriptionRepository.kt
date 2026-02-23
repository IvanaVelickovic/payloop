package com.app.payloop.data.repository

import android.content.Context
import androidx.compose.runtime.collectAsState
import com.app.payloop.data.local.SubscriptionDao
import com.app.payloop.data.model.FrequencyUnit
import com.app.payloop.data.model.Subscription
import com.app.payloop.data.model.normalizeEpochSeconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class SubscriptionRepository(
    private val dao: SubscriptionDao,
    private val context: Context
) {

    fun getAllSubscriptions(): Flow<List<Subscription>> {
        return dao.getAllSubscriptions()
    }

    suspend fun getSubscriptionById(id: Long): Subscription? {
        return dao.getSubscriptionById(id)
    }

    suspend fun insertSubscription(subscription: Subscription) {
        dao.insertSubscription(subscription)
        NotificationScheduler(context).scheduleReminder(subscription)
    }

    suspend fun updateSubscription(subscription: Subscription) {
        dao.updateSubscription(subscription)
        NotificationScheduler(context).scheduleReminder(subscription)
    }

    suspend fun deleteSubscription(subscription: Subscription) {
        dao.deleteSubscription(subscription)
    }

    fun observeSubscriptionById(id: Long): Flow<Subscription> {
        return dao.observeSubscriptionById(id)
    }

    suspend fun deleteAllSubscriptions(){
        dao.deleteAllSubscriptions()
    }

    suspend fun refreshExpiredSubscriptions() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)

        val subscriptions = dao.getAllSubscriptions().first()

        val expiredSubscriptions = subscriptions.filter { sub ->
            val nextChargeDate = Instant.ofEpochSecond(normalizeEpochSeconds(sub.nextChargeTimestamp))
                .atZone(zone)
                .toLocalDate()
            nextChargeDate <= today
        }

        if (expiredSubscriptions.isNotEmpty()) {
            val updatedSubscriptions = expiredSubscriptions.map { sub ->
                if(sub.isTrial){
                    sub.copy(
                        isTrial = false,
                        nextChargeTimestamp = calculateNextChargeDate(sub)
                    )
                } else {
                    sub.copy(nextChargeTimestamp = calculateNextChargeDate(sub))
                }
            }
            dao.updateSubscriptions(updatedSubscriptions)
        }
    }

    fun calculateNextChargeDate(sub : Subscription) : Long{
        val zone = ZoneId.systemDefault()
        var nextChargeDate = Instant.ofEpochSecond(normalizeEpochSeconds(sub.nextChargeTimestamp))
            .atZone(zone)
            .toLocalDate()
        val today = LocalDate.now(zone)

        while(today >= nextChargeDate){
            nextChargeDate = when(sub.frequencyUnit){
                FrequencyUnit.DAY -> nextChargeDate.plusDays(sub.frequencyInterval.toLong())
                FrequencyUnit.WEEK -> nextChargeDate.plusDays(sub.frequencyInterval.toLong() * 7)
                FrequencyUnit.MONTH -> nextChargeDate.plusMonths(sub.frequencyInterval.toLong())
                FrequencyUnit.YEAR -> nextChargeDate.plusYears(sub.frequencyInterval.toLong())
            }
        }

        return nextChargeDate.atStartOfDay(zone).toEpochSecond()
    }

    /* suspend fun seedDummyData() {
        val dummySubscriptions = listOf(
            Subscription(
                name = "Netflix",
                price = 1299,
                isTrial = false,
                nextChargeTimestamp = 1772841600,
                isReminderEnabled = true,
                reminderDaysBefore = 3,
                sharedWith = 1,
                frequencyUnit = FrequencyUnit.MONTH,
                frequencyInterval = 1,
                isManual = true,
                icon = "netflix_icon",
                color = 0xFFE50914.toInt()
            ),
            Subscription(
                name = "Spotify",
                price = 999,
                isTrial = true,
                nextChargeTimestamp = 1771622400,
                isReminderEnabled = false,
                reminderDaysBefore = 0,
                sharedWith = 2,
                frequencyUnit = FrequencyUnit.MONTH,
                frequencyInterval = 3,
                isManual = false,
                icon = "spotify_icon",
                color = 0xFF1DB954.toInt()
            ),
            Subscription(
                name = "Disney+",
                price = 899,
                isTrial = true,
                nextChargeTimestamp = 1778889600,
                isReminderEnabled = true,
                reminderDaysBefore = 2,
                sharedWith = 0,
                frequencyUnit = FrequencyUnit.YEAR,
                frequencyInterval = 1,
                isManual = true,
                icon = "disney_icon",
                color = 0xFF113CCF.toInt()
            ),
            Subscription(
                name = "Amazon prime",
                price = 1199,
                isTrial = false,
                nextChargeTimestamp = 1771363200,
                isReminderEnabled = false,
                reminderDaysBefore = 2,
                sharedWith = 0,
                frequencyUnit = FrequencyUnit.MONTH,
                frequencyInterval = 1,
                isManual = false,
                icon = "amazon_icon",
                color = 0xFF00A8E1.toInt()
            )
        )

        val existing = dao.getAllSubscriptions().first()
        if(existing.isEmpty()){
            dummySubscriptions.forEach { insertSubscription(it) }
        }

    } */
}
