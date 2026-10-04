package com.example.ui.library.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.library.CloudFile
import com.example.domain.model.library.CloudFolder
import com.example.domain.model.library.FileType
import com.example.domain.model.library.FolderType
import com.example.ui.library.CloudFileManagerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudFileManagerScreen(
    viewModel: CloudFileManagerViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showUploadFileDialog by remember { mutableStateOf(false) }
    var folderNameInput by remember { mutableStateOf("") }
    var fileNameInput by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Cloud Storage & Files", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateFolderDialog = true }) {
                        Icon(Icons.Default.CreateNewFolder, contentDescription = "New Folder")
                    }
                    IconButton(onClick = { showUploadFileDialog = true }) {
                        Icon(Icons.Default.UploadFile, contentDescription = "Upload File")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showUploadFileDialog = true },
                icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) },
                text = { Text("Upload File") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Storage Usage Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cloud Quota", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Text("${uiState.storageUsage.usedBytes / (1024 * 1024 * 1024)} GB / ${uiState.storageUsage.totalQuotaBytes / (1024 * 1024 * 1024)} GB", style = MaterialTheme.typography.labelSmall)
                    }

                    LinearProgressIndicator(
                        progress = { uiState.storageUsage.usedBytes.toFloat() / uiState.storageUsage.totalQuotaBytes.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Docs: ${uiState.storageUsage.documentsUsedBytes / (1024 * 1024)} MB", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("Media: ${uiState.storageUsage.mediaUsedBytes / (1024 * 1024)} MB", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("Archives: ${uiState.storageUsage.archivesUsedBytes / (1024 * 1024)} MB", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }

            // Category Folder Tabs
            ScrollableTabRow(
                selectedTabIndex = FolderType.entries.indexOf(uiState.currentFolderType),
                edgePadding = 0.dp,
                divider = {}
            ) {
                FolderType.entries.forEach { type ->
                    Tab(
                        selected = uiState.currentFolderType == type,
                        onClick = { viewModel.selectFolderType(type) },
                        text = {
                            Text(
                                text = type.name.replace("_", " "),
                                fontWeight = if (uiState.currentFolderType == type) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Folder Breadcrumbs Navigation Bar
            if (uiState.folderBreadcrumbs.isNotEmpty()) {
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Up", modifier = Modifier.size(18.dp))
                        }
                    }
                    items(uiState.folderBreadcrumbs) { crumb ->
                        Text(" / ${crumb.name}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Subfolders
                items(uiState.folders) { folder ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openFolder(folder) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(folder.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text(folder.department.ifBlank { "Folder" }, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }

                            IconButton(onClick = { viewModel.deleteFolder(folder.folderId) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                            }
                        }
                    }
                }

                // Files
                items(uiState.files) { file ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (file.fileType) {
                                        FileType.PDF -> Icons.Default.PictureAsPdf
                                        FileType.MP4 -> Icons.Default.VideoFile
                                        FileType.MP3 -> Icons.Default.AudioFile
                                        else -> Icons.Default.InsertDriveFile
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(file.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                                    Text("${file.fileType.name} • ${file.fileSize / (1024 * 1024)} MB", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }

                            Row {
                                IconButton(onClick = { /* Download */ }) {
                                    Icon(Icons.Default.Download, contentDescription = "Download")
                                }
                                IconButton(onClick = { viewModel.deleteFile(file.fileId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateFolderDialog) {
        AlertDialog(
            onDismissRequest = { showCreateFolderDialog = false },
            title = { Text("Create New Folder") },
            text = {
                OutlinedTextField(
                    value = folderNameInput,
                    onValueChange = { folderNameInput = it },
                    label = { Text("Folder Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createNewFolder(folderNameInput)
                        folderNameInput = ""
                        showCreateFolderDialog = false
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateFolderDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showUploadFileDialog) {
        AlertDialog(
            onDismissRequest = { showUploadFileDialog = false },
            title = { Text("Upload File to Cloud") },
            text = {
                OutlinedTextField(
                    value = fileNameInput,
                    onValueChange = { fileNameInput = it },
                    label = { Text("Document Title / File Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fileNameInput.isNotBlank()) {
                            viewModel.uploadFile(fileNameInput, FileType.PDF)
                            fileNameInput = ""
                            showUploadFileDialog = false
                        }
                    }
                ) {
                    Text("Upload")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadFileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
