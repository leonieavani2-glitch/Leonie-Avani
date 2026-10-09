package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CustodyRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustodyViewModel(
    private val repository: CustodyRepository = CustodyRepository()
) : ViewModel() {

    // Tab Navigation: "packages", "binance", "deposit", "support", "admin", "profile"
    private val _currentTab = MutableStateFlow("packages")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Mode Toggle: Customer View vs Admin/Backend View
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    // The 3 Packages & Subscriptions
    val platformPackages = repository.platformPackages
    val subscriptions = repository.subscriptions
    val customerBalanceUsd = repository.customerBalanceUsd

    // User Authentication
    val currentUser = repository.currentUser

    // Admin Configured Wallets
    val adminWallets = repository.adminWallets

    // Admin Verification Queues
    val depositProofs = repository.depositProofs
    val payoutRequests = repository.payoutRequests

    // Customer Service Chat
    val supportMessages = repository.supportMessages

    // Binance Live Market & Working Orders
    val binanceTickers = repository.binanceTickers
    val recentOrders = repository.recentOrders

    // Live Trading Signals & Live Chat
    val tradingRoomMessages = repository.tradingRoomMessages

    // Admin PowerShell Engine Logs
    val powerShellLogs = repository.powerShellLogs

    init {
        viewModelScope.launch {
            repository.refreshTickers()
        }
    }

    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    fun toggleAdminMode(enabled: Boolean) {
        _isAdminMode.value = enabled
        if (enabled) {
            _currentTab.value = "admin"
        } else {
            _currentTab.value = "packages"
        }
    }

    fun login(email: String) {
        repository.login(email)
    }

    fun register(name: String, email: String, phone: String) {
        repository.register(name, email, phone)
    }

    fun logout() {
        repository.logout()
    }

    fun executeBinanceTrade(symbol: String, side: String, amountUsd: Double, currentPrice: Double): Boolean {
        return repository.executeBinanceTrade(symbol, side, amountUsd, currentPrice)
    }

    fun broadcastTradingSignal(symbol: String, action: String, entry: Double, target: Double, sl: Double, comment: String) {
        repository.broadcastTradingSignal(symbol, action, entry, target, sl, comment)
    }

    fun sendTradingChatMessage(message: String, isTraderPro: Boolean) {
        val name = if (isTraderPro) "Master Trader (Admin)" else repository.currentUser.value.fullName
        repository.sendTradingChatMessage(name, isTraderPro, message)
    }

    fun copyTrade(signalId: String, amountUsd: Double): Boolean {
        return repository.copyTrade(signalId, amountUsd)
    }

    fun runPowerShellCommand(command: String): String {
        return repository.runPowerShellCommand(command)
    }

    fun updatePackage(id: String, newTitle: String, newRange: String, minUsd: Double, maxUsd: Double) {
        repository.updatePackage(id, newTitle, newRange, minUsd, maxUsd)
    }

    fun updateAdminWallets(usdtTrc20: String, usdtErc20: String, btc: String, eth: String) {
        repository.updateAdminWallets(usdtTrc20, usdtErc20, btc, eth)
    }

    fun submitDeposit(customerName: String, amountUsd: Double, asset: String, network: String, txHash: String) {
        repository.submitDepositProof(customerName, amountUsd, asset, network, txHash)
    }

    fun approveDeposit(depositId: String) {
        repository.approveDeposit(depositId)
    }

    fun completePayout(payoutId: String) {
        repository.completePayout(payoutId)
    }

    fun subscribePackage(pkg: PlatformPackage, amountUsd: Double): Boolean {
        return repository.subscribePackage(pkg, amountUsd)
    }

    fun requestProfitPayout(subId: String, destWallet: String): Boolean {
        return repository.requestProfitPayout(subId, destWallet)
    }

    fun sendSupportMessage(text: String, isAdmin: Boolean) {
        val name = if (isAdmin) "Admin Support Desk" else repository.currentUser.value.fullName
        repository.sendSupportMessage(name, isAdmin, text)
    }

    fun refreshBinance() {
        viewModelScope.launch {
            repository.refreshTickers()
        }
    }
}
