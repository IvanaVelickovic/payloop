package com.app.payloop

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.app.payloop.data.local.AppDatabase
import com.app.payloop.data.local.SubscriptionDao
import com.app.payloop.data.repository.SubscriptionRepository
import com.app.payloop.navigation.AppNavGraph
import com.app.payloop.settings.EditSubscriptionScreen
import com.app.payloop.ui.mainscreen.MainScreen
import com.app.payloop.ui.mainscreen.MainScreenViewModel
import com.app.payloop.ui.theme.PayloopTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var repository: SubscriptionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        database = AppDatabase.getDatabase(applicationContext)
        repository = SubscriptionRepository(database.subscriptionDao(), applicationContext)

        //refresh za provjeru nextChargeDatea
        lifecycleScope.launch {
            repository.refreshExpiredSubscriptions()
        }

        setContent {
            PayloopTheme {
                AppNavGraph(repository = repository,
                    initialIntent = intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            repository.refreshExpiredSubscriptions()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)  // Update intent za deep linking
    }
}
