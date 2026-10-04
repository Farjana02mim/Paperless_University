package com.example.ui.identity.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.identity.DigitalIdStatus
import com.example.domain.model.identity.QrPassVerificationResult
import com.example.ui.identity.DigitalIdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrPassVerificationScreen(
    viewModel: DigitalIdViewModel,
    onBackClick: () -> Unit
) {
    val verificationResult by viewModel.verificationResult.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    var simPayloadInput by remember { mutableStateOf("SMART_PASS|UID:2024-3-60-042|ROLE:STUDENT|EXPIRE:2025-12-31|SIG:VERIFIED") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff QR Pass Scanner", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Scanner Camera Frame Simulation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Position QR Pass inside the frame",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Scanner Corner Reticle Visual
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Simulation Presets
            Text("Simulate Staff Gate Scan:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = simPayloadInput.contains("VERIFIED"),
                    onClick = {
                        simPayloadInput = "SMART_PASS|UID:2024-3-60-042|ROLE:STUDENT|STATUS:ACTIVE"
                        viewModel.verifyQrCode(simPayloadInput)
                    },
                    label = { Text("Active Student") },
                    leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )

                FilterChip(
                    selected = simPayloadInput.contains("EXPIRED"),
                    onClick = {
                        simPayloadInput = "SMART_PASS|UID:EXPIRED_CARD|STATUS:EXPIRED"
                        viewModel.verifyQrCode(simPayloadInput)
                    },
                    label = { Text("Expired Pass") },
                    leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )

                FilterChip(
                    selected = simPayloadInput.contains("SUSPENDED"),
                    onClick = {
                        simPayloadInput = "SMART_PASS|UID:SUSPENDED|STATUS:SUSPENDED"
                        viewModel.verifyQrCode(simPayloadInput)
                    },
                    label = { Text("Suspended") },
                    leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isScanning) {
                CircularProgressIndicator()
            }

            // Animated Verification Result
            verificationResult?.let { res ->
                Spacer(modifier = Modifier.height(16.dp))
                VerificationResultCard(result = res, onDismiss = { viewModel.clearVerificationResult() })
            }
        }
    }
}

@Composable
fun VerificationResultCard(
    result: QrPassVerificationResult,
    onDismiss: () -> Unit
) {
    val (statusColor, statusTitle, statusIcon) = when (result.status) {
        DigitalIdStatus.VERIFIED_ACTIVE -> Triple(Color(0xFF2E7D32), "VERIFIED CAMPUS ACCESS", Icons.Default.CheckCircle)
        DigitalIdStatus.EXPIRED -> Triple(Color(0xFFE65100), "PASS EXPIRED", Icons.Default.Warning)
        DigitalIdStatus.SUSPENDED -> Triple(Color(0xFFC62828), "ACCESS DENIED - SUSPENDED", Icons.Default.Block)
        else -> Triple(Color.Gray, "INVALID QR CODE", Icons.Default.Error)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(2.dp, statusColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusTitle,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = result.scannedPhotoUrl.ifEmpty { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500" },
                    contentDescription = "Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(text = result.scannedName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(text = "ID: ${result.scannedUniversityId}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Dept: ${result.scannedDepartment}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Role: ${result.scannedRole}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = result.verificationMessage,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onDismiss) {
                Text("Dismiss Scan Result")
            }
        }
    }
}
