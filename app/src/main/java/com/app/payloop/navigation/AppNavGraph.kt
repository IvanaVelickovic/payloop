package com.app.payloop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.payloop.data.repository.SubscriptionRepository
import com.app.payloop.settings.EditSubscriptionScreen
import com.app.payloop.settings.ViewSubscriptionScreen
import com.app.payloop.ui.editsubscription.EditSubscriptionViewModel
import com.app.payloop.ui.editsubscription.EditSubscriptionViewModelFactory
import com.app.payloop.ui.mainscreen.MainScreen
import com.app.payloop.ui.mainscreen.MainScreenViewModel
import com.app.payloop.ui.mainscreen.MainScreenViewModelFactory
import com.app.payloop.ui.subscriptionview.ViewSubscriptionViewModel
import com.app.payloop.ui.subscriptionview.ViewSubscriptionViewModelFactory

@Composable
fun AppNavGraph(repository: SubscriptionRepository) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Main.route
    ) {
        composable(NavRoutes.Main.route) {
            val viewModel : MainScreenViewModel = viewModel (
                factory = MainScreenViewModelFactory(repository)
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

        composable(route = NavRoutes.ViewSubscription.route,
            arguments = listOf(
                navArgument("subscriptionId") {
                    type = NavType.LongType
                }
            )) { backStackEntry ->
            val subscriptionId = backStackEntry.arguments!!.getLong("subscriptionId")

            val viewModel : ViewSubscriptionViewModel = viewModel (
                factory = ViewSubscriptionViewModelFactory(repository, subscriptionId)
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
                factory = EditSubscriptionViewModelFactory(repository, subscriptionId)
            )
            val state by viewModel.state.collectAsState()


            EditSubscriptionScreen(
                state = state,
                subscriptionId = subscriptionId,
                navController = navController,
                onEvent = { event ->
                    viewModel.onEvent(event)
                }
            )
        }


    }

}