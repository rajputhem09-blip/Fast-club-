package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VIPLevel
import com.example.ui.theme.*
import com.example.viewmodel.KhelvViewModel

@Composable
fun AccountScreen(
  viewModel: KhelvViewModel,
  onNavigateToTab: (com.example.model.NavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val user by viewModel.user.collectAsState()
  val vipLevels = viewModel.vipLevels
  var selectedVipLevel by remember { mutableIntStateOf(1) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. User Header with Avatar & VIP Badge
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = KhelvNavySurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(GradientGold)),
              contentAlignment = Alignment.Center
            ) {
              Text("👑", fontSize = 28.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = KhelvGoldDark
                ) {
                  Text(
                    text = "VIP ${user.vipLevel}",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Text(
                  text = user.username,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp
                )
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Phone: ${user.phone}",
                color = KhelvTextSecondary,
                fontSize = 12.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // EXP & Payout Info Badges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFF1E2238),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text("${user.exp} EXP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("My experience", color = KhelvTextSecondary, fontSize = 11.sp)
              }
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFF1E2238),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text("22 Days", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Payout time", color = KhelvTextSecondary, fontSize = 11.sp)
              }
            }
          }
        }
      }
    }

    // 2. VIP Level Cards Carousel
    item {
      Text("VIP CLUB PRIVILEGES", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Spacer(modifier = Modifier.height(6.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(vipLevels.drop(1)) { vip ->
          val isSelected = selectedVipLevel == vip.level
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) Color(0xFF2563EB) else KhelvNavySurface,
            modifier = Modifier
              .width(220.dp)
              .clickable { selectedVipLevel = vip.level }
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(
                  Brush.linearGradient(
                    if (isSelected) listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                    else listOf(Color(0xFF1E2238), Color(0xFF13172E))
                  )
                )
                .padding(14.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = vip.title,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                  )
                  Text("🔒 Requires ${vip.reqExp} EXP", color = KhelvGold, fontSize = 10.sp)
                }

                Text("Bet ₹1 = 1 EXP", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)

                LinearProgressIndicator(
                  progress = { (user.exp.toFloat() / vip.reqExp.toFloat()).coerceIn(0f, 1f) },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                  color = KhelvGold,
                  trackColor = Color.Black.copy(alpha = 0.3f)
                )

                Text("${user.exp} / ${vip.reqExp} EXP", color = KhelvTextSecondary, fontSize = 10.sp)
              }
            }
          }
        }
      }
    }

    // 3. VIP Benefits Details Table
    item {
      val currentVipInfo = vipLevels.find { it.level == selectedVipLevel } ?: vipLevels[1]
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = KhelvNavySurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "VIP ${currentVipInfo.level} Benefits",
            color = KhelvGold,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )

          VipBenefitRow(
            emoji = "🎁",
            title = "Level up rewards",
            value = "₹${currentVipInfo.levelReward.toInt()}"
          )
          VipBenefitRow(
            emoji = "⭐",
            title = "Monthly reward",
            value = "₹${currentVipInfo.monthlyReward.toInt()}"
          )
          VipBenefitRow(
            emoji = "🪙",
            title = "Rebate rate",
            value = "${currentVipInfo.rebateRate}%"
          )
        }
      }
    }

    // 4. Quick Account Actions
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = KhelvNavySurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          AccountMenuItem(
            icon = Icons.Default.Notifications,
            title = "Notification",
            subtitle = "Welcome announcements & rewards",
            onClick = { viewModel.showNotificationDialog.value = true }
          )
          Divider(color = KhelvNavyBorder, thickness = 0.5.dp)
          AccountMenuItem(
            icon = Icons.Default.MonetizationOn,
            title = "Betting Rebate",
            subtitle = "Real-time washing & VIP rates",
            onClick = { viewModel.showRebateDialog.value = true }
          )
          Divider(color = KhelvNavyBorder, thickness = 0.5.dp)
          AccountMenuItem(
            icon = Icons.Default.CardGiftcard,
            title = "Gifts & Promo Codes",
            subtitle = "Claim voucher rewards",
            onClick = { viewModel.showGiftRedeemDialog.value = true }
          )
          Divider(color = KhelvNavyBorder, thickness = 0.5.dp)
          AccountMenuItem(
            icon = Icons.Default.MenuBook,
            title = "Game Rules & Strategy Guide",
            subtitle = "WinGo 3X Rule, Aviator, Mines tricks",
            onClick = { viewModel.showGameRulesDialog.value = true }
          )
          Divider(color = KhelvNavyBorder, thickness = 0.5.dp)
          AccountMenuItem(
            icon = Icons.Default.AdminPanelSettings,
            title = "Admin Portal & Control Center",
            subtitle = "Restricted management • 2FA Protected",
            onClick = {
              if (viewModel.adminSession.value != null) {
                viewModel.isAdminPortalOpen.value = true
              } else {
                viewModel.showAdminLoginDialog.value = true
              }
            }
          )
          Divider(color = KhelvNavyBorder, thickness = 0.5.dp)
          AccountMenuItem(
            icon = Icons.Default.Translate,
            title = "Language",
            subtitle = "English (US)",
            onClick = { viewModel.showToast("Current Language: English") }
          )
        }
      }
    }

    // 5. Service Center
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = KhelvNavySurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("SERVICE CENTER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            ServiceCenterIcon("⚙️", "Settings") { viewModel.showToast("Settings: Sound On • Notifications Active") }
            ServiceCenterIcon("💬", "Feedback") { viewModel.showToast("Feedback submitted. Thank you!") }
            ServiceCenterIcon("🤖", "Support") { viewModel.showCustomerService.value = true }
            ServiceCenterIcon("📖", "Guide") { viewModel.showGameRulesDialog.value = true }
            ServiceCenterIcon("🛡️", "Admin") {
              if (viewModel.adminSession.value != null) {
                viewModel.isAdminPortalOpen.value = true
              } else {
                viewModel.showAdminLoginDialog.value = true
              }
            }
          }
        }
      }
    }

    // 6. Log Out / Switch Account Button
    item {
      Button(
        onClick = { viewModel.showAuthDialog.value = true },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2238)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("account_auth_btn")
      ) {
        Text("Switch Account / Log In", color = KhelvTextSecondary, fontWeight = FontWeight.Bold)
      }
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

@Composable
private fun VipBenefitRow(
  emoji: String,
  title: String,
  value: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(emoji, fontSize = 18.sp)
      Text(title, color = Color.White, fontSize = 13.sp)
    }
    Text(value, color = KhelvGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
  }
}

@Composable
private fun AccountMenuItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Icon(icon, contentDescription = title, tint = KhelvBlueLight, modifier = Modifier.size(20.dp))
      Column {
        Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Text(subtitle, color = KhelvTextMuted, fontSize = 11.sp)
      }
    }
    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = KhelvTextSecondary, modifier = Modifier.size(18.dp))
  }
}

@Composable
private fun ServiceCenterIcon(
  emoji: String,
  label: String,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable(onClick = onClick)
      .padding(6.dp)
  ) {
    Box(
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(KhelvNavyCard),
      contentAlignment = Alignment.Center
    ) {
      Text(emoji, fontSize = 20.sp)
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(label, color = KhelvTextSecondary, fontSize = 11.sp)
  }
}
