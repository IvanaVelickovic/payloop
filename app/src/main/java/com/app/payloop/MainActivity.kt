package com.app.payloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.payloop.data.local.AppDatabase
import com.app.payloop.data.local.SubscriptionDao
import com.app.payloop.data.repository.SubscriptionRepository
import com.app.payloop.navigation.AppNavGraph
import com.app.payloop.settings.EditSubscriptionScreen
import com.app.payloop.ui.mainscreen.MainScreen
import com.app.payloop.ui.mainscreen.MainScreenViewModel
import com.app.payloop.ui.theme.PayloopTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var repository: SubscriptionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        database = AppDatabase.getDatabase(applicationContext)
        repository = SubscriptionRepository(database.subscriptionDao())

        setContent {
            PayloopTheme {
                AppNavGraph(repository = repository)
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    //
}
