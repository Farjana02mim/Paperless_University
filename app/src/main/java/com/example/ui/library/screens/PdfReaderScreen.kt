package com.example.ui.library.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.library.PdfReaderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfReaderScreen(
    viewModel: PdfReaderViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBookmarkDialog by remember { mutableStateOf(false) }
    var bookmarkNote by remember { mutableStateOf("") }
    var scale by remember { mutableFloatStateOf(1f) }

    val transformState = rememberTransformableState { zoomChange, _, _ ->
        scale = (scale * zoomChange).coerceIn(0.75f, 3f)
    }

    val backgroundColor = if (uiState.isNightMode) Color(0xFF121212) else Color(0xFFF8F9FA)
    val textColor = if (uiState.isNightMode) Color(0xFFE0E0E0) else Color(0xFF212121)
    val cardBg = if (uiState.isNightMode) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.resource?.title ?: "Document Reader",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "Page ${uiState.currentPage} of ${uiState.totalPages}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleNightMode() }) {
                        Icon(
                            imageVector = if (uiState.isNightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Night Mode"
                        )
                    }
                    IconButton(onClick = { viewModel.toggleScrollMode() }) {
                        Icon(
                            imageVector = if (uiState.isHorizontalMode) Icons.Default.SwapHoriz else Icons.Default.SwapVert,
                            contentDescription = "Toggle Scroll Mode"
                        )
                    }
                    IconButton(onClick = { showBookmarkDialog = true }) {
                        Icon(Icons.Default.BookmarkAdd, contentDescription = "Add Bookmark")
                    }
                    IconButton(onClick = { viewModel.toggleBookmarkDrawer(!uiState.isBookmarkDrawerOpen) }) {
                        Icon(Icons.Default.Bookmarks, contentDescription = "Bookmarks List")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (uiState.isNightMode) Color(0xFF1E1E1E) else MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                color = if (uiState.isNightMode) Color(0xFF1E1E1E) else MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { viewModel.onPageChanged(uiState.currentPage - 1) },
                            enabled = uiState.currentPage > 1
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Page")
                        }

                        Slider(
                            value = uiState.currentPage.toFloat(),
                            onValueChange = { viewModel.onPageChanged(it.toInt()) },
                            valueRange = 1f..uiState.totalPages.toFloat().coerceAtLeast(1f),
                            steps = (uiState.totalPages - 2).coerceAtLeast(0),
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { viewModel.onPageChanged(uiState.currentPage + 1) },
                            enabled = uiState.currentPage < uiState.totalPages
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Page")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.zoomOut() }) {
                            Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out")
                        }
                        Text(
                            text = "${(uiState.zoomLevel * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(onClick = { viewModel.zoomIn() }) {
                            Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor)
        ) {
            // PDF Page Canvas Viewer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .transformable(state = transformState)
                    .graphicsLayer(
                        scaleX = uiState.zoomLevel * scale,
                        scaleY = uiState.zoomLevel * scale
                    ),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .fillMaxHeight(0.88f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = uiState.resource?.courseCode ?: "CS301",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Page ${uiState.currentPage}",
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = 0.6f)
                            )
                        }

                        // Simulated Academic Page Content
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Chapter ${((uiState.currentPage - 1) / 10) + 1}: Core Academic Concepts & Applications",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                            HorizontalDivider(color = textColor.copy(alpha = 0.2f))

                            Text(
                                text = "1. Introduction and Architectural Overview\n\n" +
                                        "In this section, we analyze the fundamentals of enterprise software architecture, data modeling, relational constraints, and scalable cloud synchronization systems.\n\n" +
                                        "2. Mathematical Formulations & Theorem Proofs\n\n" +
                                        "Let S be the set of valid database transactions. For every transaction T in S, the ACID properties guarantee that concurrency control protocol P maintains consistency state C_0 -> C_final.",
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 20.sp,
                                color = textColor
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (uiState.isNightMode) Color(0xFF2C2C2C) else Color(0xFFE8F0FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AutoGraph, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Figure ${uiState.currentPage}.1: Architectural Workflow & Data Flow Diagram",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColor
                                    )
                                }
                            }
                        }

                        // Footer
                        Text(
                            text = "Smart University Enterprise Digital Library • Strictly for Academic Use",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = textColor.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            // Bookmarks Drawer
            AnimatedVisibility(
                visible = uiState.isBookmarkDrawerOpen,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(280.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 12.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Bookmarks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { viewModel.toggleBookmarkDrawer(false) }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        if (uiState.bookmarks.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No bookmarks added yet.", style = MaterialTheme.typography.bodySmall)
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(uiState.bookmarks) { bookmark ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.onPageChanged(bookmark.pageNumber)
                                                viewModel.toggleBookmarkDrawer(false)
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Page ${bookmark.pageNumber}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                                Text(bookmark.note, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                            }
                                            IconButton(onClick = { viewModel.deleteBookmark(bookmark.bookmarkId) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBookmarkDialog) {
        AlertDialog(
            onDismissRequest = { showBookmarkDialog = false },
            title = { Text("Add Bookmark on Page ${uiState.currentPage}") },
            text = {
                OutlinedTextField(
                    value = bookmarkNote,
                    onValueChange = { bookmarkNote = it },
                    label = { Text("Bookmark Note (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addBookmark(bookmarkNote)
                        bookmarkNote = ""
                        showBookmarkDialog = false
                    }
                ) {
                    Text("Save Bookmark")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBookmarkDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
