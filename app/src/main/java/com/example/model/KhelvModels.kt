package com.example.model

import androidx.compose.ui.graphics.Color

enum class NavTab {
  HOME, ACTIVITY, PROMOTION, WALLET, ACCOUNT
}

enum class GameCategory(val title: String) {
  POPULAR("Popular"),
  SLOTS("Slot Games"),
  FISHING("Fishing Games"),
  CRASH("Crash / Aviator"),
  CRICKET("Cricket Games"),
  LOTTERY("Number / Colour"),
  MINI_GAMES("Mini Games"),
  CARDS("Card Games"),
  CASINO_SPORTS("Live Casino & Sports")
}

data class UserAccount(
  val id: String = "u_882914",
  val phone: String = "+91 98765 43210",
  val username: String = "MemberNNGXBDMZ",
  val balance: Double = 731.44,
  val exp: Long = 1250L,
  val vipLevel: Int = 0,
  val inviteCode: String = "12981111730",
  val totalDeposit: Double = 1200.0,
  val totalWithdraw: Double = 500.0,
  val dailyStreak: Int = 1,
  val hasCheckedInToday: Boolean = false,
  val availableSpins: Int = 3,
  val directSubordinates: Int = 2,
  val totalTeamSubordinates: Int = 5,
  val todayCommission: Double = 42.50,
  val totalCommission: Double = 380.00,
  val todayRebate: Double = 15.20,
  val totalRebate: Double = 142.80
)

data class GameItem(
  val id: String,
  val title: String,
  val category: GameCategory,
  val provider: String = "JILI",
  val badge: String = "HOT",
  val accentColor: Color = Color(0xFF2563EB),
  val iconEmoji: String = "🎰",
  val isEnabled: Boolean = true,
  val playCount: Int = 1200,
  val rtpPercentage: Double = 97.5
)

data class LiveWinner(
  val id: String,
  val username: String,
  val gameName: String,
  val amount: Double
)

data class LeaderboardEntry(
  val rank: Int,
  val username: String,
  val amount: Double
)

enum class TransactionType {
  DEPOSIT, WITHDRAW, WIN, BET, REWARD, BONUS
}

enum class TransactionStatus {
  SUCCESS, PENDING, FAILED
}

data class WalletTransaction(
  val id: String,
  val title: String,
  val type: TransactionType,
  val amount: Double,
  val method: String,
  val timestamp: String,
  val status: TransactionStatus = TransactionStatus.SUCCESS
)

data class VIPLevel(
  val level: Int,
  val reqExp: Long,
  val levelReward: Double,
  val monthlyReward: Double,
  val rebateRate: Double,
  val title: String
)

data class DailyMission(
  val id: String,
  val title: String,
  val depositReq: Double,
  val betReq: Double,
  val awardAmount: Double,
  val currentDeposit: Double = 0.0,
  val currentBet: Double = 0.0,
  val isClaimed: Boolean = false
)

data class AttendanceReward(
  val day: Int,
  val amount: Double,
  val isClaimed: Boolean = false,
  val isToday: Boolean = false
)

data class SpinReward(
  val id: Int,
  val label: String,
  val cashAmount: Double,
  val isGrandPrize: Boolean = false,
  val color: Color
)

enum class AdminRole(val label: String) {
  SUPER_ADMIN("Super Admin"),
  STAFF_ADMIN("Staff Admin")
}

data class AdminSession(
  val username: String = "superadmin",
  val role: AdminRole = AdminRole.SUPER_ADMIN,
  val token: String = "AUTH_SEC_ADMIN_9921",
  val loggedInAt: String = "Today 10:00"
)

data class ManagedUser(
  val id: String,
  val username: String,
  val phone: String,
  val balance: Double,
  val isBlocked: Boolean = false,
  val registeredAt: String,
  val lastActive: String,
  val vipLevel: Int = 0,
  val totalDeposit: Double = 0.0,
  val totalWithdraw: Double = 0.0,
  val gameHistory: List<UserGameHistoryEntry> = emptyList()
)

data class UserGameHistoryEntry(
  val id: String,
  val gameName: String,
  val betAmount: Double,
  val winAmount: Double,
  val multiplier: Double,
  val timestamp: String
)

data class AdminAuditLog(
  val id: String,
  val adminUser: String,
  val action: String,
  val target: String,
  val details: String,
  val timestamp: String
)

data class AdminLoginLog(
  val id: String,
  val username: String,
  val role: String,
  val ipAddress: String,
  val timestamp: String,
  val status: String
)

data class WebsiteSettings(
  val siteName: String = "Khelv",
  val siteDomain: String = "khelv.com",
  val logoEmoji: String = "♠️",
  val announcementText: String = "Welcome to Khelv! Enjoy 100% deposit bonus on your first recharge via UPI & PhonePe.",
  val heroBannerTitle: String = "FIRST DEPOSIT BONUS",
  val heroBannerSubtitle: String = "+100% Extra Cash on all UPI deposits today",
  val isMaintenanceMode: Boolean = false,
  val minWithdrawalAmount: Double = 200.0,
  val supportTelegram: String = "@khelv_official_vip"
)

data class DailyAnalyticsReport(
  val date: String,
  val newUsers: Int,
  val activeUsers: Int,
  val totalDeposits: Double,
  val totalWithdrawals: Double,
  val ggrProfit: Double,
  val totalBetsPlaced: Int
)

enum class AdminTab(val title: String, val iconEmoji: String) {
  DASHBOARD("Dashboard", "📊"),
  USERS("Users", "👥"),
  GAMES("Games", "🎮"),
  WALLET("Virtual Wallet", "🪙"),
  WEBSITE("CMS & Site", "⚙️"),
  REPORTS("Analytics", "📈"),
  SECURITY("Security & 2FA", "🛡️")
}

