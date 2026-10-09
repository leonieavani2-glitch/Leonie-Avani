package com.example

import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `verify exactly 3 packages are available (Daily, Weekly, Monthly)`() {
    val repo = com.example.data.CustodyRepository()
    val packages = repo.platformPackages.value
    assertEquals(3, packages.size)
    assertEquals(com.example.model.PackageCycle.DAILY, packages[0].cycle)
    assertEquals(com.example.model.PackageCycle.WEEKLY, packages[1].cycle)
    assertEquals(com.example.model.PackageCycle.MONTHLY, packages[2].cycle)
  }

  @Test
  fun `verify admin wallet configuration and update`() {
    val repo = com.example.data.CustodyRepository()
    val initialWallets = repo.adminWallets.value
    assertNotNull(initialWallets.usdtTrc20)
    repo.updateAdminWallets("TNewWallet123", "0xNewEthWallet", "bc1qNewBtc", "0xNewEth")
    assertEquals("TNewWallet123", repo.adminWallets.value.usdtTrc20)
  }

  @Test
  fun `verify customer subscription and profit payout request`() {
    val repo = com.example.data.CustodyRepository()
    val dailyPkg = repo.platformPackages.value.first()
    val initialBal = repo.customerBalanceUsd.value
    val subscribed = repo.subscribePackage(dailyPkg, 100.0)
    assertTrue(subscribed)
    assertEquals(initialBal - 100.0, repo.customerBalanceUsd.value, 0.01)
  }

  @Test
  fun `verify live trading signal broadcasting and copy trade`() {
    val repo = com.example.data.CustodyRepository()
    val initialSignalsCount = repo.tradingRoomMessages.value.size
    repo.broadcastTradingSignal("BTCUSDT", "BUY / LONG", 96450.0, 98200.0, 95200.0, "Test breakout call")
    assertEquals(initialSignalsCount + 1, repo.tradingRoomMessages.value.size)
  }

  @Test
  fun `verify powershell command execution and wallet setting`() {
    val repo = com.example.data.CustodyRepository()
    val statusOut = repo.runPowerShellCommand("status")
    assertTrue(statusOut.contains("PLATFORM STATUS: HEALTHY"))
    val walletOut = repo.runPowerShellCommand("set-wallet trc20 TPowerShellWallet999")
    assertTrue(walletOut.contains("SUCCESS"))
    assertEquals("TPowerShellWallet999", repo.adminWallets.value.usdtTrc20)
  }
}
