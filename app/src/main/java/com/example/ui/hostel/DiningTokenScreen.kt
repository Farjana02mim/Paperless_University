package com.example.ui.hostel

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MealTokenPass(
    val tokenNumber: String = "JSTU-DINING-2025-0842",
    val hallName: String = "Bangabandhu Sheikh Mujibur Rahman Hall",
    val roomNo: String = "408-B",
    val mealType: String = "LUNCH", // "BREAKFAST", "LUNCH", "DINNER"
    val date: String = "08 Feb 2025",
    val menuItems: String = "Steamed Rice, Chicken Curry, Dal, Mixed Vegetable Salad",
    val priceBdt: Double = 45.0,
    val isRedeemed: Boolean = false
)

class DiningTokenViewModel : ViewModel() {
    private val _tokens = MutableStateFlow(
        listOf(
            MealTokenPass(
                tokenNumber = "JSTU-DINING-2025-0842",
                mealType = "LUNCH",
                date = "Today, 08 Feb",
                menuItems = "Steamed Rice, Chicken Roast, Dal, Salad",
                priceBdt = 45.0,
                isRedeemed = false
            ),
            MealTokenPass(
                tokenNumber = "JSTU-DINING-2025-0843",
                mealType = "DINNER",
                date = "Today, 08 Feb",
                menuItems = "Khichuri, Egg Curry, Pickle",
                priceBdt = 40.0,
                isRedeemed = false
            ),
            MealTokenPass(
                tokenNumber = "JSTU-DINING-2025-0820",
                mealType = "BREAKFAST",
                date = "Yesterday, 07 Feb",
                menuItems = "Paratha (2 pcs), Vegetable Bhaji, Egg",
                priceBdt = 25.0,
                isRedeemed = true
            )
        )
    )
    val tokens: StateFlow<List<MealTokenPass>> = _tokens.asStateFlow()

    fun buyNewToken(mealType: String, price: Double) {
        val newToken = MealTokenPass(
            tokenNumber = "JSTU-DINING-2025-${(1000..9999).random()}",
            mealType = mealType,
            date = "Tomorrow",
            menuItems = "Special Meal Menu for $mealType",
            priceBdt = price,
            isRedeemed = false
        )
        _tokens.value = listOf(newToken) + _tokens.value
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiningTokenScreen(
    viewModel: DiningTokenViewModel = remember { DiningTokenViewModel() },
    onBackClick: () -> Unit
) {
    val tokens by viewModel.tokens.collectAsState()
    var showPurchaseDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hall & Dining Token Pass", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showPurchaseDialog = true },
                icon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                text = { Text("Buy Meal Token") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Residential Hall Status Header
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
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
                    }

                    Column {
                        Text(
                            "BSMR Hall - Seat 408-B",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Bangabandhu Sheikh Mujibur Rahman Hall • JSTU",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Active & Past Dining Passes",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(tokens) { token ->
                    DiningTokenCard(token = token)
                }
            }
        }
    }

    if (showPurchaseDialog) {
        AlertDialog(
            onDismissRequest = { showPurchaseDialog = false },
            title = { Text("Buy Dining Token", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select meal type for tomorrow's dining hall service:")
                    Button(
                        onClick = {
                            viewModel.buyNewToken("LUNCH", 45.0)
                            showPurchaseDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Lunch (BDT 45.00)")
                    }
                    Button(
                        onClick = {
                            viewModel.buyNewToken("DINNER", 40.0)
                            showPurchaseDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dinner (BDT 40.00)")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPurchaseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DiningTokenCard(token: MealTokenPass) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
                    shape = RoundedCornerShape(8.dp),
                    color = if (token.isRedeemed) Color(0xFFECEFF1) else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        token.mealType,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = if (token.isRedeemed) Color.Gray else MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    token.date,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                token.menuItems,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Price: BDT ${token.priceBdt}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (token.isRedeemed) {
                    Text("USED / REDEEMED", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Text("Show QR Pass", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
