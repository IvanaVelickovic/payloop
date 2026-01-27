package com.app.payloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.payloop.mainscreen.MainScreen
import com.app.payloop.settings.EditSubscriptionScreen
import com.app.payloop.settings.SettingsScreen
import com.app.payloop.settings.SubscriptionViewScreen
import com.app.payloop.ui.theme.PayloopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            MainScreenPreview()
            /*PayloopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            } */
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    EditSubscriptionScreen()
}
