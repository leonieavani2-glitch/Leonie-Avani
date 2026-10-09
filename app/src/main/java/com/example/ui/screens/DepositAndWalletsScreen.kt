package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QrCodeView
import com.example.ui.theme.*
import com.example.viewmodel.CustodyViewModel

@Composable
fun DepositAndWalletsScreen(
    viewModel: CustodyViewModel,
    modifier: Modifier = Modifier
) {
    val adminWallets by viewModel.adminWallets.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Selected Deposit Asset Tab (0: USDT-TRC20, 1: USDT-ERC20, 2: BTC, 3: ETH)
    var selectedAssetIndex by remember { mutableStateOf(0) }

    val activeAddress = when (selectedAssetIndex) {
        0 -> adminWallets.usdtTrc20
        1 -> adminWallets.usdtErc20
        2 -> adminWallets.btcAddress
        else -> adminWallets.ethAddress
    }

    val activeNetworkLabel = when (selectedAssetIndex) {
        0 -> "USDT (TRON TRC-20)"
        1 -> "USDT (Ethereum ERC-20)"
        2 -> "Bitcoin (BTC SegWit)"
        else -> "Ethereum (ETH ERC-20)"
    }

    // Customer Deposit Proof Submission Form
    var depositAmountInput by remember { mutableStateOf("500") }
    var customerNameInput by remember { mutableStateOf("My Account") }
    var txHashInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VaultDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("deposit_header_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Official Platform Deposit Address",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Transfer crypto directly to the platform's verified wallet",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Asset Selector Chips
                    val assets = listOf("USDT TRC20", "USDT ERC20", "BTC", "ETH")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        assets.forEachIndexed { index, name ->
                            val isSelected = selectedAssetIndex == index
                            Surface(
                                color = if (isSelected) CryptoGoldPrimary else VaultSurfaceElevated,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) CryptoGoldPrimary else VaultBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedAssetIndex = index }
                            ) {
                                Text(
                                    text = name,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) androidx.compose.ui.graphics.Color.Black else TextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Code of the Admin's Receiving Wallet
                    QrCodeView(
                        data = "$activeNetworkLabel:$activeAddress",
                        size = 170.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Address Display Box with Copy
                    Surface(
                        color = VaultSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                clipboardManager.setText(AnnotatedString(activeAddress))
                                Toast.makeText(context, "Wallet address copied!", Toast.LENGTH_SHORT).show()
                            }
                            .testTag("copy_wallet_box")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeNetworkLabel,
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = activeAddress,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CryptoGoldPrimary
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "⚠️ Send only $activeNetworkLabel to this address. Funds will reach the admin safely for package activation.",
                        fontSize = 10.sp,
                        color = AmberWarning,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Section: Submit Deposit Proof / Transaction ID
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("submit_proof_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Submit Deposit Verification (Proof)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "After sending, paste your TX Hash so Admin can verify & credit your account",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customerNameInput,
                        onValueChange = { customerNameInput = it },
                        label = { Text("Your Account Name / ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CryptoGoldPrimary,
                            unfocusedBorderColor = VaultBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = depositAmountInput,
                        onValueChange = { depositAmountInput = it },
                        label = { Text("Amount Deposited ($ USD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CryptoGoldPrimary,
                            unfocusedBorderColor = VaultBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = txHashInput,
                        onValueChange = { txHashInput = it },
                        label = { Text("Transaction Hash (TX ID) or Ref") },
                        placeholder = { Text("e.g. 98f4e2a1b3c7...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CryptoGoldPrimary,
                            unfocusedBorderColor = VaultBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val amt = depositAmountInput.toDoubleOrNull() ?: 100.0
                            if (txHashInput.isNotBlank()) {
                                viewModel.submitDeposit(
                                    customerName = customerNameInput,
                                    amountUsd = amt,
                                    asset = activeNetworkLabel,
                                    network = activeNetworkLabel,
                                    txHash = txHashInput.trim()
                                )
                                txHashInput = ""
                                Toast.makeText(context, "Deposit proof submitted to Admin! Balance will be credited upon verification.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Please enter your Transaction Hash (TX ID)", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CryptoGoldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("submit_deposit_button")
                    ) {
                        Text("Submit for Admin Approval", color = androidx.compose.ui.graphics.Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
