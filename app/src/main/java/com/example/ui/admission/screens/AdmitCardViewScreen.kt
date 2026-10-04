package com.example.ui.admission.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.admission.AdmitCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdmitCardViewScreen(
    admitCard: AdmitCard?,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var isDownloaded by remember { mutableStateOf(false) }

    LaunchedEffect(isDownloaded) {
        if (isDownloaded) {
            snackbarHostState.showSnackbar("Admit Card PDF downloaded to device storage.")
            isDownloaded = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Official Admit Card", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isDownloaded = true }) {
                        Icon(Icons.Default.Download, contentDescription = "Download PDF")
                    }
                }
            )
        }
    ) { padding ->
        if (admitCard == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Official Admit Card Container
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Header
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("SMART UNIVERSITY ADMISSION", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("UNDERGRADUATE ADMISSION TEST 2026", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            }

                            // Applicant Photo & Main Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("ROLL NUMBER", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(admitCard.rollNumber, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text("APPLICANT NAME", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(admitCard.applicantName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text("FATHER'S NAME", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(admitCard.fatherName, style = MaterialTheme.typography.bodyMedium)
                                }

                                AsyncImage(
                                    model = admitCard.photoUrl,
                                    contentDescription = "Applicant Photo",
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            HorizontalDivider()

                            // Exam Date & Center
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("EXAM DATE & TIME", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(admitCard.examDate, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFD32F2F))
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("EXAM CENTER / HALL", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(admitCard.examCenter, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            HorizontalDivider()

                            // QR Verification Payload Box
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("QR Verification Code", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    Text("Scan code at exam entry gate", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(Color.White)
                                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(40.dp))
                                }
                            }
                        }
                    }
                }

                // Instructions Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Candidate Instructions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            admitCard.instructions.forEach { inst ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(inst, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { isDownloaded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download & Print Admit Card PDF", fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}
