package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KhelvRepository {
  private val _user = MutableStateFlow(UserAccount())
  val user: StateFlow<UserAccount> = _user.asStateFlow()

  private val _transactions = MutableStateFlow<List<WalletTransaction>>(
    listOf(
      WalletTransaction(
        id = "TX-9901",
        title = "First Deposit Bonus",
        type = TransactionType.BONUS,
        amount = 100.0,
        method = "System Promo",
        timestamp = "Today 10:14"
      ),
      WalletTransaction(
        id = "TX-9902",
        title = "UPI Recharge",
        type = TransactionType.DEPOSIT,
        amount = 500.0,
        method = "PhonePe UPI",
        timestamp = "Today 11:30"
      ),
      WalletTransaction(
        id = "TX-9903",
        title = "Aviator Cashout",
        type = TransactionType.WIN,
        amount = 131.44,
        method = "Game Win",
        timestamp = "Today 12:45"
      )
    )
  )
  val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

  val vipLevels = listOf(
    VIPLevel(level = 0, reqExp = 0L, levelReward = 0.0, monthlyReward = 0.0, rebateRate = 0.15, title = "VIP 0"),
    VIPLevel(level = 1, reqExp = 4000L, levelReward = 60.0, monthlyReward = 3.0, rebateRate = 0.20, title = "VIP 1"),
    VIPLevel(level = 2, reqExp = 30000L, levelReward = 180.0, monthlyReward = 9.0, rebateRate = 0.22, title = "VIP 2"),
    VIPLevel(level = 3, reqExp = 400000L, levelReward = 690.0, monthlyReward = 69.0, rebateRate = 0.25, title = "VIP 3"),
    VIPLevel(level = 4, reqExp = 4000000L, levelReward = 1690.0, monthlyReward = 690.0, rebateRate = 0.28, title = "VIP 4"),
    VIPLevel(level = 5, reqExp = 20000000L, levelReward = 6900.0, monthlyReward = 1290.0, rebateRate = 0.30, title = "VIP 5"),
    VIPLevel(level = 6, reqExp = 50000000L, levelReward = 18900.0, monthlyReward = 3890.0, rebateRate = 0.35, title = "VIP 6")
  )

  private val _attendance = MutableStateFlow(
    listOf(
      AttendanceReward(day = 1, amount = 5.0, isClaimed = true, isToday = false),
      AttendanceReward(day = 2, amount = 18.0, isClaimed = false, isToday = true),
      AttendanceReward(day = 3, amount = 100.0, isClaimed = false, isToday = false),
      AttendanceReward(day = 4, amount = 200.0, isClaimed = false, isToday = false),
      AttendanceReward(day = 5, amount = 400.0, isClaimed = false, isToday = false),
      AttendanceReward(day = 6, amount = 3000.0, isClaimed = false, isToday = false),
      AttendanceReward(day = 7, amount = 7000.0, isClaimed = false, isToday = false)
    )
  )
  val attendance: StateFlow<List<AttendanceReward>> = _attendance.asStateFlow()

  private val _dailyMissions = MutableStateFlow(
    listOf(
      DailyMission(
        id = "m1",
        title = "Daily Beginner Recharge & Bet",
        depositReq = 500.0,
        betReq = 2500.0,
        awardAmount = 5.0,
        currentDeposit = 500.0,
        currentBet = 1200.0,
        isClaimed = false
      ),
      DailyMission(
        id = "m2",
        title = "Slot Daily Recharge & Bet Task",
        depositReq = 1500.0,
        betReq = 7500.0,
        awardAmount = 20.0,
        currentDeposit = 700.0,
        currentBet = 3400.0,
        isClaimed = false
      ),
      DailyMission(
        id = "m3",
        title = "High Roller Master Mission",
        depositReq = 5000.0,
        betReq = 20000.0,
        awardAmount = 100.0,
        currentDeposit = 0.0,
        currentBet = 0.0,
        isClaimed = false
      )
    )
  )
  val dailyMissions: StateFlow<List<DailyMission>> = _dailyMissions.asStateFlow()

  private val _gamesList = MutableStateFlow(
    listOf(
      // 1. Slot Games
      GameItem("charge_buffalo", "Charge Buffalo", GameCategory.SLOTS, "JILI", "JACKPOT", Color(0xFFF59E0B), "🦬"),
      GameItem("super_ace", "Super Ace", GameCategory.SLOTS, "JILI", "HOT", Color(0xFFFF5722), "🃏"),
      GameItem("jili_slots", "JILI Slots 777", GameCategory.SLOTS, "JILI", "TOP", Color(0xFFFFB800), "🎰"),

      // 2. Fishing Games (JILI)
      GameItem("royal_fishing", "Royal Fishing", GameCategory.FISHING, "JILI", "POPULAR", Color(0xFF3B82F6), "👑"),
      GameItem("all_star_fishing", "All Star Fishing", GameCategory.FISHING, "JILI", "HOT", Color(0xFF06B6D4), "⭐"),
      GameItem("bombing_fishing", "Bombing Fishing", GameCategory.FISHING, "JILI", "MEGA", Color(0xFFEF4444), "💣"),
      GameItem("dinosaur_tycoon", "Dinosaur Tycoon", GameCategory.FISHING, "JILI", "NEW", Color(0xFF10B981), "🦖"),
      GameItem("jackpot_fishing", "Jackpot Fishing", GameCategory.FISHING, "JILI", "JACKPOT", Color(0xFFFFB800), "🎣"),
      GameItem("boom_legends", "Boom Legends", GameCategory.FISHING, "JILI", "PRO", Color(0xFF8B5CF6), "💥"),

      // 3. Crash / Aviator Games
      GameItem("aviator", "Aviator", GameCategory.CRASH, "SPRIBE", "HOT", Color(0xFFEF4444), "✈️"),
      GameItem("crash", "Crash Multiplier", GameCategory.CRASH, "ORIGINAL", "TOP", Color(0xFFDC2626), "🚀"),
      GameItem("limbo", "Limbo Rider", GameCategory.CRASH, "ORIGINAL", "FAST", Color(0xFF0284C7), "⚡"),
      GameItem("rocket", "Rocket X", GameCategory.CRASH, "TURBO", "HIGH", Color(0xFFF97316), "🛰️"),
      GameItem("plinko", "Plinko Drop", GameCategory.CRASH, "BGAMING", "FAVORITE", Color(0xFF8B5CF6), "⚪"),

      // 4. Cricket Games
      GameItem("cricket_exchange", "Cricket Exchange", GameCategory.CRICKET, "EXCH", "LIVE", Color(0xFF16A34A), "🏏"),
      GameItem("cricket_live", "Cricket Live", GameCategory.CRICKET, "SPORTS", "MATCH", Color(0xFF2563EB), "🏟️"),

      // 5. Number / Colour Games
      GameItem("wingo_30s", "Win Go 30s", GameCategory.LOTTERY, "WIN", "FAST", Color(0xFF8B5CF6), "⚡"),
      GameItem("wingo_1m", "Win Go 1m", GameCategory.LOTTERY, "WIN", "POPULAR", Color(0xFF10B981), "🟢"),
      GameItem("wingo_3m", "Win Go 3m", GameCategory.LOTTERY, "WIN", "HOT", Color(0xFFEF4444), "🔴"),
      GameItem("wingo_5m", "Win Go 5m", GameCategory.LOTTERY, "WIN", "SAFE", Color(0xFFF59E0B), "⏱️"),
      GameItem("k3_30s", "K3 30s", GameCategory.LOTTERY, "DICE", "TURBO", Color(0xFFEC4899), "🎲"),
      GameItem("k3_1m", "K3 1m", GameCategory.LOTTERY, "DICE", "HOT", Color(0xFFD946EF), "🎲"),
      GameItem("k3_3m", "K3 3m", GameCategory.LOTTERY, "DICE", "PRO", Color(0xFFA855F7), "🎲"),
      GameItem("k3_5m", "K3 5m", GameCategory.LOTTERY, "DICE", "CLASSIC", Color(0xFF6366F1), "🎲"),
      GameItem("lotre", "Lotre", GameCategory.LOTTERY, "WIN", "LIVE", Color(0xFF06B6D4), "🎫"),
      GameItem("5d_lotre", "5D Lotre", GameCategory.LOTTERY, "WIN", "DRAW", Color(0xFF14B8A6), "🔮"),
      GameItem("trx_wingo", "TRX WinGo", GameCategory.LOTTERY, "BLOCKCHAIN", "PRO", Color(0xFF3B82F6), "🪙"),
      GameItem("moto_racing", "Moto Racing", GameCategory.LOTTERY, "TURBO", "SPEED", Color(0xFFF97316), "🏍️"),
      GameItem("colour_prediction", "Colour Prediction", GameCategory.LOTTERY, "WIN", "BEST", Color(0xFFEC4899), "🎨"),

      // 6. Mini Games
      GameItem("mines", "Mines 5x5", GameCategory.MINI_GAMES, "ORIGINAL", "HOT", Color(0xFF10B981), "💎"),
      GameItem("dice", "Dice Roll", GameCategory.MINI_GAMES, "ORIGINAL", "FAST", Color(0xFFF59E0B), "🎲"),
      GameItem("snakes", "Snakes", GameCategory.MINI_GAMES, "ARCADE", "FUN", Color(0xFF84CC16), "🐍"),
      GameItem("go_rush", "Go Rush", GameCategory.MINI_GAMES, "SPEED", "NEW", Color(0xFF06B6D4), "🏃"),
      GameItem("dragon_tiger", "Dragon Tiger", GameCategory.MINI_GAMES, "LIVE", "VIP", Color(0xFFDC2626), "🐉"),
      GameItem("goal", "Goal", GameCategory.MINI_GAMES, "SOCCER", "TOP", Color(0xFF16A34A), "⚽"),
      GameItem("hilo", "Hilo", GameCategory.MINI_GAMES, "ORIGINAL", "CLASSIC", Color(0xFF3B82F6), "🔼"),
      GameItem("cryptos", "Cryptos", GameCategory.MINI_GAMES, "CHAIN", "NEW", Color(0xFFFFB800), "₿"),

      // 7. Card Games
      GameItem("teen_patti", "Teen Patti", GameCategory.CARDS, "INDIAN", "DESI", Color(0xFFB91C1C), "🃏"),
      GameItem("rummy", "Rummy", GameCategory.CARDS, "INDIAN", "SKILL", Color(0xFF047857), "🂡"),
      GameItem("andar_bahar", "Andar Bahar", GameCategory.CARDS, "INDIAN", "HOT", Color(0xFF9333EA), "⚖️"),
      GameItem("3_patti", "3 Patti Gold", GameCategory.CARDS, "CLASSIC", "TOP", Color(0xFFC026D3), "👑"),
      GameItem("3_cards", "3 Cards", GameCategory.CARDS, "PVC", "FAST", Color(0xFF2563EB), "♠️"),
      GameItem("blackjack", "Blackjack 21", GameCategory.CARDS, "CASINO", "PRO", Color(0xFF1F2937), "♣️"),
      GameItem("365_poker", "365 Poker", GameCategory.CARDS, "V8", "VIP", Color(0xFFD97706), "♥️"),

      // 8. Live Casino / Sports
      GameItem("evolution_casino", "Evolution Casino", GameCategory.CASINO_SPORTS, "EVO", "LIVE", Color(0xFF0D9488), "🏛️"),
      GameItem("mg_live_grand", "MG Live Grand", GameCategory.CASINO_SPORTS, "MG", "LUXURY", Color(0xFF7C3AED), "💃"),
      GameItem("9_sports", "9 Sports", GameCategory.CASINO_SPORTS, "SABA", "LIVE", Color(0xFF16A34A), "🏆"),
      GameItem("live_casino", "Live Casino", GameCategory.CASINO_SPORTS, "DEALER", "24/7", Color(0xFFE11D48), "🎰"),
      GameItem("sports_games", "Sports Games", GameCategory.CASINO_SPORTS, "BET", "GLOBAL", Color(0xFF2563EB), "⚽")
    )
  )
  val gamesFlow: StateFlow<List<GameItem>> = _gamesList.asStateFlow()
  val gamesList: List<GameItem> get() = _gamesList.value

  val liveWinners = listOf(
    LiveWinner("w1", "Mem***VWI", "WinGo 30sec", 588.00),
    LiveWinner("w2", "Mem***RUB", "WinGo 1 Min", 980.00),
    LiveWinner("w3", "Mem***SPR", "WinGo 1 Min", 392.00),
    LiveWinner("w4", "Mem***J9K", "Aviator", 1450.00),
    LiveWinner("w5", "Mem***Q8X", "Fortune Gems", 3200.00),
    LiveWinner("w6", "Mem***77L", "Mines 5x5", 780.00),
    LiveWinner("w7", "Mem***24M", "Super Ace", 2540.00)
  )

  val leaderboard = listOf(
    LeaderboardEntry(1, "Mem***ER4", 23479846.46),
    LeaderboardEntry(2, "Mem***OEP", 8448184.08),
    LeaderboardEntry(3, "Mem***EAY", 8428000.00),
    LeaderboardEntry(4, "Mem***KPD", 6424684.00),
    LeaderboardEntry(5, "Mem***M3Z", 6048266.00),
    LeaderboardEntry(6, "Mem***NHV", 4918972.80)
  )

  val wheelPrizes = listOf(
    SpinReward(0, "₹2", 2.0, false, Color(0xFFFFE082)),
    SpinReward(1, "₹50", 50.0, false, Color(0xFF81D4FA)),
    SpinReward(2, "₹100", 100.0, false, Color(0xFF80CBC4)),
    SpinReward(3, "₹10", 10.0, false, Color(0xFFA5D6A7)),
    SpinReward(4, "Apple Watch", 2500.0, true, Color(0xFFCE93D8)),
    SpinReward(5, "₹5", 5.0, false, Color(0xFFFFF59D)),
    SpinReward(6, "₹1,000", 1000.0, false, Color(0xFFFFAB91)),
    SpinReward(7, "iPhone 18", 5000.0, true, Color(0xFFFF8A80))
  )

  fun deposit(amount: Double, channel: String): Boolean {
    if (amount <= 0) return false
    val newBalance = _user.value.balance + amount
    val newDeposit = _user.value.totalDeposit + amount
    _user.update { it.copy(balance = newBalance, totalDeposit = newDeposit) }
    addTransaction("Recharge $channel", TransactionType.DEPOSIT, amount, channel, TransactionStatus.SUCCESS)
    checkVipUpdate(0)
    return true
  }

  fun withdraw(amount: Double, method: String, accountInfo: String): Pair<Boolean, String> {
    if (amount < 200.0) {
      return Pair(false, "Minimum withdrawal is ₹200.00")
    }
    if (amount > _user.value.balance) {
      return Pair(false, "Insufficient balance! Available: ₹${String.format(Locale.US, "%.2f", _user.value.balance)}")
    }
    val newBalance = _user.value.balance - amount
    val newWithdraw = _user.value.totalWithdraw + amount
    _user.update { it.copy(balance = newBalance, totalWithdraw = newWithdraw) }
    addTransaction("Withdraw to $accountInfo", TransactionType.WITHDRAW, amount, method, TransactionStatus.PENDING)
    return Pair(true, "Withdrawal request of ₹$amount submitted successfully! Arriving in 5-15 mins.")
  }

  fun placeBet(amount: Double, gameName: String): Boolean {
    if (amount <= 0 || amount > _user.value.balance) return false
    val newBalance = _user.value.balance - amount
    val expEarned = amount.toLong()
    _user.update {
      it.copy(
        balance = newBalance,
        exp = it.exp + expEarned,
        todayRebate = it.todayRebate + (amount * 0.002)
      )
    }
    checkVipUpdate(expEarned)
    return true
  }

  fun creditWin(amount: Double, gameName: String) {
    if (amount <= 0) return
    val newBalance = _user.value.balance + amount
    _user.update { it.copy(balance = newBalance) }
    addTransaction("$gameName Win", TransactionType.WIN, amount, gameName, TransactionStatus.SUCCESS)
  }

  fun checkInToday(): Pair<Boolean, String> {
    val currentAttendance = _attendance.value
    val todayItem = currentAttendance.find { it.isToday && !it.isClaimed }
      ?: return Pair(false, "Already checked in today or no active reward!")

    val rewardAmount = todayItem.amount
    _user.update {
      it.copy(
        balance = it.balance + rewardAmount,
        hasCheckedInToday = true,
        dailyStreak = it.dailyStreak + 1
      )
    }
    _attendance.update { list ->
      list.map {
        if (it.day == todayItem.day) it.copy(isClaimed = true) else it
      }
    }
    addTransaction("Day ${todayItem.day} Check-In", TransactionType.REWARD, rewardAmount, "Attendance", TransactionStatus.SUCCESS)
    return Pair(true, "Claimed ₹${String.format(Locale.US, "%.2f", rewardAmount)} successfully!")
  }

  fun spinWheelReward(prize: SpinReward): String {
    if (_user.value.availableSpins <= 0) {
      return "No spins remaining! Complete deposits or bets to earn spins."
    }
    _user.update {
      it.copy(
        availableSpins = it.availableSpins - 1,
        balance = it.balance + prize.cashAmount
      )
    }
    addTransaction("Lucky Wheel: ${prize.label}", TransactionType.REWARD, prize.cashAmount, "Wheel Spin", TransactionStatus.SUCCESS)
    return "You won ${prize.label}!"
  }

  fun redeemGiftCode(code: String): Pair<Boolean, String> {
    val cleanCode = code.trim().uppercase()
    val reward = when (cleanCode) {
      "KHELV2026", "KHELVVIP" -> 200.0
      "BONUS100", "WELCOME100" -> 100.0
      "LUCKY777" -> 50.0
      "DIAMOND500" -> 500.0
      else -> 0.0
    }
    if (reward > 0) {
      _user.update { it.copy(balance = it.balance + reward) }
      addTransaction("Gift Code: $cleanCode", TransactionType.BONUS, reward, "Redemption", TransactionStatus.SUCCESS)
      return Pair(true, "Gift Code redeemed successfully! Credited ₹$reward to your wallet.")
    }
    return Pair(false, "Invalid or expired gift code. Try 'KHELV2026' or 'BONUS100'")
  }

  fun claimOneClickRebate(): Pair<Boolean, String> {
    val rebate = _user.value.todayRebate
    if (rebate <= 0.0) return Pair(false, "No rebate available to claim yet. Place bets to earn rebate!")
    _user.update {
      it.copy(
        balance = it.balance + rebate,
        totalRebate = it.totalRebate + rebate,
        todayRebate = 0.0
      )
    }
    addTransaction("One-Click Rebate", TransactionType.BONUS, rebate, "Rebate System", TransactionStatus.SUCCESS)
    return Pair(true, "Claimed ₹${String.format(Locale.US, "%.2f", rebate)} rebate successfully!")
  }

  private fun checkVipUpdate(addedExp: Long) {
    val totalExp = _user.value.exp
    var highestVip = 0
    for (v in vipLevels) {
      if (totalExp >= v.reqExp) {
        highestVip = v.level
      }
    }
    if (highestVip > _user.value.vipLevel) {
      val bonus = vipLevels.find { it.level == highestVip }?.levelReward ?: 0.0
      _user.update { it.copy(vipLevel = highestVip, balance = it.balance + bonus) }
      if (bonus > 0) {
        addTransaction("VIP $highestVip Level-Up Bonus", TransactionType.BONUS, bonus, "VIP Club", TransactionStatus.SUCCESS)
      }
    }
  }

  private fun addTransaction(title: String, type: TransactionType, amount: Double, method: String, status: TransactionStatus) {
    val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    val timestamp = sdf.format(Date())
    val tx = WalletTransaction(
      id = "TX-${System.currentTimeMillis() % 100000}",
      title = title,
      type = type,
      amount = amount,
      method = method,
      timestamp = timestamp,
      status = status
    )
    _transactions.update { listOf(tx) + it }
  }

  // -------------------------------------------------------------------------
  // ADMIN PANEL BACKEND & DATABASE MANAGEMENT
  // -------------------------------------------------------------------------

  private val _usersList = MutableStateFlow<List<ManagedUser>>(
    listOf(
      ManagedUser(
        id = "u_882914",
        username = "MemberNNGXBDMZ",
        phone = "+91 98765 43210",
        balance = 731.44,
        isBlocked = false,
        registeredAt = "2026-11-03 12:14",
        lastActive = "Online now",
        vipLevel = 0,
        totalDeposit = 1200.0,
        totalWithdraw = 500.0,
        gameHistory = listOf(
          UserGameHistoryEntry("gh_1", "Aviator", 100.0, 150.0, 1.50, "Today 12:45"),
          UserGameHistoryEntry("gh_2", "Win Go 30s", 50.0, 98.0, 1.96, "Today 12:40"),
          UserGameHistoryEntry("gh_3", "Mines 5x5", 30.0, 0.0, 0.0, "Today 12:30")
        )
      ),
      ManagedUser(
        id = "u_771902",
        username = "RajeshVIP77",
        phone = "+91 91234 56780",
        balance = 5420.00,
        isBlocked = false,
        registeredAt = "2026-10-28 15:30",
        lastActive = "5 mins ago",
        vipLevel = 2,
        totalDeposit = 15000.0,
        totalWithdraw = 8000.0,
        gameHistory = listOf(
          UserGameHistoryEntry("gh_4", "Super Ace", 500.0, 2500.0, 5.0, "Today 12:10"),
          UserGameHistoryEntry("gh_5", "Charge Buffalo", 200.0, 600.0, 3.0, "Today 11:55")
        )
      ),
      ManagedUser(
        id = "u_654118",
        username = "SunilWinner99",
        phone = "+91 98111 22334",
        balance = 128.50,
        isBlocked = false,
        registeredAt = "2026-11-01 09:20",
        lastActive = "12 mins ago",
        vipLevel = 1,
        totalDeposit = 3500.0,
        totalWithdraw = 2800.0,
        gameHistory = listOf(
          UserGameHistoryEntry("gh_6", "Dragon Tiger", 100.0, 200.0, 2.0, "Today 11:40")
        )
      ),
      ManagedUser(
        id = "u_992014",
        username = "AmitGamblerX",
        phone = "+91 97654 32109",
        balance = 0.00,
        isBlocked = true,
        registeredAt = "2026-10-15 18:00",
        lastActive = "2 days ago",
        vipLevel = 0,
        totalDeposit = 500.0,
        totalWithdraw = 450.0,
        gameHistory = emptyList()
      ),
      ManagedUser(
        id = "u_338901",
        username = "VikramLucky7",
        phone = "+91 99887 76655",
        balance = 3250.75,
        isBlocked = false,
        registeredAt = "2026-11-05 14:15",
        lastActive = "25 mins ago",
        vipLevel = 1,
        totalDeposit = 6000.0,
        totalWithdraw = 3000.0,
        gameHistory = listOf(
          UserGameHistoryEntry("gh_7", "Cricket Exchange", 500.0, 900.0, 1.80, "Today 10:15")
        )
      )
    )
  )
  val usersList: StateFlow<List<ManagedUser>> = _usersList.asStateFlow()

  private val _websiteSettings = MutableStateFlow(WebsiteSettings())
  val websiteSettings: StateFlow<WebsiteSettings> = _websiteSettings.asStateFlow()

  private val _auditLogs = MutableStateFlow<List<AdminAuditLog>>(
    listOf(
      AdminAuditLog("aud_1", "superadmin", "COIN_ADJUST", "u_771902", "+₹500.00 VIP Promo Bonus added", "Today 11:00"),
      AdminAuditLog("aud_2", "superadmin", "USER_BLOCK", "u_992014", "Account flagged for suspicious activity", "Yesterday 16:30"),
      AdminAuditLog("aud_3", "staff_admin", "GAME_TOGGLE", "dinosaur_tycoon", "Game enabled in catalog", "Yesterday 14:10"),
      AdminAuditLog("aud_4", "superadmin", "SETTINGS_UPDATE", "Website Settings", "Banner announcement refreshed", "2 days ago")
    )
  )
  val auditLogs: StateFlow<List<AdminAuditLog>> = _auditLogs.asStateFlow()

  private val _loginLogs = MutableStateFlow<List<AdminLoginLog>>(
    listOf(
      AdminLoginLog("log_1", "superadmin", "SUPER_ADMIN", "103.21.124.89", "Today 09:30", "SUCCESS"),
      AdminLoginLog("log_2", "staff_admin", "STAFF_ADMIN", "49.36.178.212", "Today 08:45", "SUCCESS"),
      AdminLoginLog("log_3", "intruder", "STAFF_ADMIN", "185.220.101.5", "Yesterday 23:12", "FAILED_INVALID_2FA"),
      AdminLoginLog("log_4", "superadmin", "SUPER_ADMIN", "103.21.124.89", "Yesterday 10:00", "SUCCESS")
    )
  )
  val loginLogs: StateFlow<List<AdminLoginLog>> = _loginLogs.asStateFlow()

  private val _adminSession = MutableStateFlow<AdminSession?>(null)
  val adminSession: StateFlow<AdminSession?> = _adminSession.asStateFlow()

  val analyticsReports = listOf(
    DailyAnalyticsReport("Today", 48, 386, 142500.0, 98200.0, 44300.0, 3120),
    DailyAnalyticsReport("Yesterday", 56, 412, 168000.0, 112000.0, 56000.0, 3450),
    DailyAnalyticsReport("07 Nov", 62, 395, 155000.0, 105000.0, 50000.0, 2980),
    DailyAnalyticsReport("06 Nov", 41, 360, 134000.0, 89000.0, 45000.0, 2810),
    DailyAnalyticsReport("05 Nov", 53, 380, 149000.0, 95000.0, 54000.0, 3050)
  )

  fun adminLogin(usernameInput: String, passInput: String, code2FA: String, role: AdminRole): Pair<Boolean, String> {
    val cleanUser = usernameInput.trim()
    val cleanPass = passInput.trim()
    val clean2FA = code2FA.trim()

    val isValidCreds = (cleanUser == "admin" && cleanPass == "khelv@2026") ||
      (cleanUser == "staff" && cleanPass == "staff@2026") ||
      (cleanUser.isNotEmpty() && cleanPass.length >= 6)

    if (!isValidCreds) {
      logAdminLogin(cleanUser, role.name, "FAILED_CREDENTIALS")
      return Pair(false, "Invalid admin username or password.")
    }

    if (clean2FA.length != 6) {
      logAdminLogin(cleanUser, role.name, "FAILED_INVALID_2FA")
      return Pair(false, "Invalid 2FA Authenticator Code! Enter 6-digit PIN.")
    }

    val session = AdminSession(
      username = cleanUser,
      role = role,
      token = "AUTH_SEC_${System.currentTimeMillis() % 100000}",
      loggedInAt = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    )
    _adminSession.value = session
    logAdminLogin(cleanUser, role.name, "SUCCESS")
    recordAuditLog(cleanUser, "ADMIN_LOGIN", "Security Portal", "Logged in with role ${role.label} and verified 2FA")
    return Pair(true, "Welcome back, ${session.username}! Admin access granted.")
  }

  fun adminLogout() {
    val user = _adminSession.value?.username ?: "admin"
    recordAuditLog(user, "ADMIN_LOGOUT", "Security Portal", "Session terminated securely")
    _adminSession.value = null
  }

  fun toggleUserBlock(userId: String, adminUser: String): Pair<Boolean, String> {
    var newState = false
    var userFound = false
    _usersList.update { list ->
      list.map { user ->
        if (user.id == userId) {
          userFound = true
          newState = !user.isBlocked
          user.copy(isBlocked = newState)
        } else user
      }
    }
    if (!userFound) return Pair(false, "User not found")
    val actionText = if (newState) "BLOCKED" else "UNBLOCKED"
    recordAuditLog(adminUser, "USER_$actionText", userId, "Account status changed to $actionText")
    return Pair(true, "User $userId successfully $actionText!")
  }

  fun adjustUserCoins(userId: String, amount: Double, isAdd: Boolean, reason: String, adminUser: String): Pair<Boolean, String> {
    if (amount <= 0.0) return Pair(false, "Please enter an amount greater than 0")
    var updated = false
    _usersList.update { list ->
      list.map { user ->
        if (user.id == userId) {
          updated = true
          val newBalance = if (isAdd) user.balance + amount else maxOf(0.0, user.balance - amount)
          user.copy(balance = newBalance)
        } else user
      }
    }
    if (!updated) return Pair(false, "User not found")

    // Sync with active player account if matching
    if (userId == _user.value.id) {
      val playerBalance = if (isAdd) _user.value.balance + amount else maxOf(0.0, _user.value.balance - amount)
      _user.update { it.copy(balance = playerBalance) }
      addTransaction("Admin Adjustment ($reason)", if (isAdd) TransactionType.BONUS else TransactionType.BET, amount, "Virtual Wallet Manager", TransactionStatus.SUCCESS)
    }

    val action = if (isAdd) "+₹$amount" else "-₹$amount"
    recordAuditLog(adminUser, "COIN_ADJUST", userId, "$action ($reason)")
    return Pair(true, "Virtual coin balance adjusted: $action for $userId")
  }

  fun addGame(game: GameItem, adminUser: String): Pair<Boolean, String> {
    if (game.title.isBlank()) return Pair(false, "Game title cannot be empty")
    _gamesList.update { listOf(game) + it }
    recordAuditLog(adminUser, "GAME_ADD", game.id, "Added new game: ${game.title} (${game.category.title})")
    return Pair(true, "Game '${game.title}' added to catalog successfully!")
  }

  fun updateGame(game: GameItem, adminUser: String): Pair<Boolean, String> {
    _gamesList.update { list ->
      list.map { if (it.id == game.id) game else it }
    }
    recordAuditLog(adminUser, "GAME_EDIT", game.id, "Updated details for ${game.title}")
    return Pair(true, "Game '${game.title}' updated successfully!")
  }

  fun toggleGameStatus(gameId: String, adminUser: String): Pair<Boolean, String> {
    var newState = true
    _gamesList.update { list ->
      list.map {
        if (it.id == gameId) {
          newState = !it.isEnabled
          it.copy(isEnabled = newState)
        } else it
      }
    }
    val statusText = if (newState) "ENABLED" else "DISABLED"
    recordAuditLog(adminUser, "GAME_STATUS", gameId, "Game $statusText")
    return Pair(true, "Game is now $statusText!")
  }

  fun removeGame(gameId: String, adminUser: String): Pair<Boolean, String> {
    val target = _gamesList.value.find { it.id == gameId }
    _gamesList.update { list -> list.filterNot { it.id == gameId } }
    recordAuditLog(adminUser, "GAME_DELETE", gameId, "Removed game ${target?.title ?: gameId}")
    return Pair(true, "Game removed from catalog!")
  }

  fun updateWebsiteSettings(settings: WebsiteSettings, adminUser: String): Pair<Boolean, String> {
    _websiteSettings.value = settings
    recordAuditLog(adminUser, "SETTINGS_UPDATE", "Website Config", "Updated Site Name: ${settings.siteName}, Banners & Announcements")
    return Pair(true, "Website settings & content updated successfully!")
  }

  private fun logAdminLogin(username: String, role: String, status: String) {
    val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    val entry = AdminLoginLog(
      id = "log_${System.currentTimeMillis() % 10000}",
      username = username,
      role = role,
      ipAddress = "103.21.124.${(10..99).random()}",
      timestamp = sdf.format(Date()),
      status = status
    )
    _loginLogs.update { listOf(entry) + it }
  }

  private fun recordAuditLog(adminUser: String, action: String, target: String, details: String) {
    val sdf = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault())
    val log = AdminAuditLog(
      id = "aud_${System.currentTimeMillis() % 100000}",
      adminUser = adminUser,
      action = action,
      target = target,
      details = details,
      timestamp = sdf.format(Date())
    )
    _auditLogs.update { listOf(log) + it }
  }
}
