package com.app.payloop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.payloop.data.local.SettingsDataStore
import com.app.payloop.data.repository.SettingsRepository
import com.app.payloop.data.repository.SubscriptionRepository
import com.app.payloop.settings.EditSubscriptionScreen
import com.app.payloop.settings.ViewSubscriptionScreen
import com.app.payloop.ui.editsubscription.EditSubscriptionViewModel
import com.app.payloop.ui.editsubscription.EditSubscriptionViewModelFactory
import com.app.payloop.ui.mainscreen.MainScreen
import com.app.payloop.ui.mainscreen.MainScreenViewModel
import com.app.payloop.ui.mainscreen.MainScreenViewModelFactory
import com.app.payloop.ui.settings.SettingsScreen
import com.app.payloop.ui.settings.SettingsViewModel
import com.app.payloop.ui.settings.SettingsViewModelFactory
import com.app.payloop.ui.subscriptionview.ViewSubscriptionViewModel
import com.app.payloop.ui.subscriptionview.ViewSubscriptionViewModelFactory

@Composable
fun AppNavGraph(repository: SubscriptionRepository) {

    val navController = rememberNavController()

    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val settingsRepository = remember { SettingsRepository(settingsDataStore) }


    NavHost(
        navController = navController,
        startDestination = NavRoutes.Main.route
    ) {
        composable(NavRoutes.Main.route) {
            val viewModel : MainScreenViewModel = viewModel (
                factory = MainScreenViewModelFactory(repository, settingsRepository)
            )

            val state by viewModel.state.collectAsState()

            MainScreen(
                state = state,
                navController = navController,
                //onEvent = {
                    //handle events
                //}
            )

        }

        composable(NavRoutes.Settings.route) {
            val viewModel : SettingsViewModel = viewModel (
                factory = SettingsViewModelFactory(settingsRepository, repository)
            )

            SettingsScreen(
                viewModel = viewModel,
                navController = navController,
            )

        }

        composable(route = NavRoutes.ViewSubscription.route,
            arguments = listOf(
                navArgument("subscriptionId") {
                    type = NavType.LongType
                }
            )) { backStackEntry ->
            val subscriptionId = backStackEntry.arguments!!.getLong("subscriptionId")

            val viewModel : ViewSubscriptionViewModel = viewModel (
                factory = ViewSubscriptionViewModelFactory(repository, settingsRepository, subscriptionId)
            )
            val state by viewModel.state.collectAsState()


            ViewSubscriptionScreen(
                state = state,
                subscriptionId = subscriptionId,
                navController = navController,
                onEvent = { event ->
                    viewModel.onEvent(event)
                }
            )
        }

        composable(route = NavRoutes.EditSubscription.route,
            arguments = listOf(
                navArgument("subscriptionId") {
                    type = NavType.LongType
                }
            )) { backStackEntry ->
            val subscriptionId = backStackEntry.arguments!!.getLong("subscriptionId")

            val viewModel : EditSubscriptionViewModel = viewModel (
                factory = EditSubscriptionViewModelFactory(repository, settingsRepository, subscriptionId)
            )
            val state by viewModel.state.collectAsState()


            EditSubscriptionScreen(
                state = state,
                navController = navController,
                onEvent = { event ->
                    viewModel.onEvent(event)
                }
            )
        }


    }

}