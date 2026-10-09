package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BinanceTicker
import com.example.model.CandleData
import com.example.ui.components.BinanceTradingViewChart
import com.example.ui.theme.*
import com.example.viewmodel.CustodyViewModel

@Composable
fun BinanceMarketScreen(
    viewModel: CustodyViewModel,
    modifier: Modifier = Modifier
) {
    val tickers by viewModel.binanceTickers.collectAsState()
    val recentOrders by viewModel.recentOrders.collectAsState()
    val balanceUsd by viewModel.customerBalanceUsd.collectAsState()
    val tradingMessages by viewModel.tradingRoomMessages.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val context = LocalContext.current

    var selectedTicker by remember { mutableStateOf(tickers.firstOrNull() ?: tickers[0]) }
    var selectedTimeframe by remember { mutableStateOf("15m") }
    var chartViewMode by remember { mutableStateOf("tradingview") } // "tradingview", "candles", "line"
    var isCandleMode by remember { mutableStateOf(true) }
    var activeSubSection by remember { mutableStateOf("chat") } // "chat" (Live Trading Room), "terminal" (Spot Trade Terminal)

    var tradeAmountInput by remember { mutableStateOf("100") }
    var chatMessageInput by remember { mutableStateOf("") }

    // Copy trade dialog
    var copySignalIdToExecute by remember { mutableStateOf<String?>(null) }
    var copyTradeAmountInput by remember { mutableStateOf("100") }

    // Admin quick broadcast dialog
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastAction by remember { mutableStateOf("BUY / LONG") }
    var broadcastEntryInput by remember { mutableStateOf("") }
    var broadcastTargetInput by remember { mutableStateOf("") }
    var broadcastSlInput by remember { mutableStateOf("") }
    var broadcastNoteInput by remember { mutableStateOf("") }

    // Generate dynamic candles based on selected ticker and timeframe
    val candles = remember(selectedTicker.symbol, selectedTimeframe) {
        generateCandlesForTicker(selectedTicker, selectedTimeframe)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VaultDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card: Binance Live Online Status & Refresh
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("binance_header_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "BINANCE SPOT LIVE FEED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.refreshBinance()
                                Toast.makeText(context, "Binance market data refreshed!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp).testTag("refresh_binance_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = CryptoGoldPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal Tickers Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tickers.forEach { ticker ->
                            val isSelected = ticker.symbol == selectedTicker.symbol
                            Surface(
                                color = if (isSelected) VaultSurfaceElevated else Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) CryptoGoldPrimary else VaultBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTicker = ticker }
                                    .testTag("ticker_select_${ticker.symbol}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(ticker.baseAsset, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(
                                        text = "$${String.format("%,.0f", ticker.price)}",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "${if (ticker.priceChangePercent >= 0) "+" else ""}${String.format("%.2f", ticker.priceChangePercent)}%",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ticker.priceChangePercent >= 0) EmeraldSuccess else CoralError
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Price & 24h Stats Display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${selectedTicker.symbol} Spot",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = CryptoGoldPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CryptoGoldPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "$${String.format("%,.2f", selectedTicker.price)}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${if (selectedTicker.priceChangePercent >= 0) "+" else ""}${String.format("%.2f", selectedTicker.priceChangePercent)}% (24h)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTicker.priceChangePercent >= 0) EmeraldSuccess else CoralError
                            )
                            Text(
                                text = "24h Vol: $${String.format("%,.0f", selectedTicker.volume24h * selectedTicker.price)}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Timeframe & Chart Style Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Timeframe chips
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("1m", "15m", "1H", "4H", "1D").forEach { tf ->
                                val active = selectedTimeframe == tf
                                Surface(
                                    color = if (active) CryptoGoldPrimary else VaultSurfaceElevated,
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (active) CryptoGoldPrimary else VaultBorder),
                                    modifier = Modifier.clickable { selectedTimeframe = tf }
                                ) {
                                    Text(
                                        text = tf,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) Color.Black else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Chart Type Switcher: TradingView vs Canvas Candles/Line
                        Surface(
                            color = VaultSurfaceElevated,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder)
                        ) {
                            Row(modifier = Modifier.padding(2.dp)) {
                                Text(
                                    text = "TradingView",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (chartViewMode == "tradingview") CryptoGoldPrimary else TextMuted,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (chartViewMode == "tradingview") VaultSurfaceDark else Color.Transparent)
                                        .clickable { chartViewMode = "tradingview" }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                        .testTag("chart_mode_tradingview")
                                )
                                Text(
                                    text = "Candles",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (chartViewMode == "candles") CryptoGoldPrimary else TextMuted,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (chartViewMode == "candles") VaultSurfaceDark else Color.Transparent)
                                        .clickable {
                                            chartViewMode = "candles"
                                            isCandleMode = true
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                        .testTag("chart_mode_candles")
                                )
                                Text(
                                    text = "Line",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (chartViewMode == "line") CryptoGoldPrimary else TextMuted,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (chartViewMode == "line") VaultSurfaceDark else Color.Transparent)
                                        .clickable {
                                            chartViewMode = "line"
                                            isCandleMode = false
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                        .testTag("chart_mode_line")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (chartViewMode == "tradingview") {
                        // Real-Time TradingView Iframe / WebView Chart Component
                        BinanceTradingViewChart(
                            tickers = tickers,
                            initialSymbol = selectedTicker.symbol
                        )
                    } else {
                        // Interactive Native Candlestick / Line Chart Canvas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(VaultSurfaceElevated)
                                .border(1.dp, VaultBorder, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                if (candles.isNotEmpty()) {
                                    val minPrice = candles.minOf { it.low }
                                    val maxPrice = candles.maxOf { it.high }
                                    val range = (maxPrice - minPrice).coerceAtLeast(1.0)

                                    if (chartViewMode == "candles") {
                                        // Draw Candlesticks with wicks
                                        val candleCount = candles.size
                                        val step = w / candleCount
                                        val bodyWidth = (step * 0.65f).coerceAtLeast(3f)

                                        candles.forEachIndexed { i, c ->
                                            val isUp = c.close >= c.open
                                            val candleColor = if (isUp) EmeraldSuccess else CoralError
                                            val centerX = i * step + step / 2

                                            // Wick
                                            val wickYHigh = h - ((c.high - minPrice) / range * h).toFloat()
                                            val wickYLow = h - ((c.low - minPrice) / range * h).toFloat()
                                            drawLine(
                                                color = candleColor,
                                                start = Offset(centerX, wickYHigh),
                                                end = Offset(centerX, wickYLow),
                                                strokeWidth = 1.5.dp.toPx()
                                            )

                                            // Body
                                            val openY = h - ((c.open - minPrice) / range * h).toFloat()
                                            val closeY = h - ((c.close - minPrice) / range * h).toFloat()
                                            val topY = minOf(openY, closeY)
                                            val bodyH = kotlin.math.abs(closeY - openY).coerceAtLeast(2f)

                                            drawRect(
                                                color = candleColor,
                                                topLeft = Offset(centerX - bodyWidth / 2, topY),
                                                size = Size(bodyWidth, bodyH)
                                            )
                                        }
                                    } else {
                                        // Draw Line chart
                                        val path = Path()
                                        candles.forEachIndexed { i, c ->
                                            val x = i * (w / (candles.size - 1))
                                            val y = h - ((c.close - minPrice) / range * h).toFloat()
                                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                        }

                                        drawPath(
                                            path = path,
                                            color = CryptoGoldPrimary,
                                            style = Stroke(width = 2.5.dp.toPx())
                                        )
                                    }
                                }
                            }

                            // High / Low Indicators
                            Column(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = "High: $${String.format("%,.2f", selectedTicker.high24h)}",
                                    fontSize = 9.sp,
                                    color = EmeraldSuccess,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Low: $${String.format("%,.2f", selectedTicker.low24h)}",
                                    fontSize = 9.sp,
                                    color = CoralError,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Binance Depth Book Preview
                    Surface(
                        color = VaultSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("BUY BIDS (Support)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                Text("SELL ASKS (Resistance)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CoralError)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("$${String.format("%.1f", selectedTicker.price * 0.9995)} (18.4 BTC)", fontSize = 9.sp, color = TextSecondary)
                                Text("$${String.format("%.1f", selectedTicker.price * 1.0005)} (14.2 BTC)", fontSize = 9.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // 2. Section Selector: "Live Trading Room & Signals" vs "Spot Order Terminal"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { activeSubSection = "chat" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeSubSection == "chat") CryptoGoldPrimary else VaultSurfaceElevated
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("tab_live_trading_chat")
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = null,
                        tint = if (activeSubSection == "chat") Color.Black else TextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live Trading Room",
                        color = if (activeSubSection == "chat") Color.Black else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = { activeSubSection = "terminal" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeSubSection == "terminal") CryptoGoldPrimary else VaultSurfaceElevated
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("tab_trade_terminal")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = if (activeSubSection == "terminal") Color.Black else TextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Trade Execution",
                        color = if (activeSubSection == "terminal") Color.Black else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 3. Sub-Section: LIVE TRADING ROOM & SIGNALS CHAT
        if (activeSubSection == "chat") {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CryptoGoldPrimary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("live_signals_room_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Binance Live Trading Room",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Live signals broadcasted by Master Traders. Copy trade in 1-click!",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }

                            if (isAdminMode) {
                                Button(
                                    onClick = { showBroadcastDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = CryptoGoldPrimary),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp).testTag("broadcast_signal_button")
                                ) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Post Signal", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // List of signals & chat messages
            items(tradingMessages) { msg ->
                if (msg.isSignal) {
                    // Professional Pro Signal Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VaultSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CryptoGoldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("signal_card_${msg.id}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = CryptoGoldPrimary,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "PRO SIGNAL",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${msg.signalSymbol} • ${msg.signalAction}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (msg.signalAction?.contains("BUY") == true) EmeraldSuccess else CoralError
                                    )
                                }
                                Text(msg.timestamp, fontSize = 9.sp, color = TextMuted)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = msg.messageText,
                                fontSize = 11.sp,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Trade Levels
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Entry Price", fontSize = 9.sp, color = TextMuted)
                                    Text("$${String.format("%,.2f", msg.entryPrice ?: 0.0)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Column {
                                    Text("Target (TP)", fontSize = 9.sp, color = TextMuted)
                                    Text("$${String.format("%,.2f", msg.targetPrice ?: 0.0)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                }
                                Column {
                                    Text("Stop Loss (SL)", fontSize = 9.sp, color = TextMuted)
                                    Text("$${String.format("%,.2f", msg.stopLoss ?: 0.0)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CoralError)
                                }
                                Column {
                                    Text("Leverage", fontSize = 9.sp, color = TextMuted)
                                    Text(msg.leverage ?: "10x", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CryptoGoldPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "👥 ${msg.copyCount} Investors Copied",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )

                                Button(
                                    onClick = {
                                        copySignalIdToExecute = msg.id
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(32.dp).testTag("copy_trade_button_${msg.id}")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy Trade", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // Normal Chat Message
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = msg.senderName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (msg.isTraderPro) CryptoGoldPrimary else TextPrimary
                                    )
                                    if (msg.isTraderPro) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Surface(color = CryptoGoldPrimary.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                            Text("Admin", fontSize = 8.sp, color = CryptoGoldPrimary, modifier = Modifier.padding(horizontal = 4.dp))
                                        }
                                    }
                                }
                                Text(msg.timestamp, fontSize = 9.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(msg.messageText, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // Chat Input Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = chatMessageInput,
                        onValueChange = { chatMessageInput = it },
                        placeholder = { Text("Ask trader or discuss Binance trades...", fontSize = 11.sp, color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("trading_chat_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CryptoGoldPrimary,
                            unfocusedBorderColor = VaultBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    IconButton(
                        onClick = {
                            if (chatMessageInput.isNotBlank()) {
                                viewModel.sendTradingChatMessage(chatMessageInput, isTraderPro = isAdminMode)
                                chatMessageInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CryptoGoldPrimary)
                            .testTag("send_trading_chat_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
                    }
                }
            }
        }

        // 4. Sub-Section: SPOT ORDER EXECUTION TERMINAL
        if (activeSubSection == "terminal") {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("live_trading_terminal_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Live Binance Spot Trading Terminal",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Execute live buy and sell orders on ${selectedTicker.symbol}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = tradeAmountInput,
                            onValueChange = { tradeAmountInput = it },
                            label = { Text("Order Size ($ USD)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("trade_amount_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CryptoGoldPrimary,
                                unfocusedBorderColor = VaultBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        val amount = tradeAmountInput.toDoubleOrNull() ?: 100.0
                        val cryptoQty = if (selectedTicker.price > 0) amount / selectedTicker.price else 0.0

                        Text(
                            text = "Estimated Fill: ${String.format("%.5f", cryptoQty)} ${selectedTicker.baseAsset} • Available Balance: $${String.format("%,.2f", balanceUsd)}",
                            fontSize = 10.sp,
                            color = CryptoGoldPrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val ok = viewModel.executeBinanceTrade(
                                        symbol = selectedTicker.symbol,
                                        side = "BUY",
                                        amountUsd = amount,
                                        currentPrice = selectedTicker.price
                                    )
                                    if (ok) {
                                        Toast.makeText(context, "BUY ${selectedTicker.symbol} Filled at $${selectedTicker.price}!", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Insufficient balance! Deposit funds first.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("buy_order_button")
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("BUY / LONG", color = Color.Black, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val ok = viewModel.executeBinanceTrade(
                                        symbol = selectedTicker.symbol,
                                        side = "SELL",
                                        amountUsd = amount,
                                        currentPrice = selectedTicker.price
                                    )
                                    if (ok) {
                                        Toast.makeText(context, "SELL ${selectedTicker.symbol} Filled at $${selectedTicker.price}!", Toast.LENGTH_LONG).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CoralError),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("sell_order_button")
                            ) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SELL / SHORT", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Recent Executed Orders Log
            item {
                Text(
                    text = "Live Executed Orders Log",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            items(recentOrders) { order ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("order_item_${order.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = if (order.side == "BUY") EmeraldSuccess.copy(alpha = 0.2f) else CoralError.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = order.side,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (order.side == "BUY") EmeraldSuccess else CoralError,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(order.symbol, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("${order.id} • ${order.timestamp}", fontSize = 9.sp, color = TextMuted)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$${String.format("%,.2f", order.totalUsd)} USD",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "@ $${String.format("%,.2f", order.price)}",
                                fontSize = 10.sp,
                                color = CryptoGoldPrimary
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal: Copy Trade Dialog
    copySignalIdToExecute?.let { sigId ->
        AlertDialog(
            onDismissRequest = { copySignalIdToExecute = null },
            title = { Text("Confirm Copy Trade", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column {
                    Text("Enter order size to automatically mirror this Master Trader signal:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = copyTradeAmountInput,
                        onValueChange = { copyTradeAmountInput = it },
                        label = { Text("Copy Amount ($ USD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CryptoGoldPrimary,
                            unfocusedBorderColor = VaultBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Available: $${String.format("%,.2f", balanceUsd)} USD", fontSize = 10.sp, color = CryptoGoldPrimary)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = copyTradeAmountInput.toDoubleOrNull() ?: 100.0
                        val ok = viewModel.copyTrade(sigId, amt)
                        if (ok) {
                            Toast.makeText(context, "Trade copied successfully!", Toast.LENGTH_SHORT).show()
                            copySignalIdToExecute = null
                        } else {
                            Toast.makeText(context, "Insufficient balance! Deposit first.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Text("Execute Copy Trade", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { copySignalIdToExecute = null }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = VaultSurfaceDark
        )
    }

    // Modal: Admin Broadcast Signal Dialog
    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Broadcast Binance Signal", fontWeight = FontWeight.Bold, color = CryptoGoldPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Broadcast a live signal to all customers in the trading room:", fontSize = 11.sp, color = TextSecondary)

                    // Action toggle
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { broadcastAction = "BUY / LONG" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (broadcastAction == "BUY / LONG") EmeraldSuccess else VaultSurfaceElevated
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("BUY / LONG", color = if (broadcastAction == "BUY / LONG") Color.Black else TextPrimary, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { broadcastAction = "SELL / SHORT" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (broadcastAction == "SELL / SHORT") CoralError else VaultSurfaceElevated
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("SELL / SHORT", color = if (broadcastAction == "SELL / SHORT") Color.White else TextPrimary, fontSize = 11.sp)
                        }
                    }

                    OutlinedTextField(
                        value = broadcastEntryInput.ifBlank { selectedTicker.price.toString() },
                        onValueChange = { broadcastEntryInput = it },
                        label = { Text("Entry Price ($)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = broadcastTargetInput.ifBlank { (selectedTicker.price * 1.02).toString() },
                        onValueChange = { broadcastTargetInput = it },
                        label = { Text("Target TP ($)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = broadcastSlInput.ifBlank { (selectedTicker.price * 0.985).toString() },
                        onValueChange = { broadcastSlInput = it },
                        label = { Text("Stop Loss SL ($)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = broadcastNoteInput,
                        onValueChange = { broadcastNoteInput = it },
                        label = { Text("Trader Commentary / Analysis") },
                        placeholder = { Text("e.g. 15m breakout confirmed") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val entry = broadcastEntryInput.toDoubleOrNull() ?: selectedTicker.price
                        val tp = broadcastTargetInput.toDoubleOrNull() ?: (selectedTicker.price * 1.02)
                        val sl = broadcastSlInput.toDoubleOrNull() ?: (selectedTicker.price * 0.985)

                        viewModel.broadcastTradingSignal(
                            symbol = selectedTicker.symbol,
                            action = broadcastAction,
                            entry = entry,
                            target = tp,
                            sl = sl,
                            comment = broadcastNoteInput
                        )
                        Toast.makeText(context, "Signal broadcasted to live room!", Toast.LENGTH_SHORT).show()
                        showBroadcastDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CryptoGoldPrimary)
                ) {
                    Text("Broadcast Now", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = VaultSurfaceDark
        )
    }
}

// Procedural candle generator based on current ticker and timeframe
fun generateCandlesForTicker(ticker: BinanceTicker, timeframe: String): List<CandleData> {
    val base = ticker.price
    val count = 18
    val deltaPct = when (timeframe) {
        "1m" -> 0.002
        "15m" -> 0.006
        "1H" -> 0.012
        "4H" -> 0.025
        else -> 0.04
    }

    val list = mutableListOf<CandleData>()
    var prevClose = base * (1.0 - deltaPct * 1.5)

    for (i in 0 until count) {
        val rand = ((i * 17 + (base.toInt() % 31)) % 100) / 100.0
        val isUp = rand > 0.42
        val change = (rand * 0.8 + 0.2) * (base * deltaPct)
        val open = prevClose
        val close = if (isUp) open + change else (open - change).coerceAtLeast(1.0)
        val high = maxOf(open, close) + (rand * change * 0.5)
        val low = (minOf(open, close) - (rand * change * 0.5)).coerceAtLeast(1.0)
        val volume = (1000..50000).random().toDouble()

        list.add(
            CandleData(
                timeLabel = "$i",
                open = open,
                high = high,
                low = low,
                close = close,
                volume = volume
            )
        )
        prevClose = close
    }

    // Set last candle close to exact live ticker price
    val last = list.last()
    list[list.lastIndex] = last.copy(
        close = ticker.price,
        high = maxOf(last.high, ticker.high24h.coerceAtMost(ticker.price * 1.01)),
        low = minOf(last.low, ticker.low24h.coerceAtLeast(ticker.price * 0.99))
    )

    return list
}
