package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.util.Locale

import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Security

@Composable
fun KhelvTopBar(
  balance: Double,
  onDepositClick: () -> Unit,
  onSupportClick: () -> Unit,
  onNotificationClick: () -> Unit = {},
  onAdminClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Surface(
    color = KhelvNavyDark,
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Brand Logo
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.testTag("brand_logo")
      ) {
        Text(
          text = "♠️",
          fontSize = 18.sp,
          modifier = Modifier.padding(end = 4.dp)
        )
        Text(
          text = "khelv",
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          color = KhelvGoldLight
        )
        Text(
          text = ".com",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = KhelvBlueLight
        )
      }

      // Balance Pill & Add Button
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Balance Badge
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(KhelvNavyCard)
            .clickable(onClick = onDepositClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("balance_chip"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(20.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(GradientGold)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "₹",
              color = Color.Black,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          Text(
            text = String.format(Locale.US, "%.2f", balance),
            color = KhelvTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )

          Box(
            modifier = Modifier
              .size(18.dp)
              .clip(CircleShape)
              .background(KhelvGold),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Recharge",
              tint = Color.Black,
              modifier = Modifier.size(14.dp)
            )
          }
        }

        // Action Icons
        IconButton(
          onClick = onNotificationClick,
          modifier = Modifier
            .size(36.dp)
            .testTag("topbar_notif_btn")
        ) {
          Icon(
            imageVector = Icons.Default.NotificationsNone,
            contentDescription = "Notifications",
            tint = KhelvTextSecondary,
            modifier = Modifier.size(20.dp)
          )
        }

        IconButton(
          onClick = onSupportClick,
          modifier = Modifier
            .size(36.dp)
            .testTag("support_btn")
        ) {
          Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = "Support",
            tint = KhelvTextSecondary,
            modifier = Modifier.size(20.dp)
          )
        }

        // Direct Admin Portal Quick Link Button
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF1E3A8A),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6)),
          modifier = Modifier
            .clickable(onClick = onAdminClick)
            .testTag("topbar_admin_btn")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = "Admin Portal",
              tint = Color(0xFF93C5FD),
              modifier = Modifier.size(15.dp)
            )
            Text(
              text = "Admin",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }
    }
  }
}
