package com.example.model

data class BinanceTicker(
    val symbol: String,
    val baseAsset: String,
    val quoteAsset: String,
    val price: Double,
    val priceChangePercent: Double,
    val high24h: Double,
    val low24h: Double,
    val volume24h: Double,
    val sparkline: List<Double>
)

data class BinanceTradeOrder(
    val id: String,
    val symbol: String,
    val side: String, // "BUY" or "SELL"
    val price: Double,
    val amountCrypto: Double,
    val totalUsd: Double,
    val timestamp: String,
    val status: String = "FILLED"
)

enum class PackageCycle {
    DAILY,
    WEEKLY,
    MONTHLY
}

data class PlatformPackage(
    val id: String,
    val title: String,
    val cycle: PackageCycle,
    val durationText: String, // "24 Hours (Daily)", "7 Days (Weekly)", "30 Days (Monthly)"
    val durationDays: Int,
    val estimatedProfitRange: String, // "1.5% - 2.5% Daily", "3% - 5% Weekly", "10% - 18% Monthly"
    val cycleProfitPct: Double, // Target profit % for the whole cycle (e.g. 2.0% daily, 4.0% weekly, 14.0% monthly)
    val dailyRatePct: Double,
    val minDepositUsd: Double,
    val maxDepositUsd: Double,
    val description: String,
    val payoutFrequency: String,
    val riskLevel: String
)

data class PackageSubscription(
    val id: String,
    val packageId: String,
    val packageTitle: String,
    val cycle: PackageCycle,
    val principalAmountUsd: Double,
    val dailyRatePct: Double,
    val startDate: String,
    val daysElapsed: Int,
    val totalDays: Int,
    val totalEarnedUsd: Double,
    val status: String // "ACTIVE_EARNING", "COMPLETED", "CLAIMED"
)

data class AdminWalletSettings(
    val usdtTrc20: String = "TJz98YxK4uQpL7vM2eN1wX9aB3c4d5e6f7",
    val usdtErc20: String = "0x71C8360d37F9b291583d76296313B11A4dD23c58",
    val btcAddress: String = "bc1q9v8u0q3lwr9hsz07t2kgmd08vwhn4g7ek3lmqa",
    val ethAddress: String = "0x71C8360d37F9b291583d76296313B11A4dD23c58"
)

data class CustomerDepositProof(
    val id: String,
    val customerName: String,
    val amountUsd: Double,
    val assetSymbol: String,
    val targetNetwork: String,
    val txHashOrProof: String,
    val timestamp: String,
    val isApproved: Boolean = false
)

data class ProfitPayoutRecord(
    val id: String,
    val customerName: String,
    val amountUsd: Double,
    val packageTitle: String,
    val destinationWallet: String,
    val cycle: PackageCycle,
    val timestamp: String,
    val isPaidOut: Boolean = false
)

data class CandleData(
    val timeLabel: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
)

data class LiveTradingSignalMessage(
    val id: String,
    val senderName: String,
    val isTraderPro: Boolean,
    val messageText: String,
    val timestamp: String,
    val isSignal: Boolean = false,
    val signalSymbol: String? = null,
    val signalAction: String? = null, // "BUY / LONG" or "SELL / SHORT"
    val entryPrice: Double? = null,
    val targetPrice: Double? = null,
    val stopLoss: Double? = null,
    val leverage: String? = "10x",
    val copyCount: Int = 14
)

data class PowerShellLog(
    val command: String,
    val output: String,
    val timestamp: String
)

data class SupportTicketMessage(
    val id: String,
    val senderName: String,
    val isAdmin: Boolean,
    val messageText: String,
    val timestamp: String
)

data class UserAccount(
    val email: String,
    val fullName: String,
    val phone: String,
    val isLoggedIn: Boolean = true,
    val accountTier: String = "Verified VIP Investor",
    val joinDate: String = "October 2026"
)
