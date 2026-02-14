package com.incidentcommand.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.incidentcommand.android.ui.auth.LoginScreen
import com.incidentcommand.android.ui.auth.RegisterScreen
import com.incidentcommand.android.ui.dashboard.DashboardScreen
import com.incidentcommand.android.ui.incidents.detail.IncidentDetailScreen
import com.incidentcommand.android.ui.incidents.form.IncidentFormScreen
import com.incidentcommand.android.ui.incidents.list.IncidentListScreen
import com.incidentcommand.android.ui.teams.TeamManagementScreen

@Composable
fun AppNavHost(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToIncidents = { navController.navigate(Screen.IncidentList.route) },
                onNavigateToTeams = { navController.navigate(Screen.TeamManagement.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.IncidentList.route) {
            IncidentListScreen(
                onBack = { navController.popBackStack() },
                onOpenIncident = { id ->
                    navController.navigate(Screen.IncidentDetail.createRoute(id))
                },
                onCreateIncident = {
                    navController.navigate(Screen.IncidentForm.createRoute())
                }
            )
        }

        composable(
            route = Screen.IncidentDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val incidentId = backStackEntry.arguments?.getLong("id") ?: return@composable
            IncidentDetailScreen(
                incidentId = incidentId,
                onBack = { navController.popBackStack() },
                onEdit = { id ->
                    navController.navigate(Screen.IncidentForm.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.IncidentForm.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val rawId = backStackEntry.arguments?.getLong("id") ?: -1L
            val incidentId = rawId.takeIf { it != -1L }
            IncidentFormScreen(
                incidentId = incidentId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Screen.TeamManagement.route) {
            TeamManagementScreen(onBack = { navController.popBackStack() })
        }
    }
}
