package com.app.payloop.navigation

sealed class NavRoutes(val route: String) {
    object Main : NavRoutes("main")
    object EditSubscription : NavRoutes("edit_subscription/{subscriptionId}") {
        fun createRoute(subscriptionId: Long) = "edit_subscription/$subscriptionId"
    }
    object ViewSubscription : NavRoutes("view_subscription/{subscriptionId}") {
        fun createRoute(subscriptionId: Long) = "view_subscription/$subscriptionId"
    }
    object Settings : NavRoutes("settings")

    object AddSubscription : NavRoutes("add_subscription")
}