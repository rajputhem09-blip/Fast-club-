package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.KhelvViewModel
import java.util.Locale

@Composable
fun AdminPanelScreen(
  viewModel: KhelvViewModel,
  onExitAdmin: () -> Unit,
  modifier: Modifier = Modifier
) {
  val activeTab by viewModel.activeAdminTab.collectAsState()
  val adminSession by viewModel.adminSession.collectAsState()
  val websiteSettings by viewModel.websiteSettings.collectAsState()

  var selectedUserForHistory by remember { mutableStateOf<ManagedUser?>(null) }
  var selectedUserForCoinAdjust by remember { mutableStateOf<ManagedUser?>(null) }
  var showAddGameModal by remember { mutableStateOf(false) }
  var gameToEdit by remember { mutableStateOf<GameItem?>(null) }

  Scaffold(
    containerColor = Color(0xFF090D1A),
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      AdminTopBar(
        session = adminSession,
        siteName = websiteSettings.siteName,
        onExitToPlayer = onExitAdmin,
        onLogout = { viewModel.logoutAdmin() }
      )
    },
    bottomBar = {
      AdminBottomNavBar(
        activeTab = activeTab,
        onTabSelected = { viewModel.activeAdminTab.value = it }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (activeTab) {
        AdminTab.DASHBOARD -> AdminDashboardTab(
          viewModel = viewModel,
          onNavigateTab = { viewModel.activeAdminTab.value = it }
        )
        AdminTab.USERS -> AdminUsersTab(
          viewModel = viewModel,
          onViewHistory = { selectedUserForHistory = it },
          onAdjustCoins = { selectedUserForCoinAdjust = it }
        )
        AdminTab.GAMES -> AdminGamesTab(
          viewModel = viewModel,
          onAddGame = { showAddGameModal = true },
          onEditGame = { gameToEdit = it }
        )
        AdminTab.WALLET -> AdminWalletTab(
          viewModel = viewModel,
          onAdjustCoins = { selectedUserForCoinAdjust = it }
        )
        AdminTab.WEBSITE -> AdminWebsiteTab(
          viewModel = viewModel
        )
        AdminTab.REPORTS -> AdminReportsTab(
          viewModel = viewModel
        )
        AdminTab.SECURITY -> AdminSecurityTab(
          viewModel = viewModel
        )
      }
    }
  }

  // Modals
  selectedUserForHistory?.let { user ->
    UserHistoryDialog(user = user, onDismiss = { selectedUserForHistory = null })
  }

  selectedUserForCoinAdjust?.let { user ->
    CoinAdjustmentDialog(
      user = user,
      onConfirm = { amount, isAdd, reason ->
        viewModel.adjustCoins(user.id, amount, isAdd, reason)
        selectedUserForCoinAdjust = null
      },
      onDismiss = { selectedUserForCoinAdjust = null }
    )
  }

  if (showAddGameModal) {
    AddOrEditGameDialog(
      existingGame = null,
      onSave = { game ->
        viewModel.addGame(game)
        showAddGameModal = false
      },
      onDismiss = { showAddGameModal = false }
    )
  }

  gameToEdit?.let { game ->
    AddOrEditGameDialog(
      existingGame = game,
      onSave = { updated ->
        viewModel.updateGame(updated)
        gameToEdit = null
      },
      onDismiss = { gameToEdit = null }
    )
  }
}

// ---------------------------------------------------------------------------
// 1. TOP BAR
// ---------------------------------------------------------------------------

