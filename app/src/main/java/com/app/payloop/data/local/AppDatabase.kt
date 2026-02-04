package com.app.payloop.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.payloop.data.local.SubscriptionDao
import com.app.payloop.data.model.Subscription
import kotlin.concurrent.Volatile

@Database(entities = [Subscription::class], version = 1, exportSchema = false)
@TypeConverters
abstract class AppDatabase: RoomDatabase() {

    abstract fun subscriptionDao(): SubscriptionDao

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{

            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "subscription_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}