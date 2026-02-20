package com.app.payloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.payloop.ui.add_subscription.AddSubscriptionScreen
import com.app.payloop.ui.theme.PayloopTheme // Pazi da je import točan!

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // 1. OBAVEZNO: Aktiviraj svoju temu!
            PayloopTheme {
                // 2. Postavi podlogu preko cijelog ekrana
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Pozovi naš novi ekran
                    AddSubscriptionScreen(
                        // Kad user stisne "Back" na prvom koraku, gasi Activity
                        onNavigateBack = { finish() }
                    )
                }
            }
        }
    }
}

// Ovo ostavi dolje ako želiš preview u Android Studiju (Split view),
// ali nemoj to zvati gore u setContent.
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PayloopTheme {
        AddSubscriptionScreen(onNavigateBack = {})
    }
}