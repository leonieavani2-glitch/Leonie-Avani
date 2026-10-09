package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PlatformBottomBar
import com.example.ui.components.PlatformTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CustodyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: CustodyViewModel = viewModel()
                val currentTab by viewModel.currentTab.collectAsState()
                val isAdminMode by viewModel.isAdminMode.collectAsState()

                // Hardware back press handler
                BackHandler(enabled = currentTab != "packages") {
                    viewModel.setTab("packages")
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        PlatformTopBar(
                            isAdminMode = isAdminMode,
                            onToggleAdminMode = { viewModel.toggleAdminMode(it) },
                            onOpenProfile = { viewModel.setTab("profile") }
                        )
                    },
                    bottomBar = {
                        PlatformBottomBar(
                            currentTab = currentTab,
                            isAdminMode = isAdminMode,
                            onTabSelected = { viewModel.setTab(it) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            "packages" -> PackagesScreen(
                                viewModel = viewModel,
                                onNavigateToDeposit = { viewModel.setTab("deposit") }
                            )
                            "binance" -> BinanceMarketScreen(viewModel = viewModel)
                            "deposit" -> DepositAndWalletsScreen(viewModel = viewModel)
                            "support" -> CustomerServiceScreen(viewModel = viewModel)
                            "admin" -> AdminBackendScreen(viewModel = viewModel)
                            "profile" -> AuthAndProfileScreen(viewModel = viewModel)
                            else -> PackagesScreen(
                                viewModel = viewModel,
                                onNavigateToDeposit = { viewModel.setTab("deposit") }
                            )
                        }
                    }
                }
            }
        }
    }
}
