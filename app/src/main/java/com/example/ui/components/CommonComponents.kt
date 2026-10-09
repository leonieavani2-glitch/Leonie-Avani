package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.ui.theme.*

@Composable
fun PlatformTopBar(
    isAdminMode: Boolean,
    onToggleAdminMode: (Boolean) -> Unit,
    onOpenProfile: () -> Unit = {}
) {
    Surface(
        color = VaultSurfaceDark,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Platform Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("app_brand_header")
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CryptoGoldDark, CryptoGoldPrimary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CurrencyBitcoin,
                        contentDescription = "Platform Emblem",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "BINANCE",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PROFIT DESK",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CryptoGoldPrimary
                        )
                    }
                    Text(
                        text = if (isAdminMode) "Backend Admin Mode (Full Access)" else "Daily • Weekly • Monthly Packages",
                        fontSize = 10.sp,
                        color = if (isAdminMode) CryptoGoldPrimary else TextMuted
                    )
                }
            }

            // Actions: User Profile / Login and Mode Switcher
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Profile & Web Portal Quick Access
                IconButton(
                    onClick = onOpenProfile,
                    modifier = Modifier.size(34.dp).testTag("quick_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "User Profile & Portal",
                        tint = CryptoGoldPrimary
                    )
                }

                // Mode Switcher Pill (Customer Mode vs Backend Admin Mode)
                Button(
                    onClick = { onToggleAdminMode(!isAdminMode) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAdminMode) CryptoGoldPrimary else VaultSurfaceElevated
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isAdminMode) CryptoGoldPrimary else VaultBorder),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp).testTag("mode_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isAdminMode) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                        contentDescription = null,
                        tint = if (isAdminMode) Color.Black else TextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAdminMode) "Backend Admin" else "Customer View",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAdminMode) Color.Black else TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun PlatformBottomBar(
    currentTab: String,
    isAdminMode: Boolean,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = VaultSurfaceDark,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars,
        modifier = Modifier.testTag("institutional_navigation_bar")
    ) {
        // Tab 1: 3 Packages
        NavigationBarItem(
            selected = currentTab == "packages",
            onClick = { onTabSelected("packages") },
            icon = {
                Icon(
                    imageVector = if (currentTab == "packages") Icons.Default.CardGiftcard else Icons.Outlined.CardGiftcard,
                    contentDescription = "3 Packages"
                )
            },
            label = { Text("3 Packages", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = CryptoGoldPrimary,
                indicatorColor = CryptoGoldPrimary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_packages")
        )

        // Tab 2: Binance Live
        NavigationBarItem(
            selected = currentTab == "binance",
            onClick = { onTabSelected("binance") },
            icon = {
                Icon(
                    imageVector = if (currentTab == "binance") Icons.Default.TrendingUp else Icons.Outlined.TrendingUp,
                    contentDescription = "Binance Live"
                )
            },
            label = { Text("Binance Live", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = CryptoGoldPrimary,
                indicatorColor = CryptoGoldPrimary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_binance")
        )

        // Tab 3: Deposit
        NavigationBarItem(
            selected = currentTab == "deposit",
            onClick = { onTabSelected("deposit") },
            icon = {
                Icon(
                    imageVector = if (currentTab == "deposit") Icons.Default.AddCircle else Icons.Outlined.AddCircle,
                    contentDescription = "Deposit"
                )
            },
            label = { Text("Deposit", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = CryptoGoldPrimary,
                indicatorColor = CryptoGoldPrimary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_deposit")
        )

        // Tab 4: Customer Service
        NavigationBarItem(
            selected = currentTab == "support",
            onClick = { onTabSelected("support") },
            icon = {
                Icon(
                    imageVector = if (currentTab == "support") Icons.Default.Headphones else Icons.Outlined.Headphones,
                    contentDescription = "Customer Service"
                )
            },
            label = { Text("Support", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = CryptoGoldPrimary,
                indicatorColor = CryptoGoldPrimary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_support")
        )

        if (isAdminMode) {
            // Tab 5: Admin Backend
            NavigationBarItem(
                selected = currentTab == "admin",
                onClick = { onTabSelected("admin") },
                icon = {
                    Icon(
                        imageVector = if (currentTab == "admin") Icons.Default.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                        contentDescription = "Admin Backend"
                    )
                },
                label = { Text("Backend", fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = CryptoGoldPrimary,
                    indicatorColor = CryptoGoldPrimary,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_tab_admin")
            )
        } else {
            // Tab 5: User Account & Web Portal
            NavigationBarItem(
                selected = currentTab == "profile",
                onClick = { onTabSelected("profile") },
                icon = {
                    Icon(
                        imageVector = if (currentTab == "profile") Icons.Default.AccountCircle else Icons.Outlined.AccountCircle,
                        contentDescription = "Account & Web"
                    )
                },
                label = { Text("Portal", fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = CryptoGoldPrimary,
                    indicatorColor = CryptoGoldPrimary,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_tab_profile")
            )
        }
    }
}
