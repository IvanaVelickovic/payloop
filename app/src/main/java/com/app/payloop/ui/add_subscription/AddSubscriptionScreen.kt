package com.app.payloop.ui.add_subscription

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.payloop.data.local.AppDatabase

@Composable
fun AddSubscriptionScreen(
    viewModel: AddSubscriptionViewModel,
    navController: NavController,
) {
    val state by viewModel.uiState.collectAsState()

    AddSubscriptionFlowScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onSubmit = { viewModel.submit(onSuccess = { navController.popBackStack() }) },
        onNavigateBack = { navController.popBackStack() },
    )
}
