package com.app.payloop.navigation

import android.content.Intent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.app.payloop.navigation.NavRoutes.ViewSubscription.createRoute
import com.app.payloop.settings.EditSubscriptionScreen
import com.app.payloop.settings.ViewSubscriptionScreen
import com.app.payloop.ui.add_subscription.AddSubscriptionScreen
import com.app.payloop.ui.add_subscription.AddSubscriptionViewModel
import com.app.payloop.ui.add_subscription.AddSubscriptionViewModelFactory
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
fun AppNavGraph(repository: SubscriptionRepository,
                initialIntent: Intent? = null) {

    val navController = rememberNavController()

    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val settingsRepository = remember { SettingsRepository(settingsDataStore) }

    LaunchedEffect(initialIntent) {
        initialIntent?.let{intent ->
            if(intent.getBooleanExtra("openViewScreen", false)){
                val subscriptionId = intent.getLongExtra("subscriptionId", -1)
                if(subscriptionId != -1L){
                    val route = createRoute(subscriptionId)
                    navController.navigate(route)
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Main.route
    ) {
        composable(
            route = NavRoutes.Main.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(240),
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(240),
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(240),
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(240),
                )
            },
        ) {
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

        composable(
            route = NavRoutes.Settings.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(240),
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(240),
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(240),
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(240),
                )
            },
        ) {
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
            ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(240),
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(240),
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(240),
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(240),
                )
            },
        ) { backStackEntry ->
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
            ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(240),
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(240),
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(240),
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(240),
                )
            },
        ) { backStackEntry ->
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

        composable(
            route = NavRoutes.AddSubscription.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(240),
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(240),
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(240),
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(240),
                )
            },
        ) {
            val viewModel : AddSubscriptionViewModel = viewModel (
                factory = AddSubscriptionViewModelFactory(repository)
            )

            AddSubscriptionScreen(
                viewModel = viewModel,
                navController = navController,
            )

        }


    }

}
