package com.rin.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rin.android.ui.screen.detail.FeedDetailScreen
import com.rin.android.ui.screen.drafts.DraftsScreen
import com.rin.android.ui.screen.editor.EditorScreen
import com.rin.android.ui.screen.home.HomeScreen
import com.rin.android.ui.screen.login.LoginScreen
import com.rin.android.ui.screen.profile.ProfileScreen
import com.rin.android.ui.screen.search.SearchScreen
import com.rin.android.ui.screen.setup.SetupScreen

@Composable
fun RinNavHost() {
    val navController = rememberNavController()
    val startDestination = Screen.Setup.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Setup.route) {
            SetupScreen(
                onNext = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Setup.route) { inclusive = true } } },
                onAlreadyLoggedIn = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onFeedClick = { id -> navController.navigate(Screen.FeedDetail.createRoute(id.toString())) },
                onWriteClick = { navController.navigate(Screen.Editor.createRoute()) },
                onDraftsClick = { navController.navigate(Screen.Drafts.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onSearchClick = { navController.navigate(Screen.Search.route) },
            )
        }
        composable(
            route = Screen.FeedDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) {
            FeedDetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Screen.Editor.createRoute(id)) },
            )
        }
        composable(
            route = Screen.Editor.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")?.takeIf { it > 0 }
            EditorScreen(
                feedId = id,
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.Drafts.route) {
            DraftsScreen(
                onBack = { navController.popBackStack() },
                onEditDraft = { id -> navController.navigate(Screen.Editor.createRoute()) },
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = { navController.navigate(Screen.Setup.route) { popUpTo(0) } },
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onFeedClick = { id -> navController.navigate(Screen.FeedDetail.createRoute(id.toString())) },
            )
        }
    }
}
