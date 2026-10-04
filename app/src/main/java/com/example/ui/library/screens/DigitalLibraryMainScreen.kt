package com.example.ui.library.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.library.FileType
import com.example.domain.model.library.LibraryResourceModel
import com.example.domain.model.library.ResourceType
import com.example.ui.library.DigitalLibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalLibraryMainScreen(
    viewModel: DigitalLibraryViewModel,
    onResourceClick: (String) -> Unit,
    onReadResourceClick: (String) -> Unit,
    onPlayMediaClick: (String) -> Unit,
    onOpenCloudStorageClick: () -> Unit,
    onOpenDownloadsClick: () -> Unit,
    onOpenAnalyticsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var isGridView by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showUploadModal by remember { mutableStateOf(false) }
    var showRoleMenu by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    val filteredResources = uiState.resources.filter { res ->
        val matchesSearch = uiState.searchQuery.isBlank() ||
                res.title.contains(uiState.searchQuery, ignoreCase = true) ||
                res.author.contains(uiState.searchQuery, ignoreCase = true) ||
                res.courseCode.contains(uiState.searchQuery, ignoreCase = true)

        val matchesDept = uiState.selectedDepartment == null || res.department.equals(uiState.selectedDepartment, ignoreCase = true)
        val matchesType = uiState.selectedResourceType == null || res.resourceType == uiState.selectedResourceType
        val matchesFileType = uiState.selectedFileType == null || res.fileType == uiState.selectedFileType

        matchesSearch && matchesDept && matchesType && matchesFileType
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Enterprise Digital Library", fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        TextButton(onClick = { showRoleMenu = true }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(uiState.userRole, fontWeight = FontWeight.Bold)
                        }
                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            listOf("Student", "Teacher", "Librarian", "Administrator").forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(role) },
                                    onClick = {
                                        viewModel.setUserRole(role)
                                        showRoleMenu = false
                                    }
                                )
                            }
                        }
                    }

                    IconButton(onClick = onOpenCloudStorageClick) {
                        Icon(Icons.Default.CloudQueue, contentDescription = "Cloud Files")
                    }
                    IconButton(onClick = onOpenDownloadsClick) {
                        Icon(Icons.Default.Download, contentDescription = "Downloads")
                    }
                    IconButton(onClick = onOpenAnalyticsClick) {
                        Icon(Icons.Default.Analytics, contentDescription = "Analytics")
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.userRole in listOf("Teacher", "Librarian", "Administrator")) {
                ExtendedFloatingActionButton(
                    onClick = { showUploadModal = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Upload Resource") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar & Filter Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = { Text("Search books, notes, question papers...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                IconButton(
                    onClick = { showFilterSheet = true },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filters",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                IconButton(
                    onClick = { isGridView = !isGridView },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                        contentDescription = "Toggle Grid",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Category Type Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = uiState.selectedResourceType == null,
                        onClick = { viewModel.onResourceTypeSelect(null) },
                        label = { Text("All Resources") }
                    )
                }
                items(ResourceType.entries.toTypedArray()) { type ->
                    FilterChip(
                        selected = uiState.selectedResourceType == type,
                        onClick = { viewModel.onResourceTypeSelect(type) },
                        label = { Text(type.name.replace("_", " ")) }
                    )
                }
            }

            // Main Resources Content
            if (filteredResources.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No academic resources found matching criteria", color = Color.Gray)
                    }
                }
            } else if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredResources) { resource ->
                        LibraryResourceGridCard(
                            resource = resource,
                            onClick = { onResourceClick(resource.resourceId) },
                            onToggleFav = { viewModel.toggleFavorite(resource.resourceId, resource.isFavorite) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredResources) { resource ->
                        LibraryResourceListCard(
                            resource = resource,
                            onClick = { onResourceClick(resource.resourceId) },
                            onRead = {
                                if (resource.fileType == FileType.MP4 || resource.fileType == FileType.MP3) {
                                    onPlayMediaClick(resource.resourceId)
                                } else {
                                    onReadResourceClick(resource.resourceId)
                                }
                            },
                            onDownload = { viewModel.startDownload(resource.resourceId) },
                            onToggleFav = { viewModel.toggleFavorite(resource.resourceId, resource.isFavorite) }
                        )
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Filter Library Resources", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Text("Department", fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Computer Science", "Electrical Engineering", "Business").forEach { dept ->
                        FilterChip(
                            selected = uiState.selectedDepartment == dept,
                            onClick = { viewModel.onDepartmentFilterSelect(dept) },
                            label = { Text(dept) }
                        )
                    }
                }

                Text("File Type", fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(FileType.PDF, FileType.DOCX, FileType.MP4, FileType.MP3).forEach { ft ->
                        FilterChip(
                            selected = uiState.selectedFileType == ft,
                            onClick = { viewModel.onFileTypeSelect(ft) },
                            label = { Text(ft.name) }
                        )
                    }
                }

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Apply Filters")
                }
            }
        }
    }

    if (showUploadModal) {
        UploadResourceDialog(
            onDismiss = { showUploadModal = false },
            onUpload = { res ->
                viewModel.uploadResource(res)
                showUploadModal = false
            }
        )
    }
}

@Composable
fun LibraryResourceListCard(
    resource: LibraryResourceModel,
    onClick: () -> Unit,
    onRead: () -> Unit,
    onDownload: () -> Unit,
    onToggleFav: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (resource.coverImage.isNotBlank()) {
                AsyncImage(
                    model = resource.coverImage,
                    contentDescription = resource.title,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (resource.fileType == FileType.MP4) Icons.Default.VideoLibrary else Icons.Default.Book,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = resource.courseCode,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${resource.rating}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = resource.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "By ${resource.author}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onRead,
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Read / Play", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onDownload,
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                    }

                    IconButton(onClick = onToggleFav, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (resource.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Fav",
                            tint = if (resource.isFavorite) Color(0xFFE91E63) else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryResourceGridCard(
    resource: LibraryResourceModel,
    onClick: () -> Unit,
    onToggleFav: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (resource.coverImage.isNotBlank()) {
                    AsyncImage(
                        model = resource.coverImage,
                        contentDescription = resource.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = resource.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = resource.author,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text(resource.fileType.name, fontSize = 10.sp) },
                    modifier = Modifier.height(28.dp)
                )

                IconButton(onClick = onToggleFav, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (resource.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Fav",
                        tint = if (resource.isFavorite) Color(0xFFE91E63) else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UploadResourceDialog(
    onDismiss: () -> Unit,
    onUpload: (LibraryResourceModel) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("Computer Science") }
    var courseCode by remember { mutableStateOf("CS301") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload Library Resource") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Author / Lecturer") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = courseCode, onValueChange = { courseCode = it }, label = { Text("Course Code (e.g., CS301)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val res = LibraryResourceModel(
                            title = title,
                            author = author,
                            courseCode = courseCode,
                            department = dept,
                            description = desc,
                            uploadedBy = "Teacher Admin",
                            uploadedByRole = "Teacher",
                            fileType = FileType.PDF,
                            resourceType = ResourceType.LECTURE_NOTES,
                            fileUrl = "https://www.w3.org/W3C/DesignIssues/PDF.pdf"
                        )
                        onUpload(res)
                    }
                }
            ) {
                Text("Publish Resource")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
