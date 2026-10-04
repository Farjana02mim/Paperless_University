package com.example.ui.dashboard

import androidx.compose.runtime.Composable
import com.example.ui.admin.AdminMainPortalScreen

@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit
) {
    AdminMainPortalScreen(
        onNavigateBack = onLogout
    )
}
