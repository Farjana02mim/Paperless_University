package com.example.ui.campus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.campus.CampusServicesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusServicesHubScreen(
    viewModel: CampusServicesViewModel,
    onNavigateToTransport: () -> Unit,
    onNavigateToHostel: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToMedical: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToEco: () -> Unit,
    onNavigateToResearch: () -> Unit = {},
    onNavigateToExamController: () -> Unit = {},
    onNavigateToDining: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val ecoStats by viewModel.ecoStats.collectAsState()
    val uiNotice by viewModel.uiNotice.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Campus Services", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = {
            uiNotice?.let { msg ->
                Snackbar(
                    action = {
                        TextButton(onClick = { viewModel.clearNotice() }) {
                            Text("OK")
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(msg)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // One-Tap Emergency Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToEmergency() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.error, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Emergency SOS",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "One-Tap Emergency SOS",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            "Instant Security, Medical & Fire Alert Dispatch",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Quick Services Grid Section
            Text(
                "Campus Services & Facilities",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            val serviceItems = listOf(
                ServiceHubItem("JSTU Research Hub", "Journals & Papers Repository", Icons.Default.MenuBook, MaterialTheme.colorScheme.primaryContainer, onNavigateToResearch),
                ServiceHubItem("Exam Controller", "Admit Cards & UGC CGPA", Icons.Default.Badge, MaterialTheme.colorScheme.secondaryContainer, onNavigateToExamController),
                ServiceHubItem("Hall Dining Pass", "Mess Seat & Meal QR Tokens", Icons.Default.Restaurant, MaterialTheme.colorScheme.tertiaryContainer, onNavigateToDining),
                ServiceHubItem("Smart Transport", "Live Bus Routes & Passes", Icons.Default.DirectionsBus, MaterialTheme.colorScheme.primaryContainer, onNavigateToTransport),
                ServiceHubItem("Hostel Hub", "Room, Mess & Leave Requests", Icons.Default.Bed, MaterialTheme.colorScheme.secondaryContainer, onNavigateToHostel),
                ServiceHubItem("Medical Center", "Doctors & Appointments", Icons.Default.LocalHospital, MaterialTheme.colorScheme.tertiaryContainer, onNavigateToMedical),
                ServiceHubItem("Campus Map", "Buildings & Floor Guides", Icons.Default.Map, MaterialTheme.colorScheme.surfaceVariant, onNavigateToMap),
                ServiceHubItem("Events & Clubs", "Hackathons & Memberships", Icons.Default.Groups, MaterialTheme.colorScheme.primaryContainer, onNavigateToEvents),
                ServiceHubItem("Eco Sustainability", "Paperless Metrics & Score", Icons.Default.Forest, Color(0xFFE8F5E9), onNavigateToEco)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (chunk in serviceItems.chunked(2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (item in chunk) {
                            ServiceCard(
                                item = item,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (chunk.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Eco Impact Snapshot Card
            ecoStats?.let { stats ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToEco() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Eco, contentDescription = null, tint = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("University Paperless Impact", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            }
                            Text("See Analytics", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("1,420,500", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1B5E20))
                                Text("Sheets Saved", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${stats.estimatedTreesSaved}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1B5E20))
                                Text("Trees Saved", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${stats.userPersonalScore}/100", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                                Text("Your Eco Score", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class ServiceHubItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val containerColor: Color,
    val onClick: () -> Unit
)

@Composable
private fun ServiceCard(
    item: ServiceHubItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { item.onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = item.containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Text(item.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
        }
    }
}
