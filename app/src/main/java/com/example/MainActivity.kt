package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.NavTab
import com.example.ui.components.FloatingActionWidgets
import com.example.ui.components.KhelvBottomBar
import com.example.ui.components.KhelvTopBar
import com.example.ui.dialogs.AdminLoginDialog
import com.example.ui.dialogs.AuthDialog
import com.example.ui.dialogs.CustomerServiceDialog
import com.example.ui.dialogs.GameRulesTricksDialog
import com.example.ui.dialogs.GiftRedeemDialog
import com.example.ui.dialogs.NotificationDialog
import com.example.ui.dialogs.RebateDialog
import com.example.ui.screens.*
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.theme.KhelvNavyDark
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ActiveGameScreen
import com.example.viewmodel.KhelvViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        KhelvAppRoot()
      }
    }
  }
}

@Composable
fun KhelvAppRoot(
  viewModel: KhelvViewModel = viewModel()
) {
  val user by viewModel.user.collectAsState()
  val activeTab by viewModel.activeTab.collectAsState()
  val activeGame by viewModel.activeGame.collectAsState()
  val toastMessage by viewModel.toastMessage.collectAsState()

  val showGiftDialog by viewModel.showGiftRedeemDialog.collectAsState()
  val showSupportDialog by viewModel.showCustomerService.collectAsState()
  val showAuthDialog by viewModel.showAuthDialog.collectAsState()
  val showDepositDialog by viewModel.showDepositDialog.collectAsState()
  val showRebateDialog by viewModel.showRebateDialog.collectAsState()
  val showNotificationDialog by viewModel.showNotificationDialog.collectAsState()
  val showGameRulesDialog by viewModel.showGameRulesDialog.collectAsState()
  val showAdminLoginDialog by viewModel.showAdminLoginDialog.collectAsState()
  val isAdminPortalOpen by viewModel.isAdminPortalOpen.collectAsState()

  // Full-screen Admin Panel if opened
  if (isAdminPortalOpen) {
    AdminPanelScreen(
      viewModel = viewModel,
      onExitAdmin = { viewModel.isAdminPortalOpen.value = false }
    )
    return
  }

  // Handle active game screen if launched
  when (activeGame) {
    ActiveGameScreen.AVIATOR -> {
      AviatorGameScreen(
        viewModel = viewModel,
        onBack = { viewModel.closeGame() }
      )
      return
    }
    ActiveGameScreen.MINES -> {
      MinesGameScreen(
        viewModel = viewModel,
        onBack = { viewModel.closeGame() }
      )
      return
    }
    ActiveGameScreen.WINGO -> {
      WingoGameScreen(
        viewModel = viewModel,
        onBack = { viewModel.closeGame() }
      )
      return
    }
    ActiveGameScreen.WHEEL_SPIN -> {
      WheelSpinScreen(
        viewModel = viewModel,
        onBack = { viewModel.closeGame() }
      )
      return
    }
    ActiveGameScreen.NONE -> {
      // Main App Layout with TopBar & BottomBar
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(KhelvNavyDark)
  ) {
    Scaffold(
      containerColor = KhelvNavyDark,
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      topBar = {
        KhelvTopBar(
          balance = user.balance,
          onDepositClick = { viewModel.setTab(NavTab.WALLET) },
          onSupportClick = { viewModel.showCustomerService.value = true },
          onNotificationClick = { viewModel.showNotificationDialog.value = true },
          onAdminClick = {
            if (viewModel.adminSession.value != null) {
              viewModel.isAdminPortalOpen.value = true
            } else {
              viewModel.showAdminLoginDialog.value = true
            }
          }
        )
      },
      bottomBar = {
        KhelvBottomBar(
          currentTab = activeTab,
          onTabSelected = { viewModel.setTab(it) }
        )
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (activeTab) {
          NavTab.HOME -> HomeScreen(
            viewModel = viewModel,
            onNavigateToTab = { viewModel.setTab(it) }
          )
          NavTab.ACTIVITY -> ActivityScreen(
            viewModel = viewModel
          )
          NavTab.PROMOTION -> PromotionScreen(
            viewModel = viewModel
          )
          NavTab.WALLET -> WalletScreen(
            viewModel = viewModel
          )
          NavTab.ACCOUNT -> AccountScreen(
            viewModel = viewModel,
            onNavigateToTab = { viewModel.setTab(it) }
          )
        }

        // Floating Action Buttons (Gifts, Wheel, AI Customer Service)
        if (activeTab == NavTab.HOME || activeTab == NavTab.ACTIVITY) {
          FloatingActionWidgets(
            onWheelSpinClick = { viewModel.openGame(ActiveGameScreen.WHEEL_SPIN) },
            onGiftsClick = { viewModel.showGiftRedeemDialog.value = true },
            onCustomerServiceClick = { viewModel.showCustomerService.value = true },
            modifier = Modifier.align(Alignment.BottomEnd)
          )
        }
      }
    }

    // Floating In-App Toast Message
    AnimatedVisibility(
      visible = toastMessage != null,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier
        .align(Alignment.TopCenter)
        .statusBarsPadding()
        .padding(top = 60.dp, start = 20.dp, end = 20.dp)
    ) {
      toastMessage?.let { msg ->
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFF1E293B),
          shadowElevation = 8.dp,
          modifier = Modifier.testTag("app_toast")
        ) {
          Text(
            text = msg,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
          )
        }
      }
    }

    // Modal Dialogs
    if (showGiftDialog) {
      GiftRedeemDialog(
        viewModel = viewModel,
        onDismiss = { viewModel.showGiftRedeemDialog.value = false }
      )
    }

    if (showSupportDialog) {
      CustomerServiceDialog(
        viewModel = viewModel,
        onDismiss = { viewModel.showCustomerService.value = false }
      )
    }

    if (showAuthDialog) {
      AuthDialog(
        viewModel = viewModel,
        onDismiss = { viewModel.showAuthDialog.value = false }
      )
    }

    if (showRebateDialog) {
      RebateDialog(
        viewModel = viewModel,
        onDismiss = { viewModel.showRebateDialog.value = false }
      )
    }

    if (showNotificationDialog) {
      NotificationDialog(
        viewModel = viewModel,
        onDismiss = { viewModel.showNotificationDialog.value = false }
      )
    }

    if (showGameRulesDialog) {
      GameRulesTricksDialog(
        viewModel = viewModel,
        onDismiss = { viewModel.showGameRulesDialog.value = false }
      )
    }

    if (showAdminLoginDialog) {
      AdminLoginDialog(
        viewModel = viewModel,
        onDismiss = { viewModel.showAdminLoginDialog.value = false }
      )
    }

    if (showDepositDialog) {
      LaunchedEffect(Unit) {
        viewModel.setTab(NavTab.WALLET)
        viewModel.showDepositDialog.value = false
      }
    }
  }
}
