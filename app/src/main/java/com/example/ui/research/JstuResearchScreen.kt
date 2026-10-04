package com.example.ui.research

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ResearchPaper(
    val id: String,
    val title: String,
    val authors: String,
    val department: String,
    val journalName: String,
    val publicationDate: String,
    val doi: String,
    val citationsCount: Int,
    val downloadsCount: Int,
    val abstractText: String,
    val isPeerReviewed: Boolean = true
)

class ResearchViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _papers = MutableStateFlow(
        listOf(
            ResearchPaper(
                id = "PUB-JSTU-2025-01",
                title = "IoT-Enabled Precision Agriculture Framework for Climate-Resilient Crops in Riverine Bangladesh",
                authors = "Dr. M. A. Rahaman, Prof. Anisur Rahman, S. K. Roy",
                department = "Computer Science & Engineering",
                journalName = "JSTU Journal of Science & Technology (Vol 12, Issue 2)",
                publicationDate = "January 2025",
                doi = "10.5281/zenodo.jstu.2025.101",
                citationsCount = 14,
                downloadsCount = 420,
                abstractText = "This paper presents a low-cost IoT sensor architecture deployed in agricultural fields of Jamalpur district to monitor soil moisture, pH, and micro-climate parameters using LoRaWAN protocol."
            ),
            ResearchPaper(
                id = "PUB-JSTU-2025-02",
                title = "Phytoremediation of Heavy Metals in Industrial Runoff using Native Aquatic Plants of Old Brahmaputra River",
                authors = "Dr. Farhana Islam, Tanvir Ahmed, Nahid Hasan",
                department = "Environmental Science & Resource Management",
                journalName = "International Journal of Environmental Tech and Research",
                publicationDate = "November 2024",
                doi = "10.5281/zenodo.jstu.2024.882",
                citationsCount = 29,
                downloadsCount = 890,
                abstractText = "Investigates bio-accumulation rates of Lead and Chromium in Eichhornia crassipes samples collected near Jamalpur industrial zones."
            ),
            ResearchPaper(
                id = "PUB-JSTU-2024-09",
                title = "Deep Learning Architecture for Automated Diagnosis of Crop Disease from Smartphone Imagery",
                authors = "Prof. S. M. Chowdhury, Dr. A. K. Das",
                department = "Information & Communication Technology",
                journalName = "IEEE Transactions on Agri-Computing",
                publicationDate = "September 2024",
                doi = "10.1109/AGRI.2024.3310022",
                citationsCount = 45,
                downloadsCount = 1250,
                abstractText = "Proposes a lightweight Convolutional Neural Network deployed on Android mobile applications enabling offline crop disease detection for local Bangladeshi farmers."
            )
        )
    )
    val papers: StateFlow<List<ResearchPaper>> = _papers.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JstuResearchScreen(
    viewModel: ResearchViewModel = remember { ResearchViewModel() },
    onBackClick: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val allPapers by viewModel.papers.collectAsState()

    val filteredPapers = remember(searchQuery, allPapers) {
        if (searchQuery.isBlank()) allPapers
        else allPapers.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.authors.contains(searchQuery, ignoreCase = true) ||
            it.department.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("JSTU Research Repository", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Open Submit Paper Modal */ }) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Submit Paper")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by paper title, author, or department...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Repository Header Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Column {
                        Text(
                            "Official JSTU Publications Hub",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Open access peer-reviewed research journals & conference proceedings",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPapers) { paper ->
                    ResearchPaperCard(paper = paper)
                }
            }
        }
    }
}

@Composable
fun ResearchPaperCard(paper: ResearchPaper) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        paper.department,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Text(
                    paper.publicationDate,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                paper.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Authors: ${paper.authors}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                paper.abstractText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Citations: ${paper.citationsCount}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Downloads: ${paper.downloadsCount}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { /* Trigger PDF View/Download */ },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF Paper", fontSize = 11.sp)
                }
            }
        }
    }
}
