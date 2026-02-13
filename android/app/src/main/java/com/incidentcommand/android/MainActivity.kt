package com.incidentcommand.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.incidentcommand.android.data.manager.TokenManager
import com.incidentcommand.android.ui.navigation.AppNavHost
import com.incidentcommand.android.ui.theme.IncidentCommandTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IncidentCommandTheme {
                AppNavHost(isLoggedIn = tokenManager.isLoggedIn())
            }
        }
    }
}
