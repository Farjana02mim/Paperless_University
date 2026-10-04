package com.example.ui.finance

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.finance.InvoiceModel
import com.example.domain.model.finance.PaymentModel
import com.example.domain.model.finance.PaymentStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFinanceDashboardScreen(
    viewModel: StudentFinanceViewModel,
    onNavigateToCheckout: (InvoiceModel) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("University Financial Portal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("One App, One Smart Campus", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToAnalytics,
                        modifier = Modifier.testTag("analytics_nav_button")
                    ) {
                        Icon(Icons.Default.Analytics, contentDescription = "Analytics")
                    }
                    IconButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.testTag("history_nav_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "History")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Hero FinTech Card
                item {
                    FinTechOverviewCard(
                        totalOutstanding = state.totalOutstanding,
                        upcomingDue = state.upcomingDue,
                        paidThisSemester = state.paidThisSemester,
                        scholarship = state.totalScholarship,
                        onPayClick = {
                            state.selectedInvoice?.let { onNavigateToCheckout(it) }
                        }
                    )
                }

                // Unpaid Invoices Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active Invoices", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${state.invoices.count { it.status == PaymentStatus.PENDING }} Pending", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }

                items(state.invoices) { invoice ->
                    InvoiceCard(
                        invoice = invoice,
                        onPayNow = { onNavigateToCheckout(invoice) }
                    )
                }

                // Recent Transactions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Payments", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        TextButton(onClick = onNavigateToHistory) {
                            Text("View All")
                        }
                    }
                }

                if (state.payments.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Text(
                                "No payment history recorded yet.",
                                modifier = Modifier.padding(16.dp),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(state.payments.take(3)) { payment ->
                        PaymentRowItem(payment = payment)
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun FinTechOverviewCard(
    totalOutstanding: Double,
    upcomingDue: Double,
    paidThisSemester: Double,
    scholarship: Double,
    onPayClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0D47A1),
                            Color(0xFF1976D2),
                            Color(0xFF42A5F5)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Due Balance", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    ) {
                        Text("Verified", color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "৳ %.2f".format(totalOutstanding),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Paid This Semester", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text("৳ %.2f".format(paidThisSemester), color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                    Column {
                        Text("Scholarship Benefit", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text("৳ %.2f".format(scholarship), color = Color(0xFFA5D6A7), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onPayClick,
                    enabled = totalOutstanding > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quick_pay_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF0D47A1)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quick Pay Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InvoiceCard(
    invoice: InvoiceModel,
    onPayNow: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (invoice.status == PaymentStatus.SUCCESSFUL)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(invoice.semester, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Invoice #${invoice.invoiceId}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(
                    color = if (invoice.status == PaymentStatus.SUCCESSFUL) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = invoice.status.name,
                        color = if (invoice.status == PaymentStatus.SUCCESSFUL) Color(0xFF2E7D32) else Color(0xFFE65100),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                invoice.feeItems.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.title, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("৳ %.2f".format(item.amount), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            if (invoice.scholarship > 0 || invoice.waiver > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Scholarship & Waiver", fontSize = 12.sp, color = Color(0xFF2E7D32))
                    Text("- ৳ %.2f".format(invoice.scholarship + invoice.waiver), fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Grand Total", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("৳ %.2f".format(invoice.grandTotal), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                if (invoice.status != PaymentStatus.SUCCESSFUL) {
                    Button(
                        onClick = onPayNow,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("pay_invoice_${invoice.invoiceId}")
                    ) {
                        Text("Pay ৳ %.2f".format(invoice.dueAmount))
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentRowItem(payment: PaymentModel) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(payment.paymentDate))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color(0xFF0D47A1),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(payment.paymentGateway.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("TxID: ${payment.transactionId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(dateStr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("৳ %.2f".format(payment.amount), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF2E7D32))
                Text(payment.status.name, fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
