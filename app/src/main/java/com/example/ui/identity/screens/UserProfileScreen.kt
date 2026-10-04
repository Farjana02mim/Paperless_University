package com.example.ui.identity.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.identity.ExpandedUserProfile
import com.example.ui.identity.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel,
    onNavigateToDigitalId: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val updateMessage by viewModel.updateMessage.collectAsState()

    var editedPhone by remember(profile) { mutableStateOf(profile.phone) }
    var editedEmergencyContact by remember(profile) { mutableStateOf(profile.emergencyContact) }
    var editedAddress by remember(profile) { mutableStateOf(profile.address) }
    var editedBio by remember(profile) { mutableStateOf(profile.biography) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Identity", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToDigitalId) {
                        Icon(Icons.Default.Badge, contentDescription = "Digital ID Card", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (isEditing) {
                        viewModel.saveProfile(
                            profile.copy(
                                phone = editedPhone,
                                emergencyContact = editedEmergencyContact,
                                address = editedAddress,
                                biography = editedBio
                            )
                        )
                    } else {
                        viewModel.toggleEditMode(true)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                        contentDescription = null
                    )
                },
                text = { Text(if (isEditing) "Save Profile" else "Edit Profile") },
                containerColor = MaterialTheme.colorScheme.primary
            )
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
            // Profile Photo & Header Section
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = profile.photoUrl.ifEmpty { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500" },
                    contentDescription = "Profile Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )

                IconButton(
                    onClick = {
                        viewModel.updatePhotoUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500")
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = profile.fullName,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "${profile.role.roleName} • ${profile.department}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Digital ID Card Launcher Banner
            Card(
                onClick = onNavigateToDigitalId,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Digital University ID & QR Pass", fontWeight = FontWeight.Bold)
                            Text("Tap to view 3D Card & Security Pass", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Editable or View-Only Fields
            if (isEditing) {
                Text("Editing Personal Details", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editedPhone,
                    onValueChange = { editedPhone = it },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = editedEmergencyContact,
                    onValueChange = { editedEmergencyContact = it },
                    label = { Text("Emergency Contact") },
                    leadingIcon = { Icon(Icons.Default.ContactPhone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = editedAddress,
                    onValueChange = { editedAddress = it },
                    label = { Text("Home Address") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = editedBio,
                    onValueChange = { editedBio = it },
                    label = { Text("Biography") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            } else {
                // Profile Information Cards
                ProfileSectionCard(title = "Academic Information") {
                    ProfileInfoRow(icon = Icons.Default.Numbers, label = "University ID", value = profile.universityId)
                    ProfileInfoRow(icon = Icons.Default.Business, label = "Department", value = profile.department)
                    ProfileInfoRow(icon = Icons.Default.School, label = "Faculty", value = profile.faculty)
                    ProfileInfoRow(icon = Icons.Default.Class, label = "Semester", value = profile.semester)
                    ProfileInfoRow(icon = Icons.Default.CalendarToday, label = "Enrollment Year", value = profile.enrollmentYear)
                }

                Spacer(modifier = Modifier.height(16.dp))

                ProfileSectionCard(title = "Contact & Personal") {
                    ProfileInfoRow(icon = Icons.Default.Email, label = "Email Address", value = profile.email)
                    ProfileInfoRow(icon = Icons.Default.Phone, label = "Phone Number", value = profile.phone)
                    ProfileInfoRow(icon = Icons.Default.ContactPhone, label = "Emergency Contact", value = profile.emergencyContact)
                    ProfileInfoRow(icon = Icons.Default.Bloodtype, label = "Blood Group", value = profile.bloodGroup)
                    ProfileInfoRow(icon = Icons.Default.Home, label = "Address", value = profile.address)
                    ProfileInfoRow(icon = Icons.Default.Public, label = "Nationality", value = profile.nationality)
                }

                Spacer(modifier = Modifier.height(16.dp))

                ProfileSectionCard(title = "Biography & Social Links") {
                    Text(text = profile.biography, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfileInfoRow(icon = Icons.Default.Code, label = "GitHub", value = profile.githubUrl)
                    ProfileInfoRow(icon = Icons.Default.Work, label = "LinkedIn", value = profile.linkedinUrl)
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ProfileSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider()
            content()
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text = value.ifEmpty { "Not specified" },
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
