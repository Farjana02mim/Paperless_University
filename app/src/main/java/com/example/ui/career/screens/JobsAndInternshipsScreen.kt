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
import com.example.domain.model.career.JobPosting
import com.example.ui.career.CareerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsAndInternshipsScreen(
    viewModel: CareerViewModel,
    onBackClick: () -> Unit
) {
    val jobs by viewModel.jobPostings.collectAsState()
    val internships by viewModel.internshipPostings.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showApplyDialog by remember { mutableStateOf<JobPosting?>(null) }

    val filteredJobs = jobs.filter {
        searchQuery.isBlank() ||
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.companyName.contains(searchQuery, ignoreCase = true) ||
                it.requiredSkills.any { skill -> skill.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jobs & Internship Portal", fontWeight = FontWeight.Bold) },
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
                    text = { Text("Full-time Jobs (${jobs.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Internships (${internships.size})") }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search title, company, skills...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )

                if (selectedTab == 0) {
                    filteredJobs.forEach { job ->
                        JobCard(
                            job = job,
                            onBookmarkToggle = { viewModel.toggleBookmark(job.jobId, job.isBookmarked) },
                            onApply = { showApplyDialog = job }
                        )
                    }
                } else {
                    internships.forEach { intern ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(intern.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(intern.companyName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Duration: ${intern.durationMonths} Months • Stipend: BDT ${intern.stipendMonthly.toInt()}/mo", style = MaterialTheme.typography.bodySmall)
                                Text("Location: ${intern.location}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Required: ${intern.requiredSkills.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)

                                Spacer(modifier = Modifier.height(12.dp))
                                if (intern.isApplied) {
                                    OutlinedButton(
                                        onClick = { },
                                        enabled = false,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Application Submitted")
                                    }
                                } else {
                                    Button(
                                        onClick = { },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Apply for Internship")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    showApplyDialog?.let { job ->
        AlertDialog(
            onDismissRequest = { showApplyDialog = null },
            title = { Text("Confirm Job Application") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Applying for: ${job.title}", fontWeight = FontWeight.Bold)
                    Text("Company: ${job.companyName}")
                    Text("Salary Range: ${job.salaryRange}")
                    Text("Your current ATS Resume will be automatically submitted with your academic credentials.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.applyForJob(job.jobId, job.title, job.companyName)
                        showApplyDialog = null
                    }
                ) {
                    Text("Submit Resume & Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApplyDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun JobCard(
    job: JobPosting,
    onBookmarkToggle: () -> Unit,
    onApply: () -> Unit
) {
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
                Column {
                    Text(job.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(job.companyName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }

                IconButton(onClick = onBookmarkToggle) {
                    Icon(
                        if (job.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (job.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Compensation: ${job.salaryRange}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text("Location: ${job.location} • Type: ${job.employmentType.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Min CGPA: ${job.minimumCgpa} • Deadline: ${job.deadline}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(10.dp))
            Text("Skills: ${job.requiredSkills.joinToString(" • ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)

            Spacer(modifier = Modifier.height(12.dp))
            if (job.isApplied) {
                OutlinedButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Applied")
                }
            } else {
                Button(
                    onClick = onApply,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("One-Click Apply with ATS Resume")
                }
            }
        }
    }
}
