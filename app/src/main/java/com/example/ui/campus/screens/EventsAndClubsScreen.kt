package com.example.ui.campus.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.campus.CampusClub
import com.example.domain.model.campus.CampusEvent
import com.example.ui.campus.CampusServicesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsAndClubsScreen(
    viewModel: CampusServicesViewModel,
    onBackClick: () -> Unit
) {
    val events by viewModel.events.collectAsState()
    val clubs by viewModel.clubs.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showTicketDialog by remember { mutableStateOf<CampusEvent?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Events & Student Clubs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Campus Events") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Student Clubs") }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedTab == 0) {
                    events.forEach { event ->
                        EventCard(
                            event = event,
                            onShowTicket = { showTicketDialog = event }
                        )
                    }
                } else {
                    clubs.forEach { club ->
                        ClubCard(club = club)
                    }
                }
            }
        }
    }

    showTicketDialog?.let { event ->
        AlertDialog(
            onDismissRequest = { showTicketDialog = null },
            title = { Text("QR Event Ticket") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(event.title, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier
                            .size(180.dp)
                            .padding(8.dp),
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color.White, modifier = Modifier.size(140.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(event.ticketQrToken, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                }
            },
            confirmButton = {
                Button(onClick = { showTicketDialog = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun EventCard(
    event: CampusEvent,
    onShowTicket: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(event.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Organized by ${event.organizer}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text("${event.date} • ${event.venue}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(event.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))
            if (event.isRegistered) {
                Button(
                    onClick = onShowTicket,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Show QR Ticket")
                }
            } else {
                OutlinedButton(
                    onClick = { /* Register */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register for Event")
                }
            }
        }
    }
}

@Composable
private fun ClubCard(club: CampusClub) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(club.clubName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        club.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(club.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Advisor: ${club.leadName} • Members: ${club.totalMembers}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (club.isMember) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            "ACTIVE MEMBER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Button(onClick = { /* Join */ }) {
                        Text("Join Club")
                    }
                }
            }
        }
    }
}
