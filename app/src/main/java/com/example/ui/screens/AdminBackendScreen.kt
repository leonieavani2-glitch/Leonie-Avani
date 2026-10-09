package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.CustodyViewModel

@Composable
fun AdminBackendScreen(
    viewModel: CustodyViewModel,
    modifier: Modifier = Modifier
) {
    val adminWallets by viewModel.adminWallets.collectAsState()
    val depositProofs by viewModel.depositProofs.collectAsState()
    val payoutRequests by viewModel.payoutRequests.collectAsState()
    val supportMessages by viewModel.supportMessages.collectAsState()
    val powerShellLogs by viewModel.powerShellLogs.collectAsState()
    val packages by viewModel.platformPackages.collectAsState()
    val context = LocalContext.current

    // 0: My Wallets, 1: Approve Deposits, 2: Payouts, 3: Support Desk, 4: PowerShell, 5: Package Config
    var activeAdminTab by remember { mutableStateOf(0) }

    // Wallet Inputs
    var usdtTrc20Input by remember { mutableStateOf(adminWallets.usdtTrc20) }
    var usdtErc20Input by remember { mutableStateOf(adminWallets.usdtErc20) }
    var btcInput by remember { mutableStateOf(adminWallets.btcAddress) }
    var ethInput by remember { mutableStateOf(adminWallets.ethAddress) }

    // Keep inputs synced when adminWallets change
    LaunchedEffect(adminWallets) {
        usdtTrc20Input = adminWallets.usdtTrc20
        usdtErc20Input = adminWallets.usdtErc20
        btcInput = adminWallets.btcAddress
        ethInput = adminWallets.ethAddress
    }

    // Admin Chat Reply Input
    var adminReplyText by remember { mutableStateOf("") }

    // PowerShell Input
    var psInput by remember { mutableStateOf("") }

    // Package Edit Dialog
    var editingPackageId by remember { mutableStateOf<String?>(null) }
    var editPkgTitle by remember { mutableStateOf("") }
    var editPkgRange by remember { mutableStateOf("") }
    var editPkgMin by remember { mutableStateOf("") }
    var editPkgMax by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VaultDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, CryptoGoldPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("admin_header_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CryptoGoldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Admin Backend Control Panel",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Surface(
                            color = CryptoGoldPrimary,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Full Access",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Manage your receiving crypto wallets, approve customer deposits, process profit payouts, manage PowerShell, and adjust packages.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Sub Tabs Switcher
        item {
            Surface(
                color = VaultSurfaceDark,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("My Wallets", "Deposits", "Payouts").forEachIndexed { idx, title ->
                            val isSelected = activeAdminTab == idx
                            Surface(
                                color = if (isSelected) CryptoGoldPrimary else Color.Transparent,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { activeAdminTab = idx }
                                    .testTag("admin_subtab_$idx")
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Support Desk", "PowerShell", "Packages").forEachIndexed { idx, title ->
                            val realIdx = idx + 3
                            val isSelected = activeAdminTab == realIdx
                            Surface(
                                color = if (isSelected) CryptoGoldPrimary else Color.Transparent,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { activeAdminTab = realIdx }
                                    .testTag("admin_subtab_$realIdx")
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ================= TAB 0: WALLETS CONFIGURATION =================
        if (activeAdminTab == 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("wallet_config_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Admin Receiving Wallets",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Customers send deposits to these addresses. Replace with your own personal/exchange wallets to receive funds safely.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // USDT TRC20
                        OutlinedTextField(
                            value = usdtTrc20Input,
                            onValueChange = { usdtTrc20Input = it },
                            label = { Text("USDT (TRON TRC-20) Address") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_trc20_address"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CryptoGoldPrimary,
                                unfocusedBorderColor = VaultBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // USDT ERC20
                        OutlinedTextField(
                            value = usdtErc20Input,
                            onValueChange = { usdtErc20Input = it },
                            label = { Text("USDT (Ethereum ERC-20 / BEP-20) Address") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_erc20_address"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CryptoGoldPrimary,
                                unfocusedBorderColor = VaultBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bitcoin (BTC)
                        OutlinedTextField(
                            value = btcInput,
                            onValueChange = { btcInput = it },
                            label = { Text("Bitcoin (BTC Native SegWit) Address") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_btc_address"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CryptoGoldPrimary,
                                unfocusedBorderColor = VaultBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Ethereum (ETH)
                        OutlinedTextField(
                            value = ethInput,
                            onValueChange = { ethInput = it },
                            label = { Text("Ethereum (ETH Mainnet) Address") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_eth_address"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CryptoGoldPrimary,
                                unfocusedBorderColor = VaultBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.updateAdminWallets(
                                    usdtTrc20 = usdtTrc20Input,
                                    usdtErc20 = usdtErc20Input,
                                    btc = btcInput,
                                    eth = ethInput
                                )
                                Toast.makeText(context, "Receiving wallet addresses updated successfully!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CryptoGoldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("save_wallets_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save All Receiving Wallets", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ================= TAB 1: APPROVE DEPOSITS =================
        if (activeAdminTab == 1) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Customer Deposit Verification Queue",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${depositProofs.count { !it.isApproved }} Pending",
                        fontSize = 11.sp,
                        color = AmberWarning
                    )
                }
            }

            items(depositProofs) { dep ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (dep.isApproved) EmeraldSuccess.copy(alpha = 0.5f) else AmberWarning.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("deposit_item_${dep.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dep.customerName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${dep.id} • ${dep.timestamp}",
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                            }

                            Text(
                                text = "+$${String.format("%,.2f", dep.amountUsd)} USD",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldSuccess
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = VaultSurfaceElevated,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Asset / Network: ${dep.assetSymbol} (${dep.targetNetwork})",
                                    fontSize = 10.sp,
                                    color = CryptoGoldPrimary
                                )
                                Text(
                                    text = "Tx Hash / Proof: ${dep.txHashOrProof}",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (!dep.isApproved) {
                            Button(
                                onClick = {
                                    viewModel.approveDeposit(dep.id)
                                    Toast.makeText(context, "Deposit approved! $${dep.amountUsd} credited.", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth().testTag("approve_button_${dep.id}")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Approve & Credit Balance", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Surface(
                                color = EmeraldSuccessBg.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "✓ Deposit Approved & Funds Credited",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ================= TAB 2: PROFIT PAYOUTS QUEUE =================
        if (activeAdminTab == 2) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Customer Profit Payout Requests",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${payoutRequests.count { !it.isPaidOut }} Pending Payout",
                        fontSize = 11.sp,
                        color = AmberWarning
                    )
                }
            }

            items(payoutRequests) { pay ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (pay.isPaidOut) EmeraldSuccess.copy(alpha = 0.5f) else AmberWarning.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("payout_item_${pay.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = pay.customerName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Plan: ${pay.packageTitle}",
                                    fontSize = 10.sp,
                                    color = CryptoGoldPrimary
                                )
                            }

                            Text(
                                text = "$${String.format("%,.2f", pay.amountUsd)} USD",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = VaultSurfaceElevated,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Destination Wallet:",
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = pay.destinationWallet,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (!pay.isPaidOut) {
                            Button(
                                onClick = {
                                    viewModel.completePayout(pay.id)
                                    Toast.makeText(context, "Payout ${pay.id} marked as disbursed!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CryptoGoldPrimary),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth().testTag("disburse_button_${pay.id}")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Disburse Profit & Mark Paid", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Surface(
                                color = EmeraldSuccessBg.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "✓ Profit Payout Completed Successfully",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ================= TAB 3: ADMIN CUSTOMER SERVICE DESK =================
        if (activeAdminTab == 3) {
            item {
                Text(
                    text = "Customer Service Messages Inbox",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            items(supportMessages) { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = msg.senderName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (msg.isAdmin) CryptoGoldPrimary else ElectricCyan
                            )
                            Text(msg.timestamp, fontSize = 9.sp, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(msg.messageText, fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = adminReplyText,
                        onValueChange = { adminReplyText = it },
                        label = { Text("Reply as Admin Support Desk...") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (adminReplyText.isNotBlank()) {
                                viewModel.sendSupportMessage(adminReplyText.trim(), isAdmin = true)
                                adminReplyText = ""
                                Toast.makeText(context, "Reply sent to customer!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CryptoGoldPrimary)
                    ) {
                        Text("Reply", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ================= TAB 4: INTERACTIVE POWERSHELL CLI =================
        if (activeAdminTab == 4) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF050811)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("powershell_console_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(10.dp).clip(CircleShape).background(ElectricCyan)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Windows PowerShell — Binance Custody CLI",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan
                                )
                            }
                            Text(
                                text = "v2.4 Online",
                                fontSize = 9.sp,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Command Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("status", "packages", "wallets", "deposits", "payouts", "help").forEach { cmd ->
                                Surface(
                                    color = VaultSurfaceElevated,
                                    shape = RoundedCornerShape(4.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                                    modifier = Modifier.clickable {
                                        viewModel.runPowerShellCommand(cmd)
                                    }
                                ) {
                                    Text(
                                        text = cmd,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = CryptoGoldPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Terminal output logs
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 160.dp, max = 320.dp)
                                .background(Color(0xFF080C16))
                                .border(1.dp, Color(0xFF141F36), RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            powerShellLogs.takeLast(10).forEach { log ->
                                Text(
                                    text = log.command,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = log.output,
                                    fontSize = 10.sp,
                                    color = Color(0xFFCCD6E0),
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 14.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Command Input Field
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = psInput,
                                onValueChange = { psInput = it },
                                placeholder = {
                                    Text("Type command (e.g. status, approve DEP-502, signal BTC BUY)", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextMuted)
                                },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("powershell_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = VaultBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    if (psInput.isNotBlank()) {
                                        viewModel.runPowerShellCommand(psInput)
                                        psInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.testTag("run_powershell_button")
                            ) {
                                Text("Run", color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }

        // ================= TAB 5: PACKAGE CONFIGURATION =================
        if (activeAdminTab == 5) {
            item {
                Text(
                    text = "Edit Platform Investment Packages",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            items(packages) { pkg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_package_item_${pkg.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(pkg.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(pkg.durationText, fontSize = 10.sp, color = TextMuted)
                            }
                            Surface(
                                color = CryptoGoldPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = pkg.estimatedProfitRange,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CryptoGoldPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Deposit Range: $${pkg.minDepositUsd} - $${pkg.maxDepositUsd} USD", fontSize = 10.sp, color = TextSecondary)
                            Text("Payout: ${pkg.payoutFrequency}", fontSize = 10.sp, color = EmeraldSuccess)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                editingPackageId = pkg.id
                                editPkgTitle = pkg.title
                                editPkgRange = pkg.estimatedProfitRange
                                editPkgMin = pkg.minDepositUsd.toString()
                                editPkgMax = pkg.maxDepositUsd.toString()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VaultSurfaceElevated),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = CryptoGoldPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Rename or Change Profit Rates", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal: Edit Package Dialog
    editingPackageId?.let { pkgId ->
        AlertDialog(
            onDismissRequest = { editingPackageId = null },
            title = { Text("Edit Package Details", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editPkgTitle,
                        onValueChange = { editPkgTitle = it },
                        label = { Text("Package Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPkgRange,
                        onValueChange = { editPkgRange = it },
                        label = { Text("Return Range (e.g. 1.5% - 2.5% Daily)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPkgMin,
                        onValueChange = { editPkgMin = it },
                        label = { Text("Min Deposit ($ USD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPkgMax,
                        onValueChange = { editPkgMax = it },
                        label = { Text("Max Deposit ($ USD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val minD = editPkgMin.toDoubleOrNull() ?: 100.0
                        val maxD = editPkgMax.toDoubleOrNull() ?: 1000.0
                        viewModel.updatePackage(pkgId, editPkgTitle, editPkgRange, minD, maxD)
                        Toast.makeText(context, "Package updated successfully!", Toast.LENGTH_SHORT).show()
                        editingPackageId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CryptoGoldPrimary)
                ) {
                    Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingPackageId = null }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = VaultSurfaceDark
        )
    }
}
