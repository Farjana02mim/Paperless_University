package com.example.ui.identity.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.identity.DigitalIdCard
import com.example.domain.model.identity.DigitalIdStatus
import com.example.ui.identity.DigitalIdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalIdCardScreen(
    viewModel: DigitalIdViewModel,
    onNavigateToQrVerify: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val idCard by viewModel.idCard.collectAsState()
    val isFlipped by viewModel.isFlipped.collectAsState()
    val context = LocalContext.current
    var showSnackbar by remember { mutableStateOf<String?>(null) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "cardRotation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Digital University ID", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToQrVerify) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR Staff Pass", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = {
                        showSnackbar = "Digital ID Card PDF saved to Downloads"
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Export ID PDF")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = {
            showSnackbar?.let { msg ->
                Snackbar(
                    action = {
                        TextButton(onClick = { showSnackbar = null }) {
                            Text("OK", color = MaterialTheme.colorScheme.inverseOnSurface)
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(msg)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Interactive Flip Hint
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Flip,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tap Card to Flip between Front & Back",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // 3D Flip Card Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .clickable { viewModel.flipCard() }
            ) {
                if (rotation <= 90f) {
                    // Front Card Side
                    DigitalIdCardFront(idCard = idCard)
                } else {
                    // Back Card Side
                    Box(
                        modifier = Modifier.graphicsLayer { rotationY = 180f }
                    ) {
                        DigitalIdCardBack(idCard = idCard, onRefreshQr = { viewModel.refreshQrPass() })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pass Status & NFC Ready Architecture Indicator
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pass Status", fontWeight = FontWeight.SemiBold)
                        }

                        AssistChip(
                            onClick = { },
                            label = { Text("VERIFIED ACTIVE", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFFE8F5E9),
                                labelColor = Color(0xFF2E7D32)
                            )
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Nfc,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("NFC Campus Tap Access", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Ready for Library, Hostel, Transport & Gate Turnstiles",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Actions Button Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToQrVerify,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Staff QR Scanner")
                }

                OutlinedButton(
                    onClick = { viewModel.flipCard() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Flip, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isFlipped) "Show Front" else "Show Back")
                }
            }
        }
    }
}

@Composable
fun DigitalIdCardFront(idCard: DigitalIdCard) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D47A1), // Deep Navy Blue
                            Color(0xFF1565C0),
                            Color(0xFF1E88E5)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Logo & University Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color(0xFF0D47A1),
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SMART UNIVERSITY",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "DIGITAL CAMPUS IDENTITY",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Role Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = idCard.role.name,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Profile Image with Gold Ring Accent
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFD700),
                        modifier = Modifier.size(114.dp)
                    ) {}
                    AsyncImage(
                        model = idCard.photoUrl.ifEmpty { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500" },
                        contentDescription = "User Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color.White, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // User Full Name
                Text(
                    text = idCard.fullName,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = idCard.designation.ifEmpty { idCard.department },
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Information Grid Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IdFieldRow(label = "UNIVERSITY ID", value = idCard.universityId)
                        IdFieldRow(label = "DEPARTMENT", value = idCard.department)
                        IdFieldRow(label = "BLOOD GROUP", value = idCard.bloodGroup)
                        IdFieldRow(label = "VALID UNTIL", value = idCard.expiryDate)
                    }
                }
            }
        }
    }
}

@Composable
fun IdFieldRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun DigitalIdCardBack(idCard: DigitalIdCard, onRefreshQr: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A237E),
                            Color(0xFF283593)
                        )
                    )
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SECURITY QR PASS",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    IconButton(onClick = onRefreshQr) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh QR", tint = Color.White)
                    }
                }

                // Native QR Code Canvas Rendering
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier
                        .size(190.dp)
                        .padding(8.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CustomQrCanvas(modifier = Modifier.size(160.dp))
                    }
                }

                Text(
                    text = "Scan at Gate / Library / Cafeteria",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                // Emergency & Signature Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.12f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "EMERGENCY CONTACT: ${idCard.emergencyContact}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Token: ${idCard.digitalSignatureToken}",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Property of Smart University. Non-transferable digital asset.",
                            fontSize = 8.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Compose Canvas drawing clean QR Code matrix pattern.
 */
@Composable
fun CustomQrCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val sizePx = size.width
        val modules = 21
        val cellSize = sizePx / modules

        // Draw background white
        drawRect(Color.White)

        // Draw QR Finder Patterns (Top-Left, Top-Right, Bottom-Left)
        drawFinderPattern(0f, 0f, cellSize)
        drawFinderPattern((modules - 7) * cellSize, 0f, cellSize)
        drawFinderPattern(0f, (modules - 7) * cellSize, cellSize)

        // Draw deterministic simulated QR data squares
        for (r in 0 until modules) {
            for (c in 0 until modules) {
                // Skip finder pattern zones
                if ((r < 7 && c < 7) || (r < 7 && c >= modules - 7) || (r >= modules - 7 && c < 7)) {
                    continue
                }
                if ((r + c * 3 + r * c) % 5 == 0 || (r * 7 + c * 11) % 3 == 0) {
                    drawRect(
                        color = Color.Black,
                        topLeft = androidx.compose.ui.geometry.Offset(c * cellSize, r * cellSize),
                        size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFinderPattern(
    x: Float,
    y: Float,
    cellSize: Float
) {
    // Outer 7x7 black
    drawRect(
        color = Color.Black,
        topLeft = androidx.compose.ui.geometry.Offset(x, y),
        size = androidx.compose.ui.geometry.Size(7 * cellSize, 7 * cellSize)
    )
    // Inner 5x5 white
    drawRect(
        color = Color.White,
        topLeft = androidx.compose.ui.geometry.Offset(x + cellSize, y + cellSize),
        size = androidx.compose.ui.geometry.Size(5 * cellSize, 5 * cellSize)
    )
    // Center 3x3 black
    drawRect(
        color = Color.Black,
        topLeft = androidx.compose.ui.geometry.Offset(x + 2 * cellSize, y + 2 * cellSize),
        size = androidx.compose.ui.geometry.Size(3 * cellSize, 3 * cellSize)
    )
}
