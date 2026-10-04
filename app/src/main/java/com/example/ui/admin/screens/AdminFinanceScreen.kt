package com.example.ui.admin.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.admin.AdminFinanceInvoice

@Composable
fun AdminFinanceScreen(
    invoices: List<AdminFinanceInvoice>,
    onUpdateInvoiceStatus: (String, String) -> Unit
) {
    val totalCollected = invoices.filter { it.status == "PAID" }.sumOf { it.amount }
    val totalPending = invoices.filter { it.status == "UNPAID" }.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("University Financial Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Total Collected", fontSize = 12.sp, color = Color(0xFF166534))
                    Text("$${totalCollected.toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF166534))
                }
            }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Outstanding Fees", fontSize = 12.sp, color = Color(0xFF991B1B))
                    Text("$${totalPending.toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF991B1B))
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(invoices) { inv ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${inv.invoiceId} • ${inv.studentName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("$${inv.amount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Roll: ${inv.studentRoll} | Dept: ${inv.department}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Category: ${inv.feeCategory}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Due Date: ${inv.dueDate} | Method: ${inv.paymentMethod}", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = inv.status == "PAID",
                                onClick = {},
                                label = { Text(inv.status, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )

                            if (inv.status == "UNPAID") {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(onClick = { onUpdateInvoiceStatus(inv.invoiceId, "WAIVED") }) { Text("Waive Fee", fontSize = 11.sp) }
                                    Button(onClick = { onUpdateInvoiceStatus(inv.invoiceId, "PAID") }) { Text("Mark Paid", fontSize = 11.sp) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
