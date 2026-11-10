package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NavTab
import com.example.ui.theme.*

@Composable
fun KhelvBottomBar(
  currentTab: NavTab,
  onTabSelected: (NavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .background(KhelvNavyHeader)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .padding(horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      // 1. Home
      BottomNavItem(
        title = "Home",
        selected = currentTab == NavTab.HOME,
        activeIcon = Icons.Filled.Home,
        inactiveIcon = Icons.Outlined.Home,
        activeColor = KhelvGold,
        testTag = "nav_home",
        onClick = { onTabSelected(NavTab.HOME) }
      )

      // 2. Activity
      BottomNavItem(
        title = "Activity",
        selected = currentTab == NavTab.ACTIVITY,
        activeIcon = Icons.Filled.DateRange,
        inactiveIcon = Icons.Outlined.DateRange,
        activeColor = KhelvOrange,
        testTag = "nav_activity",
        onClick = { onTabSelected(NavTab.ACTIVITY) }
      )

      // 3. Center Promotion Button (Elevated Diamond)
      Box(
        modifier = Modifier
          .offset(y = (-10).dp)
          .testTag("nav_promotion")
          .clickable { onTabSelected(NavTab.PROMOTION) },
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(52.dp)
              .shadow(12.dp, CircleShape, spotColor = KhelvBlueCyan)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  colors = listOf(
                    Color(0xFF00E5FF),
                    Color(0xFF2563EB),
                    Color(0xFF1E1B4B)
                  )
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "💎",
              fontSize = 24.sp
            )
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = "Promotion",
            fontSize = 11.sp,
            fontWeight = if (currentTab == NavTab.PROMOTION) FontWeight.Bold else FontWeight.Medium,
            color = if (currentTab == NavTab.PROMOTION) KhelvBlueCyan else KhelvTextSecondary
          )
        }
      }

      // 4. Wallet
      BottomNavItem(
        title = "Wallet",
        selected = currentTab == NavTab.WALLET,
        activeIcon = Icons.Filled.AccountBalanceWallet,
        inactiveIcon = Icons.Outlined.AccountBalanceWallet,
        activeColor = KhelvGreen,
        testTag = "nav_wallet",
        onClick = { onTabSelected(NavTab.WALLET) }
      )

      // 5. Account
      BottomNavItem(
        title = "Account",
        selected = currentTab == NavTab.ACCOUNT,
        activeIcon = Icons.Filled.Person,
        inactiveIcon = Icons.Outlined.Person,
        activeColor = KhelvGold,
        testTag = "nav_account",
        onClick = { onTabSelected(NavTab.ACCOUNT) }
      )
    }
  }
}

@Composable
private fun BottomNavItem(
  title: String,
  selected: Boolean,
  activeIcon: ImageVector,
  inactiveIcon: ImageVector,
  activeColor: Color,
  testTag: String,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .testTag(testTag)
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = if (selected) activeIcon else inactiveIcon,
      contentDescription = title,
      tint = if (selected) activeColor else KhelvTextSecondary,
      modifier = Modifier.size(24.dp)
    )

    Spacer(modifier = Modifier.height(3.dp))

    Text(
      text = title,
      fontSize = 11.sp,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
      color = if (selected) activeColor else KhelvTextSecondary
    )
  }
}
