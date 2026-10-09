package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.BinanceTicker
import com.example.ui.theme.*

/**
 * Real-time TradingView / Binance Interactive Market Chart Component.
 * Embedded inside an optimized Android WebView displaying live Binance order book and candlestick streams.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BinanceTradingViewChart(
    tickers: List<BinanceTicker>,
    initialSymbol: String = "BTCUSDT",
    modifier: Modifier = Modifier
) {
    var selectedSymbol by remember { mutableStateOf(initialSymbol) }
    var selectedInterval by remember { mutableStateOf("15") }
    var isExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var reloadTrigger by remember { mutableIntStateOf(0) }

    val activeTicker = tickers.find { it.symbol == selectedSymbol } ?: tickers.firstOrNull()

    val chartHtml = remember(selectedSymbol, selectedInterval, reloadTrigger) {
        generateTradingViewHtml(symbol = selectedSymbol, interval = selectedInterval)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = VaultSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, VaultBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("tradingview_chart_card")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Bar: Pair Selector & Live Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (hasError) CoralError else EmeraldSuccess)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BINANCE LIVE CHART",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CryptoGoldPrimary,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = CryptoGoldPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "TradingView",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CryptoGoldPrimary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { reloadTrigger++ },
                        modifier = Modifier.size(28.dp).testTag("tv_reload_button")
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Reload Chart",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(28.dp).testTag("tv_expand_button")
                    ) {
                        Icon(
                            if (isExpanded) Icons.Default.CloseFullscreen else Icons.Default.OpenInFull,
                            contentDescription = if (isExpanded) "Contract" else "Expand",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Quick Pair Selection Chips
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val supportedPairs = listOf("BTCUSDT", "ETHUSDT", "SOLUSDT", "BNBUSDT")
                supportedPairs.forEach { sym ->
                    val isSel = sym == selectedSymbol
                    val t = tickers.find { it.symbol == sym }
                    Surface(
                        color = if (isSel) VaultSurfaceElevated else Color.Transparent,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSel) CryptoGoldPrimary else VaultBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedSymbol = sym }
                            .testTag("tv_pair_select_$sym")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = sym.replace("USDT", ""),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) CryptoGoldPrimary else TextSecondary
                            )
                            if (t != null) {
                                Text(
                                    text = "$${String.format("%,.0f", t.price)}",
                                    fontSize = 9.sp,
                                    color = if (t.priceChangePercent >= 0) EmeraldSuccess else CoralError
                                )
                            }
                        }
                    }
                }
            }

            // Intervals: 1m, 15m, 1H, 4H, 1D
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val intervals = listOf("1" to "1m", "15" to "15m", "60" to "1H", "240" to "4H", "D" to "1D")
                    intervals.forEach { (code, label) ->
                        val active = selectedInterval == code
                        Surface(
                            color = if (active) CryptoGoldPrimary else VaultSurfaceDark,
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (active) CryptoGoldPrimary else VaultBorder
                            ),
                            modifier = Modifier.clickable { selectedInterval = code }
                        ) {
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) Color.Black else TextMuted,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (activeTicker != null) {
                    Text(
                        text = "Spot: $${String.format("%,.2f", activeTicker.price)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // WebView Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isExpanded) 380.dp else 230.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(VaultDarkBg)
                    .border(1.dp, VaultBorder, RoundedCornerShape(8.dp))
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                databaseEnabled = true
                                cacheMode = WebSettings.LOAD_DEFAULT
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            }
                            setBackgroundColor(android.graphics.Color.parseColor("#0B0E14"))
                            webChromeClient = WebChromeClient()
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    isLoading = true
                                    hasError = false
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isLoading = false
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    if (request?.isForMainFrame == true) {
                                        hasError = true
                                        isLoading = false
                                    }
                                }
                            }
                            loadDataWithBaseURL(
                                "https://www.tradingview.com",
                                chartHtml,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        }
                    },
                    update = { webView ->
                        val currentTag = webView.tag as? String
                        val newTag = "$selectedSymbol-$selectedInterval-$reloadTrigger"
                        if (currentTag != newTag) {
                            webView.tag = newTag
                            webView.loadDataWithBaseURL(
                                "https://www.tradingview.com",
                                chartHtml,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize().testTag("binance_tradingview_webview")
                )

                // Loading Indicator Overlay
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(VaultDarkBg.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = CryptoGoldPrimary,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Loading Binance live feed...",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                // Error / Offline Fallback Display
                if (hasError) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(VaultDarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(Icons.Default.CloudOff, contentDescription = null, tint = CoralError, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Binance chart feed offline", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("Real-time prices are still updating via Spot WebSocket", fontSize = 10.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { reloadTrigger++ },
                                colors = ButtonDefaults.buttonColors(containerColor = CryptoGoldPrimary),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Retry Connection", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Live Binance Spot pairs: BTC, ETH, SOL, BNB",
                    fontSize = 9.sp,
                    color = TextMuted
                )
                Text(
                    text = "High-Frequency Realtime Streams",
                    fontSize = 9.sp,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun generateTradingViewHtml(symbol: String, interval: String): String {
    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
          <meta charset="utf-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
          <style>
            * { box-sizing: border-box; }
            html, body {
              margin: 0;
              padding: 0;
              width: 100%;
              height: 100%;
              background-color: #0B0E14;
              overflow: hidden;
              font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            }
            .tradingview-widget-container {
              width: 100% !important;
              height: 100% !important;
            }
            #tradingview_chart {
              width: 100% !important;
              height: 100% !important;
            }
          </style>
        </head>
        <body>
          <div class="tradingview-widget-container">
            <div id="tradingview_chart"></div>
            <script type="text/javascript" src="https://s3.tradingview.com/tv.js"></script>
            <script type="text/javascript">
              try {
                new TradingView.widget({
                  "autosize": true,
                  "symbol": "BINANCE:${symbol}",
                  "interval": "${interval}",
                  "timezone": "Etc/UTC",
                  "theme": "dark",
                  "style": "1",
                  "locale": "en",
                  "toolbar_bg": "#0B0E14",
                  "enable_publishing": false,
                  "hide_top_toolbar": false,
                  "hide_legend": false,
                  "save_image": false,
                  "backgroundColor": "#0B0E14",
                  "gridColor": "rgba(42, 46, 57, 0.25)",
                  "container_id": "tradingview_chart"
                });
              } catch(e) {
                console.error(e);
              }
            </script>
          </div>
        </body>
        </html>
    """.trimIndent()
}