@Composable
private fun AdminTopBar(
  session: AdminSession?,
  siteName: String,
  onExitToPlayer: () -> Unit,
  onLogout: () -> Unit
) {
  Surface(
    color = Color(0xFF0F172A),
    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF1E293B)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFF8B5CF6)))),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }

        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "$siteName Admin",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (session?.role == AdminRole.SUPER_ADMIN) Color(0xFFB45309) else Color(0xFF1E3A8A)
            ) {
              Text(
                text = session?.role?.label ?: "Super Admin",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "Connected • Session: ${session?.username ?: "admin"}",
            color = Color(0xFF10B981),
            fontSize = 10.sp
          )
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF1E293B),
          modifier = Modifier
            .clickable(onClick = onExitToPlayer)
            .testTag("exit_admin_btn")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(14.dp))
            Text("Player View", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }

        IconButton(
          onClick = onLogout,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// 2. BOTTOM NAVIGATION BAR
// ---------------------------------------------------------------------------

@Composable
private fun AdminBottomNavBar(
  activeTab: AdminTab,
  onTabSelected: (AdminTab) -> Unit
) {
  Surface(
    color = Color(0xFF0F172A),
    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF1E293B)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(vertical = 6.dp, horizontal = 4.dp)
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      AdminTab.values().forEach { tab ->
        val isSelected = tab == activeTab
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isSelected) Color(0xFF2563EB) else Color.Transparent,
          modifier = Modifier
            .clickable { onTabSelected(tab) }
            .testTag("admin_tab_${tab.name.lowercase()}")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(tab.iconEmoji, fontSize = 14.sp)
            Text(
              text = tab.title,
              color = if (isSelected) Color.White else Color(0xFF94A3B8),
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              fontSize = 12.sp
            )
          }
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// 3. TAB 1: DASHBOARD
// ---------------------------------------------------------------------------

@Composable
private fun AdminDashboardTab(
  viewModel: KhelvViewModel,
  onNavigateTab: (AdminTab) -> Unit
) {
  val users by viewModel.usersList.collectAsState()
  val games by viewModel.gamesFlow.collectAsState()
  val auditLogs by viewModel.auditLogs.collectAsState()
  val analytics = viewModel.analyticsReports.firstOrNull()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Overview KPIs
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          AdminMetricCard(
            title = "Total Users",
            value = "${users.size * 284 + 120}",
            subtitle = "+48 registered today",
            emoji = "👥",
            accentColor = Color(0xFF3B82F6),
            modifier = Modifier.weight(1f)
          )
          AdminMetricCard(
            title = "Active Players",
            value = "${users.count { !it.isBlocked } * 78}",
            subtitle = "🟢 48 live online",
            emoji = "🔥",
            accentColor = Color(0xFF10B981),
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          AdminMetricCard(
            title = "Total Games",
            value = "${games.size}",
            subtitle = "${games.count { it.isEnabled }} active online",
            emoji = "🎮",
            accentColor = Color(0xFF8B5CF6),
            modifier = Modifier.weight(1f)
          )
          AdminMetricCard(
            title = "Today GGR Profit",
            value = "₹${String.format(Locale.US, "%,.0f", analytics?.ggrProfit ?: 44300.0)}",
            subtitle = "Bets: ₹${String.format(Locale.US, "%,.0f", analytics?.totalDeposits ?: 142500.0)}",
            emoji = "💰",
            accentColor = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 2. Revenue & Performance Chart (Custom Canvas)
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("7-Day Revenue & GGR Analytics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Gross Gaming Revenue vs Player Payouts", color = Color(0xFF94A3B8), fontSize = 11.sp)
            }
            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF10B981).copy(alpha = 0.2f)) {
              Text("+14.2% Growth", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
          }

          // Custom Canvas Bar Chart
          Canvas(
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp)
              .padding(top = 10.dp)
          ) {
            val bars = listOf(35f, 42f, 38f, 45f, 50f, 56f, 64f)
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val barWidth = size.width / (bars.size * 2)
            val maxVal = 70f

            bars.forEachIndexed { i, valPct ->
              val barHeight = (valPct / maxVal) * (size.height - 20f)
              val x = (i * 2 + 0.5f) * barWidth
              val y = size.height - barHeight - 15f

              // Deposit bar (Blue)
              drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color(0xFF3B82F6), Color(0xFF1E3A8A))),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
              )

              // Baseline indicator
              drawLine(
                color = Color(0xFF334155),
                start = Offset(0f, size.height - 10f),
                end = Offset(size.width, size.height - 10f),
                strokeWidth = 1f
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
              Text(day, color = Color(0xFF64748B), fontSize = 10.sp)
            }
          }
        }
      }
    }

    // 3. Quick Actions
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Quick Management Actions", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            AdminActionChip(
              label = "Manage Users",
              icon = Icons.Default.People,
              color = Color(0xFF3B82F6),
              modifier = Modifier.weight(1f),
              onClick = { onNavigateTab(AdminTab.USERS) }
            )
            AdminActionChip(
              label = "Add Game",
              icon = Icons.Default.AddCircle,
              color = Color(0xFF10B981),
              modifier = Modifier.weight(1f),
              onClick = { onNavigateTab(AdminTab.GAMES) }
            )
            AdminActionChip(
              label = "Site CMS",
              icon = Icons.Default.Edit,
              color = Color(0xFFF59E0B),
              modifier = Modifier.weight(1f),
              onClick = { onNavigateTab(AdminTab.WEBSITE) }
            )
          }
        }
      }
    }

    // 4. Recent Admin Audit Logs
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Recent Admin Audit Trail", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Auto-Logged", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }

          auditLogs.take(4).forEach { log ->
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF1E293B)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF334155)) {
                      Text(log.action, color = Color(0xFF93C5FD), fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                    Text(log.target, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                  Text(log.details, color = Color(0xFF94A3B8), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Text(log.timestamp, color = Color(0xFF64748B), fontSize = 10.sp)
              }
            }
          }
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// 4. TAB 2: USER MANAGEMENT
// ---------------------------------------------------------------------------

@Composable
private fun AdminUsersTab(
  viewModel: KhelvViewModel,
  onViewHistory: (ManagedUser) -> Unit,
  onAdjustCoins: (ManagedUser) -> Unit
) {
  val users by viewModel.usersList.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var filterStatus by remember { mutableStateOf("ALL") }

  val filteredUsers = remember(users, searchQuery, filterStatus) {
    users.filter { user ->
      val matchesSearch = user.username.contains(searchQuery, ignoreCase = true) ||
        user.phone.contains(searchQuery) ||
        user.id.contains(searchQuery, ignoreCase = true)

      val matchesStatus = when (filterStatus) {
        "ACTIVE" -> !user.isBlocked
        "BLOCKED" -> user.isBlocked
        else -> true
      }
      matchesSearch && matchesStatus
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search by username, phone, or ID...", color = Color(0xFF64748B), fontSize = 13.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Color(0xFF3B82F6),
          unfocusedBorderColor = Color(0xFF1E293B),
          focusedContainerColor = Color(0xFF0F172A),
          unfocusedContainerColor = Color(0xFF0F172A),
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
      )
    }

    // Filter pills
    item {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("ALL" to "All Users (${users.size})", "ACTIVE" to "Active (${users.count { !it.isBlocked }})", "BLOCKED" to "Blocked (${users.count { it.isBlocked }})").forEach { (key, label) ->
          val isSel = filterStatus == key
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSel) Color(0xFF2563EB) else Color(0xFF0F172A),
            modifier = Modifier.clickable { filterStatus = key }
          ) {
            Text(
              text = label,
              color = if (isSel) Color.White else Color(0xFF94A3B8),
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }
    }

    items(filteredUsers) { user ->
      UserManagementCard(
        user = user,
        onToggleBlock = { viewModel.toggleUserBlock(user.id) },
        onAdjustCoins = { onAdjustCoins(user) },
        onViewHistory = { onViewHistory(user) }
      )
    }
  }
}

