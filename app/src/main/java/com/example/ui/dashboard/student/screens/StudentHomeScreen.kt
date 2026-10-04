package com.example.ui.dashboard.student.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.dashboard.student.StudentDashboardUiState
import com.example.ui.dashboard.student.widgets.BannerCarouselWidget
import com.example.ui.dashboard.student.widgets.DashboardSkeletonLoader
import com.example.ui.dashboard.student.widgets.DigitalIdCardDialog
import com.example.ui.dashboard.student.widgets.LatestNoticesWidget
import com.example.ui.dashboard.student.widgets.NoticeDetailDialog
import com.example.ui.dashboard.student.widgets.PerformanceSummaryWidget
import com.example.ui.dashboard.student.widgets.QrScannerDialog
import com.example.ui.dashboard.student.widgets.QuickActionDetailModal
import com.example.ui.dashboard.student.widgets.QuickActionsGridWidget
import com.example.ui.dashboard.student.widgets.RecentActivitiesWidget
import com.example.ui.dashboard.student.widgets.TodayScheduleWidget
import com.example.ui.dashboard.student.widgets.UpcomingAssignmentsWidget
import com.example.ui.dashboard.student.widgets.WelcomeCardWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    uiState: StudentDashboardUiState,
    onRefresh: () -> Unit,
    onQuickActionClick: (String) -> Unit,
    onNoticeClick: (com.example.domain.model.NoticeItem) -> Unit,
    onDismissNoticeDialog: () -> Unit,
    onDismissDigitalIdModal: () -> Unit,
    onDismissQrModal: () -> Unit,
    onDismissQuickActionModal: () -> Unit,
    onScanQrCode: (String) -> Unit
) {
    if (uiState.isLoading && !uiState.isRefreshing) {
        DashboardSkeletonLoader()
        return
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. Welcome Card
            WelcomeCardWidget(data = uiState.data)

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Announcements Banner Carousel
            BannerCarouselWidget(banners = uiState.data.banners)

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Quick Actions Grid (12 Actions)
            QuickActionsGridWidget(
                onActionClick = { actionKey ->
                    if (actionKey == "digital_id") {
                        onQuickActionClick("digital_id")
                    } else if (actionKey == "attendance") {
                        onQuickActionClick("qr_scanner")
                    } else {
                        onQuickActionClick(actionKey)
                    }
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Today's Schedule
            TodayScheduleWidget(schedules = uiState.data.schedules)

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Latest Notices
            LatestNoticesWidget(
                notices = uiState.data.notices,
                onNoticeClick = onNoticeClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 6. Upcoming Assignments
            UpcomingAssignmentsWidget(assignments = uiState.data.assignments)

            Spacer(modifier = Modifier.height(18.dp))

            // 7. Performance Summary Cards
            PerformanceSummaryWidget(summary = uiState.data.performance)

            Spacer(modifier = Modifier.height(18.dp))

            // 8. Recent Activities Timeline
            RecentActivitiesWidget(activities = uiState.data.activities)

            Spacer(modifier = Modifier.height(80.dp)) // Extra bottom padding for FAB and Nav Bar
        }
    }

    // Modal Dialogs
    if (uiState.showDigitalIdModal) {
        DigitalIdCardDialog(
            data = uiState.data,
            onDismiss = onDismissDigitalIdModal
        )
    }

    if (uiState.showQrScannerModal) {
        QrScannerDialog(
            scanMessage = uiState.qrScanMessage,
            onScanCode = onScanQrCode,
            onDismiss = onDismissQrModal
        )
    }

    uiState.selectedNotice?.let { notice ->
        NoticeDetailDialog(
            notice = notice,
            onDismiss = onDismissNoticeDialog
        )
    }

    uiState.activeQuickActionDialog?.let { actionKey ->
        QuickActionDetailModal(
            actionKey = actionKey,
            onDismiss = onDismissQuickActionModal
        )
    }
}
