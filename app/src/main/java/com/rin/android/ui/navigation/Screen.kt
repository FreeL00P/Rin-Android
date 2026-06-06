package com.rin.android.ui.navigation

sealed class Screen(val route: String) {
    data object Setup : Screen("setup")
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object FeedDetail : Screen("feed/{id}") {
        fun createRoute(id: String) = "feed/$id"
    }
    data object Editor : Screen("editor?id={id}") {
        fun createRoute(id: Int? = null) = if (id != null) "editor?id=$id" else "editor"
    }
    data object Drafts : Screen("drafts")
    data object Profile : Screen("profile")
    data object Search : Screen("search")
}
