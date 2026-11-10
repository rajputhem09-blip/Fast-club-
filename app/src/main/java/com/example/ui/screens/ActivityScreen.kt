package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.viewmodel.ActiveGameScreen
import com.example.viewmodel.KhelvViewModel
import java.util.Locale

@Composable
fun ActivityScreen(
  viewModel: KhelvViewModel,
  modifier: Modifier = Modifier
) {
  val user by viewModel.user.collectAsState()
  val attendanceList by viewModel.attendance.collectAsState()
  val dailyMissions by viewModel.dailyMissions.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Activity Award Banner
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFFFF5722), Color(0xFFFF9800), Color(0xFFFFB300))
              )
            )
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Activity Award",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Complete weekly/daily tasks to receive rich rewards. Daily rewards reset at midnight.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp
              )
            }

            Box(
              modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f)),
              contentAlignment = Alignment.Center
            ) {
              Text("🎁", fontSize = 30.sp)
            }
          }
        }
      }
    }

    // 2. Quick Activity Category Grid
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Attendance
        ActivityActionCard(
          title = "Attendance",
          subtitle = "Consecutive login gifts",
          emoji = "📅",
          gradient = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
          modifier = Modifier.weight(1f),
          testTag = "act_attendance",
          onClick = { viewModel.checkIn() }
        )

        // Wheel Spin
        ActivityActionCard(
          title = "Wheel Spin",
          subtitle = "iPhone & Cash prizes",
          emoji = "🎡",
          gradient = listOf(Color(0xFFDC2626), Color(0xFFB91C1C)),
          modifier = Modifier.weight(1f),
          testTag = "act_wheel",
          onClick = { viewModel.openGame(ActiveGameScreen.WHEEL_SPIN) }
        )
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Rebate
        ActivityActionCard(
          title = "Betting Rebate",
          subtitle = "Up to 0.35% return",
          emoji = "🪙",
          gradient = listOf(Color(0xFFD97706), Color(0xFFB45309)),
          modifier = Modifier.weight(1f),
          testTag = "act_rebate",
          onClick = { viewModel.claimRebate() }
        )

        // Gift Code
        ActivityActionCard(
          title = "Redeem Gifts",
          subtitle = "Enter voucher code",
          emoji = "🎟️",
          gradient = listOf(Color(0xFF7C3AED), Color(0xFF6D28D9)),
          modifier = Modifier.weight(1f),
          testTag = "act_gifts",
          onClick = { viewModel.showGiftRedeemDialog.value = true }
        )
      }
    }

    // 3. Attendance Bonus 7-Day Streak Section
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = KhelvNavySurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "ATTENDANCE BONUS",
                color = KhelvGold,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "Streak: ${user.dailyStreak} Days Consecutive",
                color = KhelvTextSecondary,
                fontSize = 12.sp
              )
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF1E2238)
            ) {
              Text(
                text = "Accumulated: ₹123.00",
                color = KhelvTextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 7 Days Tiles
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            attendanceList.take(6).forEach { dayItem ->
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = when {
                  dayItem.isClaimed -> Color(0xFF166534)
                  dayItem.isToday -> Color(0xFF2563EB)
                  else -> KhelvNavyCard
                },
                modifier = Modifier
                  .weight(1f)
                  .clickable(enabled = dayItem.isToday && !dayItem.isClaimed) {
                    viewModel.checkIn()
                  }
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "Day ${dayItem.day}",
                    color = KhelvTextSecondary,
                    fontSize = 10.sp
                  )
                  Text(
                    text = "₹${dayItem.amount.toInt()}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )
                  Text(
                    text = if (dayItem.isClaimed) "✓" else if (dayItem.isToday) "Claim" else "🔒",
                    color = if (dayItem.isToday) KhelvGold else KhelvTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Day 7 Grand Reward Box
          val day7 = attendanceList.getOrNull(6)
          if (day7 != null) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (day7.isClaimed) Color(0xFF166534) else Color(0xFFB45309),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Text("🎁", fontSize = 26.sp)
                  Column {
                    Text("Day 7 Grand Box", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Guaranteed Cash Reward", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                  }
                }

                Text(
                  text = "₹7,000.00",
                  color = KhelvGoldLight,
                  fontWeight = FontWeight.Black,
                  fontSize = 16.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Check In Button
          Button(
            onClick = { viewModel.checkIn() },
            enabled = !user.hasCheckedInToday,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("attendance_btn"),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (!user.hasCheckedInToday) KhelvBluePrimary else Color(0xFF374151)
            ),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              text = if (!user.hasCheckedInToday) "Attendance Check-In Today" else "Already Checked In Today",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = Color.White
            )
          }
        }
      }
    }

    // 4. Daily Missions Section
    item {
      Text(
        text = "DAILY MISSIONS",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
      )
    }

    items(dailyMissions) { mission ->
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = KhelvNavySurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = mission.title,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFF1E2238)
            ) {
              Text(
                text = "Unfinished",
                color = KhelvTextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          // Progress Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Deposit: ${mission.currentDeposit.toInt()}/${mission.depositReq.toInt()}",
              color = KhelvTextSecondary,
              fontSize = 12.sp
            )
            Text(
              text = "Bet: ${mission.currentBet.toInt()}/${mission.betReq.toInt()}",
              color = KhelvTextSecondary,
              fontSize = 12.sp
            )
          }

          LinearProgressIndicator(
            progress = { (mission.currentBet / mission.betReq).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = KhelvBluePrimary,
            trackColor = Color(0xFF1E2238)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Award amount: ₹${mission.awardAmount.toInt()}.00",
              color = KhelvGold,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )

            Button(
              onClick = {
                viewModel.showToast("Mission registered! Complete bets in Aviator or Mines to unlock award.")
              },
              colors = ButtonDefaults.buttonColors(containerColor = KhelvBluePrimary),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.height(36.dp)
            ) {
              Text("To Complete", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

@Composable
private fun ActivityActionCard(
  title: String,
  subtitle: String,
  emoji: String,
  gradient: List<Color>,
  modifier: Modifier = Modifier,
  testTag: String,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = KhelvNavySurface,
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(Brush.linearGradient(gradient))
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text(subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
        }
        Text(emoji, fontSize = 24.sp)
      }
    }
  }
}
