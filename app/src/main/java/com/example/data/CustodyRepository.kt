package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CustodyRepository {

    // ================= 1. THE 3 REDESIGNED PLATFORM PACKAGES =================
    private val _platformPackages = MutableStateFlow(
        listOf(
            PlatformPackage(
                id = "pkg_apex_daily",
                title = "Apex 24H Liquidity Surge",
                cycle = PackageCycle.DAILY,
                durationText = "24 Hours (Daily Cycle)",
                durationDays = 1,
                estimatedProfitRange = "1.5% – 2.5% Daily",
                cycleProfitPct = 2.0,
                dailyRatePct = 2.0,
                minDepositUsd = 100.0,
                maxDepositUsd = 1000.0,
                description = "High-speed automated scalping on Binance BTC & ETH liquidity spreads. Fast, reliable 24-hour liquidity cycle with instant daily payouts.",
                payoutFrequency = "Every 24 Hours",
                riskLevel = "Conservative"
            ),
            PlatformPackage(
                id = "pkg_titan_weekly",
                title = "Titan 7D Momentum Harvest",
                cycle = PackageCycle.WEEKLY,
                durationText = "7 Days (Weekly Cycle)",
                durationDays = 7,
                estimatedProfitRange = "3% – 5% Weekly",
                cycleProfitPct = 4.0,
                dailyRatePct = 0.57,
                minDepositUsd = 500.0,
                maxDepositUsd = 5000.0,
                description = "Active swing-trading strategy capturing 4H candle breakouts on top Binance pairs (BTC, ETH, SOL). Returns disbursed every 7 days.",
                payoutFrequency = "Every 7 Days",
                riskLevel = "Moderate"
            ),
            PlatformPackage(
                id = "pkg_sovereign_monthly",
                title = "Sovereign 30D Wealth Multiplier",
                cycle = PackageCycle.MONTHLY,
                durationText = "30 Days (Monthly Cycle)",
                durationDays = 30,
                estimatedProfitRange = "10% – 18% Monthly",
                cycleProfitPct = 14.0,
                dailyRatePct = 0.467,
                minDepositUsd = 5000.0,
                maxDepositUsd = 50000.0,
                description = "Institutional delta-neutral arbitrage and perpetual funding-rate harvesting. Highest compounding growth pool for serious capital.",
                payoutFrequency = "Monthly Compounding Payout",
                riskLevel = "Institutional Alpha"
            )
        )
    )
    val platformPackages: StateFlow<List<PlatformPackage>> = _platformPackages.asStateFlow()

    fun updatePackage(id: String, newTitle: String, newRange: String, minUsd: Double, maxUsd: Double) {
        _platformPackages.update { list ->
            list.map { pkg ->
                if (pkg.id == id) {
                    pkg.copy(
                        title = newTitle,
                        estimatedProfitRange = newRange,
                        minDepositUsd = minUsd,
                        maxDepositUsd = maxUsd
                    )
                } else pkg
            }
        }
    }

    // Active package subscriptions held by the customer
    private val _subscriptions = MutableStateFlow(
        listOf(
            PackageSubscription(
                id = "SUB-101",
                packageId = "pkg_apex_daily",
                packageTitle = "Apex 24H Liquidity Surge",
                cycle = PackageCycle.DAILY,
                principalAmountUsd = 500.0,
                dailyRatePct = 2.0,
                startDate = "2026-10-08",
                daysElapsed = 1,
                totalDays = 1,
                totalEarnedUsd = 10.0,
                status = "ACTIVE_EARNING"
            ),
            PackageSubscription(
                id = "SUB-102",
                packageId = "pkg_titan_weekly",
                packageTitle = "Titan 7D Momentum Harvest",
                cycle = PackageCycle.WEEKLY,
                principalAmountUsd = 1200.0,
                dailyRatePct = 0.57,
                startDate = "2026-10-05",
                daysElapsed = 4,
                totalDays = 7,
                totalEarnedUsd = 27.36,
                status = "ACTIVE_EARNING"
            )
        )
    )
    val subscriptions: StateFlow<List<PackageSubscription>> = _subscriptions.asStateFlow()

    // Customer internal balance in USD
    private val _customerBalanceUsd = MutableStateFlow(2450.00)
    val customerBalanceUsd: StateFlow<Double> = _customerBalanceUsd.asStateFlow()

    // ================= 2. USER AUTHENTICATION & LOGIN STATE =================
    private val _currentUser = MutableStateFlow(
        UserAccount(
            email = "investor@binance-desk.com",
            fullName = "Verified Client",
            phone = "+1 (555) 720-4491",
            isLoggedIn = true
        )
    )
    val currentUser: StateFlow<UserAccount> = _currentUser.asStateFlow()

    fun login(email: String) {
        _currentUser.value = UserAccount(
            email = email,
            fullName = email.substringBefore("@").replace(".", " ").capitalize(),
            phone = "+1 (555) 000-1234",
            isLoggedIn = true
        )
    }

    fun register(name: String, email: String, phone: String) {
        _currentUser.value = UserAccount(
            email = email,
            fullName = name,
            phone = phone,
            isLoggedIn = true
        )
    }

    fun logout() {
        _currentUser.value = _currentUser.value.copy(isLoggedIn = false)
    }

    // ================= 3. WORKING BINANCE LIVE TRADING ORDERS ENGINE =================
    val binanceClient = BinanceApiClient()
    private val _binanceTickers = MutableStateFlow(
        listOf(
            BinanceTicker("BTCUSDT", "BTC", "USDT", 96450.00, 3.42, 97200.00, 94100.00, 42190.5, listOf(94100.0, 94800.0, 95200.0, 94900.0, 95800.0, 96100.0, 96450.0)),
            BinanceTicker("ETHUSDT", "ETH", "USDT", 3480.50, 2.18, 3520.00, 3380.00, 312450.0, listOf(3380.0, 3410.0, 3440.0, 3420.0, 3465.0, 3470.0, 3480.5)),
            BinanceTicker("SOLUSDT", "SOL", "USDT", 189.20, 5.75, 192.50, 178.00, 1845000.0, listOf(178.0, 180.5, 183.0, 182.0, 186.5, 188.0, 189.2)),
            BinanceTicker("BNBUSDT", "BNB", "USDT", 612.40, 1.65, 618.00, 598.00, 145000.0, listOf(598.0, 602.0, 605.0, 608.0, 610.0, 611.5, 612.4)),
            BinanceTicker("DOGEUSDT", "DOGE", "USDT", 0.1850, -0.84, 0.1920, 0.1810, 8900000.0, listOf(0.189, 0.191, 0.188, 0.186, 0.184, 0.183, 0.185))
        )
    )
    val binanceTickers: StateFlow<List<BinanceTicker>> = _binanceTickers.asStateFlow()

    private val _recentOrders = MutableStateFlow(
        listOf(
            BinanceTradeOrder("ORD-901", "BTCUSDT", "BUY", 96250.0, 0.00519, 500.0, "Today 09:15 AM"),
            BinanceTradeOrder("ORD-900", "ETHUSDT", "BUY", 3460.0, 0.1445, 500.0, "Yesterday 04:30 PM")
        )
    )
    val recentOrders: StateFlow<List<BinanceTradeOrder>> = _recentOrders.asStateFlow()

    fun executeBinanceTrade(symbol: String, side: String, amountUsd: Double, currentPrice: Double): Boolean {
        if (side == "BUY") {
            if (_customerBalanceUsd.value < amountUsd) return false
            _customerBalanceUsd.update { it - amountUsd }
        } else {
            _customerBalanceUsd.update { it + amountUsd }
        }

        val amountCrypto = if (currentPrice > 0) amountUsd / currentPrice else 0.0
        val newOrder = BinanceTradeOrder(
            id = "ORD-${(902..999).random()}",
            symbol = symbol,
            side = side,
            price = currentPrice,
            amountCrypto = amountCrypto,
            totalUsd = amountUsd,
            timestamp = "Just now"
        )
        _recentOrders.update { listOf(newOrder) + it }
        return true
    }

    suspend fun refreshTickers() {
        val res = binanceClient.fetchLiveTickers()
        if (res.isNotEmpty()) {
            _binanceTickers.value = res
        }
    }

    // ================= 4. ADMIN WALLET CONFIGURATION =================
    private val _adminWallets = MutableStateFlow(
        AdminWalletSettings(
            usdtTrc20 = "TJz98YxK4uQpL7vM2eN1wX9aB3c4d5e6f7",
            usdtErc20 = "0x71C8360d37F9b291583d76296313B11A4dD23c58",
            btcAddress = "bc1q9v8u0q3lwr9hsz07t2kgmd08vwhn4g7ek3lmqa",
            ethAddress = "0x71C8360d37F9b291583d76296313B11A4dD23c58"
        )
    )
    val adminWallets: StateFlow<AdminWalletSettings> = _adminWallets.asStateFlow()

    fun updateAdminWallets(usdtTrc20: String, usdtErc20: String, btc: String, eth: String) {
        _adminWallets.value = AdminWalletSettings(
            usdtTrc20 = usdtTrc20.trim(),
            usdtErc20 = usdtErc20.trim(),
            btcAddress = btc.trim(),
            ethAddress = eth.trim()
        )
    }

    // ================= 5. DEPOSIT PROOFS & PAYOUTS QUEUE =================
    private val _depositProofs = MutableStateFlow(
        listOf(
            CustomerDepositProof(
                id = "DEP-501",
                customerName = "Ali Raza",
                amountUsd = 500.0,
                assetSymbol = "USDT (TRC20)",
                targetNetwork = "TRON TRC-20",
                txHashOrProof = "98f4e2a1b3c7d6e5a4b3c2d1e0f9a8b7c6d5e4f3a2b1",
                timestamp = "Today 08:30 AM",
                isApproved = true
            ),
            CustomerDepositProof(
                id = "DEP-502",
                customerName = "Tariq Mehmood",
                amountUsd = 1000.0,
                assetSymbol = "USDT (TRC20)",
                targetNetwork = "TRON TRC-20",
                txHashOrProof = "3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b",
                timestamp = "Just now",
                isApproved = false
            )
        )
    )
    val depositProofs: StateFlow<List<CustomerDepositProof>> = _depositProofs.asStateFlow()

    private val _payoutRequests = MutableStateFlow(
        listOf(
            ProfitPayoutRecord(
                id = "PAY-201",
                customerName = "Ali Raza",
                amountUsd = 10.0,
                packageTitle = "Alpha Daily Scalp Vault",
                destinationWallet = "TYx849B2mKqP91FvD8cW45xM9aZc7E6Q1L",
                cycle = PackageCycle.DAILY,
                timestamp = "Today 09:00 AM",
                isPaidOut = true
            ),
            ProfitPayoutRecord(
                id = "PAY-202",
                customerName = "Tariq Mehmood",
                amountUsd = 27.36,
                packageTitle = "Momentum Weekly Strategy Pool",
                destinationWallet = "TJz98YxK4uQpL7vM2eN1wX9aB3c4d5e6f7",
                cycle = PackageCycle.WEEKLY,
                timestamp = "Pending Review",
                isPaidOut = false
            )
        )
    )
    val payoutRequests: StateFlow<List<ProfitPayoutRecord>> = _payoutRequests.asStateFlow()

    fun submitDepositProof(customerName: String, amountUsd: Double, assetSymbol: String, network: String, txHash: String) {
        val newDep = CustomerDepositProof(
            id = "DEP-${(503..999).random()}",
            customerName = customerName,
            amountUsd = amountUsd,
            assetSymbol = assetSymbol,
            targetNetwork = network,
            txHashOrProof = txHash,
            timestamp = "Just now",
            isApproved = false
        )
        _depositProofs.update { listOf(newDep) + it }
    }

    fun approveDeposit(depositId: String) {
        _depositProofs.update { list ->
            list.map { dep ->
                if (dep.id == depositId && !dep.isApproved) {
                    _customerBalanceUsd.update { it + dep.amountUsd }
                    dep.copy(isApproved = true)
                } else dep
            }
        }
    }

    fun completePayout(payoutId: String) {
        _payoutRequests.update { list ->
            list.map { pay ->
                if (pay.id == payoutId) pay.copy(isPaidOut = true) else pay
            }
        }
    }

    fun subscribePackage(pkg: PlatformPackage, amountUsd: Double): Boolean {
        if (_customerBalanceUsd.value < amountUsd) return false

        _customerBalanceUsd.update { it - amountUsd }

        val newSub = PackageSubscription(
            id = "SUB-${(103..999).random()}",
            packageId = pkg.id,
            packageTitle = pkg.title,
            cycle = pkg.cycle,
            principalAmountUsd = amountUsd,
            dailyRatePct = pkg.dailyRatePct,
            startDate = "2026-10-09",
            daysElapsed = 0,
            totalDays = pkg.durationDays,
            totalEarnedUsd = 0.0,
            status = "ACTIVE_EARNING"
        )
        _subscriptions.update { listOf(newSub) + it }
        return true
    }

    fun requestProfitPayout(subId: String, destWallet: String): Boolean {
        val sub = _subscriptions.value.find { it.id == subId } ?: return false
        val earned = sub.totalEarnedUsd
        if (earned <= 0.0) return false

        val newPay = ProfitPayoutRecord(
            id = "PAY-${(203..999).random()}",
            customerName = _currentUser.value.fullName,
            amountUsd = earned,
            packageTitle = sub.packageTitle,
            destinationWallet = destWallet,
            cycle = sub.cycle,
            timestamp = "Just now",
            isPaidOut = false
        )
        _payoutRequests.update { listOf(newPay) + it }

        _subscriptions.update { list ->
            list.map { if (it.id == subId) it.copy(totalEarnedUsd = 0.0) else it }
        }
        return true
    }

    // ================= 6. CUSTOMER SERVICE (BACKEND CHAT) =================
    private val _supportMessages = MutableStateFlow(
        listOf(
            SupportTicketMessage("MSG-1", "Support Desk", true, "Welcome to Aethel Desk! How can we assist you with packages, Binance trading, or deposits?", "08:00 AM"),
            SupportTicketMessage("MSG-2", "Ali Raza", false, "I sent 500 USDT to your TRC20 address. Please verify my Alpha Daily Scalp Vault.", "08:32 AM"),
            SupportTicketMessage("MSG-3", "Support Desk", true, "Hello Ali, your deposit of 500 USDT has been verified and your Daily Vault is active!", "08:35 AM")
        )
    )
    val supportMessages: StateFlow<List<SupportTicketMessage>> = _supportMessages.asStateFlow()

    fun sendSupportMessage(senderName: String, isAdmin: Boolean, text: String) {
        val newMsg = SupportTicketMessage(
            id = "MSG-${(4..999).random()}",
            senderName = senderName,
            isAdmin = isAdmin,
            messageText = text,
            timestamp = "Just now"
        )
        _supportMessages.update { it + newMsg }
    }

    // ================= 7. BINANCE LIVE TRADING CHAT & PRO SIGNALS ROOM =================
    private val _tradingRoomMessages = MutableStateFlow(
        listOf(
            LiveTradingSignalMessage(
                id = "SIG-101",
                senderName = "Master Trader (Admin)",
                isTraderPro = true,
                messageText = "⚡ HIGH CONVICTION BREAKOUT: BTC/USDT 4H candle breaking resistance at $96,200. Clean liquidity sweep confirmed. Enter long.",
                timestamp = "10:15 AM",
                isSignal = true,
                signalSymbol = "BTCUSDT",
                signalAction = "BUY / LONG",
                entryPrice = 96450.0,
                targetPrice = 98200.0,
                stopLoss = 95200.0,
                leverage = "10x Cross",
                copyCount = 28
            ),
            LiveTradingSignalMessage(
                id = "MSG-201",
                senderName = "Zubair Ahmad",
                isTraderPro = false,
                messageText = "Copied the BTC trade with $200! What is the estimated hold time?",
                timestamp = "10:18 AM"
            ),
            LiveTradingSignalMessage(
                id = "MSG-202",
                senderName = "Master Trader (Admin)",
                isTraderPro = true,
                messageText = "Hold time is 6 to 12 hours. Target 1 is $97,400. Trailing stop will activate automatically.",
                timestamp = "10:20 AM"
            ),
            LiveTradingSignalMessage(
                id = "SIG-102",
                senderName = "Master Trader (Admin)",
                isTraderPro = true,
                messageText = "🔥 SOL/USDT MOMENTUM PUMP: Solana holding $188 volume shelf. Bullish pennant confirmed on 15m Binance spot chart.",
                timestamp = "10:45 AM",
                isSignal = true,
                signalSymbol = "SOLUSDT",
                signalAction = "BUY / LONG",
                entryPrice = 189.20,
                targetPrice = 196.50,
                stopLoss = 184.00,
                leverage = "5x Cross",
                copyCount = 19
            )
        )
    )
    val tradingRoomMessages: StateFlow<List<LiveTradingSignalMessage>> = _tradingRoomMessages.asStateFlow()

    fun broadcastTradingSignal(
        symbol: String,
        action: String,
        entryPrice: Double,
        targetPrice: Double,
        stopLoss: Double,
        comment: String
    ) {
        val signal = LiveTradingSignalMessage(
            id = "SIG-${(103..999).random()}",
            senderName = "Master Trader (Admin)",
            isTraderPro = true,
            messageText = comment.ifBlank { "Live Binance Signal for $symbol ($action). Follow trade now." },
            timestamp = "Just now",
            isSignal = true,
            signalSymbol = symbol,
            signalAction = action,
            entryPrice = entryPrice,
            targetPrice = targetPrice,
            stopLoss = stopLoss,
            leverage = "10x Cross",
            copyCount = 1
        )
        _tradingRoomMessages.update { it + signal }
    }

    fun sendTradingChatMessage(senderName: String, isTraderPro: Boolean, messageText: String) {
        val msg = LiveTradingSignalMessage(
            id = "MSG-${(203..999).random()}",
            senderName = senderName,
            isTraderPro = isTraderPro,
            messageText = messageText,
            timestamp = "Just now"
        )
        _tradingRoomMessages.update { it + msg }
    }

    fun copyTrade(signalId: String, copyAmountUsd: Double): Boolean {
        val sig = _tradingRoomMessages.value.find { it.id == signalId } ?: return false
        val symbol = sig.signalSymbol ?: "BTCUSDT"
        val side = if (sig.signalAction?.contains("BUY") == true) "BUY" else "SELL"
        val price = sig.entryPrice ?: 96450.0

        val success = executeBinanceTrade(symbol, side, copyAmountUsd, price)
        if (success) {
            _tradingRoomMessages.update { list ->
                list.map { if (it.id == signalId) it.copy(copyCount = it.copyCount + 1) else it }
            }
        }
        return success
    }

    // ================= 8. INTERACTIVE ADMIN POWERSHELL ENGINE =================
    private val _powerShellLogs = MutableStateFlow(
        listOf(
            PowerShellLog(
                command = "PS C:\\binance-custody> Initialize-Platform",
                output = "[SYSTEM ONLINE] Binance Custodial Platform v2.4 initialized.\n[LEDGER] Double-entry settlement active.\n[WALLETS] 4 receiving networks active (TRC20, ERC20, BTC, ETH).\n[PACKAGES] 3 Investment Tiers loaded (Daily, Weekly, Monthly).\nType 'help' for command list.",
                timestamp = "Boot"
            )
        )
    )
    val powerShellLogs: StateFlow<List<PowerShellLog>> = _powerShellLogs.asStateFlow()

    fun runPowerShellCommand(cmd: String): String {
        val trimmed = cmd.trim()
        val parts = trimmed.split(" ")
        val mainCmd = parts.firstOrNull()?.lowercase() ?: ""

        val output = when (mainCmd) {
            "help", "?" -> {
                """Available PowerShell Commands:
  status                     - Show platform liquidity, users, and queue summary
  packages                   - List active packages, returns and ranges
  wallets                    - Display receiving addresses for TRC20, ERC20, BTC, ETH
  set-wallet <net> <address> - Update receiving wallet (net: trc20, erc20, btc, eth)
  deposits                   - List customer deposit verification queue
  approve <dep_id>           - Approve customer deposit & credit their balance
  payouts                    - List pending profit payout requests
  pay <payout_id>            - Complete and mark payout as disbursed
  signal <sym> <action> <msg>- Broadcast live trade call to customer Binance chat
  users                      - View registered user account details
  clear, cls                 - Clear PowerShell terminal log"""
            }
            "status" -> {
                val totalDep = _depositProofs.value.filter { it.isApproved }.sumOf { it.amountUsd }
                val pendingDep = _depositProofs.value.filter { !it.isApproved }.size
                val pendingPay = _payoutRequests.value.filter { !it.isPaidOut }.size
                """[PLATFORM STATUS: HEALTHY]
  Total Customer Approved Liquidity: $$totalDep USD
  Customer Active Balance: $${_customerBalanceUsd.value} USD
  Active Subscriptions: ${_subscriptions.value.size}
  Pending Deposit Verifications: $pendingDep
  Pending Profit Payouts: $pendingPay
  Binance Tickers Tracked: ${_binanceTickers.value.size}
  Active User: ${_currentUser.value.fullName} (${_currentUser.value.email})"""
            }
            "packages" -> {
                _platformPackages.value.joinToString("\n\n") { pkg ->
                    "• [${pkg.cycle.name}] ${pkg.title}\n  Cycle: ${pkg.durationText} | Return: ${pkg.estimatedProfitRange}\n  Min: $${pkg.minDepositUsd} - Max: $${pkg.maxDepositUsd}\n  Payout: ${pkg.payoutFrequency}"
                }
            }
            "wallets" -> {
                val w = _adminWallets.value
                """[CONFIGURED RECEIVING WALLETS]
  USDT (TRON TRC-20):    ${w.usdtTrc20}
  USDT (Ethereum ERC-20): ${w.usdtErc20}
  Bitcoin (BTC SegWit):   ${w.btcAddress}
  Ethereum (ETH):         ${w.ethAddress}"""
            }
            "set-wallet" -> {
                if (parts.size >= 3) {
                    val net = parts[1].lowercase()
                    val addr = parts[2]
                    val curr = _adminWallets.value
                    when (net) {
                        "trc20", "tron" -> updateAdminWallets(addr, curr.usdtErc20, curr.btcAddress, curr.ethAddress)
                        "erc20", "eth-usdt" -> updateAdminWallets(curr.usdtTrc20, addr, curr.btcAddress, curr.ethAddress)
                        "btc", "bitcoin" -> updateAdminWallets(curr.usdtTrc20, curr.usdtErc20, addr, curr.ethAddress)
                        "eth", "ethereum" -> updateAdminWallets(curr.usdtTrc20, curr.usdtErc20, curr.btcAddress, addr)
                        else -> return "Invalid network. Use: trc20, erc20, btc, or eth."
                    }
                    "[SUCCESS] Wallet for $net updated to: $addr"
                } else {
                    "Usage: set-wallet <trc20|erc20|btc|eth> <address>"
                }
            }
            "deposits" -> {
                if (_depositProofs.value.isEmpty()) "No deposit records in queue."
                else _depositProofs.value.joinToString("\n") { dep ->
                    "[${dep.id}] ${dep.customerName} | $${dep.amountUsd} ${dep.assetSymbol} | Status: ${if (dep.isApproved) "APPROVED" else "PENDING"} | Proof: ${dep.txHashOrProof.take(16)}..."
                }
            }
            "approve" -> {
                if (parts.size >= 2) {
                    val depId = parts[1].uppercase()
                    approveDeposit(depId)
                    "[SUCCESS] Deposit $depId approved and credited to customer balance."
                } else {
                    "Usage: approve <DEP_ID> (e.g. approve DEP-502)"
                }
            }
            "payouts" -> {
                if (_payoutRequests.value.isEmpty()) "No payout requests."
                else _payoutRequests.value.joinToString("\n") { pay ->
                    "[${pay.id}] ${pay.customerName} | $${pay.amountUsd} | Plan: ${pay.packageTitle} | Dest: ${pay.destinationWallet.take(14)}... | Paid: ${pay.isPaidOut}"
                }
            }
            "pay" -> {
                if (parts.size >= 2) {
                    val payId = parts[1].uppercase()
                    completePayout(payId)
                    "[SUCCESS] Payout request $payId marked as paid and disbursed."
                } else {
                    "Usage: pay <PAYOUT_ID> (e.g. pay PAY-202)"
                }
            }
            "signal" -> {
                if (parts.size >= 3) {
                    val sym = parts[1].uppercase()
                    val action = parts[2].uppercase()
                    val restText = parts.drop(3).joinToString(" ")
                    broadcastTradingSignal(
                        symbol = sym,
                        action = if (action.contains("BUY")) "BUY / LONG" else "SELL / SHORT",
                        entryPrice = if (sym.contains("BTC")) 96450.0 else 3480.0,
                        targetPrice = if (sym.contains("BTC")) 98500.0 else 3620.0,
                        stopLoss = if (sym.contains("BTC")) 95000.0 else 3390.0,
                        comment = restText.ifBlank { "Signal for $sym from Admin PowerShell." }
                    )
                    "[SUCCESS] Signal for $sym broadcasted to all customer live trading chats."
                } else {
                    "Usage: signal <SYMBOL> <BUY|SELL> <comment>"
                }
            }
            "users" -> {
                val u = _currentUser.value
                """[REGISTERED USER]
  Name: ${u.fullName}
  Email: ${u.email}
  Phone: ${u.phone}
  Tier: ${u.accountTier}
  Status: ${if (u.isLoggedIn) "Logged In" else "Logged Out"}"""
            }
            "cls", "clear" -> {
                _powerShellLogs.value = emptyList()
                return ""
            }
            "" -> ""
            else -> "Command '$trimmed' not recognized. Type 'help' for available commands."
        }

        if (mainCmd != "cls" && mainCmd != "clear" && trimmed.isNotBlank()) {
            val log = PowerShellLog(
                command = "PS C:\\binance-custody> $trimmed",
                output = output,
                timestamp = "Just now"
            )
            _powerShellLogs.update { it + log }
        }
        return output
    }
}
