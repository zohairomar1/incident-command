package com.incidentcommand.android.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Dashboard : Screen("dashboard")
    data object IncidentList : Screen("incident-list")
    data object IncidentDetail : Screen("incident-detail/{id}") {
        fun createRoute(id: Long): String = "incident-detail/$id"
    }

    data object IncidentForm : Screen("incident-form?id={id}") {
        fun createRoute(id: Long? = null): String {
            return if (id == null) "incident-form" else "incident-form?id=$id"
        }
    }

    data object TeamManagement : Screen("team-management")
}
