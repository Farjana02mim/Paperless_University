package com.example.ui.admin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.admin.*
import com.example.ui.admin.AdminModalType

@Composable
fun AdminUserManagementScreen(
    users: List<AdminUserItem>,
    roles: List<RolePermission>,
    searchQuery: String,
    roleFilter: String,
    deptFilter: String,
    selectedUser: AdminUserItem?,
    onSearchChange: (String) -> Unit,
    onFilterChange: (String, String) -> Unit,
    onSelectUser: (AdminUserItem?) -> Unit,
    onToggleStatus: (String, UserStatus) -> Unit,
    onDeleteUser: (String) -> Unit,
    onOpenModal: (AdminModalType) -> Unit
) {
    var viewSubTab by remember { mutableIntStateOf(0) } // 0: Users Directory, 1: Roles & Permissions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("User & Security Access Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(
                onClick = { onOpenModal(AdminModalType.CREATE_USER) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Provision User")
            }
        }

        TabRow(selectedTabIndex = viewSubTab) {
            Tab(selected = viewSubTab == 0, onClick = { viewSubTab = 0 }) {
                Text("User Roster (${users.size})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            Tab(selected = viewSubTab == 1, onClick = { viewSubTab = 1 }) {
                Text("Role & Permission Matrix", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }

        if (viewSubTab == 0) {
            // Search & Filters
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search by name, ID, or email...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("All", "STUDENT", "TEACHER", "ADMIN", "FINANCE_OFFICER").forEach { r ->
                    FilterChip(
                        selected = roleFilter == r,
                        onClick = { onFilterChange(r, deptFilter) },
                        label = { Text(if (r == "All") "All Roles" else r.take(7), fontSize = 11.sp) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(users) { user ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth().clickable { onSelectUser(user) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(user.name.take(1), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("${user.userId} • ${user.role.name}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    Text("${user.email} | ${user.department}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FilterChip(
                                    selected = user.status == UserStatus.ACTIVE,
                                    onClick = { onToggleStatus(user.userId, user.status) },
                                    label = { Text(user.status.name, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFDCFCE7),
                                        selectedLabelColor = Color(0xFF166534)
                                    )
                                )

                                IconButton(onClick = { onDeleteUser(user.userId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Permission Matrix Subtab
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(roles) { roleItem ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(roleItem.roleName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("${roleItem.userCount} Assigned Users", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Granted Permissions:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                roleItem.permissions.forEach { perm ->
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(perm, fontSize = 10.sp) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedUser != null) {
        AlertDialog(
            onDismissRequest = { onSelectUser(null) },
            confirmButton = {
                Button(onClick = { onSelectUser(null) }) { Text("Close User Card") }
            },
            title = { Text(selectedUser.name, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("User ID: ${selectedUser.userId}", fontSize = 13.sp)
                    Text("Role: ${selectedUser.role.name}", fontSize = 13.sp)
                    Text("Email: ${selectedUser.email}", fontSize = 13.sp)
                    Text("Department: ${selectedUser.department}", fontSize = 13.sp)
                    Text("Phone: ${selectedUser.phone}", fontSize = 13.sp)
                    Text("Account Status: ${selectedUser.status.name}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Permissions: ${selectedUser.permissions.joinToString()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}
