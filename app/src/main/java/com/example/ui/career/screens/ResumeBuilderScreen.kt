package com.example.ui.career.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.career.CareerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeBuilderScreen(
    viewModel: CareerViewModel,
    onBackClick: () -> Unit
) {
    val resume by viewModel.resumeProfile.collectAsState()

    var headlineInput by remember(resume) { mutableStateOf(resume.headline) }
    var summaryInput by remember(resume) { mutableStateOf(resume.summary) }
    var skillsInput by remember(resume) { mutableStateOf(resume.skills.joinToString(", ")) }
    var projectsInput by remember(resume) { mutableStateOf(resume.projects.joinToString("\n")) }
    var isSavedSnackbarVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ATS Resume Builder Engine", fontWeight = FontWeight.Bold) },
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
            // Header Score Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Current Resume ATS Score: ${resume.resumeScore}%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Optimized for Global & Local Tech Employers (PDF Generated)", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Text("Personal Details (Auto-Synced)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = resume.studentName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Student Name") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = resume.studentId,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Student ID") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = resume.department,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Department") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = resume.cgpa.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Current CGPA") },
                    modifier = Modifier.weight(1f)
                )
            }

            Text("Professional Headline & Summary", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = headlineInput,
                onValueChange = { headlineInput = it },
                label = { Text("Professional Headline") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = summaryInput,
                onValueChange = { summaryInput = it },
                label = { Text("Executive Career Summary") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Technical & Soft Skills (Comma Separated)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = skillsInput,
                onValueChange = { skillsInput = it },
                label = { Text("Skills List") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Key Projects & Achievements", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = projectsInput,
                onValueChange = { projectsInput = it },
                label = { Text("Projects (One per line)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val updated = resume.copy(
                        headline = headlineInput,
                        summary = summaryInput,
                        skills = skillsInput.split(",").map { it.trim() }.filter { it.isNotBlank() },
                        projects = projectsInput.split("\n").map { it.trim() }.filter { it.isNotBlank() }
                    )
                    viewModel.updateResume(updated)
                    isSavedSnackbarVisible = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Profile & Re-calculate ATS Score")
            }

            OutlinedButton(
                onClick = { /* Export PDF */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download Official ATS PDF Resume")
            }

            if (isSavedSnackbarVisible) {
                Snackbar(
                    action = {
                        TextButton(onClick = { isSavedSnackbarVisible = false }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text("ATS Resume Profile updated successfully!")
                }
            }
        }
    }
}
