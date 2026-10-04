package com.example.ui.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.data.repository.teacher.TeacherRepositoryImpl
import com.example.ui.teacher.TeacherMainPortalScreen
import com.example.ui.teacher.TeacherPortalViewModel

@Composable
fun TeacherDashboardScreen(
    onLogout: () -> Unit
) {
    val repository = remember { TeacherRepositoryImpl() }
    val viewModel = remember { TeacherPortalViewModel(repository) }

    TeacherMainPortalScreen(
        viewModel = viewModel,
        onLogout = onLogout
    )
}
