package com.example.ui.result

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.result.PdfTranscriptExporter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranscriptViewScreen(
    viewModel: StudentResultViewModel,
    onNavigateBack: () -> Unit
) {
    val transcriptState by viewModel.transcriptState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Official Digital Transcript", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    transcriptState?.let { transcript ->
                        IconButton(onClick = {
                            val pdfFile = PdfTranscriptExporter.generateTranscriptPdf(context, transcript)
                            Toast.makeText(context, "Transcript PDF Saved: ${pdfFile.name}", Toast.LENGTH_LONG).show()
                        }) {
                            Icon(Icons.Default.Download, contentDescription = "Download PDF")
                        }
                        IconButton(onClick = {
                            val pdfFile = PdfTranscriptExporter.generateTranscriptPdf(context, transcript)
                            PdfTranscriptExporter.printTranscript(context, pdfFile)
                        }) {
                            Icon(Icons.Default.Print, contentDescription = "Print")
                        }
                    }
                }
            )
        }
    ) { padding ->
        transcriptState?.let { transcript ->
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "SMART PAPERLESS UNIVERSITY",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "OFFICIAL ACADEMIC TRANSCRIPT OF RECORD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Student: ${transcript.studentName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Roll Number: ${transcript.rollNumber}", fontSize = 12.sp, color = Color.Gray)
                                    Text("Degree: ${transcript.degreeTitle}", fontSize = 11.sp, color = Color.Gray)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("CGPA: %.2f".format(transcript.overallCGPA), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                    Text("Credits: ${transcript.overallCredits.toInt()}", fontSize = 12.sp, color = Color.Gray)
                                    Text(transcript.graduationStatus, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                }
                            }
                        }
                    }
                }

                items(transcript.allSemesterResults) { sem ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${sem.semester} (${sem.academicYear})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("GPA: %.2f".format(sem.semesterGPA), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            sem.courseResults.forEach { c ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${c.courseCode} - ${c.courseTitle}", fontSize = 12.sp, modifier = Modifier.weight(1f))
                                    Text("${c.letterGrade} (${c.gradePoint})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Verification Code: ${transcript.verificationCode}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Digital Signature: ${transcript.digitalSignature.take(24)}...", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
