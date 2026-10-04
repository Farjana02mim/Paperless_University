package com.example.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.result.ResultStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminResultPublishScreen(
    viewModel: AdminResultPublishViewModel,
    onNavigateBack: () -> Unit
) {
    val department by viewModel.selectedDepartment.collectAsStateWithLifecycle()
    val semester by viewModel.selectedSemester.collectAsStateWithLifecycle()
    val approvalStatus by viewModel.approvalStatus.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Result Publication", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Department Verification & Publication", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("$department • $semester", fontSize = 13.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when (approvalStatus) {
                                ResultStatus.PUBLISHED -> Color(0xFF2E7D32)
                                ResultStatus.LOCKED -> Color(0xFF37474F)
                                ResultStatus.APPROVED_BY_ADMIN -> Color(0xFF1565C0)
                                else -> Color(0xFFEF6C00)
                            }
                        ) {
                            Text(
                                "Status: ${approvalStatus.name}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Publication Lifecycle Actions", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        Button(
                            onClick = { viewModel.approveDepartmentResults() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = approvalStatus == ResultStatus.SUBMITTED
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("1. Verify & Approve Department Results")
                        }

                        Button(
                            onClick = { viewModel.publishSemesterResultsToStudents() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            enabled = approvalStatus == ResultStatus.APPROVED_BY_ADMIN
                        ) {
                            Icon(Icons.Default.Publish, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("2. Publish Results to Students")
                        }

                        Button(
                            onClick = { viewModel.lockPublishedResults() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                            enabled = approvalStatus == ResultStatus.PUBLISHED
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("3. Lock Published Result Set")
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Audit & Security Trail", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• 05 Aug 2026: Marks entered by Prof. Robert Vance", fontSize = 12.sp, color = Color.Gray)
                        Text("• 05 Aug 2026: Department verification passed by HOD", fontSize = 12.sp, color = Color.Gray)
                        Text("• Cryptographic Hash: SHA256-UNI-RES-LOG-8472", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }

    actionMessage?.let { msg ->
        Snackbar(
            action = { TextButton(onClick = { viewModel.clearActionMessage() }) { Text("OK") } },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(msg)
        }
    }
}
