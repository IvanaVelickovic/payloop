package com.app.payloop.util

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.app.payloop.MainActivity
import com.app.payloop.MyApplication
import com.app.payloop.R
import com.app.payloop.data.model.Subscription



class NotificationHelper {

    fun showReminder(context: Context, subscription: Subscription, currency : String) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply{
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("subscriptionId", subscription.id)
                putExtra("openViewScreen", true)
            }

            val pendingIntent = TaskStackBuilder.create(context).run{
                addNextIntentWithParentStack(intent)
                getPendingIntent(
                    subscription.id.toInt(),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }



            val contentText = "Charging in ${subscription.reminderDaysBefore} days." +
                    if(subscription.isManual) " This subscription requires manual payment."
                    else if(subscription.isTrial) " This was a free trial" + if(subscription.price != 0L) " - price after is ${subscription.price/100.0}${currency}" else ""
                    else ""

            val notification = NotificationCompat.Builder(context, "subscription_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("${subscription.name} renewal")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            NotificationManagerCompat.from(context)
                .notify(subscription.id.toInt(), notification)

        } catch (e: SecurityException) {
            Log.e("NotificationHelper", "SecurityException: ${e.message}")
            e.printStackTrace()
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Exception: ${e.message}")
            e.printStackTrace()
        }
    }

}