package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PackageCycle
import com.example.model.PlatformPackage
import com.example.ui.components.BinanceTradingViewChart
import com.example.ui.theme.*
import com.example.viewmodel.CustodyViewModel

@Composable
fun PackagesScreen(
    viewModel: CustodyViewModel,
    onNavigateToDeposit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val packages by viewModel.platformPackages.collectAsState()
    val subscriptions by viewModel.subscriptions.collectAsState()
    val balanceUsd by viewModel.customerBalanceUsd.collectAsState()
    val tickers by viewModel.binanceTickers.collectAsState()
    val context = LocalContext.current

    // Subscribe Dialog State
    var packageToSubscribe by remember { mutableStateOf<PlatformPackage?>(null) }
    var subscribeAmountInput by remember { mutableStateOf("100.0") }

    // Payout Request Dialog State
    var subForPayout by remember { mutableStateOf<String?>(null) }
    var payoutDestWalletInput by remember { mutableStateOf("TJz98YxK4uQpL7vM2eN1wX9aB3c4d5e6f7") }

    // Interactive Profit Calculator State
    var calcAmountInput by remember { mutableStateOf("1000") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VaultDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Customer Balance & Deposit Action Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("balance_overview_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AVAILABLE ACCOUNT BALANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            color = EmeraldSuccessBg.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Active Wallet",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$${String.format("%,.2f", balanceUsd)} USD",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToDeposit,
                            colors = ButtonDefaults.buttonColors(containerColor = CryptoGoldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("deposit_funds_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Deposit Funds", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Select an active package below to withdraw accrued profits", Toast.LENGTH_SHORT).show()
                            },
                            border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ArrowOutward, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Withdraw", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Active Subscriptions Section
        if (subscriptions.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Active Earning Packages",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${subscriptions.size} Running",
                        fontSize = 11.sp,
                        color = CryptoGoldPrimary
                    )
                }
            }

            items(subscriptions) { sub ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("sub_card_${sub.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = sub.packageTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Principal: $${String.format("%,.2f", sub.principalAmountUsd)} USD",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Surface(
                                color = EmeraldSuccessBg.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "EARNING PROFIT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Accrued Profit:", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "+$${String.format("%.2f", sub.totalEarnedUsd)} USD",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                            }

                            Column {
                                Text("Cycle Progress:", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "Day ${sub.daysElapsed} of ${sub.totalDays}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }

                            Button(
                                onClick = {
                                    if (sub.totalEarnedUsd > 0) {
                                        subForPayout = sub.id
                                    } else {
                                        Toast.makeText(context, "Profit is currently accruing. Available at cycle interval.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp).testTag("claim_payout_button_${sub.id}")
                            ) {
                                Text("Request Payout", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Real-Time TradingView Live Binance Market Tracker
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Live Binance Market Chart",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Track live crypto market momentum before choosing a plan",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                BinanceTradingViewChart(tickers = tickers)
            }
        }

        // Section Title: The 3 Core Platform Packages
        item {
            Column {
                Text(
                    text = "Select From 3 Investment Packages",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Daily, Weekly, and Monthly profit cycles tailored to your capital",
                    fontSize = 11.sp,
                    color = CryptoGoldPrimary
                )
            }
        }

        // Render the 3 Packages
        items(packages) { pkg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when (pkg.cycle) {
                        PackageCycle.DAILY -> EmeraldSuccess.copy(alpha = 0.5f)
                        PackageCycle.WEEKLY -> CryptoGoldPrimary.copy(alpha = 0.5f)
                        PackageCycle.MONTHLY -> ElectricCyan.copy(alpha = 0.5f)
                    }
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("package_item_${pkg.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = pkg.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = pkg.durationText,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Surface(
                            color = when (pkg.cycle) {
                                PackageCycle.DAILY -> EmeraldSuccessBg.copy(alpha = 0.4f)
                                PackageCycle.WEEKLY -> AmberWarningBg.copy(alpha = 0.4f)
                                PackageCycle.MONTHLY -> Color(0xFF0C4A6E).copy(alpha = 0.4f)
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = pkg.estimatedProfitRange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = when (pkg.cycle) {
                                    PackageCycle.DAILY -> EmeraldSuccess
                                    PackageCycle.WEEKLY -> CryptoGoldPrimary
                                    PackageCycle.MONTHLY -> ElectricCyan
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = pkg.description,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Payout Frequency & Range Row
                    Surface(
                        color = VaultSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Payout Schedule:", fontSize = 9.sp, color = TextMuted)
                                Text(pkg.payoutFrequency, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Deposit Range:", fontSize = 9.sp, color = TextMuted)
                                Text("$${String.format("%,.0f", pkg.minDepositUsd)} - $${String.format("%,.0f", pkg.maxDepositUsd)} USD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CryptoGoldPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            packageToSubscribe = pkg
                            subscribeAmountInput = pkg.minDepositUsd.toString()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (pkg.cycle) {
                                PackageCycle.DAILY -> EmeraldSuccess
                                PackageCycle.WEEKLY -> CryptoGoldPrimary
                                PackageCycle.MONTHLY -> ElectricCyan
                            }
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("subscribe_pkg_btn_${pkg.id}")
                    ) {
                        Text(
                            text = "Choose ${pkg.title}",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Interactive Profit Calculator
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("profit_calculator_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Estimate Your Earnings",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = calcAmountInput,
                        onValueChange = { calcAmountInput = it },
                        label = { Text("Investment Amount ($)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CryptoGoldPrimary,
                            unfocusedBorderColor = VaultBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    val amount = calcAmountInput.toDoubleOrNull() ?: 1000.0

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Daily (2%):", fontSize = 10.sp, color = TextMuted)
                            Text("+$${String.format("%.2f", amount * 0.02)} /day", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                        }
                        Column {
                            Text("Weekly (15%):", fontSize = 10.sp, color = TextMuted)
                            Text("+$${String.format("%.2f", amount * 0.15)} /week", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CryptoGoldPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Monthly (65%):", fontSize = 10.sp, color = TextMuted)
                            Text("+$${String.format("%.2f", amount * 0.65)} /month", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                        }
                    }
                }
            }
        }
    }

    // Modal to Subscribe to Package
    packageToSubscribe?.let { pkg ->
        AlertDialog(
            onDismissRequest = { packageToSubscribe = null },
            title = {
                Text("Subscribe to ${pkg.title}", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text(
                        text = "Cycle: ${pkg.durationText} • Est. Profit: ${pkg.estimatedProfitRange}",
                        fontSize = 11.sp,
                        color = CryptoGoldPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Available Balance: $${String.format("%,.2f", balanceUsd)} USD",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = subscribeAmountInput,
                        onValueChange = { subscribeAmountInput = it },
                        label = { Text("Amount to Invest ($)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = subscribeAmountInput.toDoubleOrNull() ?: pkg.minDepositUsd
                        if (amt >= pkg.minDepositUsd) {
                            val ok = viewModel.subscribePackage(pkg, amt)
                            if (ok) {
                                Toast.makeText(context, "Successfully joined ${pkg.title}!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Insufficient account balance. Please deposit funds first.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Minimum deposit is $${pkg.minDepositUsd}", Toast.LENGTH_SHORT).show()
                        }
                        packageToSubscribe = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CryptoGoldPrimary)
                ) {
                    Text("Confirm & Start Earning", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { packageToSubscribe = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = VaultSurfaceDark
        )
    }

    // Modal to Request Profit Payout
    subForPayout?.let { subId ->
        AlertDialog(
            onDismissRequest = { subForPayout = null },
            title = {
                Text("Request Profit Payout", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text(
                        text = "Profits are disbursed safely to your personal crypto wallet address.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = payoutDestWalletInput,
                        onValueChange = { payoutDestWalletInput = it },
                        label = { Text("Your Receiving Wallet Address (USDT / Crypto)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ok = viewModel.requestProfitPayout(subId, payoutDestWalletInput)
                        if (ok) {
                            Toast.makeText(context, "Payout request submitted to Admin! Processing shortly.", Toast.LENGTH_LONG).show()
                        }
                        subForPayout = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Text("Submit Payout Request", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { subForPayout = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = VaultSurfaceDark
        )
    }
}
