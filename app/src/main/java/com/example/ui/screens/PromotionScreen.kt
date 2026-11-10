package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.SupervisorAccount
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
import com.example.ui.theme.*
import com.example.viewmodel.KhelvViewModel
import java.util.Locale

@Composable
fun PromotionScreen(
  viewModel: KhelvViewModel,
  modifier: Modifier = Modifier
) {
  val user by viewModel.user.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Commission Hero Banner
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
                listOf(Color(0xFFDC2626), Color(0xFF1E40AF))
              )
            )
            .padding(20.dp)
        ) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "₹${String.format(Locale.US, "%.2f", user.todayCommission)}",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 32.sp
            )

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color.Black.copy(alpha = 0.35f)
            ) {
              Text(
                text = "Yesterday's total commission",
                color = KhelvGoldLight,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Upgrade agent level to increase commission percentage!",
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 11.sp
            )
          }
        }
      }
    }

    // 2. Invitation Link Action Button
    item {
      Button(
        onClick = {
          viewModel.showToast("Invitation Link copied: https://khelv.com/register?r=${user.inviteCode}")
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("invite_link_btn"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
        shape = RoundedCornerShape(16.dp)
      ) {
        Text(
          text = "INVITATION LINK",
          fontWeight = FontWeight.Black,
          fontSize = 16.sp,
          color = Color.White
        )
      }
    }

    // 3. Copy Invitation Code Card
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = KhelvNavySurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text("📥", fontSize = 20.sp)
            Text(
              text = "Copy invitation code",
              color = Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable {
              viewModel.showToast("Copied code: ${user.inviteCode}")
            }
          ) {
            Text(
              text = user.inviteCode,
              color = KhelvGold,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy",
              tint = KhelvBlueLight,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }

    // 4. Agency Menu Links
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = KhelvNavySurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          AgencyMenuRow(
            icon = Icons.Default.SupervisorAccount,
            title = "Subordinate data",
            testTag = "subordinate_data_row",
            onClick = { viewModel.showToast("Subordinates active: 5 members in team.") }
          )
          Divider(color = KhelvNavyBorder, thickness = 0.5.dp)
          AgencyMenuRow(
            icon = Icons.Default.ReceiptLong,
            title = "Commission detail",
            testTag = "commission_detail_row",
            onClick = { viewModel.showToast("Total commissions earned: ₹${user.totalCommission}") }
          )
          Divider(color = KhelvNavyBorder, thickness = 0.5.dp)
          AgencyMenuRow(
            icon = Icons.Default.Rule,
            title = "Invitation rules",
            testTag = "invitation_rules_row",
            onClick = { viewModel.showToast("Earn up to ₹308,888 invite bonuses on user recharges!") }
          )
          Divider(color = KhelvNavyBorder, thickness = 0.5.dp)
          AgencyMenuRow(
            icon = Icons.Default.HeadsetMic,
            title = "Agent line customer service",
            testTag = "agent_support_row",
            onClick = { viewModel.showCustomerService.value = true }
          )
          Divider(color = KhelvNavyBorder, thickness = 0.5.dp)
          AgencyMenuRow(
            icon = Icons.Default.Percent,
            title = "Rebate ratio",
            testTag = "rebate_ratio_row",
            onClick = { viewModel.showToast("Current agency tier rebate: 0.35%") }
          )
        }
      }
    }

    // 5. Promotion Data Summary Card
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = KhelvNavySurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "PROMOTION DATA",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("₹42.50", color = KhelvGold, fontWeight = FontWeight.Black, fontSize = 18.sp)
              Text("This Week", color = KhelvTextSecondary, fontSize = 11.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
              Text("₹${user.totalCommission.toInt()}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
              Text("Total commission", color = KhelvTextSecondary, fontSize = 11.sp)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("${user.directSubordinates}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
              Text("Direct Subordinate", color = KhelvTextSecondary, fontSize = 11.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
              Text("${user.totalTeamSubordinates}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
              Text("Team Subordinates", color = KhelvTextSecondary, fontSize = 11.sp)
            }
          }
        }
      }
    }

    // 6. Tiered Invitation Bonuses (Screenshot 1 & 2)
    item {
      Text(
        text = "INVITATION BONUS MILESTONES",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
    }

    item {
      InvitationMilestoneCard(
        bonusNum = 1,
        bonusAmount = 38.0,
        reqInvitees = 1,
        reqRecharge = 300.0,
        currentInvitees = 1,
        currentDeposits = 0,
        onClaim = { viewModel.showToast("Requirement pending: Invitee must recharge min ₹300.") }
      )
    }

    item {
      InvitationMilestoneCard(
        bonusNum = 2,
        bonusAmount = 158.0,
        reqInvitees = 3,
        reqRecharge = 300.0,
        currentInvitees = 2,
        currentDeposits = 0,
        onClaim = { viewModel.showToast("Requirement pending: 3 invitees needed.") }
      )
    }

    item {
      InvitationMilestoneCard(
        bonusNum = 3,
        bonusAmount = 580.0,
        reqInvitees = 10,
        reqRecharge = 500.0,
        currentInvitees = 2,
        currentDeposits = 0,
        onClaim = { viewModel.showToast("Requirement pending: 10 invitees needed.") }
      )
    }

    item {
      InvitationMilestoneCard(
        bonusNum = 4,
        bonusAmount = 1800.0,
        reqInvitees = 30,
        reqRecharge = 800.0,
        currentInvitees = 2,
        currentDeposits = 0,
        onClaim = { viewModel.showToast("Requirement pending: 30 invitees needed.") }
      )
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

@Composable
private fun AgencyMenuRow(
  icon: ImageVector,
  title: String,
  testTag: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .testTag(testTag),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Icon(icon, contentDescription = title, tint = KhelvBlueLight, modifier = Modifier.size(20.dp))
      Text(title, color = Color.White, fontSize = 14.sp)
    }
    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = KhelvTextSecondary, modifier = Modifier.size(18.dp))
  }
}

@Composable
private fun InvitationMilestoneCard(
  bonusNum: Int,
  bonusAmount: Double,
  reqInvitees: Int,
  reqRecharge: Double,
  currentInvitees: Int,
  currentDeposits: Int,
  onClaim: () -> Unit
) {
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
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF10B981)
        ) {
          Text(
            text = "Bonus $bonusNum",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }

        Text(
          text = "₹${String.format(Locale.US, "%,.2f", bonusAmount)}",
          color = KhelvGold,
          fontWeight = FontWeight.Black,
          fontSize = 16.sp
        )
      }

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E2238),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Number of invitees", color = KhelvTextSecondary, fontSize = 12.sp)
          Text("$reqInvitees", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E2238),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Recharge per people", color = KhelvTextSecondary, fontSize = 12.sp)
          Text("₹${reqRecharge.toInt()}.00", color = KhelvGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        Text("$currentInvitees / $reqInvitees invitees", color = KhelvTextSecondary, fontSize = 12.sp)
        Text("$currentDeposits / $reqInvitees deposits", color = KhelvTextSecondary, fontSize = 12.sp)
      }

      Button(
        onClick = onClaim,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Unfinished", color = KhelvTextSecondary, fontWeight = FontWeight.Bold)
      }
    }
  }
}
