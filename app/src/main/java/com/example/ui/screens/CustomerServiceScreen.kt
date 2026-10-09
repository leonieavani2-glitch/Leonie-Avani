package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.CustodyViewModel

@Composable
fun CustomerServiceScreen(
    viewModel: CustodyViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.supportMessages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VaultDarkBg)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Support Header Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().testTag("support_header_card")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CryptoGoldPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "Support",
                        tint = CryptoGoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Customer Service & Help Desk",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Direct assistance with packages, deposits & profit payouts",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Messages Thread
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isSupport = msg.isAdmin
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isSupport) Alignment.Start else Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (isSupport) Arrangement.Start else Arrangement.End
                    ) {
                        Text(
                            text = msg.senderName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSupport) CryptoGoldPrimary else ElectricCyan
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = msg.timestamp,
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = if (isSupport) VaultSurfaceElevated else CryptoGoldPrimary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSupport) VaultBorder else CryptoGoldPrimary.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isSupport) 2.dp else 12.dp,
                            bottomEnd = if (isSupport) 12.dp else 2.dp
                        ),
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Text(
                            text = msg.messageText,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Write your message to Customer Service...") },
                singleLine = true,
                modifier = Modifier.weight(1f).testTag("customer_chat_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CryptoGoldPrimary,
                    unfocusedBorderColor = VaultBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendSupportMessage(inputText.trim(), isAdmin = false)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CryptoGoldPrimary)
                    .testTag("send_support_message_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
            }
        }
    }
}
