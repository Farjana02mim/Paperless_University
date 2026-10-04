package com.example.ui.campus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.campus.CampusServicesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoMonitoringScreen(
    viewModel: CampusServicesViewModel,
    onBackClick: () -> Unit
) {
    val ecoStats by viewModel.ecoStats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campus Eco Sustainability", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Eco Green Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFF2E7D32), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Forest, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "100% Paperless University Impact",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        "By digitizing admissions, clearance, exam grading, and payments.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF2E7D32)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ecoStats?.let { stats ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("1.42M", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color(0xFF1B5E20))
                                Text("Paper Sheets Saved", fontSize = 11.sp, color = Color(0xFF2E7D32))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${stats.estimatedTreesSaved}", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color(0xFF1B5E20))
                                Text("Trees Saved", fontSize = 11.sp, color = Color(0xFF2E7D32))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("11.3 Tons", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color(0xFF1B5E20))
                                Text("CO2 Reduced", fontSize = 11.sp, color = Color(0xFF2E7D32))
                            }
                        }
                    }
                }
            }

            // Student Personal Score Section
            ecoStats?.let { stats ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Your Personal Eco Rating", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            }

                            Text("${stats.userPersonalScore} / 100", fontWeight = FontWeight.Black, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { stats.userPersonalScore / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color(0xFF2E7D32),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("You have completed ${stats.personalDigitalTransfers} paperless digital operations (Admissions, Fee Payments, Library Requests) this academic year.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