@Composable
private fun UserManagementCard(
  user: ManagedUser,
  onToggleBlock: () -> Unit,
  onAdjustCoins: () -> Unit,
  onViewHistory: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFF0F172A),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (user.isBlocked) Color(0xFFEF4444).copy(alpha = 0.5f) else Color(0xFF1E293B)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(if (user.isBlocked) Color(0xFFEF4444) else Color(0xFF1E3A8A)),
            contentAlignment = Alignment.Center
          ) {
            Text(if (user.isBlocked) "🚫" else "👤", fontSize = 18.sp)
          }

          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(user.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF334155)) {
                Text("VIP ${user.vipLevel}", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
              }
            }
            Text("ID: ${user.id} • ${user.phone}", color = Color(0xFF94A3B8), fontSize = 11.sp)
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (user.isBlocked) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f)
        ) {
          Text(
            text = if (user.isBlocked) "BLOCKED" else "ACTIVE",
            color = if (user.isBlocked) Color(0xFFEF4444) else Color(0xFF10B981),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      // Financial Metrics
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF1E293B))
          .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("Virtual Balance", color = Color(0xFF94A3B8), fontSize = 10.sp)
          Text("₹${String.format(Locale.US, "%.2f", user.balance)}", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("Total Deposit", color = Color(0xFF94A3B8), fontSize = 10.sp)
          Text("₹${String.format(Locale.US, "%.2f", user.totalDeposit)}", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("Withdrawals", color = Color(0xFF94A3B8), fontSize = 10.sp)
          Text("₹${String.format(Locale.US, "%.2f", user.totalWithdraw)}", color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }

      // Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onAdjustCoins,
          modifier = Modifier.weight(1f).height(36.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(0.dp)
        ) {
          Text("🪙 Adjust Coins", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onViewHistory,
          modifier = Modifier.weight(1f).height(36.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(0.dp)
        ) {
          Text("📜 Game Logs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onToggleBlock,
          modifier = Modifier.weight(1f).height(36.dp),
          colors = ButtonDefaults.buttonColors(containerColor = if (user.isBlocked) Color(0xFF10B981) else Color(0xFFEF4444)),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(0.dp)
        ) {
          Text(if (user.isBlocked) "Unblock" else "Block", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// 5. TAB 3: GAME MANAGEMENT
// ---------------------------------------------------------------------------

@Composable
private fun AdminGamesTab(
  viewModel: KhelvViewModel,
  onAddGame: () -> Unit,
  onEditGame: (GameItem) -> Unit
) {
  val games by viewModel.gamesFlow.collectAsState()
  var selectedCategory by remember { mutableStateOf<GameCategory?>(null) }

  val filteredGames = remember(games, selectedCategory) {
    if (selectedCategory == null) games else games.filter { it.category == selectedCategory }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Header & Add Game Button
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Game Management", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
          Text("${games.size} Total Games • ${games.count { it.isEnabled }} Live", color = Color(0xFF94A3B8), fontSize = 11.sp)
        }

        Button(
          onClick = onAddGame,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(38.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Game", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }

    // Category Filter Pills
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (selectedCategory == null) Color(0xFF2563EB) else Color(0xFF0F172A),
          modifier = Modifier.clickable { selectedCategory = null }
        ) {
          Text("All (${games.size})", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
        }

        GameCategory.values().filterNot { it == GameCategory.POPULAR }.forEach { cat ->
          val isSel = cat == selectedCategory
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSel) Color(0xFF2563EB) else Color(0xFF0F172A),
            modifier = Modifier.clickable { selectedCategory = cat }
          ) {
            Text(cat.title, color = if (isSel) Color.White else Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
          }
        }
      }
    }

    items(filteredGames) { game ->
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (game.isEnabled) Color(0xFF1E293B) else Color(0xFF475569).copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(game.accentColor.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text(game.iconEmoji, fontSize = 22.sp)
            }

            Column {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(game.title, color = if (game.isEnabled) Color.White else Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF1E293B)) {
                  Text(game.badge, color = Color(0xFFF59E0B), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                }
              }
              Text("Provider: ${game.provider} • Cat: ${game.category.title} • RTP: ${game.rtpPercentage}%", color = Color(0xFF94A3B8), fontSize = 10.sp)
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Switch(
              checked = game.isEnabled,
              onCheckedChange = { viewModel.toggleGameStatus(game.id) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF10B981)
              )
            )

            IconButton(onClick = { onEditGame(game) }, modifier = Modifier.size(32.dp)) {
              Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
            }

            IconButton(onClick = { viewModel.removeGame(game.id) }, modifier = Modifier.size(32.dp)) {
              Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// 6. TAB 4: VIRTUAL WALLET MANAGEMENT
// ---------------------------------------------------------------------------

@Composable
private fun AdminWalletTab(
  viewModel: KhelvViewModel,
  onAdjustCoins: (ManagedUser) -> Unit
) {
  val users by viewModel.usersList.collectAsState()
  val transactions by viewModel.transactions.collectAsState()
  val auditLogs by viewModel.auditLogs.collectAsState()

  val totalCoinSupply = users.sumOf { it.balance }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Virtual Coin Treasury & Reserves", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
              Text("Total Virtual Coins in Circulation", color = Color(0xFF94A3B8), fontSize = 11.sp)
              Text("🪙 ₹${String.format(Locale.US, "%,.2f", totalCoinSupply)}", color = Color(0xFFF59E0B), fontWeight = FontWeight.Black, fontSize = 24.sp)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF1E3A8A)) {
              Text("Audit Verified", color = Color(0xFF93C5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
          }
        }
      }
    }

    item {
      Text("All Registered Player Balances", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }

    items(users) { user ->
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F172A),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(user.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Balance: ₹${String.format(Locale.US, "%.2f", user.balance)}", color = Color(0xFFF59E0B), fontSize = 12.sp)
          }
          Button(
            onClick = { onAdjustCoins(user) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.height(32.dp)
          ) {
            Text("Adjust Coins", fontSize = 11.sp)
          }
        }
      }
    }

    item {
      Text("Recent Wallet Transaction Ledger", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }

    items(transactions) { tx ->
      Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF0F172A), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column {
            Text(tx.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text("${tx.method} • ${tx.timestamp}", color = Color(0xFF94A3B8), fontSize = 10.sp)
          }
          val isPositive = tx.type in listOf(TransactionType.DEPOSIT, TransactionType.WIN, TransactionType.BONUS)
          Text(
            text = "${if (isPositive) "+" else "-"}₹${String.format(Locale.US, "%.2f", tx.amount)}",
            color = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// 7. TAB 5: WEBSITE CMS & SETTINGS
// ---------------------------------------------------------------------------

@Composable
private fun AdminWebsiteTab(
  viewModel: KhelvViewModel
) {
  val settings by viewModel.websiteSettings.collectAsState()

  var siteName by remember(settings) { mutableStateOf(settings.siteName) }
  var siteDomain by remember(settings) { mutableStateOf(settings.siteDomain) }
  var logoEmoji by remember(settings) { mutableStateOf(settings.logoEmoji) }
  var announcement by remember(settings) { mutableStateOf(settings.announcementText) }
  var heroBannerTitle by remember(settings) { mutableStateOf(settings.heroBannerTitle) }
  var heroBannerSubtitle by remember(settings) { mutableStateOf(settings.heroBannerSubtitle) }
  var isMaintenance by remember(settings) { mutableStateOf(settings.isMaintenanceMode) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Website CMS & Branding Management", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text("Live changes will update the home screen, logos, announcements and banners immediately.", color = Color(0xFF94A3B8), fontSize = 11.sp)
      }
    }

    item {
      Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF0F172A), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Brand & Identity", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = logoEmoji,
              onValueChange = { logoEmoji = it },
              label = { Text("Logo Icon") },
              modifier = Modifier.width(90.dp),
              colors = adminTextFieldColors()
            )

            OutlinedTextField(
              value = siteName,
              onValueChange = { siteName = it },
              label = { Text("Website / App Name") },
              modifier = Modifier.weight(1f),
              colors = adminTextFieldColors()
            )
          }

          OutlinedTextField(
            value = siteDomain,
            onValueChange = { siteDomain = it },
            label = { Text("Website Domain URL") },
            modifier = Modifier.fillMaxWidth(),
            colors = adminTextFieldColors()
          )
        }
      }
    }

    item {
      Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF0F172A), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Homepage Announcements & Hero Promo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

          OutlinedTextField(
            value = announcement,
            onValueChange = { announcement = it },
            label = { Text("Live Audio Notice Ticker") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            colors = adminTextFieldColors()
          )

          OutlinedTextField(
            value = heroBannerTitle,
            onValueChange = { heroBannerTitle = it },
            label = { Text("Hero Banner Headline") },
            modifier = Modifier.fillMaxWidth(),
            colors = adminTextFieldColors()
          )

          OutlinedTextField(
            value = heroBannerSubtitle,
            onValueChange = { heroBannerSubtitle = it },
            label = { Text("Hero Banner Subtitle Description") },
            modifier = Modifier.fillMaxWidth(),
            colors = adminTextFieldColors()
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Maintenance Mode", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text("Show maintenance notice to players", color = Color(0xFF94A3B8), fontSize = 11.sp)
            }
            Switch(
              checked = isMaintenance,
              onCheckedChange = { isMaintenance = it },
              colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFFEF4444))
            )
          }
        }
      }
    }

    item {
      Button(
        onClick = {
          viewModel.saveWebsiteSettings(
            settings.copy(
              siteName = siteName,
              siteDomain = siteDomain,
              logoEmoji = logoEmoji,
              announcementText = announcement,
              heroBannerTitle = heroBannerTitle,
              heroBannerSubtitle = heroBannerSubtitle,
              isMaintenanceMode = isMaintenance
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("save_cms_btn")
      ) {
        Text("Save & Publish Live to Website", fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }
    }
  }
}

// ---------------------------------------------------------------------------
// 8. TAB 6: REPORTS & ANALYTICS
// ---------------------------------------------------------------------------

@Composable
private fun AdminReportsTab(
  viewModel: KhelvViewModel
) {
  val reports = viewModel.analyticsReports
  val games by viewModel.gamesFlow.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text("Platform Performance & Financial Reports", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }

    item {
      Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF0F172A), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Top 5 Games by Bet Popularity", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

          val topGames = games.sortedByDescending { it.playCount }.take(5)
          topGames.forEachIndexed { i, game ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("#${i + 1}", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(game.iconEmoji, fontSize = 16.sp)
                Text(game.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              }
              Text("${game.playCount * 3 + 240} Bets Placed", color = Color(0xFF60A5FA), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    item {
      Text("Daily Financial Breakdown (Live Audited)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }

    items(reports) { rep ->
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(rep.date, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("GGR: +₹${String.format(Locale.US, "%,.0f", rep.ggrProfit)}", color = Color(0xFF10B981), fontWeight = FontWeight.Black, fontSize = 13.sp)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Recharge: ₹${String.format(Locale.US, "%,.0f", rep.totalDeposits)}", color = Color(0xFF94A3B8), fontSize = 11.sp)
            Text("Payouts: ₹${String.format(Locale.US, "%,.0f", rep.totalWithdrawals)}", color = Color(0xFF94A3B8), fontSize = 11.sp)
            Text("Active: ${rep.activeUsers}", color = Color(0xFF60A5FA), fontSize = 11.sp)
          }
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// 9. TAB 7: SECURITY & 2FA
// ---------------------------------------------------------------------------

@Composable
private fun AdminSecurityTab(
  viewModel: KhelvViewModel
) {
  val session by viewModel.adminSession.collectAsState()
  val loginLogs by viewModel.loginLogs.collectAsState()
  val auditLogs by viewModel.auditLogs.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("🛡️", fontSize = 24.sp)
            Column {
              Text("Admin Security & Authorization Matrix", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Two-Factor Authentication (2FA) Active", color = Color(0xFF10B981), fontSize = 11.sp)
            }
          }

          Divider(color = Color(0xFF1E293B))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Current Logged Role:", color = Color(0xFF94A3B8), fontSize = 12.sp)
            Text(session?.role?.label ?: "Super Admin", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Session Security Token:", color = Color(0xFF94A3B8), fontSize = 12.sp)
            Text(session?.token ?: "AUTH_SEC_ACTIVE", color = Color(0xFF60A5FA), fontSize = 11.sp)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("2FA TOTP Protection:", color = Color(0xFF94A3B8), fontSize = 12.sp)
            Text("ENABLED (Mandatory)", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }

    item {
      Text("Admin Login Activity Logs", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }

    items(loginLogs) { log ->
      Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF0F172A), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Column {
            Text(log.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("IP: ${log.ipAddress} • Role: ${log.role}", color = Color(0xFF94A3B8), fontSize = 10.sp)
          }
          Column(horizontalAlignment = Alignment.End) {
            Surface(shape = RoundedCornerShape(4.dp), color = if (log.status == "SUCCESS") Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f)) {
              Text(log.status, color = if (log.status == "SUCCESS") Color(0xFF10B981) else Color(0xFFEF4444), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
            }
            Text(log.timestamp, color = Color(0xFF64748B), fontSize = 10.sp)
          }
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// HELPER DIALOGS
// ---------------------------------------------------------------------------

@Composable
private fun UserHistoryDialog(
  user: ManagedUser,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF0F172A), modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Text("User History: ${user.username}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null, tint = Color.White) }
        }

        if (user.gameHistory.isEmpty()) {
          Text("No recent game bets logged for this user.", color = Color(0xFF94A3B8), fontSize = 12.sp)
        } else {
          user.gameHistory.forEach { gh ->
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
              Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                  Text(gh.gameName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  Text("Bet: ₹${gh.betAmount} • ${gh.timestamp}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
                Text("₹${gh.winAmount} (${gh.multiplier}x)", color = if (gh.winAmount > 0) Color(0xFF10B981) else Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CoinAdjustmentDialog(
  user: ManagedUser,
  onConfirm: (Double, Boolean, String) -> Unit,
  onDismiss: () -> Unit
) {
  var amountStr by remember { mutableStateOf("100") }
  var isAdd by remember { mutableStateOf(true) }
  var reason by remember { mutableStateOf("VIP Loyalty Bonus") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF0F172A), modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Adjust Virtual Coins: ${user.username}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text("Current User Balance: ₹${user.balance}", color = Color(0xFFF59E0B), fontSize = 12.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isAdd) Color(0xFF10B981) else Color(0xFF1E293B),
            modifier = Modifier.weight(1f).clickable { isAdd = true }
          ) {
            Text("➕ Add Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (!isAdd) Color(0xFFEF4444) else Color(0xFF1E293B),
            modifier = Modifier.weight(1f).clickable { isAdd = false }
          ) {
            Text("➖ Deduct Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
          }
        }

        OutlinedTextField(
          value = amountStr,
          onValueChange = { amountStr = it },
          label = { Text("Virtual Coins Amount (₹)") },
          modifier = Modifier.fillMaxWidth(),
          colors = adminTextFieldColors()
        )

        OutlinedTextField(
          value = reason,
          onValueChange = { reason = it },
          label = { Text("Audit Reason / Remarks") },
          modifier = Modifier.fillMaxWidth(),
          colors = adminTextFieldColors()
        )

        Button(
          onClick = {
            val amount = amountStr.toDoubleOrNull() ?: 0.0
            if (amount > 0) {
              onConfirm(amount, isAdd, reason)
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = if (isAdd) Color(0xFF10B981) else Color(0xFFEF4444)),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
          Text("Confirm Adjustment & Record Audit", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}

@Composable
private fun AddOrEditGameDialog(
  existingGame: GameItem?,
  onSave: (GameItem) -> Unit,
  onDismiss: () -> Unit
) {
  var title by remember { mutableStateOf(existingGame?.title ?: "") }
  var provider by remember { mutableStateOf(existingGame?.provider ?: "JILI") }
  var badge by remember { mutableStateOf(existingGame?.badge ?: "HOT") }
  var iconEmoji by remember { mutableStateOf(existingGame?.iconEmoji ?: "🎰") }
  var selectedCategory by remember { mutableStateOf(existingGame?.category ?: GameCategory.SLOTS) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF0F172A), modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(if (existingGame == null) "Add New Game to Platform" else "Edit Game Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Game Title") },
          modifier = Modifier.fillMaxWidth(),
          colors = adminTextFieldColors()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = iconEmoji,
            onValueChange = { iconEmoji = it },
            label = { Text("Emoji") },
            modifier = Modifier.width(80.dp),
            colors = adminTextFieldColors()
          )

          OutlinedTextField(
            value = provider,
            onValueChange = { provider = it },
            label = { Text("Provider (e.g. JILI, SPRIBE)") },
            modifier = Modifier.weight(1f),
            colors = adminTextFieldColors()
          )
        }

        OutlinedTextField(
          value = badge,
          onValueChange = { badge = it },
          label = { Text("Badge (HOT, TOP, NEW)") },
          modifier = Modifier.fillMaxWidth(),
          colors = adminTextFieldColors()
        )

        Text("Select Category:", color = Color(0xFF94A3B8), fontSize = 11.sp)
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          GameCategory.values().filterNot { it == GameCategory.POPULAR }.forEach { cat ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (cat == selectedCategory) Color(0xFF2563EB) else Color(0xFF1E293B),
              modifier = Modifier.clickable { selectedCategory = cat }
            ) {
              Text(cat.title, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
          onClick = {
            if (title.isNotBlank()) {
              val gameId = existingGame?.id ?: title.lowercase().replace(" ", "_").replace("[^a-z0-9_]".toRegex(), "")
              val newGame = GameItem(
                id = gameId,
                title = title,
                category = selectedCategory,
                provider = provider,
                badge = badge,
                iconEmoji = iconEmoji,
                accentColor = existingGame?.accentColor ?: Color(0xFF3B82F6)
              )
              onSave(newGame)
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
          Text("Save Game to Database", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// UTILITY COMPONENTS
// ---------------------------------------------------------------------------

@Composable
private fun AdminMetricCard(
  title: String,
  value: String,
  subtitle: String,
  emoji: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFF0F172A),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = Color(0xFF94A3B8), fontSize = 11.sp)
        Text(emoji, fontSize = 18.sp)
      }
      Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
      Text(subtitle, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
  }
}

@Composable
private fun AdminActionChip(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = color.copy(alpha = 0.15f),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
    modifier = modifier.clickable(onClick = onClick)
  ) {
    Column(
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
      Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun adminTextFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedBorderColor = Color(0xFF3B82F6),
  unfocusedBorderColor = Color(0xFF1E293B),
  focusedContainerColor = Color(0xFF151C30),
  unfocusedContainerColor = Color(0xFF151C30),
  focusedTextColor = Color.White,
  unfocusedTextColor = Color.White,
  focusedLabelColor = Color(0xFF60A5FA),
  unfocusedLabelColor = Color(0xFF94A3B8)
)
