package com.example

import com.example.data.KhelvRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun `verify platform configuration`() {
    val repo = KhelvRepository()
    assertEquals("khelv.com", repo.websiteSettings.value.siteDomain)
    assertEquals("♠️", repo.websiteSettings.value.logoEmoji)
  }

  @Test
  fun `verify wallet deposit and betting logic`() {
    val repo = KhelvRepository()
    val initialBalance = repo.user.value.balance

    // Test deposit
    val depositSuccess = repo.deposit(200.0, "PhonePe UPI")
    assertTrue(depositSuccess)
    assertEquals(initialBalance + 200.0, repo.user.value.balance, 0.01)

    // Test bet
    val betSuccess = repo.placeBet(50.0, "Aviator")
    assertTrue(betSuccess)
    assertEquals(initialBalance + 150.0, repo.user.value.balance, 0.01)

    // Test win credit
    repo.creditWin(120.0, "Aviator")
    assertEquals(initialBalance + 270.0, repo.user.value.balance, 0.01)
  }

  @Test
  fun `verify gift code voucher redemption`() {
    val repo = KhelvRepository()
    val initialBalance = repo.user.value.balance

    val (success, _) = repo.redeemGiftCode("KHELV2026")
    assertTrue(success)
    assertEquals(initialBalance + 200.0, repo.user.value.balance, 0.01)
  }

  @Test
  fun `verify games list contains all user requested categories`() {
    val repo = KhelvRepository()
    val games = repo.gamesList

    assertTrue(games.any { it.title == "Charge Buffalo" })
    assertTrue(games.any { it.title == "Royal Fishing" })
    assertTrue(games.any { it.title == "Aviator" })
    assertTrue(games.any { it.title == "Cricket Exchange" })
    assertTrue(games.any { it.title == "Win Go 30s" })
    assertTrue(games.any { it.title == "Mines 5x5" })
    assertTrue(games.any { it.title == "Teen Patti" })
    assertTrue(games.any { it.title == "Evolution Casino" })
  }

  @Test
  fun `verify admin login with 2fa and session creation`() {
    val repo = KhelvRepository()
    val (success, msg) = repo.adminLogin("admin", "khelv@2026", "682914", com.example.model.AdminRole.SUPER_ADMIN)
    assertTrue(success)
    assertEquals("admin", repo.adminSession.value?.username?.lowercase())
    assertTrue(repo.loginLogs.value.any { it.status == "SUCCESS" })
  }

  @Test
  fun `verify admin block user and virtual coin adjustment with audit trail`() {
    val repo = KhelvRepository()
    val userId = "u_771902"
    val initialBalance = repo.usersList.value.find { it.id == userId }!!.balance

    // Block
    val (blockOk, _) = repo.toggleUserBlock(userId, "superadmin")
    assertTrue(blockOk)
    assertTrue(repo.usersList.value.find { it.id == userId }!!.isBlocked)

    // Adjust coins
    val (adjustOk, _) = repo.adjustUserCoins(userId, 500.0, true, "VIP Loyalty Bonus", "superadmin")
    assertTrue(adjustOk)
    assertEquals(initialBalance + 500.0, repo.usersList.value.find { it.id == userId }!!.balance, 0.01)

    // Verify audit log
    assertTrue(repo.auditLogs.value.any { it.target == userId && it.action == "COIN_ADJUST" })
  }

  @Test
  fun `verify admin add and toggle game in live catalog`() {
    val repo = KhelvRepository()
    val newGame = com.example.model.GameItem(
      id = "gold_blitz_777",
      title = "Gold Blitz 777",
      category = com.example.model.GameCategory.SLOTS,
      provider = "JILI",
      badge = "MEGA",
      iconEmoji = "🪙"
    )

    val (addOk, _) = repo.addGame(newGame, "superadmin")
    assertTrue(addOk)
    assertTrue(repo.gamesList.any { it.id == "gold_blitz_777" })

    // Toggle game status
    val (toggleOk, _) = repo.toggleGameStatus("gold_blitz_777", "superadmin")
    assertTrue(toggleOk)
    val game = repo.gamesList.find { it.id == "gold_blitz_777" }
    assertEquals(false, game?.isEnabled)
  }
}
