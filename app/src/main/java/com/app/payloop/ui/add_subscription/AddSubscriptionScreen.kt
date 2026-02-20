package com.app.payloop.ui.add_subscription

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.payloop.data.local.AppDatabase

@Composable
fun AddSubscriptionScreen(
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val database = remember(context) { AppDatabase.getDatabase(context.applicationContext) }
    val factory = remember(database) { AddSubscriptionViewModelFactory(database.subscriptionDao()) }
    val viewModel: AddSubscriptionViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()

    AddSubscriptionFlowScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onSubmit = { viewModel.submit(onSuccess = onNavigateBack) },
        onNavigateBack = onNavigateBack,
    )
}
