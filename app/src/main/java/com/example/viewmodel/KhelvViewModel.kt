package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.KhelvRepository
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class ActiveGameScreen {
  NONE, AVIATOR, MINES, WINGO, WHEEL_SPIN
}

class KhelvViewModel(
  val repository: KhelvRepository = KhelvRepository()
) : ViewModel() {
  val user = repository.user
  val transactions = repository.transactions
  val attendance = repository.attendance
  val dailyMissions = repository.dailyMissions
  val gamesList = repository.gamesList
  val gamesFlow = repository.gamesFlow
  val liveWinners = repository.liveWinners
  val leaderboard = repository.leaderboard
  val vipLevels = repository.vipLevels
  val wheelPrizes = repository.wheelPrizes

  // Admin Data Flows
  val usersList = repository.usersList
  val websiteSettings = repository.websiteSettings
  val auditLogs = repository.auditLogs
  val loginLogs = repository.loginLogs
  val adminSession = repository.adminSession
  val analyticsReports = repository.analyticsReports

  private val _activeTab = MutableStateFlow(NavTab.HOME)
  val activeTab: StateFlow<NavTab> = _activeTab.asStateFlow()

  private val _activeGame = MutableStateFlow(ActiveGameScreen.NONE)
  val activeGame: StateFlow<ActiveGameScreen> = _activeGame.asStateFlow()

  private val _selectedCategory = MutableStateFlow(GameCategory.POPULAR)
  val selectedCategory: StateFlow<GameCategory> = _selectedCategory.asStateFlow()

  private val _toastMessage = MutableStateFlow<String?>(null)
  val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

  // Admin UI State
  val isAdminPortalOpen = MutableStateFlow(false)
  val showAdminLoginDialog = MutableStateFlow(false)
  val activeAdminTab = MutableStateFlow(AdminTab.DASHBOARD)
  val userSearchQuery = MutableStateFlow("")
  val selectedManagedUser = MutableStateFlow<ManagedUser?>(null)
  val showAddGameDialog = MutableStateFlow(false)
  val editingGame = MutableStateFlow<GameItem?>(null)
  val showCoinAdjustmentDialog = MutableStateFlow<ManagedUser?>(null)

  // Modals & sub-flows
  val showDepositDialog = MutableStateFlow(false)
  val showWithdrawDialog = MutableStateFlow(false)
  val showGiftRedeemDialog = MutableStateFlow(false)
  val showCustomerService = MutableStateFlow(false)
  val showAuthDialog = MutableStateFlow(false)
  val showRebateDialog = MutableStateFlow(false)
  val showNotificationDialog = MutableStateFlow(false)
  val showGameRulesDialog = MutableStateFlow(false)
  val selectedRuleGame = MutableStateFlow("wingo")
  val showVipDetails = MutableStateFlow(false)

  init {
    // Start background simulation of real-time winners
    startLiveWinnerSimulation()
  }

  fun setTab(tab: NavTab) {
    _activeTab.value = tab
    _activeGame.value = ActiveGameScreen.NONE
  }

  fun openGame(game: ActiveGameScreen) {
    _activeGame.value = game
  }

  fun closeGame() {
    _activeGame.value = ActiveGameScreen.NONE
  }

  fun setCategory(category: GameCategory) {
    _selectedCategory.value = category
  }

  fun showToast(msg: String) {
    _toastMessage.value = msg
    viewModelScope.launch {
      delay(3000)
      if (_toastMessage.value == msg) {
        _toastMessage.value = null
      }
    }
  }

  fun deposit(amount: Double, channel: String) {
    val ok = repository.deposit(amount, channel)
    if (ok) {
      showToast("Successfully deposited ₹$amount via $channel! Balance updated.")
      showDepositDialog.value = false
    } else {
      showToast("Deposit failed. Please try a valid amount.")
    }
  }

  fun withdraw(amount: Double, method: String, accountInfo: String) {
    val (ok, msg) = repository.withdraw(amount, method, accountInfo)
    showToast(msg)
    if (ok) {
      showWithdrawDialog.value = false
    }
  }

  fun checkIn() {
    val (ok, msg) = repository.checkInToday()
    showToast(msg)
  }

  fun redeemGift(code: String) {
    val (ok, msg) = repository.redeemGiftCode(code)
    showToast(msg)
    if (ok) {
      showGiftRedeemDialog.value = false
    }
  }

  fun claimRebate() {
    val (ok, msg) = repository.claimOneClickRebate()
    showToast(msg)
  }

  fun onSpinWheel(prize: SpinReward) {
    val res = repository.spinWheelReward(prize)
    showToast(res)
  }

  // -------------------------------------------------------------------------
  // ADMIN PANEL OPERATIONS
  // -------------------------------------------------------------------------

  fun loginAdmin(user: String, pass: String, code2FA: String, role: AdminRole): Boolean {
    val (ok, msg) = repository.adminLogin(user, pass, code2FA, role)
    showToast(msg)
    if (ok) {
      showAdminLoginDialog.value = false
      isAdminPortalOpen.value = true
    }
    return ok
  }

  fun logoutAdmin() {
    repository.adminLogout()
    isAdminPortalOpen.value = false
    showToast("Logged out of Admin Portal successfully.")
  }

  fun toggleUserBlock(userId: String) {
    val adminUser = adminSession.value?.username ?: "admin"
    val (ok, msg) = repository.toggleUserBlock(userId, adminUser)
    showToast(msg)
  }

  fun adjustCoins(userId: String, amount: Double, isAdd: Boolean, reason: String) {
    val adminUser = adminSession.value?.username ?: "admin"
    val (ok, msg) = repository.adjustUserCoins(userId, amount, isAdd, reason, adminUser)
    showToast(msg)
    if (ok) {
      showCoinAdjustmentDialog.value = null
    }
  }

  fun addGame(game: GameItem) {
    val adminUser = adminSession.value?.username ?: "admin"
    val (ok, msg) = repository.addGame(game, adminUser)
    showToast(msg)
    if (ok) {
      showAddGameDialog.value = false
    }
  }

  fun updateGame(game: GameItem) {
    val adminUser = adminSession.value?.username ?: "admin"
    val (ok, msg) = repository.updateGame(game, adminUser)
    showToast(msg)
    if (ok) {
      editingGame.value = null
    }
  }

  fun toggleGameStatus(gameId: String) {
    val adminUser = adminSession.value?.username ?: "admin"
    val (ok, msg) = repository.toggleGameStatus(gameId, adminUser)
    showToast(msg)
  }

  fun removeGame(gameId: String) {
    val adminUser = adminSession.value?.username ?: "admin"
    val (ok, msg) = repository.removeGame(gameId, adminUser)
    showToast(msg)
  }

  fun saveWebsiteSettings(settings: WebsiteSettings) {
    val adminUser = adminSession.value?.username ?: "admin"
    val (ok, msg) = repository.updateWebsiteSettings(settings, adminUser)
    showToast(msg)
  }

  private fun startLiveWinnerSimulation() {
    viewModelScope.launch {
      while (true) {
        delay(8000)
        // Keep winners lively
      }
    }
  }
}
